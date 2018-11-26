package gruppe1.gruppenuebung1;

public class AnalogyBenchmark extends Benchmark {
	
	private boolean normalized;
	
	public AnalogyBenchmark(boolean normalized) {
		this.normalized = normalized;
	}

	@Override
	public void importData(String filePath) {
		// TODO Michael
		
		//for every line in doc
		//	addTask(new AnalogyTask(w1, w2, w3, normalized))

	}

}
