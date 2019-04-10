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

		for (int i = 0; i < this.numOfProbablePairings.length; i++) {
			this.numOfProbablePairings[i] = 0;
		}

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
							this.fwSearch(dutyStates,
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

	private void fwSearch(DutyState[] dutyStates,
							int[] numOfProbablePairings,
							Duty fd, Duty ld, int fromNdxInc, int toNdxExc,
							int numOfDhs, int totalActiveBlockTime,
							LocalDate maxMinDateDept) {
		Leg[] nls = this.nextBriefLegIndexByDutyNdx.getArray(ld.getNdx());
		for (Leg nl : nls) {
			Duty[] nds = this.dutyIndexByDepLegNdx.getArray(nl.getNdx());
			for (Duty nd : nds) {

//if ((fd.getNdx() == 41945) && (ld.getNdx() == 41945) && (nd.getNdx() == 47287))
//System.out.println();
//if ((fd.getNdx() == 41945) && (ld.getNdx() == 47287) && (nd.getNdx() == 63888))
//System.out.println();
//if ((fd.getNdx() == 34815) && (ld.getNdx() == 34815) && (nd.getNdx() == 42524))
//System.out.println();

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
							&& (ChronoUnit.DAYS.between(fd.getBriefDayBeginning(this.hbNdx), nd.getDebriefDayEnding(this.hbNdx).minusSeconds(1)) < (toNdxExc - fromNdxInc + HeurosSystemParam.pairEnumBuffer))) {
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
								&& (ChronoUnit.DAYS.between(fd.getBriefDayBeginning(this.hbNdx), nd.getDebriefDayEnding(this.hbNdx).minusSeconds(1)) < (toNdxExc - fromNdxInc + HeurosSystemParam.pairEnumBuffer))) {
							pairing[toNdxExc - 1] = nd;
							this.fwSearch(dutyStates,
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

//if ((fd.getNdx() == 63888) && (ld.getNdx() == 63888) && (pd.getNdx() == 47287))
//System.out.println();
//if ((fd.getNdx() == 47287) && (ld.getNdx() == 63888) && (pd.getNdx() == 41945))
//System.out.println();
//if ((fd.getNdx() == 42524) && (ld.getNdx() == 70087) && (pd.getNdx() == 34815))
//System.out.println();

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
							if (ChronoUnit.DAYS.between(pd.getBriefDayBeginning(this.hbNdx), ld.getDebriefDayEnding(this.hbNdx).minusSeconds(1)) < (toNdxExc - fromNdxInc + HeurosSystemParam.pairEnumBuffer)
									) {
								pairing[fromNdxInc] = pd;
								this.pairListener.onPairingFound(pairing, fromNdxInc, toNdxExc, newNumOfDhs, totalActiveBlockTime + pd.getBlockTimeInMinsActive());
								numOfProbablePairings[toNdxExc - fromNdxInc - 1]++;
								pairing[fromNdxInc] = null;
							}
						} else
							if ((toNdxExc - fromNdxInc < HeurosSystemParam.maxSearchDeptForScoreCalculations)
									&& ChronoUnit.DAYS.between(pd.getBriefDayBeginning(this.hbNdx), ld.getDebriefDayEnding(this.hbNdx).minusSeconds(1)) < (toNdxExc - fromNdxInc + HeurosSystemParam.pairEnumBuffer)
									) {
								pairing[fromNdxInc] = pd;
								this.fwSearch(dutyStates,
												numOfProbablePairings,
												pd, ld, fromNdxInc, toNdxExc + 1,
												newNumOfDhs, totalActiveBlockTime + pd.getBlockTimeInMinsActive(),
												pd.getBriefDay(this.hbNdx).plusDays(HeurosSystemParam.maxPairingLengthInDays));
								pairing[fromNdxInc] = null;
							}
					} else
						if (pd.isNonHbDep(this.hbNdx)
								&& (toNdxExc - fromNdxInc < HeurosSystemParam.maxSearchDeptForScoreCalculations)
								/*
								 * TODO
								 * This line below are put because of no rule validation code is done here!
								 * Rule validation is done in just briefing time context.
								 */
								&& (!pd.getMinNextBriefTime(hbNdx).isAfter(fd.getBriefTime(hbNdx)))
								&& (pd.getMinNextBriefTime(hbNdx).plusHours(HeurosSystemParam.maxNetDutySearchDeptInHours + 1).isAfter(fd.getBriefTime(hbNdx)))
								&& (maxMinDateDept.isBefore(pd.getBriefDay(this.hbNdx))
										|| maxMinDateDept.isEqual(pd.getBriefDay(this.hbNdx)))
								&& (ChronoUnit.DAYS.between(pd.getBriefDayBeginning(this.hbNdx), ld.getDebriefDayEnding(this.hbNdx).minusSeconds(1)) < (toNdxExc - fromNdxInc + HeurosSystemParam.pairEnumBuffer))) {
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

		int numOfDhs = duty.getNumOfLegsPassive() + dutyStates[duty.getNdx()].numOfCoverings;

		if (numOfDhs == 1) {

			/*
			 * We need ActiveBlockTime in the beginning to be able to decide whether the first state of the duty is efficient.
			 */
			int totalActiveBlockTime = duty.getBlockTimeInMinsActive();	//	 - dutyState.blockTimeOfCoveringsActive;

			pairing[HeurosSystemParam.maxPairingLengthInDays - 1] = duty;

			if (duty.isHbDep(this.hbNdx)) {
				if (duty.isHbArr(this.hbNdx)) {
					this.pairListener.onPairingFound(pairing,
														HeurosSystemParam.maxPairingLengthInDays - 1, HeurosSystemParam.maxPairingLengthInDays,
														numOfDhs, totalActiveBlockTime);
					numOfProbablePairings[0]++;
				} else
					if (HeurosSystemParam.maxSearchDeptForScoreCalculations > 1) {
						this.fwSearch(dutyStates,
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

//	public boolean validatePair(Duty[] testPair, DutyState[] dutyStates) {
//
//		int numOfDhs = dutyStates[testPair[0].getNdx()].numOfCoverings;
//
//		if ((testPair[0].getNumOfLegsPassive() == 0)
//				&& (numOfDhs <= 1)) {
//
//			/*
//			 * We need ActiveBlockTime in the beginning to be able to decide whether the first state of the duty is efficient.
//			 */
//			int totalActiveBlockTime = testPair[0].getBlockTimeInMinsActive();	//	 - dutyState.blockTimeOfCoveringsActive;
//
//			if (testPair[0].isHbDep(this.hbNdx)) {
//				if (testPair[0].isHbArr(this.hbNdx)) {
//					return true;
//				} else
//					if (HeurosSystemParam.maxSearchDeptForScoreCalculations > 1) {
//						return this.validateFwSearch(testPair,
//														dutyStates,
//														1,
//														numOfDhs,
//														totalActiveBlockTime,
//														testPair[0].getBriefDay(this.hbNdx).plusDays(HeurosSystemParam.maxPairingLengthInDays));
//					}
//			} else
//				System.out.println("Must be an unreachable line!");
//		}
//
//		return false;
//	}
//
//	private boolean validateFwSearch(Duty[] testPair,
//										DutyState[] dutyStates,
//										int nextNdx,
//										int numOfDhs,
//										int totalActiveBlockTime,
//										LocalDate maxMinDateDept) {
//		Duty nd = testPair[nextNdx];
//		if ((nd.getNumOfLegsPassive() == 0)
//				&& ((dutyStates[nd.getNdx()].numOfCoverings + numOfDhs) <= 1)) {
//	
//			int newNumOfDhs = numOfDhs + nd.getNumOfLegsPassive();
//			if (dutyStates != null)
//				newNumOfDhs += dutyStates[nd.getNdx()].numOfCoverings;
//	
//			if (nd.isHbArr(this.hbNdx)
//					&& maxMinDateDept.isAfter(nd.getDebriefDay(this.hbNdx))
//					/*
//					 * TODO
//					 * This line below are put because of no rule validation code is done here!
//					 * Rule validation is done in just briefing time context.
//					 */
//					&& (ChronoUnit.DAYS.between(testPair[0].getBriefTime(this.hbNdx), nd.getDebriefTime(this.hbNdx).minusSeconds(1)) < (nextNdx + 2))) {
//				return true;
//			} else
//				if (nd.isNonHbArr(this.hbNdx)
//						&& (nextNdx + 1 < HeurosSystemParam.maxSearchDeptForScoreCalculations)
//						&& maxMinDateDept.isAfter(nd.getDebriefDay(this.hbNdx))
//						/*
//						 * TODO
//						 * This line below are put because of no rule validation code is done here!
//						 * Rule validation is done in just briefing time context.
//						 */
//						&& (ChronoUnit.DAYS.between(testPair[0].getBriefTime(this.hbNdx), nd.getDebriefTime(this.hbNdx).minusSeconds(1)) < (nextNdx + 2))) {
//					return this.validateFwSearch(testPair,
//													dutyStates,
//													nextNdx + 1,
//													newNumOfDhs,
//													totalActiveBlockTime + nd.getBlockTimeInMinsActive(),
//													maxMinDateDept);
//				}
//		}
//		return false;
//	}
}
