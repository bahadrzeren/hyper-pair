package org.heuros.pair.sp;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.heuros.core.data.ndx.OneDimIndexInt;
import org.heuros.core.data.ndx.OneDimUniqueIndexInt;
import org.heuros.data.DutyLegOvernightConnNetwork;
import org.heuros.data.model.Duty;
import org.heuros.data.model.Leg;
import org.heuros.pair.heuro.DutyParam;

public class NetworkExplorer {

//	private static Logger logger = Logger.getLogger(NetworkExplorer.class);

	/*
	 * TODO Single base assumption!!!
	 */
	private int hbNdx = 0;

	private List<Duty> duties = null;
	private int maxPairingLengthInDays = 0;
	private int maxDutyBlockTimeInMins = 0;

	private OneDimIndexInt<Duty> dutyIndexByDepLegNdx = null;
	private OneDimIndexInt<Duty> dutyIndexByArrLegNdx = null;
	private OneDimUniqueIndexInt<Leg> nextBriefLegIndexByDutyNdx = null;
	private OneDimUniqueIndexInt<Leg> prevDebriefLegIndexByDutyNdx = null;

	private boolean[] sourceDuties = null;

	private NodeQualityMetric[] sourceNodeQmArray = null;

	private NodeQualityVector[] bestNodeQuality = null;

	/*
	 * Statistics of the network.
	 */
	private int numOfRecursions = 0;
	private int numOfNodes = 0;
	private int numOfNodesChecked = 0;
	private int numOfNodesAdded = 0;

	private int numOfFwRecursions = 0;
	private int numOfFwNodes = 0;
	private int numOfFwNodesChecked = 0;
	private int numOfFwNodesAdded = 0;
	private int maxFwDeptReached = Integer.MAX_VALUE;

	private int numOfBwRecursions = 0;
	private int numOfBwNodes = 0;
	private int numOfBwNodesChecked = 0;
	private int numOfBwNodesAdded = 0;
	private int maxBwDeptReached = Integer.MAX_VALUE;

	public NetworkExplorer(List<Duty> duties,
								int maxPairingLengthInDays,
								int maxDutyBlockTimeInMins, 
								DutyLegOvernightConnNetwork pricingNetwork) {
		this.duties = duties;
		this.sourceDuties = new boolean[this.duties.size()];

		this.sourceNodeQmArray = new NodeQualityMetric[0];

		this.bestNodeQuality = new NodeQualityVector[this.duties.size()];

		this.maxPairingLengthInDays = maxPairingLengthInDays;
		this.maxFwDeptReached = maxPairingLengthInDays;
		this.maxBwDeptReached = maxPairingLengthInDays;
		this.maxDutyBlockTimeInMins = maxDutyBlockTimeInMins;

		this.dutyIndexByDepLegNdx = pricingNetwork.getDutyIndexByDepLegNdx();
		this.dutyIndexByArrLegNdx = pricingNetwork.getDutyIndexByArrLegNdx();
		this.nextBriefLegIndexByDutyNdx = pricingNetwork.getNextBriefLegIndexByDutyNdx();
		this.prevDebriefLegIndexByDutyNdx = pricingNetwork.getPrevDebriefLegIndexByDutyNdx();
	}

	public NodeQualityMetric[] getSourceNodeQms() {
		return this.sourceNodeQmArray;
	}

	public NodeQualityVector[] getBestNodeQuality() {
		return bestNodeQuality;
	}

	public int getNumOfRecursions() {
		return numOfRecursions;
	}

	public int getNumOfNodes() {
		return numOfNodes;
	}

	public int getNumOfNodesChecked() {
		return numOfNodesChecked;
	}

	public int getNumOfNodesAdded() {
		return numOfNodesAdded;
	}

	public int getNumOfFwRecursions() {
		return numOfFwRecursions;
	}

	public int getNumOfFwNodes() {
		return numOfFwNodes;
	}

	public int getNumOfFwNodesChecked() {
		return numOfFwNodesChecked;
	}

	public int getNumOfFwNodesAdded() {
		return numOfFwNodesAdded;
	}

	public int getMaxFwDeptReached() {
		return maxFwDeptReached;
	}

	public int getNumOfBwRecursions() {
		return numOfBwRecursions;
	}

	public int getNumOfBwNodes() {
		return numOfBwNodes;
	}

	public int getNumOfBwNodesChecked() {
		return numOfBwNodesChecked;
	}

	public int getNumOfBwNodesAdded() {
		return numOfBwNodesAdded;
	}

	public int getMaxBwDeptReached() {
		return maxBwDeptReached;
	}

	private int heuristicNo = 0;
	private DutyParam[] dps = null;

	private int[] maxSearchNumDept = null;
	private LocalDate[] maxSearchDayDept = null;
	private boolean[] hbArrFound = null;

	private boolean addSourceDuty(int heuristicNo, Duty d, boolean hasImprovement) {
		if (!this.sourceDuties[d.getNdx()]) {
			this.sourceDuties[d.getNdx()] = true;
			/*
			 * We would like to keep the best duty node at first place in order to increase performance.  
			 */
			NodeQualityVector nqv = bestNodeQuality[d.getNdx()];
			for (int i = 0; i < nqv.getQuals().length; i++) {
				NodeQualityMetric nqm = nqv.getQuals()[i];
				if (nqm != null) {
					for (int j = 0; j < this.sourceNodeQmArray.length; j++) {
						if (nqm.getQual().isBetterThan(heuristicNo, this.sourceNodeQmArray[j].getQual())) {
							NodeQualityMetric hnqm = this.sourceNodeQmArray[j];
							this.sourceNodeQmArray[j] = nqm;
							nqm = hnqm;
							for (int k = j + 1; k < this.sourceNodeQmArray.length; k++) {
								hnqm = this.sourceNodeQmArray[k];
								this.sourceNodeQmArray[k] = nqm;
								nqm = hnqm;
							}
							break;
						}
					}
					this.sourceNodeQmArray = ArrayUtils.add(this.sourceNodeQmArray, nqm);
				}
			}

			return true;
		}
//			else
//			if (hasImprovement) {
//				boolean found = false;
//				for (int i = this.sourceNodeQmArray.length - 1; i > 0; i--) {
//					if (this.sourceNodeQmArray[i].getQual().isBetterThan(heuristicNo, this.sourceNodeQmArray[i - 1].getQual())) {
//						NodeQualityMetric hqm = this.sourceNodeQmArray[i];
//						this.sourceNodeQmArray[i] = this.sourceNodeQmArray[i - 1];
//						this.sourceNodeQmArray[i - 1] = hqm;
//						found = true;
//					} else
//						if (found)
//							break;
//				}
//			}
		return false;
	}

	/**
	 * 
	 * @param rootDuties must be ordered according to (hbDep, hbArr, briefTime).
	 * 
	 */
	public NetworkExplorer build(Leg legToCover,
									Duty[] rootDuties,
									int heuristicNo,
									DutyParam[] dps) throws CloneNotSupportedException {
		this.heuristicNo = heuristicNo;
		this.dps = dps;

		/*
		 * Here there are two implementation options.
		 * 1- Prevent unnecessary deeper search on the tree by checking starting date and time.
		 * 2- Prevent re-searching on the same duty node by checking maxDept parameter.
		 * These two options can not be used at the same time. If both of them are applied at the same time, 
		 * duty nodes that would be candidates to be appended to the dept-first-search tree later could be 
		 * sealed earlier.
		 * 
		 * In the case below if 1-3-4 is successful and 1-3-5 is unsuccesful, sealing the 3th node by 2th rule
		 * will prevent adding 5th node later even though 2-3-5 is a valid pairing candidate.  
		 * 
		 * 1
		 *  \
		 *   \
		 *    \
		 *     \     4
		 *      \   /
		 *       \ /
		 *        3
		 *       / \
		 *      /   \
		 *     /     \
		 *    2       \
		 *             5
		 * 
		 */

		/*
		 * In new implementation we use both Dept control and Datetime control 
		 * with the help of using legToCover's sobt and sibt values instead of duties brief and debrief parameters.
		 *
		 * 1
		 *  \     4
		 *   \   /
		 *    \ /
		 *     3
		 *    / \
		 *   /   \
		 *  /     \
		 * 2       \
		 *          5
		 * 
		 */

		this.maxSearchNumDept = new int[this.duties.size()];
		this.maxSearchDayDept = new LocalDate[this.duties.size()];
		this.hbArrFound = new boolean[this.duties.size()];

//if (legToCover.getNdx() == 10038)
//System.out.println();

		QualityMetric cumulativeQual = new QualityMetric();

		for (Duty duty: rootDuties) {

//if ((legToCover.getNdx() == 10038)
//		&& (duty.getNdx() == 90163))
//System.out.println();

			LocalDate maxMinDateDept = null;

			if (duty.isValid(this.hbNdx)
					&& duty.hasPairing(this.hbNdx)
					&& ((sourceNodeQmArray.length == 0)
							|| (duty.getNumOfLegs() > dps[duty.getNdx()].numOfDistinctCoverings)
							|| (duty.getNumOfLegs() == 1))
					) {

				cumulativeQual.addToQualityMetric(duty, dps[duty.getNdx()]);
				if (duty.isHbDep(this.hbNdx)) {
					if (duty.isHbArr(this.hbNdx)) {
						/*
						 * Because of having no connection duties we must add qualityMetric here for 1day pairings.
						 */
						this.bestNodeQuality[duty.getNdx()] = new NodeQualityVector(maxPairingLengthInDays, duty, cumulativeQual);
						this.addSourceDuty(heuristicNo, duty, false);
						hbArrFound[duty.getNdx()] = true;
					} else 
						if (heuristicNo > 0) {
							maxMinDateDept = duty.getBriefDay(this.hbNdx).plusDays(this.maxPairingLengthInDays);
							if (this.fwNetworkSearch(duty, cumulativeQual, true, maxMinDateDept, this.maxPairingLengthInDays - 1)) {
								this.addSourceDuty(heuristicNo, duty, false);
								hbArrFound[duty.getNdx()] = true;
							}
						}
				} else
					if (heuristicNo > 0) {
						if (duty.isHbArr(this.hbNdx)) {
							maxMinDateDept = duty.getDebriefDay(this.hbNdx).minusDays(this.maxPairingLengthInDays - 1);
							this.bestNodeQuality[duty.getNdx()] = new NodeQualityVector(maxPairingLengthInDays, duty, cumulativeQual);
							if (this.bwNetworkSearch(duty, true, maxMinDateDept, this.maxPairingLengthInDays)) {
								hbArrFound[duty.getNdx()] = true;
							}
						} else {
							maxMinDateDept = duty.getBriefDay(this.hbNdx).plusDays(this.maxPairingLengthInDays - 1);
							if (this.fwNetworkSearch(duty, cumulativeQual, false, maxMinDateDept, this.maxPairingLengthInDays - 2)) {
								hbArrFound[duty.getNdx()] = true;
								maxMinDateDept = duty.getDebriefDay(this.hbNdx).minusDays(this.maxPairingLengthInDays - 2);
								/*
								 * We need to use the best quality metric that is found for the root duty so far.
								 */
								this.bwNetworkSearch(duty, false, maxMinDateDept, this.maxPairingLengthInDays - 1);
							}
						}
					}
				cumulativeQual.removeFromQualityMetric(duty, dps[duty.getNdx()]);

//if (cumulativeQual.isNotEmpty())
//	logger.error("CumulativeQual is not empty!");
			}
		}
		return this;
	}

	private boolean isNodeVisitedFw(Duty d, int dept, LocalDate maxMinDateDept) {
		return (maxSearchNumDept[d.getNdx()] > dept)
				|| ((maxSearchNumDept[d.getNdx()] == dept)
						&& (maxSearchDayDept[d.getNdx()].isEqual(maxMinDateDept)
								|| maxSearchDayDept[d.getNdx()].isAfter(maxMinDateDept)));
	}

	private void setNodeVisitedFw(Duty d, int dept, LocalDate maxMinDateDept) {
		if (maxSearchNumDept[d.getNdx()] < dept) {
			maxSearchNumDept[d.getNdx()] = dept;
			maxSearchDayDept[d.getNdx()] = maxMinDateDept;
		} else
			if ((maxSearchNumDept[d.getNdx()] == dept)
					&& (maxSearchDayDept[d.getNdx()].isBefore(maxMinDateDept))) {
				maxSearchDayDept[d.getNdx()] = maxMinDateDept;
			}
	}

	private void fwRegister(Duty pd, Duty nd) {

//if ((pd.getNdx() == 60341)
//&& (nd.getNdx() == 71683))
//System.out.println();
//if ((pd.getNdx() == 71683)
//&& (nd.getNdx() == 90163))
//System.out.println();

		/*
		 * Calculate quality metric.
		 */
		NodeQualityVector nQv = this.bestNodeQuality[nd.getNdx()];
		NodeQualityVector pQv = this.bestNodeQuality[pd.getNdx()];
		/*
		 * HB arr duty
		 */
		if (nQv == null) {
			nQv = new NodeQualityVector(maxPairingLengthInDays, nd, dps[nd.getNdx()]);
			this.bestNodeQuality[nd.getNdx()] = nQv;
		}
		/*
		 * Non HB arr duty
		 */
		if (pQv == null) {
			pQv = new NodeQualityVector(maxPairingLengthInDays, pd, dps[pd.getNdx()], nQv);
			this.bestNodeQuality[pd.getNdx()] = pQv;
		} else {
			pQv.checkAndMerge(this.heuristicNo, nQv);
		}

		hbArrFound[nd.getNdx()] = true;
		numOfNodesAdded++;
		numOfFwNodesAdded++;
	}

	private boolean fwNetworkSearch(Duty pd, QualityMetric fwCumulative, boolean hbDep, LocalDate maxMinDateDept, int dept) throws CloneNotSupportedException {

		if (dept < maxFwDeptReached)
			maxFwDeptReached = dept;
		numOfRecursions++;
		numOfFwRecursions++;
		boolean res = false;
		Leg[] nextLegs = this.nextBriefLegIndexByDutyNdx.getArray(pd.getNdx());
		for (Leg leg : nextLegs) {
			Duty[] nextDuties = this.dutyIndexByDepLegNdx.getArray(leg.getNdx());
			for (Duty nd: nextDuties) {

//if ((pd.getNdx() == 60341)
//&& (nd.getNdx() == 71683))
//System.out.println();
//if ((pd.getNdx() == 71683)
//&& (nd.getNdx() == 90163))
//System.out.println();

				numOfNodes++;
				numOfFwNodes++;
				if (nd.isValid(this.hbNdx)
						&& nd.hasPairing(this.hbNdx)
						&& (nd.isHbArr(this.hbNdx) || (dept > 1))
						&& ((sourceNodeQmArray.length == 0)
								|| (nd.getNumOfLegs() > dps[nd.getNdx()].numOfDistinctCoverings)
								|| (nd.getNumOfLegs() == 1))
						&& (maxMinDateDept.isAfter(nd.getDebriefDay(this.hbNdx)))
						) {
					numOfNodesChecked++;
					numOfFwNodesChecked++;
					if (nd.isHbArr(this.hbNdx)) {
						this.fwRegister(pd, nd);
						this.setNodeVisitedFw(nd, dept, maxMinDateDept);
						res = true;
					} else
						if (dept > 1) {
							if (this.isNodeVisitedFw(nd, dept, maxMinDateDept)
									&& hbArrFound[nd.getNdx()]) {
								this.fwRegister(pd, nd);
								this.setNodeVisitedFw(nd, dept, maxMinDateDept);
								res = true;
							} else
								if (!this.isNodeVisitedFw(nd, dept, maxMinDateDept)) {
									fwCumulative.addToQualityMetric(nd, dps[nd.getNdx()]);
									if ((sourceNodeQmArray.length == 0)
													|| fwCumulative.doesItWorthToGoDeeper(this.maxDutyBlockTimeInMins, heuristicNo, dept, sourceNodeQmArray[0].getQual())) {
										if (this.fwNetworkSearch(nd, fwCumulative, hbDep, maxMinDateDept, dept - 1)) {
											this.fwRegister(pd, nd);
											res = true;
										}
										this.setNodeVisitedFw(nd, dept, maxMinDateDept);
									}
									fwCumulative.removeFromQualityMetric(nd, dps[nd.getNdx()]);
								}
						}
				}
			}
		}
		return res;
	}

	private boolean isNodeVisitedBw(Duty d, int dept, LocalDate maxMinDateDept) {
		return (maxSearchNumDept[d.getNdx()] > dept)
				|| ((maxSearchNumDept[d.getNdx()] == dept)
						&& (maxSearchDayDept[d.getNdx()].isEqual(maxMinDateDept)
								|| maxSearchDayDept[d.getNdx()].isBefore(maxMinDateDept)));
	}

	private void setNodeVisitedBw(Duty d, int dept, LocalDate maxMinDateDept) {
		if (maxSearchNumDept[d.getNdx()] < dept) {
			maxSearchNumDept[d.getNdx()] = dept;
			maxSearchDayDept[d.getNdx()] = maxMinDateDept;
		} else
			if ((maxSearchNumDept[d.getNdx()] == dept)
					&& (maxSearchDayDept[d.getNdx()].isAfter(maxMinDateDept))) {
				maxSearchDayDept[d.getNdx()] = maxMinDateDept;
			}
	}

	private boolean bwRegister(Duty pd, Duty nd) {
		boolean res = false;

//if ((pd.getNdx() == 60341)
//&& (nd.getNdx() == 71683))
//System.out.println();
//if ((pd.getNdx() == 71683)
//&& (nd.getNdx() == 90163))
//System.out.println();

		/*
		 * Calculate quality metric.
		 */
		NodeQualityVector nQv = this.bestNodeQuality[nd.getNdx()];
		NodeQualityVector pQv = this.bestNodeQuality[pd.getNdx()];
		if (pQv == null) {
			pQv = new NodeQualityVector(this.maxPairingLengthInDays, pd, dps[pd.getNdx()], nQv);
			this.bestNodeQuality[pd.getNdx()] = pQv;
		} else {
			res = pQv.checkAndMerge(this.heuristicNo, nQv);
		}

		numOfNodesAdded++;
		numOfBwNodesAdded++;

		return res;
	}

	private boolean bwNetworkSearch(Duty rootDuty, boolean hbArr, LocalDate maxMinDateDept, int maxDept) throws CloneNotSupportedException {
		boolean res = false;

		LinkedList<Duty> treeOfDuties = new LinkedList<Duty>();
		treeOfDuties.add(rootDuty);

		/*
		 * This reduce is needed because in some cases the root duty might not have a hbArr connection with desired dept.
		 * 
		 * RootDuty.QualVector[X, X, Q, X]
		 * 
		 */
		while (this.bestNodeQuality[rootDuty.getNdx()].getQuals()[maxPairingLengthInDays - maxDept] == null) {
			maxDept--;
		}

		this.setNodeVisitedBw(rootDuty, maxDept, maxMinDateDept);

		QualityMetric bwCumulative = new QualityMetric();

		while (treeOfDuties.size() > 0) {

			Duty nd = treeOfDuties.removeFirst();

			int dept = this.maxSearchNumDept[nd.getNdx()] - 1;

			Leg[] prevLegs = this.prevDebriefLegIndexByDutyNdx.getArray(nd.getNdx());
			for (Leg leg : prevLegs) {
				Duty[] prevDuties = this.dutyIndexByArrLegNdx.getArray(leg.getNdx());
				for (Duty pd: prevDuties) {

//if ((pd.getNdx() == 60341)
//&& (nd.getNdx() == 71683))
//System.out.println();
//if ((pd.getNdx() == 71683)
//&& (nd.getNdx() == 90163))
//System.out.println();

					if (pd.isValid(this.hbNdx)
							&& pd.hasPairing(this.hbNdx)
							&& (pd.isHbDep(this.hbNdx) || (dept > 1))
							&& ((sourceNodeQmArray.length == 0)
									|| (pd.getNumOfLegs() > dps[pd.getNdx()].numOfDistinctCoverings)
									|| (pd.getNumOfLegs() == 1))
							&& (maxMinDateDept.isBefore(pd.getBriefDay(this.hbNdx))
									|| maxMinDateDept.isEqual(pd.getBriefDay(this.hbNdx)))
							) {

						if (pd.isHbDep(this.hbNdx)) {
							this.addSourceDuty(heuristicNo, pd, this.bwRegister(pd, nd));
							this.setNodeVisitedBw(pd, dept, maxMinDateDept);
							res = true;
						} else
							if (dept > 1) {
								if (this.isNodeVisitedBw(pd, dept, maxMinDateDept)) {
									this.bwRegister(pd, nd);
									this.setNodeVisitedBw(pd, dept, maxMinDateDept);
								} else {
									bwCumulative.injectValues(this.bestNodeQuality[nd.getNdx()].getQuals()[maxPairingLengthInDays - dept - 1].getQual());
									bwCumulative.addToQualityMetric(pd, dps[pd.getNdx()]);

									if ((bestNodeQuality[pd.getNdx()] == null)
											|| (bestNodeQuality[pd.getNdx()].getQuals()[maxPairingLengthInDays - dept] == null)
											|| bwCumulative.isBetterThan(this.heuristicNo, bestNodeQuality[pd.getNdx()].getQuals()[maxPairingLengthInDays - dept].getQual())
											) {

										if ((sourceNodeQmArray.length == 0)
												|| bwCumulative.doesItWorthToGoDeeper(this.maxDutyBlockTimeInMins, 
																						heuristicNo, 
																						dept, 
																						sourceNodeQmArray[0].getQual())) {
											this.bwRegister(pd, nd);
											this.setNodeVisitedBw(pd, dept, maxMinDateDept);
											treeOfDuties.add(pd);
										}
									}
								}
							}
					}
				}
			}
		}

		return res;
	}
}
