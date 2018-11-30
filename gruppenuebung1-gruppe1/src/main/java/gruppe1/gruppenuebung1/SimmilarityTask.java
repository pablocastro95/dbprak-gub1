package gruppe1.gruppenuebung1;

import java.sql.SQLException;

public class SimmilarityTask implements BenchmarkTask{
	private String w1; 
	private String w2;
	private double expectedSimmilarity;
	
	public SimmilarityTask(String w1, String w2, double expectedSimmilarity) {
		this.w1 = w1;
		this.w2 = w2;
		this.expectedSimmilarity = expectedSimmilarity;
	}

	@Override
	public TaskResult run(EmbeddingRepository repo) throws SQLException {
		QueryResult<Double> result = repo.getCosSimilarity(w1, w2);
		double difference = result.getResult() - expectedSimmilarity;
		double threshold = 0.2;
		boolean success = (difference < threshold) && (difference > -threshold);
		
		return new TaskResult(result.getRunTime(), success);
	}

}
