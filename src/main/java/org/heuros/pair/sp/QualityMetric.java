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
	private int[] histMinNumOfAlternativeDuties = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
	private int[] histMinNumOfAlternativeDutiesWoDh = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
	private int minNumOfAlternativeDuties = Integer.MAX_VALUE;
	private int minNumOfAlternativeDutiesWoDh = Integer.MAX_VALUE;
	private int totalNumOfAlternativeDuties = 0;
	private int totalNumOfAlternativeDutiesWoDh = 0;
	private int numOfLegs = 0;

	public QualityMetric() {
	}

	public QualityMetric(QualityMetric qmToCopy) {
		this.numOfDh = qmToCopy.numOfDh;
		this.dhDurationInMins = qmToCopy.dhDurationInMins;
		this.activeBlocktimeInMins = qmToCopy.activeBlocktimeInMins;

		this.histMinNumOfAlternativeDuties = qmToCopy.histMinNumOfAlternativeDuties.clone();
		this.histMinNumOfAlternativeDutiesWoDh = qmToCopy.histMinNumOfAlternativeDutiesWoDh.clone();
		this.numOfDuties = qmToCopy.numOfDuties;
		this.minNumOfAlternativeDuties = qmToCopy.minNumOfAlternativeDuties;
		this.minNumOfAlternativeDutiesWoDh = qmToCopy.minNumOfAlternativeDutiesWoDh;

		this.totalNumOfAlternativeDuties = qmToCopy.totalNumOfAlternativeDuties;
		this.totalNumOfAlternativeDutiesWoDh = qmToCopy.totalNumOfAlternativeDutiesWoDh;
		this.numOfLegs = qmToCopy.numOfLegs;
	}

	public QualityMetric(DutyView duty,
							DutyParam dp) {
		this.numOfDh = (duty.getNumOfLegsPassive() + dp.numOfCoverings);
		this.dhDurationInMins = (duty.getBlockTimeInMinsPassive() + dp.blockTimeOfCoverings);
		this.activeBlocktimeInMins = (duty.getBlockTimeInMinsActive() - dp.blockTimeOfCoverings);

//		this.histMinNumOfAlternativeDuties[1] = this.minNumOfAlternativeDuties;
//		this.histMinNumOfAlternativeDutiesWoDh[1] = this.minNumOfAlternativeDutiesWoDh;
		this.numOfDuties = 1;
		this.minNumOfAlternativeDuties = duty.getMinNumOfAlternativeDuties();
		this.minNumOfAlternativeDutiesWoDh = dp.minNumOfAlternativeDutiesWoDh;

		this.totalNumOfAlternativeDuties = duty.getTotalNumOfAlternativeDuties();
		this.totalNumOfAlternativeDutiesWoDh = dp.totalNumOfAlternativeDutiesWoDh;
		this.numOfLegs = duty.getNumOfLegs();
	}

	public void injectValues(QualityMetric qmToCopy) {
		this.numOfDh = qmToCopy.numOfDh;
		this.dhDurationInMins = qmToCopy.dhDurationInMins;
		this.activeBlocktimeInMins = qmToCopy.activeBlocktimeInMins;

		this.histMinNumOfAlternativeDuties = qmToCopy.histMinNumOfAlternativeDuties.clone();
		this.histMinNumOfAlternativeDutiesWoDh = qmToCopy.histMinNumOfAlternativeDutiesWoDh.clone();
		this.numOfDuties = qmToCopy.numOfDuties;
		this.minNumOfAlternativeDuties = qmToCopy.minNumOfAlternativeDuties;
		this.minNumOfAlternativeDutiesWoDh = qmToCopy.minNumOfAlternativeDutiesWoDh;

		this.totalNumOfAlternativeDuties = qmToCopy.totalNumOfAlternativeDuties;
		this.totalNumOfAlternativeDutiesWoDh = qmToCopy.totalNumOfAlternativeDutiesWoDh;
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
//		this.histMinNumOfAlternativeDuties = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
//		this.histMinNumOfAlternativeDutiesWoDh = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
		this.numOfDuties = 0;
		this.minNumOfAlternativeDuties = Integer.MAX_VALUE;
		this.minNumOfAlternativeDutiesWoDh = Integer.MAX_VALUE;
		this.totalNumOfAlternativeDuties = 0;
		this.totalNumOfAlternativeDutiesWoDh = 0;
		this.numOfLegs = 0;
	}

	/**
	 * Since our shortest path algorithm searches from right to left,
	 * this method must be used only comparison, performance improvement purposes etc.
	 * It can not be used for building nodes to be registered!
	 * 
	 * @param d
	 * @param dp
	 */
	public void addToQualityMetricFw(DutyView d,
									DutyParam dp) {
		/*
		 * TODO
		 * 
		 * Since our shortest path algorithm searches from right to left,
		 * this method must be used only comparison, performance improvement purposes etc.
		 * It can not be used for building nodes to be registered!
		 * 
		 */
		this.numOfDh += (d.getNumOfLegsPassive() + dp.numOfCoverings);
		this.dhDurationInMins += (d.getBlockTimeInMinsPassive() + dp.blockTimeOfCoverings);
		this.activeBlocktimeInMins += (d.getBlockTimeInMinsActive() - dp.blockTimeOfCoverings);

		this.histMinNumOfAlternativeDuties[this.numOfDuties] = this.minNumOfAlternativeDuties;
		this.histMinNumOfAlternativeDutiesWoDh[this.numOfDuties] = this.minNumOfAlternativeDutiesWoDh;
		this.numOfDuties++;
		if (this.minNumOfAlternativeDuties > d.getMinNumOfAlternativeDuties())
			this.minNumOfAlternativeDuties = d.getMinNumOfAlternativeDuties();
		if (this.minNumOfAlternativeDutiesWoDh > dp.minNumOfAlternativeDutiesWoDh)
			this.minNumOfAlternativeDutiesWoDh = dp.minNumOfAlternativeDutiesWoDh;
		this.totalNumOfAlternativeDuties += d.getTotalNumOfAlternativeDuties();
		this.totalNumOfAlternativeDutiesWoDh += dp.totalNumOfAlternativeDutiesWoDh;
		this.numOfLegs += d.getNumOfLegs();
	}


	public void addToQualityMetricBw(DutyView d,
									DutyParam dp) {
		this.numOfDh += (d.getNumOfLegsPassive() + dp.numOfCoverings);
		this.dhDurationInMins += (d.getBlockTimeInMinsPassive() + dp.blockTimeOfCoverings);
		this.activeBlocktimeInMins += (d.getBlockTimeInMinsActive() - dp.blockTimeOfCoverings);

		this.histMinNumOfAlternativeDuties[this.numOfDuties] = this.minNumOfAlternativeDuties;
		this.histMinNumOfAlternativeDutiesWoDh[this.numOfDuties] = this.minNumOfAlternativeDutiesWoDh;
		this.numOfDuties++;
		if (this.minNumOfAlternativeDuties > d.getMinNumOfAlternativeDuties())
			this.minNumOfAlternativeDuties = d.getMinNumOfAlternativeDuties();
		if (this.minNumOfAlternativeDutiesWoDh > dp.minNumOfAlternativeDutiesWoDh)
			this.minNumOfAlternativeDutiesWoDh = dp.minNumOfAlternativeDutiesWoDh;
		this.totalNumOfAlternativeDuties += d.getTotalNumOfAlternativeDuties();
		this.totalNumOfAlternativeDutiesWoDh += dp.totalNumOfAlternativeDutiesWoDh;
		this.numOfLegs += d.getNumOfLegs();
	}

	public void addLeadingDutyQualityMetric(QualityMetric leadingDutyQm) {
		this.numOfDh += leadingDutyQm.numOfDh;
		this.dhDurationInMins += leadingDutyQm.dhDurationInMins;
		this.activeBlocktimeInMins += leadingDutyQm.activeBlocktimeInMins;

		this.histMinNumOfAlternativeDuties[this.numOfDuties] = this.minNumOfAlternativeDuties;
		this.histMinNumOfAlternativeDutiesWoDh[this.numOfDuties] = this.minNumOfAlternativeDutiesWoDh;
		/*
		 * TODO Can we use ++ here ?
		 */
		this.numOfDuties++;	//	+= qmToAdd.numOfDuties;
		if (this.minNumOfAlternativeDuties > leadingDutyQm.minNumOfAlternativeDuties)
			this.minNumOfAlternativeDuties = leadingDutyQm.minNumOfAlternativeDuties;
		if (this.minNumOfAlternativeDutiesWoDh > leadingDutyQm.minNumOfAlternativeDutiesWoDh)
			this.minNumOfAlternativeDutiesWoDh = leadingDutyQm.minNumOfAlternativeDutiesWoDh;
		this.totalNumOfAlternativeDuties += leadingDutyQm.totalNumOfAlternativeDuties;
		this.totalNumOfAlternativeDutiesWoDh += leadingDutyQm.totalNumOfAlternativeDutiesWoDh;
		this.numOfLegs += leadingDutyQm.numOfLegs;
	}

	public void removeLastDutyQualityMetric(DutyView d,
										DutyParam dp) {
		this.numOfDh -= (d.getNumOfLegsPassive() + dp.numOfCoverings);
		this.dhDurationInMins -= (d.getBlockTimeInMinsPassive() + dp.blockTimeOfCoverings);
		this.activeBlocktimeInMins -= (d.getBlockTimeInMinsActive() - dp.blockTimeOfCoverings);
		this.numOfDuties--;
		this.minNumOfAlternativeDuties = this.histMinNumOfAlternativeDuties[this.numOfDuties];
		this.minNumOfAlternativeDutiesWoDh = this.histMinNumOfAlternativeDutiesWoDh[this.numOfDuties];
		this.totalNumOfAlternativeDuties -= d.getTotalNumOfAlternativeDuties();
		this.totalNumOfAlternativeDutiesWoDh -= dp.totalNumOfAlternativeDutiesWoDh;
		this.numOfLegs -= d.getNumOfLegs();
	}

	public void removeLeadingDutyQualityMetric(QualityMetric leadingDutyQm) {
		this.numOfDh -= leadingDutyQm.numOfDh;
		this.dhDurationInMins -= leadingDutyQm.dhDurationInMins;
		this.activeBlocktimeInMins -= leadingDutyQm.activeBlocktimeInMins;
		/*
		 * TODO Can we use -- here ?
		 */
		this.numOfDuties--;	//	-= qmToRemove.numOfDuties;
		this.minNumOfAlternativeDuties = this.histMinNumOfAlternativeDuties[this.numOfDuties];
		this.minNumOfAlternativeDutiesWoDh = this.histMinNumOfAlternativeDutiesWoDh[this.numOfDuties];
		this.totalNumOfAlternativeDuties -= leadingDutyQm.totalNumOfAlternativeDuties;
		this.totalNumOfAlternativeDutiesWoDh -= leadingDutyQm.totalNumOfAlternativeDutiesWoDh;
		this.numOfLegs -= leadingDutyQm.numOfLegs;
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
//							&& (this.numOfIncludingDutiesOfTheSameLegs < qm.numOfIncludingDutiesOfTheSameLegs))) {
							&& (this.totalNumOfAlternativeDutiesWoDh < qm.totalNumOfAlternativeDutiesWoDh))) {
//							&& ((1.0 * this.numOfAlternativeDutiesWoDh) / this.numOfLegs < (1.0 * qm.numOfAlternativeDutiesWoDh) / qm.numOfLegs))) {
//							&& (this.minNumOfAlternativeDutiesWoDh < qm.minNumOfAlternativeDutiesWoDh))) {
//							&& (((1.0 * this.activeBlocktimeInMins) / this.numOfDuties) > ((1.0 * qm.activeBlocktimeInMins) / qm.numOfDuties)))) {
				return true;
			} else
				return false;
		} else {	//	If active block time effective.
			if ((qm.numOfLegs == 0)
					|| (((1.0 * this.activeBlocktimeInMins) / this.numOfDuties) > ((1.0 * qm.activeBlocktimeInMins) / qm.numOfDuties))
					|| ((((1.0 * this.activeBlocktimeInMins) / this.numOfDuties) == ((1.0 * qm.activeBlocktimeInMins) / qm.numOfDuties))
//							&& (this.numOfDh < qm.numOfDh))) {
//							&& (this.numOfIncludingDutiesOfTheSameLegs < qm.numOfIncludingDutiesOfTheSameLegs))) {
							&& (this.totalNumOfAlternativeDutiesWoDh < qm.totalNumOfAlternativeDutiesWoDh))) {
//							&& ((1.0 * this.numOfAlternativeDutiesWoDh) / this.numOfLegs < (1.0 * qm.numOfAlternativeDutiesWoDh) / qm.numOfLegs))) {
//							&& (this.minNumOfAlternativeDutiesWoDh < qm.minNumOfAlternativeDutiesWoDh))) {
//							&& (((1.0 * this.activeBlocktimeInMins) / this.numOfDuties) > ((1.0 * qm.activeBlocktimeInMins) / qm.numOfDuties)))) {
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
			|| (minNumOfAlternativeDuties < Integer.MAX_VALUE)
			|| (minNumOfAlternativeDutiesWoDh < Integer.MAX_VALUE)
			|| (totalNumOfAlternativeDuties < 0)
			|| (totalNumOfAlternativeDutiesWoDh > 0)
			|| (numOfLegs > 0));
	}

	public boolean isTheSame(QualityMetric qmToCompare) {
		return (this.numOfDh == qmToCompare.numOfDh)
				&& (this.dhDurationInMins == qmToCompare.dhDurationInMins)
				&& (this.activeBlocktimeInMins == qmToCompare.activeBlocktimeInMins)
				&& (this.numOfDuties == qmToCompare.numOfDuties)
				&& (this.minNumOfAlternativeDuties == qmToCompare.minNumOfAlternativeDuties)
				&& (this.minNumOfAlternativeDutiesWoDh == qmToCompare.minNumOfAlternativeDutiesWoDh)
				&& (this.totalNumOfAlternativeDuties == qmToCompare.totalNumOfAlternativeDuties)
				&& (this.totalNumOfAlternativeDutiesWoDh == qmToCompare.totalNumOfAlternativeDutiesWoDh)
				&& (this.numOfLegs == qmToCompare.numOfLegs);
	}

	public boolean hasTheSameValues(int minNumOfAlternativeDuties,
									int minNumOfAlternativeDutiesWoDh,
									int totalNumOfAlternativeDuties,
									int totalNumOfAlternativeDutiesWoDh) {
		return (this.minNumOfAlternativeDuties == minNumOfAlternativeDuties)
				&& (this.minNumOfAlternativeDutiesWoDh == minNumOfAlternativeDutiesWoDh)
				&& (this.totalNumOfAlternativeDuties == totalNumOfAlternativeDuties)
				&& (this.totalNumOfAlternativeDutiesWoDh == totalNumOfAlternativeDutiesWoDh);
	}

	public String toString() {
		return "#DH:" + numOfDh + "/" + dhDurationInMins +
				", ABT:" + activeBlocktimeInMins + "/" + numOfDuties + "=" + (Math.floor((100.0 * activeBlocktimeInMins) / numOfDuties) / 100.0) +
				", #AltMinD:" + minNumOfAlternativeDuties +
				", #AltMinDwoDH:" + minNumOfAlternativeDutiesWoDh +
				", #AltD:" + totalNumOfAlternativeDuties +
				", #AltDwoDH:" + totalNumOfAlternativeDutiesWoDh;
	}
}
