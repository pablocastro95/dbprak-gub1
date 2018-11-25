package gruppe1.gruppenuebung1;

public class SimmilarityTask implements BenchmarkTask{
	private String w1; 
	private String w2;
	double expectedResult;
	
	public SimmilarityTask(String w1, String w2, double expectedResult) {
		this.w1 = w1;
		this.w2 = w2;
		this. expectedResult = expectedResult;
	}

	@Override
	public boolean run(EmbeddingRepository repo) {
		double result = repo.getCosSimilarity(w1, w2, true);
		return result == expectedResult;
	}

}
