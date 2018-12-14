package gruppe1.gruppenuebung2;

public class TaskResult {
	
	private long runTime;
	private boolean success;
	
	public TaskResult(long runTime, boolean success) {
		this.runTime = runTime;
		this.success = success;
	}
	
	public boolean isSuccess() {
		return success;
	}
	
	
	public long getRunTime() {
		return runTime;
	}
	
	

	
}
