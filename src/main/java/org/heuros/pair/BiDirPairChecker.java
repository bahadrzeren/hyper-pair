package org.heuros.pair;

import java.util.concurrent.Callable;

import org.heuros.context.PairOptimizationContext;
import org.heuros.data.DutyLegOvernightConnNetwork;
import org.heuros.data.model.Duty;
import org.heuros.data.model.Leg;
import org.heuros.pair.conf.HeurosSystemParam;
import org.heuros.pair.enumerator.PairEnumeratorWoRuleCheck;
import org.heuros.pair.enumerator.PairListener;

public class BiDirPairChecker implements Callable<Boolean>, PairListener {

//	private static Logger logger = Logger.getLogger(BiDirPairChecker.class);

	/*
	 * TODO Single base assumption!!!
	 */
	private int hbNdx = 0;

//	private OneDimIndexInt<Duty> dutyIndexByDepLegNdx = null;
////	private OneDimIndexInt<Duty> dutyIndexByArrLegNdx = null;
//	private OneDimUniqueIndexInt<Leg> nextBriefLegIndexByDutyNdx = null;
////	private OneDimUniqueIndexInt<Leg> prevDebriefLegIndexByDutyNdx = null;

//	private List<Leg> legs = null;
//	private List<Duty> duties = null;
//	private OneDimIndexInt<Duty> dutyIndexByLegNdx = null;

	private PairEnumeratorWoRuleCheck pairEnumerator = null;

	public BiDirPairChecker(PairOptimizationContext pairOptimizationContext,
							DutyLegOvernightConnNetwork pricingNetwork) {
////		this.legs = pairOptimizationContext.getLegRepository().getModels();
////		this.duties = pairOptimizationContext.getDutyRepository().getModels();
////		this.dutyIndexByLegNdx = pairOptimizationContext.getDutyIndexByLegNdx();
//		this.dutyIndexByDepLegNdx = pricingNetwork.getDutyIndexByDepLegNdx();
////		this.dutyIndexByArrLegNdx = pricingNetwork.getDutyIndexByArrLegNdx();
//		this.nextBriefLegIndexByDutyNdx = pricingNetwork.getNextBriefLegIndexByDutyNdx();
////		this.prevDebriefLegIndexByDutyNdx = pricingNetwork.getPrevDebriefLegIndexByDutyNdx();

		pairEnumerator = new PairEnumeratorWoRuleCheck(pairOptimizationContext,
														pricingNetwork,
														this);
	}

//	private void setIncludingPairings(Duty[] pairing, int numOfDuties, int numOfDhs, int totalActiveBlockTime) {
//		for (int i = 0; i < numOfDuties; i++) {
//			for (int j = 0; j < pairing[i].getNumOfLegs(); j++) {
//				Leg l = pairing[i].getLegs().get(j);
//
//				if (l.isCover()
//						&& l.hasPair(hbNdx)) {
//					l.incNumOfIncludingPairs();
//					if (totalActiveBlockTime >= HeurosSystemParam.effectiveDutyBlockHourLimit * numOfDuties)
//						l.incNumOfIncludingEffectivePairs();
//					if (numOfDhs == 0) {
//						l.incNumOfIncludingPairsWoDh();
//						if (totalActiveBlockTime >= HeurosSystemParam.effectiveDutyBlockHourLimit * numOfDuties)
//							l.incNumOfIncludingEffectivePairsWoDh();
//					}
//				}
//			}
//		}
//	}

	@Override
	public void onPairingFound(Duty[] pairing, int fromNdxInc, int toNdxExc, int numOfDhs, int totalActiveBlockTime) {
		for (int i = fromNdxInc; i < toNdxExc; i++) {
			for (int j = 0; j < pairing[i].getNumOfLegs(); j++) {
				Leg l = pairing[i].getLegs().get(j);

				if (l.isCover()
						&& l.hasPair(hbNdx)) {
					l.incNumOfIncludingPairs();
					if (totalActiveBlockTime >= HeurosSystemParam.effectiveDutyBlockHourLimit * (toNdxExc - fromNdxInc))
						l.incNumOfIncludingEffectivePairs();
					if (numOfDhs == 0) {
						l.incNumOfIncludingPairsWoDh();
						if (totalActiveBlockTime >= HeurosSystemParam.effectiveDutyBlockHourLimit * (toNdxExc - fromNdxInc))
							l.incNumOfIncludingEffectivePairsWoDh();
					}
				}
			}
		}
	}

	@Override
	public Boolean call() {

		this.pairEnumerator.enumerateAllPairings(null);

//		logger.info("Pairings check is started!");
//
//		Duty[] pairing = new Duty[HeurosSystemParam.maxPairingLengthInDays];
//
//		int[] numOfProbablePairings = new int[HeurosSystemParam.maxPairingLengthInDays];
//
//		for (Duty duty : this.duties) {
//			if (duty.isValid(this.hbNdx)
//				&& duty.hasPairing(this.hbNdx)
////				&& (duty.getNumOfLegsPassive() == 0)
//				) {
//
//				pairing[0] = duty;
//				int numOfDhs = duty.getNumOfLegsPassive();
//				int totalActiveBlockTime = duty.getBlockTimeInMinsActive();
//
//				if (duty.isHbDep(this.hbNdx)) {
//					if (duty.isHbArr(this.hbNdx)) {
//						setIncludingPairings(pairing, 1, numOfDhs, totalActiveBlockTime);
//						numOfProbablePairings[0]++;
//					} else {
//						LocalDate maxMinDateDept = duty.getBriefDay(this.hbNdx).plusDays(HeurosSystemParam.maxPairingLengthInDays);
//						this.fwSearch(numOfProbablePairings, pairing, duty, duty, numOfDhs, totalActiveBlockTime, 1, 1, maxMinDateDept);
//
////						Leg[] nls = this.nextBriefLegIndexByDutyNdx.getArray(duty.getNdx());
////						for (Leg nl : nls) {
////							Duty[] nds = this.dutyIndexByDepLegNdx.getArray(nl.getNdx());
////							for (Duty nd : nds) {
////								if (nd.isHbArr(this.hbNdx)
////										&& (maxMinDateDept.isAfter(nd.getDebriefDay(this.hbNdx)))
////										/*
////										 * TODO
////										 * This line below are put because of no rule validation code is done here!
////										 * Rule validation is done in just briefing time context.
////										 */
////										&& (ChronoUnit.DAYS.between(duty.getBriefTime(this.hbNdx), nd.getDebriefTime(this.hbNdx).minusSeconds(1)) < 3)) {
////									pairing[1] = nd;
////									numOfDhs += nd.getNumOfLegsPassive();
////									totalActiveBlockTime += nd.getBlockTimeInMinsActive();
////									setIncludingPairings(pairing, 2, numOfDhs, totalActiveBlockTime);
////									numOfDhs -= nd.getNumOfLegsPassive();
////									totalActiveBlockTime -= nd.getBlockTimeInMinsActive();
////									pairing[1] = null;
////									numOfProbablePairings++;
////								}
////							}
////						}
//					}
//				}
//			}
//		}
//
//		for (int i = 0; i < numOfProbablePairings.length; i++) {
//			logger.info(numOfProbablePairings[i] + " num of probable " + (i + 1) + "th day pairings are found!");
//		}

		return true;
	}

//	private void fwSearch(int[] numOfProbablePairings,
//							Duty[] pairing, Duty fd, Duty ld,
//							int numOfDhs, int totalActiveBlockTime,
//							int newNdx, int maxNdx, LocalDate maxMinDateDept) {
//		Leg[] nls = this.nextBriefLegIndexByDutyNdx.getArray(ld.getNdx());
//		for (Leg nl : nls) {
//			Duty[] nds = this.dutyIndexByDepLegNdx.getArray(nl.getNdx());
//			for (Duty nd : nds) {
////				if (nd.getNumOfLegsPassive() == 0) {
//					if (nd.isHbArr(this.hbNdx)
//							&& maxMinDateDept.isAfter(nd.getDebriefDay(this.hbNdx))
//							/*
//							 * TODO
//							 * This line below are put because of no rule validation code is done here!
//							 * Rule validation is done in just briefing time context.
//							 */
//							&& (ChronoUnit.DAYS.between(fd.getBriefTime(this.hbNdx), nd.getDebriefTime(this.hbNdx).minusSeconds(1)) < (HeurosSystemParam.maxPairingLengthInDays - 1))) {
//						pairing[newNdx] = nd;
//						setIncludingPairings(pairing, newNdx + 1, numOfDhs + nd.getNumOfLegsPassive(), totalActiveBlockTime + nd.getBlockTimeInMinsActive());
//						numOfProbablePairings[newNdx]++;
//						pairing[newNdx] = null;
//					} else
//						if (nd.isNonHbArr(this.hbNdx)
//								&& (newNdx < maxNdx)
//								&& maxMinDateDept.isAfter(nd.getDebriefDay(this.hbNdx))
//								/*
//								 * TODO
//								 * This line below are put because of no rule validation code is done here!
//								 * Rule validation is done in just briefing time context.
//								 */
//								&& (ChronoUnit.DAYS.between(fd.getBriefTime(this.hbNdx), nd.getDebriefTime(this.hbNdx).minusSeconds(1)) < (HeurosSystemParam.maxPairingLengthInDays - 2))) {
//							pairing[newNdx] = nd;
//							this.fwSearch(numOfProbablePairings,
//											pairing, fd, nd, 
//											numOfDhs + nd.getNumOfLegsPassive(), totalActiveBlockTime + nd.getBlockTimeInMinsActive(),
//											newNdx + 1, maxNdx,
//											maxMinDateDept);
//							pairing[newNdx] = null;
//						}
////				}
//			}
//		}
//	}
}
