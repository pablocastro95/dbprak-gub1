package gruppe1.gruppenuebung1;

import java.sql.SQLException;

public class SimmilarityTask implements BenchmarkTask{
	private String w1; 
	private String w2;
	
	public SimmilarityTask(String w1, String w2, boolean normalized) {
		this.w1 = w1;
		this.w2 = w2;
	}

	@Override
	public long run(EmbeddingRepository repo) throws SQLException {
		return repo.getCosSimilarity(w1, w2).getRunTime();
	}

}
