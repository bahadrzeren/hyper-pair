package org.heuros.pair.heuro.state;

public class DutyState {
	public int numOfCoverings = 0;
	public int numOfCoveringsActive = 0;
	public int numOfCoveringsPassive = 0;
	public int numOfDistinctCoverings = 0;
	public int numOfDistinctCoveringsActive = 0;
	public int numOfDistinctCoveringsPassive = 0;
	public int blockTimeOfCoverings = 0;
	public int blockTimeOfCoveringsActive = 0;
	public int blockTimeOfCoveringsPassive = 0;
	public int blockTimeOfDistinctCoverings = 0;
	public int blockTimeOfDistinctCoveringsActive = 0;
	public int blockTimeOfDistinctCoveringsPassive = 0;
	/*
	 * Initial values are taken from Duty.
	 */
//	public int minNumOfAlternativeDuties = 0;
//	public int minNumOfAlternativeDutiesWoDh = 0;
//	public int maxNumOfAlternativeDuties = 0;
//	public int maxNumOfAlternativeDutiesWoDh = 0;
//	public int totalNumOfAlternativeDuties = 0;
//	public int totalNumOfAlternativeDutiesWoDh = 0;
	public boolean dhCritical = false;

	public void resetForNewIteration() {
		this.numOfCoverings = 0;
		this.numOfCoveringsActive = 0;
		this.numOfCoveringsPassive = 0;
		this.numOfDistinctCoverings = 0;
		this.numOfDistinctCoveringsActive = 0;
		this.numOfDistinctCoveringsPassive = 0;
		this.blockTimeOfCoverings = 0;
		this.blockTimeOfCoveringsActive = 0;
		this.blockTimeOfCoveringsPassive = 0;
		this.blockTimeOfDistinctCoverings = 0;
		this.blockTimeOfDistinctCoveringsActive = 0;
		this.blockTimeOfDistinctCoveringsPassive = 0;
		this.dhCritical = false;
	}
}
