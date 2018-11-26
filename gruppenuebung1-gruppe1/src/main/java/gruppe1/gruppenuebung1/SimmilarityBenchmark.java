package gruppe1.gruppenuebung1;

public class SimmilarityBenchmark extends Benchmark {
	private boolean normalized;
	

	public SimmilarityBenchmark(boolean normalized) {
		this.normalized = normalized;
	}
	
	@Override
	public void importData(String filePath) {
		// TODO Pablo

		//for every line in doc
		//	addTask(new SimmilarityTask(w1, w2, normalized))

	}

}
