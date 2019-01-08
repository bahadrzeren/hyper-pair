package org.heuros.pair.sp;

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

	private int priority = Integer.MAX_VALUE;

	public QualityMetric() {
	}

	public QualityMetric(QualityMetric qmToCopy) {
		this.numOfDh = qmToCopy.numOfDh;
		this.dhDurationInMins = qmToCopy.dhDurationInMins;
		this.activeBlocktimeInMins = qmToCopy.activeBlocktimeInMins;
		this.numOfDuties = qmToCopy.numOfDuties;
		this.numOfIncludingDutiesOfTheSameLegs = qmToCopy.numOfIncludingDutiesOfTheSameLegs;
		this.numOfLegs = qmToCopy.numOfLegs;
		this.priority = qmToCopy.priority;
	}

	public QualityMetric(DutyView duty,
							int numOfCoveringsInDuty,
							int blockTimeOfCoveringsInDuty,
							int priority) {
		this.numOfDh = (duty.getNumOfLegsPassive() + numOfCoveringsInDuty);
		this.dhDurationInMins = (duty.getBlockTimeInMinsPassive() + blockTimeOfCoveringsInDuty);
		this.activeBlocktimeInMins = (duty.getBlockTimeInMinsActive() - blockTimeOfCoveringsInDuty);
		this.numOfDuties = 1;
		this.numOfIncludingDutiesOfTheSameLegs = duty.getTotalNumOfIncludingDutiesOfTheSameLegs();
		this.numOfLegs = duty.getNumOfLegs();
		this.priority = priority;
	}

	public void injectValues(QualityMetric qmToCopy) {
		this.numOfDh = qmToCopy.numOfDh;
		this.dhDurationInMins = qmToCopy.dhDurationInMins;
		this.activeBlocktimeInMins = qmToCopy.activeBlocktimeInMins;
		this.numOfDuties = qmToCopy.numOfDuties;
		this.numOfIncludingDutiesOfTheSameLegs = qmToCopy.numOfIncludingDutiesOfTheSameLegs;
		this.numOfLegs = qmToCopy.numOfLegs;
		this.priority = qmToCopy.priority;
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
		this.priority = Integer.MAX_VALUE;
	}

	public void addToQualityMetric(DutyView d,
 									int numOfCoveringsInDuty,
									int blockTimeOfCoveringsInDuty,
									int priority) {
		this.numOfDh += (d.getNumOfLegsPassive() + numOfCoveringsInDuty);
		this.dhDurationInMins += (d.getBlockTimeInMinsPassive() + blockTimeOfCoveringsInDuty);
		this.activeBlocktimeInMins += (d.getBlockTimeInMinsActive() - blockTimeOfCoveringsInDuty);
		this.numOfDuties++;
		this.numOfIncludingDutiesOfTheSameLegs += d.getTotalNumOfIncludingDutiesOfTheSameLegs();
		this.numOfLegs += d.getNumOfLegs();
		this.priority += priority;
	}

	public void addToQualityMetric(QualityMetric qmToAdd) {
		this.numOfDh += qmToAdd.numOfDh;
		this.dhDurationInMins += qmToAdd.dhDurationInMins;
		this.activeBlocktimeInMins += qmToAdd.activeBlocktimeInMins;
		this.numOfDuties += qmToAdd.numOfDuties;
		this.numOfIncludingDutiesOfTheSameLegs += qmToAdd.numOfIncludingDutiesOfTheSameLegs;
		this.numOfLegs += qmToAdd.numOfLegs;
		this.priority += qmToAdd.priority;
	}

	public void removeFromQualityMetric(DutyView d,
										int numOfCoveringsInDuty,
										int blockTimeOfCoveringsInDuty,
										int priority) {
		this.numOfDh -= (d.getNumOfLegsPassive() + numOfCoveringsInDuty);
		this.dhDurationInMins -= (d.getBlockTimeInMinsPassive() + blockTimeOfCoveringsInDuty);
		this.activeBlocktimeInMins -= (d.getBlockTimeInMinsActive() - blockTimeOfCoveringsInDuty);
		this.numOfDuties--;
		this.numOfIncludingDutiesOfTheSameLegs -= d.getTotalNumOfIncludingDutiesOfTheSameLegs();
		this.numOfLegs -= d.getNumOfLegs();
		this.priority -= priority;
	}

	public void removeFromQualityMetric(QualityMetric qmToRemove) {
		this.numOfDh -= qmToRemove.numOfDh;
		this.dhDurationInMins -= qmToRemove.dhDurationInMins;
		this.activeBlocktimeInMins -= qmToRemove.activeBlocktimeInMins;
		this.numOfDuties -= qmToRemove.numOfDuties;
		this.numOfIncludingDutiesOfTheSameLegs -= qmToRemove.numOfIncludingDutiesOfTheSameLegs;
		this.numOfLegs -= qmToRemove.numOfLegs;
		this.priority -= qmToRemove.priority;
	}

	public boolean isBetterThan(int heuristicNo, QualityMetric qm) {
		if ((qm.numOfLegs == 0)
				|| (this.priority < qm.priority)
				|| ((this.priority == qm.priority) && (this.numOfDh < qm.numOfDh))
				|| ((this.priority == qm.priority) && (this.numOfDh == qm.numOfDh) && (((1.0 * this.activeBlocktimeInMins) / this.numOfDuties) > ((1.0 * qm.activeBlocktimeInMins) / qm.numOfDuties)))) {
			return true;
		} else
			return false;
	}

	public boolean doesItWorthToGoDeeper(int maxDutyBlockTimeInMins,
											int heuristicNo,
											int currentDept,
											QualityMetric bestQualSoFar) {
			if ((bestQualSoFar == null)
					|| (bestQualSoFar.priority > this.priority)) {
				return true;
			} else
				return false;
	}

//	public boolean isBetterThan(int heuristicNo, QualityMetric qm) {
//		if (heuristicNo < 2) {	//	If layover or dh effective. 
//			if ((qm.numOfLegs == 0)
//					|| (this.numOfDh < qm.numOfDh)
//					|| ((this.numOfDh == qm.numOfDh) && (this.dhDurationInMins < qm.dhDurationInMins))
//					|| ((this.numOfDh == qm.numOfDh) && (this.dhDurationInMins == qm.dhDurationInMins) 
////							&& (((1.0 * this.numOfIncludingDutiesOfTheSameLegs) / this.numOfLegs) < ((1.0 * qm.numOfIncludingDutiesOfTheSameLegs) / qm.numOfLegs)))) {
//							&& (((1.0 * this.activeBlocktimeInMins) / this.numOfDuties) > ((1.0 * qm.activeBlocktimeInMins) / qm.numOfDuties)))) {
//				return true;
//			} else
//				return false;
//		} else {	//	If active block time effective.
//			if ((qm.numOfLegs == 0)
//					|| (((1.0 * this.activeBlocktimeInMins) / this.numOfDuties) > ((1.0 * qm.activeBlocktimeInMins) / qm.numOfDuties))
//					|| ((((1.0 * this.activeBlocktimeInMins) / this.numOfDuties) == ((1.0 * qm.activeBlocktimeInMins) / qm.numOfDuties))
////							&& (((1.0 * this.numOfIncludingDutiesOfTheSameLegs) / this.numOfLegs) < ((1.0 * qm.numOfIncludingDutiesOfTheSameLegs) / qm.numOfLegs)))) {
//							&& (this.numOfDh < qm.numOfDh))) {
//				return true;
//			} else
//				return false;
//		}
//	}
//
//	public boolean doesItWorthToGoDeeper(int maxDutyBlockTimeInMins,
//											int heuristicNo,
//											int currentDept,
//											QualityMetric bestQualSoFar) {
//		if (heuristicNo < 2) {	//	If layover or dh effective.
//			if ((bestQualSoFar == null)
//					|| (bestQualSoFar.numOfDh > this.numOfDh)
//					|| ((bestQualSoFar.numOfDh == this.numOfDh) && (bestQualSoFar.dhDurationInMins >= this.dhDurationInMins))
////					|| ((bestQualSoFar.numOfDh == this.numOfDh) && (bestQualSoFar.dhDurationInMins == this.dhDurationInMins) 
////							&& (((1.0 * bestQualSoFar.numOfIncludingDutiesOfTheSameLegs) / bestQualSoFar.numOfLegs) >= ((1.0 * this.numOfIncludingDutiesOfTheSameLegs) / this.numOfLegs)))
//					) {
//				return true;
//			} else
//				return false;
//		} else {	//	If active block time effective.
//			if ((bestQualSoFar == null)
//					|| (((1.0 * bestQualSoFar.activeBlocktimeInMins) / bestQualSoFar.numOfDuties) <= ((1.0 * this.activeBlocktimeInMins + (currentDept - 1) * maxDutyBlockTimeInMins) / (this.numOfDuties + (currentDept - 1))))
////					|| ((((1.0 * bestQualSoFar.activeBlocktimeInMins) / bestQualSoFar.numOfDuties) == ((1.0 * this.activeBlocktimeInMins + (currentDept - 1) * maxDutyBlockTimeInMins) / (this.numOfDuties + (currentDept - 1))))
////							&& (((1.0 * bestQualSoFar.numOfIncludingDutiesOfTheSameLegs) / bestQualSoFar.numOfLegs) < ((1.0 * this.numOfIncludingDutiesOfTheSameLegs) / this.numOfLegs)))
//					) {
//				return true;
//			} else
//				return false;
//		}
//	}

	public String toString() {
		return "#DH:" + numOfDh + "/" + dhDurationInMins +
				", ABT:" + activeBlocktimeInMins + "/" + numOfDuties + "=" + (Math.floor((100.0 * activeBlocktimeInMins) / numOfDuties) / 100.0) +
				", #LD:" + numOfIncludingDutiesOfTheSameLegs + "/" + numOfLegs + "=" + (Math.floor((100.0 * numOfIncludingDutiesOfTheSameLegs) / numOfLegs) / 100.0);
	}
}
