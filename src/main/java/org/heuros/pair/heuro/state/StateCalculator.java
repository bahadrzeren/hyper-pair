package org.heuros.pair.heuro.state;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.heuros.context.PairOptimizationContext;
import org.heuros.core.data.ndx.OneDimIndexInt;
import org.heuros.core.data.ndx.OneDimUniqueIndexInt;
import org.heuros.data.DutyLegOvernightConnNetwork;
import org.heuros.data.model.Duty;
import org.heuros.data.model.Leg;
import org.heuros.pair.conf.HeurosSystemParam;
import org.heuros.pair.enumerator.PairEnumeratorWoRuleCheck;
import org.heuros.pair.enumerator.PairListener;
import org.heuros.pair.sp.PairWithQuality;

public class StateCalculator implements Callable<Double>, PairListener {
	private static Logger logger = Logger.getLogger(StateCalculator.class);

	/*
	 * TODO Single base assumption!!!
	 */
	private int hbNdx = 0;

	private List<Leg> legs = null;
	private List<Duty> duties = null;
	private OneDimIndexInt<Duty> dutyIndexByLegNdx = null;
	private LegState[] activeLegStates = null;
	private DutyState[] activeDutyStates = null;
	private LegState[] tempLegStates = null;
	private DutyState[] tempDutyStates = null;
	private PairWithQuality pwq = null;
	private Leg legToCover = null;

	private OneDimIndexInt<Duty> dutyIndexByDepLegNdx = null;
	private OneDimIndexInt<Duty> dutyIndexByArrLegNdx = null;
	private OneDimUniqueIndexInt<Leg> nextBriefLegIndexByDutyNdx = null;
	private OneDimUniqueIndexInt<Leg> prevDebriefLegIndexByDutyNdx = null;

	private PairEnumeratorWoRuleCheck pairEnumerator = null;

	public StateCalculator(PairOptimizationContext pairOptimizationContext,
							DutyLegOvernightConnNetwork pricingNetwork,
							Leg legToCover,
							LegState[] activeLegStates,
							DutyState[] activeDutyStates,
							PairWithQuality pwq) {
		this.legs = pairOptimizationContext.getLegRepository().getModels();
		this.duties = pairOptimizationContext.getDutyRepository().getModels();
		this.dutyIndexByLegNdx = pairOptimizationContext.getDutyIndexByLegNdx();

		this.dutyIndexByDepLegNdx = pricingNetwork.getDutyIndexByDepLegNdx();
		this.dutyIndexByArrLegNdx = pricingNetwork.getDutyIndexByArrLegNdx();
		this.nextBriefLegIndexByDutyNdx = pricingNetwork.getNextBriefLegIndexByDutyNdx();
		this.prevDebriefLegIndexByDutyNdx = pricingNetwork.getPrevDebriefLegIndexByDutyNdx();
		this.legToCover = legToCover;
		this.activeLegStates = activeLegStates;
		this.activeDutyStates = activeDutyStates;
		this.pwq = pwq;
		this.pairEnumerator = new PairEnumeratorWoRuleCheck(pairOptimizationContext,
															pricingNetwork,
															this);
	}

	public LegState[] getTempLegStates() {
		return tempLegStates;
	}

	public DutyState[] getTempDutyStates() {
		return tempDutyStates;
	}

	public PairWithQuality getPwq() {
		return pwq;
	}

	private LegState[] cloneLegStates() throws CloneNotSupportedException {
		LegState[] res = this.activeLegStates.clone();
		for (int i = 0; i < res.length; i++)
			res[i] = (LegState) res[i].clone();
		return res;
	}

	private DutyState[] cloneDutyStates() throws CloneNotSupportedException {
		DutyState[] res = this.activeDutyStates.clone();
		for (int i = 0; i < res.length; i++)
			res[i] = (DutyState) res[i].clone();
		return res;
	}

//	public int[][] pairControlArray = null;

	@Override
	public void onPairingFound(Duty[] pairing, int fromNdxInc, int toNdxExc, int numOfDhs, int totalActiveBlockTime) {
		for (int i = fromNdxInc; i < toNdxExc; i++) {
			for (int j = 0; j < pairing[i].getNumOfLegs(); j++) {
				Leg l = pairing[i].getLegs().get(j);
				if (l.isCover()
						&& l.hasPair(hbNdx)) {

//if ((l.getNdx() == 4222)
//		&& (pairControlArray != null)) {
//	logger.info("--------------------");
//
//	Duty[] hPair = ArrayUtils.subarray(pairing, fromNdxInc, toNdxExc);
//	for (int k = 0; k < hPair.length; k++) {
//		for (int m = hPair.length - 1; m > k; m--) {
//			if (hPair[m].getBriefTime(hbNdx).isBefore(hPair[m - 1].getBriefTime(hbNdx))) {
//				Duty hd = hPair[m];
//				hPair[m] = hPair[m - 1];
//				hPair[m - 1] = hd;
//			}
//		}
//	}
////	if (!pairEnumerator.validatePair(hPair, tempDutyStates)) {
////		System.out.println();
////		pairEnumerator.validatePair(hPair, tempDutyStates);
////	}
//
//	for (int[] dutyNdxs: pairControlArray) {
//		if (dutyNdxs.length == toNdxExc - fromNdxInc) {
//			boolean f = true;
//			for (int k = 0; k < dutyNdxs.length; k++) {
//				if ((fromNdxInc > 0) && (toNdxExc < pairing.length)
//						&& ((dutyNdxs[k] == 0)
//							|| (pairing[fromNdxInc - 1] != null)
//							|| (pairing[toNdxExc] != null)))
//					System.out.println("Must be an unreachable line!");
//				if (dutyNdxs[k] != pairing[fromNdxInc + k].getNdx()) {
//					f = false;
//					break;
//				}
//			}
//			if (f)
//				System.out.println("Must be an unreachable line!");
//		} else
//			if ((fromNdxInc > 0) && (toNdxExc < pairing.length)
//					&& ((pairing[fromNdxInc - 1] != null)
//					|| (pairing[toNdxExc] != null)))
//				System.out.println("Must be an unreachable line!");
//	}
//	pairControlArray = ArrayUtils.add(pairControlArray, new int[hPair.length]);
//	for (int k = 0; k < hPair.length; k++) {
//		pairControlArray[pairControlArray.length - 1][k] = hPair[k].getNdx();
//	}
//	if (toNdxExc - fromNdxInc == 1) {
//		logger.info(this.legToCover.getNdx() + "-" + pwq.pair.getNumOfDuties() + ": " + pairing[fromNdxInc].getNdx());
//	} else
//		if (toNdxExc - fromNdxInc == 2) {
//			logger.info(this.legToCover.getNdx() + "-" + pwq.pair.getNumOfDuties() + ": " + pairing[fromNdxInc].getNdx() + ", " + 
//																							pairing[fromNdxInc + 1].getNdx());
//		} else
//			if (toNdxExc - fromNdxInc == 3) {
//				logger.info(this.legToCover.getNdx() + "-" + pwq.pair.getNumOfDuties() + ": " + pairing[fromNdxInc].getNdx() + ", " + 
//																								pairing[fromNdxInc + 1].getNdx() + ", " + 
//																								pairing[fromNdxInc + 2].getNdx());
//			} else
//				if (toNdxExc - fromNdxInc == 4) {
//					logger.info(this.legToCover.getNdx() + "-" + pwq.pair.getNumOfDuties() + ": " + pairing[fromNdxInc].getNdx() + ", " + 
//																									pairing[fromNdxInc + 1].getNdx() + ", " +
//																									pairing[fromNdxInc + 2].getNdx() + ", " +
//																									pairing[fromNdxInc + 3].getNdx());
//				} else
//					System.out.println("Must be an unreachable line!");
//
//	for (int k = fromNdxInc; k < toNdxExc; k++) {
//		logger.info(pairing[k]);
//	}
//	logger.info(this.legToCover.getNdx() + "-" + pwq.pair.getNumOfDuties() + ": wasEffective: " + (totalActiveBlockTime >= 
//																									HeurosSystemParam.effectiveDutyBlockHourLimit * (toNdxExc - fromNdxInc)));
//	logger.info(this.legToCover.getNdx() + "-" + pwq.pair.getNumOfDuties() + ": Before PairTots: " + this.tempLegStates[l.getNdx()].numOfIncludingPairs + ", " +
//																										this.tempLegStates[l.getNdx()].numOfIncludingEffectivePairs + ", " +
//																										this.tempLegStates[l.getNdx()].numOfIncludingPairsWoDh + ", " +
//																										this.tempLegStates[l.getNdx()].numOfIncludingEffectivePairsWoDh);
//}

					if (numOfDhs == 1) {
						this.tempLegStates[l.getNdx()].numOfIncludingPairsWoDh--;
						if (totalActiveBlockTime >= HeurosSystemParam.effectiveDutyBlockHourLimit * (toNdxExc - fromNdxInc)) {
							this.tempLegStates[l.getNdx()].numOfIncludingEffectivePairsWoDh--;
						}
					} else
						System.out.println("Must be an unreachable line!");

//if ((l.getNdx() == 4222)
//		&& (pairControlArray != null)) {
//	logger.info(this.legToCover.getNdx() + "-" + pwq.pair.getNumOfDuties() + ": Before PairTots: " + this.tempLegStates[l.getNdx()].numOfIncludingPairs + ", " +
//																										this.tempLegStates[l.getNdx()].numOfIncludingEffectivePairs + ", " +
//																										this.tempLegStates[l.getNdx()].numOfIncludingPairsWoDh + ", " +
//																										this.tempLegStates[l.getNdx()].numOfIncludingEffectivePairsWoDh);
//}

					if (this.tempLegStates[l.getNdx()].numOfCoverings == 0) {
						double newDifficultyScore = this.tempLegStates[l.getNdx()].getWeightedDifficultyScore();
//						double newDifficultyScore = (1.0 * (LegState.maxNumOfIncludingPairsWoDh - this.tempLegStates[l.getNdx()].numOfIncludingPairsWoDh)) / LegState.maxNumOfIncludingPairsWoDh;
						if (newDifficultyScore > maxDifficultyScoreObtained)
							maxDifficultyScoreObtained = newDifficultyScore;
					}
				}
			}
		}
	}

	private double maxDifficultyScoreObtained = 0.0;

	public double getMaxDifficultyScoreObtained() {
		return maxDifficultyScoreObtained;
	}

	@Override
	public Double call() throws Exception {


		this.tempLegStates = this.cloneLegStates();
		this.tempDutyStates = this.cloneDutyStates();

		double maxDifficultyScoreObtained = 0.0;

//pairControlArray = null;

		/*
		 * First update COVERING states of the legs and duties that constitute new pairing.
		 */
		for (int i = 0; i < pwq.pair.getNumOfDuties(); i++) {
			Duty duty = pwq.pair.getDuties().get(i);

//if ((duty.getNdx() == 68973)
//		&& (pwq.pair.getNumOfDuties() == 2)) {
//System.out.println();
//pairControlArray = new int[0][0];
//}

			for (int j = 0; j < duty.getNumOfLegs(); j++) {
				Leg leg = duty.getLegs().get(j);
				this.tempLegStates[leg.getNdx()].numOfCoverings++;
			}
		}

		/*
		 * Set dutyOfLeg TOTALIZERs.
		 */
		for (int i = 0; i < pwq.pair.getNumOfDuties(); i++) {
			Duty duty = pwq.pair.getDuties().get(i);
			for (int j = 0; j < duty.getNumOfLegs(); j++) {
				Leg leg = duty.getLegs().get(j);

				Duty[] dutiesOfLeg = this.dutyIndexByLegNdx.getArray(leg.getNdx());
				for (int di = 0; di < dutiesOfLeg.length; di++) {
					Duty dutyOfLeg = dutiesOfLeg[di];
					if (dutyOfLeg.hasPairing(pwq.pair.getHbNdx())
							&& dutyOfLeg.isValid(pwq.pair.getHbNdx())) {

//if (dutyOfLeg.getNdx() == 70087)
//System.out.println();

						DutyState dutyOfLegStat = this.tempDutyStates[dutyOfLeg.getNdx()];

						dutyOfLegStat.numOfCoverings++;
						dutyOfLegStat.blockTimeOfCoverings += leg.getBlockTimeInMins();

						if (this.tempLegStates[leg.getNdx()].numOfCoverings == 1) {
							dutyOfLegStat.numOfDistinctCoverings++;
							dutyOfLegStat.blockTimeOfDistinctCoverings += leg.getBlockTimeInMins();
							if (leg.isCover()) {
								dutyOfLegStat.numOfCoveringsActive++;
								dutyOfLegStat.blockTimeOfCoveringsActive += leg.getBlockTimeInMins();

								dutyOfLegStat.numOfDistinctCoveringsActive++;
								dutyOfLegStat.blockTimeOfDistinctCoveringsActive += leg.getBlockTimeInMins();
							} else {
								dutyOfLegStat.numOfCoveringsPassiveExt++;
								dutyOfLegStat.blockTimeOfCoveringsPassiveExt += leg.getBlockTimeInMins();

								dutyOfLegStat.numOfDistinctCoveringsPassive++;
								dutyOfLegStat.blockTimeOfDistinctCoveringsPassive += leg.getBlockTimeInMins();
							}
						} else {
							if (leg.isCover()) {
								dutyOfLegStat.numOfCoveringsPassiveInt++;
								dutyOfLegStat.blockTimeOfCoveringsPassiveInt += leg.getBlockTimeInMins();
								if (this.tempLegStates[leg.getNdx()].numOfCoverings == 2) {
									dutyOfLegStat.numOfDistinctCoveringsPassive++;
									dutyOfLegStat.blockTimeOfDistinctCoveringsPassive += leg.getBlockTimeInMins();
								}
							} else {
								dutyOfLegStat.numOfCoveringsPassiveExt++;
								dutyOfLegStat.blockTimeOfCoveringsPassiveExt += leg.getBlockTimeInMins();
							}
						}

						boolean isDhStateChanged = (dutyOfLeg.getNumOfLegsPassive() == 0) && (dutyOfLegStat.numOfCoverings == 1);
						boolean isEffectivenessWoDhChanged = isDhStateChanged && (dutyOfLeg.getBlockTimeInMinsActive() >= HeurosSystemParam.effectiveDutyBlockHourLimit);

						/*
						 * Calculate decrease in pairing totalizers.
						 */

						this.pairEnumerator.enumerateAllPairings(dutyOfLeg, tempDutyStates);

						boolean isEffectivenessChanged = leg.isCover()
															&& (this.tempLegStates[leg.getNdx()].numOfCoverings == 1)
															&& (dutyOfLeg.getBlockTimeInMinsActive() 
																	- dutyOfLegStat.blockTimeOfDistinctCoveringsActive 
																	+ leg.getBlockTimeInMins() >= HeurosSystemParam.effectiveDutyBlockHourLimit)
															&& (dutyOfLeg.getBlockTimeInMinsActive() 
																	- dutyOfLegStat.blockTimeOfDistinctCoveringsActive < HeurosSystemParam.effectiveDutyBlockHourLimit);

						if (isDhStateChanged
								|| isEffectivenessChanged) {
							/*
							 * Calculate decrease in duty totalizers.
							 */
							for (int li = 0; li < dutyOfLeg.getLegs().size(); li++) {
								Leg indLeg = dutyOfLeg.getLegs().get(li);

								if (indLeg.isCover()) {
									if (isEffectivenessChanged) {
										this.tempLegStates[indLeg.getNdx()].numOfIncludingEffectiveDuties--;
									}

									if (isDhStateChanged) {
										this.tempLegStates[indLeg.getNdx()].numOfIncludingDutiesWoDh--;
										if (isEffectivenessWoDhChanged) {
											this.tempLegStates[indLeg.getNdx()].numOfIncludingEffectiveDutiesWoDh--;
										}
									}

//									Duty[] dutiesOfIndLeg = this.dutyIndexByLegNdx.getArray(indLeg.getNdx());
//									for (int idi = 0; idi < dutiesOfIndLeg.length; idi++) {
//										Duty dutieOfIndLeg = dutiesOfIndLeg[idi];
//										if (dutieOfIndLeg.hasPairing(p.getHbNdx())
//												&& dutieOfIndLeg.isValid(p.getHbNdx())) {
//											if (isEffectivenessChanged) {
//												this.tempDutyStates[dutieOfIndLeg.getNdx()].totalNumOfAlternativeEffectiveDuties--;
//												if (this.tempDutyStates[dutieOfIndLeg.getNdx()].minNumOfAlternativeEffectiveDuties > this.tempLegStates[indLeg.getNdx()].numOfIncludingEffectiveDuties)
//													this.tempDutyStates[dutieOfIndLeg.getNdx()].minNumOfAlternativeEffectiveDuties = this.tempLegStates[indLeg.getNdx()].numOfIncludingEffectiveDuties;
////												/*
////												 * TODO
////												 * 
////												 * This implementation does not guarantee to set exact maxNumOfAlternativeEffectiveDutiesWoDh.
////												 * We did not want to make the code more complex by adding another state variable that is needed to be maintained during the iterations.
////												 * Therefore the number of legs that has the same maxNumOfAlternativeEffectiveDutiesWoDh might cause small disruptions.
////												 *  
////												 */
////												if (this.tempDutyStates[dutieOfIndLeg.getNdx()].maxNumOfAlternativeEffectiveDuties <= this.tempLegStates[indLeg.getNdx()].numOfIncludingEffectiveDuties)
////													this.tempDutyStates[dutieOfIndLeg.getNdx()].maxNumOfAlternativeEffectiveDuties = this.tempLegStates[indLeg.getNdx()].numOfIncludingEffectiveDuties;
//											}
//											if (isDhStateChanged) {
//												this.tempDutyStates[dutieOfIndLeg.getNdx()].totalNumOfAlternativeDutiesWoDh--;
//												if (isEffectivenessWoDhChanged) {
//													this.tempDutyStates[dutieOfIndLeg.getNdx()].totalNumOfAlternativeEffectiveDutiesWoDh--;
//													if (this.tempDutyStates[dutieOfIndLeg.getNdx()].minNumOfAlternativeEffectiveDutiesWoDh > this.tempLegStates[indLeg.getNdx()].numOfIncludingEffectiveDutiesWoDh)
//														this.tempDutyStates[dutieOfIndLeg.getNdx()].minNumOfAlternativeEffectiveDutiesWoDh = this.tempLegStates[indLeg.getNdx()].numOfIncludingEffectiveDutiesWoDh;
////													/*
////													 * TODO
////													 * 
////													 * This implementation does not guarantee to set exact maxNumOfAlternativeEffectiveDutiesWoDh.
////													 * We did not want to make the code more complex by adding another state variable that is needed to be maintained during the iterations.
////													 * Therefore the number of legs that has the same maxNumOfAlternativeEffectiveDutiesWoDh might cause small disruptions.
////													 *  
////													 */
////													if (this.tempDutyStates[dutieOfIndLeg.getNdx()].maxNumOfAlternativeEffectiveDutiesWoDh <= this.tempLegStates[indLeg.getNdx()].numOfIncludingEffectiveDutiesWoDh)
////														this.tempDutyStates[dutieOfIndLeg.getNdx()].maxNumOfAlternativeEffectiveDutiesWoDh = this.tempLegStates[indLeg.getNdx()].numOfIncludingEffectiveDutiesWoDh;
//												}
//												if (this.tempDutyStates[dutieOfIndLeg.getNdx()].minNumOfAlternativeDutiesWoDh > this.tempLegStates[indLeg.getNdx()].numOfIncludingDutiesWoDh)
//													this.tempDutyStates[dutieOfIndLeg.getNdx()].minNumOfAlternativeDutiesWoDh = this.tempLegStates[indLeg.getNdx()].numOfIncludingDutiesWoDh;
////												/*
////												 * TODO
////												 * 
////												 * This implementation does not guarantee to set exact maxNumOfAlternativeDutiesWoDh.
////												 * We did not want to make the code more complex by adding another state variable that is needed to be maintained during the iterations.
////												 * Therefore the number of legs that has the same maxNumOfAlternativeDutiesWoDh might cause small disruptions.
////												 *  
////												 */
////												if (this.tempDutyStates[dutieOfIndLeg.getNdx()].maxNumOfAlternativeDutiesWoDh <= this.tempLegStates[indLeg.getNdx()].numOfIncludingDutiesWoDh)
////													this.tempDutyStates[dutieOfIndLeg.getNdx()].maxNumOfAlternativeDutiesWoDh = this.tempLegStates[indLeg.getNdx()].numOfIncludingDutiesWoDh;
//											}
//										}
//									}
								}
							}
						}
					}
				}
			}
		}

		return maxDifficultyScoreObtained;
	}
}
