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
			try(Reader in = new BufferedReader(new FileReader(new File("src/main/resources/out.csv")));) {
				// Import data to local database
				repo.importData(in);
				
				// Execute Similarity Benchmark (Exercise 4.1)
				System.out.println("Executing benchmark 4.1...");								
				Benchmark bm = new SimmilarityBenchmark(false);
				System.out.println("File MEN_dataset_natural_form_full loaded with status: " + bm.importData("src/main/resources/MEN_dataset_natural_form_full"));
				BenchmarkResult result= bm.run(repo);
				result.printResults();
				
				// Execute Contains Benchmark (Exercise 4.3)
				System.out.println("Executing benchmark 4.3...");				
				bm = new ContainsBenchmark();
				System.out.println("File vocabs_shuffled loaded with status: " + bm.importData("src/main/resources/vocabs_shuffled.txt"));
				result= bm.run(repo);
				result.printResults();				
				

			} catch (IOException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		} else {
			System.out.println("Error: Could not connect to database. ");
		}
		
		
		
		
	}
	
}