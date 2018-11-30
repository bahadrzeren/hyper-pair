package org.heuros.hyperpair.heuristic;

import org.heuros.data.model.DutyView;

public class NodeQualityMetric implements Cloneable {
	/*
	 * Deadhead
	 */
	public int numOfDh = 0;
	public int dhDurationInMins = 0;
	/*
	 * Dutyday
	 */
	public int activeBlocktimeInMins = 0;
	public int numOfDuties = 0;
	/*
	 * The last metric to check if others are equal!
	 */
	public int numOfIncludingDutiesOfTheSameLegs = 0;
	public int numOfLegs = 0;

//	public Duty nodeOwner = null;
//	public NodeQualityMetric nextNodeMetric = null;
//	public NodeQualityMetric prevNodeMetric = null;

	@Override
    public Object clone() throws CloneNotSupportedException {
		return (NodeQualityMetric) super.clone();
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

	public void addToQualityMetric(NodeQualityMetric dqm) {
		this.numOfDh += dqm.numOfDh;
		this.dhDurationInMins += dqm.dhDurationInMins;
		this.activeBlocktimeInMins += dqm.activeBlocktimeInMins;
		this.numOfDuties += dqm.numOfDuties;
		this.numOfIncludingDutiesOfTheSameLegs += dqm.numOfIncludingDutiesOfTheSameLegs;
		this.numOfLegs += dqm.numOfLegs;
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

	public void removeFromQualityMetric(NodeQualityMetric dqm) {
		this.numOfDh -= dqm.numOfDh;
		this.dhDurationInMins -= dqm.dhDurationInMins;
		this.activeBlocktimeInMins -= dqm.activeBlocktimeInMins;
		this.numOfDuties -= dqm.numOfDuties;
		this.numOfIncludingDutiesOfTheSameLegs -= dqm.numOfIncludingDutiesOfTheSameLegs;
		this.numOfLegs -= dqm.numOfLegs;
	}

	public boolean isBetterThan(int heuristicNo, NodeQualityMetric qm) {
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

	public boolean doesItWorthToGoDeeper(int heuristicNo,
											NodeQualityMetric fwCumulative,
											DutyView d,
											int[] numOfCoveringsInDuties,
											int[] blockTimeOfCoveringsInDuties) {
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


	public void injectValues(NodeQualityMetric qm) {
		this.numOfDh = qm.numOfDh;
		this.dhDurationInMins = qm.dhDurationInMins;
		this.activeBlocktimeInMins = qm.activeBlocktimeInMins;
		this.numOfDuties = qm.numOfDuties;
		this.numOfIncludingDutiesOfTheSameLegs = qm.numOfIncludingDutiesOfTheSameLegs;
		this.numOfLegs = qm.numOfLegs;
	}

//	public void injectValues(DutyView d,
//								int[] numOfCoveringsInDuties,
//								int[] blockTimeOfCoveringsInDuties) {
//		this.numOfDh = d.getNumOfLegsPassive() + numOfCoveringsInDuties[d.getNdx()];
//		this.dhDurationInMins = d.getBlockTimeInMinsPassive() + blockTimeOfCoveringsInDuties[d.getNdx()];
//		this.activeBlocktimeInMins = d.getBlockTimeInMinsActive() - blockTimeOfCoveringsInDuties[d.getNdx()];
//		this.numOfDuties = 1;
//		this.numOfIncludingDutiesOfTheSameLegs = d.getTotalNumOfIncludingDutiesOfTheSameLegs();
//		this.numOfLegs = d.getNumOfLegs();
//	}

	public static NodeQualityMetric generateQualityMetric(DutyView d,
															int[] numOfCoveringsInDuties,
															int[] blockTimeOfCoveringsInDuties) {
		NodeQualityMetric qm = new NodeQualityMetric();
		qm.numOfDh = d.getNumOfLegsPassive() + numOfCoveringsInDuties[d.getNdx()];
		qm.dhDurationInMins = d.getBlockTimeInMinsPassive() + blockTimeOfCoveringsInDuties[d.getNdx()];
		qm.activeBlocktimeInMins = d.getBlockTimeInMinsActive() - blockTimeOfCoveringsInDuties[d.getNdx()];
		qm.numOfDuties = 1;
		qm.numOfIncludingDutiesOfTheSameLegs = d.getTotalNumOfIncludingDutiesOfTheSameLegs();
		qm.numOfLegs = d.getNumOfLegs();
		return qm;
	}

	public String toString() {
		return "#DH:" + numOfDh + ", DHD:" + dhDurationInMins +
				", ABT/#D:" + activeBlocktimeInMins + "/" + numOfDuties +
				", #LD:" + numOfIncludingDutiesOfTheSameLegs +
				", #L:" + numOfLegs;
	}
}
