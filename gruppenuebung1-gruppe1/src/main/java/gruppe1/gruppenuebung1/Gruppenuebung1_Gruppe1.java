package gruppe1.gruppenuebung1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;

public class Gruppenuebung1_Gruppe1 {
	
	public static void main(String[] args) {
		EmbeddingRepository repo = EmbeddingRepository.createRepository("localhost", "5432", "postgres", "seLect14");
		
		
		if(repo != null) {
			try(Reader in = new BufferedReader(new FileReader(new File("hallo")));) {
				repo.importData(in);
				
			} catch (IOException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		} else {
			System.out.println("Error: Could not connect to database. ");
		}
		
		
		
		
	}
	
}