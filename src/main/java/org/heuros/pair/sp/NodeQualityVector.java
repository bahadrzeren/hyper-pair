package org.heuros.pair.sp;

import org.heuros.data.model.Duty;
import org.heuros.pair.heuro.state.DutyState;

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
		this.nodeQuals[0] = new NodeQualityMetric(this, hbArrDutyQm);
	}

	public NodeQualityVector(int maxPairingLengthInDays, Duty hbArrDuty, DutyState dp) {
		this.nodeOwner = hbArrDuty;
		this.nodeQuals = new NodeQualityMetric[maxPairingLengthInDays];
		this.nodeQuals[0] = new NodeQualityMetric(this, dp);
	}

	/*
	 * Only for non HB arrival duty nodes!
	 */
	public NodeQualityVector(int maxPairingLengthInDays, Duty nonHbArrDuty, DutyState dp, NodeQualityVector nextNodeQv) {
		this.nodeOwner = nonHbArrDuty;
		this.nodeOwnerQm = new QualityMetric(this.nodeOwner, dp);
		this.nodeQuals = new NodeQualityMetric[maxPairingLengthInDays];
		for (int i = 1; i < this.nodeQuals.length; i++) {
			if (nextNodeQv.nodeQuals[i - 1] != null) {
				this.nodeQuals[i] = new NodeQualityMetric(this, nextNodeQv.nodeQuals[i - 1].getQual());
				this.nodeQuals[i].getQual().addLeadingDutyQualityMetric(this.nodeOwnerQm);
				this.nodeQuals[i].setNextNodeMetric(nextNodeQv.nodeQuals[i - 1]);
				/*
				 * Sets previous path.
				 * Checks if new connection is better.
				 */
//				if (nextNodeQv.nodeQuals[i - 1].getPrevNodeMetric() == null)
//					nextNodeQv.nodeQuals[i - 1].setPrevNodeMetric(this.nodeQuals[i]);
//				else
//					if (this.nodeQuals[i].getQual().isBetterThan(heuristicNo, nextNodeQv.nodeQuals[i - 1].getPrevNodeMetric().getQual())) {
//						nextNodeQv.nodeQuals[i - 1].setPrevNodeMetric(this.nodeQuals[i]);
//					}
			}
		}
	}

	public Duty getNodeOwner() {
		return nodeOwner;
	}

	public QualityMetric getNodeOwnerQm() {
		return nodeOwnerQm;
	}

	public NodeQualityMetric[] getQuals() {
		return this.nodeQuals;
	}

	/*
	 * Compare quality and modify the vector if necessary!
	 */
	public boolean checkAndMerge(NodeQualityVector nextNodeQv) {
		boolean res = false;
		for (int i = 1; i < this.nodeQuals.length; i++) {
			if ((this.nodeQuals[i] != null) || (nextNodeQv.nodeQuals[i - 1] != null)) {
				if (this.nodeQuals[i] == null) {
					this.nodeQuals[i] = new NodeQualityMetric(this, nextNodeQv.nodeQuals[i - 1].getQual());
					this.nodeQuals[i].getQual().addLeadingDutyQualityMetric(this.nodeOwnerQm);
					this.nodeQuals[i].setNextNodeMetric(nextNodeQv.nodeQuals[i - 1]);

					if (nextNodeQv.nodeQuals[i - 1].getPrevNodeMetric() == null)
						nextNodeQv.nodeQuals[i - 1].setPrevNodeMetric(this.nodeQuals[i]);
					else
						if (this.nodeQuals[i].getQual().isBetterThan(nextNodeQv.nodeQuals[i - 1].getPrevNodeMetric().getQual())) {
							nextNodeQv.nodeQuals[i - 1].setPrevNodeMetric(this.nodeQuals[i]);
						}
				} else
					if (nextNodeQv.nodeQuals[i - 1] != null) {
						nextNodeQv.nodeQuals[i - 1].getQual().addLeadingDutyQualityMetric(this.nodeOwnerQm);
						if (nextNodeQv.nodeQuals[i - 1].getQual().isBetterThan(this.nodeQuals[i].getQual())) {
							this.nodeQuals[i].getQual().injectValues(nextNodeQv.nodeQuals[i - 1].getQual());
							this.nodeQuals[i].setNextNodeMetric(nextNodeQv.nodeQuals[i - 1]);

							/*
							 * Sets previous path.
							 * Checks if new connection is better.
							 * And updates all nodes till the leading hbDep node.
							 */
//							nextNodeQv.nodeQuals[i - 1].setPrevNodeMetric(this.nodeQuals[i]);
//							if (this.nodeQuals[i].getPrevNodeMetric() != null) {
//								NodeQualityMetric prevNqm = this.nodeQuals[i].getPrevNodeMetric();
//								while (prevNqm != null) {
//									prevNqm.getQual().reset();
//									prevNqm.getQual().injectValues(prevNqm.getNextNodeMetric().getQual());
//									prevNqm.getQual().addLeadingDutyQualityMetric(prevNqm.getParent().getNodeOwnerQm());
//									prevNqm = prevNqm.getPrevNodeMetric();
//								}
//							}

							res = true;
						}
						nextNodeQv.nodeQuals[i - 1].getQual().removeLeadingDutyQualityMetric(this.nodeOwnerQm);
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
