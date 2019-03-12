package org.heuros.pair.heuro.state;

import org.heuros.data.model.Leg;

public class LegState {

	public static int maxNumOfIncludingDuties = 0;
	public static int maxNumOfIncludingDutiesWoDh = 0;
	public static int maxNumOfIncludingEffectiveDuties = 0;
	public static int maxNumOfIncludingEffectiveDutiesWoDh = 0;
	public static double maxHeuristicModifierValue = 0.0;

	private Leg associatedLeg = null;

	public LegState(Leg associatedLeg) {
		this.associatedLeg = associatedLeg;
	}

	public int numOfCoverings = 0;
	/*
	 * Initial values are taken from Leg.
	 */
	public int numOfIncludingDuties = 0;
	public int numOfIncludingDutiesWoDh = 0;
	public int numOfIncludingEffectiveDuties = 0;
	public int numOfIncludingEffectiveDutiesWoDh = 0;

	private void resetForNewIteration() {
		this.numOfCoverings = 0;
		this.numOfIncludingDuties = 0;
		this.numOfIncludingDutiesWoDh = 0;
		this.numOfIncludingEffectiveDuties = 0;
		this.numOfIncludingEffectiveDutiesWoDh = 0;
	}

	public void initializeForNewIteration(Leg leg) {
		this.resetForNewIteration();
		this.numOfIncludingDuties = leg.getNumOfIncludingDuties();
		this.numOfIncludingDutiesWoDh = leg.getNumOfIncludingDutiesWoDh();
		this.numOfIncludingEffectiveDuties = leg.getNumOfIncludingEffectiveDuties();
		this.numOfIncludingEffectiveDutiesWoDh = leg.getNumOfIncludingEffectiveDutiesWoDh();
	}

	/*
	 * Cumulative values that do not reset during optimization.
	 */
	public int numOfIterations = 0;
	public double heuristicModifierValue = 0.0;

	/*
	 * Difficulty Score calculations.
	 */
	public static double weightInclusionScore = 0.0;
	public static double weightInclusionScoreWoDh = 0.5;
	public static double weightEffectiveInclusionScore = 0.0;
	public static double weightEffectiveInclusionScoreWoDh = 0.0;
	public static double weightHeuristicModifier = 0.5;

	private double getInclusionScore() {
		return (weightInclusionScore * (LegState.maxNumOfIncludingDuties - this.numOfIncludingDuties)) / LegState.maxNumOfIncludingDuties;
	}

	private double getInclusionScoreWoDh() {
		return (weightInclusionScoreWoDh * (LegState.maxNumOfIncludingDutiesWoDh - this.numOfIncludingDutiesWoDh)) / LegState.maxNumOfIncludingDutiesWoDh;
	}

	private double getEffectiveInclusionScore() {
		return (weightEffectiveInclusionScore * (LegState.maxNumOfIncludingEffectiveDuties - this.numOfIncludingEffectiveDuties)) / LegState.maxNumOfIncludingEffectiveDuties;
	}

	private double getEffectiveInclusionScoreWoDh() {
		return (weightEffectiveInclusionScoreWoDh * (LegState.maxNumOfIncludingEffectiveDutiesWoDh - this.numOfIncludingEffectiveDutiesWoDh)) / LegState.maxNumOfIncludingEffectiveDutiesWoDh;
	}

	private double getHeuristicModifierScore() {
		if (LegState.maxHeuristicModifierValue > 0.0) {
			return (weightHeuristicModifier * this.heuristicModifierValue) / LegState.maxHeuristicModifierValue;
		} else {
			return 0.0;
		}
	}

	public double getDifficultyScoreOfTheLeg() {
		if (this.associatedLeg.isCover()
				&& (this.numOfCoverings == 0)) {
			return this.getInclusionScore()
					+ this.getInclusionScoreWoDh()
					+ this.getEffectiveInclusionScore()
					+ this.getEffectiveInclusionScoreWoDh()
					+ this.getHeuristicModifierScore();
		} else {
			return 0.0;
		}
	}

	/*
	 * Validation test.
	 */
	public boolean valuesAreOk(int numOfIncludingDuties,
			int numOfIncludingDutiesWoDh,
			int numOfIncludingEffectiveDuties,
			int numOfIncludingEffectiveDutiesWoDh) {
		return (this.numOfIncludingDuties == numOfIncludingDuties)
				&& (this.numOfIncludingDutiesWoDh == numOfIncludingDutiesWoDh)
				&& (this.numOfIncludingEffectiveDuties == numOfIncludingEffectiveDuties)
				&& (this.numOfIncludingEffectiveDutiesWoDh == numOfIncludingEffectiveDutiesWoDh);
	}

	@Override
	public String toString() {
		return "numOfCoverings: " + numOfCoverings + 
				", numOfIncludingDuties: " + numOfIncludingDuties + 
				", numOfIncludingDutiesWoDh: " + numOfIncludingDutiesWoDh + 
				", numOfIncludingEffectiveDuties: " + numOfIncludingEffectiveDuties + 
				", numOfIncludingEffectiveDutiesWoDh: " + numOfIncludingEffectiveDutiesWoDh + 
				", numOfIterations: " + numOfIterations + 
				", heuristicModifierValue: " + heuristicModifierValue;
		
	}
}
