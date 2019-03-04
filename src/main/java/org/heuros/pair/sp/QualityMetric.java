package org.heuros.pair.sp;

import org.heuros.data.model.DutyView;
import org.heuros.pair.heuro.state.DutyState;

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
//	private int[] histMinNumOfAlternativeDuties = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
//	private int[] histMinNumOfAlternativeDutiesWoDh = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
//	private int minNumOfAlternativeDuties = Integer.MAX_VALUE;
//	private int minNumOfAlternativeDutiesWoDh = Integer.MAX_VALUE;
//	private int[] histMaxNumOfAlternativeDuties = {0,0,0,0,0,0,0};
//	private int[] histMaxNumOfAlternativeDutiesWoDh = {0,0,0,0,0,0,0};
//	private int maxNumOfAlternativeDuties = 0;
//	private int maxNumOfAlternativeDutiesWoDh = 0;
//	private int totalNumOfAlternativeDuties = 0;
//	private int totalNumOfAlternativeDutiesWoDh = 0;

	private int numOfLegs = 0;
	private int numOfDhCriticalDuties = 0;

	private double getAvgActiveBlockTimePerDuty() {
		return (1.0 * this.activeBlocktimeInMins) / this.numOfDuties;
	}

	public QualityMetric() {
	}

	public QualityMetric(QualityMetric qmToCopy) {
		this.numOfDh = qmToCopy.numOfDh;
		this.dhDurationInMins = qmToCopy.dhDurationInMins;
		this.activeBlocktimeInMins = qmToCopy.activeBlocktimeInMins;

//		this.histMinNumOfAlternativeDuties = qmToCopy.histMinNumOfAlternativeDuties.clone();
//		this.histMinNumOfAlternativeDutiesWoDh = qmToCopy.histMinNumOfAlternativeDutiesWoDh.clone();
//		this.histMaxNumOfAlternativeDuties = qmToCopy.histMaxNumOfAlternativeDuties.clone();
//		this.histMaxNumOfAlternativeDutiesWoDh = qmToCopy.histMaxNumOfAlternativeDutiesWoDh.clone();
//		this.minNumOfAlternativeDuties = qmToCopy.minNumOfAlternativeDuties;
//		this.minNumOfAlternativeDutiesWoDh = qmToCopy.minNumOfAlternativeDutiesWoDh;
//		this.maxNumOfAlternativeDuties = qmToCopy.maxNumOfAlternativeDuties;
//		this.maxNumOfAlternativeDutiesWoDh = qmToCopy.maxNumOfAlternativeDutiesWoDh;
//		this.totalNumOfAlternativeDuties = qmToCopy.totalNumOfAlternativeDuties;
//		this.totalNumOfAlternativeDutiesWoDh = qmToCopy.totalNumOfAlternativeDutiesWoDh;

		this.numOfDuties = qmToCopy.numOfDuties;
		this.numOfLegs = qmToCopy.numOfLegs;

		this.numOfDhCriticalDuties = qmToCopy.numOfDhCriticalDuties;
	}

	public QualityMetric(DutyView d,
							DutyState dp) {
		this.numOfDh = (d.getNumOfLegsPassive() + dp.numOfCoverings);
		this.dhDurationInMins = (d.getBlockTimeInMinsPassive() + dp.blockTimeOfCoverings);
		this.activeBlocktimeInMins = (d.getBlockTimeInMinsActive() - dp.blockTimeOfCoverings);

////		this.histMinNumOfAlternativeDuties[1] = this.minNumOfAlternativeDuties;
////		this.histMinNumOfAlternativeDutiesWoDh[1] = this.minNumOfAlternativeDutiesWoDh;
//		this.minNumOfAlternativeDuties = d.getMinNumOfAlternativeDuties();
//		this.minNumOfAlternativeDutiesWoDh = dp.minNumOfAlternativeDutiesWoDh;
//		this.maxNumOfAlternativeDuties = d.getMaxNumOfAlternativeDuties();
//		this.maxNumOfAlternativeDutiesWoDh = dp.maxNumOfAlternativeDutiesWoDh;
//		this.totalNumOfAlternativeDuties = d.getTotalNumOfAlternativeDuties();
//		this.totalNumOfAlternativeDutiesWoDh = dp.totalNumOfAlternativeDutiesWoDh;

		this.numOfDuties = 1;
		this.numOfLegs = d.getNumOfLegs();

		if (dp.dhCritical)
			this.numOfDhCriticalDuties++;
	}

	public void injectValues(QualityMetric qmToCopy) {
		this.numOfDh = qmToCopy.numOfDh;
		this.dhDurationInMins = qmToCopy.dhDurationInMins;
		this.activeBlocktimeInMins = qmToCopy.activeBlocktimeInMins;

//		this.histMinNumOfAlternativeDuties = qmToCopy.histMinNumOfAlternativeDuties.clone();
//		this.histMinNumOfAlternativeDutiesWoDh = qmToCopy.histMinNumOfAlternativeDutiesWoDh.clone();
//		this.histMaxNumOfAlternativeDuties = qmToCopy.histMaxNumOfAlternativeDuties.clone();
//		this.histMaxNumOfAlternativeDutiesWoDh = qmToCopy.histMaxNumOfAlternativeDutiesWoDh.clone();
//		this.minNumOfAlternativeDuties = qmToCopy.minNumOfAlternativeDuties;
//		this.minNumOfAlternativeDutiesWoDh = qmToCopy.minNumOfAlternativeDutiesWoDh;
//		this.maxNumOfAlternativeDuties = qmToCopy.maxNumOfAlternativeDuties;
//		this.maxNumOfAlternativeDutiesWoDh = qmToCopy.maxNumOfAlternativeDutiesWoDh;
//		this.totalNumOfAlternativeDuties = qmToCopy.totalNumOfAlternativeDuties;
//		this.totalNumOfAlternativeDutiesWoDh = qmToCopy.totalNumOfAlternativeDutiesWoDh;

		this.numOfDuties = qmToCopy.numOfDuties;
		this.numOfLegs = qmToCopy.numOfLegs;

		this.numOfDhCriticalDuties = qmToCopy.numOfDhCriticalDuties;
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

////		this.histMinNumOfAlternativeDuties = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
////		this.histMinNumOfAlternativeDutiesWoDh = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
//		this.minNumOfAlternativeDuties = Integer.MAX_VALUE;
//		this.minNumOfAlternativeDutiesWoDh = Integer.MAX_VALUE;
//		this.maxNumOfAlternativeDuties = 0;
//		this.maxNumOfAlternativeDutiesWoDh = 0;
//		this.totalNumOfAlternativeDuties = 0;
//		this.totalNumOfAlternativeDutiesWoDh = 0;

		this.numOfDuties = 0;
		this.numOfLegs = 0;

		this.numOfDhCriticalDuties = 0;
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
									DutyState dp) {
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

//		this.histMinNumOfAlternativeDuties[this.numOfDuties] = this.minNumOfAlternativeDuties;
//		this.histMinNumOfAlternativeDutiesWoDh[this.numOfDuties] = this.minNumOfAlternativeDutiesWoDh;
//		this.histMaxNumOfAlternativeDuties[this.numOfDuties] = this.maxNumOfAlternativeDuties;
//		this.histMaxNumOfAlternativeDutiesWoDh[this.numOfDuties] = this.maxNumOfAlternativeDutiesWoDh;
//		if (this.minNumOfAlternativeDuties > d.getMinNumOfAlternativeDuties())
//			this.minNumOfAlternativeDuties = d.getMinNumOfAlternativeDuties();
//		if (this.minNumOfAlternativeDutiesWoDh > dp.minNumOfAlternativeDutiesWoDh)
//			this.minNumOfAlternativeDutiesWoDh = dp.minNumOfAlternativeDutiesWoDh;
//		if (this.maxNumOfAlternativeDuties < d.getMaxNumOfAlternativeDuties())
//			this.maxNumOfAlternativeDuties = d.getMaxNumOfAlternativeDuties();
//		if (this.maxNumOfAlternativeDutiesWoDh < dp.maxNumOfAlternativeDutiesWoDh)
//			this.maxNumOfAlternativeDutiesWoDh = dp.maxNumOfAlternativeDutiesWoDh;
//		this.totalNumOfAlternativeDuties += d.getTotalNumOfAlternativeDuties();
//		this.totalNumOfAlternativeDutiesWoDh += dp.totalNumOfAlternativeDutiesWoDh;

		this.numOfDuties++;
		this.numOfLegs += d.getNumOfLegs();

		if (dp.dhCritical)
			this.numOfDhCriticalDuties++;
	}


	public void addToQualityMetricBw(DutyView d,
										DutyState dp) {
		this.numOfDh += (d.getNumOfLegsPassive() + dp.numOfCoverings);
		this.dhDurationInMins += (d.getBlockTimeInMinsPassive() + dp.blockTimeOfCoverings);
		this.activeBlocktimeInMins += (d.getBlockTimeInMinsActive() - dp.blockTimeOfCoverings);

//		this.histMinNumOfAlternativeDuties[this.numOfDuties] = this.minNumOfAlternativeDuties;
//		this.histMinNumOfAlternativeDutiesWoDh[this.numOfDuties] = this.minNumOfAlternativeDutiesWoDh;
//		this.histMaxNumOfAlternativeDuties[this.numOfDuties] = this.maxNumOfAlternativeDuties;
//		this.histMaxNumOfAlternativeDutiesWoDh[this.numOfDuties] = this.maxNumOfAlternativeDutiesWoDh;
//		if (this.minNumOfAlternativeDuties > d.getMinNumOfAlternativeDuties())
//			this.minNumOfAlternativeDuties = d.getMinNumOfAlternativeDuties();
//		if (this.minNumOfAlternativeDutiesWoDh > dp.minNumOfAlternativeDutiesWoDh)
//			this.minNumOfAlternativeDutiesWoDh = dp.minNumOfAlternativeDutiesWoDh;
//		if (this.maxNumOfAlternativeDuties < d.getMaxNumOfAlternativeDuties())
//			this.maxNumOfAlternativeDuties = d.getMaxNumOfAlternativeDuties();
//		if (this.maxNumOfAlternativeDutiesWoDh < dp.maxNumOfAlternativeDutiesWoDh)
//			this.maxNumOfAlternativeDutiesWoDh = dp.maxNumOfAlternativeDutiesWoDh;
//		this.totalNumOfAlternativeDuties += d.getTotalNumOfAlternativeDuties();
//		this.totalNumOfAlternativeDutiesWoDh += dp.totalNumOfAlternativeDutiesWoDh;

		this.numOfDuties++;
		this.numOfLegs += d.getNumOfLegs();

		if (dp.dhCritical)
			this.numOfDhCriticalDuties++;
	}

	public void addLeadingDutyQualityMetric(QualityMetric leadingDutyQm) {
		this.numOfDh += leadingDutyQm.numOfDh;
		this.dhDurationInMins += leadingDutyQm.dhDurationInMins;
		this.activeBlocktimeInMins += leadingDutyQm.activeBlocktimeInMins;

//		this.histMinNumOfAlternativeDuties[this.numOfDuties] = this.minNumOfAlternativeDuties;
//		this.histMinNumOfAlternativeDutiesWoDh[this.numOfDuties] = this.minNumOfAlternativeDutiesWoDh;
//		this.histMaxNumOfAlternativeDuties[this.numOfDuties] = this.maxNumOfAlternativeDuties;
//		this.histMaxNumOfAlternativeDutiesWoDh[this.numOfDuties] = this.maxNumOfAlternativeDutiesWoDh;
//		if (this.minNumOfAlternativeDuties > leadingDutyQm.minNumOfAlternativeDuties)
//			this.minNumOfAlternativeDuties = leadingDutyQm.minNumOfAlternativeDuties;
//		if (this.minNumOfAlternativeDutiesWoDh > leadingDutyQm.minNumOfAlternativeDutiesWoDh)
//			this.minNumOfAlternativeDutiesWoDh = leadingDutyQm.minNumOfAlternativeDutiesWoDh;
//		if (this.maxNumOfAlternativeDuties < leadingDutyQm.maxNumOfAlternativeDuties)
//			this.maxNumOfAlternativeDuties = leadingDutyQm.maxNumOfAlternativeDuties;
//		if (this.maxNumOfAlternativeDutiesWoDh < leadingDutyQm.maxNumOfAlternativeDutiesWoDh)
//			this.maxNumOfAlternativeDutiesWoDh = leadingDutyQm.maxNumOfAlternativeDutiesWoDh;
//		this.totalNumOfAlternativeDuties += leadingDutyQm.totalNumOfAlternativeDuties;
//		this.totalNumOfAlternativeDutiesWoDh += leadingDutyQm.totalNumOfAlternativeDutiesWoDh;
		/*
		 * TODO Can we use ++ here ?
		 */
		this.numOfDuties++;	//	+= qmToAdd.numOfDuties;
		this.numOfLegs += leadingDutyQm.numOfLegs;

		this.numOfDhCriticalDuties += leadingDutyQm.numOfDhCriticalDuties;
	}

	public void removeLastDutyQualityMetric(DutyView d,
										DutyState dp) {
		this.numOfDh -= (d.getNumOfLegsPassive() + dp.numOfCoverings);
		this.dhDurationInMins -= (d.getBlockTimeInMinsPassive() + dp.blockTimeOfCoverings);
		this.activeBlocktimeInMins -= (d.getBlockTimeInMinsActive() - dp.blockTimeOfCoverings);
		this.numOfDuties--;
		this.numOfLegs -= d.getNumOfLegs();

		if (dp.dhCritical)
			this.numOfDhCriticalDuties--;

//		this.minNumOfAlternativeDuties = this.histMinNumOfAlternativeDuties[this.numOfDuties];
//		this.minNumOfAlternativeDutiesWoDh = this.histMinNumOfAlternativeDutiesWoDh[this.numOfDuties];
//		this.maxNumOfAlternativeDuties = this.histMaxNumOfAlternativeDuties[this.numOfDuties];
//		this.maxNumOfAlternativeDutiesWoDh = this.histMaxNumOfAlternativeDutiesWoDh[this.numOfDuties];
//		this.totalNumOfAlternativeDuties -= d.getTotalNumOfAlternativeDuties();
//		this.totalNumOfAlternativeDutiesWoDh -= dp.totalNumOfAlternativeDutiesWoDh;
	}

	public void removeLeadingDutyQualityMetric(QualityMetric leadingDutyQm) {
		this.numOfDh -= leadingDutyQm.numOfDh;
		this.dhDurationInMins -= leadingDutyQm.dhDurationInMins;
		this.activeBlocktimeInMins -= leadingDutyQm.activeBlocktimeInMins;
		/*
		 * TODO Can we use -- here ?
		 */
		this.numOfDuties--;	//	-= qmToRemove.numOfDuties;
		this.numOfLegs -= leadingDutyQm.numOfLegs;

		this.numOfDhCriticalDuties -= leadingDutyQm.numOfDhCriticalDuties;

//		this.minNumOfAlternativeDuties = this.histMinNumOfAlternativeDuties[this.numOfDuties];
//		this.minNumOfAlternativeDutiesWoDh = this.histMinNumOfAlternativeDutiesWoDh[this.numOfDuties];
//		this.maxNumOfAlternativeDuties = this.histMaxNumOfAlternativeDuties[this.numOfDuties];
//		this.maxNumOfAlternativeDutiesWoDh = this.histMaxNumOfAlternativeDutiesWoDh[this.numOfDuties];
//		this.totalNumOfAlternativeDuties -= leadingDutyQm.totalNumOfAlternativeDuties;
//		this.totalNumOfAlternativeDutiesWoDh -= leadingDutyQm.totalNumOfAlternativeDutiesWoDh;
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
		if ((qm.numOfLegs == 0)
				|| (this.numOfDh < qm.numOfDh)

//				|| ((this.numOfDh == qm.numOfDh) && (this.numOfDuties < qm.numOfDuties))
//				|| ((this.numOfDh == qm.numOfDh) && (this.numOfDuties == qm.numOfDuties) && (this.getAvgActiveBlockTimePerDuty() > qm.getAvgActiveBlockTimePerDuty()))

				|| ((this.numOfDh == qm.numOfDh) && (this.numOfDhCriticalDuties < qm.numOfDhCriticalDuties))
				|| ((this.numOfDh == qm.numOfDh) && (this.numOfDhCriticalDuties == qm.numOfDhCriticalDuties) && (this.numOfDuties < qm.numOfDuties))
				|| ((this.numOfDh == qm.numOfDh) && (this.numOfDhCriticalDuties == qm.numOfDhCriticalDuties) && (this.numOfDuties == qm.numOfDuties) && (this.getAvgActiveBlockTimePerDuty() > qm.getAvgActiveBlockTimePerDuty()))

//				&& (this.totalNumOfAlternativeDutiesWoDh < qm.totalNumOfAlternativeDutiesWoDh)
		) {
			return true;
		} else
			return false;
	}

	public boolean doesItWorthToGoDeeper(int maxDutyBlockTimeInMins,
											int heuristicNo,
											int currentDept,
											QualityMetric bestQualSoFar) {
		if (heuristicNo < 2) {	//	If layover or dh effective.
			if ((bestQualSoFar == null)
					|| (bestQualSoFar.numOfDh > this.numOfDh)

//					|| ((bestQualSoFar.numOfDh == this.numOfDh) && (bestQualSoFar.numOfDuties > this.numOfDuties))
//					|| ((bestQualSoFar.numOfDh == this.numOfDh) && (bestQualSoFar.numOfDuties == this.numOfDuties) && (bestQualSoFar.getAvgActiveBlockTimePerDuty() < this.getAvgActiveBlockTimePerDuty()))

					|| ((bestQualSoFar.numOfDh == this.numOfDh) && (bestQualSoFar.numOfDhCriticalDuties > this.numOfDhCriticalDuties))
					|| ((bestQualSoFar.numOfDh == this.numOfDh) && (bestQualSoFar.numOfDhCriticalDuties == this.numOfDhCriticalDuties) && (bestQualSoFar.numOfDuties > this.numOfDuties))
					|| ((bestQualSoFar.numOfDh == this.numOfDh) && (bestQualSoFar.numOfDhCriticalDuties == this.numOfDhCriticalDuties) && (bestQualSoFar.numOfDuties == this.numOfDuties) && (bestQualSoFar.getAvgActiveBlockTimePerDuty() < this.getAvgActiveBlockTimePerDuty()))

//					&& (bestQualSoFar.totalNumOfAlternativeDutiesWoDh > this.totalNumOfAlternativeDutiesWoDh)
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
//			|| (minNumOfAlternativeDuties < Integer.MAX_VALUE)
//			|| (minNumOfAlternativeDutiesWoDh < Integer.MAX_VALUE)
//			|| (maxNumOfAlternativeDuties > 0)
//			|| (maxNumOfAlternativeDutiesWoDh > 0)
//			|| (totalNumOfAlternativeDuties < 0)
//			|| (totalNumOfAlternativeDutiesWoDh > 0)
			|| (numOfLegs > 0)
			|| (numOfDhCriticalDuties > 0));
	}

	public boolean isTheSame(QualityMetric qmToCompare) {
		return (this.numOfDh == qmToCompare.numOfDh)
				&& (this.dhDurationInMins == qmToCompare.dhDurationInMins)
				&& (this.activeBlocktimeInMins == qmToCompare.activeBlocktimeInMins)
				&& (this.numOfDuties == qmToCompare.numOfDuties)
//				&& (this.minNumOfAlternativeDuties == qmToCompare.minNumOfAlternativeDuties)
//				&& (this.minNumOfAlternativeDutiesWoDh == qmToCompare.minNumOfAlternativeDutiesWoDh)
//				&& (this.maxNumOfAlternativeDuties == qmToCompare.maxNumOfAlternativeDuties)
//				&& (this.maxNumOfAlternativeDutiesWoDh == qmToCompare.maxNumOfAlternativeDutiesWoDh)
//				&& (this.totalNumOfAlternativeDuties == qmToCompare.totalNumOfAlternativeDuties)
//				&& (this.totalNumOfAlternativeDutiesWoDh == qmToCompare.totalNumOfAlternativeDutiesWoDh)
				&& (this.numOfLegs == qmToCompare.numOfLegs)
				&& (this.numOfDhCriticalDuties == qmToCompare.numOfDhCriticalDuties);
	}

	public boolean hasTheSameValues(int minNumOfAlternativeDuties,
									int minNumOfAlternativeDutiesWoDh,
									int maxNumOfAlternativeDuties,
									int maxNumOfAlternativeDutiesWoDh,
									int totalNumOfAlternativeDuties,
									int totalNumOfAlternativeDutiesWoDh) {
		return true;
//				(this.minNumOfAlternativeDuties == minNumOfAlternativeDuties)
//				&& (this.minNumOfAlternativeDutiesWoDh == minNumOfAlternativeDutiesWoDh)
//				&& (this.maxNumOfAlternativeDuties == maxNumOfAlternativeDuties)
//				&& (this.maxNumOfAlternativeDutiesWoDh == maxNumOfAlternativeDutiesWoDh)
//				&& (this.totalNumOfAlternativeDuties == totalNumOfAlternativeDuties)
//				&& (this.totalNumOfAlternativeDutiesWoDh == totalNumOfAlternativeDutiesWoDh);
	}

	public String toString() {
		return "#DH:" + numOfDh + "/" + dhDurationInMins +
				", ABT:" + activeBlocktimeInMins + "/" + numOfDuties + "=" + (Math.floor((100.0 * activeBlocktimeInMins) / numOfDuties) / 100.0) +
//				", #AltMinD:" + minNumOfAlternativeDuties +
//				", #AltMinDwoDH:" + minNumOfAlternativeDutiesWoDh +
//				", #AltMaxD:" + maxNumOfAlternativeDuties +
//				", #AltMaxDwoDH:" + maxNumOfAlternativeDutiesWoDh +
//				", #AltD:" + totalNumOfAlternativeDuties +
//				", #AltDwoDH:" + totalNumOfAlternativeDutiesWoDh +
				", #DhCriticalDs:" + numOfDhCriticalDuties;
	}
}
