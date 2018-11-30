package gruppe1.gruppenuebung1;

public class QueryResult<E> {
	private E result;
	private long runTime;
	
	public QueryResult(E result, long runTime) {
		this.result = result;
		this.runTime = runTime;
	}
	
	public E getResult() {
		return this.result;
	}
	
	public long  getRunTime() {
		return this.runTime;
	}
}
