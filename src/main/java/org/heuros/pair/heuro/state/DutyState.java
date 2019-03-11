package org.heuros.pair.heuro.state;

import org.heuros.data.model.Duty;

public class DutyState {
	private Duty associatedDuty = null;

	public DutyState(Duty associatedDuty) {
		this.associatedDuty = associatedDuty;
	}

	public int numOfCoverings = 0;
	public int numOfCoveringsActive = 0;
	public int numOfCoveringsPassiveInt = 0;
	public int numOfCoveringsPassiveExt = 0;
	public int numOfDistinctCoverings = 0;
	public int numOfDistinctCoveringsActive = 0;
	public int numOfDistinctCoveringsPassive = 0;
	public int blockTimeOfCoverings = 0;
	public int blockTimeOfCoveringsActive = 0;
	public int blockTimeOfCoveringsPassiveInt = 0;
	public int blockTimeOfCoveringsPassiveExt = 0;
	public int blockTimeOfDistinctCoverings = 0;
	public int blockTimeOfDistinctCoveringsActive = 0;
	public int blockTimeOfDistinctCoveringsPassive = 0;
	/*
	 * Initial values are taken from Duty.
	 */
	public int minNumOfAlternativeDuties = 0;
	public int minNumOfAlternativeDutiesWoDh = 0;
	public int maxNumOfAlternativeDuties = 0;
	public int maxNumOfAlternativeDutiesWoDh = 0;
	public int totalNumOfAlternativeDuties = 0;
	public int totalNumOfAlternativeDutiesWoDh = 0;
	public int minNumOfAlternativeEffectiveDuties = 0;
	public int minNumOfAlternativeEffectiveDutiesWoDh = 0;
	public int maxNumOfAlternativeEffectiveDuties = 0;
	public int maxNumOfAlternativeEffectiveDutiesWoDh = 0;
	public int totalNumOfAlternativeEffectiveDuties = 0;
	public int totalNumOfAlternativeEffectiveDutiesWoDh = 0;

	private void resetForNewIteration() {
		this.numOfCoverings = 0;
		this.numOfCoveringsActive = 0;
		this.numOfCoveringsPassiveInt = 0;
		this.numOfCoveringsPassiveExt = 0;
		this.numOfDistinctCoverings = 0;
		this.numOfDistinctCoveringsActive = 0;
		this.numOfDistinctCoveringsPassive = 0;
		this.blockTimeOfCoverings = 0;
		this.blockTimeOfCoveringsActive = 0;
		this.blockTimeOfCoveringsPassiveInt = 0;
		this.blockTimeOfCoveringsPassiveExt = 0;
		this.blockTimeOfDistinctCoverings = 0;
		this.blockTimeOfDistinctCoveringsActive = 0;
		this.blockTimeOfDistinctCoveringsPassive = 0;
	}

	public void initializeForNewIteration(Duty duty) {
		this.resetForNewIteration();
		this.minNumOfAlternativeDuties = duty.getMinNumOfAlternativeDuties();
		this.minNumOfAlternativeDutiesWoDh = duty.getMinNumOfAlternativeDutiesWoDh();
		this.maxNumOfAlternativeDuties = duty.getMaxNumOfAlternativeDuties();
		this.maxNumOfAlternativeDutiesWoDh = duty.getMaxNumOfAlternativeDutiesWoDh();
		this.totalNumOfAlternativeDuties = duty.getTotalNumOfAlternativeDuties();
		this.totalNumOfAlternativeDutiesWoDh = duty.getTotalNumOfAlternativeDutiesWoDh();
		this.minNumOfAlternativeEffectiveDuties = duty.getMinNumOfAlternativeEffectiveDuties();
		this.minNumOfAlternativeEffectiveDutiesWoDh = duty.getMinNumOfAlternativeEffectiveDutiesWoDh();
		this.maxNumOfAlternativeEffectiveDuties = duty.getMaxNumOfAlternativeEffectiveDuties();
		this.maxNumOfAlternativeEffectiveDutiesWoDh = duty.getMaxNumOfAlternativeEffectiveDutiesWoDh();
		this.totalNumOfAlternativeEffectiveDuties = duty.getTotalNumOfAlternativeEffectiveDuties();
		this.totalNumOfAlternativeEffectiveDutiesWoDh = duty.getTotalNumOfAlternativeEffectiveDutiesWoDh();
	}

	/*
	 * Cumulative values that do not reset during optimization.
	 */
	public double totalHeuristicModifiers = 0.0;

	private double getInclusionScore() {
		return ((LegState.weightInclusionScore / LegState.maxNumOfIncludingDuties)
				* ((this.associatedDuty.getNumOfLegsActive() - this.numOfCoveringsActive) * LegState.maxNumOfIncludingDuties - this.totalNumOfAlternativeDuties));
	}

	private double getInclusionScoreWoDh() {
		return ((LegState.weightInclusionScoreWoDh / LegState.maxNumOfIncludingDutiesWoDh)
				* ((this.associatedDuty.getNumOfLegsActive() - this.numOfCoveringsActive) * LegState.maxNumOfIncludingDutiesWoDh - this.totalNumOfAlternativeDutiesWoDh));
	}

	private double getEffectiveInclusionScore() {
		return ((LegState.weightEffectiveInclusionScore / LegState.maxNumOfIncludingEffectiveDuties)
				* ((this.associatedDuty.getNumOfLegsActive() - this.numOfCoveringsActive) * LegState.maxNumOfIncludingEffectiveDuties - this.totalNumOfAlternativeEffectiveDuties));
	}

	private double getEffectiveInclusionScoreWoDh() {
		return ((LegState.weightEffectiveInclusionScoreWoDh / LegState.maxNumOfIncludingEffectiveDutiesWoDh)
				* ((this.associatedDuty.getNumOfLegsActive() - this.numOfCoveringsActive) * LegState.maxNumOfIncludingEffectiveDutiesWoDh - this.totalNumOfAlternativeEffectiveDutiesWoDh));
	}

	private double getHeuristicModifierScore() {
		if (LegState.maxHeuristicModifierValue > 0.0) {
			return (this.totalHeuristicModifiers * LegState.weightHeuristicModifier / LegState.maxHeuristicModifierValue);
		} else {
			return 0.0;
		}
	}

	public double getDifficultyScoreOfTheLeg() {
		if ((this.associatedDuty.getNumOfLegsActive() - this.numOfCoveringsActive) > 0) {
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
	 * Validation tests.
	 */
	public boolean stateVariablesAreOk(int numOfCoverings, 
										int numOfCoveringsActive, 
										int numOfCoveringsPassiveInt, 
										int numOfCoveringsPassiveExt, 
										int numOfDistinctCoverings, 
										int numOfDistinctCoveringsActive, 
										int numOfDistinctCoveringsPassive, 
										int blockTimeOfCoverings, 
										int blockTimeOfCoveringsActive, 
										int blockTimeOfCoveringsPassiveInt, 
										int blockTimeOfCoveringsPassiveExt, 
										int blockTimeOfDistinctCoverings, 
										int blockTimeOfDistinctCoveringsActive, 
										int blockTimeOfDistinctCoveringsPassive) {
		return (this.numOfCoverings == numOfCoverings)
				&& (this.numOfCoveringsActive == numOfCoveringsActive)
				&& (this.numOfCoveringsPassiveInt == numOfCoveringsPassiveInt)
				&& (this.numOfCoveringsPassiveExt == numOfCoveringsPassiveExt)
				&& (this.numOfDistinctCoverings == numOfDistinctCoverings)
				&& (this.numOfDistinctCoveringsActive == numOfDistinctCoveringsActive)
				&& (this.numOfDistinctCoveringsPassive == numOfDistinctCoveringsPassive)
				&& (this.blockTimeOfCoverings == blockTimeOfCoverings)
				&& (this.blockTimeOfCoveringsActive == blockTimeOfCoveringsActive)
				&& (this.blockTimeOfCoveringsPassiveInt == blockTimeOfCoveringsPassiveInt)
				&& (this.blockTimeOfCoveringsPassiveExt == blockTimeOfCoveringsPassiveExt)
				&& (this.blockTimeOfDistinctCoverings == blockTimeOfDistinctCoverings)
				&& (this.blockTimeOfDistinctCoveringsActive == blockTimeOfDistinctCoveringsActive)
				&& (this.blockTimeOfDistinctCoveringsPassive == blockTimeOfDistinctCoveringsPassive);
	}

	public boolean totaizersAreOk(int minNumOfAlternativeDuties,
								int minNumOfAlternativeDutiesWoDh,
								int maxNumOfAlternativeDuties,
								int maxNumOfAlternativeDutiesWoDh,
								int totalNumOfAlternativeDuties,
								int totalNumOfAlternativeDutiesWoDh,
								int minNumOfAlternativeEffectiveDuties,
								int minNumOfAlternativeEffectiveDutiesWoDh,
								int maxNumOfAlternativeEffectiveDuties,
								int maxNumOfAlternativeEffectiveDutiesWoDh,
								int totalNumOfAlternativeEffectiveDuties,
								int totalNumOfAlternativeEffectiveDutiesWoDh,
								double totalHeuristicModifiers) {
		return (this.minNumOfAlternativeDuties == minNumOfAlternativeDuties)
				&& (this.minNumOfAlternativeDutiesWoDh == minNumOfAlternativeDutiesWoDh)
//				&& (this.maxNumOfAlternativeDuties == maxNumOfAlternativeDuties)
//				&& (this.maxNumOfAlternativeDutiesWoDh == maxNumOfAlternativeDutiesWoDh)
				&& (this.totalNumOfAlternativeDuties == totalNumOfAlternativeDuties)
				&& (this.totalNumOfAlternativeDutiesWoDh == totalNumOfAlternativeDutiesWoDh)
				&& (this.minNumOfAlternativeEffectiveDuties == minNumOfAlternativeEffectiveDuties)
				&& (this.minNumOfAlternativeEffectiveDutiesWoDh == minNumOfAlternativeEffectiveDutiesWoDh)
//				&& (this.maxNumOfAlternativeEffectiveDuties == maxNumOfAlternativeEffectiveDuties)
//				&& (this.maxNumOfAlternativeEffectiveDutiesWoDh == maxNumOfAlternativeEffectiveDutiesWoDh)
				&& (this.totalNumOfAlternativeEffectiveDuties == totalNumOfAlternativeEffectiveDuties)
				&& (this.totalNumOfAlternativeEffectiveDutiesWoDh == totalNumOfAlternativeEffectiveDutiesWoDh)
				&& (this.totalHeuristicModifiers == totalHeuristicModifiers);
	}
}
