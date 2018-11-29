package gruppe1.gruppenuebung1;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A generic Benchmark-structure, which contains Tasks and runs them on a 
 * EmbeddingRepository to determine it's performance.
 * Different Benchmarks can be created by adding corresponding tasks.
 *
 */
public abstract class Benchmark {
	private List<BenchmarkTask> tasks;
	
	
	public Benchmark() {
		tasks = new ArrayList<BenchmarkTask>();
	}
	
	public void addTask(BenchmarkTask task, int amount) {
		for(int i=0; i< amount; i++) {
			tasks.add(task);
		}
	}
	
	public void reset() {
		tasks = new ArrayList<BenchmarkTask>();
	}
	
	/**
	 * Runs the stored tasks in random order.
	 * @param repo The repository which performance is tested.
	 * @return An object containing information about the performance of the tasks.
	 * @throws SQLException 
	 */
	public BenchmarkResult run(EmbeddingRepository repo) throws SQLException {
		BenchmarkResult result = new BenchmarkResult();
		Random r = new Random();

		for(int length = tasks.size(); length > 0; length--) {
			int index =  r.nextInt(length);
			BenchmarkTask randomTask = tasks.remove(index);
			long runTime = randomTask.run(repo);
			
			result.addObservation(runTime);
		}
		return result;
	}
	
	/**
	 * Import Data from a file
	 * @param filePath The path of the file containing the data.
	 * @return If the import was successful.
	 */
	public abstract boolean importData(String filePath);

}
