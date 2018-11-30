package gruppe1.gruppenuebung1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Gruppenuebung1_Gruppe1 {

	public static void main(String[] args) {
		EmbeddingRepository repo = EmbeddingRepository.createRepository("localhost", "5432", "postgres", "seLect14");
		System.out.print("Do you want to use normalized dataset? Please type: 'Y' for yes or 'N' for no:\n");
		Scanner scanner = null;
		String consoleInput = "";
		String srcFile;
		
		try {
		    scanner = new Scanner(System.in);
		    consoleInput = scanner.nextLine();				    
		    if(!(consoleInput.equals("Y") || consoleInput.equals("N"))) {
				System.out.print("Invalid input, please restart the program and try again.\n");	
			    System.exit(0);
		    }
		}
		finally {
		    if(scanner!=null)
		        scanner.close();
		}
		if(consoleInput.equals("Y")) {
			srcFile = "src/main/resources/out-normalized.csv";
		} else {
			srcFile = "src/main/resources/out-unnormalized.csv";
		}
		
		if(repo != null) {
			try(Reader in = new BufferedReader(new FileReader(new File(srcFile)));) {
				// Import data to local database
				repo.importData(in);
				
				List<BenchmarkResult> results = new ArrayList<BenchmarkResult>();

				
				
				// Execute Similarity Benchmark (Exercise 4.1)
				System.out.println("Executing benchmark 4.1...");								
				Benchmark bm = new SimmilarityBenchmark();
				System.out.println("File MEN_dataset_natural_form_full loaded with status: " + bm.importData("src/main/resources/MEN_dataset_natural_form_full"));
				BenchmarkResult result= bm.run(repo);
				results.add(result);
				//result.printResults();
				
				// Execute Contains Benchmark (Exercise 4.3)
				System.out.println("Executing benchmark 4.3...");				
				bm = new ContainsBenchmark();
				System.out.println("File vocabs_shuffled loaded with status: " + bm.importData("src/main/resources/vocabs_shuffled.txt"));
				result= bm.run(repo);
				results.add(result);
				//result.printResults();
				
				BenchmarkResultPrinter.printPerformance(results);
				System.out.println("SUCCESS: You can view the results");

			} catch (IOException | SQLException e) {
				// TODO Auto-generated catch block
				System.out.println("Error: " + e.getMessage());
			} finally {
				repo.disconnect();
			}
			
		} else {
			System.out.println("Error: Could not connect to database. ");
		}
		
		
		
		
	}
	
}