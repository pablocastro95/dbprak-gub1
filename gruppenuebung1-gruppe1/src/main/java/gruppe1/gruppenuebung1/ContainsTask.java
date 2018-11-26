package gruppe1.gruppenuebung1;

import java.sql.SQLException;

public class ContainsTask implements BenchmarkTask {
	
	private String word;
	
	public ContainsTask(String word) {
		this.word = word;
	}

	@Override
	public long run(EmbeddingRepository repo) throws SQLException {
		return repo.containsWord(word).getRunTime();
	}

}
