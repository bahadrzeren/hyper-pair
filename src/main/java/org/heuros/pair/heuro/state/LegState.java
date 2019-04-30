package org.heuros.pair.heuro.state;

import org.heuros.data.model.Leg;
import org.heuros.pair.conf.HeurosSystemParam;

public class LegState implements Cloneable {

	public static int maxNumOfIncludingDuties = 0;
	public static int maxNumOfIncludingDutiesWoDh = 0;
	public static int maxNumOfIncludingEffectiveDuties = 0;
	public static int maxNumOfIncludingEffectiveDutiesWoDh = 0;

	public static int maxNumOfIncludingPairs = 0;
	public static int maxNumOfIncludingPairsWoDh = 0;
	public static int maxNumOfIncludingEffectivePairs = 0;
	public static int maxNumOfIncludingEffectivePairsWoDh = 0;

	public static double maxHeurModDh = 0.0;
	public static double maxHeurModEf = 0.0;

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
	public double bestHeurModDh= 0.0;
	public double bestHeurModEf= 0.0;
	public double actHeurModDh= 0.0;
	public double actHeurModEf= 0.0;

	private double getDutyInclusionScore() {
		return (1.0 - (1.0 * this.numOfIncludingDuties) / LegState.maxNumOfIncludingDuties);
	}

	private double getDutyInclusionScoreWoDh() {
		return (1.0 - (1.0 * this.numOfIncludingDutiesWoDh) / LegState.maxNumOfIncludingDutiesWoDh);
	}

	private double getDutyEffectiveInclusionScore() {
		return (1.0 - (1.0 * this.numOfIncludingEffectiveDuties) / LegState.maxNumOfIncludingEffectiveDuties);
	}

	private double getDutyEffectiveInclusionScoreWoDh() {
		return (1.0 - (1.0 * this.numOfIncludingEffectiveDutiesWoDh) / LegState.maxNumOfIncludingEffectiveDutiesWoDh);
	}

	private double getPairInclusionScore() {
		return (1.0 - (1.0 * this.numOfIncludingPairs) / LegState.maxNumOfIncludingPairs);
	}

	private double getPairInclusionScoreWoDh() {
		return (1.0 - (1.0 * this.numOfIncludingPairsWoDh) / LegState.maxNumOfIncludingPairsWoDh);
	}

	private double getPairEffectiveInclusionScore() {
		return (1.0 - (1.0 * this.numOfIncludingEffectivePairs) / LegState.maxNumOfIncludingEffectivePairs);
	}

	private double getPairEffectiveInclusionScoreWoDh() {
		return (1.0 - (1.0 * this.numOfIncludingEffectivePairsWoDh) / LegState.maxNumOfIncludingEffectivePairsWoDh);
	}

	private double getHeurModDhScore() {
		if (LegState.maxHeurModDh > 0.0) {
			return this.actHeurModDh / LegState.maxHeurModDh;
		} else {
			return 0.0;
		}
	}

	private double getHeurModEfScore() {
		if (LegState.maxHeurModEf > 0.0) {
			return this.actHeurModEf / LegState.maxHeurModEf;
		} else {
			return 0.0;
		}
	}

//	public double getDifficultyScoreWithoutHeurMods() {
//		if (this.associatedLeg.isCover()
//				&& (this.numOfCoverings == 0)) {
//			return this.getDutyInclusionScore()
//					+ this.getDutyInclusionScoreWoDh()
//					+ this.getDutyEffectiveInclusionScore()
//					+ this.getDutyEffectiveInclusionScoreWoDh()
//					+ this.getPairInclusionScore()
//					+ this.getPairInclusionScoreWoDh()
//					+ this.getPairEffectiveInclusionScore()
//					+ this.getPairEffectiveInclusionScoreWoDh();
//		} else {
//			return 0.0;
//		}
//	}

//	public double getWeightedDifficultyScoreWithoutHeurMods() {
//		if (this.associatedLeg.isCover()
//				&& (this.numOfCoverings == 0)) {
//			return HeurosSystemParam.weightDutyInclusionScore * this.getDutyInclusionScore()
//					+ HeurosSystemParam.weightDutyInclusionScoreWoDh * this.getDutyInclusionScoreWoDh()
//					+ HeurosSystemParam.weightDutyEffectiveInclusionScore * this.getDutyEffectiveInclusionScore()
//					+ HeurosSystemParam.weightDutyEffectiveInclusionScoreWoDh * this.getDutyEffectiveInclusionScoreWoDh()
//					+ HeurosSystemParam.weightPairInclusionScore * this.getPairInclusionScore()
//					+ HeurosSystemParam.weightPairInclusionScoreWoDh * this.getPairInclusionScoreWoDh()
//					+ HeurosSystemParam.weightPairEffectiveInclusionScore * this.getPairEffectiveInclusionScore()
//					+ HeurosSystemParam.weightPairEffectiveInclusionScoreWoDh * this.getPairEffectiveInclusionScoreWoDh();
//		} else {
//			return 0.0;
//		}
//	}

	public double getWeightedDifficultyScore() {
		if (this.associatedLeg.isCover()
//				&& (this.numOfCoverings == 0)
				) {
//			double v1 = this.getDutyInclusionScore();
//			double v2 = this.getDutyInclusionScoreWoDh();
//			double v3 = this.getDutyEffectiveInclusionScore();
//			double v4 = this.getDutyEffectiveInclusionScoreWoDh();
//			double v5 = this.getPairInclusionScore();
//			double v6 = this.getPairInclusionScoreWoDh();
//			double v7 = this.getPairEffectiveInclusionScore();
//			double v8 = this.getPairEffectiveInclusionScoreWoDh();
//			double v9 = this.getHeurModDhScore();
//			double v10 = this.getHeurModEfScore();
			return HeurosSystemParam.weightDutyInclusionScore * this.getDutyInclusionScore()
					+ HeurosSystemParam.weightDutyInclusionScoreWoDh * this.getDutyInclusionScoreWoDh()
					+ HeurosSystemParam.weightDutyEffectiveInclusionScore * this.getDutyEffectiveInclusionScore()
					+ HeurosSystemParam.weightDutyEffectiveInclusionScoreWoDh * this.getDutyEffectiveInclusionScoreWoDh()
					+ HeurosSystemParam.weightPairInclusionScore * this.getPairInclusionScore()
					+ HeurosSystemParam.weightPairInclusionScoreWoDh * this.getPairInclusionScoreWoDh()
					+ HeurosSystemParam.weightPairEffectiveInclusionScore * this.getPairEffectiveInclusionScore()
					+ HeurosSystemParam.weightPairEffectiveInclusionScoreWoDh * this.getPairEffectiveInclusionScoreWoDh()
					+ HeurosSystemParam.weightHeurModDh * this.getHeurModDhScore()
					+ HeurosSystemParam.weightHeurModEf * this.getHeurModEfScore();
		} else {
			return 0.0;
		}
	}

	/*
	 * Validation test.
	 */
	public boolean areDutyTotalizersOk_ParamOrderingIsChanged(int numOfIncludingDuties,
										int numOfIncludingEffectiveDuties,
										int numOfIncludingDutiesWoDh,
										int numOfIncludingEffectiveDutiesWoDh) {
		return (this.numOfIncludingDuties == numOfIncludingDuties)
				&& (this.numOfIncludingEffectiveDuties == numOfIncludingEffectiveDuties)
				&& (this.numOfIncludingDutiesWoDh == numOfIncludingDutiesWoDh)
				&& (this.numOfIncludingEffectiveDutiesWoDh == numOfIncludingEffectiveDutiesWoDh);
	}

	public boolean arePairTotalizersOk(int numOfIncludingPairs,
										int numOfIncludingEffectivePairs,
										int numOfIncludingPairsWoDh,
										int numOfIncludingEffectivePairsWoDh) {
		return 
//				(this.numOfIncludingPairs == numOfIncludingPairs) && 
//				(this.numOfIncludingEffectivePairs == numOfIncludingEffectivePairs) && 
				(this.numOfIncludingPairsWoDh == numOfIncludingPairsWoDh)
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
				", heurModDh: " + actHeurModDh +
				", heurModEf: " + actHeurModEf;
		
	}
}
