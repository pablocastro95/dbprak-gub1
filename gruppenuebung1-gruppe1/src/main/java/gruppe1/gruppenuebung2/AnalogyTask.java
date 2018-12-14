package gruppe1.gruppenuebung2;

import java.sql.SQLException;

public class AnalogyTask implements BenchmarkTask {
	private String a1;
	private String a2;
	private String b1;
	private String expectedB2;
	
	public AnalogyTask(String a1, String a2, String b1, String expectedB2) {
		this.a1 = a1;
		this.a2 = a2;
		this.b1 = b1;
		this.expectedB2 = expectedB2;
	}

	@Override
	public TaskResult run(EmbeddingRepository repo) throws SQLException {
		QueryResult<String> result = repo.getAnalogousWord(a1, a2, b1);
		boolean success = result.getResult().equals(expectedB2);
		return new TaskResult(result.getRunTime(), success);
	}

}
