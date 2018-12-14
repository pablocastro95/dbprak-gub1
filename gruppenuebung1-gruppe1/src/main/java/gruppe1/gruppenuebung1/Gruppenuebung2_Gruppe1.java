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

/*** 
 * 
 *
 * @version v1.0.0
 *
 */


public class Gruppenuebung2_Gruppe1 {

	public static void main(String[] args) {
		System.out.print("Setup: connecting to database..");
		EmbeddingRepository repo = EmbeddingRepository.createRepository("localhost", "5432", "postgres", "seLect14");
		
		
		if(repo != null) {
			System.out.println("SUCCESS");
			System.out.println();
			
			String srcFile = chooseImportData();
			
			
			try(Reader in = new BufferedReader(new FileReader(new File(srcFile)));) {
				// Import data to local database
				System.out.println();
				System.out.print("Importing embeddings into database... ");
				boolean importSuccess =  repo.importData(in);
				
				if(importSuccess) {
					System.out.println("SUCCESS");
					System.out.println();
					List<BenchmarkResult> results = new ArrayList<BenchmarkResult>();
					
					
					// Execute Similarity Benchmark (Exercise 4.1)
					BenchmarkResult result = null;
					result = runBenchmark("BENCHMARK 4.1: Simmilarity", new SimmilarityBenchmark(), "src/main/resources/MEN_dataset_natural_form_full", repo);
					if(result != null) {
						results.add(result);
					}
					
					result = runBenchmark("BENCHMARK 4.2: Analogy (20 mins, 500 tests)", new AnalogyBenchmark(), "src/main/resources/questions-words.txt", repo);
					if(result != null) {
						results.add(result);
					}
					
					result = runBenchmark("BENCHMARK 4.3: Contains", new ContainsBenchmark(),  "src/main/resources/vocabs_shuffled.txt", repo);
					if(result != null) {
						results.add(result);
					}
					
					BenchmarkResultPrinter.printPerformance(results);
					System.out.println("SUCCESS: You can view the results");

				} else {
					System.out.println("FAIL");
					System.out.println("An error oucurred reading the data. Place the CSV files in the src/main/resources folder");
				}
				
			} catch (IOException | SQLException e) {
				// TODO Auto-generated catch block
				System.out.println("Error: " + e.getMessage());
			} finally {
				repo.disconnect();
			}
			
		} else {
			System.out.println("FAIL");
			System.out.println("Error: Could not connect to database. ");
		}

	}
	
	private static String chooseImportData() {
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
		    if(consoleInput.equals("Y")) {
				srcFile = "src/main/resources/out-normalized.csv";
			} else {
				srcFile = "src/main/resources/out-unnormalized.csv";
			}
		}
		finally {
		    if(scanner!=null)
		        scanner.close();
		}
		return srcFile;
	}
	
	private static BenchmarkResult runBenchmark(String bmName, Benchmark bm, String filePath, EmbeddingRepository repo) {
		BenchmarkResult result = null;
		System.out.println(bmName);								
		System.out.print("Importing Tasks..");
		boolean importSuccess = bm.importData(filePath);
		if (importSuccess) {
			System.out.println("SUCCESS");
			System.out.print("Running Tasks...");
			result= bm.run(repo);
			if (result != null) {
				System.out.println("SUCCESS");
			} else {
				System.out.println("FAIL");
			}
		} else {
			System.out.println("FAIL");
			System.out.println("Benchmark will be skipped due to error importing the tasks.");
			
		} 
		System.out.println();
		return result;
		
	}

}