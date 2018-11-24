package gruppe1.gruppenuebung1;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
			stmt.close();
			serverCon.close();
			
			serverCon =  DriverManager.getConnection(url + "nlp/", user, password);
			stmt = serverCon.createStatement();
			String createTable = "CREATE TABLE EMBEDDINGS (WORD int not NULL, ";
			for(int i = 1; i <301; i++) {
				createTable.concat(" DIM" + i + " double,");
			}
			createTable.concat(" PRIMARY KEY (WORD)); ");
			stmt.executeUpdate(createTable);
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
		}
		return repo;
	}
	
	public boolean connect(String user, String password) {
		boolean success = true;
		
		return success;	
	}
	
	
	
	public void importData(Reader in) throws SQLException{
		String tableName = "EMBEDDINGS";
		
		if(con != null) {
			try {
				CopyManager copyManager = new CopyManager((BaseConnection) con);
		        copyManager.copyIn("COPY " + tableName + " FROM STDIN CSV HEADER DELIMITER ','", in);
			} catch(SQLException e) {
				e.printStackTrace();
			} catch(FileNotFoundException e) {
				e.printStackTrace();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	

	
	
	

}
