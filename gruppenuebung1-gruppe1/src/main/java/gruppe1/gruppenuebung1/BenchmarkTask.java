package gruppe1.gruppenuebung1;

import java.sql.SQLException;

public interface BenchmarkTask {
	
	public long run(EmbeddingRepository repo) throws SQLException;

}
