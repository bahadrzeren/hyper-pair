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
import org.heuros.pair.conf.HeurosSystemParam;
import org.heuros.pair.heuro.state.DutyState;

public class NetworkExplorer {

//	private static Logger logger = Logger.getLogger(NetworkExplorer.class);

	/*
	 * TODO Single base assumption!!!
	 */
	private int hbNdx = 0;

	private List<Duty> duties = null;

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
								DutyLegOvernightConnNetwork pricingNetwork) {
		this.duties = duties;
		this.sourceDuties = new boolean[this.duties.size()];

		this.sourceNodeQmArray = new NodeQualityMetric[0];

		this.bestNodeQuality = new NodeQualityVector[this.duties.size()];

		this.maxFwDeptReached = HeurosSystemParam.maxPairingLengthInDays;
		this.maxBwDeptReached = HeurosSystemParam.maxPairingLengthInDays;

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

	private DutyState[] dps = null;

	private int[] maxSearchNumDept = null;
	private LocalDate[] maxSearchDayDept = null;
	private boolean[] hbArrFound = null;

	private boolean addSourceDuty(Duty d, boolean hasImprovement) {
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
						if (nqm.getQual().isBetterThan(this.sourceNodeQmArray[j].getQual())) {
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
									DutyState[] dps) {
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

		LinkedList<BwRootNodeInfo> treeOfBwDuties = new LinkedList<BwRootNodeInfo>();

		for (Duty duty: rootDuties) {

//if ((legToCover.getNdx() == 10038)
//		&& (duty.getNdx() == 90163))
//System.out.println();

			LocalDate maxMinDateDept = null;

			if (duty.isValid(this.hbNdx)
					&& duty.hasPairing(this.hbNdx)
					&& ((sourceNodeQmArray.length == 0)
							|| (duty.getNumOfLegs() > dps[duty.getNdx()].numOfDistinctCoverings)
							|| (duty.getNumOfLegs() == 1))) {

				cumulativeQual.addToQualityMetricFw(duty, dps[duty.getNdx()]);
				if (duty.isHbDep(this.hbNdx)) {
					if (duty.isHbArr(this.hbNdx)) {
						/*
						 * Because of having no connection duties we must add qualityMetric here for 1day pairings.
						 */
						this.bestNodeQuality[duty.getNdx()] = new NodeQualityVector(HeurosSystemParam.maxPairingLengthInDays, duty, cumulativeQual);
						this.addSourceDuty(duty, false);
						hbArrFound[duty.getNdx()] = true;
					} else {
							maxMinDateDept = duty.getBriefDay(this.hbNdx).plusDays(HeurosSystemParam.maxPairingLengthInDays);
							if (this.fwNetworkSearch(duty, cumulativeQual, true, maxMinDateDept, HeurosSystemParam.maxPairingLengthInDays - 1)) {
								this.addSourceDuty(duty, false);
								hbArrFound[duty.getNdx()] = true;
							}
						}
				} else {
						if (duty.isHbArr(this.hbNdx)) {
							maxMinDateDept = duty.getDebriefDay(this.hbNdx).minusDays(HeurosSystemParam.maxPairingLengthInDays - 1);
							this.bestNodeQuality[duty.getNdx()] = new NodeQualityVector(HeurosSystemParam.maxPairingLengthInDays, duty, cumulativeQual);
//							if (this.bwNetworkSearch(duty, true, maxMinDateDept, HeurosSystemParam.maxPairingLengthInDays)) {
//								hbArrFound[duty.getNdx()] = true;
//							}
							treeOfBwDuties.add(new BwRootNodeInfo(duty, maxMinDateDept, HeurosSystemParam.maxPairingLengthInDays).initRootNode());
						} else {
							maxMinDateDept = duty.getBriefDay(this.hbNdx).plusDays(HeurosSystemParam.maxPairingLengthInDays - 1);
							if (this.fwNetworkSearch(duty, cumulativeQual, false, maxMinDateDept, HeurosSystemParam.maxPairingLengthInDays - 2)) {
								hbArrFound[duty.getNdx()] = true;
								maxMinDateDept = duty.getDebriefDay(this.hbNdx).minusDays(HeurosSystemParam.maxPairingLengthInDays - 2);
//								/*
//								 * We need to use the best quality metric that is found for the root duty so far.
//								 */
//								this.bwNetworkSearch(duty, false, maxMinDateDept, HeurosSystemParam.maxPairingLengthInDays - 1);
								treeOfBwDuties.add(new BwRootNodeInfo(duty, maxMinDateDept, HeurosSystemParam.maxPairingLengthInDays - 1).initRootNode());
							}
						}
					}
				cumulativeQual.removeLastDutyQualityMetric(duty, dps[duty.getNdx()]);

//if (cumulativeQual.isNotEmpty())
//	logger.error("CumulativeQual is not empty!");
			}
		}

		this.bwNetworkSearch(treeOfBwDuties);

		treeOfBwDuties.clear();

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
			nQv = new NodeQualityVector(HeurosSystemParam.maxPairingLengthInDays, nd, dps[nd.getNdx()]);
			this.bestNodeQuality[nd.getNdx()] = nQv;
		}
		/*
		 * Non HB arr duty
		 */
		if (pQv == null) {
			pQv = new NodeQualityVector(HeurosSystemParam.maxPairingLengthInDays, pd, dps[pd.getNdx()], nQv);
			this.bestNodeQuality[pd.getNdx()] = pQv;
		} else {
			pQv.checkAndMerge(nQv);
		}

		hbArrFound[nd.getNdx()] = true;
		numOfNodesAdded++;
		numOfFwNodesAdded++;
	}

	private boolean fwNetworkSearch(Duty pd, QualityMetric fwCumulative, boolean hbDep, LocalDate maxMinDateDept, int dept) {

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
									fwCumulative.addToQualityMetricFw(nd, dps[nd.getNdx()]);
									if ((sourceNodeQmArray.length == 0)
													|| fwCumulative.doesItWorthToGoDeeper(dept, sourceNodeQmArray[0].getQual())) {
										if (this.fwNetworkSearch(nd, fwCumulative, hbDep, maxMinDateDept, dept - 1)) {
											this.fwRegister(pd, nd);
											res = true;
										}
										this.setNodeVisitedFw(nd, dept, maxMinDateDept);
									}
									fwCumulative.removeLastDutyQualityMetric(nd, dps[nd.getNdx()]);
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
			pQv = new NodeQualityVector(HeurosSystemParam.maxPairingLengthInDays, pd, dps[pd.getNdx()], nQv);
			this.bestNodeQuality[pd.getNdx()] = pQv;
		} else {
			res = pQv.checkAndMerge(nQv);
		}

		numOfNodesAdded++;
		numOfBwNodesAdded++;

		return res;
	}

	private class BwRootNodeInfo {
		public Duty duty;
		public LocalDate maxMinDateDept;
		public int maxDept;
		public BwRootNodeInfo(Duty rootDuty, LocalDate maxMinDateDept, int maxDept) {
			this.duty = rootDuty;
			this.maxMinDateDept = maxMinDateDept;
			this.maxDept = maxDept;
		}
		public BwRootNodeInfo initRootNode() {
			/*
			 * This reduce is needed because in some cases the root duty might not have a hbArr connection with desired dept.
			 * 
			 * RootDuty.QualVector[X, X, Q, X]
			 * 
			 */
			while (bestNodeQuality[duty.getNdx()].getQuals()[HeurosSystemParam.maxPairingLengthInDays - this.maxDept] == null) {
				this.maxDept--;
			}
			setNodeVisitedBw(this.duty, this.maxDept, this.maxMinDateDept);
			return this;
		}
	}

	private boolean bwNetworkSearch(LinkedList<BwRootNodeInfo> treeOfDuties) {
		boolean res = false;


		QualityMetric bwCumulative = new QualityMetric();

		while (treeOfDuties.size() > 0) {

			BwRootNodeInfo ndInfo = treeOfDuties.removeFirst();

			int dept = ndInfo.maxDept - 1;	//	this.maxSearchNumDept[ndInfo.duty.getNdx()] - 1;

			Leg[] prevLegs = this.prevDebriefLegIndexByDutyNdx.getArray(ndInfo.duty.getNdx());
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
							&& (!pd.getMinNextBriefTime(hbNdx).isAfter(ndInfo.duty.getBriefTime(hbNdx)))
							&& (pd.getMinNextBriefTime(hbNdx).plusHours(HeurosSystemParam.maxNetDutySearchDeptInHours + 1).isAfter(ndInfo.duty.getBriefTime(hbNdx)))
							&& (pd.isHbDep(this.hbNdx) || (dept > 1))
							&& ((sourceNodeQmArray.length == 0)
									|| (pd.getNumOfLegs() > dps[pd.getNdx()].numOfDistinctCoverings)
									|| (pd.getNumOfLegs() == 1))
							&& (ndInfo.maxMinDateDept.isBefore(pd.getBriefDay(this.hbNdx))
									|| ndInfo.maxMinDateDept.isEqual(pd.getBriefDay(this.hbNdx)))
							) {

						if (pd.isHbDep(this.hbNdx)) {
							this.addSourceDuty(pd, this.bwRegister(pd, ndInfo.duty));
							this.setNodeVisitedBw(pd, dept, ndInfo.maxMinDateDept);
							res = true;
						} else
							if (dept > 1) {
								if (this.isNodeVisitedBw(pd, dept, ndInfo.maxMinDateDept)) {
									this.bwRegister(pd, ndInfo.duty);
									this.setNodeVisitedBw(pd, dept, ndInfo.maxMinDateDept);
								} else {
									bwCumulative.injectValues(this.bestNodeQuality[ndInfo.duty.getNdx()].getQuals()[HeurosSystemParam.maxPairingLengthInDays - dept - 1].getQual());
									bwCumulative.addToQualityMetricBw(pd, dps[pd.getNdx()]);

									if ((bestNodeQuality[pd.getNdx()] == null)
											|| (bestNodeQuality[pd.getNdx()].getQuals()[HeurosSystemParam.maxPairingLengthInDays - dept] == null)
											|| bwCumulative.isBetterThan(bestNodeQuality[pd.getNdx()].getQuals()[HeurosSystemParam.maxPairingLengthInDays - dept].getQual())
											) {

										if ((sourceNodeQmArray.length == 0)
												|| bwCumulative.doesItWorthToGoDeeper(dept, sourceNodeQmArray[0].getQual())) {
											this.bwRegister(pd, ndInfo.duty);
											this.setNodeVisitedBw(pd, dept, ndInfo.maxMinDateDept);
											treeOfDuties.add(new BwRootNodeInfo(pd, ndInfo.maxMinDateDept, dept));
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
