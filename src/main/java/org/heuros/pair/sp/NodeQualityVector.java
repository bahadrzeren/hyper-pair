package org.heuros.pair.sp;

import org.heuros.data.model.Duty;
import org.heuros.pair.heuro.DutyParam;

public class NodeQualityVector {

	private Duty nodeOwner = null;
	private QualityMetric nodeOwnerQm = null;
	private NodeQualityMetric[] nodeQuals = null;

	/*
	 * Only for HB arrival duty nodes!
	 */
	public NodeQualityVector(int maxPairingLengthInDays, Duty hbArrDuty, QualityMetric hbArrDutyQm) {
		this.nodeOwner = hbArrDuty;
		this.nodeQuals = new NodeQualityMetric[maxPairingLengthInDays];
		this.nodeQuals[0] = new NodeQualityMetric(this.nodeOwner, hbArrDutyQm, null);
	}

	public NodeQualityVector(int maxPairingLengthInDays, Duty hbArrDuty, DutyParam dp) {
		this.nodeOwner = hbArrDuty;
		this.nodeQuals = new NodeQualityMetric[maxPairingLengthInDays];
		this.nodeQuals[0] = new NodeQualityMetric(this.nodeOwner, dp, null);
	}

	/*
	 * Only for non HB arrival duty nodes!
	 */
	public NodeQualityVector(int maxPairingLengthInDays, Duty nonHbArrDuty, DutyParam dp, NodeQualityVector nextNodeQv) {
		this.nodeOwner = nonHbArrDuty;
		this.nodeOwnerQm = new QualityMetric(this.nodeOwner, dp);
		this.nodeQuals = new NodeQualityMetric[maxPairingLengthInDays];
		for (int i = 1; i < this.nodeQuals.length; i++) {
			if (nextNodeQv.nodeQuals[i - 1] != null) {
				this.nodeQuals[i] = new NodeQualityMetric(this.nodeOwner, this.nodeOwnerQm, nextNodeQv.nodeQuals[i - 1]);
				this.nodeQuals[i].getQual().addToQualityMetric(nextNodeQv.nodeQuals[i - 1].getQual());
			}
		}
	}

	public Duty getNodeOwner() {
		return nodeOwner;
	}

	public NodeQualityMetric[] getQuals() {
		return this.nodeQuals;
	}

	/*
	 * Compare quality and modify the vector if necessary!
	 */
	public boolean checkAndMerge(int heuristicNo, NodeQualityVector nextNodeQv) {
		boolean res = false;
		for (int i = 1; i < this.nodeQuals.length; i++) {
			if ((this.nodeQuals[i] != null) || (nextNodeQv.nodeQuals[i - 1] != null)) {
				if (this.nodeQuals[i] == null) {
					this.nodeQuals[i] = new NodeQualityMetric(this.nodeOwner, this.nodeOwnerQm, nextNodeQv.nodeQuals[i - 1]);
					this.nodeQuals[i].getQual().addToQualityMetric(nextNodeQv.nodeQuals[i - 1].getQual());
				} else
					if (nextNodeQv.nodeQuals[i - 1] != null) {
						nextNodeQv.nodeQuals[i - 1].getQual().addToQualityMetric(this.nodeOwnerQm);
						if (nextNodeQv.nodeQuals[i - 1].getQual().isBetterThan(heuristicNo, this.nodeQuals[i].getQual())) {
							this.nodeQuals[i].setNextNodeMetric(nextNodeQv.nodeQuals[i - 1]);
							this.nodeQuals[i].getQual().injectValues(nextNodeQv.nodeQuals[i - 1].getQual());
							res = true;
						}
						nextNodeQv.nodeQuals[i - 1].getQual().removeFromQualityMetric(this.nodeOwnerQm);
					}
			}
		}
		return res;
	}

//	/*
//	 * For fw search!
//	 */
//	public boolean doesItWorthToGoDeeperFw(int maxDutyBlockTimeInMins,
//											int heuristicNo,
//											int currentDept,
//											NodeQualityMetric qmCumulative) {
//
//		for (int i = this.quals.length - currentDept + 1; i < this.quals.length; i++) {
//			if (this.quals[i] != null) {
//				if (qmCumulative.doesItWorthToGoDeeper(maxDutyBlockTimeInMins, heuristicNo, currentDept, this.quals[i])) {
//					return true;
//				}
//			} else
//				return true;
//		}
//		return false;
//	}
//
//	/*
//	 * For bw search!
//	 */
//	public boolean doesItWorthToGoDeeperBw(int maxDutyBlockTimeInMins,
//											int heuristicNo,
//											int currentDept,
//											NodeQualityVector qmCumulative) {
//
//		for (int i = 1; i < this.quals.length - currentDept + 1; i++) {
//			if (qmCumulative.quals[i] != null) {
//				for (int j = this.quals.length - currentDept + 1; j < this.quals.length; j++) {
//					if (this.quals[j] != null) {
//						if (qmCumulative.quals[i].doesItWorthToGoDeeper(maxDutyBlockTimeInMins, heuristicNo, currentDept, this.quals[j])) {
//							return true;
//						}
//					} else
//						return true;
//				}
//			}
//		}
//		return false;
//	}
//
//	public boolean hasAnyBetter(int heuristicNo, NodeQualityVector qv) {
//		for (int i = 0; i < this.quals.length; i++) {
//			if ((this.quals[i] != null)
//					&& (qv.quals[i] != null)) {
//				if (this.quals[i].isBetterThan(heuristicNo, qv.quals[i])) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}
//
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
