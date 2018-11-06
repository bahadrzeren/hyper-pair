package org.heuros.hyperpair.heuristic;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

//	private static Logger logger = Logger.getLogger(PartialPairPricingNetwork.class);

	/*
	 * TODO Single base assumption!!!
	 */
	private int hbNdx = 0;

	private List<Duty> duties = null;
	private int maxPairingLengthInDays = 0;

	private DutyRuleContext dutyRuleContext = null;
	private PairRuleContext pairRuleContext = null;
	private OneDimIndexInt<DutyView> dutyIndexByDepLegNdx = null;
	private OneDimIndexInt<DutyView> dutyIndexByArrLegNdx = null;
	private OneDimUniqueIndexInt<LegView> nextBriefLegIndexByDutyNdx = null;
	private OneDimUniqueIndexInt<LegView> prevDebriefLegIndexByDutyNdx = null;

	private Set<Integer> sourceDuties = null;
	private List<HashSet<Integer>> dutyConnections = null;

	private int[] sourceDutyArray = null;
	private int[][] dutyConnectionArray = null;

	public PricingSubNetwork(List<Duty> duties,
								int maxPairingLengthInDays,
								DutyRuleContext dutyRuleContext,
								PairRuleContext pairRuleContext,
								DutyLegOvernightConnNetwork pricingNetwork) {
		this.duties = duties;
		this.sourceDuties = new HashSet<Integer>();
		this.dutyConnections = new ArrayList<HashSet<Integer>>(this.duties.size());
		this.sourceDutyArray = new int[0];
		this.dutyConnectionArray = new int[this.duties.size()][0];

		this.maxPairingLengthInDays = maxPairingLengthInDays;

		this.dutyRuleContext = dutyRuleContext;
		this.pairRuleContext = pairRuleContext;

		this.dutyIndexByDepLegNdx = pricingNetwork.getDutyIndexByDepLegNdx();
		this.dutyIndexByArrLegNdx = pricingNetwork.getDutyIndexByArrLegNdx();
		this.nextBriefLegIndexByDutyNdx = pricingNetwork.getNextBriefLegIndexByDutyNdx();
		this.prevDebriefLegIndexByDutyNdx = pricingNetwork.getPrevDebriefLegIndexByDutyNdx();
	}

	private boolean addSourceDuty(DutyView d) {
		if (this.sourceDuties.add(d.getNdx())) {
			this.sourceDutyArray = ArrayUtils.add(this.sourceDutyArray, d.getNdx());
			return true;
		}
		return false;
	}

	private boolean addDuty(DutyView pd, DutyView nd) {
		HashSet<Integer> nextDutyNdxs = this.dutyConnections.get(pd.getNdx());
		if (nextDutyNdxs == null) {
			nextDutyNdxs = new HashSet<Integer>();
			this.dutyConnections.set(pd.getNdx(), nextDutyNdxs);
		}
		if (nextDutyNdxs.add(nd.getNdx())) {
			dutyConnectionArray[pd.getNdx()] = ArrayUtils.add(dutyConnectionArray[pd.getNdx()], nd.getNdx());
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

	public int[] getSourceDuties() {
		return this.sourceDutyArray;
	}

	public int[][] getDutyConnections() {
		return this.dutyConnectionArray;
	}

	public int[] getNextDuties(DutyView pd) {
		return this.dutyConnectionArray[pd.getNdx()];
	}

	public PricingSubNetwork build(int heuristicNo, Duty[] sourceDuties) {

//		int[] maxSearchDept = new int[this.duties.size()];

		for (Duty duty: sourceDuties) {

			if (duty.isValid(this.hbNdx)) {
				if (duty.isHbDep(this.hbNdx)) {
					if (duty.isHbArr(this.hbNdx)) {
						if (this.pairRuleContext.getStarterCheckerProxy().canBeStarter(this.hbNdx, duty))
							this.addSourceDuty(duty);
					} else 
						if (heuristicNo > 0) {
							if (this.pairRuleContext.getStarterCheckerProxy().canBeStarter(this.hbNdx, duty)) {
								if (this.fwNetworkSearch(duty, true, duty.getBriefTime(this.hbNdx), this.maxPairingLengthInDays - 1)) {
									this.addSourceDuty(duty);
								}
							}
						}
				} else
					if (heuristicNo > 0) {
						if (duty.isHbArr(this.hbNdx)) {
							this.bwNetworkSearch(duty, true, duty.getDebriefTime(this.hbNdx), this.maxPairingLengthInDays - 1);
						} else {
							if (this.fwNetworkSearch(duty, false, duty.getBriefTime(this.hbNdx), this.maxPairingLengthInDays - 2))
								this.bwNetworkSearch(duty, false, duty.getDebriefTime(this.hbNdx), this.maxPairingLengthInDays - 2);
						}
					}
			}
		}
		return this;
	}

	private boolean fwNetworkSearch(DutyView pd, boolean hbDep, LocalDateTime rootBriefTime, int dept) {
		boolean res = false;
		LegView[] nextLegs = this.nextBriefLegIndexByDutyNdx.getArray(pd.getNdx());
		for (LegView leg : nextLegs) {
			DutyView[] nextDuties = this.dutyIndexByDepLegNdx.getArray(leg.getNdx());
			for (DutyView nd: nextDuties) {
				if (nd.isValid(this.hbNdx)
//						&& (maxSearchDept[nd.getNdx()] < dept)
						&& (nd.isHbArr(this.hbNdx) || (dept > 1))
						/*
						 * TODO Instead of performing minus operations all the time, debriefTime could be reduced by 1 second by default. 
						 */
						&& ((hbDep && nd.isHbArr(this.hbNdx) && (ChronoUnit.DAYS.between(rootBriefTime, nd.getDebriefTime(this.hbNdx).minusSeconds(1)) < this.maxPairingLengthInDays))
								|| (hbDep && nd.isNonHbArr(this.hbNdx) && (ChronoUnit.DAYS.between(rootBriefTime, nd.getDebriefTime(this.hbNdx).minusSeconds(1)) < this.maxPairingLengthInDays - 1))
								|| ((!hbDep) && nd.isHbArr(this.hbNdx) && (ChronoUnit.DAYS.between(rootBriefTime, nd.getDebriefTime(this.hbNdx).minusSeconds(1)) < this.maxPairingLengthInDays - 1))
								|| ((!hbDep) && nd.isNonHbArr(this.hbNdx) && (ChronoUnit.DAYS.between(rootBriefTime, nd.getDebriefTime(this.hbNdx).minusSeconds(1)) < this.maxPairingLengthInDays - 2)))
						&& this.dutyRuleContext.getConnectionCheckerProxy().areConnectable(this.hbNdx, pd, nd)) {
					if (nd.isHbArr(this.hbNdx)) {
						this.addDuty(pd, nd);
						res = true;
					} else
						if (dept > 1) {
//							root.isHbDep(this.hbNdx)
							if (this.fwNetworkSearch(nd, hbDep, rootBriefTime, dept - 1)) {
								res = true;
								this.addDuty(pd, nd);
							}
						}
//					if (res)
//						maxSearchDept[nd.getNdx()] = dept;
				}
			}
		}
		return res;
	}

	private boolean bwNetworkSearch(DutyView nd, boolean hbArr, LocalDateTime rootDebriefTime, int dept) {
		boolean res = false;
		LegView[] prevLegs = this.prevDebriefLegIndexByDutyNdx.getArray(nd.getNdx());
		for (LegView leg : prevLegs) {
			DutyView[] prevDuties = this.dutyIndexByArrLegNdx.getArray(leg.getNdx());
			for (DutyView pd: prevDuties) {
				if (pd.isValid(this.hbNdx)
//						&& (maxSearchDept[pd.getNdx()] < dept)
						&& (pd.isHbDep(this.hbNdx) || (dept > 1))
						/*
						 * TODO Instead of performing minus operations all the time, debriefTime could be reduced by 1 second by default. 
						 */
						&& ((hbArr && nd.isHbDep(this.hbNdx) && (ChronoUnit.DAYS.between(pd.getBriefTime(this.hbNdx), rootDebriefTime.minusSeconds(1)) < this.maxPairingLengthInDays))
								|| (hbArr && nd.isNonHbDep(this.hbNdx) && (ChronoUnit.DAYS.between(pd.getBriefTime(this.hbNdx), rootDebriefTime.minusSeconds(1)) < this.maxPairingLengthInDays - 1))
								|| ((!hbArr) && nd.isHbDep(this.hbNdx) && (ChronoUnit.DAYS.between(pd.getBriefTime(this.hbNdx), rootDebriefTime.minusSeconds(1)) < this.maxPairingLengthInDays - 1))
								|| ((!hbArr) && nd.isNonHbDep(this.hbNdx) && (ChronoUnit.DAYS.between(pd.getBriefTime(this.hbNdx), rootDebriefTime.minusSeconds(1)) < this.maxPairingLengthInDays - 2)))
						&& this.dutyRuleContext.getConnectionCheckerProxy().areConnectable(this.hbNdx, pd, nd)) {
					if (pd.isHbDep(this.hbNdx)) {
						if (this.pairRuleContext.getStarterCheckerProxy().canBeStarter(this.hbNdx, pd)) {
							this.addDuty(pd, nd);
							this.addSourceDuty(pd);
							res = true;
						}
					} else
						if (dept > 1) {
//							root.isHbArr(this.hbNdx)
							if (this.bwNetworkSearch(pd, hbArr, rootDebriefTime, dept - 1)) {
								res = true;
								this.addDuty(pd, nd);
							}
						}
//					if (res)
//						maxSearchDept[pd.getNdx()] = dept;
				}
			}
		}
		return res;
	}
}
