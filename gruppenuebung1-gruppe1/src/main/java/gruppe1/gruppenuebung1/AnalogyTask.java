package gruppe1.gruppenuebung1;

import java.sql.SQLException;

public class AnalogyTask implements BenchmarkTask {
	private String a1;
	private String a2;
	private String b1;
	
	public AnalogyTask(String a1, String a2, String b1) {
		this.a1 = a1;
		this.a2 = a2;
		this.b1 = b1;
	}

	@Override
	public long run(EmbeddingRepository repo) throws SQLException {
		return repo.getAnalogousWord(a1, a2, b1).getRunTime();
	}

}
