package org.heuros.hyperpair.heuristic;

import org.heuros.data.model.DutyView;

public class NodeQualityVector {

	private NodeQualityMetric nodeDutyQm = null;
	private NodeQualityMetric[] quals = null;

	/*
	 * Only for HB arrival duty nodes!
	 */
	public NodeQualityVector(int maxPairingLengthInDays, DutyView hbArrDuty, NodeQualityMetric hbArrDutyQm) {
		this.quals = new NodeQualityMetric[maxPairingLengthInDays];
		this.quals[0] = new NodeQualityMetric(hbArrDutyQm);
	}

	/*
	 * Only for HB arrival duty nodes!
	 */
	public NodeQualityVector(int maxPairingLengthInDays, DutyView hbArrDuty, int[] numOfCoveringsInDuties, int[] blockTimeOfCoveringsInDuties) {
		this.quals = new NodeQualityMetric[maxPairingLengthInDays];
		this.quals[0] = new NodeQualityMetric(hbArrDuty, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
	}

	/*
	 * Only for non HB arrival duty nodes!
	 */
	public NodeQualityVector(int maxPairingLengthInDays, DutyView nonHbArrDuty, int[] numOfCoveringsInDuties, int[] blockTimeOfCoveringsInDuties, NodeQualityVector nextQv) {
		this.nodeDutyQm = new NodeQualityMetric(nonHbArrDuty, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
		this.quals = new NodeQualityMetric[maxPairingLengthInDays];
		for (int i = 1; i < this.quals.length; i++) {
			if (nextQv.quals[i - 1] != null) {
				this.quals[i] = new NodeQualityMetric(this.nodeDutyQm);
				this.quals[i].addToQualityMetric(nextQv.quals[i - 1]);
			}
		}
	}

	/*
	 * Compare quality and modify the vector if necessary!
	 */
	public boolean checkAndMerge(int heuristicNo, NodeQualityVector nextQv) {
		boolean res = false;
		for (int i = 1; i < this.quals.length; i++) {
			if ((this.quals[i] != null) || (nextQv.quals[i - 1] != null)) {
				if (this.quals[i] == null) {
					this.quals[i] = new NodeQualityMetric(this.nodeDutyQm);
					this.quals[i].addToQualityMetric(nextQv.quals[i - 1]);
				} else
					if (nextQv.quals[i - 1] != null) {
						nextQv.quals[i - 1].addToQualityMetric(this.nodeDutyQm);
						if (nextQv.quals[i - 1].isBetterThan(heuristicNo, this.quals[i])) {
							this.quals[i].injectValues(nextQv.quals[i - 1]);
							res = true;
						}
						nextQv.quals[i - 1].removeFromQualityMetric(this.nodeDutyQm);
					}
			}
		}
		return res;
	}

	/*
	 * For fw search!
	 */
	public boolean doesItWorthToGoDeeperFw(int maxDutyBlockTimeInMins,
											int heuristicNo,
											int currentDept,
											NodeQualityMetric qmCumulative) {

		for (int i = this.quals.length - currentDept + 1; i < this.quals.length; i++) {
			if (this.quals[i] != null) {
				if (qmCumulative.doesItWorthToGoDeeper(maxDutyBlockTimeInMins, heuristicNo, currentDept, this.quals[i])) {
					return true;
				}
			} else
				return true;
		}
		return false;
	}

	/*
	 * For bw search!
	 */
	public boolean doesItWorthToGoDeeperBw(int maxDutyBlockTimeInMins,
											int heuristicNo,
											int currentDept,
											NodeQualityVector qmCumulative) {

		for (int i = 1; i < this.quals.length - currentDept + 1; i++) {
			if (qmCumulative.quals[i] != null) {
				for (int j = this.quals.length - currentDept + 1; j < this.quals.length; j++) {
					if (this.quals[j] != null) {
						if (qmCumulative.quals[i].doesItWorthToGoDeeper(maxDutyBlockTimeInMins, heuristicNo, currentDept, this.quals[j])) {
							return true;
						}
					} else
						return true;
				}
			}
		}
		return false;
	}


	public boolean hasAnyBetter(int heuristicNo, NodeQualityVector qv) {
		for (int i = 0; i < this.quals.length; i++) {
			if ((this.quals[i] != null)
					&& (qv.quals[i] != null)) {
				if (this.quals[i].isBetterThan(heuristicNo, qv.quals[i])) {
					return true;
				}
			}
		}
		return false;
	}

//	public boolean hasAllBetter(int heuristicNo, NodeQualityVector qv) {
//		for (int i = 0; i < this.quals.length; i++) {
//			if (this.quals[i] != null) {
//				if (!this.quals[i].isBetterThan(heuristicNo, qv.quals[i])) {
//					return false;
//				}
//			}			
//		}
//		return true;
//	}
}
