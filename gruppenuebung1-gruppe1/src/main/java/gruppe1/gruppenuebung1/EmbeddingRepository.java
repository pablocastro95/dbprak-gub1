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
		
	
	private EmbeddingRepository(Connection con) {
			this.con = con;
			
	}
	
	/**
	 * Creates and initializes a Repository for Embeddings
	 * Database and Table are created.
	 * 
	 * @param host
	 * @param port
	 * @param user
	 * @param password
	 * @return The Repository. Null if Connection to DB fails.
	 */
	public static EmbeddingRepository createRepository(String host, String port, String user, String password) {
		String url = "jdbc:postgresql://" + host + ":" + port +  "/";
		Connection serverCon = null;
		EmbeddingRepository repo = null;
		
		try {
			serverCon = DriverManager.getConnection(url, user, password);
			Statement stmt = serverCon.createStatement();
			stmt.executeUpdate("DROP DATABASE IF EXISTS nlp");
			stmt.executeUpdate("CREATE DATABASE nlp");
			System.out.println("Local database created!");		        			
			stmt.close();
			serverCon.close();
			
			serverCon =  DriverManager.getConnection(url + "nlp", user, password);
			
			//Create Table for Data
			stmt = serverCon.createStatement();
			String createTable = "CREATE TABLE EMBEDDINGS (WORD VARCHAR not NULL, ";
			for(int i = 1; i <301; i++) {
				createTable = createTable.concat(" DIM" + i + " double precision,");
			}
			createTable = createTable.concat(" LENGTH double precision, ");
			createTable = createTable.concat(" PRIMARY KEY (WORD)); ");
			stmt.executeUpdate(createTable);
			System.out.println("Table EMBEDDINGS created!\n");		        						
						
		
			stmt.close();
			repo = new EmbeddingRepository(serverCon);
			repo.createFunctionsForNearestNeighbors();
			System.out.println("Function for receiving nearest neighbors created!");
			
		} catch(SQLException e) {
			if (serverCon != null) {
				try {
					serverCon.close();
				} catch (SQLException f) {
					f.printStackTrace();
				}
				
			}
			e.printStackTrace();
		}
		return repo;
	}
	
	private void createFunctionsForNearestNeighbors() {
		String returnStatement = "";
		
		for(int i = 1; i< 301; i++) {
			returnStatement += "w1.DIM" + i + "* w2.DIM" + i + "+";
		}
		returnStatement = returnStatement.substring(0, returnStatement.length()-1);
		returnStatement += ";";
		String function1 = "CREATE OR REPLACE FUNCTION sim(w1 embeddings,w2 embeddings)\n" + 
				"RETURNS double precision AS\n" + 
				"$$\n DECLARE result double precision;" + 
				"\n" + 
				"BEGIN\n" + 
				"\n" + 
				"result = " + returnStatement + 
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
				"RETURN QUERY  SELECT embeddings.word, sim(embeddings.*,entry) as sim FROM embeddings where embeddings.word != wort AND length != 0\n" + 
				"order by sim desc limit k;\n" + 
				"\n" + 
				"END;\n" + 
				"$$\n" + 
				"  LANGUAGE plpgsql;";
		
		try (Statement statement = con.createStatement()){
			statement.execute(function1);
			statement.execute(function2);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public void importData(Reader in) throws SQLException{
		String tableName = "EMBEDDINGS";
		
		System.out.println("Started copying data to the database...");										
		if(con != null) {
			try {
				CopyManager copyManager = new CopyManager((BaseConnection) con);
		        copyManager.copyIn("COPY " + tableName + " FROM STDIN CSV HEADER DELIMITER ';'", in);
				System.out.println("Data successfully imported to the databsase!\n");		        
			} catch(SQLException e) {
				e.printStackTrace();
			} catch(FileNotFoundException e) {
				e.printStackTrace();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public QueryResult<Boolean> containsWord(String word) throws SQLException{
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
	
	public double getVectorNorm(String word) throws SQLException {
		PreparedStatement stmt = con.prepareStatement("SELECT * FROM EMBEDDINGS WHERE word=?");
		stmt.setString(1, word);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		
		double squaredSum = 0;
		for(int i = 1; i< 301; i++) {
			squaredSum = squaredSum + Math.pow(rs.getDouble("DIM" + i), 2); 
		}
		rs.close();
		stmt.close();
		return Math.sqrt(squaredSum);
	}
	
	public QueryResult<String> getAnalogousWord(String a1, String a2, String b1) {
		//TODO Michael
		String result = null;
		long runTime = 0;
		
		return new QueryResult<String>(result, runTime);
	}

	/**
	 * This method returns the k nearest neighbors of a given word using the cos similarity.
	 * @param k
	 * @param word 
	 * @return
	 * @throws SQLException
	 */
	public QueryResult<List<WordResult>> getKNearestNeighbors(int k, String word) throws SQLException {
		
		PreparedStatement stmt= con.prepareStatement("SELECT * FROM getknearestneighbors(?,?);");
		stmt.setString(1, word);
		stmt.setInt(2, k);
		
		long startTime = System.currentTimeMillis();
		ResultSet result = stmt.executeQuery();
		long runTime = System.currentTimeMillis() - startTime;
		
		List<WordResult> results = new ArrayList<WordResult>();
		
		while (result.next()) {
			results.add(new WordResult(result.getString("word"), result.getDouble("sim")));
		}
				
		return new QueryResult<List<WordResult>>(results, runTime);
	}
	
	public QueryResult<Double> getCosSimilarity(String w1, String w2) throws SQLException{
		double simmilarity = -1;
		
		String cosQuery = "SELECT ";
		for(int i = 1; i< 301; i++) {
			cosQuery += "w1.DIM" + i + "* w2.DIM" + i + "+";
		}
		cosQuery += "0 FROM embeddings w1, embeddings w2 WHERE w1.word=? AND w2.word=?"; 
		
		
		PreparedStatement stmt= con.prepareStatement(cosQuery);
		stmt.setString(1, w1);
		stmt.setString(2, w2);
		
		long startTime = System.currentTimeMillis();
		ResultSet rs = stmt.executeQuery();
		long runTime = System.currentTimeMillis() - startTime;
		
		if(rs.next()) {
			simmilarity = rs.getDouble(1);
		}
		rs.close();
		
		return new QueryResult<Double>(new Double(simmilarity), runTime);
	}
	
	private void createTable() {
		
	}
	
	public void disconnect() {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				
			}
		}
	}
}
