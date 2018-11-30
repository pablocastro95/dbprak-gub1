package gruppe1.gruppenuebung1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ContainsBenchmark extends Benchmark {
	
	public ContainsBenchmark() {
		super("ContainsBenchmark");
	}	
	
	@Override
	public boolean importData(String filePath) {
		boolean success = true;
		
		try(BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)))) {
			String line = reader.readLine();
			String[] words;
			while(line != null) {
				words = line.split(" ");
				addTask(new ContainsTask(words[0]), 10);
				line = reader.readLine();
			}
		} catch (IOException e) {
			success = false;
			System.out.println(e);			
		}		

		return success;
	}

}
