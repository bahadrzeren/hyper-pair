package org.heuros.hyperpair.heuristic;

import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.heuros.core.data.ndx.OneDimIndexInt;
import org.heuros.core.data.ndx.OneDimUniqueIndexInt;
import org.heuros.data.DutyLegOvernightConnNetwork;
import org.heuros.data.model.Duty;
import org.heuros.data.model.DutyView;
import org.heuros.data.model.LegView;

public class PricingSubNetwork {

//	private static Logger logger = Logger.getLogger(PricingSubNetwork.class);

	/*
	 * TODO Single base assumption!!!
	 */
	private int hbNdx = 0;

//	private int numOfEliteNodes = 3;

	private List<Duty> duties = null;
	private int maxPairingLengthInDays = 0;
	private int maxDutyBlockTimeInMins = 0;

//	private DutyRuleContext dutyRuleContext = null;
//	private PairRuleContext pairRuleContext = null;
	private OneDimIndexInt<DutyView> dutyIndexByDepLegNdx = null;
	private OneDimIndexInt<DutyView> dutyIndexByArrLegNdx = null;
	private OneDimUniqueIndexInt<LegView> nextBriefLegIndexByDutyNdx = null;
	private OneDimUniqueIndexInt<LegView> prevDebriefLegIndexByDutyNdx = null;

//	private Set<Integer> sourceDuties = null;
	private boolean[] sourceDuties = null;
//	private List<HashSet<Integer>> dutyConnections = null;
	private OneDimUniqueIndexInt<DutyView> dutyConnections = null;
//	private int[] sourceDutyArray = null;
	private DutyView[] sourceDutyArray = null;
//	private int[][] dutyConnectionArray = null;
//	private NodeQualityMetric bestSourceNodeQuality = null;

	private NodeQualityMetric[] bestNodeQuality = null;

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

	public PricingSubNetwork(List<Duty> duties,
								int maxPairingLengthInDays,
								int maxDutyBlockTimeInMins, 
//								DutyRuleContext dutyRuleContext,
//								PairRuleContext pairRuleContext,
								DutyLegOvernightConnNetwork pricingNetwork) {
		this.duties = duties;
//		this.sourceDuties = new HashSet<Integer>();
		this.sourceDuties = new boolean[this.duties.size()];
//		this.dutyConnections = new ArrayList<HashSet<Integer>>(this.duties.size());
		this.dutyConnections = new OneDimUniqueIndexInt<DutyView>(new DutyView[this.duties.size()][0]);
//		this.sourceDutyArray = new int[0];
		this.sourceDutyArray = new DutyView[0];
//		this.dutyConnectionArray = new int[this.duties.size()][0];

		this.bestNodeQuality = new NodeQualityMetric[this.duties.size()];

		this.maxPairingLengthInDays = maxPairingLengthInDays;
		this.maxFwDeptReached = maxPairingLengthInDays;
		this.maxBwDeptReached = maxPairingLengthInDays;
		this.maxDutyBlockTimeInMins = maxDutyBlockTimeInMins;

//		this.dutyRuleContext = dutyRuleContext;
//		this.pairRuleContext = pairRuleContext;

		this.dutyIndexByDepLegNdx = pricingNetwork.getDutyIndexByDepLegNdx();
		this.dutyIndexByArrLegNdx = pricingNetwork.getDutyIndexByArrLegNdx();
		this.nextBriefLegIndexByDutyNdx = pricingNetwork.getNextBriefLegIndexByDutyNdx();
		this.prevDebriefLegIndexByDutyNdx = pricingNetwork.getPrevDebriefLegIndexByDutyNdx();
	}

	private boolean addSourceDuty(int heuristicNo, DutyView d) {
		if (!this.sourceDuties[d.getNdx()]) {
			this.sourceDuties[d.getNdx()] = true;
//			this.sourceDutyArray = ArrayUtils.add(this.sourceDutyArray, d.getNdx());
//			this.sourceDutyArray = ArrayUtils.add(this.sourceDutyArray, d);
			/*
			 * We would like to keep the best duty node at first place in order to increase performance.  
			 */
			DutyView duty = d;
			if (this.sourceDutyArray.length > 0) {
				for (int i = 0; i < this.sourceDutyArray.length; i++) {
					if (bestNodeQuality[duty.getNdx()].isBetterThan(heuristicNo, bestNodeQuality[this.sourceDutyArray[i].getNdx()])) {
						DutyView hd = this.sourceDutyArray[i];
						this.sourceDutyArray[i] = duty;
						duty = hd;
						for (int j = i + 1; j < this.sourceDutyArray.length; j++) {
							hd = this.sourceDutyArray[j];
							this.sourceDutyArray[j] = duty;
							duty = hd;
						}
						break;
					}
				}
			}
			this.sourceDutyArray = ArrayUtils.add(this.sourceDutyArray, duty);
			return true;
		}
		return false;
	}

	private void calculateFwQual(DutyView pd, DutyView nd) {
		/*
		 * Calculate quality metric.
		 */
		NodeQualityMetric ndQ = this.bestNodeQuality[nd.getNdx()];
		NodeQualityMetric pdQ = this.bestNodeQuality[pd.getNdx()];
		if (ndQ == null) {
			ndQ = new NodeQualityMetric();
			ndQ.addToQualityMetric(nd, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
			this.bestNodeQuality[nd.getNdx()] = ndQ;
			ndQ.nodeOwner = nd;
		}
		if (pdQ == null) {
			pdQ = new NodeQualityMetric();
			pdQ.addToQualityMetric(ndQ);
			pdQ.addToQualityMetric(pd, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
			this.bestNodeQuality[pd.getNdx()] = pdQ;
			pdQ.nodeOwner = pd;
			pdQ.nextNodeMetric = ndQ;
		} else {
			ndQ.addToQualityMetric(pd, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
			if (ndQ.isBetterThan(heuristicNo, pdQ)) {
				pdQ.injectValues(ndQ);
				pdQ.nextNodeMetric = ndQ;
			}
			ndQ.removeFromQualityMetric(pd, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
		}
	}

	private void calculateBwQual(DutyView pd, DutyView nd, NodeQualityMetric bwCumulative) {
		/*
		 * Calculate quality metric.
		 */
		NodeQualityMetric pdQ = this.bestNodeQuality[pd.getNdx()];
		if (pdQ == null) {
			pdQ = new NodeQualityMetric();
			pdQ.injectValues(bwCumulative);
			this.bestNodeQuality[pd.getNdx()] = pdQ;
			pdQ.nodeOwner = pd;
			pdQ.nextNodeMetric = this.bestNodeQuality[nd.getNdx()];
		} else {
			if (bwCumulative.isBetterThan(heuristicNo, pdQ)) {
				pdQ.injectValues(bwCumulative);
				pdQ.nextNodeMetric = this.bestNodeQuality[nd.getNdx()];
			}
		}
	}

	private boolean addDuty(DutyView pd, DutyView nd) {
//		HashSet<Integer> nextDutyNdxs = this.dutyConnections.get(pd.getNdx());
//		if (nextDutyNdxs == null) {
//			nextDutyNdxs = new HashSet<Integer>();
//			this.dutyConnections.set(pd.getNdx(), nextDutyNdxs);
//		}
//		if (nextDutyNdxs.add(nd.getNdx())) {
		if (this.dutyConnections.add(pd.getNdx(), nd.getNdx(), nd)) {
//			dutyConnectionArray[pd.getNdx()] = ArrayUtils.add(dutyConnectionArray[pd.getNdx()], nd.getNdx());
			return true;
		}
		return false;
	}

//	public Set<Integer> getSourceDuties() {
//		return this.sourceDuties;
//	}
//
//	public List<HashSet<Integer>> getDutyConnections() {
//		return this.dutyConnections;
//	}
//
//	public Set<Integer> getNextDuties(DutyView pd) {
//		return this.dutyConnections.get(pd.getNdx());
//	}

	public DutyView[] getSourceDuties() {
		return this.sourceDutyArray;
	}

//	public int[][] getDutyConnections() {
//		return this.dutyConnectionArray;
//	}

	public DutyView[] getNextDuties(DutyView pd) {
//		return this.dutyConnectionArray[pd.getNdx()];
		return this.dutyConnections.getArray(pd.getNdx());
	}

	public NodeQualityMetric[] getBestNodeQuality() {
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

//	private LegView legToCover = null;
//	private Duty[] rootDuties = null;
	private int heuristicNo = 0;
	private int[] numOfCoveringsInDuties = null;
	private int[] numOfDistinctCoveringsInDuties = null;
	private int[] blockTimeOfCoveringsInDuties = null;

	private int[] maxSearchNumDept = null;
	private LocalDate[] maxSearchDayDept = null;
	private boolean[] hbArrFound = null;
	private boolean[] hbDepFound = null;

	public PricingSubNetwork build(LegView legToCover,
									Duty[] rootDuties,
									int heuristicNo,
									int[] numOfCoveringsInDuties,
									int[] numOfDistinctCoveringsInDuties,
									int[] blockTimeOfCoveringsInDuties) throws CloneNotSupportedException {
//		this.legToCover = legToCover;
//		this.rootDuties = rootDuties;
		this.heuristicNo = heuristicNo;
		this.numOfCoveringsInDuties = numOfCoveringsInDuties;
		this.numOfDistinctCoveringsInDuties = numOfDistinctCoveringsInDuties;
		this.blockTimeOfCoveringsInDuties = blockTimeOfCoveringsInDuties;

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
		this.hbDepFound = new boolean[this.duties.size()];

//		Arrays.parallelSort(rootDuties,  new Comparator<DutyView>() {
//			@Override
//			public int compare(DutyView a, DutyView b) {
//				if (a.get() < b.get())
//					return -1;
//				else
//					if (a.get() > b.get())
//						return 1;
//				return 0;
//			}
//		});

//if (legToCover.getNdx() == 1768)
//System.out.println();

		for (Duty duty: rootDuties) {

//if ((legToCover.getNdx() == 1768)
//		&& (duty.getNdx() == 14333))
//System.out.println();

			LocalDate maxMinDateDept = null;

			if (duty.isValid(this.hbNdx)
					&& duty.hasPairing(this.hbNdx)
					&& ((sourceDutyArray.length == 0)
							|| (duty.getNumOfLegs() > numOfDistinctCoveringsInDuties[duty.getNdx()])
							|| (duty.getNumOfLegs() == 1))
					) {
				NodeQualityMetric cumulativeQual = new NodeQualityMetric();
				cumulativeQual.addToQualityMetric(duty, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
				if (duty.isHbDep(this.hbNdx)) {
					if (duty.isHbArr(this.hbNdx)) {
						/*
						 * Because of having no connection duties we must add qualityMetric here for 1day pairings.
						 */
						this.bestNodeQuality[duty.getNdx()] = cumulativeQual;
						this.addSourceDuty(heuristicNo, duty);
						hbDepFound[duty.getNdx()] = true;
						hbArrFound[duty.getNdx()] = true;
					} else 
						if (heuristicNo > 0) {
							maxMinDateDept = duty.getBriefDay(this.hbNdx).plusDays(this.maxPairingLengthInDays);
							if (this.fwNetworkSearch(duty, cumulativeQual, true, maxMinDateDept, this.maxPairingLengthInDays - 1)) {
								this.addSourceDuty(heuristicNo, duty);
								hbDepFound[duty.getNdx()] = true;
								hbArrFound[duty.getNdx()] = true;
							}
//							maxSearchNumDept[duty.getNdx()] = this.maxPairingLengthInDays;
						}
				} else
					if (heuristicNo > 0) {
						if (duty.isHbArr(this.hbNdx)) {
							maxMinDateDept = duty.getDebriefDay(this.hbNdx).minusDays(this.maxPairingLengthInDays - 1);
							if (this.bwNetworkSearch(duty, cumulativeQual, true, maxMinDateDept, this.maxPairingLengthInDays - 1)) {
								this.bestNodeQuality[duty.getNdx()] = cumulativeQual;
								hbDepFound[duty.getNdx()] = true;
								hbArrFound[duty.getNdx()] = true;
							}
//							maxSearchNumDept[duty.getNdx()] = this.maxPairingLengthInDays;
						} else {
							maxMinDateDept = duty.getBriefDay(this.hbNdx).plusDays(this.maxPairingLengthInDays - 1);
							if (this.fwNetworkSearch(duty, cumulativeQual, false, maxMinDateDept, this.maxPairingLengthInDays - 2)) {
								hbArrFound[duty.getNdx()] = true;
								maxMinDateDept = duty.getDebriefDay(this.hbNdx).minusDays(this.maxPairingLengthInDays - 2);
								/*
								 * We need to use the best quality metric that is found for the root duty so far.
								 */
								cumulativeQual.injectValues(bestNodeQuality[duty.getNdx()]);
								if (this.bwNetworkSearch(duty, cumulativeQual, false, maxMinDateDept, this.maxPairingLengthInDays - 2)) {
									hbDepFound[duty.getNdx()] = true;
								}
							}
//							maxSearchNumDept[duty.getNdx()] = this.maxPairingLengthInDays - 1;
						}
					}
			}
		}
		return this;
	}

	private boolean isNodeVisited(DutyView d, int dept, LocalDate maxMinDateDept) {
		return (maxSearchNumDept[d.getNdx()] > dept)
				|| ((maxSearchNumDept[d.getNdx()] == dept)
//						&& (maxSearchDayDept[d.getNdx()] != null)
						&& (maxSearchDayDept[d.getNdx()].isEqual(maxMinDateDept)
								|| maxSearchDayDept[d.getNdx()].isAfter(maxMinDateDept)));
	}

	private void setNodeVisited(DutyView d, int dept, LocalDate maxMinDateDept) {
		if (maxSearchNumDept[d.getNdx()] < dept) {
			maxSearchNumDept[d.getNdx()] = dept;
			maxSearchDayDept[d.getNdx()] = maxMinDateDept;
		} else
			if ((maxSearchNumDept[d.getNdx()] == dept)
					&& (maxSearchDayDept[d.getNdx()].isBefore(maxMinDateDept))) {
				maxSearchDayDept[d.getNdx()] = maxMinDateDept;
			}
	}

	private void fwRegister(DutyView pd, DutyView nd) {
		this.calculateFwQual(pd, nd);
		this.addDuty(pd, nd);
		hbArrFound[nd.getNdx()] = true;
		numOfNodesAdded++;
		numOfFwNodesAdded++;
	}

	private boolean fwNetworkSearch(DutyView pd, NodeQualityMetric fwCumulative, boolean hbDep, LocalDate maxMinDateDept, int dept) throws CloneNotSupportedException {

		if (dept < maxFwDeptReached)
			maxFwDeptReached = dept;
		numOfRecursions++;
		numOfFwRecursions++;
		boolean res = false;
		LegView[] nextLegs = this.nextBriefLegIndexByDutyNdx.getArray(pd.getNdx());
		for (LegView leg : nextLegs) {
			DutyView[] nextDuties = this.dutyIndexByDepLegNdx.getArray(leg.getNdx());
			for (DutyView nd: nextDuties) {
//if ((pd.getNdx() == 14333)
//&& (nd.getNdx() == 23018))
//System.out.println();
//if ((pd.getNdx() == 23018)
//&& (nd.getNdx() == 31625))
//System.out.println();
//if ((pd.getNdx() == 31625)
//&& (nd.getNdx() == 37544))
//System.out.println();
				numOfNodes++;
				numOfFwNodes++;
				if (nd.isValid(this.hbNdx)
						&& nd.hasPairing(this.hbNdx)
						&& (nd.isHbArr(this.hbNdx) || (dept > 1))
						&& ((sourceDutyArray.length == 0)
								|| (nd.getNumOfLegs() > numOfDistinctCoveringsInDuties[nd.getNdx()])
								|| (nd.getNumOfLegs() == 1))
						/*
						 * TODO Instead of performing minus operations all the time, debriefTime could be reduced by 1 second by default. 
						 */
//						&& ((hbDep && nd.isHbArr(this.hbNdx) && (ChronoUnit.DAYS.between(rootBriefTime, nd.getDebriefTime(this.hbNdx).minusSeconds(1)) < this.maxPairingLengthInDays))
//								|| (hbDep && nd.isNonHbArr(this.hbNdx) && (ChronoUnit.DAYS.between(rootBriefTime, nd.getDebriefTime(this.hbNdx).minusSeconds(1)) < this.maxPairingLengthInDays - 1))
//								|| ((!hbDep) && nd.isHbArr(this.hbNdx) && (ChronoUnit.DAYS.between(rootBriefTime, nd.getDebriefTime(this.hbNdx).minusSeconds(1)) < this.maxPairingLengthInDays - 1))
//								|| ((!hbDep) && nd.isNonHbArr(this.hbNdx) && (ChronoUnit.DAYS.between(rootBriefTime, nd.getDebriefTime(this.hbNdx).minusSeconds(1)) < this.maxPairingLengthInDays - 2)))
						&& (maxMinDateDept.isAfter(nd.getDebriefDay(this.hbNdx)))
						) {
					numOfNodesChecked++;
					numOfFwNodesChecked++;
					fwCumulative.addToQualityMetric(nd, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
					if (nd.isHbArr(this.hbNdx)) {
						this.fwRegister(pd, nd);
//						maxSearchNumDept[nd.getNdx()] = dept;
						this.setNodeVisited(nd, dept, maxMinDateDept);
						res = true;
					} else
						if (dept > 1) {
							if (//	(maxSearchNumDept[nd.getNdx()] >= dept)
									this.isNodeVisited(nd, dept, maxMinDateDept)
									&& hbArrFound[nd.getNdx()]) {
								this.fwRegister(pd, nd);
								this.setNodeVisited(nd, dept, maxMinDateDept);
								res = true;
							} else
								if (//	maxSearchNumDept[nd.getNdx()] < dept
										!this.isNodeVisited(nd, dept, maxMinDateDept)
										) {
									if ((sourceDutyArray.length == 0)
													|| fwCumulative.doesItWorthToGoDeeper(this.maxDutyBlockTimeInMins, heuristicNo, dept, bestNodeQuality[sourceDutyArray[0].getNdx()])) {
										if (this.fwNetworkSearch(nd, fwCumulative, hbDep, maxMinDateDept, dept - 1)) {
											this.fwRegister(pd, nd);
											res = true;
										}
//										maxSearchNumDept[nd.getNdx()] = dept;
										this.setNodeVisited(nd, dept, maxMinDateDept);
									}
								}
						}
					fwCumulative.removeFromQualityMetric(nd, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
				}
			}
		}
		return res;
	}

	private void bwRegister(DutyView pd, DutyView nd, NodeQualityMetric bwCumulative) {
		this.calculateBwQual(pd, nd, bwCumulative);
		this.addDuty(pd, nd);
		hbDepFound[pd.getNdx()] = true;
		numOfNodesAdded++;
		numOfBwNodesAdded++;
	}

	private boolean bwNetworkSearch(DutyView nd, NodeQualityMetric bwCumulative, boolean hbArr, LocalDate maxMinDateDept, int dept) throws CloneNotSupportedException {
		if (dept < maxBwDeptReached)
			maxBwDeptReached = dept;
		numOfRecursions++;
		numOfBwRecursions++;
		boolean res = false;
		LegView[] prevLegs = this.prevDebriefLegIndexByDutyNdx.getArray(nd.getNdx());
		for (LegView leg : prevLegs) {
			DutyView[] prevDuties = this.dutyIndexByArrLegNdx.getArray(leg.getNdx());
			for (DutyView pd: prevDuties) {
				numOfNodes++;
				numOfBwNodes++;
				if (pd.isValid(this.hbNdx)
						&& pd.hasPairing(this.hbNdx)
						&& (pd.isHbDep(this.hbNdx) || (dept > 1))
						&& ((sourceDutyArray.length == 0)
								|| (pd.getNumOfLegs() > numOfDistinctCoveringsInDuties[pd.getNdx()])
								|| (pd.getNumOfLegs() == 1))
						/*
						 * TODO Instead of performing minus operations all the time, debriefTime could be reduced by 1 second by default. 
						 */
//						&& ((hbArr && nd.isHbDep(this.hbNdx) && (ChronoUnit.DAYS.between(pd.getBriefTime(this.hbNdx), rootDebriefTime.minusSeconds(1)) < this.maxPairingLengthInDays))
//								|| (hbArr && nd.isNonHbDep(this.hbNdx) && (ChronoUnit.DAYS.between(pd.getBriefTime(this.hbNdx), rootDebriefTime.minusSeconds(1)) < this.maxPairingLengthInDays - 1))
//								|| ((!hbArr) && nd.isHbDep(this.hbNdx) && (ChronoUnit.DAYS.between(pd.getBriefTime(this.hbNdx), rootDebriefTime.minusSeconds(1)) < this.maxPairingLengthInDays - 1))
//								|| ((!hbArr) && nd.isNonHbDep(this.hbNdx) && (ChronoUnit.DAYS.between(pd.getBriefTime(this.hbNdx), rootDebriefTime.minusSeconds(1)) < this.maxPairingLengthInDays - 2)))
						&& (maxMinDateDept.isBefore(pd.getBriefDay(this.hbNdx))
								|| maxMinDateDept.isEqual(pd.getBriefDay(this.hbNdx)))
						) {
					numOfNodesChecked++;
					numOfBwNodesChecked++;
					bwCumulative.addToQualityMetric(pd, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
					if (pd.isHbDep(this.hbNdx)) {
						this.bwRegister(pd, nd, bwCumulative);
						this.addSourceDuty(heuristicNo, pd);
//						maxSearchNumDept[pd.getNdx()] = dept;
						this.setNodeVisited(pd, dept, maxMinDateDept);
						res = true;
					} else
						if (dept > 1) {
							if (//	(maxSearchNumDept[pd.getNdx()] >= dept)
									this.isNodeVisited(pd, dept, maxMinDateDept)
									&& hbDepFound[pd.getNdx()]) {
								this.bwRegister(pd, nd, bwCumulative);
								this.setNodeVisited(pd, dept, maxMinDateDept);
								res = true;
							} else
								if (//	maxSearchNumDept[pd.getNdx()] < dept
										!this.isNodeVisited(pd, dept, maxMinDateDept)
										) {
									if ((sourceDutyArray.length == 0)
											|| bwCumulative.doesItWorthToGoDeeper(this.maxDutyBlockTimeInMins, heuristicNo, dept, bestNodeQuality[sourceDutyArray[0].getNdx()])) {
										if (this.bwNetworkSearch(pd, bwCumulative, hbArr, maxMinDateDept, dept - 1)) {
											this.bwRegister(pd, nd, bwCumulative);
											res = true;
										}
//										maxSearchNumDept[pd.getNdx()] = dept;
										this.setNodeVisited(pd, dept, maxMinDateDept);
									}
								}
						}
					bwCumulative.removeFromQualityMetric(pd, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
				}
			}
		}
		return res;
	}
}
