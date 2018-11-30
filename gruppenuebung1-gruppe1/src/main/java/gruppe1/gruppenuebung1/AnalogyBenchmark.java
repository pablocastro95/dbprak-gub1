package gruppe1.gruppenuebung1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class AnalogyBenchmark extends Benchmark {

	public AnalogyBenchmark() {
		super("AnalogyBenchmark");
	}

	@Override
	public boolean importData(String filePath) {
		boolean success = true;
		try (BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)))) {
			String line = reader.readLine();
			String[] words;
			while (line != null && !line.startsWith(":")) {
				words = line.split(" ");
				addTask(new AnalogyTask(words[0], words[1], words[2], words[3]), 10);
				line = reader.readLine();
			}
		} catch (IOException e) {
			success = false;
			System.out.println(e);
		}
		return success;
	}
}