package gruppe1.gruppenuebung1;

import java.sql.SQLException;

public class ContainsTask implements BenchmarkTask {
	
	private String word;
	private boolean expected;
	
	public ContainsTask(String word, boolean expected) {
		this.word = word;
		this.expected = expected;
		
	}

	@Override
	public TaskResult run(EmbeddingRepository repo) throws SQLException {
		QueryResult<Boolean> result = repo.containsWord(word);
		return new TaskResult(result.getRunTime(), result.getResult().equals(expected));
	}

}
