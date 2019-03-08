package org.heuros.pair.heuro.state;

import org.heuros.data.model.Leg;

public class LegState {
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

	public void initialize(Leg leg) {
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
	private static double weightInclusionScore = 0.0;
	private static double weightInclusionScoreWoDh = 0.4;
	private static double weightEffectiveInclusionScore = 0.0;
	private static double weightEffectiveInclusionScoreWoDh = 0.4;
	private static double weightHeuristicModifier = 0.2;

	private double getInclusionScore(LegState legMaxState) {
		return (weightInclusionScore * (legMaxState.numOfIncludingDuties - this.numOfIncludingDuties)) / legMaxState.numOfIncludingDuties;
	}

	private double getInclusionScoreWoDh(LegState legMaxState) {
		return (weightInclusionScoreWoDh * (legMaxState.numOfIncludingDutiesWoDh - this.numOfIncludingDutiesWoDh)) / legMaxState.numOfIncludingDutiesWoDh;
	}

	private double getEffectiveInclusionScore(LegState legMaxState) {
		return (weightEffectiveInclusionScore * (legMaxState.numOfIncludingEffectiveDuties - this.numOfIncludingEffectiveDuties)) / legMaxState.numOfIncludingEffectiveDuties;
	}

	private double getEffectiveInclusionScoreWoDh(LegState legMaxState) {
		return (weightEffectiveInclusionScoreWoDh * (legMaxState.numOfIncludingEffectiveDutiesWoDh - this.numOfIncludingEffectiveDutiesWoDh)) / legMaxState.numOfIncludingEffectiveDutiesWoDh;
	}

	private double getHeuristicModifierScore(LegState legMaxState) {
		if (legMaxState.heuristicModifierValue > 0.0) {
			return (weightHeuristicModifier * this.heuristicModifierValue) / legMaxState.heuristicModifierValue;
		} else {
			return 0.0;
		}
	}

	public double getDifficultyScoreOfTheLeg(LegState legMaxState) {
		return this.getInclusionScore(legMaxState)
				+ this.getInclusionScoreWoDh(legMaxState)
				+ this.getEffectiveInclusionScore(legMaxState)
				+ this.getEffectiveInclusionScoreWoDh(legMaxState)
				+ this.getHeuristicModifierScore(legMaxState);
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
}
