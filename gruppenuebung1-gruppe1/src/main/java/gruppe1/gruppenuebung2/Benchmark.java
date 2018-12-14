package gruppe1.gruppenuebung2;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A generic Benchmark-structure, which contains Tasks and runs them on a 
 * EmbeddingRepository to determine it's performance.
 * Different Benchmarks can be created by adding corresponding tasks.
 *
 */
public abstract class Benchmark {
	private List<BenchmarkTask> tasks;
	private String name;
	

	public Benchmark(String name) {
		tasks = new ArrayList<BenchmarkTask>();
		this.name = name;
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
	public BenchmarkResult run(EmbeddingRepository repo) {
		BenchmarkResult result = new BenchmarkResult(name);
		Collections.shuffle(tasks);
		for(BenchmarkTask randomTask: tasks) {
			try {
				TaskResult tr = randomTask.run(repo);
				long runTime = tr.getRunTime();
				boolean success = tr.isSuccess();
				
				result.addObservation(runTime, success);
			} catch (SQLException e) {
			}
		}
		return result;
	}
	
	public String getName() {
		return name;
	}
	
	/**
	 * Import Data from a file
	 * @param filePath The path of the file containing the data.
	 * @return If the import was successful.
	 */
	public abstract boolean importData(String filePath);

}
