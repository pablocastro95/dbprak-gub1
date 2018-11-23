package gruppe1.gruppenuebung1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class EmbeddingRepository {
	private Connection con;
	private String url;
		
	
	public EmbeddingRepository(String host, String port) {
		url = "jdbc:postgresql://" + host + ":" + port +  "/";		
	}
	
	public void connect(String user, String password) throws SQLException {
		con =  DriverManager.getConnection(url, user, password);
	}
	
	private void initialize() {
		//create DB
		//
		
	}

}
