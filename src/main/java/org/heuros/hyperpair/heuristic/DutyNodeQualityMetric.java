package org.heuros.hyperpair.heuristic;

import org.heuros.data.model.DutyView;

public class DutyNodeQualityMetric {
	public int maxRhsDept = 0;
	/*
	 * Deadhead
	 */
	public int numOfDh = Integer.MAX_VALUE;
	public int dhDurationInMins = Integer.MAX_VALUE;
	/*
	 * Dutyday
	 */
	public int activeBlocktimeInMins = 0;
	public int numOfDuties = 0;
	/*
	 * The last metric to check if others are equal!
	 */
	public int numOfIncludingDutiesOfTheSameLegs = Integer.MAX_VALUE;
	public int numOfLegs = Integer.MAX_VALUE;

//	@Override
//    public Object clone() throws CloneNotSupportedException {
//		QualityMetric qm = (QualityMetric) super.clone();
//		return qm;
//	}

	public void addToQualityMetric(DutyView d,
									int dept,
 									int[] numOfCoveringsInDuties,
									int[] blockTimeOfCoveringsInDuties) {
		this.maxRhsDept++;
		this.numOfDh += (d.getNumOfLegsPassive() + numOfCoveringsInDuties[d.getNdx()]);
		this.dhDurationInMins += (d.getBlockTimeInMinsPassive() + blockTimeOfCoveringsInDuties[d.getNdx()]);
		this.activeBlocktimeInMins += (d.getBlockTimeInMinsActive() - blockTimeOfCoveringsInDuties[d.getNdx()]);
		this.numOfDuties++;
		this.numOfIncludingDutiesOfTheSameLegs += d.getTotalNumOfIncludingDutiesOfTheSameLegs();
		this.numOfLegs += d.getNumOfLegs();
	}

	public void removeFromQualityMetric(DutyView d,
										int dept,
										int[] numOfCoveringsInDuties,
										int[] blockTimeOfCoveringsInDuties) {
		this.maxRhsDept--;
		this.numOfDh -= (d.getNumOfLegsPassive() + numOfCoveringsInDuties[d.getNdx()]);
		this.dhDurationInMins -= (d.getBlockTimeInMinsPassive() + blockTimeOfCoveringsInDuties[d.getNdx()]);
		this.activeBlocktimeInMins -= (d.getBlockTimeInMinsActive() - blockTimeOfCoveringsInDuties[d.getNdx()]);
		this.numOfDuties--;
		this.numOfIncludingDutiesOfTheSameLegs -= d.getTotalNumOfIncludingDutiesOfTheSameLegs();
		this.numOfLegs -= d.getNumOfLegs();
	}

	public boolean isBetterThan(int heuristicNo, DutyNodeQualityMetric pqm) {
		DutyNodeQualityMetric qm = (DutyNodeQualityMetric) pqm;
		if (heuristicNo < 2) {	//	If layover or dh effective. 
			if ((this.numOfDh < qm.numOfDh)
					|| ((this.numOfDh == qm.numOfDh) && (this.dhDurationInMins < qm.dhDurationInMins))
					|| ((this.numOfDh == qm.numOfDh) && (this.dhDurationInMins == qm.dhDurationInMins) 
							&& (((1.0 * this.numOfIncludingDutiesOfTheSameLegs) / this.numOfLegs) < ((1.0 * qm.numOfIncludingDutiesOfTheSameLegs) / qm.numOfLegs)))) {
				return true;
			} else
				return false;
		} else {	//	If active block time effective.
			if ((((1.0 * this.activeBlocktimeInMins) / this.numOfDuties) > ((1.0 * qm.activeBlocktimeInMins) / qm.numOfDuties))
					|| ((((1.0 * this.activeBlocktimeInMins) / this.numOfDuties) == ((1.0 * qm.activeBlocktimeInMins) / qm.numOfDuties))
							&& (((1.0 * this.numOfIncludingDutiesOfTheSameLegs) / this.numOfLegs) < ((1.0 * qm.numOfIncludingDutiesOfTheSameLegs) / qm.numOfLegs)))) {
				return true;
			} else
				return false;
		}
	}

	public void injectValues(DutyNodeQualityMetric pqm) {
		DutyNodeQualityMetric qm = (DutyNodeQualityMetric) pqm;
		this.maxRhsDept = qm.maxRhsDept;
		this.numOfDh = qm.numOfDh;
		this.dhDurationInMins = qm.dhDurationInMins;
		this.activeBlocktimeInMins = qm.activeBlocktimeInMins;
		this.numOfDuties = qm.numOfDuties;
		this.numOfIncludingDutiesOfTheSameLegs = qm.numOfIncludingDutiesOfTheSameLegs;
		this.numOfLegs = qm.numOfLegs;
	}

	public void injectValues(DutyView d,
								int dept,
								int[] numOfCoveringsInDuties,
								int[] blockTimeOfCoveringsInDuties) {
		this.maxRhsDept = dept;
		this.numOfDh = d.getNumOfLegsPassive() + numOfCoveringsInDuties[d.getNdx()];
		this.dhDurationInMins = d.getBlockTimeInMinsPassive() + blockTimeOfCoveringsInDuties[d.getNdx()];
		this.activeBlocktimeInMins = d.getBlockTimeInMinsActive() - blockTimeOfCoveringsInDuties[d.getNdx()];
		this.numOfDuties = 1;
		this.numOfIncludingDutiesOfTheSameLegs = d.getTotalNumOfIncludingDutiesOfTheSameLegs();
		this.numOfLegs = d.getNumOfLegs();
	}

//	public static QualityMetric calculateQualityMetric(DutyView d,
//														int[] numOfCoveringsInDuties,
//														int[] blockTimeOfCoveringsInDuties) {
//		QualityMetric qm = new QualityMetric();
//		qm.dept = 0;
//		qm.numOfDh = d.getNumOfLegsPassive() + numOfCoveringsInDuties[d.getNdx()];
//		qm.dhDurationInMins = d.getBlockTimeInMinsPassive() + blockTimeOfCoveringsInDuties[d.getNdx()];
//		qm.activeBlocktimeInMins = d.getBlockTimeInMinsActive() - blockTimeOfCoveringsInDuties[d.getNdx()];
//		qm.numOfDuties = 1;
//		qm.numOfIncludingDutiesOfTheSameLegs = d.getTotalNumOfIncludingDutiesOfTheSameLegs();
//		qm.numOfLegs = d.getNumOfLegs();
//		return qm;
//	}
}