package gruppe1.gruppenuebung1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SimmilarityBenchmark extends Benchmark {
	private boolean normalized;
	

	public SimmilarityBenchmark(boolean normalized) {
		this.normalized = normalized;
	}
	
	@Override
	public boolean importData(String filePath) {
		boolean success = true;
		try(BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)))) {
			String line = reader.readLine();
			String[] words;
			while(line != null) {
				words = line.split(" ");
				addTask(new SimmilarityTask(words[0], words[1], normalized), 10);
				line = reader.readLine();
			}
		} catch (IOException e) {
			success = false;
		}
		return success;
	}

}