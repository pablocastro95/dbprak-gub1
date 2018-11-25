package gruppe1.gruppenuebung1;

public class BenchmarkResult {
	private boolean success;
	private long minTime;
	private double avgTime;
	private long maxTime;
	private int countObservations;
	private double squareSum;
	
	public BenchmarkResult() {
		success = false;
		countObservations = 0;
		squareSum = 0;
	}
	
	public void addObservation(long runTime) {
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
		
	}
	
	public double getVariance() {
		double variance = 0;
		if (countObservations != 0) {
			variance = squareSum / countObservations - Math.pow(avgTime, 2);
		}
		return variance;
	}
	
	


}
