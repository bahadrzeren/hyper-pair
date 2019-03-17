package org.heuros.pair.sp;

import org.heuros.data.model.Duty;
import org.heuros.pair.conf.HeurosSystemParam;
import org.heuros.pair.heuro.state.DutyState;

public class QualityMetric {
	/*
	 * TODO Single base assumption!!!
	 */
	private int hbNdx = 0;
	/*
	 * Deadhead
	 */
	private int numOfDh = 0;
	private int dhDurationInMins = 0;
	/*
	 * Dutyday
	 */
	private int activeBlocktimeInMins = 0;
	private int dutyTimeInMins = 0;
	private int numOfDuties = 0;
	private int numOfLegs = 0;
//	/*
//	 * The last metric to check if others are equal!
//	 */
//	private int[] histMinNumOfAlternativeDuties = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
//	private int[] histMinNumOfAlternativeDutiesWoDh = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
//	private int minNumOfAlternativeDuties = Integer.MAX_VALUE;
//	private int minNumOfAlternativeDutiesWoDh = Integer.MAX_VALUE;
////	private int[] histMaxNumOfAlternativeDuties = {0,0,0,0,0,0,0};
////	private int[] histMaxNumOfAlternativeDutiesWoDh = {0,0,0,0,0,0,0};
////	private int maxNumOfAlternativeDuties = 0;
////	private int maxNumOfAlternativeDutiesWoDh = 0;
//	private int totalNumOfAlternativeDuties = 0;
//	private int totalNumOfAlternativeDutiesWoDh = 0;
//
//	private int[] histMinNumOfAlternativeEffectiveDuties = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
//	private int[] histMinNumOfAlternativeEffectiveDutiesWoDh = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
//	private int minNumOfAlternativeEffectiveDuties = Integer.MAX_VALUE;
//	private int minNumOfAlternativeEffectiveDutiesWoDh = Integer.MAX_VALUE;
////	private int[] histMaxNumOfAlternativeEffectiveDuties = {0,0,0,0,0,0,0};
////	private int[] histMaxNumOfAlternativeEffectiveDutiesWoDh = {0,0,0,0,0,0,0};
////	private int maxNumOfAlternativeEffectiveDuties = 0;
////	private int maxNumOfAlternativeEffectiveDutiesWoDh = 0;
//	private int totalNumOfAlternativeEffectiveDuties = 0;
//	private int totalNumOfAlternativeEffectiveDutiesWoDh = 0;
//
//	private double totalDifficultyScore = 0.0;

//	private double getAvgNumOfAlternativeDutiesWoDhPerDuty() {
//		return (1.0 * this.totalNumOfAlternativeDutiesWoDh) / this.numOfDuties;
//	}

//	private double getAvgDifficultyScorePerDuty() {
//		return this.totalDifficultyScore / this.numOfDuties;
//	}

	private double getAvgActiveBlockTimePerDuty() {
		return (1.0 * this.activeBlocktimeInMins) / this.numOfDuties;
	}

	private double getActiveBlockTimeDutyTimeRate() {
		return (1.0 * this.activeBlocktimeInMins) / this.dutyTimeInMins;
	}

//	private double getAvgAlternativeDutiesWoDhPerDuty() {
//		return (1.0 * this.totalNumOfAlternativeDutiesWoDh) / this.numOfDuties;
//	}
//
//	private double getAvgAlternativeEffectiveDutiesPerDuty() {
//		return (1.0 * this.totalNumOfAlternativeEffectiveDuties) / this.numOfDuties;
//	}
//
//	private double getAvgAlternativeEffectiveDutiesWoDhPerDuty() {
//		return (1.0 * this.totalNumOfAlternativeEffectiveDutiesWoDh) / this.numOfDuties;
//	}

	public QualityMetric() {
	}

	public int getNumOfDuties() {
		return numOfDuties;
	}

	public QualityMetric(QualityMetric qmToCopy) {
		this.numOfDh = qmToCopy.numOfDh;
		this.dhDurationInMins = qmToCopy.dhDurationInMins;
		this.activeBlocktimeInMins = qmToCopy.activeBlocktimeInMins;
		this.dutyTimeInMins = qmToCopy.dutyTimeInMins;

//		this.histMinNumOfAlternativeDuties = qmToCopy.histMinNumOfAlternativeDuties.clone();
//		this.histMinNumOfAlternativeDutiesWoDh = qmToCopy.histMinNumOfAlternativeDutiesWoDh.clone();
////		this.histMaxNumOfAlternativeDuties = qmToCopy.histMaxNumOfAlternativeDuties.clone();
////		this.histMaxNumOfAlternativeDutiesWoDh = qmToCopy.histMaxNumOfAlternativeDutiesWoDh.clone();
//		this.minNumOfAlternativeDuties = qmToCopy.minNumOfAlternativeDuties;
//		this.minNumOfAlternativeDutiesWoDh = qmToCopy.minNumOfAlternativeDutiesWoDh;
////		this.maxNumOfAlternativeDuties = qmToCopy.maxNumOfAlternativeDuties;
////		this.maxNumOfAlternativeDutiesWoDh = qmToCopy.maxNumOfAlternativeDutiesWoDh;
//		this.totalNumOfAlternativeDuties = qmToCopy.totalNumOfAlternativeDuties;
//		this.totalNumOfAlternativeDutiesWoDh = qmToCopy.totalNumOfAlternativeDutiesWoDh;
//
//		this.histMinNumOfAlternativeEffectiveDuties = qmToCopy.histMinNumOfAlternativeEffectiveDuties.clone();
//		this.histMinNumOfAlternativeEffectiveDutiesWoDh = qmToCopy.histMinNumOfAlternativeEffectiveDutiesWoDh.clone();
////		this.histMaxNumOfAlternativeEffectiveDuties = qmToCopy.histMaxNumOfAlternativeEffectiveDuties.clone();
////		this.histMaxNumOfAlternativeEffectiveDutiesWoDh = qmToCopy.histMaxNumOfAlternativeEffectiveDutiesWoDh.clone();
//		this.minNumOfAlternativeEffectiveDuties = qmToCopy.minNumOfAlternativeEffectiveDuties;
//		this.minNumOfAlternativeEffectiveDutiesWoDh = qmToCopy.minNumOfAlternativeEffectiveDutiesWoDh;
////		this.maxNumOfAlternativeEffectiveDuties = qmToCopy.maxNumOfAlternativeEffectiveDuties;
////		this.maxNumOfAlternativeEffectiveDutiesWoDh = qmToCopy.maxNumOfAlternativeEffectiveDutiesWoDh;
//		this.totalNumOfAlternativeEffectiveDuties = qmToCopy.totalNumOfAlternativeEffectiveDuties;
//		this.totalNumOfAlternativeEffectiveDutiesWoDh = qmToCopy.totalNumOfAlternativeEffectiveDutiesWoDh;

		this.numOfDuties = qmToCopy.numOfDuties;
		this.numOfLegs = qmToCopy.numOfLegs;

//		this.totalDifficultyScore = qmToCopy.totalDifficultyScore;
	}

	public QualityMetric(Duty d,
							DutyState dp) {
		this.numOfDh = (d.getNumOfLegsPassive() + dp.numOfCoverings);
		this.dhDurationInMins = (d.getBlockTimeInMinsPassive() + dp.blockTimeOfCoverings);
		this.activeBlocktimeInMins = (d.getBlockTimeInMinsActive() - dp.blockTimeOfCoveringsActive);
		this.dutyTimeInMins = d.getDutyDurationInMins(hbNdx);

//		this.minNumOfAlternativeDuties = d.getMinNumOfAlternativeDuties();
//		this.minNumOfAlternativeDutiesWoDh = dp.minNumOfAlternativeDutiesWoDh;
////		this.maxNumOfAlternativeDuties = d.getMaxNumOfAlternativeDuties();
////		this.maxNumOfAlternativeDutiesWoDh = dp.maxNumOfAlternativeDutiesWoDh;
//		this.totalNumOfAlternativeDuties = d.getTotalNumOfAlternativeDuties();
//		this.totalNumOfAlternativeDutiesWoDh = dp.totalNumOfAlternativeDutiesWoDh;
//
//		this.minNumOfAlternativeEffectiveDuties = dp.minNumOfAlternativeEffectiveDuties;
//		this.minNumOfAlternativeEffectiveDutiesWoDh = dp.minNumOfAlternativeEffectiveDutiesWoDh;
////		this.maxNumOfAlternativeEffectiveDuties = dp.maxNumOfAlternativeEffectiveDuties;
////		this.maxNumOfAlternativeEffectiveDutiesWoDh = dp.maxNumOfAlternativeEffectiveDutiesWoDh;
//		this.totalNumOfAlternativeEffectiveDuties = dp.totalNumOfAlternativeEffectiveDuties;
//		this.totalNumOfAlternativeEffectiveDutiesWoDh = dp.totalNumOfAlternativeEffectiveDutiesWoDh;

		this.numOfDuties = 1;
		this.numOfLegs = d.getNumOfLegs();

//		this.totalDifficultyScore = dp.getDifficultyScore();
	}

	public void injectValues(QualityMetric qmToCopy) {
		this.numOfDh = qmToCopy.numOfDh;
		this.dhDurationInMins = qmToCopy.dhDurationInMins;
		this.activeBlocktimeInMins = qmToCopy.activeBlocktimeInMins;
		this.dutyTimeInMins = qmToCopy.dutyTimeInMins;

//		this.histMinNumOfAlternativeDuties = qmToCopy.histMinNumOfAlternativeDuties.clone();
//		this.histMinNumOfAlternativeDutiesWoDh = qmToCopy.histMinNumOfAlternativeDutiesWoDh.clone();
////		this.histMaxNumOfAlternativeDuties = qmToCopy.histMaxNumOfAlternativeDuties.clone();
////		this.histMaxNumOfAlternativeDutiesWoDh = qmToCopy.histMaxNumOfAlternativeDutiesWoDh.clone();
//		this.minNumOfAlternativeDuties = qmToCopy.minNumOfAlternativeDuties;
//		this.minNumOfAlternativeDutiesWoDh = qmToCopy.minNumOfAlternativeDutiesWoDh;
////		this.maxNumOfAlternativeDuties = qmToCopy.maxNumOfAlternativeDuties;
////		this.maxNumOfAlternativeDutiesWoDh = qmToCopy.maxNumOfAlternativeDutiesWoDh;
//		this.totalNumOfAlternativeDuties = qmToCopy.totalNumOfAlternativeDuties;
//		this.totalNumOfAlternativeDutiesWoDh = qmToCopy.totalNumOfAlternativeDutiesWoDh;
//
//		this.histMinNumOfAlternativeEffectiveDuties = qmToCopy.histMinNumOfAlternativeEffectiveDuties.clone();
//		this.histMinNumOfAlternativeEffectiveDutiesWoDh = qmToCopy.histMinNumOfAlternativeEffectiveDutiesWoDh.clone();
////		this.histMaxNumOfAlternativeEffectiveDuties = qmToCopy.histMaxNumOfAlternativeEffectiveDuties.clone();
////		this.histMaxNumOfAlternativeEffectiveDutiesWoDh = qmToCopy.histMaxNumOfAlternativeEffectiveDutiesWoDh.clone();
//		this.minNumOfAlternativeEffectiveDuties = qmToCopy.minNumOfAlternativeEffectiveDuties;
//		this.minNumOfAlternativeEffectiveDutiesWoDh = qmToCopy.minNumOfAlternativeEffectiveDutiesWoDh;
////		this.maxNumOfAlternativeEffectiveDuties = qmToCopy.maxNumOfAlternativeEffectiveDuties;
////		this.maxNumOfAlternativeEffectiveDutiesWoDh = qmToCopy.maxNumOfAlternativeEffectiveDutiesWoDh;
//		this.totalNumOfAlternativeEffectiveDuties = qmToCopy.totalNumOfAlternativeEffectiveDuties;
//		this.totalNumOfAlternativeEffectiveDutiesWoDh = qmToCopy.totalNumOfAlternativeEffectiveDutiesWoDh;

		this.numOfDuties = qmToCopy.numOfDuties;
		this.numOfLegs = qmToCopy.numOfLegs;

//		this.totalDifficultyScore = qmToCopy.totalDifficultyScore;
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
		this.dutyTimeInMins = 0;

//		this.minNumOfAlternativeDuties = Integer.MAX_VALUE;
//		this.minNumOfAlternativeDutiesWoDh = Integer.MAX_VALUE;
////		this.maxNumOfAlternativeDuties = 0;
////		this.maxNumOfAlternativeDutiesWoDh = 0;
//		this.totalNumOfAlternativeDuties = 0;
//		this.totalNumOfAlternativeDutiesWoDh = 0;
//
//		this.minNumOfAlternativeEffectiveDuties = Integer.MAX_VALUE;
//		this.minNumOfAlternativeEffectiveDutiesWoDh = Integer.MAX_VALUE;
////		this.maxNumOfAlternativeEffectiveDuties = 0;
////		this.maxNumOfAlternativeEffectiveDutiesWoDh = 0;
//		this.totalNumOfAlternativeEffectiveDuties = 0;
//		this.totalNumOfAlternativeEffectiveDutiesWoDh = 0;

		this.numOfDuties = 0;
		this.numOfLegs = 0;

//		this.totalDifficultyScore = 0.0;
	}

	/**
	 * Since our shortest path algorithm searches from right to left,
	 * this method must be used only comparison, performance improvement purposes etc.
	 * It can not be used for building nodes to be registered!
	 * 
	 * @param d
	 * @param dp
	 */
	public void addToQualityMetricFw(Duty d,
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
		this.activeBlocktimeInMins += (d.getBlockTimeInMinsActive() - dp.blockTimeOfCoveringsActive);
		this.dutyTimeInMins += d.getDutyDurationInMins(hbNdx);

//		this.histMinNumOfAlternativeDuties[this.numOfDuties] = this.minNumOfAlternativeDuties;
//		this.histMinNumOfAlternativeDutiesWoDh[this.numOfDuties] = this.minNumOfAlternativeDutiesWoDh;
////		this.histMaxNumOfAlternativeDuties[this.numOfDuties] = this.maxNumOfAlternativeDuties;
////		this.histMaxNumOfAlternativeDutiesWoDh[this.numOfDuties] = this.maxNumOfAlternativeDutiesWoDh;
//		if (this.minNumOfAlternativeDuties > d.getMinNumOfAlternativeDuties())
//			this.minNumOfAlternativeDuties = d.getMinNumOfAlternativeDuties();
//		if (this.minNumOfAlternativeDutiesWoDh > dp.minNumOfAlternativeDutiesWoDh)
//			this.minNumOfAlternativeDutiesWoDh = dp.minNumOfAlternativeDutiesWoDh;
////		if (this.maxNumOfAlternativeDuties < d.getMaxNumOfAlternativeDuties())
////			this.maxNumOfAlternativeDuties = d.getMaxNumOfAlternativeDuties();
////		if (this.maxNumOfAlternativeDutiesWoDh < dp.maxNumOfAlternativeDutiesWoDh)
////			this.maxNumOfAlternativeDutiesWoDh = dp.maxNumOfAlternativeDutiesWoDh;
//		this.totalNumOfAlternativeDuties += d.getTotalNumOfAlternativeDuties();
//		this.totalNumOfAlternativeDutiesWoDh += dp.totalNumOfAlternativeDutiesWoDh;
//
//		this.histMinNumOfAlternativeEffectiveDuties[this.numOfDuties] = this.minNumOfAlternativeEffectiveDuties;
//		this.histMinNumOfAlternativeEffectiveDutiesWoDh[this.numOfDuties] = this.minNumOfAlternativeEffectiveDutiesWoDh;
////		this.histMaxNumOfAlternativeEffectiveDuties[this.numOfDuties] = this.maxNumOfAlternativeEffectiveDuties;
////		this.histMaxNumOfAlternativeEffectiveDutiesWoDh[this.numOfDuties] = this.maxNumOfAlternativeEffectiveDutiesWoDh;
//		if (this.minNumOfAlternativeEffectiveDuties > dp.minNumOfAlternativeEffectiveDuties)
//			this.minNumOfAlternativeEffectiveDuties = dp.minNumOfAlternativeEffectiveDuties;
//		if (this.minNumOfAlternativeEffectiveDutiesWoDh > dp.minNumOfAlternativeEffectiveDutiesWoDh)
//			this.minNumOfAlternativeEffectiveDutiesWoDh = dp.minNumOfAlternativeEffectiveDutiesWoDh;
////		if (this.maxNumOfAlternativeEffectiveDuties < dp.maxNumOfAlternativeEffectiveDuties)
////			this.maxNumOfAlternativeEffectiveDuties = dp.maxNumOfAlternativeEffectiveDuties;
////		if (this.maxNumOfAlternativeEffectiveDutiesWoDh < dp.maxNumOfAlternativeEffectiveDutiesWoDh)
////			this.maxNumOfAlternativeEffectiveDutiesWoDh = dp.maxNumOfAlternativeEffectiveDutiesWoDh;
//		this.totalNumOfAlternativeEffectiveDuties += dp.totalNumOfAlternativeEffectiveDuties;
//		this.totalNumOfAlternativeEffectiveDutiesWoDh += dp.totalNumOfAlternativeEffectiveDutiesWoDh;

		this.numOfDuties++;
		this.numOfLegs += d.getNumOfLegs();

//		this.totalDifficultyScore += dp.getDifficultyScore();
	}


	public void addToQualityMetricBw(Duty d,
										DutyState dp) {
		this.numOfDh += (d.getNumOfLegsPassive() + dp.numOfCoverings);
		this.dhDurationInMins += (d.getBlockTimeInMinsPassive() + dp.blockTimeOfCoverings);
		this.activeBlocktimeInMins += (d.getBlockTimeInMinsActive() - dp.blockTimeOfCoveringsActive);
		this.dutyTimeInMins += d.getDutyDurationInMins(hbNdx);

//		this.histMinNumOfAlternativeDuties[this.numOfDuties] = this.minNumOfAlternativeDuties;
//		this.histMinNumOfAlternativeDutiesWoDh[this.numOfDuties] = this.minNumOfAlternativeDutiesWoDh;
////		this.histMaxNumOfAlternativeDuties[this.numOfDuties] = this.maxNumOfAlternativeDuties;
////		this.histMaxNumOfAlternativeDutiesWoDh[this.numOfDuties] = this.maxNumOfAlternativeDutiesWoDh;
//		if (this.minNumOfAlternativeDuties > d.getMinNumOfAlternativeDuties())
//			this.minNumOfAlternativeDuties = d.getMinNumOfAlternativeDuties();
//		if (this.minNumOfAlternativeDutiesWoDh > dp.minNumOfAlternativeDutiesWoDh)
//			this.minNumOfAlternativeDutiesWoDh = dp.minNumOfAlternativeDutiesWoDh;
////		if (this.maxNumOfAlternativeDuties < d.getMaxNumOfAlternativeDuties())
////			this.maxNumOfAlternativeDuties = d.getMaxNumOfAlternativeDuties();
////		if (this.maxNumOfAlternativeDutiesWoDh < dp.maxNumOfAlternativeDutiesWoDh)
////			this.maxNumOfAlternativeDutiesWoDh = dp.maxNumOfAlternativeDutiesWoDh;
//		this.totalNumOfAlternativeDuties += d.getTotalNumOfAlternativeDuties();
//		this.totalNumOfAlternativeDutiesWoDh += dp.totalNumOfAlternativeDutiesWoDh;
//
//		this.histMinNumOfAlternativeEffectiveDuties[this.numOfDuties] = this.minNumOfAlternativeEffectiveDuties;
//		this.histMinNumOfAlternativeEffectiveDutiesWoDh[this.numOfDuties] = this.minNumOfAlternativeEffectiveDutiesWoDh;
////		this.histMaxNumOfAlternativeEffectiveDuties[this.numOfDuties] = this.maxNumOfAlternativeEffectiveDuties;
////		this.histMaxNumOfAlternativeEffectiveDutiesWoDh[this.numOfDuties] = this.maxNumOfAlternativeEffectiveDutiesWoDh;
//		if (this.minNumOfAlternativeEffectiveDuties > dp.minNumOfAlternativeEffectiveDuties)
//			this.minNumOfAlternativeEffectiveDuties = dp.minNumOfAlternativeEffectiveDuties;
//		if (this.minNumOfAlternativeEffectiveDutiesWoDh > dp.minNumOfAlternativeEffectiveDutiesWoDh)
//			this.minNumOfAlternativeEffectiveDutiesWoDh = dp.minNumOfAlternativeEffectiveDutiesWoDh;
////		if (this.maxNumOfAlternativeEffectiveDuties < dp.maxNumOfAlternativeEffectiveDuties)
////			this.maxNumOfAlternativeEffectiveDuties = dp.maxNumOfAlternativeEffectiveDuties;
////		if (this.maxNumOfAlternativeEffectiveDutiesWoDh < dp.maxNumOfAlternativeEffectiveDutiesWoDh)
////			this.maxNumOfAlternativeEffectiveDutiesWoDh = dp.maxNumOfAlternativeEffectiveDutiesWoDh;
//		this.totalNumOfAlternativeEffectiveDuties += dp.totalNumOfAlternativeEffectiveDuties;
//		this.totalNumOfAlternativeEffectiveDutiesWoDh += dp.totalNumOfAlternativeEffectiveDutiesWoDh;

		this.numOfDuties++;
		this.numOfLegs += d.getNumOfLegs();

//		this.totalDifficultyScore += dp.getDifficultyScore();
	}

	public void addLeadingDutyQualityMetric(QualityMetric leadingDutyQm) {
		this.numOfDh += leadingDutyQm.numOfDh;
		this.dhDurationInMins += leadingDutyQm.dhDurationInMins;
		this.activeBlocktimeInMins += leadingDutyQm.activeBlocktimeInMins;
		this.dutyTimeInMins += leadingDutyQm.dutyTimeInMins;

//		this.histMinNumOfAlternativeDuties[this.numOfDuties] = this.minNumOfAlternativeDuties;
//		this.histMinNumOfAlternativeDutiesWoDh[this.numOfDuties] = this.minNumOfAlternativeDutiesWoDh;
////		this.histMaxNumOfAlternativeDuties[this.numOfDuties] = this.maxNumOfAlternativeDuties;
////		this.histMaxNumOfAlternativeDutiesWoDh[this.numOfDuties] = this.maxNumOfAlternativeDutiesWoDh;
//		if (this.minNumOfAlternativeDuties > leadingDutyQm.minNumOfAlternativeDuties)
//			this.minNumOfAlternativeDuties = leadingDutyQm.minNumOfAlternativeDuties;
//		if (this.minNumOfAlternativeDutiesWoDh > leadingDutyQm.minNumOfAlternativeDutiesWoDh)
//			this.minNumOfAlternativeDutiesWoDh = leadingDutyQm.minNumOfAlternativeDutiesWoDh;
////		if (this.maxNumOfAlternativeDuties < leadingDutyQm.maxNumOfAlternativeDuties)
////			this.maxNumOfAlternativeDuties = leadingDutyQm.maxNumOfAlternativeDuties;
////		if (this.maxNumOfAlternativeDutiesWoDh < leadingDutyQm.maxNumOfAlternativeDutiesWoDh)
////			this.maxNumOfAlternativeDutiesWoDh = leadingDutyQm.maxNumOfAlternativeDutiesWoDh;
//		this.totalNumOfAlternativeDuties += leadingDutyQm.totalNumOfAlternativeDuties;
//		this.totalNumOfAlternativeDutiesWoDh += leadingDutyQm.totalNumOfAlternativeDutiesWoDh;
//
//		this.histMinNumOfAlternativeEffectiveDuties[this.numOfDuties] = this.minNumOfAlternativeEffectiveDuties;
//		this.histMinNumOfAlternativeEffectiveDutiesWoDh[this.numOfDuties] = this.minNumOfAlternativeEffectiveDutiesWoDh;
////		this.histMaxNumOfAlternativeEffectiveDuties[this.numOfDuties] = this.maxNumOfAlternativeEffectiveDuties;
////		this.histMaxNumOfAlternativeEffectiveDutiesWoDh[this.numOfDuties] = this.maxNumOfAlternativeEffectiveDutiesWoDh;
//		if (this.minNumOfAlternativeEffectiveDuties > leadingDutyQm.minNumOfAlternativeEffectiveDuties)
//			this.minNumOfAlternativeEffectiveDuties = leadingDutyQm.minNumOfAlternativeEffectiveDuties;
//		if (this.minNumOfAlternativeEffectiveDutiesWoDh > leadingDutyQm.minNumOfAlternativeEffectiveDutiesWoDh)
//			this.minNumOfAlternativeEffectiveDutiesWoDh = leadingDutyQm.minNumOfAlternativeEffectiveDutiesWoDh;
////		if (this.maxNumOfAlternativeEffectiveDuties < leadingDutyQm.maxNumOfAlternativeEffectiveDuties)
////			this.maxNumOfAlternativeEffectiveDuties = leadingDutyQm.maxNumOfAlternativeEffectiveDuties;
////		if (this.maxNumOfAlternativeEffectiveDutiesWoDh < leadingDutyQm.maxNumOfAlternativeEffectiveDutiesWoDh)
////			this.maxNumOfAlternativeEffectiveDutiesWoDh = leadingDutyQm.maxNumOfAlternativeEffectiveDutiesWoDh;
//		this.totalNumOfAlternativeEffectiveDuties += leadingDutyQm.totalNumOfAlternativeEffectiveDuties;
//		this.totalNumOfAlternativeEffectiveDutiesWoDh += leadingDutyQm.totalNumOfAlternativeEffectiveDutiesWoDh;
		/*
		 * TODO Can we use ++ here ?
		 */
		this.numOfDuties++;	//	+= qmToAdd.numOfDuties;
		this.numOfLegs += leadingDutyQm.numOfLegs;

//		this.totalDifficultyScore += leadingDutyQm.totalDifficultyScore;
	}

	public void removeLastDutyQualityMetric(Duty d,
											DutyState dp) {
		this.numOfDh -= (d.getNumOfLegsPassive() + dp.numOfCoverings);
		this.dhDurationInMins -= (d.getBlockTimeInMinsPassive() + dp.blockTimeOfCoverings);
		this.activeBlocktimeInMins -= (d.getBlockTimeInMinsActive() - dp.blockTimeOfCoveringsActive);
		this.dutyTimeInMins -= d.getDutyDurationInMins(hbNdx);

		this.numOfDuties--;
		this.numOfLegs -= d.getNumOfLegs();

//		this.minNumOfAlternativeDuties = this.histMinNumOfAlternativeDuties[this.numOfDuties];
//		this.minNumOfAlternativeDutiesWoDh = this.histMinNumOfAlternativeDutiesWoDh[this.numOfDuties];
////		this.maxNumOfAlternativeDuties = this.histMaxNumOfAlternativeDuties[this.numOfDuties];
////		this.maxNumOfAlternativeDutiesWoDh = this.histMaxNumOfAlternativeDutiesWoDh[this.numOfDuties];
//		this.totalNumOfAlternativeDuties -= d.getTotalNumOfAlternativeDuties();
//		this.totalNumOfAlternativeDutiesWoDh -= dp.totalNumOfAlternativeDutiesWoDh;
//
//		this.minNumOfAlternativeEffectiveDuties = this.histMinNumOfAlternativeEffectiveDuties[this.numOfDuties];
//		this.minNumOfAlternativeEffectiveDutiesWoDh = this.histMinNumOfAlternativeEffectiveDutiesWoDh[this.numOfDuties];
////		this.maxNumOfAlternativeEffectiveDuties = this.histMaxNumOfAlternativeEffectiveDuties[this.numOfDuties];
////		this.maxNumOfAlternativeEffectiveDutiesWoDh = this.histMaxNumOfAlternativeEffectiveDutiesWoDh[this.numOfDuties];
//		this.totalNumOfAlternativeEffectiveDuties -= dp.totalNumOfAlternativeEffectiveDuties;
//		this.totalNumOfAlternativeEffectiveDutiesWoDh -= dp.totalNumOfAlternativeEffectiveDutiesWoDh;
//
//		this.totalDifficultyScore -= dp.getDifficultyScore();
	}

	public void removeLeadingDutyQualityMetric(QualityMetric leadingDutyQm) {
		this.numOfDh -= leadingDutyQm.numOfDh;
		this.dhDurationInMins -= leadingDutyQm.dhDurationInMins;
		this.activeBlocktimeInMins -= leadingDutyQm.activeBlocktimeInMins;
		this.dutyTimeInMins -= leadingDutyQm.dutyTimeInMins;

		/*
		 * TODO Can we use -- here ?
		 */
		this.numOfDuties--;
		this.numOfLegs -= leadingDutyQm.numOfLegs;

//		this.minNumOfAlternativeDuties = this.histMinNumOfAlternativeDuties[this.numOfDuties];
//		this.minNumOfAlternativeDutiesWoDh = this.histMinNumOfAlternativeDutiesWoDh[this.numOfDuties];
////		this.maxNumOfAlternativeDuties = this.histMaxNumOfAlternativeDuties[this.numOfDuties];
////		this.maxNumOfAlternativeDutiesWoDh = this.histMaxNumOfAlternativeDutiesWoDh[this.numOfDuties];
//		this.totalNumOfAlternativeDuties -= leadingDutyQm.totalNumOfAlternativeDuties;
//		this.totalNumOfAlternativeDutiesWoDh -= leadingDutyQm.totalNumOfAlternativeDutiesWoDh;
//
//		this.minNumOfAlternativeEffectiveDuties = this.histMinNumOfAlternativeEffectiveDuties[this.numOfDuties];
//		this.minNumOfAlternativeEffectiveDutiesWoDh = this.histMinNumOfAlternativeEffectiveDutiesWoDh[this.numOfDuties];
////		this.maxNumOfAlternativeEffectiveDuties = this.histMaxNumOfAlternativeEffectiveDuties[this.numOfDuties];
////		this.maxNumOfAlternativeEffectiveDutiesWoDh = this.histMaxNumOfAlternativeEffectiveDutiesWoDh[this.numOfDuties];
//		this.totalNumOfAlternativeEffectiveDuties -= leadingDutyQm.totalNumOfAlternativeEffectiveDuties;
//		this.totalNumOfAlternativeEffectiveDutiesWoDh -= leadingDutyQm.totalNumOfAlternativeEffectiveDutiesWoDh;
//
//		this.totalDifficultyScore -= leadingDutyQm.totalDifficultyScore;
	}

	public boolean isBetterInTermsOfDh(QualityMetric qm, double diffScore, double diffScoreQm) {
		if ((qm.numOfLegs == 0) || (this.numOfDh < qm.numOfDh)
				|| ((this.numOfDh == qm.numOfDh) && (diffScore < diffScoreQm))) {
			return true;
		} else
			return false;
	}

	public boolean isBetterWithLessDuties(QualityMetric qm) {
		if ((qm.numOfLegs == 0) || (this.numOfDh < qm.numOfDh)
				|| ((this.numOfDh == qm.numOfDh) && (this.numOfDuties < qm.numOfDuties))
				|| ((this.numOfDh == qm.numOfDh) && (this.numOfDuties == qm.numOfDuties) && (this.getAvgActiveBlockTimePerDuty() > qm.getAvgActiveBlockTimePerDuty()))) {
			return true;
		} else
			return false;
	}

	public boolean isBetterThan(int heuristicNo, QualityMetric qm) {

//if ((!theLast) && (this.numOfDuties != qm.numOfDuties))
//System.out.println();

		if ((qm.numOfLegs == 0) || (this.numOfDh < qm.numOfDh)

				|| ((this.numOfDh == qm.numOfDh) && (this.getAvgActiveBlockTimePerDuty() > qm.getAvgActiveBlockTimePerDuty()))
				|| ((this.numOfDh == qm.numOfDh) && (this.getAvgActiveBlockTimePerDuty() == qm.getAvgActiveBlockTimePerDuty()) && (this.getActiveBlockTimeDutyTimeRate() > qm.getActiveBlockTimeDutyTimeRate()))

//				|| ((this.numOfDh == qm.numOfDh) && (this.totalNumOfAlternativeDutiesWoDh < qm.totalNumOfAlternativeDutiesWoDh))

//				|| ((this.numOfDh == qm.numOfDh) && (this.getAvgDifficultyScorePerDuty() > qm.getAvgDifficultyScorePerDuty()))

//				|| ((this.numOfDh == qm.numOfDh) && (this.minNumOfAlternativeDutiesWoDh < qm.minNumOfAlternativeDutiesWoDh))
//				|| ((this.numOfDh == qm.numOfDh) && (this.minNumOfAlternativeDutiesWoDh == qm.minNumOfAlternativeDutiesWoDh) && (this.getAvgActiveBlockTimePerDuty() > qm.getAvgActiveBlockTimePerDuty()))

//				|| ((this.numOfDh == qm.numOfDh) && (this.numOfDuties < qm.numOfDuties))
//				|| ((this.numOfDh == qm.numOfDh) && (this.numOfDuties == qm.numOfDuties) && (this.getAvgDifficultyScorePerDuty() > qm.getAvgDifficultyScorePerDuty()))

//				|| ((this.numOfDh == qm.numOfDh) && (this.numOfDuties < qm.numOfDuties))
//				|| ((this.numOfDh == qm.numOfDh) && (this.numOfDuties == qm.numOfDuties) && (this.getAvgActiveBlockTimePerDuty() > qm.getAvgActiveBlockTimePerDuty()))

//				|| ((this.numOfDh == qm.numOfDh) && (this.numOfDhCriticalDuties < qm.numOfDhCriticalDuties))
//				|| ((this.numOfDh == qm.numOfDh) && (this.numOfDhCriticalDuties == qm.numOfDhCriticalDuties) && (this.numOfDuties < qm.numOfDuties))
//				|| ((this.numOfDh == qm.numOfDh) && (this.numOfDhCriticalDuties == qm.numOfDhCriticalDuties) && (this.numOfDuties == qm.numOfDuties) && (this.getAvgActiveBlockTimePerDuty() > qm.getAvgActiveBlockTimePerDuty()))

//				&& (this.totalNumOfAlternativeDutiesWoDh < qm.totalNumOfAlternativeDutiesWoDh)
		) {
			return true;
		} else
			return false;
	}

	public boolean doesItWorthToGoDeeper(int currentDept,
											QualityMetric bestQualSoFar) {
		if ((bestQualSoFar == null) || (bestQualSoFar.numOfDh > this.numOfDh)

//				|| ((bestQualSoFar.numOfDh == this.numOfDh) && (bestQualSoFar.totalNumOfAlternativeDutiesWoDh > this.totalNumOfAlternativeDutiesWoDh))

				|| ((bestQualSoFar.numOfDh == this.numOfDh) && (bestQualSoFar.getAvgActiveBlockTimePerDuty() < (1.0 * this.activeBlocktimeInMins + (currentDept - 1) * HeurosSystemParam.maxDutyBlockTimeInMins) / (this.numOfDuties + (currentDept - 1))))

//				|| ((bestQualSoFar.numOfDh == this.numOfDh) && (bestQualSoFar.numOfDuties > this.numOfDuties))
//				|| ((bestQualSoFar.numOfDh == this.numOfDh) && (bestQualSoFar.numOfDuties == this.numOfDuties) && (bestQualSoFar.getAvgActiveBlockTimePerDuty() <= (1.0 * this.activeBlocktimeInMins + (currentDept - 1) * HeurosSystemParam.maxDutyBlockTimeInMins) / (this.numOfDuties + (currentDept - 1))))

//				|| ((bestQualSoFar.numOfDh == this.numOfDh) && (bestQualSoFar.numOfDuties > this.numOfDuties))
//				|| ((bestQualSoFar.numOfDh == this.numOfDh) && (bestQualSoFar.numOfDuties == this.numOfDuties) && (bestQualSoFar.getAvgActiveBlockTimePerDuty() <= (1.0 * this.activeBlocktimeInMins + (currentDept - 1) * HeurosSystemParam.maxDutyBlockTimeInMins) / (this.numOfDuties + (currentDept - 1))))

//				|| ((bestQualSoFar.numOfDh == this.numOfDh) && (bestQualSoFar.numOfDuties > this.numOfDuties))
//				|| ((bestQualSoFar.numOfDh == this.numOfDh) && (bestQualSoFar.numOfDuties == this.numOfDuties) && (bestQualSoFar.getAvgActiveBlockTimePerDuty() < this.getAvgActiveBlockTimePerDuty()))

//				|| ((bestQualSoFar.numOfDh == this.numOfDh) && (bestQualSoFar.numOfDhCriticalDuties > this.numOfDhCriticalDuties))
//				|| ((bestQualSoFar.numOfDh == this.numOfDh) && (bestQualSoFar.numOfDhCriticalDuties == this.numOfDhCriticalDuties) && (bestQualSoFar.numOfDuties > this.numOfDuties))
//				|| ((bestQualSoFar.numOfDh == this.numOfDh) && (bestQualSoFar.numOfDhCriticalDuties == this.numOfDhCriticalDuties) && (bestQualSoFar.numOfDuties == this.numOfDuties) && (bestQualSoFar.getAvgActiveBlockTimePerDuty() < this.getAvgActiveBlockTimePerDuty()))

//				&& (bestQualSoFar.totalNumOfAlternativeDutiesWoDh > this.totalNumOfAlternativeDutiesWoDh)
				) {
			return true;
		} else
			return false;
	}

	public boolean isNotEmpty() {
		return ((numOfDh > 0)
			|| (dhDurationInMins > 0)
			|| (activeBlocktimeInMins > 0)
			|| (dutyTimeInMins > 0)
			|| (numOfDuties > 0)
			|| (numOfLegs > 0)
//			|| (minNumOfAlternativeDuties < Integer.MAX_VALUE)
//			|| (minNumOfAlternativeDutiesWoDh < Integer.MAX_VALUE)
////			|| (maxNumOfAlternativeDuties > 0)
////			|| (maxNumOfAlternativeDutiesWoDh > 0)
//			|| (totalNumOfAlternativeDuties < 0)
//			|| (totalNumOfAlternativeDutiesWoDh > 0)
//			|| (minNumOfAlternativeEffectiveDuties < Integer.MAX_VALUE)
//			|| (minNumOfAlternativeEffectiveDutiesWoDh < Integer.MAX_VALUE)
////			|| (maxNumOfAlternativeEffectiveDuties > 0)
////			|| (maxNumOfAlternativeEffectiveDutiesWoDh > 0)
//			|| (totalNumOfAlternativeEffectiveDuties < 0)
//			|| (totalNumOfAlternativeEffectiveDutiesWoDh > 0)
//			|| (totalDifficultyScore > 0.0)
			);
	}

	public boolean isTheSame(QualityMetric qmToCompare) {
		return (this.numOfDh == qmToCompare.numOfDh)
				&& (this.dhDurationInMins == qmToCompare.dhDurationInMins)
				&& (this.activeBlocktimeInMins == qmToCompare.activeBlocktimeInMins)
				&& (this.dutyTimeInMins == qmToCompare.dutyTimeInMins)
				&& (this.numOfDuties == qmToCompare.numOfDuties)
				&& (this.numOfLegs == qmToCompare.numOfLegs)
//				&& (this.minNumOfAlternativeDuties == qmToCompare.minNumOfAlternativeDuties)
//				&& (this.minNumOfAlternativeDutiesWoDh == qmToCompare.minNumOfAlternativeDutiesWoDh)
////				&& (this.maxNumOfAlternativeDuties == qmToCompare.maxNumOfAlternativeDuties)
////				&& (this.maxNumOfAlternativeDutiesWoDh == qmToCompare.maxNumOfAlternativeDutiesWoDh)
//				&& (this.totalNumOfAlternativeDuties == qmToCompare.totalNumOfAlternativeDuties)
//				&& (this.totalNumOfAlternativeDutiesWoDh == qmToCompare.totalNumOfAlternativeDutiesWoDh)
//				&& (this.minNumOfAlternativeEffectiveDuties == qmToCompare.minNumOfAlternativeEffectiveDuties)
//				&& (this.minNumOfAlternativeEffectiveDutiesWoDh == qmToCompare.minNumOfAlternativeEffectiveDutiesWoDh)
////				&& (this.maxNumOfAlternativeEffectiveDuties == qmToCompare.maxNumOfAlternativeEffectiveDuties)
////				&& (this.maxNumOfAlternativeEffectiveDutiesWoDh == qmToCompare.maxNumOfAlternativeEffectiveDutiesWoDh)
//				&& (this.totalNumOfAlternativeEffectiveDuties == qmToCompare.totalNumOfAlternativeEffectiveDuties)
//				&& (this.totalNumOfAlternativeEffectiveDutiesWoDh == qmToCompare.totalNumOfAlternativeEffectiveDutiesWoDh)
//				&& (this.totalDifficultyScore == qmToCompare.totalDifficultyScore)
				;
	}

	public boolean hasTheSameValues(int numOfDh,
									int dhDurationInMins,
									int activeBlocktimeInMins,
									int dutyTimeInMins,
									int numOfDuties,
									int numOfLegs,
									int minNumOfAlternativeDuties,
									int minNumOfAlternativeDutiesWoDh,
//									int maxNumOfAlternativeDuties,
//									int maxNumOfAlternativeDutiesWoDh,
									int totalNumOfAlternativeDuties,
									int totalNumOfAlternativeDutiesWoDh,
									int minNumOfAlternativeEffectiveDuties,
									int minNumOfAlternativeEffectiveDutiesWoDh,
//									int maxNumOfAlternativeEffectiveDuties,
//									int maxNumOfAlternativeEffectiveDutiesWoDh,
									int totalNumOfAlternativeEffectiveDuties,
									int totalNumOfAlternativeEffectiveDutiesWoDh,
									double totalDifficultyScore) {
		return 	(this.numOfDh == numOfDh)
				&& (this.dhDurationInMins == dhDurationInMins)
				&& (this.activeBlocktimeInMins == activeBlocktimeInMins)
				&& (this.dutyTimeInMins == dutyTimeInMins)
				&& (this.numOfDuties == numOfDuties)
				&& (this.numOfLegs == numOfLegs)
//				&& (this.minNumOfAlternativeDuties == minNumOfAlternativeDuties)
//				&& (this.minNumOfAlternativeDutiesWoDh == minNumOfAlternativeDutiesWoDh)
////				&& (this.maxNumOfAlternativeDuties == maxNumOfAlternativeDuties)
////				&& (this.maxNumOfAlternativeDutiesWoDh == maxNumOfAlternativeDutiesWoDh)
//				&& (this.totalNumOfAlternativeDuties == totalNumOfAlternativeDuties)
//				&& (this.totalNumOfAlternativeDutiesWoDh == totalNumOfAlternativeDutiesWoDh)
//				&& (this.minNumOfAlternativeEffectiveDuties == minNumOfAlternativeEffectiveDuties)
//				&& (this.minNumOfAlternativeEffectiveDutiesWoDh == minNumOfAlternativeEffectiveDutiesWoDh)
////				&& (this.maxNumOfAlternativeEffectiveDuties == maxNumOfAlternativeEffectiveDuties)
////				&& (this.maxNumOfAlternativeEffectiveDutiesWoDh == maxNumOfAlternativeEffectiveDutiesWoDh)
//				&& (this.totalNumOfAlternativeEffectiveDuties == totalNumOfAlternativeEffectiveDuties)
//				&& (this.totalNumOfAlternativeEffectiveDutiesWoDh == totalNumOfAlternativeEffectiveDutiesWoDh)
//				&& (Math.abs(this.totalDifficultyScore - totalDifficultyScore) < 0.0001)
				;
	}

	public String toString() {
		return "#DH:" + numOfDh + "/" + dhDurationInMins +
				", ABT:" + activeBlocktimeInMins + "/" + numOfDuties + "=" + (Math.floor((100.0 * activeBlocktimeInMins) / numOfDuties) / 100.0) +
				", DTR:" + activeBlocktimeInMins + "/" + dutyTimeInMins + "=" + (Math.floor((100.0 * activeBlocktimeInMins) / dutyTimeInMins) / 100.0)
//				+ ", #AltDMin:" + minNumOfAlternativeDuties +
////				", #AltDMax:" + maxNumOfAlternativeDuties +
//				", #AltD:" + totalNumOfAlternativeDuties +
//				", #AltDMinWoDH:" + minNumOfAlternativeDutiesWoDh +
////				", #AltDMaxWoDH:" + maxNumOfAlternativeDutiesWoDh +
//				", #AltDWoDH:" + totalNumOfAlternativeDutiesWoDh +
//				", #AltEftDMin:" + minNumOfAlternativeEffectiveDuties +
////				", #AltEftDMax:" + maxNumOfAlternativeEffectiveDuties +
//				", #AltEftD:" + totalNumOfAlternativeEffectiveDuties +
//				", #AltEftDMinWoDH:" + minNumOfAlternativeEffectiveDutiesWoDh +
////				", #AltEftDMaxWoDH:" + maxNumOfAlternativeEffectiveDutiesWoDh +
//				", #AltEftDWoDH:" + totalNumOfAlternativeEffectiveDutiesWoDh +
//				", DiffScore: " + totalDifficultyScore
				;
	}
}
