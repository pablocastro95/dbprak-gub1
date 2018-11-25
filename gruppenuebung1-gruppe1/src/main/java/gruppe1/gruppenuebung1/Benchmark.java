package gruppe1.gruppenuebung1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Benchmark {
	List<BenchmarkTask> tasks;
	
	
	public Benchmark() {
		tasks = new ArrayList<BenchmarkTask>();
	}
	
	public void addTask(BenchmarkTask task) {
		for(int i=0; i< 10; i++) {
			tasks.add(task);
		}
	}
	
	public void reset() {
		tasks = new ArrayList<BenchmarkTask>();
	}
	
	public BenchmarkResult run(EmbeddingRepository repo) {
		BenchmarkResult result = new BenchmarkResult();
		Random r = new Random();
		long startTime = 0;
		long endTime = 0;
		
		for(int length = tasks.size(); length > 0; length--) {
			int index =  r.nextInt(length - 1);
			BenchmarkTask randomTask = tasks.remove(index);
			startTime = System.currentTimeMillis();
			randomTask.run(repo);
			endTime = System.currentTimeMillis();
			
			result.addObservation(endTime - startTime);
		}
		return result;
	}

}
