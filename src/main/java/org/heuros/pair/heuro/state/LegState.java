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

	public void reset() {
		this.numOfCoverings = 0;
		this.numOfIncludingDuties = 0;
		this.numOfIncludingDutiesWoDh = 0;
		this.numOfIncludingEffectiveDuties = 0;
		this.numOfIncludingEffectiveDutiesWoDh = 0;
		this.potentialDh = false;
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
