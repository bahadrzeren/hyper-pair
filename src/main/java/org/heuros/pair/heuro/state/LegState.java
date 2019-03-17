package org.heuros.pair.heuro.state;

import org.heuros.data.model.Leg;

public class LegState implements Cloneable {

	public static int maxNumOfIncludingDuties = 0;
	public static int maxNumOfIncludingDutiesWoDh = 0;
	public static int maxNumOfIncludingEffectiveDuties = 0;
	public static int maxNumOfIncludingEffectiveDutiesWoDh = 0;

	public static int maxNumOfIncludingPairs = 0;
	public static int maxNumOfIncludingPairsWoDh = 0;
	public static int maxNumOfIncludingEffectivePairs = 0;
	public static int maxNumOfIncludingEffectivePairsWoDh = 0;

	public static double maxHeuristicModifierValue = 0.0;

	@Override
	public Object clone() throws CloneNotSupportedException {
		return (LegState) super.clone();
	}

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

	public int numOfIncludingPairs = 0;
	public int numOfIncludingPairsWoDh = 0;
	public int numOfIncludingEffectivePairs = 0;
	public int numOfIncludingEffectivePairsWoDh = 0;

	private void resetForNewIteration() {
		this.numOfCoverings = 0;
		this.numOfIncludingDuties = 0;
		this.numOfIncludingDutiesWoDh = 0;
		this.numOfIncludingEffectiveDuties = 0;
		this.numOfIncludingEffectiveDutiesWoDh = 0;
		this.numOfIncludingPairs = 0;
		this.numOfIncludingPairsWoDh = 0;
		this.numOfIncludingEffectivePairs = 0;
		this.numOfIncludingEffectivePairsWoDh = 0;
	}

	public void initializeForNewIteration(Leg leg) {
		this.resetForNewIteration();
		this.numOfIncludingDuties = leg.getNumOfIncludingDuties();
		this.numOfIncludingDutiesWoDh = leg.getNumOfIncludingDutiesWoDh();
		this.numOfIncludingEffectiveDuties = leg.getNumOfIncludingEffectiveDuties();
		this.numOfIncludingEffectiveDutiesWoDh = leg.getNumOfIncludingEffectiveDutiesWoDh();
		this.numOfIncludingPairs = leg.getNumOfIncludingPairs();
		this.numOfIncludingPairsWoDh = leg.getNumOfIncludingPairsWoDh();
		this.numOfIncludingEffectivePairs = leg.getNumOfIncludingEffectivePairs();
		this.numOfIncludingEffectivePairsWoDh = leg.getNumOfIncludingEffectivePairsWoDh();
	}

	/*
	 * Cumulative values that do not reset during optimization.
	 */
	public int numOfIterations = 0;
	public double heuristicModifierValue = 0.0;

	/*
	 * Difficulty Score calculations.
	 */
	public static double weightDutyInclusionScore = 0.0;
	public static double weightDutyInclusionScoreWoDh = 0.0;
	public static double weightDutyEffectiveInclusionScore = 0.0;
	public static double weightDutyEffectiveInclusionScoreWoDh = 0.0;

	public static double weightPairInclusionScore = 0.0;
	public static double weightPairInclusionScoreWoDh = 1.0;
	public static double weightPairEffectiveInclusionScore = 0.0;
	public static double weightPairEffectiveInclusionScoreWoDh = 0.0;

	public static double weightHeuristicModifier = 0.0;

	private double getDutyInclusionScore() {
		return (weightDutyInclusionScore * (1.0 - (1.0 * this.numOfIncludingDuties / LegState.maxNumOfIncludingDuties)));
	}

	private double getDutyInclusionScoreWoDh() {
		return (weightDutyInclusionScoreWoDh * (1.0 - (1.0 * this.numOfIncludingDutiesWoDh / LegState.maxNumOfIncludingDutiesWoDh)));
	}

	private double getDutyEffectiveInclusionScore() {
		return (weightDutyEffectiveInclusionScore * (1.0 - (1.0 * this.numOfIncludingEffectiveDuties / LegState.maxNumOfIncludingEffectiveDuties)));
	}

	private double getDutyEffectiveInclusionScoreWoDh() {
		return (weightDutyEffectiveInclusionScoreWoDh * (1.0 - (1.0 * this.numOfIncludingEffectiveDutiesWoDh / LegState.maxNumOfIncludingEffectiveDutiesWoDh)));
	}

	private double getPairInclusionScore() {
		return (weightPairInclusionScore * (1.0 - (1.0 * this.numOfIncludingPairs / LegState.maxNumOfIncludingPairs)));
	}

	private double getPairInclusionScoreWoDh() {
		return (weightPairInclusionScoreWoDh * (1.0 - (1.0 * this.numOfIncludingPairsWoDh / LegState.maxNumOfIncludingPairsWoDh)));
	}

	private double getPairEffectiveInclusionScore() {
		return (weightPairEffectiveInclusionScore * (1.0 - (1.0 * this.numOfIncludingEffectivePairs / LegState.maxNumOfIncludingEffectivePairs)));
	}

	private double getPairEffectiveInclusionScoreWoDh() {
		return (weightPairEffectiveInclusionScoreWoDh * (1.0 - (1.0 * this.numOfIncludingEffectivePairsWoDh / LegState.maxNumOfIncludingEffectivePairsWoDh)));
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
//			double v1 = this.getDutyInclusionScore();
//			double v2 = this.getDutyInclusionScoreWoDh();
//			double v3 = this.getDutyEffectiveInclusionScore();
//			double v4 = this.getDutyEffectiveInclusionScoreWoDh();
//			double v5 = this.getPairInclusionScore();
//			double v6 = this.getPairInclusionScoreWoDh();
//			double v7 = this.getPairEffectiveInclusionScore();
//			double v8 = this.getPairEffectiveInclusionScoreWoDh();
//			double v9 = this.getHeuristicModifierScore();
////if (v1 + v2 + v3 + v4 + v5 > 1.0)
////System.out.println();
			return this.getDutyInclusionScore()
					+ this.getDutyInclusionScoreWoDh()
					+ this.getDutyEffectiveInclusionScore()
					+ this.getDutyEffectiveInclusionScoreWoDh()
					+ this.getPairInclusionScore()
					+ this.getPairInclusionScoreWoDh()
					+ this.getPairEffectiveInclusionScore()
					+ this.getPairEffectiveInclusionScoreWoDh()
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
								int numOfIncludingEffectiveDutiesWoDh,
								int numOfIncludingPairs,
								int numOfIncludingPairsWoDh,
								int numOfIncludingEffectivePairs,
								int numOfIncludingEffectivePairsWoDh) {
		return (this.numOfIncludingDuties == numOfIncludingDuties)
				&& (this.numOfIncludingDutiesWoDh == numOfIncludingDutiesWoDh)
				&& (this.numOfIncludingEffectiveDuties == numOfIncludingEffectiveDuties)
				&& (this.numOfIncludingEffectiveDutiesWoDh == numOfIncludingEffectiveDutiesWoDh)
				&& (this.numOfIncludingPairs == numOfIncludingPairs)
				&& (this.numOfIncludingPairsWoDh == numOfIncludingPairsWoDh)
				&& (this.numOfIncludingEffectivePairs == numOfIncludingEffectivePairs)
				&& (this.numOfIncludingEffectivePairsWoDh == numOfIncludingEffectivePairsWoDh);
	}

	@Override
	public String toString() {
		return "numOfCoverings: " + numOfCoverings + 
				", numOfIncludingDuties: " + numOfIncludingDuties + 
				", numOfIncludingDutiesWoDh: " + numOfIncludingDutiesWoDh + 
				", numOfIncludingEffectiveDuties: " + numOfIncludingEffectiveDuties + 
				", numOfIncludingEffectiveDutiesWoDh: " + numOfIncludingEffectiveDutiesWoDh + 
				", numOfIncludingPairs: " + numOfIncludingPairs + 
				", numOfIncludingPairsWoDh: " + numOfIncludingPairsWoDh + 
				", numOfIncludingEffectivePairs: " + numOfIncludingEffectivePairs + 
				", numOfIncludingEffectivePairsWoDh: " + numOfIncludingEffectivePairsWoDh + 
				", numOfIterations: " + numOfIterations + 
				", heuristicModifierValue: " + heuristicModifierValue;
		
	}
}
