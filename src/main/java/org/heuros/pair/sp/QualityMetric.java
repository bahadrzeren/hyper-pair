package org.heuros.pair.sp;

import org.heuros.data.model.DutyView;
import org.heuros.pair.heuro.DutyParam;

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

	private int numOfAlternativeDutiesWoDh = 0;

	public QualityMetric() {
	}

	public QualityMetric(QualityMetric qmToCopy) {
		this.numOfDh = qmToCopy.numOfDh;
		this.dhDurationInMins = qmToCopy.dhDurationInMins;
		this.activeBlocktimeInMins = qmToCopy.activeBlocktimeInMins;
		this.numOfDuties = qmToCopy.numOfDuties;
		this.numOfIncludingDutiesOfTheSameLegs = qmToCopy.numOfIncludingDutiesOfTheSameLegs;
		this.numOfLegs = qmToCopy.numOfLegs;
		this.numOfAlternativeDutiesWoDh = qmToCopy.numOfAlternativeDutiesWoDh;
	}

	public QualityMetric(DutyView duty,
							DutyParam dp) {
		this.numOfDh = (duty.getNumOfLegsPassive() + dp.numOfCoverings);
		this.dhDurationInMins = (duty.getBlockTimeInMinsPassive() + dp.blockTimeOfCoverings);
		this.activeBlocktimeInMins = (duty.getBlockTimeInMinsActive() - dp.blockTimeOfCoverings);
		this.numOfDuties = 1;
		this.numOfIncludingDutiesOfTheSameLegs = duty.getTotalNumOfIncludingDutiesOfTheSameLegs();
		this.numOfLegs = duty.getNumOfLegs();
		this.numOfAlternativeDutiesWoDh = dp.numOfAlternativeDutiesWoDh;
	}

	public void injectValues(QualityMetric qmToCopy) {
		this.numOfDh = qmToCopy.numOfDh;
		this.dhDurationInMins = qmToCopy.dhDurationInMins;
		this.activeBlocktimeInMins = qmToCopy.activeBlocktimeInMins;
		this.numOfDuties = qmToCopy.numOfDuties;
		this.numOfIncludingDutiesOfTheSameLegs = qmToCopy.numOfIncludingDutiesOfTheSameLegs;
		this.numOfLegs = qmToCopy.numOfLegs;
		this.numOfAlternativeDutiesWoDh = qmToCopy.numOfAlternativeDutiesWoDh;
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
		this.numOfAlternativeDutiesWoDh = 0;
	}

	public void addToQualityMetric(DutyView d,
									DutyParam dp) {
		this.numOfDh += (d.getNumOfLegsPassive() + dp.numOfCoverings);
		this.dhDurationInMins += (d.getBlockTimeInMinsPassive() + dp.blockTimeOfCoverings);
		this.activeBlocktimeInMins += (d.getBlockTimeInMinsActive() - dp.blockTimeOfCoverings);
		this.numOfDuties++;
		this.numOfIncludingDutiesOfTheSameLegs += d.getTotalNumOfIncludingDutiesOfTheSameLegs();
		this.numOfLegs += d.getNumOfLegs();
		this.numOfAlternativeDutiesWoDh += dp.numOfAlternativeDutiesWoDh;
	}

	public void addToQualityMetric(QualityMetric qmToAdd) {
		this.numOfDh += qmToAdd.numOfDh;
		this.dhDurationInMins += qmToAdd.dhDurationInMins;
		this.activeBlocktimeInMins += qmToAdd.activeBlocktimeInMins;
		this.numOfDuties += qmToAdd.numOfDuties;
		this.numOfIncludingDutiesOfTheSameLegs += qmToAdd.numOfIncludingDutiesOfTheSameLegs;
		this.numOfLegs += qmToAdd.numOfLegs;
		this.numOfAlternativeDutiesWoDh += qmToAdd.numOfAlternativeDutiesWoDh;
	}

	public void removeFromQualityMetric(DutyView d,
										DutyParam dp) {
		this.numOfDh -= (d.getNumOfLegsPassive() + dp.numOfCoverings);
		this.dhDurationInMins -= (d.getBlockTimeInMinsPassive() + dp.blockTimeOfCoverings);
		this.activeBlocktimeInMins -= (d.getBlockTimeInMinsActive() - dp.blockTimeOfCoverings);
		this.numOfDuties--;
		this.numOfIncludingDutiesOfTheSameLegs -= d.getTotalNumOfIncludingDutiesOfTheSameLegs();
		this.numOfLegs -= d.getNumOfLegs();
		this.numOfAlternativeDutiesWoDh -= dp.numOfAlternativeDutiesWoDh;
	}

	public void removeFromQualityMetric(QualityMetric qmToRemove) {
		this.numOfDh -= qmToRemove.numOfDh;
		this.dhDurationInMins -= qmToRemove.dhDurationInMins;
		this.activeBlocktimeInMins -= qmToRemove.activeBlocktimeInMins;
		this.numOfDuties -= qmToRemove.numOfDuties;
		this.numOfIncludingDutiesOfTheSameLegs -= qmToRemove.numOfIncludingDutiesOfTheSameLegs;
		this.numOfLegs -= qmToRemove.numOfLegs;
		this.numOfAlternativeDutiesWoDh -= qmToRemove.numOfAlternativeDutiesWoDh;
	}

//	public boolean isBetterThan(int heuristicNo, QualityMetric qm) {
//		if ((qm.numOfLegs == 0)
//				|| (this.priority < qm.priority)
//				|| ((this.priority == qm.priority) && (this.numOfDh < qm.numOfDh))
//				|| ((this.priority == qm.priority) && (this.numOfDh == qm.numOfDh) && (((1.0 * this.activeBlocktimeInMins) / this.numOfDuties) > ((1.0 * qm.activeBlocktimeInMins) / qm.numOfDuties)))) {
//			return true;
//		} else
//			return false;
//	}
//
//	public boolean doesItWorthToGoDeeper(int maxDutyBlockTimeInMins,
//											int heuristicNo,
//											int currentDept,
//											QualityMetric bestQualSoFar) {
//			if ((bestQualSoFar == null)
//					|| (bestQualSoFar.priority > this.priority)) {
//				return true;
//			} else
//				return false;
//	}

	public boolean isBetterThan(int heuristicNo, QualityMetric qm) {
		if (heuristicNo < 2) {	//	If layover or dh effective. 
			if ((qm.numOfLegs == 0)
					|| (this.numOfDh < qm.numOfDh)
					|| ((this.numOfDh == qm.numOfDh) && (this.dhDurationInMins < qm.dhDurationInMins))
					|| ((this.numOfDh == qm.numOfDh) && (this.dhDurationInMins == qm.dhDurationInMins) 
//							&& (((1.0 * this.numOfIncludingDutiesOfTheSameLegs) / this.numOfLegs) < ((1.0 * qm.numOfIncludingDutiesOfTheSameLegs) / qm.numOfLegs)))) {
//							&& (((1.0 * this.activeBlocktimeInMins) / this.numOfDuties) > ((1.0 * qm.activeBlocktimeInMins) / qm.numOfDuties)))) {
							&& (this.numOfAlternativeDutiesWoDh < qm.numOfAlternativeDutiesWoDh))) {
				return true;
			} else
				return false;
		} else {	//	If active block time effective.
			if ((qm.numOfLegs == 0)
					|| (((1.0 * this.activeBlocktimeInMins) / this.numOfDuties) > ((1.0 * qm.activeBlocktimeInMins) / qm.numOfDuties))
					|| ((((1.0 * this.activeBlocktimeInMins) / this.numOfDuties) == ((1.0 * qm.activeBlocktimeInMins) / qm.numOfDuties))
//							&& (((1.0 * this.numOfIncludingDutiesOfTheSameLegs) / this.numOfLegs) < ((1.0 * qm.numOfIncludingDutiesOfTheSameLegs) / qm.numOfLegs)))) {
//							&& (this.numOfDh < qm.numOfDh))) {
							&& (this.numOfAlternativeDutiesWoDh < qm.numOfAlternativeDutiesWoDh))) {
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

	public boolean isNotEmpty() {
		return ((numOfDh > 0)
			|| (dhDurationInMins > 0)
			|| (activeBlocktimeInMins > 0)
			|| (numOfDuties > 0)
			|| (numOfIncludingDutiesOfTheSameLegs > 0)
			|| (numOfLegs > 0)
			|| (numOfAlternativeDutiesWoDh > 0));
	}

	public String toString() {
		return "#DH:" + numOfDh + "/" + dhDurationInMins +
				", ABT:" + activeBlocktimeInMins + "/" + numOfDuties + "=" + (Math.floor((100.0 * activeBlocktimeInMins) / numOfDuties) / 100.0) +
				", #LD:" + numOfIncludingDutiesOfTheSameLegs + "/" + numOfLegs + "=" + (Math.floor((100.0 * numOfIncludingDutiesOfTheSameLegs) / numOfLegs) / 100.0);
	}
}
