package org.heuros.pair.heuro;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.heuros.context.PairOptimizationContext;
import org.heuros.data.DutyLegOvernightConnNetwork;
import org.heuros.data.model.Leg;
import org.heuros.data.model.Pair;
import org.heuros.pair.conf.HeurosSystemParam;
import org.heuros.pair.heuro.state.SolutionState;
import org.heuros.pair.sp.PairWithQuality;
import org.heuros.pair.sp.PairingGenerator;

public class SolutionGenerator {

	private static Logger logger = Logger.getLogger(SolutionGenerator.class);

	/*
	 * TODO Single base assumption!!!
	 */
	private int hbNdx = 0;

	private List<Leg> legs = null;
//	private List<Duty> duties = null;
//	private OneDimIndexInt<Duty> dutyIndexByLegNdx = null;

	private ExecutorService pairingGensExecutor = Executors.newFixedThreadPool(HeurosSystemParam.numOfLegsToBeChoosen);
	private PairingGenerator[] pairingGenerators = new PairingGenerator[HeurosSystemParam.numOfLegsToBeChoosen];
	private List<Future<PairWithQuality[]>> pairGenProcessL = new ArrayList<Future<PairWithQuality[]>>();
	private int[] legNdxsToCover = new int[HeurosSystemParam.numOfLegsToBeChoosen];

	public SolutionGenerator(PairOptimizationContext pairOptimizationContext,
								DutyLegOvernightConnNetwork pricingNetwork) {
		this.legs = pairOptimizationContext.getLegRepository().getModels();
//		this.duties = pairOptimizationContext.getDutyRepository().getModels();
//		this.dutyIndexByLegNdx = pairOptimizationContext.getDutyIndexByLegNdx();
		for (int i = 0; i < this.pairingGenerators.length; i++) {
			this.pairingGenerators[i] = new PairingGenerator(pairOptimizationContext, pricingNetwork);
			this.pairGenProcessL.add(null);
			this.legNdxsToCover[i] = -1;
		}
	}

	public int generateSolution(List<Pair> solution,
									SolutionState solutionState) throws InterruptedException, ExecutionException, CloneNotSupportedException {

		logger.info("Solution generation process is started!");

		int uncoveredLegs = 0;

		while (true) {

			PairWithQuality[] pqs = new PairWithQuality[0];
//			try {
				if (solutionState.setNextLegNdxsToCover(this.hbNdx, legNdxsToCover)) {
					for (int i = 0; i < this.legNdxsToCover.length; i++) {
						if (this.legNdxsToCover[i] >= 0) {
							Leg legToCover = this.legs.get(this.legNdxsToCover[i]);
							this.pairingGenerators[i].setLegForPairGeneration(legToCover, solutionState.getActiveDutyStates());
							this.pairGenProcessL.set(i, pairingGensExecutor.submit(this.pairingGenerators[i]));
						} else
							this.pairGenProcessL.set(i, null);
					}
				} else
					break;
//			} catch (CloneNotSupportedException ex) {
//				logger.error(ex);
//			}

			for (int i = 0; i < this.pairGenProcessL.size(); i++) {
				if (this.pairGenProcessL.get(i) != null) {
					PairWithQuality[] pwqs = this.pairGenProcessL.get(i).get();
					pqs = ArrayUtils.addAll(pqs, pwqs);
				}
			}
			PairWithQuality pwq = solutionState.chooseBestPairing(pqs);

			if (pwq != null) {

					solution.add(pwq.p);

					/**
					 * TEST BLOCK BEGIN
					 * 
					 * Checks duty totalizers on LegState instances.
					 * 
					 */
//					this.legs.forEach((leg) -> {
//						if (leg.isCover()
//								&& leg.hasPair(this.hbNdx)
//								&& leg.getSobt().isBefore(HeurosDatasetParam.optPeriodEndExc)
////								&& (solutionState.getLegStates()[leg.getNdx()].numOfCoverings == 0)
//								) {
//							int numOfIncludingDuties = 0;
//							int numOfIncludingDutiesWoDh = 0;
//							int numOfIncludingEffectiveDuties = 0;
//							int numOfIncludingEffectiveDutiesWoDh = 0;
//
//							/*
//							 * Leg duty totalizers.
//							 */
//							Duty[] dutiesOfLeg = dutyIndexByLegNdx.getArray(leg.getNdx());
//							for (Duty dutyOfLeg: dutiesOfLeg) {
//								if (dutyOfLeg.hasPairing(this.hbNdx)
//										&& dutyOfLeg.isValid(this.hbNdx)) {
//									numOfIncludingDuties++;
//									if ((dutyOfLeg.getNumOfLegsPassive() == 0)
//											&& (solutionState.getActiveDutyStates()[dutyOfLeg.getNdx()].numOfCoverings == 0)) {
//										numOfIncludingDutiesWoDh++;
//										if (dutyOfLeg.getBlockTimeInMinsActive() >= HeurosSystemParam.effectiveDutyBlockHourLimit) {
//											numOfIncludingEffectiveDutiesWoDh++;
//										}
//									}
//									if (dutyOfLeg.getBlockTimeInMinsActive() - solutionState.getActiveDutyStates()[dutyOfLeg.getNdx()].blockTimeOfDistinctCoveringsActive >= HeurosSystemParam.effectiveDutyBlockHourLimit) {
//										numOfIncludingEffectiveDuties++;
//									}
//								}
//							}
//
//							if (!solutionState.getActiveLegStates()[leg.getNdx()].areDutyTotalizersOk(numOfIncludingDuties, 
//																										numOfIncludingDutiesWoDh, 
//																										numOfIncludingEffectiveDuties, 
//																										numOfIncludingEffectiveDutiesWoDh)) {
//								logger.error("LegState values has not been set correctly for " + leg);
//								solutionState.getActiveLegStates()[leg.getNdx()].areDutyTotalizersOk(numOfIncludingDuties, 
//																										numOfIncludingDutiesWoDh, 
//																										numOfIncludingEffectiveDuties, 
//																										numOfIncludingEffectiveDutiesWoDh);
//							}
//						}
//					});
					/**
					 * TEST BLOCK END
					 * 
					 */

					/**
					 * TEST BLOCK BEGIN
					 * 
					 * Checks DutyState state variables.
					 * 
					 */
//					int[] legsCoveringVector = new int[this.legs.size()];
//					for (int i = 0; i < solution.size(); i++) {
//						Pair pair = solution.get(i);
//						for (int j = 0; j < pair.getNumOfDuties(); j++) {
//							Duty duty = pair.getDuties().get(j);
//							for (int k = 0; k < duty.getNumOfLegs(); k++) {
//								Leg leg = duty.getLegs().get(k);
//								legsCoveringVector[leg.getNdx()]++;
//							}
//						}
//					}
//
//					this.duties.forEach((d) -> {
//						if (d.isValid(this.hbNdx)
//								&& d.hasPairing(this.hbNdx)) {
//							DutyState ds = solutionState.getActiveDutyStates()[d.getNdx()];
//							int numOfCoverings = 0;
//							int numOfCoveringsActive = 0;
//							int numOfCoveringsPassiveInt = 0;
//							int numOfCoveringsPassiveExt = 0;
//							int numOfDistinctCoverings = 0;
//							int numOfDistinctCoveringsActive = 0;
//							int numOfDistinctCoveringsPassive = 0;
//							int blockTimeOfCoverings = 0;
//							int blockTimeOfCoveringsActive = 0;
//							int blockTimeOfCoveringsPassiveInt = 0;
//							int blockTimeOfCoveringsPassiveExt = 0;
//							int blockTimeOfDistinctCoverings = 0;
//							int blockTimeOfDistinctCoveringsActive = 0;
//							int blockTimeOfDistinctCoveringsPassive = 0;
//
//							for (int i = 0; i < d.getNumOfLegs(); i++) {
//								Leg l = d.getLegs().get(i);
//
//								if (legsCoveringVector[l.getNdx()] > 0) {
//									numOfCoverings += legsCoveringVector[l.getNdx()];
//									blockTimeOfCoverings += legsCoveringVector[l.getNdx()] * l.getBlockTimeInMins();
//									numOfDistinctCoverings++;
//									blockTimeOfDistinctCoverings += l.getBlockTimeInMins();
//									if (l.isCover()) {
//										numOfCoveringsActive++;
//										blockTimeOfCoveringsActive += l.getBlockTimeInMins();
//										numOfDistinctCoveringsActive++;
//										blockTimeOfDistinctCoveringsActive += l.getBlockTimeInMins();
//										if (legsCoveringVector[l.getNdx()] > 1) {
//											numOfCoveringsPassiveInt += (legsCoveringVector[l.getNdx()] - 1);
//											blockTimeOfCoveringsPassiveInt += (legsCoveringVector[l.getNdx()] - 1) * l.getBlockTimeInMins();
//											numOfDistinctCoveringsPassive++;
//											blockTimeOfDistinctCoveringsPassive += l.getBlockTimeInMins();
//										}
//									} else {
//										numOfCoveringsPassiveExt += legsCoveringVector[l.getNdx()];
//										blockTimeOfCoveringsPassiveExt += legsCoveringVector[l.getNdx()] * l.getBlockTimeInMins();
//										numOfDistinctCoveringsPassive++;
//										blockTimeOfDistinctCoveringsPassive += l.getBlockTimeInMins();
//									}
//								}
//							}
//							if (!ds.stateVariablesAreOk(numOfCoverings, 
//														numOfCoveringsActive, 
//														numOfCoveringsPassiveInt, 
//														numOfCoveringsPassiveExt, 
//														numOfDistinctCoverings, 
//														numOfDistinctCoveringsActive, 
//														numOfDistinctCoveringsPassive, 
//														blockTimeOfCoverings, 
//														blockTimeOfCoveringsActive, 
//														blockTimeOfCoveringsPassiveInt, 
//														blockTimeOfCoveringsPassiveExt, 
//														blockTimeOfDistinctCoverings, 
//														blockTimeOfDistinctCoveringsActive, 
//														blockTimeOfDistinctCoveringsPassive)) {
//								logger.error("LegToCover: " + legToCover);
//								logger.error("DutyState values has not been set correctly for " + d);
//								ds.stateVariablesAreOk(numOfCoverings, 
//														numOfCoveringsActive, 
//														numOfCoveringsPassiveInt, 
//														numOfCoveringsPassiveExt, 
//														numOfDistinctCoverings, 
//														numOfDistinctCoveringsActive, 
//														numOfDistinctCoveringsPassive, 
//														blockTimeOfCoverings, 
//														blockTimeOfCoveringsActive, 
//														blockTimeOfCoveringsPassiveInt, 
//														blockTimeOfCoveringsPassiveExt, 
//														blockTimeOfDistinctCoverings, 
//														blockTimeOfDistinctCoveringsActive, 
//														blockTimeOfDistinctCoveringsPassive);
//							}
//						}
//					});
					/**
					 * TEST BLOCK END
					 * 
					 */

					/**
					 * TEST BLOCK BEGIN
					 * 
					 * Checks DutyState AlternativeDuties related TOTALIZERS.
					 * 
					 */
//					this.duties.forEach((d) -> {
//						if (d.isValid(this.hbNdx)
//								&& d.hasPairing(this.hbNdx)) {
//							DutyState ds = solutionState.getDutyStates()[d.getNdx()];
//							int minNumOfAlternativeDuties = Integer.MAX_VALUE;
//							int minNumOfAlternativeDutiesWoDh = Integer.MAX_VALUE;
//							int maxNumOfAlternativeDuties = 0;
//							int maxNumOfAlternativeDutiesWoDh = 0;
//							int totalNumOfAlternativeDuties = 0;
//							int totalNumOfAlternativeDutiesWoDh = 0;
//							int minNumOfAlternativeEffectiveDuties = Integer.MAX_VALUE;
//							int minNumOfAlternativeEffectiveDutiesWoDh = Integer.MAX_VALUE;
//							int maxNumOfAlternativeEffectiveDuties = 0;
//							int maxNumOfAlternativeEffectiveDutiesWoDh = 0;
//							int totalNumOfAlternativeEffectiveDuties = 0;
//							int totalNumOfAlternativeEffectiveDutiesWoDh = 0;
//							for (int i = 0; i < d.getNumOfLegs(); i++) {
//								Leg l = d.getLegs().get(i);
//								if (l.isCover()) {
//									if (minNumOfAlternativeDuties > solutionState.getLegStates()[l.getNdx()].numOfIncludingDuties)
//										minNumOfAlternativeDuties = solutionState.getLegStates()[l.getNdx()].numOfIncludingDuties;
//									if (minNumOfAlternativeDutiesWoDh > solutionState.getLegStates()[l.getNdx()].numOfIncludingDutiesWoDh)
//										minNumOfAlternativeDutiesWoDh = solutionState.getLegStates()[l.getNdx()].numOfIncludingDutiesWoDh;
//									if (maxNumOfAlternativeDuties < solutionState.getLegStates()[l.getNdx()].numOfIncludingDuties)
//										maxNumOfAlternativeDuties = solutionState.getLegStates()[l.getNdx()].numOfIncludingDuties;
//									if (maxNumOfAlternativeDutiesWoDh < solutionState.getLegStates()[l.getNdx()].numOfIncludingDutiesWoDh)
//										maxNumOfAlternativeDutiesWoDh = solutionState.getLegStates()[l.getNdx()].numOfIncludingDutiesWoDh;
//									totalNumOfAlternativeDuties += solutionState.getLegStates()[l.getNdx()].numOfIncludingDuties;
//									totalNumOfAlternativeDutiesWoDh += solutionState.getLegStates()[l.getNdx()].numOfIncludingDutiesWoDh;
//
//									if (minNumOfAlternativeEffectiveDuties > solutionState.getLegStates()[l.getNdx()].numOfIncludingEffectiveDuties)
//										minNumOfAlternativeEffectiveDuties = solutionState.getLegStates()[l.getNdx()].numOfIncludingEffectiveDuties;
//									if (minNumOfAlternativeEffectiveDutiesWoDh > solutionState.getLegStates()[l.getNdx()].numOfIncludingEffectiveDutiesWoDh)
//										minNumOfAlternativeEffectiveDutiesWoDh = solutionState.getLegStates()[l.getNdx()].numOfIncludingEffectiveDutiesWoDh;
//									if (maxNumOfAlternativeEffectiveDuties < solutionState.getLegStates()[l.getNdx()].numOfIncludingEffectiveDuties)
//										maxNumOfAlternativeEffectiveDuties = solutionState.getLegStates()[l.getNdx()].numOfIncludingEffectiveDuties;
//									if (maxNumOfAlternativeEffectiveDutiesWoDh < solutionState.getLegStates()[l.getNdx()].numOfIncludingEffectiveDutiesWoDh)
//										maxNumOfAlternativeEffectiveDutiesWoDh = solutionState.getLegStates()[l.getNdx()].numOfIncludingEffectiveDutiesWoDh;
//									totalNumOfAlternativeEffectiveDuties += solutionState.getLegStates()[l.getNdx()].numOfIncludingEffectiveDuties;
//									totalNumOfAlternativeEffectiveDutiesWoDh += solutionState.getLegStates()[l.getNdx()].numOfIncludingEffectiveDutiesWoDh;
//								}
//							}
//							if (!ds.valuesAreOk(minNumOfAlternativeDuties, 
//												minNumOfAlternativeDutiesWoDh, 
//												maxNumOfAlternativeDuties, 
//												maxNumOfAlternativeDutiesWoDh, 
//												totalNumOfAlternativeDuties, 
//												totalNumOfAlternativeDutiesWoDh, 
//												minNumOfAlternativeEffectiveDuties, 
//												minNumOfAlternativeEffectiveDutiesWoDh, 
//												maxNumOfAlternativeEffectiveDuties, 
//												maxNumOfAlternativeEffectiveDutiesWoDh, 
//												totalNumOfAlternativeEffectiveDuties, 
//												totalNumOfAlternativeEffectiveDutiesWoDh)) {
//								logger.error("LegToCover: " + legToCover);
//								logger.error("DutyState values has not been set correctly for " + d);
//								ds.valuesAreOk(minNumOfAlternativeDuties, 
//												minNumOfAlternativeDutiesWoDh, 
//												maxNumOfAlternativeDuties, 
//												maxNumOfAlternativeDutiesWoDh, 
//												totalNumOfAlternativeDuties, 
//												totalNumOfAlternativeDutiesWoDh, 
//												minNumOfAlternativeEffectiveDuties, 
//												minNumOfAlternativeEffectiveDutiesWoDh, 
//												maxNumOfAlternativeEffectiveDuties, 
//												maxNumOfAlternativeEffectiveDutiesWoDh, 
//												totalNumOfAlternativeEffectiveDuties, 
//												totalNumOfAlternativeEffectiveDutiesWoDh);
//							}
//						}
//					});
					/**
					 * TEST BLOCK END
					 * 
					 */

			} else {
				logger.error("Pairing could not be found for " + ArrayUtils.toString(legNdxsToCover));
				uncoveredLegs++;
			}
		}

		return uncoveredLegs;
	}
}
