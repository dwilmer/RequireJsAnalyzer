package reporting;

import java.util.Collection;


public abstract class Scorable implements Comparable<Scorable> {
	public static double[] getScores(double[] percentiles, Collection<? extends Scorable> scores) {
		int numScores = scores.size();
		double[] scoreList = new double[numScores];
		int i = 0;
		for(Scorable sc : scores) {
			scoreList[i] = sc.getScore();
			i++;
		}
		
		double[] percentileScores = new double[percentiles.length];
		for(i = 0; i < percentiles.length; i++) {
			int index = (int) Math.round(((double)numScores)*percentiles[i]);
			percentileScores[i] = scoreList[index];
		}
		return percentileScores;
	}
	
	/**
	 * @return a number between 0 and 1, showing how well it performs
	 */
	public abstract double getScore();
	
	public int compareTo(Scorable other) {
		return Double.compare(this.getScore(), other.getScore());
	}
}
