package org.heuros.hyperpair.heuristic;

public class QualityMetric {
	/*
	 * Deadhead
	 */
	public int bestNumOfDh = Integer.MAX_VALUE;
	public int bestDhDurationInMins = Integer.MAX_VALUE;
	/*
	 * Dutyday
	 */
	public int bestActiveBlocktimeInMins = 0;
	/*
	 * The last metric to check if others are equal!
	 */
	public double bestAvgNumOfIncludingDutiesOfTheSameLegs = Double.MAX_VALUE;	
}
