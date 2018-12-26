package org.heuros.hyperpair.heuristic;

import org.heuros.data.model.DutyView;

public class QualityMetric {
	/*
	 * Deadhead
	 */
	private int numOfDh = 0;
	private int dhDurationInMins = 0;
	/*
	 * Dutyday
	 */
	private int activeBlocktimeInMins = 0;
	private int numOfDuties = 0;
	/*
	 * The last metric to check if others are equal!
	 */
	private int numOfIncludingDutiesOfTheSameLegs = 0;
	private int numOfLegs = 0;

	public QualityMetric() {
	}

	public QualityMetric(QualityMetric qmToCopy) {
		this.numOfDh = qmToCopy.numOfDh;
		this.dhDurationInMins = qmToCopy.dhDurationInMins;
		this.activeBlocktimeInMins = qmToCopy.activeBlocktimeInMins;
		this.numOfDuties = qmToCopy.numOfDuties;
		this.numOfIncludingDutiesOfTheSameLegs = qmToCopy.numOfIncludingDutiesOfTheSameLegs;
		this.numOfLegs = qmToCopy.numOfLegs;
	}

	public QualityMetric(DutyView duty,
							int[] numOfCoveringsInDuties,
							int[] blockTimeOfCoveringsInDuties) {
		this.numOfDh = (duty.getNumOfLegsPassive() + numOfCoveringsInDuties[duty.getNdx()]);
		this.dhDurationInMins = (duty.getBlockTimeInMinsPassive() + blockTimeOfCoveringsInDuties[duty.getNdx()]);
		this.activeBlocktimeInMins = (duty.getBlockTimeInMinsActive() - blockTimeOfCoveringsInDuties[duty.getNdx()]);
		this.numOfDuties = 1;
		this.numOfIncludingDutiesOfTheSameLegs = duty.getTotalNumOfIncludingDutiesOfTheSameLegs();
		this.numOfLegs = duty.getNumOfLegs();
	}

	public void injectValues(QualityMetric qmToCopy) {
		this.numOfDh = qmToCopy.numOfDh;
		this.dhDurationInMins = qmToCopy.dhDurationInMins;
		this.activeBlocktimeInMins = qmToCopy.activeBlocktimeInMins;
		this.numOfDuties = qmToCopy.numOfDuties;
		this.numOfIncludingDutiesOfTheSameLegs = qmToCopy.numOfIncludingDutiesOfTheSameLegs;
		this.numOfLegs = qmToCopy.numOfLegs;
	}

//	public void injectValues(DutyView nodeOwner,
//								int[] numOfCoveringsInDuties,
//								int[] blockTimeOfCoveringsInDuties) {
//		this.numOfDh = nodeOwner.getNumOfLegsPassive() + numOfCoveringsInDuties[nodeOwner.getNdx()];
//		this.dhDurationInMins = nodeOwner.getBlockTimeInMinsPassive() + blockTimeOfCoveringsInDuties[nodeOwner.getNdx()];
//		this.activeBlocktimeInMins = nodeOwner.getBlockTimeInMinsActive() - blockTimeOfCoveringsInDuties[nodeOwner.getNdx()];
//		this.numOfDuties = 1;
//		this.numOfIncludingDutiesOfTheSameLegs = nodeOwner.getTotalNumOfIncludingDutiesOfTheSameLegs();
//		this.numOfLegs = nodeOwner.getNumOfLegs();
//		this.nodeOwner = nodeOwner;
//	}

	public void reset() {
		this.numOfDh = 0;
		this.dhDurationInMins = 0;
		this.activeBlocktimeInMins = 0;
		this.numOfDuties = 0;
		this.numOfIncludingDutiesOfTheSameLegs = 0;
		this.numOfLegs = 0;
	}

	public void addToQualityMetric(DutyView d,
 									int[] numOfCoveringsInDuties,
									int[] blockTimeOfCoveringsInDuties) {
		this.numOfDh += (d.getNumOfLegsPassive() + numOfCoveringsInDuties[d.getNdx()]);
		this.dhDurationInMins += (d.getBlockTimeInMinsPassive() + blockTimeOfCoveringsInDuties[d.getNdx()]);
		this.activeBlocktimeInMins += (d.getBlockTimeInMinsActive() - blockTimeOfCoveringsInDuties[d.getNdx()]);
		this.numOfDuties++;
		this.numOfIncludingDutiesOfTheSameLegs += d.getTotalNumOfIncludingDutiesOfTheSameLegs();
		this.numOfLegs += d.getNumOfLegs();
	}

	public void addToQualityMetric(QualityMetric qmToAdd) {
		this.numOfDh += qmToAdd.numOfDh;
		this.dhDurationInMins += qmToAdd.dhDurationInMins;
		this.activeBlocktimeInMins += qmToAdd.activeBlocktimeInMins;
		this.numOfDuties += qmToAdd.numOfDuties;
		this.numOfIncludingDutiesOfTheSameLegs += qmToAdd.numOfIncludingDutiesOfTheSameLegs;
		this.numOfLegs += qmToAdd.numOfLegs;
	}

	public void removeFromQualityMetric(DutyView d,
										int[] numOfCoveringsInDuties,
										int[] blockTimeOfCoveringsInDuties) {
		this.numOfDh -= (d.getNumOfLegsPassive() + numOfCoveringsInDuties[d.getNdx()]);
		this.dhDurationInMins -= (d.getBlockTimeInMinsPassive() + blockTimeOfCoveringsInDuties[d.getNdx()]);
		this.activeBlocktimeInMins -= (d.getBlockTimeInMinsActive() - blockTimeOfCoveringsInDuties[d.getNdx()]);
		this.numOfDuties--;
		this.numOfIncludingDutiesOfTheSameLegs -= d.getTotalNumOfIncludingDutiesOfTheSameLegs();
		this.numOfLegs -= d.getNumOfLegs();
	}

	public void removeFromQualityMetric(QualityMetric qmToRemove) {
		this.numOfDh -= qmToRemove.numOfDh;
		this.dhDurationInMins -= qmToRemove.dhDurationInMins;
		this.activeBlocktimeInMins -= qmToRemove.activeBlocktimeInMins;
		this.numOfDuties -= qmToRemove.numOfDuties;
		this.numOfIncludingDutiesOfTheSameLegs -= qmToRemove.numOfIncludingDutiesOfTheSameLegs;
		this.numOfLegs -= qmToRemove.numOfLegs;
	}

	public boolean isBetterThan(int heuristicNo, QualityMetric qm) {
		if (heuristicNo < 2) {	//	If layover or dh effective. 
			if ((qm.numOfLegs == 0)
					|| (this.numOfDh < qm.numOfDh)
					|| ((this.numOfDh == qm.numOfDh) && (this.dhDurationInMins < qm.dhDurationInMins))
					|| ((this.numOfDh == qm.numOfDh) && (this.dhDurationInMins == qm.dhDurationInMins) 
							&& (((1.0 * this.numOfIncludingDutiesOfTheSameLegs) / this.numOfLegs) < ((1.0 * qm.numOfIncludingDutiesOfTheSameLegs) / qm.numOfLegs)))) {
				return true;
			} else
				return false;
		} else {	//	If active block time effective.
			if ((qm.numOfLegs == 0)
					|| (((1.0 * this.activeBlocktimeInMins) / this.numOfDuties) > ((1.0 * qm.activeBlocktimeInMins) / qm.numOfDuties))
					|| ((((1.0 * this.activeBlocktimeInMins) / this.numOfDuties) == ((1.0 * qm.activeBlocktimeInMins) / qm.numOfDuties))
							&& (((1.0 * this.numOfIncludingDutiesOfTheSameLegs) / this.numOfLegs) < ((1.0 * qm.numOfIncludingDutiesOfTheSameLegs) / qm.numOfLegs)))) {
				return true;
			} else
				return false;
		}
	}

	public boolean doesItWorthToGoDeeper(int maxDutyBlockTimeInMins,
											int heuristicNo,
											int currentDept,
											QualityMetric bestQualSoFar) {
		if (heuristicNo < 2) {	//	If layover or dh effective.
			if ((bestQualSoFar == null)
					|| (bestQualSoFar.numOfDh > this.numOfDh)
					|| ((bestQualSoFar.numOfDh == this.numOfDh) && (bestQualSoFar.dhDurationInMins >= this.dhDurationInMins))
//					|| ((bestQualSoFar.numOfDh == this.numOfDh) && (bestQualSoFar.dhDurationInMins == this.dhDurationInMins) 
//							&& (((1.0 * bestQualSoFar.numOfIncludingDutiesOfTheSameLegs) / bestQualSoFar.numOfLegs) >= ((1.0 * this.numOfIncludingDutiesOfTheSameLegs) / this.numOfLegs)))
					) {
				return true;
			} else
				return false;
		} else {	//	If active block time effective.
			if ((bestQualSoFar == null)
					|| (((1.0 * bestQualSoFar.activeBlocktimeInMins) / bestQualSoFar.numOfDuties) <= ((1.0 * this.activeBlocktimeInMins + (currentDept - 1) * maxDutyBlockTimeInMins) / (this.numOfDuties + (currentDept - 1))))
//					|| ((((1.0 * bestQualSoFar.activeBlocktimeInMins) / bestQualSoFar.numOfDuties) == ((1.0 * this.activeBlocktimeInMins + (currentDept - 1) * maxDutyBlockTimeInMins) / (this.numOfDuties + (currentDept - 1))))
//							&& (((1.0 * bestQualSoFar.numOfIncludingDutiesOfTheSameLegs) / bestQualSoFar.numOfLegs) < ((1.0 * this.numOfIncludingDutiesOfTheSameLegs) / this.numOfLegs)))
					) {
				return true;
			} else
				return false;
		}
	}

	public String toString() {
		return "#DH:" + numOfDh + ", DHD:" + dhDurationInMins +
				", ABT/#D:" + activeBlocktimeInMins + "/" + numOfDuties +
				", #LD:" + numOfIncludingDutiesOfTheSameLegs +
				", #L:" + numOfLegs;
	}
}
