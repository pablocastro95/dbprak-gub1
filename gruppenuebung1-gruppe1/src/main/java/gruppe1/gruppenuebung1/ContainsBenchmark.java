package gruppe1.gruppenuebung1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class ContainsBenchmark extends Benchmark {
	
	public ContainsBenchmark() {
		super("ContainsBenchmark");
	}	
	
	@Override
	public boolean importData(String filePath) {
		boolean success = true;
		Charset charset = Charset.forName("UTF-8");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), charset))) {
			String line = reader.readLine();
			String[] words;
			while(line != null) {
				words = line.split(" ");
				addTask(new ContainsTask(words[0], true), 10);
				line = reader.readLine();
			}
		} catch (IOException e) {
			success = false;
			System.out.println(e);			
		}		

		return success;
	}

}
