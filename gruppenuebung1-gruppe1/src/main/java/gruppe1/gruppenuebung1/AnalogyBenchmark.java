package gruppe1.gruppenuebung1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class AnalogyBenchmark extends Benchmark {
	
	private int upperLimit;
	private int lowerLimit;//Limits the amount of tasks due to performance issues

	public AnalogyBenchmark() {
		super("AnalogyBenchmark");
		upperLimit = 19500;
		lowerLimit = 19000;
	}

	@Override
	public boolean importData(String filePath) {
		boolean success = true;
		Charset charset = Charset.forName("UTF-8");
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), charset))) {
			String line = reader.readLine();
			String[] words;
			int i = 0;
			while (line != null && i < upperLimit) {
				if(!line.startsWith(":") && i > lowerLimit) {
					words = line.split(" ");
					addTask(new AnalogyTask(words[0], words[1], words[2], words[3]), 1); //Reduced amount of tasks added per line to one 
				}
				line = reader.readLine();
				i++;
			}
		} catch (IOException e) {
			success = false;
			System.out.println(e);
		}
		return success;
	}
}