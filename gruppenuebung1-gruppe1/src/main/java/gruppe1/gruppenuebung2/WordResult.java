package gruppe1.gruppenuebung2;

public class WordResult {

	private String word;
	private double similarity;

	public WordResult(String word, double sim) {
		this.word = word;
		this.similarity = sim;
	}

	public String getWord() {
		return word;
	}

	public double getSimiliarty() {
		return similarity;
	}

}
