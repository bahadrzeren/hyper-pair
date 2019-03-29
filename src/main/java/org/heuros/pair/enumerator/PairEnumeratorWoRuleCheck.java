package org.heuros.pair.enumerator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.apache.log4j.Logger;
import org.heuros.context.PairOptimizationContext;
import org.heuros.core.data.ndx.OneDimIndexInt;
import org.heuros.core.data.ndx.OneDimUniqueIndexInt;
import org.heuros.data.DutyLegOvernightConnNetwork;
import org.heuros.data.model.Duty;
import org.heuros.data.model.Leg;
import org.heuros.pair.conf.HeurosSystemParam;
import org.heuros.pair.heuro.state.DutyState;

public class PairEnumeratorWoRuleCheck {

	private static Logger logger = Logger.getLogger(PairEnumeratorWoRuleCheck.class);

	/*
	 * TODO Single base assumption!!!
	 */
	private int hbNdx = 0;

	private PairListener pairListener = null;

	private OneDimIndexInt<Duty> dutyIndexByDepLegNdx = null;
	private OneDimIndexInt<Duty> dutyIndexByArrLegNdx = null;
	private OneDimUniqueIndexInt<Leg> nextBriefLegIndexByDutyNdx = null;
	private OneDimUniqueIndexInt<Leg> prevDebriefLegIndexByDutyNdx = null;

	private List<Duty> duties = null;

	private Duty[] pairing = null;
	private int[] numOfProbablePairings = null;

	public PairEnumeratorWoRuleCheck(PairOptimizationContext pairOptimizationContext,
										DutyLegOvernightConnNetwork pricingNetwork,
										PairListener pairListener) {
		this.duties = pairOptimizationContext.getDutyRepository().getModels();

		this.pairListener = pairListener;

		this.dutyIndexByDepLegNdx = pricingNetwork.getDutyIndexByDepLegNdx();
		this.dutyIndexByArrLegNdx = pricingNetwork.getDutyIndexByArrLegNdx();
		this.nextBriefLegIndexByDutyNdx = pricingNetwork.getNextBriefLegIndexByDutyNdx();
		this.prevDebriefLegIndexByDutyNdx = pricingNetwork.getPrevDebriefLegIndexByDutyNdx();

		this.pairing = new Duty[HeurosSystemParam.maxPairingLengthInDays * 2 - 1];
		this.numOfProbablePairings = new int[HeurosSystemParam.maxPairingLengthInDays];
	}

	public void enumerateAllPairings(DutyState[] dutyStates) {

		logger.info("Pairings enumeration is started!");

		for (Duty duty : this.duties) {
			if (duty.isValid(this.hbNdx)
				&& duty.hasPairing(this.hbNdx)
				&& (duty.getNumOfLegsPassive() == 0)
				&& ((dutyStates == null)
						|| (dutyStates[duty.getNdx()].numOfCoverings == 0))) {

				pairing[HeurosSystemParam.maxPairingLengthInDays - 1] = duty;

				int numOfDhs = duty.getNumOfLegsPassive();
				if (dutyStates != null)
					numOfDhs += dutyStates[duty.getNdx()].numOfCoverings;
				int totalActiveBlockTime = duty.getBlockTimeInMinsActive();

				if (duty.isHbDep(this.hbNdx)) {
					if (duty.isHbArr(this.hbNdx)) {
						this.pairListener.onPairingFound(pairing,
															HeurosSystemParam.maxPairingLengthInDays - 1,
															HeurosSystemParam.maxPairingLengthInDays,
															numOfDhs,
															totalActiveBlockTime);
						numOfProbablePairings[0]++;
					} else
						if (HeurosSystemParam.maxSearchDeptForScoreCalculations > 1) {
							this.fwSearch(true,
											dutyStates,
											numOfProbablePairings,
											duty, duty, HeurosSystemParam.maxPairingLengthInDays - 1, HeurosSystemParam.maxPairingLengthInDays + 1,
											numOfDhs, totalActiveBlockTime, duty.getBriefDay(this.hbNdx).plusDays(HeurosSystemParam.maxPairingLengthInDays));
						}
				}
			}
		}

		for (int i = 0; i < numOfProbablePairings.length; i++) {
			logger.info(numOfProbablePairings[i] + " num of probable " + (i + 1) + "th day pairings are found!");
		}
	}

	private void fwSearch(boolean hbDep,
							DutyState[] dutyStates,
							int[] numOfProbablePairings,
							Duty fd, Duty ld, int fromNdxInc, int toNdxExc,
							int numOfDhs, int totalActiveBlockTime,
							LocalDate maxMinDateDept) {
		Leg[] nls = this.nextBriefLegIndexByDutyNdx.getArray(ld.getNdx());
		for (Leg nl : nls) {
			Duty[] nds = this.dutyIndexByDepLegNdx.getArray(nl.getNdx());
			for (Duty nd : nds) {
				if ((nd.getNumOfLegsPassive() == 0)
						&& ((dutyStates == null)
								|| (dutyStates[nd.getNdx()].numOfCoverings == 0))) {

					int newNumOfDhs = numOfDhs + nd.getNumOfLegsPassive();
					if (dutyStates != null)
						newNumOfDhs += dutyStates[nd.getNdx()].numOfCoverings;

					if (nd.isHbArr(this.hbNdx)
							&& maxMinDateDept.isAfter(nd.getDebriefDay(this.hbNdx))
							/*
							 * TODO
							 * This line below are put because of no rule validation code is done here!
							 * Rule validation is done in just briefing time context.
							 */
							&& (ChronoUnit.DAYS.between(fd.getBriefTime(this.hbNdx), nd.getDebriefTime(this.hbNdx).minusSeconds(1)) < (HeurosSystemParam.maxPairingLengthInDays - 1))) {
						pairing[toNdxExc - 1] = nd;
						this.pairListener.onPairingFound(pairing, fromNdxInc, toNdxExc, newNumOfDhs, totalActiveBlockTime + nd.getBlockTimeInMinsActive());
						numOfProbablePairings[toNdxExc - fromNdxInc - 1]++;
						pairing[toNdxExc - 1] = null;
					} else
						if (nd.isNonHbArr(this.hbNdx)
								&& (toNdxExc - fromNdxInc < HeurosSystemParam.maxSearchDeptForScoreCalculations)
								&& maxMinDateDept.isAfter(nd.getDebriefDay(this.hbNdx))
								/*
								 * TODO
								 * This line below are put because of no rule validation code is done here!
								 * Rule validation is done in just briefing time context.
								 */
								&& (ChronoUnit.DAYS.between(fd.getBriefTime(this.hbNdx), nd.getDebriefTime(this.hbNdx).minusSeconds(1)) < (HeurosSystemParam.maxPairingLengthInDays - 2))) {
							pairing[toNdxExc - 1] = nd;
							this.fwSearch(hbDep,
											dutyStates,
											numOfProbablePairings,
											fd, nd, fromNdxInc, toNdxExc + 1,
											newNumOfDhs, totalActiveBlockTime + nd.getBlockTimeInMinsActive(),
											maxMinDateDept);
							pairing[toNdxExc - 1] = null;
						}
				}
			}
		}
	}

	private void bwSearch(boolean hbArr,
							DutyState[] dutyStates,
							int[] numOfProbablePairings,
							Duty fd, Duty ld, int fromNdxInc, int toNdxExc,
							int numOfDhs, int totalActiveBlockTime,
							LocalDate maxMinDateDept) {
		Leg[] pls = this.prevDebriefLegIndexByDutyNdx.getArray(fd.getNdx());
		for (Leg pl : pls) {
			Duty[] pds = this.dutyIndexByArrLegNdx.getArray(pl.getNdx());
			for (Duty pd : pds) {
				if ((pd.getNumOfLegsPassive() == 0)
						&& ((dutyStates == null)
								|| (dutyStates[pd.getNdx()].numOfCoverings == 0))) {

					int newNumOfDhs = numOfDhs + pd.getNumOfLegsPassive();
					if (dutyStates != null)
						newNumOfDhs += dutyStates[pd.getNdx()].numOfCoverings;

					if (pd.isHbDep(this.hbNdx)
							/*
							 * TODO
							 * This line below are put because of no rule validation code is done here!
							 * Rule validation is done in just briefing time context.
							 */
							&& (!pd.getMinNextBriefTime(hbNdx).isAfter(fd.getBriefTime(hbNdx)))
							&& (pd.getMinNextBriefTime(hbNdx).plusHours(HeurosSystemParam.maxNetDutySearchDeptInHours + 1).isAfter(fd.getBriefTime(hbNdx)))
							&& (maxMinDateDept.isBefore(pd.getBriefDay(this.hbNdx))
									|| maxMinDateDept.isEqual(pd.getBriefDay(this.hbNdx)))) {
						if (hbArr) {
							if (ChronoUnit.DAYS.between(pd.getBriefTime(this.hbNdx), ld.getDebriefTime(this.hbNdx).minusSeconds(1)) < (HeurosSystemParam.maxPairingLengthInDays - 1)) {
								pairing[fromNdxInc] = pd;
								this.pairListener.onPairingFound(pairing, fromNdxInc, toNdxExc, newNumOfDhs, totalActiveBlockTime + pd.getBlockTimeInMinsActive());
								numOfProbablePairings[toNdxExc - fromNdxInc - 1]++;
								pairing[fromNdxInc] = null;
							}
						} else
							if (ChronoUnit.DAYS.between(pd.getBriefTime(this.hbNdx), ld.getDebriefTime(this.hbNdx).minusSeconds(1)) < (HeurosSystemParam.maxPairingLengthInDays - 2)) {
								pairing[fromNdxInc] = pd;
								this.fwSearch(true,
												dutyStates,
												numOfProbablePairings,
												pd, ld, fromNdxInc, toNdxExc + 1,
												newNumOfDhs, totalActiveBlockTime + pd.getBlockTimeInMinsActive(),
												pd.getBriefDay(this.hbNdx).plusDays(HeurosSystemParam.maxPairingLengthInDays));
								pairing[fromNdxInc] = null;
							}
					} else
						if (pd.isNonHbArr(this.hbNdx)
								&& (toNdxExc - fromNdxInc < HeurosSystemParam.maxSearchDeptForScoreCalculations)
								&& (maxMinDateDept.isBefore(pd.getBriefDay(this.hbNdx))
										|| maxMinDateDept.isEqual(pd.getBriefDay(this.hbNdx)))
								/*
								 * TODO
								 * This line below are put because of no rule validation code is done here!
								 * Rule validation is done in just briefing time context.
								 */
								&& (ChronoUnit.DAYS.between(pd.getBriefTime(this.hbNdx), ld.getDebriefTime(this.hbNdx).minusSeconds(1)) < (HeurosSystemParam.maxPairingLengthInDays - 2))) {
							pairing[fromNdxInc] = pd;
							this.bwSearch(hbArr,
											dutyStates,
											numOfProbablePairings,
											pd, ld, fromNdxInc - 1, toNdxExc,
											newNumOfDhs, totalActiveBlockTime + pd.getBlockTimeInMinsActive(),
											maxMinDateDept);
							pairing[fromNdxInc] = null;
						}
				}
			}
		}
	}

	public void enumerateAllPairings(Duty duty, DutyState[] dutyStates) {

		DutyState dutyState = dutyStates[duty.getNdx()];

		pairing[HeurosSystemParam.maxPairingLengthInDays - 1] = duty;

		int numOfDhs = duty.getNumOfLegsPassive() + dutyState.numOfCoverings;
		/*
		 * We need ActiveBlockTime in the beginning to be able to decide whether the first state of the duty is efficient.
		 */
		int totalActiveBlockTime = duty.getBlockTimeInMinsActive();	//	 - dutyState.blockTimeOfCoveringsActive;

		if (numOfDhs == 1) {
			if (duty.isHbDep(this.hbNdx)) {
				if (duty.isHbArr(this.hbNdx)) {
					this.pairListener.onPairingFound(pairing,
														HeurosSystemParam.maxPairingLengthInDays - 1, HeurosSystemParam.maxPairingLengthInDays,
														numOfDhs, totalActiveBlockTime);
					numOfProbablePairings[0]++;
				} else
					if (HeurosSystemParam.maxSearchDeptForScoreCalculations > 1) {
						this.fwSearch(true,
										dutyStates,
										numOfProbablePairings,
										duty, duty, HeurosSystemParam.maxPairingLengthInDays - 1, HeurosSystemParam.maxPairingLengthInDays + 1,
										numOfDhs, totalActiveBlockTime, duty.getBriefDay(this.hbNdx).plusDays(HeurosSystemParam.maxPairingLengthInDays));
					}
			} else
				if (duty.isHbArr(this.hbNdx)) {
					if (HeurosSystemParam.maxSearchDeptForScoreCalculations > 1) {
						this.bwSearch(true,
										dutyStates,
										numOfProbablePairings,
										duty, duty, HeurosSystemParam.maxPairingLengthInDays - 2, HeurosSystemParam.maxPairingLengthInDays,
										numOfDhs, totalActiveBlockTime, duty.getDebriefDay(this.hbNdx).minusDays(HeurosSystemParam.maxPairingLengthInDays - 1));
					}
				} else
					if (HeurosSystemParam.maxSearchDeptForScoreCalculations > 2) {
						this.bwSearch(false,
										dutyStates,
										numOfProbablePairings,
										duty, duty, HeurosSystemParam.maxPairingLengthInDays - 2, HeurosSystemParam.maxPairingLengthInDays,
										numOfDhs, totalActiveBlockTime, duty.getDebriefDay(this.hbNdx).minusDays(HeurosSystemParam.maxPairingLengthInDays - 2));
					}
		}
	}
}
