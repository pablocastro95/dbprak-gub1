package gruppe1.gruppenuebung2;

public class BenchmarkResult {
	private String benchmarkName;
	private long minTime;
	private double avgTime;
	private long maxTime;
	private int countObservations;
	private double squareSum;
	private int countSuccessful;
	
	public BenchmarkResult(String benchmarkName) {
		countObservations = 0;
		countSuccessful = 0;
		squareSum = 0;
		this.benchmarkName = benchmarkName;
	}
	
	public void addObservation(long runTime, boolean success) {
		countObservations++;
		squareSum = squareSum + Math.pow(runTime, 2);
		if(countObservations == 1) {
			minTime = runTime;
			maxTime = runTime;
			avgTime = runTime;
		} else {
			if (runTime < minTime) {
				minTime = runTime;
			} else if (runTime > maxTime) {
				maxTime = runTime;
			}
			avgTime = avgTime + (runTime - avgTime) / countObservations;
		}
		
		if(success) {
			countSuccessful++;
		}
		
	}
	
	public long getMin() {
		return minTime;
	}
	
	public long getMax() {
		return maxTime;
	}
	
	public double getAvg() {
		return avgTime;
	}
	
	public double getVariance() {
		double variance = 0;
		if (countObservations != 0) {
			variance = squareSum / countObservations - Math.pow(avgTime, 2);
		}
		return variance;
	}
	
	public String getBenchmarkName() {
		return benchmarkName;
	}
	
	public void printResults() {
		System.out.println("Min: " + this.getMin());
		System.out.println("Max: " + this.getMax());
		System.out.println("Avg: " + this.getAvg());
		System.out.println("Variance: " + this.getVariance() + "\n");		
	}
	
	public double getSuccessRate() {
		double rate =((double) countSuccessful) / countObservations;
		return rate;
	}


}
