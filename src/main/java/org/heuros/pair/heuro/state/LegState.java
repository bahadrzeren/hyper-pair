package org.heuros.pair.heuro.state;

public class LegState {
	public int numOfCoverings = 0;
	/*
	 * Initial values are taken from Leg.
	 */
	public int numOfIncludingDuties = 0;
	public int numOfIncludingDutiesWoDh = 0;
	public int numOfIncludingEffectiveDuties = 0;
	public int numOfIncludingEffectiveDutiesWoDh = 0;
	public boolean potentialDh = false;

	public void resetForNewIteration() {
		this.numOfCoverings = 0;
		this.numOfIncludingDuties = 0;
		this.numOfIncludingDutiesWoDh = 0;
		this.numOfIncludingEffectiveDuties = 0;
		this.numOfIncludingEffectiveDutiesWoDh = 0;
		this.potentialDh = false;
	}

	public double heuristicModifierValue = 0.0;

	private static double weightInclusionScore = 0.0;
	private static double weightInclusionScoreWoDh = 0.4;
	private static double weightEffectiveInclusionScore = 0.4;
	private static double weightEffectiveInclusionScoreWoDh = 0.0;
	private static double weightHeuristicModifier = 0.2;

	private double getInclusionScore(LegState legMaxState) {
		return (weightInclusionScore * this.numOfIncludingDuties) / legMaxState.numOfIncludingDuties;
	}

	private double getInclusionScoreWoDh(LegState legMaxState) {
		return (weightInclusionScoreWoDh * this.numOfIncludingDutiesWoDh) / legMaxState.numOfIncludingDutiesWoDh;
	}

	private double getEffectiveInclusionScore(LegState legMaxState) {
		return (weightEffectiveInclusionScore * this.numOfIncludingEffectiveDuties) / legMaxState.numOfIncludingEffectiveDuties;
	}

	private double getEffectiveInclusionScoreWoDh(LegState legMaxState) {
		return (weightEffectiveInclusionScoreWoDh * this.numOfIncludingEffectiveDutiesWoDh) / legMaxState.numOfIncludingEffectiveDutiesWoDh;
	}

	private double getHeuristicModifierScore(LegState legMaxState) {
		return (weightEffectiveInclusionScoreWoDh * this.numOfIncludingEffectiveDutiesWoDh) / legMaxState.numOfIncludingEffectiveDutiesWoDh;
	}

	private double getDifficultyScoreOfTheLeg(LegState legMaxState) {
		return this.getInclusionScore(legMaxState)
				+ this.getInclusionScoreWoDh(legMaxState)
				+ this.getEffectiveInclusionScore(legMaxState)
				+ this.getEffectiveInclusionScoreWoDh(legMaxState);
	}

	public boolean valuesAreOk(int numOfIncludingDuties,
			int numOfIncludingDutiesWoDh,
			int numOfIncludingEffectiveDuties,
			int numOfIncludingEffectiveDutiesWoDh) {
		return (this.numOfIncludingDuties == numOfIncludingDuties)
				&& (this.numOfIncludingDutiesWoDh == numOfIncludingDutiesWoDh)
				&& (this.numOfIncludingEffectiveDuties == numOfIncludingEffectiveDuties)
				&& (this.numOfIncludingEffectiveDutiesWoDh == numOfIncludingEffectiveDutiesWoDh);
	}
}
