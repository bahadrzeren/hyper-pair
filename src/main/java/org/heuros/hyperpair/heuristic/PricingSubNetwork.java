package org.heuros.hyperpair.heuristic;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.heuros.core.data.ndx.OneDimIndexInt;
import org.heuros.core.data.ndx.OneDimUniqueIndexInt;
import org.heuros.data.DutyLegOvernightConnNetwork;
import org.heuros.data.model.Duty;
import org.heuros.data.model.DutyView;
import org.heuros.data.model.LegView;
import org.heuros.rule.DutyRuleContext;
import org.heuros.rule.PairRuleContext;

public class PricingSubNetwork {

//	private static Logger logger = Logger.getLogger(PricingSubNetwork.class);

	/*
	 * TODO Single base assumption!!!
	 */
	private int hbNdx = 0;

	private int numOfEliteNodes = 3;

	private List<Duty> duties = null;
	private int maxPairingLengthInDays = 0;

	private DutyRuleContext dutyRuleContext = null;
	private PairRuleContext pairRuleContext = null;
	private OneDimIndexInt<DutyView> dutyIndexByDepLegNdx = null;
	private OneDimIndexInt<DutyView> dutyIndexByArrLegNdx = null;
	private OneDimUniqueIndexInt<LegView> nextBriefLegIndexByDutyNdx = null;
	private OneDimUniqueIndexInt<LegView> prevDebriefLegIndexByDutyNdx = null;

	private Set<Integer> sourceDuties = null;
//	private List<HashSet<Integer>> dutyConnections = null;
	private OneDimUniqueIndexInt<DutyView> dutyConnections = null;

//	private int[] sourceDutyArray = null;
	private DutyView[] sourceDutyArray = null;
//	private int[][] dutyConnectionArray = null;
	private NodeQualityMetric[] bestNodeQuality = null;
//	private NodeQualityMetric bestSourceNodeQuality = null;

	public PricingSubNetwork(List<Duty> duties,
								int maxPairingLengthInDays,
								DutyRuleContext dutyRuleContext,
								PairRuleContext pairRuleContext,
								DutyLegOvernightConnNetwork pricingNetwork) {
		this.duties = duties;
		this.sourceDuties = new HashSet<Integer>();
//		this.dutyConnections = new ArrayList<HashSet<Integer>>(this.duties.size());
		this.dutyConnections = new OneDimUniqueIndexInt<DutyView>(new DutyView[this.duties.size()][0]);
//		this.sourceDutyArray = new int[0];
		this.sourceDutyArray = new DutyView[0];
//		this.dutyConnectionArray = new int[this.duties.size()][0];
		this.bestNodeQuality = new NodeQualityMetric[this.duties.size()];

		this.maxPairingLengthInDays = maxPairingLengthInDays;

		this.dutyRuleContext = dutyRuleContext;
		this.pairRuleContext = pairRuleContext;

		this.dutyIndexByDepLegNdx = pricingNetwork.getDutyIndexByDepLegNdx();
		this.dutyIndexByArrLegNdx = pricingNetwork.getDutyIndexByArrLegNdx();
		this.nextBriefLegIndexByDutyNdx = pricingNetwork.getNextBriefLegIndexByDutyNdx();
		this.prevDebriefLegIndexByDutyNdx = pricingNetwork.getPrevDebriefLegIndexByDutyNdx();
	}

	private boolean addSourceDuty(int heuristicNo, DutyView d) {
		if (this.sourceDuties.add(d.getNdx())) {
//			this.sourceDutyArray = ArrayUtils.add(this.sourceDutyArray, d.getNdx());
			this.sourceDutyArray = ArrayUtils.add(this.sourceDutyArray, d);
			/*
			 * We would like to keep the best duty node at first place in order to increase performance.  
			 */
			if (this.sourceDutyArray.length > numOfEliteNodes) {
				for (int i = 0; i < numOfEliteNodes; i++) {
					if (bestNodeQuality[d.getNdx()].isBetterThan(heuristicNo, bestNodeQuality[this.sourceDutyArray[i].getNdx()])) {
						DutyView duty = this.sourceDutyArray[i];
						this.sourceDutyArray[i] = d;
						this.sourceDutyArray[this.sourceDutyArray.length - 1] = duty;
						break;
					}
				}
			}
//			this.bestSourceNodeQuality = bestNodeQuality[this.sourceDutyArray[0].getNdx()];
			return true;
		}
		return false;
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

	public PricingSubNetwork build(Duty[] rootDuties,
									int heuristicNo,
									int[] numOfCoveringsInDuties,
									int[] numOfDistinctCoveringsInDuties,
									int[] blockTimeOfCoveringsInDuties) throws CloneNotSupportedException {
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

		int[] maxSearchDept = new int[this.duties.size()];
		boolean[] hbArrFound = new boolean[this.duties.size()];
		boolean[] hbDepFound = new boolean[this.duties.size()];

//boolean log = false;

		for (Duty duty: rootDuties) {

			if (duty.isValid(this.hbNdx)
					&& duty.hasPairing(this.hbNdx)
					&& ((duty.getNumOfLegs() > numOfDistinctCoveringsInDuties[duty.getNdx()])
						|| (duty.getNumOfLegs() == 1))) {
				if (duty.isHbDep(this.hbNdx)) {
					if (duty.isHbArr(this.hbNdx)) {
//						if (this.pairRuleContext.getStarterCheckerProxy().canBeStarter(this.hbNdx, duty)) {
							/*
							 * Because of having no connection duties we must add qualityMetric here for 1day pairings.
							 */
							if (this.bestNodeQuality[duty.getNdx()] == null) {
								this.bestNodeQuality[duty.getNdx()] = new NodeQualityMetric();
								this.bestNodeQuality[duty.getNdx()].addToQualityMetric(duty, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
							}
							this.addSourceDuty(heuristicNo, duty);
							hbDepFound[duty.getNdx()] = true;
							hbArrFound[duty.getNdx()] = true;
//log = duty.getFirstLeg().getSobt().isAfter(LocalDateTime.of(2014, 1, 5, 0, 0));
//if (log)
//logger.debug(duty.getNdx() + ":" + duty.getFirstDepAirport().getCode() + "->" + duty.getLastArrAirport().getCode() + "; " + this.bestNodeQuality[duty.getNdx()]);
////						}
					} else 
						if (heuristicNo > 0) {
//							if (this.pairRuleContext.getStarterCheckerProxy().canBeStarter(this.hbNdx, duty)) {
								if (this.fwNetworkSearch(maxSearchDept, hbArrFound, heuristicNo, numOfCoveringsInDuties, numOfDistinctCoveringsInDuties, blockTimeOfCoveringsInDuties,
															duty, true, duty.getBriefTime(this.hbNdx), this.maxPairingLengthInDays - 1)) {
									this.addSourceDuty(heuristicNo, duty);
									hbDepFound[duty.getNdx()] = true;
									hbArrFound[duty.getNdx()] = true;
								}
								maxSearchDept[duty.getNdx()] = this.maxPairingLengthInDays;
//							}
						}
				} else
					if (heuristicNo > 0) {
						if (duty.isHbArr(this.hbNdx)) {
							if (this.bwNetworkSearch(maxSearchDept, hbDepFound, heuristicNo, numOfCoveringsInDuties, numOfDistinctCoveringsInDuties, blockTimeOfCoveringsInDuties,
														duty, true, duty.getDebriefTime(this.hbNdx), this.maxPairingLengthInDays - 1)) {
								hbDepFound[duty.getNdx()] = true;
								hbArrFound[duty.getNdx()] = true;
							}
							maxSearchDept[duty.getNdx()] = this.maxPairingLengthInDays;
						} else {
							if (this.fwNetworkSearch(maxSearchDept, hbArrFound, heuristicNo, numOfCoveringsInDuties, numOfDistinctCoveringsInDuties, blockTimeOfCoveringsInDuties,
														duty, false, duty.getBriefTime(this.hbNdx), this.maxPairingLengthInDays - 2)) {
								hbArrFound[duty.getNdx()] = true;
								if (this.bwNetworkSearch(maxSearchDept, hbDepFound, heuristicNo, numOfCoveringsInDuties, numOfDistinctCoveringsInDuties, blockTimeOfCoveringsInDuties,
														duty, false, duty.getDebriefTime(this.hbNdx), this.maxPairingLengthInDays - 2)) {
									hbDepFound[duty.getNdx()] = true;
								}
							}
							maxSearchDept[duty.getNdx()] = this.maxPairingLengthInDays - 1;
						}
					}
			}
		}
		return this;
	}

//	private DutyNodeQualityMetric getDutyNodeQuality(int heuristicNo, int[] numOfCoveringsInDuties, int[] blockTimeOfCoveringsInDuties, DutyView d) {
//		DutyNodeQualityMetric res = this.bestDutyNodeQuality[d.getNdx()];
//		if (res == null) {
//			res = new DutyNodeQualityMetric();
//			res.addToQualityMetric(d, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
//			this.bestDutyNodeQuality[d.getNdx()] = res;
//		}
//		return res;
//	}

	private void checkAndUpdateCumulativeQuality(int heuristicNo, int[] numOfCoveringsInDuties, int[] numOfDistinctCoveringsInDuties,
													int[] blockTimeOfCoveringsInDuties, DutyView pd, DutyView nd) throws CloneNotSupportedException {
		NodeQualityMetric ndQ = this.bestNodeQuality[nd.getNdx()];
		NodeQualityMetric pdQ = this.bestNodeQuality[pd.getNdx()];
		if (ndQ == null) {
			ndQ = new NodeQualityMetric();
			ndQ.addToQualityMetric(nd, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
			this.bestNodeQuality[nd.getNdx()] = ndQ;
		}
		if (pdQ == null) {
			pdQ = new NodeQualityMetric();
			pdQ.addToQualityMetric(ndQ);
			pdQ.addToQualityMetric(pd, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
			this.bestNodeQuality[pd.getNdx()] = pdQ;
		} else {
			ndQ.addToQualityMetric(pd, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
			if (ndQ.isBetterThan(heuristicNo, pdQ)) {
				pdQ = (NodeQualityMetric) ndQ.clone();
				this.bestNodeQuality[pd.getNdx()] = pdQ;
			}
			ndQ.removeFromQualityMetric(pd, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
		}

//if (log)
//logger.debug(pd.getNdx() + ":" + pd.getFirstDepAirport().getCode() + "->" + pd.getLastArrAirport().getCode() + "; " + this.bestNodeQuality[pd.getNdx()] + " -->> " + 
//				nd.getNdx() + ":" + nd.getFirstDepAirport().getCode() + "->" + nd.getLastArrAirport().getCode() + "; " + this.bestNodeQuality[nd.getNdx()]);
	}

	private boolean fwNetworkSearch(int[] maxSearchDept, boolean[] hbArrFound, int heuristicNo, int[] numOfCoveringsInDuties, int[] numOfDistinctCoveringsInDuties,
									int[] blockTimeOfCoveringsInDuties, DutyView pd, boolean hbDep, LocalDateTime rootBriefTime, int dept) throws CloneNotSupportedException {
		boolean res = false;
		LegView[] nextLegs = this.nextBriefLegIndexByDutyNdx.getArray(pd.getNdx());
		for (LegView leg : nextLegs) {
			DutyView[] nextDuties = this.dutyIndexByDepLegNdx.getArray(leg.getNdx());
			for (DutyView nd: nextDuties) {
				if (nd.isValid(this.hbNdx)
						&& nd.hasPairing(this.hbNdx)
						&& (nd.isHbArr(this.hbNdx) || (dept > 1))
						&& ((nd.getNumOfLegs() > numOfDistinctCoveringsInDuties[nd.getNdx()])
								|| (nd.getNumOfLegs() == 1))
						/*
						 * TODO Instead of performing minus operations all the time, debriefTime could be reduced by 1 second by default. 
						 */
//						&& ((hbDep && nd.isHbArr(this.hbNdx) && (ChronoUnit.DAYS.between(rootBriefTime, nd.getDebriefTime(this.hbNdx).minusSeconds(1)) < this.maxPairingLengthInDays))
//								|| (hbDep && nd.isNonHbArr(this.hbNdx) && (ChronoUnit.DAYS.between(rootBriefTime, nd.getDebriefTime(this.hbNdx).minusSeconds(1)) < this.maxPairingLengthInDays - 1))
//								|| ((!hbDep) && nd.isHbArr(this.hbNdx) && (ChronoUnit.DAYS.between(rootBriefTime, nd.getDebriefTime(this.hbNdx).minusSeconds(1)) < this.maxPairingLengthInDays - 1))
//								|| ((!hbDep) && nd.isNonHbArr(this.hbNdx) && (ChronoUnit.DAYS.between(rootBriefTime, nd.getDebriefTime(this.hbNdx).minusSeconds(1)) < this.maxPairingLengthInDays - 2)))
////						&& this.dutyRuleContext.getConnectionCheckerProxy().areConnectable(this.hbNdx, pd, nd)
						) {
					if (nd.isHbArr(this.hbNdx)) {
						this.checkAndUpdateCumulativeQuality(heuristicNo, numOfCoveringsInDuties, numOfDistinctCoveringsInDuties, blockTimeOfCoveringsInDuties, pd, nd);
						this.addDuty(pd, nd);
						hbArrFound[nd.getNdx()] = true;
						res = true;
					} else
						if (dept > 1) {
							if (((maxSearchDept[nd.getNdx()] >= dept) && hbArrFound[nd.getNdx()])
									|| ((maxSearchDept[nd.getNdx()] < dept)
											&& this.fwNetworkSearch(maxSearchDept, hbArrFound, heuristicNo, numOfCoveringsInDuties, numOfDistinctCoveringsInDuties, blockTimeOfCoveringsInDuties,
														nd, hbDep, rootBriefTime, dept - 1))) {
								this.checkAndUpdateCumulativeQuality(heuristicNo, numOfCoveringsInDuties, numOfDistinctCoveringsInDuties, blockTimeOfCoveringsInDuties, pd, nd);
								this.addDuty(pd, nd);
								hbArrFound[nd.getNdx()] = true;
								res = true;
							}
						}
//					if (res)
						maxSearchDept[nd.getNdx()] = dept;
				}
			}
		}
		return res;
	}

	private boolean bwNetworkSearch(int[] maxSearchDept, boolean[] hbDepFound, int heuristicNo, int[] numOfCoveringsInDuties, int[] numOfDistinctCoveringsInDuties,
									int[] blockTimeOfCoveringsInDuties, DutyView nd, boolean hbArr, LocalDateTime rootDebriefTime, int dept) throws CloneNotSupportedException {
		boolean res = false;
		LegView[] prevLegs = this.prevDebriefLegIndexByDutyNdx.getArray(nd.getNdx());
		for (LegView leg : prevLegs) {
			DutyView[] prevDuties = this.dutyIndexByArrLegNdx.getArray(leg.getNdx());
			for (DutyView pd: prevDuties) {
				if (pd.isValid(this.hbNdx)
						&& pd.hasPairing(this.hbNdx)
						&& (pd.isHbDep(this.hbNdx) || (dept > 1))
						&& ((pd.getNumOfLegs() > numOfDistinctCoveringsInDuties[pd.getNdx()])
								|| (pd.getNumOfLegs() == 1))
						/*
						 * TODO Instead of performing minus operations all the time, debriefTime could be reduced by 1 second by default. 
						 */
//						&& ((hbArr && nd.isHbDep(this.hbNdx) && (ChronoUnit.DAYS.between(pd.getBriefTime(this.hbNdx), rootDebriefTime.minusSeconds(1)) < this.maxPairingLengthInDays))
//								|| (hbArr && nd.isNonHbDep(this.hbNdx) && (ChronoUnit.DAYS.between(pd.getBriefTime(this.hbNdx), rootDebriefTime.minusSeconds(1)) < this.maxPairingLengthInDays - 1))
//								|| ((!hbArr) && nd.isHbDep(this.hbNdx) && (ChronoUnit.DAYS.between(pd.getBriefTime(this.hbNdx), rootDebriefTime.minusSeconds(1)) < this.maxPairingLengthInDays - 1))
//								|| ((!hbArr) && nd.isNonHbDep(this.hbNdx) && (ChronoUnit.DAYS.between(pd.getBriefTime(this.hbNdx), rootDebriefTime.minusSeconds(1)) < this.maxPairingLengthInDays - 2)))
////						&& this.dutyRuleContext.getConnectionCheckerProxy().areConnectable(this.hbNdx, pd, nd)
						) {
					if (pd.isHbDep(this.hbNdx)) {
//						if (this.pairRuleContext.getStarterCheckerProxy().canBeStarter(this.hbNdx, pd)) {
							this.checkAndUpdateCumulativeQuality(heuristicNo, numOfCoveringsInDuties, numOfDistinctCoveringsInDuties, blockTimeOfCoveringsInDuties, pd, nd);
							this.addDuty(pd, nd);
							this.addSourceDuty(heuristicNo, pd);
							hbDepFound[pd.getNdx()] = true;
							res = true;
//						}
					} else
						if (dept > 1) {
							if (((maxSearchDept[pd.getNdx()] >= dept) && hbDepFound[pd.getNdx()])
									|| ((maxSearchDept[pd.getNdx()] < dept)
											&& this.bwNetworkSearch(maxSearchDept, hbDepFound, heuristicNo, numOfCoveringsInDuties, numOfDistinctCoveringsInDuties, blockTimeOfCoveringsInDuties,
														pd, hbArr, rootDebriefTime, dept - 1))) {
								this.checkAndUpdateCumulativeQuality(heuristicNo, numOfCoveringsInDuties, numOfDistinctCoveringsInDuties, blockTimeOfCoveringsInDuties, pd, nd);
								this.addDuty(pd, nd);
								hbDepFound[pd.getNdx()] = true;
								res = true;
							}
						}
//					if (res)
						maxSearchDept[pd.getNdx()] = dept;
				}
			}
		}
		return res;
	}
}
