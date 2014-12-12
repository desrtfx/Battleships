import java.io.Serializable;


public class HighScore implements Comparable<HighScore>, Serializable {

	/**
	 * Serialization ID
	 */
	private static final long serialVersionUID = -2934099689576386066L;

	private String name;
	private int score;
	
	public HighScore() {
		this.name = "";
		this.score = 0;
	}
	
	public HighScore(String commanderName, int score) {
		this.name = commanderName;
		this.score = score;
	}
	
	// Fills Highscore element with data as in .toString format
	public HighScore(String data) {
		String[] fields = data.split("\t");
		if (fields.length == 2) {
			this.name = fields[0];
			this.score = Integer.parseInt(fields[1]);
		} else {
			this.name = "";
			this.score = 0;
		}
	}
	
	public String getName() {
		return name;
	}
	
	public int getScore() {
		return score;
	}
	
	public boolean equals(HighScore other) {
		return this.score == other.score;
	}

	@Override
	public String toString() {
		return name + "\t" + score;
	}

	@Override
	public int compareTo(HighScore other) {
		return other.score - this.score;
	}

}
