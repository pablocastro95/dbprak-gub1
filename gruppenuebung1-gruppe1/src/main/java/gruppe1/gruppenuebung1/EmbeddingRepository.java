package gruppe1.gruppenuebung1;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

public class EmbeddingRepository {
	private Connection con;
	private PreparedStatement simStatement;

	private EmbeddingRepository(Connection con) {
		this.con = con;

	}

	/**
	 * Creates and initializes a Repository for Embeddings Database and Table are
	 * created.
	 * 
	 * @param host
	 * @param port
	 * @param user
	 * @param password
	 * @return The Repository. Null if Connection to DB fails.
	 */
	public static EmbeddingRepository createRepository(String host, String port, String user, String password) {
		String url = "jdbc:postgresql://" + host + ":" + port + "/";
		Connection serverCon = null;
		EmbeddingRepository repo = null;

		try {
			serverCon = DriverManager.getConnection(url, user, password);
			Statement stmt = serverCon.createStatement();
			stmt.executeUpdate("DROP DATABASE IF EXISTS nlp");
			stmt.executeUpdate("CREATE DATABASE nlp");
			stmt.close();
			serverCon.close();

			serverCon = DriverManager.getConnection(url + "nlp", user, password);

			// Create Table for Data
			stmt = serverCon.createStatement();
			String createTable = "CREATE TABLE EMBEDDINGS (WORD VARCHAR not NULL, ";
			for (int i = 1; i < 301; i++) {
				createTable = createTable.concat(" DIM" + i + " double precision,");
			}
			createTable = createTable.concat(" LENGTH double precision, ");
			createTable = createTable.concat(" PRIMARY KEY (WORD)); ");
			stmt.executeUpdate(createTable);

			stmt.close();
			repo = new EmbeddingRepository(serverCon);
			repo.createFunctionsForNearestNeighbors();

		} catch (SQLException e) {
			repo = null;
			if (serverCon != null) {
				try {
					serverCon.close();
				} catch (SQLException f) {
					f.printStackTrace();
				}

			}
		}
		return repo;
	}


	private void createFunctionsForNearestNeighbors() {
		String returnStatement = "";
		String analogySelect = "";
		String lengthAttribute = "SQRT(";

		for (int i = 1; i < 301; i++) {
			returnStatement += "w1.DIM" + i + "* w2.DIM" + i + "+";
			analogySelect += "(a2.DIM" + i + " - a1.DIM" + i + " + b1.DIM" + i + ") AS DIM" + i + ",";
			lengthAttribute += "pow(b2.DIM" + i + ", 2.0) + ";
		}
		returnStatement = returnStatement.substring(0, returnStatement.length() - 1);
		
		analogySelect = analogySelect.substring(0, analogySelect.length() - 1);
		
		lengthAttribute = lengthAttribute.substring(0, lengthAttribute.length() - 2);
		lengthAttribute += ")";
		
		String function1 = "CREATE OR REPLACE FUNCTION sim(w1 embeddings,w2 embeddings)\n" + 
				"RETURNS double precision AS\n" + 
				"$$\n DECLARE result double precision;" + 
				"\n" + 
				"BEGIN\n" + 
				"\n" + 
				"result = " + returnStatement + ";" +
				"\n return result / (w1.length * w2.length);" + 
				"\nEND;" + 
				"\n$$\n" + 
				"  LANGUAGE plpgsql IMMUTABLE;";

		String function2 = "CREATE OR REPLACE FUNCTION getKNearestNeighbors(IN wort character varying,IN k integer)\n" + 
				"  RETURNS TABLE(word character varying, sim double precision) AS\n" + 
				"$$\n DECLARE " + 
				"entry embeddings;\n" + 
				"\n" + 
				"BEGIN\n" + 
				"SELECT * FROM embeddings INTO entry where embeddings.word = wort limit 1;\n" + 
				"\n" + 
				"RETURN QUERY  SELECT embeddings.word, sim(embeddings.*,entry) as sim FROM embeddings where embeddings.word != wort AND length != 0 AND entry.length != 0\n" + 
				"order by sim desc limit k;\n" + 
				"\n" + 
				"END;\n" + 
				"$$\n" + 
				"  LANGUAGE plpgsql;";
		String function3 = "CREATE OR REPLACE FUNCTION getAnalogousWord(a1w varchar, a2w varchar, b1w varchar) \r\n" + 
				"RETURNS TABLE(word character varying, sim double precision) AS\r\n" + 
				"$$ DECLARE\r\n" + 
				"	contains integer;\r\n" + 
				"	distinctWords integer;\r\n" + 
				"	b2 embeddings;\r\n" + 
				"BEGIN\r\n" + 
				"	SELECT count(DISTINCT input.word) INTO distinctWords FROM (Values (a1w), (a2w), (b1w)) input (word);\r\n" + 
				"	SELECT count(embeddings.word) INTO contains FROM embeddings WHERE embeddings.word=a1w OR embeddings.word=a2w OR embeddings.word=b1w;\r\n" + 
				"	IF contains <  distinctWords THEN\r\n" + 
				"		RAISE 'missing % word(s)', distinctWords - contains; \r\n" + 
				"	ELSE\r\n" + 
				"		SELECT '', " + analogySelect + " INTO b2\r\n" + 
				"		FROM embeddings a1, embeddings a2, embeddings b1\r\n" + 
				"		WHERE a1.word = a1w AND a2.word = a2w AND b1.word =b1w;\r\n" +
				"       SELECT "+ lengthAttribute + " INTO b2.length;" +
				"	END IF;\r\n" + 
				"																			   \r\n" + 
				"																			   \r\n" + 
				"	RETURN QUERY  SELECT embeddings.word, sim(embeddings.*,b2) as sim FROM embeddings where length != 0 order by sim desc limit 1;\r\n" + 
				"END;$$\r\n" + 
				"LANGUAGE PLPGSQL;";

		try (Statement statement = con.createStatement()){
			statement.execute(function1);
			statement.execute(function2);
			statement.execute(function3);
			simStatement = con.prepareStatement("SELECT " + returnStatement + " FROM embeddings w1, embeddings w2 WHERE w1.word=? AND w2.word=?");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void importData(Reader in) throws SQLException {
		String tableName = "EMBEDDINGS";

		System.out.println("Started copying data to the database...");
		if (con != null) {
			try {
				CopyManager copyManager = new CopyManager((BaseConnection) con);
				copyManager.copyIn("COPY " + tableName + " FROM STDIN CSV HEADER DELIMITER ';'", in);
				System.out.println("Data successfully imported to the databsase!\n");
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public QueryResult<Boolean> containsWord(String word) throws SQLException {
		PreparedStatement stmt = con.prepareStatement("SELECT WORD FROM EMBEDDINGS WHERE word=?");
		stmt.setString(1, word);
		long startTime = System.currentTimeMillis();
		ResultSet rs = stmt.executeQuery();
		long runTime = System.currentTimeMillis() - startTime;
		boolean contains = rs.next();
		rs.close();
		stmt.close();
		return new QueryResult<Boolean>(new Boolean(contains), runTime);
	}


	public QueryResult<String> getAnalogousWord(String a1, String a2, String b1) throws SQLException {
		String result = null;
				String sql = "SELECT * FROM getAnalogousWord(?, ?, ?)";
				PreparedStatement preparedStatement2 = con.prepareStatement(sql);
				preparedStatement2.setString(1, a1);
				preparedStatement2.setString(2, a2);
				preparedStatement2.setString(3, b1);
				long start = System.currentTimeMillis();
			try {
				
				ResultSet rs = preparedStatement2.executeQuery();
				if(rs.next()) {
					result = rs.getString(1);
				}
				rs.close();
			} catch (SQLException e) {
				throw e;
			} finally {
				
				preparedStatement2.close();
			}
			
			long runtime = System.currentTimeMillis() - start;
			
		return new QueryResult<String>(result, runtime);
	}

	/**
	 * This method returns the k nearest neighbors of a given word using the cos
	 * similarity.
	 * 
	 * @param k
	 * @param word
	 * @return
	 * @throws SQLException
	 */
	public QueryResult<List<WordResult>> getKNearestNeighbors(int k, String word) throws SQLException {

		PreparedStatement stmt = con.prepareStatement("SELECT * FROM getknearestneighbors(?,?);");
		stmt.setString(1, word);
		stmt.setInt(2, k);

		long startTime = System.currentTimeMillis();
		ResultSet result = stmt.executeQuery();
		long runTime = System.currentTimeMillis() - startTime;

		List<WordResult> results = new ArrayList<WordResult>();

		while (result.next()) {
			results.add(new WordResult(result.getString("word"), result.getDouble("sim")));
		}
		result.close();
		stmt.close();		
		
		return new QueryResult<List<WordResult>>(results, runTime);
	}

	public QueryResult<Double> getCosSimilarity(String w1, String w2) throws SQLException {
		double simmilarity = -1;

		
		simStatement.setString(1, w1);
		simStatement.setString(2, w2);

		long startTime = System.currentTimeMillis();
		ResultSet rs = simStatement.executeQuery();
		long runTime = System.currentTimeMillis() - startTime;

		if (rs.next()) {
			simmilarity = rs.getDouble(1);
		}
		rs.close();
		
		return new QueryResult<Double>(new Double(simmilarity), runTime);
	}
	
	
	public void disconnect() {
		if (con != null) {
			try {
				simStatement.close();
				con.close();
			} catch (SQLException e) {

			}
		}
	}
}
