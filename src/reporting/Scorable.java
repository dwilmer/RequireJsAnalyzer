package reporting;

public abstract class Scorable {
	/**
	 * @return a number between 0 and 1, showing how well it performs
	 */
	public abstract double getScore();
	
	public int compareTo(Scorable other) {
		return Double.compare(this.getScore(), other.getScore());
	}
}
