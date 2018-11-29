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
			
			//TODO CREATE function for cos similarity
			
			//TODO Create function for 
			String todo = "CREATE OR REPLACE FUNCTION new_order(word1 VARCHAR, word2 VARCHAR, OUT sim double precision)"
					+ "AS '"
					+ "DECLARE "
						+ "count_words integer; "
					+ "BEGIN "
						+ "SELECT COUNT(WORD) INTO count_word FROM embeddings WHERE word = word1 OR word = word2; "
						+ "SELECT customer.credit INTO balance FROM customer WHERE name = session_user; "
						+ "possible := balance >= total_price; "
						+ "SELECT MAX(id) + 1 INTO new_id FROM orders; "
						+ "IF possible THEN "
							+ "INSERT INTO orders VALUES (new_id, session_user, current_date, newArticle, quantity); "
							+ "UPDATE customer SET credit = balance - total_price WHERE name = session_user; "
						+ "END IF; "
					+ "END;'"
					+ "LANGUAGE PLPGSQL ";
			
			
			
			stmt.close();
			repo = new EmbeddingRepository(serverCon);
			
			
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
	
	public QueryResult<String> getAnalogousWord(String a1, String a2, String b1, boolean normalized) {
		//TODO Michael
		String result = null;
		long runTime = 0;
		
		return new QueryResult<String>(result, runTime);
	}

	
	public QueryResult<List<String>> getKNearestNeighbors(int k, String word, boolean normalized) {
		//TODO Eric
		List<String> results = new ArrayList<String>();
		long runTime = 0;
		
		return new QueryResult<List<String>>(results, runTime);
	}
	
	public QueryResult<Double> getCosSimilarity(String w1, String w2, boolean normalized) throws SQLException{
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
