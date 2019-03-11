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
	private int numOfLegs = 0;
	/*
	 * The last metric to check if others are equal!
	 */
	private int[] histMinNumOfAlternativeDuties = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
	private int[] histMinNumOfAlternativeDutiesWoDh = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
	private int minNumOfAlternativeDuties = Integer.MAX_VALUE;
	private int minNumOfAlternativeDutiesWoDh = Integer.MAX_VALUE;
	private int[] histMaxNumOfAlternativeDuties = {0,0,0,0,0,0,0};
	private int[] histMaxNumOfAlternativeDutiesWoDh = {0,0,0,0,0,0,0};
	private int maxNumOfAlternativeDuties = 0;
	private int maxNumOfAlternativeDutiesWoDh = 0;
	private int totalNumOfAlternativeDuties = 0;
	private int totalNumOfAlternativeDutiesWoDh = 0;

	private int[] histMinNumOfAlternativeEffectiveDuties = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
	private int[] histMinNumOfAlternativeEffectiveDutiesWoDh = {Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE};
	private int minNumOfAlternativeEffectiveDuties = Integer.MAX_VALUE;
	private int minNumOfAlternativeEffectiveDutiesWoDh = Integer.MAX_VALUE;
	private int[] histMaxNumOfAlternativeEffectiveDuties = {0,0,0,0,0,0,0};
	private int[] histMaxNumOfAlternativeEffectiveDutiesWoDh = {0,0,0,0,0,0,0};
	private int maxNumOfAlternativeEffectiveDuties = 0;
	private int maxNumOfAlternativeEffectiveDutiesWoDh = 0;
	private int totalNumOfAlternativeEffectiveDuties = 0;
	private int totalNumOfAlternativeEffectiveDutiesWoDh = 0;

	private double difficultyScore = 0.0;

//	private double getAvgActiveBlockTimePerDuty() {
//		return (1.0 * this.activeBlocktimeInMins) / this.numOfDuties;
//	}
//
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

	public QualityMetric(QualityMetric qmToCopy) {
		this.numOfDh = qmToCopy.numOfDh;
		this.dhDurationInMins = qmToCopy.dhDurationInMins;
		this.activeBlocktimeInMins = qmToCopy.activeBlocktimeInMins;

		this.histMinNumOfAlternativeDuties = qmToCopy.histMinNumOfAlternativeDuties.clone();
		this.histMinNumOfAlternativeDutiesWoDh = qmToCopy.histMinNumOfAlternativeDutiesWoDh.clone();
		this.histMaxNumOfAlternativeDuties = qmToCopy.histMaxNumOfAlternativeDuties.clone();
		this.histMaxNumOfAlternativeDutiesWoDh = qmToCopy.histMaxNumOfAlternativeDutiesWoDh.clone();
		this.minNumOfAlternativeDuties = qmToCopy.minNumOfAlternativeDuties;
		this.minNumOfAlternativeDutiesWoDh = qmToCopy.minNumOfAlternativeDutiesWoDh;
		this.maxNumOfAlternativeDuties = qmToCopy.maxNumOfAlternativeDuties;
		this.maxNumOfAlternativeDutiesWoDh = qmToCopy.maxNumOfAlternativeDutiesWoDh;
		this.totalNumOfAlternativeDuties = qmToCopy.totalNumOfAlternativeDuties;
		this.totalNumOfAlternativeDutiesWoDh = qmToCopy.totalNumOfAlternativeDutiesWoDh;

		this.histMinNumOfAlternativeEffectiveDuties = qmToCopy.histMinNumOfAlternativeEffectiveDuties.clone();
		this.histMinNumOfAlternativeEffectiveDutiesWoDh = qmToCopy.histMinNumOfAlternativeEffectiveDutiesWoDh.clone();
		this.histMaxNumOfAlternativeEffectiveDuties = qmToCopy.histMaxNumOfAlternativeEffectiveDuties.clone();
		this.histMaxNumOfAlternativeEffectiveDutiesWoDh = qmToCopy.histMaxNumOfAlternativeEffectiveDutiesWoDh.clone();
		this.minNumOfAlternativeEffectiveDuties = qmToCopy.minNumOfAlternativeEffectiveDuties;
		this.minNumOfAlternativeEffectiveDutiesWoDh = qmToCopy.minNumOfAlternativeEffectiveDutiesWoDh;
		this.maxNumOfAlternativeEffectiveDuties = qmToCopy.maxNumOfAlternativeEffectiveDuties;
		this.maxNumOfAlternativeEffectiveDutiesWoDh = qmToCopy.maxNumOfAlternativeEffectiveDutiesWoDh;
		this.totalNumOfAlternativeEffectiveDuties = qmToCopy.totalNumOfAlternativeEffectiveDuties;
		this.totalNumOfAlternativeEffectiveDutiesWoDh = qmToCopy.totalNumOfAlternativeEffectiveDutiesWoDh;

		this.numOfDuties = qmToCopy.numOfDuties;
		this.numOfLegs = qmToCopy.numOfLegs;

		this.difficultyScore = qmToCopy.difficultyScore;
	}

	public QualityMetric(DutyView d,
							DutyState dp) {
		this.numOfDh = (d.getNumOfLegsPassive() + dp.numOfCoverings);
		this.dhDurationInMins = (d.getBlockTimeInMinsPassive() + dp.blockTimeOfCoverings);
		this.activeBlocktimeInMins = (d.getBlockTimeInMinsActive() - dp.blockTimeOfCoveringsActive);

		this.minNumOfAlternativeDuties = d.getMinNumOfAlternativeDuties();
		this.minNumOfAlternativeDutiesWoDh = dp.minNumOfAlternativeDutiesWoDh;
		this.maxNumOfAlternativeDuties = d.getMaxNumOfAlternativeDuties();
		this.maxNumOfAlternativeDutiesWoDh = dp.maxNumOfAlternativeDutiesWoDh;
		this.totalNumOfAlternativeDuties = d.getTotalNumOfAlternativeDuties();
		this.totalNumOfAlternativeDutiesWoDh = dp.totalNumOfAlternativeDutiesWoDh;

		this.minNumOfAlternativeEffectiveDuties = d.getMinNumOfAlternativeEffectiveDuties();
		this.minNumOfAlternativeEffectiveDutiesWoDh = dp.minNumOfAlternativeEffectiveDutiesWoDh;
		this.maxNumOfAlternativeEffectiveDuties = d.getMaxNumOfAlternativeEffectiveDuties();
		this.maxNumOfAlternativeEffectiveDutiesWoDh = dp.maxNumOfAlternativeEffectiveDutiesWoDh;
		this.totalNumOfAlternativeEffectiveDuties = d.getTotalNumOfAlternativeEffectiveDuties();
		this.totalNumOfAlternativeEffectiveDutiesWoDh = dp.totalNumOfAlternativeEffectiveDutiesWoDh;

		this.numOfDuties = 1;
		this.numOfLegs = d.getNumOfLegs();

		this.difficultyScore = dp.getDifficultyScoreOfTheLeg();
	}

	public void injectValues(QualityMetric qmToCopy) {
		this.numOfDh = qmToCopy.numOfDh;
		this.dhDurationInMins = qmToCopy.dhDurationInMins;
		this.activeBlocktimeInMins = qmToCopy.activeBlocktimeInMins;

		this.histMinNumOfAlternativeDuties = qmToCopy.histMinNumOfAlternativeDuties.clone();
		this.histMinNumOfAlternativeDutiesWoDh = qmToCopy.histMinNumOfAlternativeDutiesWoDh.clone();
		this.histMaxNumOfAlternativeDuties = qmToCopy.histMaxNumOfAlternativeDuties.clone();
		this.histMaxNumOfAlternativeDutiesWoDh = qmToCopy.histMaxNumOfAlternativeDutiesWoDh.clone();
		this.minNumOfAlternativeDuties = qmToCopy.minNumOfAlternativeDuties;
		this.minNumOfAlternativeDutiesWoDh = qmToCopy.minNumOfAlternativeDutiesWoDh;
		this.maxNumOfAlternativeDuties = qmToCopy.maxNumOfAlternativeDuties;
		this.maxNumOfAlternativeDutiesWoDh = qmToCopy.maxNumOfAlternativeDutiesWoDh;
		this.totalNumOfAlternativeDuties = qmToCopy.totalNumOfAlternativeDuties;
		this.totalNumOfAlternativeDutiesWoDh = qmToCopy.totalNumOfAlternativeDutiesWoDh;

		this.histMinNumOfAlternativeEffectiveDuties = qmToCopy.histMinNumOfAlternativeEffectiveDuties.clone();
		this.histMinNumOfAlternativeEffectiveDutiesWoDh = qmToCopy.histMinNumOfAlternativeEffectiveDutiesWoDh.clone();
		this.histMaxNumOfAlternativeEffectiveDuties = qmToCopy.histMaxNumOfAlternativeEffectiveDuties.clone();
		this.histMaxNumOfAlternativeEffectiveDutiesWoDh = qmToCopy.histMaxNumOfAlternativeEffectiveDutiesWoDh.clone();
		this.minNumOfAlternativeEffectiveDuties = qmToCopy.minNumOfAlternativeEffectiveDuties;
		this.minNumOfAlternativeEffectiveDutiesWoDh = qmToCopy.minNumOfAlternativeEffectiveDutiesWoDh;
		this.maxNumOfAlternativeEffectiveDuties = qmToCopy.maxNumOfAlternativeEffectiveDuties;
		this.maxNumOfAlternativeEffectiveDutiesWoDh = qmToCopy.maxNumOfAlternativeEffectiveDutiesWoDh;
		this.totalNumOfAlternativeEffectiveDuties = qmToCopy.totalNumOfAlternativeEffectiveDuties;
		this.totalNumOfAlternativeEffectiveDutiesWoDh = qmToCopy.totalNumOfAlternativeEffectiveDutiesWoDh;

		this.numOfDuties = qmToCopy.numOfDuties;
		this.numOfLegs = qmToCopy.numOfLegs;

		this.difficultyScore = qmToCopy.difficultyScore;
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

		this.minNumOfAlternativeDuties = Integer.MAX_VALUE;
		this.minNumOfAlternativeDutiesWoDh = Integer.MAX_VALUE;
		this.maxNumOfAlternativeDuties = 0;
		this.maxNumOfAlternativeDutiesWoDh = 0;
		this.totalNumOfAlternativeDuties = 0;
		this.totalNumOfAlternativeDutiesWoDh = 0;

		this.minNumOfAlternativeEffectiveDuties = Integer.MAX_VALUE;
		this.minNumOfAlternativeEffectiveDutiesWoDh = Integer.MAX_VALUE;
		this.maxNumOfAlternativeEffectiveDuties = 0;
		this.maxNumOfAlternativeEffectiveDutiesWoDh = 0;
		this.totalNumOfAlternativeEffectiveDuties = 0;
		this.totalNumOfAlternativeEffectiveDutiesWoDh = 0;

		this.numOfDuties = 0;
		this.numOfLegs = 0;

		this.difficultyScore = 0.0;
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
		this.activeBlocktimeInMins += (d.getBlockTimeInMinsActive() - dp.blockTimeOfCoveringsActive);

		this.histMinNumOfAlternativeDuties[this.numOfDuties] = this.minNumOfAlternativeDuties;
		this.histMinNumOfAlternativeDutiesWoDh[this.numOfDuties] = this.minNumOfAlternativeDutiesWoDh;
		this.histMaxNumOfAlternativeDuties[this.numOfDuties] = this.maxNumOfAlternativeDuties;
		this.histMaxNumOfAlternativeDutiesWoDh[this.numOfDuties] = this.maxNumOfAlternativeDutiesWoDh;
		if (this.minNumOfAlternativeDuties > d.getMinNumOfAlternativeDuties())
			this.minNumOfAlternativeDuties = d.getMinNumOfAlternativeDuties();
		if (this.minNumOfAlternativeDutiesWoDh > dp.minNumOfAlternativeDutiesWoDh)
			this.minNumOfAlternativeDutiesWoDh = dp.minNumOfAlternativeDutiesWoDh;
		if (this.maxNumOfAlternativeDuties < d.getMaxNumOfAlternativeDuties())
			this.maxNumOfAlternativeDuties = d.getMaxNumOfAlternativeDuties();
		if (this.maxNumOfAlternativeDutiesWoDh < dp.maxNumOfAlternativeDutiesWoDh)
			this.maxNumOfAlternativeDutiesWoDh = dp.maxNumOfAlternativeDutiesWoDh;
		this.totalNumOfAlternativeDuties += d.getTotalNumOfAlternativeDuties();
		this.totalNumOfAlternativeDutiesWoDh += dp.totalNumOfAlternativeDutiesWoDh;

		this.histMinNumOfAlternativeEffectiveDuties[this.numOfDuties] = this.minNumOfAlternativeEffectiveDuties;
		this.histMinNumOfAlternativeEffectiveDutiesWoDh[this.numOfDuties] = this.minNumOfAlternativeEffectiveDutiesWoDh;
		this.histMaxNumOfAlternativeEffectiveDuties[this.numOfDuties] = this.maxNumOfAlternativeEffectiveDuties;
		this.histMaxNumOfAlternativeEffectiveDutiesWoDh[this.numOfDuties] = this.maxNumOfAlternativeEffectiveDutiesWoDh;
		if (this.minNumOfAlternativeEffectiveDuties > d.getMinNumOfAlternativeEffectiveDuties())
			this.minNumOfAlternativeEffectiveDuties = d.getMinNumOfAlternativeEffectiveDuties();
		if (this.minNumOfAlternativeEffectiveDutiesWoDh > dp.minNumOfAlternativeEffectiveDutiesWoDh)
			this.minNumOfAlternativeEffectiveDutiesWoDh = dp.minNumOfAlternativeEffectiveDutiesWoDh;
		if (this.maxNumOfAlternativeEffectiveDuties < d.getMaxNumOfAlternativeEffectiveDuties())
			this.maxNumOfAlternativeEffectiveDuties = d.getMaxNumOfAlternativeEffectiveDuties();
		if (this.maxNumOfAlternativeEffectiveDutiesWoDh < dp.maxNumOfAlternativeEffectiveDutiesWoDh)
			this.maxNumOfAlternativeEffectiveDutiesWoDh = dp.maxNumOfAlternativeEffectiveDutiesWoDh;
		this.totalNumOfAlternativeEffectiveDuties += d.getTotalNumOfAlternativeEffectiveDuties();
		this.totalNumOfAlternativeEffectiveDutiesWoDh += dp.totalNumOfAlternativeEffectiveDutiesWoDh;

		this.numOfDuties++;
		this.numOfLegs += d.getNumOfLegs();

		this.difficultyScore += dp.getDifficultyScoreOfTheLeg();
	}


	public void addToQualityMetricBw(DutyView d,
										DutyState dp) {
		this.numOfDh += (d.getNumOfLegsPassive() + dp.numOfCoverings);
		this.dhDurationInMins += (d.getBlockTimeInMinsPassive() + dp.blockTimeOfCoverings);
		this.activeBlocktimeInMins += (d.getBlockTimeInMinsActive() - dp.blockTimeOfCoveringsActive);

		this.histMinNumOfAlternativeDuties[this.numOfDuties] = this.minNumOfAlternativeDuties;
		this.histMinNumOfAlternativeDutiesWoDh[this.numOfDuties] = this.minNumOfAlternativeDutiesWoDh;
		this.histMaxNumOfAlternativeDuties[this.numOfDuties] = this.maxNumOfAlternativeDuties;
		this.histMaxNumOfAlternativeDutiesWoDh[this.numOfDuties] = this.maxNumOfAlternativeDutiesWoDh;
		if (this.minNumOfAlternativeDuties > d.getMinNumOfAlternativeDuties())
			this.minNumOfAlternativeDuties = d.getMinNumOfAlternativeDuties();
		if (this.minNumOfAlternativeDutiesWoDh > dp.minNumOfAlternativeDutiesWoDh)
			this.minNumOfAlternativeDutiesWoDh = dp.minNumOfAlternativeDutiesWoDh;
		if (this.maxNumOfAlternativeDuties < d.getMaxNumOfAlternativeDuties())
			this.maxNumOfAlternativeDuties = d.getMaxNumOfAlternativeDuties();
		if (this.maxNumOfAlternativeDutiesWoDh < dp.maxNumOfAlternativeDutiesWoDh)
			this.maxNumOfAlternativeDutiesWoDh = dp.maxNumOfAlternativeDutiesWoDh;
		this.totalNumOfAlternativeDuties += d.getTotalNumOfAlternativeDuties();
		this.totalNumOfAlternativeDutiesWoDh += dp.totalNumOfAlternativeDutiesWoDh;

		this.histMinNumOfAlternativeEffectiveDuties[this.numOfDuties] = this.minNumOfAlternativeEffectiveDuties;
		this.histMinNumOfAlternativeEffectiveDutiesWoDh[this.numOfDuties] = this.minNumOfAlternativeEffectiveDutiesWoDh;
		this.histMaxNumOfAlternativeEffectiveDuties[this.numOfDuties] = this.maxNumOfAlternativeEffectiveDuties;
		this.histMaxNumOfAlternativeEffectiveDutiesWoDh[this.numOfDuties] = this.maxNumOfAlternativeEffectiveDutiesWoDh;
		if (this.minNumOfAlternativeEffectiveDuties > d.getMinNumOfAlternativeEffectiveDuties())
			this.minNumOfAlternativeEffectiveDuties = d.getMinNumOfAlternativeEffectiveDuties();
		if (this.minNumOfAlternativeEffectiveDutiesWoDh > dp.minNumOfAlternativeEffectiveDutiesWoDh)
			this.minNumOfAlternativeEffectiveDutiesWoDh = dp.minNumOfAlternativeEffectiveDutiesWoDh;
		if (this.maxNumOfAlternativeEffectiveDuties < d.getMaxNumOfAlternativeEffectiveDuties())
			this.maxNumOfAlternativeEffectiveDuties = d.getMaxNumOfAlternativeEffectiveDuties();
		if (this.maxNumOfAlternativeEffectiveDutiesWoDh < dp.maxNumOfAlternativeEffectiveDutiesWoDh)
			this.maxNumOfAlternativeEffectiveDutiesWoDh = dp.maxNumOfAlternativeEffectiveDutiesWoDh;
		this.totalNumOfAlternativeEffectiveDuties += d.getTotalNumOfAlternativeEffectiveDuties();
		this.totalNumOfAlternativeEffectiveDutiesWoDh += dp.totalNumOfAlternativeEffectiveDutiesWoDh;

		this.numOfDuties++;
		this.numOfLegs += d.getNumOfLegs();

		this.difficultyScore += dp.getDifficultyScoreOfTheLeg();
	}

	public void addLeadingDutyQualityMetric(QualityMetric leadingDutyQm) {
		this.numOfDh += leadingDutyQm.numOfDh;
		this.dhDurationInMins += leadingDutyQm.dhDurationInMins;
		this.activeBlocktimeInMins += leadingDutyQm.activeBlocktimeInMins;

		this.histMinNumOfAlternativeDuties[this.numOfDuties] = this.minNumOfAlternativeDuties;
		this.histMinNumOfAlternativeDutiesWoDh[this.numOfDuties] = this.minNumOfAlternativeDutiesWoDh;
		this.histMaxNumOfAlternativeDuties[this.numOfDuties] = this.maxNumOfAlternativeDuties;
		this.histMaxNumOfAlternativeDutiesWoDh[this.numOfDuties] = this.maxNumOfAlternativeDutiesWoDh;
		if (this.minNumOfAlternativeDuties > leadingDutyQm.minNumOfAlternativeDuties)
			this.minNumOfAlternativeDuties = leadingDutyQm.minNumOfAlternativeDuties;
		if (this.minNumOfAlternativeDutiesWoDh > leadingDutyQm.minNumOfAlternativeDutiesWoDh)
			this.minNumOfAlternativeDutiesWoDh = leadingDutyQm.minNumOfAlternativeDutiesWoDh;
		if (this.maxNumOfAlternativeDuties < leadingDutyQm.maxNumOfAlternativeDuties)
			this.maxNumOfAlternativeDuties = leadingDutyQm.maxNumOfAlternativeDuties;
		if (this.maxNumOfAlternativeDutiesWoDh < leadingDutyQm.maxNumOfAlternativeDutiesWoDh)
			this.maxNumOfAlternativeDutiesWoDh = leadingDutyQm.maxNumOfAlternativeDutiesWoDh;
		this.totalNumOfAlternativeDuties += leadingDutyQm.totalNumOfAlternativeDuties;
		this.totalNumOfAlternativeDutiesWoDh += leadingDutyQm.totalNumOfAlternativeDutiesWoDh;

		this.histMinNumOfAlternativeEffectiveDuties[this.numOfDuties] = this.minNumOfAlternativeEffectiveDuties;
		this.histMinNumOfAlternativeEffectiveDutiesWoDh[this.numOfDuties] = this.minNumOfAlternativeEffectiveDutiesWoDh;
		this.histMaxNumOfAlternativeEffectiveDuties[this.numOfDuties] = this.maxNumOfAlternativeEffectiveDuties;
		this.histMaxNumOfAlternativeEffectiveDutiesWoDh[this.numOfDuties] = this.maxNumOfAlternativeEffectiveDutiesWoDh;
		if (this.minNumOfAlternativeEffectiveDuties > leadingDutyQm.minNumOfAlternativeEffectiveDuties)
			this.minNumOfAlternativeEffectiveDuties = leadingDutyQm.minNumOfAlternativeEffectiveDuties;
		if (this.minNumOfAlternativeEffectiveDutiesWoDh > leadingDutyQm.minNumOfAlternativeEffectiveDutiesWoDh)
			this.minNumOfAlternativeEffectiveDutiesWoDh = leadingDutyQm.minNumOfAlternativeEffectiveDutiesWoDh;
		if (this.maxNumOfAlternativeEffectiveDuties < leadingDutyQm.maxNumOfAlternativeEffectiveDuties)
			this.maxNumOfAlternativeEffectiveDuties = leadingDutyQm.maxNumOfAlternativeEffectiveDuties;
		if (this.maxNumOfAlternativeEffectiveDutiesWoDh < leadingDutyQm.maxNumOfAlternativeEffectiveDutiesWoDh)
			this.maxNumOfAlternativeEffectiveDutiesWoDh = leadingDutyQm.maxNumOfAlternativeEffectiveDutiesWoDh;
		this.totalNumOfAlternativeEffectiveDuties += leadingDutyQm.totalNumOfAlternativeEffectiveDuties;
		this.totalNumOfAlternativeEffectiveDutiesWoDh += leadingDutyQm.totalNumOfAlternativeEffectiveDutiesWoDh;
		/*
		 * TODO Can we use ++ here ?
		 */
		this.numOfDuties++;	//	+= qmToAdd.numOfDuties;
		this.numOfLegs += leadingDutyQm.numOfLegs;

		this.difficultyScore += leadingDutyQm.difficultyScore;
	}

	public void removeLastDutyQualityMetric(DutyView d,
										DutyState dp) {
		this.numOfDh -= (d.getNumOfLegsPassive() + dp.numOfCoverings);
		this.dhDurationInMins -= (d.getBlockTimeInMinsPassive() + dp.blockTimeOfCoverings);
		this.activeBlocktimeInMins -= (d.getBlockTimeInMinsActive() - dp.blockTimeOfCoveringsActive);
		this.numOfDuties--;
		this.numOfLegs -= d.getNumOfLegs();

		this.minNumOfAlternativeDuties = this.histMinNumOfAlternativeDuties[this.numOfDuties];
		this.minNumOfAlternativeDutiesWoDh = this.histMinNumOfAlternativeDutiesWoDh[this.numOfDuties];
		this.maxNumOfAlternativeDuties = this.histMaxNumOfAlternativeDuties[this.numOfDuties];
		this.maxNumOfAlternativeDutiesWoDh = this.histMaxNumOfAlternativeDutiesWoDh[this.numOfDuties];
		this.totalNumOfAlternativeDuties -= d.getTotalNumOfAlternativeDuties();
		this.totalNumOfAlternativeDutiesWoDh -= dp.totalNumOfAlternativeDutiesWoDh;

		this.minNumOfAlternativeEffectiveDuties = this.histMinNumOfAlternativeEffectiveDuties[this.numOfDuties];
		this.minNumOfAlternativeEffectiveDutiesWoDh = this.histMinNumOfAlternativeEffectiveDutiesWoDh[this.numOfDuties];
		this.maxNumOfAlternativeEffectiveDuties = this.histMaxNumOfAlternativeEffectiveDuties[this.numOfDuties];
		this.maxNumOfAlternativeEffectiveDutiesWoDh = this.histMaxNumOfAlternativeEffectiveDutiesWoDh[this.numOfDuties];
		this.totalNumOfAlternativeEffectiveDuties -= d.getTotalNumOfAlternativeEffectiveDuties();
		this.totalNumOfAlternativeEffectiveDutiesWoDh -= dp.totalNumOfAlternativeEffectiveDutiesWoDh;

		this.difficultyScore -= dp.getDifficultyScoreOfTheLeg();
	}

	public void removeLeadingDutyQualityMetric(QualityMetric leadingDutyQm) {
		this.numOfDh -= leadingDutyQm.numOfDh;
		this.dhDurationInMins -= leadingDutyQm.dhDurationInMins;
		this.activeBlocktimeInMins -= leadingDutyQm.activeBlocktimeInMins;
		/*
		 * TODO Can we use -- here ?
		 */
		this.numOfDuties--;
		this.numOfLegs -= leadingDutyQm.numOfLegs;

		this.minNumOfAlternativeDuties = this.histMinNumOfAlternativeDuties[this.numOfDuties];
		this.minNumOfAlternativeDutiesWoDh = this.histMinNumOfAlternativeDutiesWoDh[this.numOfDuties];
		this.maxNumOfAlternativeDuties = this.histMaxNumOfAlternativeDuties[this.numOfDuties];
		this.maxNumOfAlternativeDutiesWoDh = this.histMaxNumOfAlternativeDutiesWoDh[this.numOfDuties];
		this.totalNumOfAlternativeDuties -= leadingDutyQm.totalNumOfAlternativeDuties;
		this.totalNumOfAlternativeDutiesWoDh -= leadingDutyQm.totalNumOfAlternativeDutiesWoDh;

		this.minNumOfAlternativeEffectiveDuties = this.histMinNumOfAlternativeEffectiveDuties[this.numOfDuties];
		this.minNumOfAlternativeEffectiveDutiesWoDh = this.histMinNumOfAlternativeEffectiveDutiesWoDh[this.numOfDuties];
		this.maxNumOfAlternativeEffectiveDuties = this.histMaxNumOfAlternativeEffectiveDuties[this.numOfDuties];
		this.maxNumOfAlternativeEffectiveDutiesWoDh = this.histMaxNumOfAlternativeEffectiveDutiesWoDh[this.numOfDuties];
		this.totalNumOfAlternativeEffectiveDuties -= leadingDutyQm.totalNumOfAlternativeEffectiveDuties;
		this.totalNumOfAlternativeEffectiveDutiesWoDh -= leadingDutyQm.totalNumOfAlternativeEffectiveDutiesWoDh;

		this.difficultyScore -= leadingDutyQm.difficultyScore;
	}

	public boolean isBetterThan(int heuristicNo, QualityMetric qm) {
		if ((qm.numOfLegs == 0)
				|| (this.numOfDh < qm.numOfDh)
				|| ((this.numOfDh == qm.numOfDh) && (this.totalNumOfAlternativeDutiesWoDh < qm.totalNumOfAlternativeDutiesWoDh))

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

	public boolean doesItWorthToGoDeeper(int maxDutyBlockTimeInMins,
											int heuristicNo,
											int currentDept,
											QualityMetric bestQualSoFar) {
		if ((bestQualSoFar == null)
				|| (bestQualSoFar.numOfDh > this.numOfDh)
				|| ((bestQualSoFar.numOfDh == this.numOfDh) && (bestQualSoFar.totalNumOfAlternativeDutiesWoDh > this.totalNumOfAlternativeDutiesWoDh))

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
			|| (numOfDuties > 0)
			|| (minNumOfAlternativeDuties < Integer.MAX_VALUE)
			|| (minNumOfAlternativeDutiesWoDh < Integer.MAX_VALUE)
			|| (maxNumOfAlternativeDuties > 0)
			|| (maxNumOfAlternativeDutiesWoDh > 0)
			|| (totalNumOfAlternativeDuties < 0)
			|| (totalNumOfAlternativeDutiesWoDh > 0)
			|| (minNumOfAlternativeEffectiveDuties < Integer.MAX_VALUE)
			|| (minNumOfAlternativeEffectiveDutiesWoDh < Integer.MAX_VALUE)
			|| (maxNumOfAlternativeEffectiveDuties > 0)
			|| (maxNumOfAlternativeEffectiveDutiesWoDh > 0)
			|| (totalNumOfAlternativeEffectiveDuties < 0)
			|| (totalNumOfAlternativeEffectiveDutiesWoDh > 0)
			|| (numOfLegs > 0)
			|| (difficultyScore > 0.0));
	}

	public boolean isTheSame(QualityMetric qmToCompare) {
		return (this.numOfDh == qmToCompare.numOfDh)
				&& (this.dhDurationInMins == qmToCompare.dhDurationInMins)
				&& (this.activeBlocktimeInMins == qmToCompare.activeBlocktimeInMins)
				&& (this.numOfDuties == qmToCompare.numOfDuties)
				&& (this.minNumOfAlternativeDuties == qmToCompare.minNumOfAlternativeDuties)
				&& (this.minNumOfAlternativeDutiesWoDh == qmToCompare.minNumOfAlternativeDutiesWoDh)
				&& (this.maxNumOfAlternativeDuties == qmToCompare.maxNumOfAlternativeDuties)
				&& (this.maxNumOfAlternativeDutiesWoDh == qmToCompare.maxNumOfAlternativeDutiesWoDh)
				&& (this.totalNumOfAlternativeDuties == qmToCompare.totalNumOfAlternativeDuties)
				&& (this.totalNumOfAlternativeDutiesWoDh == qmToCompare.totalNumOfAlternativeDutiesWoDh)
				&& (this.minNumOfAlternativeEffectiveDuties == qmToCompare.minNumOfAlternativeEffectiveDuties)
				&& (this.minNumOfAlternativeEffectiveDutiesWoDh == qmToCompare.minNumOfAlternativeEffectiveDutiesWoDh)
				&& (this.maxNumOfAlternativeEffectiveDuties == qmToCompare.maxNumOfAlternativeEffectiveDuties)
				&& (this.maxNumOfAlternativeEffectiveDutiesWoDh == qmToCompare.maxNumOfAlternativeEffectiveDutiesWoDh)
				&& (this.totalNumOfAlternativeEffectiveDuties == qmToCompare.totalNumOfAlternativeEffectiveDuties)
				&& (this.totalNumOfAlternativeEffectiveDutiesWoDh == qmToCompare.totalNumOfAlternativeEffectiveDutiesWoDh)
				&& (this.numOfLegs == qmToCompare.numOfLegs)
				&& (this.difficultyScore == qmToCompare.difficultyScore);
	}

	public boolean hasTheSameValues(int minNumOfAlternativeDuties,
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
									double totalDifficultyScore) {
		return (this.minNumOfAlternativeDuties == minNumOfAlternativeDuties)
				&& (this.minNumOfAlternativeDutiesWoDh == minNumOfAlternativeDutiesWoDh)
				&& (this.maxNumOfAlternativeDuties == maxNumOfAlternativeDuties)
				&& (this.maxNumOfAlternativeDutiesWoDh == maxNumOfAlternativeDutiesWoDh)
				&& (this.totalNumOfAlternativeDuties == totalNumOfAlternativeDuties)
				&& (this.totalNumOfAlternativeDutiesWoDh == totalNumOfAlternativeDutiesWoDh)
				&& (this.minNumOfAlternativeEffectiveDuties == minNumOfAlternativeEffectiveDuties)
				&& (this.minNumOfAlternativeEffectiveDutiesWoDh == minNumOfAlternativeEffectiveDutiesWoDh)
				&& (this.maxNumOfAlternativeEffectiveDuties == maxNumOfAlternativeEffectiveDuties)
				&& (this.maxNumOfAlternativeEffectiveDutiesWoDh == maxNumOfAlternativeEffectiveDutiesWoDh)
				&& (this.totalNumOfAlternativeEffectiveDuties == totalNumOfAlternativeEffectiveDuties)
				&& (this.totalNumOfAlternativeEffectiveDutiesWoDh == totalNumOfAlternativeEffectiveDutiesWoDh)
				&& (this.difficultyScore == totalDifficultyScore);
	}

	public String toString() {
		return "#DH:" + numOfDh + "/" + dhDurationInMins +
				", ABT:" + activeBlocktimeInMins + "/" + numOfDuties + "=" + (Math.floor((100.0 * activeBlocktimeInMins) / numOfDuties) / 100.0) +
				", #AltDMin:" + minNumOfAlternativeDuties +
				", #AltDMinWoDH:" + minNumOfAlternativeDutiesWoDh +
				", #AltDMax:" + maxNumOfAlternativeDuties +
				", #AltDMaxWoDH:" + maxNumOfAlternativeDutiesWoDh +
				", #AltD:" + totalNumOfAlternativeDuties +
				", #AltDWoDH:" + totalNumOfAlternativeDutiesWoDh +
				", #AltEftDMin:" + minNumOfAlternativeEffectiveDuties +
				", #AltEftDMinWoDH:" + minNumOfAlternativeEffectiveDutiesWoDh +
				", #AltEftDMax:" + maxNumOfAlternativeEffectiveDuties +
				", #AltEftDMaxWoDH:" + maxNumOfAlternativeEffectiveDutiesWoDh +
				", #AltEftD:" + totalNumOfAlternativeEffectiveDuties +
				", #AltEftDWoDH:" + totalNumOfAlternativeEffectiveDutiesWoDh +
				", DiffScore: " + difficultyScore;
	}
}
