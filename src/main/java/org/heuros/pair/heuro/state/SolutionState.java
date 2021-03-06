package org.heuros.pair.heuro.state;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.heuros.context.PairOptimizationContext;
import org.heuros.data.DutyLegOvernightConnNetwork;
import org.heuros.data.model.Duty;
import org.heuros.data.model.Leg;
import org.heuros.data.model.Pair;
import org.heuros.pair.SolutionCost;
import org.heuros.pair.conf.HeurosDatasetParam;
import org.heuros.pair.conf.HeurosSystemParam;
import org.heuros.pair.sp.PairWithQuality;

public class SolutionState {

//	private static Logger logger = Logger.getLogger(SolutionState.class);

	/*
	 * TODO Single base assumption!!!
	 */
	private int hbNdx = 0;

//	private PairOptimizationContext pairOptimizationContext = null;
	private List<Leg> legs = null;
	private List<Duty> duties = null;
//	private OneDimIndexInt<Duty> dutyIndexByLegNdx = null;
//	private DutyLegOvernightConnNetwork pricingNetwork = null;
	private LegState[] activeLegStates = null;
	private DutyState[] activeDutyStates = null;

//	private List<Leg> orderedLegs = null;

//	private PairEnumeratorWoRuleCheck pairEnumerator = null;

	private StateCalculator[] stateCalculators = null;
	private List<Future<Double>> stateProcessL = null;

	private ExecutorService pairingProcessExecutor = null;

	public SolutionState(PairOptimizationContext pairOptimizationContext,
							DutyLegOvernightConnNetwork pricingNetwork) throws CloneNotSupportedException {
//		this.pairOptimizationContext = pairOptimizationContext;
		this.legs = pairOptimizationContext.getLegRepository().getModels();
		this.duties = pairOptimizationContext.getDutyRepository().getModels();
//		this.dutyIndexByLegNdx = pairOptimizationContext.getDutyIndexByLegNdx();
//		this.pricingNetwork = pricingNetwork;

		this.activeLegStates = new LegState[this.legs.size()];
		this.activeDutyStates = new DutyState[this.duties.size()];

		for (int i = 0; i < this.activeLegStates.length; i++) {
			this.activeLegStates[i] = new LegState(this.legs.get(i));
			this.activeLegStates[i].initializeForNewIteration(this.legs.get(i));
		}
		for (int i = 0; i < this.activeDutyStates.length; i++) {
			this.activeDutyStates[i] = new DutyState();	//	this.duties.get(j));
			this.activeDutyStates[i].initializeForNewIteration(this.duties.get(i));
		}
		this.calculateAndSetMaxValuesOfHeuristicsParameters();
		this.pairingProcessExecutor = Executors.newFixedThreadPool(HeurosSystemParam.maxPairingLengthInDays);

//		pairEnumerator = new PairEnumeratorWoRuleCheck(pairOptimizationContext,
//														pricingNetwork,
//														this);

		this.stateCalculators = new StateCalculator[HeurosSystemParam.maxPairingLengthInDays];
		this.stateProcessL = new ArrayList<Future<Double>>(HeurosSystemParam.maxPairingLengthInDays);
		for (int i = 0; i < this.stateCalculators.length; i++) {
			this.stateCalculators[i] = new StateCalculator(pairOptimizationContext,
															pricingNetwork,
															activeLegStates,
															activeDutyStates);
			this.stateProcessL.add(null);
		}
	}

	public LegState[] getActiveLegStates() {
		return activeLegStates;
	}

	public DutyState[] getActiveDutyStates() {
		return activeDutyStates;
	}

	public void initializeForNewIteration() {
		for (int j = 0; j < this.activeLegStates.length; j++) {
			this.activeLegStates[j].initializeForNewIteration(this.legs.get(j));
		}
		for (int j = 0; j < this.activeDutyStates.length; j++) {
			this.activeDutyStates[j].initializeForNewIteration(this.duties.get(j));
		}
		this.calculateAndSetMaxValuesOfHeuristicsParameters();
		this.pairingProcessExecutor = Executors.newFixedThreadPool(HeurosSystemParam.maxPairingLengthInDays);
	}

	private void calculateAndSetMaxValuesOfHeuristicsParameters() {
		LegState.maxNumOfIncludingDuties = 0;
		LegState.maxNumOfIncludingDutiesWoDh = 0;
		LegState.maxNumOfIncludingEffectiveDuties = 0;
		LegState.maxNumOfIncludingEffectiveDutiesWoDh = 0;
		LegState.maxNumOfIncludingPairs = 0;
		LegState.maxNumOfIncludingPairsWoDh = 0;
		LegState.maxNumOfIncludingEffectivePairs = 0;
		LegState.maxNumOfIncludingEffectivePairsWoDh = 0;

		LegState.maxHeurModDh = 0.0;
		LegState.maxHeurModEf = 0.0;
		for (int j = 0; j < activeLegStates.length; j++) {
			if (LegState.maxNumOfIncludingDuties < this.activeLegStates[j].numOfIncludingDuties)
				LegState.maxNumOfIncludingDuties = this.activeLegStates[j].numOfIncludingDuties;
			if (LegState.maxNumOfIncludingDutiesWoDh < this.activeLegStates[j].numOfIncludingDutiesWoDh)
				LegState.maxNumOfIncludingDutiesWoDh = this.activeLegStates[j].numOfIncludingDutiesWoDh;
			if (LegState.maxNumOfIncludingEffectiveDuties < this.activeLegStates[j].numOfIncludingEffectiveDuties)
				LegState.maxNumOfIncludingEffectiveDuties = this.activeLegStates[j].numOfIncludingEffectiveDuties;
			if (LegState.maxNumOfIncludingEffectiveDutiesWoDh < this.activeLegStates[j].numOfIncludingEffectiveDutiesWoDh)
				LegState.maxNumOfIncludingEffectiveDutiesWoDh = this.activeLegStates[j].numOfIncludingEffectiveDutiesWoDh;

			if (LegState.maxNumOfIncludingPairs < this.activeLegStates[j].numOfIncludingPairs)
				LegState.maxNumOfIncludingPairs = this.activeLegStates[j].numOfIncludingPairs;
			if (LegState.maxNumOfIncludingPairsWoDh < this.activeLegStates[j].numOfIncludingPairsWoDh)
				LegState.maxNumOfIncludingPairsWoDh = this.activeLegStates[j].numOfIncludingPairsWoDh;
			if (LegState.maxNumOfIncludingEffectivePairs < this.activeLegStates[j].numOfIncludingEffectivePairs)
				LegState.maxNumOfIncludingEffectivePairs = this.activeLegStates[j].numOfIncludingEffectivePairs;
			if (LegState.maxNumOfIncludingEffectivePairsWoDh < this.activeLegStates[j].numOfIncludingEffectivePairsWoDh)
				LegState.maxNumOfIncludingEffectivePairsWoDh = this.activeLegStates[j].numOfIncludingEffectivePairsWoDh;

			if (LegState.maxHeurModDh < this.activeLegStates[j].actHeurModDh)
				LegState.maxHeurModDh = this.activeLegStates[j].actHeurModDh;
			if (LegState.maxHeurModEf < this.activeLegStates[j].actHeurModEf)
				LegState.maxHeurModEf = this.activeLegStates[j].actHeurModEf;
		}
	}

	public int getNextLegNdxToCover(int hbNdx) {
		int res = -1;
		double highestScore = 0.0;
		for (int i = 0; i < this.legs.size(); i++) {
			if (this.legs.get(i).isCover()
					&& this.legs.get(i).hasPair(hbNdx)
					&& this.legs.get(i).getSobt().isBefore(HeurosDatasetParam.optPeriodEndExc)
					&& (activeLegStates[i].numOfCoverings == 0)) {
				if (highestScore < activeLegStates[i].getWeightedDifficultyScore()) {
					highestScore = activeLegStates[i].getWeightedDifficultyScore();
					res = i;
				}
			}
		}
		return res;
	}

//	public boolean setNextLegNdxsToCover(int hbNdx, int[] activeLegNdxs) {
//		boolean atLeastOneLegExists = false;
//		boolean[] addedLegNdxs = new boolean[this.legs.size()];
//		for (int ndx = 0; ndx < activeLegNdxs.length; ndx++) {
////			if (activeLegNdxs[ndx] < 0) {
//				boolean legExists = false;
//				double highestScore = 0.0;
//				for (int i = 0; i < this.legs.size(); i++) {
//					if (this.legs.get(i).isCover()
//							&& this.legs.get(i).hasPair(hbNdx)
//							&& this.legs.get(i).getSobt().isBefore(HeurosDatasetParam.optPeriodEndExc)
//							&& (activeLegStates[i].numOfCoverings == 0)
//							&& (!addedLegNdxs[i])) {
//						if (highestScore < activeLegStates[i].getWeightedDifficultyScore()) {
//							highestScore = activeLegStates[i].getWeightedDifficultyScore();
//							addedLegNdxs[i] = true;
//							activeLegNdxs[ndx] = i;
//							atLeastOneLegExists = true;
//							legExists = true;
//						}
//					}
//				}
//				if (!legExists)
//					break;
////			}
//		}
//		return atLeastOneLegExists;
//	}

//	private Leg prevLegToCover = null;
//	private int[][] pairControlArray1 = null;
//	private int[][] pairControlArray2 = null;

//	private int[] numOfPairsWoDh = null;
//	private int[] numOfEffectivePairsWoDh = null;

//	@Override
//	public void onPairingFound(Duty[] pairing, int fromNdxInc, int toNdxExc, int numOfDhs, int totalActiveBlockTime) {
//		for (int i = fromNdxInc; i < toNdxExc; i++) {
//			for (int j = 0; j < pairing[i].getNumOfLegs(); j++) {
//				Leg l = pairing[i].getLegs().get(j);
//				if (l.isCover()
//						&& l.hasPair(hbNdx)) {
//
////if (l.getNdx() == 4798) {
////
////	pairControlArray2 = ArrayUtils.add(pairControlArray2, new int[toNdxExc - fromNdxInc]);
////	int h = 0;
////	for (int k = fromNdxInc; k < toNdxExc; k++) {
////		pairControlArray2[pairControlArray2.length - 1][h] = pairing[k].getNdx();
////		h++;
////	}
////
//////	logger.info("XXXXXXXXXXXXXXXXXXXX");
//////	if (toNdxExc - fromNdxInc == 1)
//////		logger.info((toNdxExc - fromNdxInc) + ": " + pairing[fromNdxInc].getNdx());
//////	else
//////		if (toNdxExc - fromNdxInc == 2)
//////			logger.info((toNdxExc - fromNdxInc) + ": " + pairing[fromNdxInc].getNdx() + ", " + 
//////															pairing[fromNdxInc + 1].getNdx());
//////		else
//////			if (toNdxExc - fromNdxInc == 3)
//////				logger.info((toNdxExc - fromNdxInc) + ": " + pairing[fromNdxInc].getNdx() + ", " + 
//////																pairing[fromNdxInc + 1].getNdx() + ", " + 
//////																pairing[fromNdxInc + 2].getNdx());
//////			else
//////				if (toNdxExc - fromNdxInc == 4)
//////					logger.info((toNdxExc - fromNdxInc) + ": " + pairing[fromNdxInc].getNdx() + ", " + 
//////																	pairing[fromNdxInc + 1].getNdx() + ", " +
//////																	pairing[fromNdxInc + 2].getNdx() + ", " +
//////																	pairing[fromNdxInc + 3].getNdx());
////
////	if (bestStateCalculator.pairControlArray != null) {
////		for (int[] dutyNdxs: bestStateCalculator.pairControlArray) {
////			if (dutyNdxs.length == (toNdxExc - fromNdxInc)) {
////				boolean f = true;
////				for (int k = 0; k < dutyNdxs.length; k++) {
////					if ((fromNdxInc > 0) && (toNdxExc < pairing.length)
////							&& ((dutyNdxs[k] == 0)
////								|| (pairing[fromNdxInc - 1] != null)
////								|| (pairing[toNdxExc] != null)))
////						System.out.println("Must be an unreachable line!");
////					if (dutyNdxs[k] != pairing[fromNdxInc + k].getNdx()) {
////						f = false;
////						break;
////					}
////				}
////				if (f)
////					System.out.println("Must be an unreachable line!");
////			} else
////				if ((fromNdxInc > 0) && (toNdxExc < pairing.length)
////						&& ((pairing[fromNdxInc - 1] != null)
////								|| (pairing[toNdxExc] != null)))
////					System.out.println("Must be an unreachable line!");
////		}
////	}
////
//////	for (int k = fromNdxInc; k < toNdxExc; k++) {
//////		logger.info(pairing[k]);
//////	}
//////
//////	logger.info((toNdxExc - fromNdxInc) + ": wasEffective: " + (totalActiveBlockTime >= HeurosSystemParam.effectiveDutyBlockHourLimit * (toNdxExc - fromNdxInc)));
////}
//
//					if (numOfDhs == 0) {
//						numOfPairsWoDh[l.getNdx()]++;
//						if (totalActiveBlockTime >= HeurosSystemParam.effectiveDutyBlockHourLimit * (toNdxExc - fromNdxInc))
//							numOfEffectivePairsWoDh[l.getNdx()]++;
//					} else
//						System.out.println("Must be an unreachable line!");
//
//				}
//			}
//		}
//	}

	private void updateActiveLegStates(LegState[] tempLegStates) {
		for (int i = 0; i < activeLegStates.length; i++) {
			this.activeLegStates[i].numOfCoverings = tempLegStates[i].numOfCoverings;

			this.activeLegStates[i].numOfIncludingDuties = tempLegStates[i].numOfIncludingDuties;
			this.activeLegStates[i].numOfIncludingDutiesWoDh = tempLegStates[i].numOfIncludingDutiesWoDh;
			this.activeLegStates[i].numOfIncludingEffectiveDuties = tempLegStates[i].numOfIncludingEffectiveDuties;
			this.activeLegStates[i].numOfIncludingEffectiveDutiesWoDh = tempLegStates[i].numOfIncludingEffectiveDutiesWoDh;

			this.activeLegStates[i].numOfIncludingPairs = tempLegStates[i].numOfIncludingPairs;
			this.activeLegStates[i].numOfIncludingPairsWoDh = tempLegStates[i].numOfIncludingPairsWoDh;
			this.activeLegStates[i].numOfIncludingEffectivePairs = tempLegStates[i].numOfIncludingEffectivePairs;
			this.activeLegStates[i].numOfIncludingEffectivePairsWoDh = tempLegStates[i].numOfIncludingEffectivePairsWoDh;

			this.activeLegStates[i].actHeurModDh = tempLegStates[i].actHeurModDh;
			this.activeLegStates[i].actHeurModEf = tempLegStates[i].actHeurModEf;
		}
	}

	private void updateActiveDutyStates(DutyState[] tempDutyStates) {
		for (int i = 0; i < activeDutyStates.length; i++) {
			this.activeDutyStates[i].numOfCoverings = tempDutyStates[i].numOfCoverings;
			this.activeDutyStates[i].numOfCoveringsActive = tempDutyStates[i].numOfCoveringsActive;
			this.activeDutyStates[i].numOfCoveringsPassiveInt = tempDutyStates[i].numOfCoveringsPassiveInt;
			this.activeDutyStates[i].numOfCoveringsPassiveExt = tempDutyStates[i].numOfCoveringsPassiveExt;
			this.activeDutyStates[i].numOfDistinctCoverings = tempDutyStates[i].numOfDistinctCoverings;
			this.activeDutyStates[i].numOfDistinctCoveringsActive = tempDutyStates[i].numOfDistinctCoveringsActive;
			this.activeDutyStates[i].numOfDistinctCoveringsPassive = tempDutyStates[i].numOfDistinctCoveringsPassive;
			this.activeDutyStates[i].blockTimeOfCoverings = tempDutyStates[i].blockTimeOfCoverings;
			this.activeDutyStates[i].blockTimeOfCoveringsActive = tempDutyStates[i].blockTimeOfCoveringsActive;
			this.activeDutyStates[i].blockTimeOfCoveringsPassiveInt = tempDutyStates[i].blockTimeOfCoveringsPassiveInt;
			this.activeDutyStates[i].blockTimeOfCoveringsPassiveExt = tempDutyStates[i].blockTimeOfCoveringsPassiveExt;
			this.activeDutyStates[i].blockTimeOfDistinctCoverings = tempDutyStates[i].blockTimeOfDistinctCoverings;
			this.activeDutyStates[i].blockTimeOfDistinctCoveringsActive = tempDutyStates[i].blockTimeOfDistinctCoveringsActive;
			this.activeDutyStates[i].blockTimeOfDistinctCoveringsPassive = tempDutyStates[i].blockTimeOfDistinctCoveringsPassive;
		}
	}

	private StateCalculator bestStateCalculator = null;

	public PairWithQuality chooseBestPairing(PairWithQuality[] pqs) throws InterruptedException, ExecutionException {

		int k = 0;
		for (int i = 0; i < pqs.length; i++) {
			PairWithQuality pwq = pqs[i];
			if (pwq.p != null) {
				if (pwq.p.getFirstDuty().getBriefTime(pwq.p.getHbNdx()).isBefore(HeurosDatasetParam.optPeriodEndExc)) {

					stateCalculators[k].setPairForEnumeration(pwq);
					Future<Double> doubleFuture = pairingProcessExecutor.submit(stateCalculators[k]);
					stateProcessL.set(k, doubleFuture);
					k++;

					/*
					 * Temoporary
					 */
//					double maxLegDifficultyScore = doubleFuture.get();
//					if ((bestStateCalculator == null)
//							|| (stateCalculator.getPwq().pairQ.isBetterInTermsOfDh(bestStateCalculator.getPwq().pairQ, maxLegDifficultyScore, bestDifficutlyScore))) {
//						bestDifficutlyScore = maxLegDifficultyScore;
//						bestStateCalculator = stateCalculator;
//					}
////					if (worstDifficutlyScore < maxLegDifficultyScore) {
////						worstDifficutlyScore = maxLegDifficultyScore;
////					}
				}
			}
		}

		if (k > 0) {

//			double worstDifficutlyScore = 0.0;
			double bestDifficutlyScore = Integer.MAX_VALUE;
//			StateCalculator bestStateCalculator = null;
			bestStateCalculator = null;

			for (int i = 0; i < k; i++) {
				double maxLegDifficultyScore = stateProcessL.get(i).get();
//logger.info(
//stateCalculators[i].getPwq().p.getNumOfDuties() + ", " + 
//stateCalculators[i].getPwq().p.getFirstDuty().getNdx() + ", " +
//stateCalculators[i].getPwq().legToCover.getNdx() + ", " +
//String.valueOf(maxLegDifficultyScore) + ", " +
//stateCalculators[i].getPwq().qm);
				if ((bestStateCalculator == null)
						|| (stateCalculators[i].getPwq().qm.isBetterInTermsOfDh(bestStateCalculator.getPwq().qm, maxLegDifficultyScore, bestDifficutlyScore))) {
					bestDifficutlyScore = maxLegDifficultyScore;
					bestStateCalculator = stateCalculators[i];
				}
//				if (worstDifficutlyScore < maxLegDifficultyScore) {
//					worstDifficutlyScore = maxLegDifficultyScore;
//				}
			}

//			StateCalculator lessStateCalculator = stateCalculators.get(0);
//			double lessDifficutlyScore = lessStateCalculator.getMaxDifficultyScoreObtained();
//			for (int i = 1; i < stateProcessL.size(); i++) {
//				if (stateCalculators.get(i).getPwq().pairQ.isBetterWithLessDuties(lessStateCalculator.getPwq().pairQ)) {
//					lessStateCalculator = stateCalculators.get(i);
//					lessDifficutlyScore = lessStateCalculator.getMaxDifficultyScoreObtained();
//				}
//			}

			/**
			 * TEST BLOCK BEGIN
			 * 
			 * Checks probable pairing numbers of Legs.
			 * 
			 */
////		if (
////			(legToCover.getNdx() == 7019) || 
////			(legToCover.getNdx() == 7810)) {
//			if (true) {
//
//				numOfPairsWoDh = new int[this.legs.size()];
//				numOfEffectivePairsWoDh = new int[this.legs.size()];
//
////			pairControlArray1 = pairControlArray2;
////			pairControlArray2 = new int[0][0];
//
//				logger.info("------------------------------------------------------------------");
//				logger.info(legToCover);
//				logger.info(bestStateCalculator.getMaxDifficultyScoreObtained());
//				logger.info(legToCover.getNumOfIncludingPairsWoDh() + "/" + legToCover.getNumOfIncludingEffectivePairsWoDh());
//				logger.info(bestStateCalculator.getPwq().pair);
//				this.pairEnumerator.enumerateAllPairings(bestStateCalculator.getTempDutyStates());
//				logger.info("------------------------------------------------------------------");
//
////			if (pairControlArray1 != null) {
////				for (int i = 0; i < pairControlArray1.length; i++) {
////					int[] pair1 = pairControlArray1[i];
////					boolean found = false;
////					for (int j = 0; j < pairControlArray2.length; j++) {
////						int[] pair2 = pairControlArray2[j];
////						if (pair1.length == pair2.length) {
////							boolean f = true; 
////							for (int k = 0; k < pair1.length; k++) {
////								if (pair1[k] != pair2[k]) {
////									f = false;
////									break;
////								}
////							}
////							if (f) {
////								found = true;
////								break;
////							}
////						}
////					}
////					if (!found) {
////						for (int j = 0; j < bestStateCalculator.pairControlArray.length; j++) {
////							int[] pairT = bestStateCalculator.pairControlArray[j];
////							if (pair1.length == pairT.length) {
////								boolean f = true; 
////								for (int k = 0; k < pair1.length; k++) {
////									if (pair1[k] != pairT[k]) {
////										f = false;
////										break;
////									}
////								}
////								if (f) {
////									found = true;
////									break;
////								}
////							}
////						}
////						if (!found) {
////							System.out.println("Pairing could not be reproduced!");
////							System.out.println("Pair1 :" + ArrayUtils.toString(pair1));
////							for (int j : pair1) {
////								System.out.println(this.duties.get(j));
////							}
////						}
////					}
////				}
////			}
//
////			if (pairControlArray1 != null) {
////				for (int j = 0; j < bestStateCalculator.pairControlArray.length; j++) {
////					int[] pairT = bestStateCalculator.pairControlArray[j];
////					boolean found = false;
////					for (int i = 0; i < pairControlArray1.length; i++) {
////						int[] pair1 = pairControlArray1[i];
////						if (pair1.length == pairT.length) {
////							boolean f = true; 
////							for (int k = 0; k < pair1.length; k++) {
////								if (pair1[k] != pairT[k]) {
////									f = false;
////									break;
////								}
////							}
////							if (f) {
////								found = true;
////								break;
////							}
////						}
////					}
////					if (!found) {
////						System.out.println("Pairing could not be reproduced!");
////						System.out.println("Pair2 :" + ArrayUtils.toString(pairT));
////						for (int i : pairT) {
////							System.out.println(this.duties.get(i));
////						}
////					}
////				}
////			}
//
//				for (int i = 0; i < this.legs.size(); i++) {
//					Leg l = this.legs.get(i);
//					if (l.isCover()
//						&& l.hasPair(hbNdx)) {
//						LegState ls = bestStateCalculator.getTempLegStates()[l.getNdx()];
//						if (!ls.arePairTotalizersOk(0,
//													0,
//													numOfPairsWoDh[l.getNdx()],
//													numOfEffectivePairsWoDh[l.getNdx()])) {
//							logger.error("LegToCover: " + legToCover);
//							logger.error("Leg pair totalizers are not set correctly! " + l);
//							ls.arePairTotalizersOk(0,
//													0,
//													numOfPairsWoDh[l.getNdx()],
//													numOfEffectivePairsWoDh[l.getNdx()]);
//						}
//					}
//				}
//			}
			/**
			 * TEST BLOCK END
			 * 
			 */

//			this.prevLegToCover = legToCover;

//			this.activeLegStates = bestStateCalculator.getTempLegStates();
//			this.activeDutyStates = bestStateCalculator.getTempDutyStates();
			this.updateActiveLegStates(bestStateCalculator.getTempLegStates());
			this.updateActiveDutyStates(bestStateCalculator.getTempDutyStates());

//			this.activeLegStates = lessStateCalculator.getTempLegStates();
//			this.activeDutyStates = lessStateCalculator.getTempDutyStates();

//logger.info("--- " + legToCover);
////logger.info("bestScore: " + bestDifficutlyScore + ", lessScore: " + lessDifficutlyScore + ", worstScore: " + worstDifficutlyScore);
//////if (worstDifficutlyScore > lessDifficutlyScore)
//////	logger.info("worstScore: " + worstDifficutlyScore + " >>>>>>> lessScore: " + lessDifficutlyScore);
////if (lessDifficutlyScore > bestDifficutlyScore)
////	logger.info("lessScore: " + lessDifficutlyScore + " >>>>>>> bestScore: " + bestDifficutlyScore);
////if (lessStateCalculator.getPwq().pair.getNumOfDuties() > bestStateCalculator.getPwq().pair.getNumOfDuties())
////	logger.info("lessNum: " + lessStateCalculator.getPwq().pair.getNumOfDuties() + " >>>>>>> bestNum: " + bestStateCalculator.getPwq().pair.getNumOfDuties());
////logger.info("LessDifficulty: " + lessStateCalculator.getPwq().pair.getNumOfDuties() + " - " + lessDifficutlyScore);
//logger.info("BestDifficulty: " + bestStateCalculator.getPwq().pair.getNumOfDuties() + " - " + bestDifficutlyScore);

			this.calculateAndSetMaxValuesOfHeuristicsParameters();

			return bestStateCalculator.getPwq();
		}
		return null;
	}

	private int numOfPairs = 0;
	private int numOfDuties = 0;
	private int numOfPairDays = 0;
	private int numOfDutyDays = 0;

	private int numOfDeadheads = 0;
	private int numOfDistinctLegsFromTheFleet = 0;
	private int numOfDistinctDeadheadLegsFromTheFleet = 0;
	private int numOfDistinctLegsOutsideOfTheFleet = 0;

//	private double totalHeurModDh = 0.0;
//	private double totalHeurModEf = 0.0;

//	private double finalCost = 0.0;

	private SolutionCost finalCost = null;

	public int getNumOfPairs() {
		return numOfPairs;
	}

	public int getNumOfDuties() {
		return numOfDuties;
	}

	public int getNumOfPairDays() {
		return numOfPairDays;
	}

	public int getNumOfDutyDays() {
		return numOfDutyDays;
	}

	public int getNumOfDeadheads() {
		return numOfDeadheads;
	}

	public int getNumOfDistinctLegsFromTheFleet() {
		return numOfDistinctLegsFromTheFleet;
	}

	public int getNumOfDistinctDeadheadLegsFromTheFleet() {
		return numOfDistinctDeadheadLegsFromTheFleet;
	}

	public int getNumOfDistinctLegsOutsideOfTheFleet() {
		return numOfDistinctLegsOutsideOfTheFleet;
	}

//	public double getTotalHeurModDh() {
//		return totalHeurModDh;
//	}
//
//	public double getTotalHeurModEf() {
//		return totalHeurModEf;
//	}

//	public double getFinalCost() {
//		return finalCost;
//	}

	public SolutionCost getFinalCost() {
		return finalCost;
	}

	public SolutionCost finalizeIteration(SolutionCost bestSoFar, SolutionCost prevCost, List<Pair> solution) {

		this.pairingProcessExecutor.shutdown();

		numOfPairs = 0;
		numOfPairDays = 0;
		numOfDuties = 0;
		numOfDutyDays = 0;

		double totalHeurModDh = 0.0;
		double totalHeurModEf = 0.0;

		/*
		 * Calculate standard fitness.
		 */
		for (int i = 0; i < solution.size(); i++) {
			Pair p = solution.get(i);
			numOfDuties += p.getNumOfDuties();
			numOfPairDays += p.getNumOfDaysTouched();
			numOfPairs++;

			boolean[] days = new boolean[p.getNumOfDaysTouched()];

			/*
			 * Set Heuristic Modifiers!
			 */
			for (int j = 0; j < p.getNumOfDuties(); j++) {
				Duty d = p.getDuties().get(j);

//				numOfDutyDays += d.getNumOfDaysTouched(this.hbNdx);
				int startingDayNdx = (int) ChronoUnit.DAYS.between(p.getFirstDuty().getBriefDay(hbNdx), d.getBriefDay(hbNdx));
				int endingDayNdx = (int) ChronoUnit.DAYS.between(p.getFirstDuty().getBriefDay(hbNdx), d.getDebriefDay(hbNdx));
				days[startingDayNdx] = true;
				days[endingDayNdx] = true;

				/*
				 * Effectiveness!
				 */
				double modValueEf = 0.0;
				if (this.activeDutyStates[d.getNdx()].blockTimeOfCoveringsActive < HeurosSystemParam.effectiveDutyBlockHourLimit) {
					modValueEf = (HeurosSystemParam.effectiveDutyBlockHourLimit - this.activeDutyStates[d.getNdx()].blockTimeOfCoveringsActive);
				}

				double modValueDhExt = activeDutyStates[d.getNdx()].numOfCoveringsPassiveExt;
				double modValueDhInt = 0.0;
				for (int k = 0; k < d.getNumOfLegs(); k++) {
					Leg l = d.getLegs().get(k);
					if (l.isCover()) {
						modValueDhInt += (activeLegStates[l.getNdx()].numOfCoverings - 1);
					}
				}

				totalHeurModDh += modValueDhExt + modValueDhInt / 2.0;
				totalHeurModEf += modValueEf;
			}
			for (boolean b : days) {
				if (b)
					numOfDutyDays++;
			}
		}

		/*
		 * Calculate solution statistics and add standard DH cost to the fitness.
		 */
		numOfDistinctLegsFromTheFleet = 0;
		numOfDistinctDeadheadLegsFromTheFleet = 0;
		numOfDistinctLegsOutsideOfTheFleet = 0;
		numOfDeadheads = 0;

		for (int i = 0; i < this.legs.size(); i++) {
			if (this.legs.get(i).isCover()) {
				if (this.activeLegStates[i].numOfCoverings > 0) 
					numOfDistinctLegsFromTheFleet++;
				if (this.activeLegStates[i].numOfCoverings > 1) {
//					fitness += (2.0 * (this.legStates[i].numOfCoverings - 1) * (this.legs.get(i).getBlockTimeInMins() / 60.0) * dhPenalty);
					numOfDeadheads += (this.activeLegStates[i].numOfCoverings - 1);
					numOfDistinctDeadheadLegsFromTheFleet++;
				}
			} else {
				if (this.activeLegStates[i].numOfCoverings > 0) {
//					fitness += (2.0 * this.legStates[i].numOfCoverings * (this.legs.get(i).getBlockTimeInMins() / 60.0) * dhPenalty);
					numOfDeadheads += this.activeLegStates[i].numOfCoverings;
					numOfDistinctLegsOutsideOfTheFleet++;
				}
			}
		}

//		finalCost = (totalHeurModDh * HeurosSystemParam.weightHeurModDh + totalHeurModEf * HeurosSystemParam.weightHeurModEf / HeurosSystemParam.effectiveDutyBlockHourLimit);
		finalCost = new SolutionCost(totalHeurModDh, totalHeurModEf);
				

		boolean bestFound = finalCost.doesPerformBetterThan(bestSoFar);
		boolean solutionIsImproved = finalCost.doesPerformBetterThan(prevCost);

		/*
		 * Calculate standard fitness and heuristic cost to be able to calculate value of Heuristic Modifiers.
		 */
		for (int i = 0; i < solution.size(); i++) {
			Pair p = solution.get(i);
			/*
			 * Set Heuristic Modifiers!
			 */
			for (int j = 0; j < p.getNumOfDuties(); j++) {
				Duty d = p.getDuties().get(j);
				/*
				 * Effectiveness!
				 */
				double modValueEf = 0.0;
				if (this.activeDutyStates[d.getNdx()].blockTimeOfCoveringsActive < HeurosSystemParam.effectiveDutyBlockHourLimit) {
					modValueEf = (HeurosSystemParam.effectiveDutyBlockHourLimit - this.activeDutyStates[d.getNdx()].blockTimeOfCoveringsActive);
				}

				double modValueDhExt = activeDutyStates[d.getNdx()].numOfCoveringsPassiveExt;
				double modValueDhInt = 0.0;
				for (int k = 0; k < d.getNumOfLegs(); k++) {
					Leg l = d.getLegs().get(k);
					if (l.isCover()) {
						modValueDhInt += (activeLegStates[l.getNdx()].numOfCoverings - 1);
					}
				}
				double modValueDh = modValueDhExt + modValueDhInt;

				for (int k = 0; k < d.getNumOfLegs(); k++) {
					Leg l = d.getLegs().get(k);
					if (l.isCover()) {
						/*
						 * Set dh related heuristicModifier.
						 */
						if (bestFound) {
							activeLegStates[l.getNdx()].bestHeurModDh = activeLegStates[l.getNdx()].actHeurModDh;
							activeLegStates[l.getNdx()].actHeurModDh = modValueDh + activeLegStates[l.getNdx()].actHeurModDh * HeurosSystemParam.hmResetWeightAfterBestSol;
						} else
							if (solutionIsImproved)
								activeLegStates[l.getNdx()].actHeurModDh = modValueDh + activeLegStates[l.getNdx()].actHeurModDh * HeurosSystemParam.hmResetWeightAfterImprSol;
							else
								activeLegStates[l.getNdx()].actHeurModDh += modValueDh;

						/*
						 * Set ef related heuristicModifier.
						 */
						if (bestFound) {
							activeLegStates[l.getNdx()].bestHeurModEf = activeLegStates[l.getNdx()].actHeurModEf;
							activeLegStates[l.getNdx()].actHeurModEf = modValueEf + activeLegStates[l.getNdx()].actHeurModEf * HeurosSystemParam.hmResetWeightAfterBestSol;
						} else
							if (solutionIsImproved)
								activeLegStates[l.getNdx()].actHeurModEf = modValueEf + activeLegStates[l.getNdx()].actHeurModEf * HeurosSystemParam.hmResetWeightAfterImprSol;
							else
								activeLegStates[l.getNdx()].actHeurModEf += modValueEf;
					}
				}
			}
		}

		return finalCost;
	}

//	/*
//	 * COST calculations.
//	 */
//
//	private int hotelTransportTime = 30;
//
//	private int hotelCostDomPerCrewPerDay = 10000;	//	1000;
//	private int hotelCostIntPerCrewPerDay = 10000;	//	1000;
//
//	private int hotelTransferCost = 40;
//
//	private int getHotelCostPerCrew(AirportView ap, int gmtDiff, LocalDateTime debriefTime, LocalDateTime briefTime) {
//		if (briefTime == null)
//			return 0;
//
//		LocalDateTime hotelIn = debriefTime.plusMinutes(hotelTransportTime + gmtDiff);
//		LocalDateTime hotelOut = briefTime.plusMinutes(- hotelTransportTime + gmtDiff);
//
//		LocalDateTime hotelIn00 = hotelIn.truncatedTo(ChronoUnit.DAYS);
//		LocalDateTime hotelOut00 = hotelOut.minusSeconds(1).truncatedTo(ChronoUnit.DAYS);
//		int numOfLayNights = (int) ChronoUnit.DAYS.between(hotelIn00, hotelOut00);
//
//		if ((ChronoUnit.DAYS.between(hotelIn, hotelOut) < 1.0)
//				|| (numOfLayNights == 0)) {
//
//        	if (ap.isDomestic())
//        		return hotelCostDomPerCrewPerDay;
//        	else
//        		return hotelCostIntPerCrewPerDay;
//        }
//        else
//        	if (ap.isDomestic())
//        		return hotelCostDomPerCrewPerDay * numOfLayNights;
//        	else
//        		return hotelCostIntPerCrewPerDay * numOfLayNights;
//    }
//
//	private double dutyDayPenalty = 60000.0;	//	20000.0;
//	private double dutyHourPenalty = 1050.0;	//	0.0;
//	private double dhPenalty = 500000.0;	//	10000.0;
//	private double acChangePenalty = 250.0;	//	20.0;
//	private double squareRestPenalty = 2.0;
//	private int longRestLimit = 14 * 60;
//	private double longRestPenalty = 400;	//	700;
//	private double longConnPenalty = 100;	//	40;
//	private double augmentionHourPenalty = 100000.0;	//	2000;
//	private double specialDhPenalty = 10000.0;
//	private double augmentionDayPenalty = 0.0;	//	4000.0;
//
//	private double getDutyCost(int hbNdx, int cc, DutyView d, boolean hbDep, LocalDateTime nt) {
//		LocalDateTime sh = d.getBriefTime(hbNdx);
//		LocalDateTime eh = d.getDebriefTime(hbNdx);
//
//		if (nt == null)
//			eh = d.getDebriefDayEnding(hbNdx);
//		else
//			eh = nt;
//		if (hbDep)
//			sh = d.getBriefDayBeginning(hbNdx);
//
//		double dutyDurationInMins = ChronoUnit.MINUTES.between(sh, eh);
//		double dCost = 0.0;
//
//		dCost += cc * dutyDurationInMins * dutyDayPenalty / (24.0 * 60.0);
//
//		if (hbDep)
//			dCost += cc * d.getDutyDurationInMins(hbNdx) * dutyHourPenalty / 60.0;
//		else
//			dCost += cc * d.getDutyDurationInMins(hbNdx) * dutyHourPenalty / 60.0;
//
//		dCost += acChangePenalty * d.getNumOfAcChanges();
//
//		if (nt != null) {
//			double idleTime = ChronoUnit.MINUTES.between(d.getDebriefTime(hbNdx), nt);
//
//			dCost += cc * getHotelCostPerCrew(d.getLastArrAirport(), d.getLastLeg().getArrOffset(), d.getDebriefTime(hbNdx), nt);
//
//			dCost += 2.0 * hotelTransferCost;
//
//			if (squareRestPenalty * idleTime * idleTime / 10000.0 > (squareRestPenalty * cc * dutyDayPenalty / 20.0))
//				dCost += squareRestPenalty * idleTime * idleTime / 10000.0;
//			else
//				dCost += (squareRestPenalty * cc * dutyDayPenalty) / 20.0;
//
//			if (idleTime > longRestLimit) {
//				dCost += (idleTime - longRestLimit) * longRestPenalty / 60.0;
//			}
//
//		} else
//			if (hbDep) {
//				dCost += cc * dutyDayPenalty / 20.0;
//			}
//
//		dCost += d.getLongConnDiff() * longConnPenalty / 60.0;
//
//		if (hbDep) {
//			if (d.getAugmented(hbNdx) > 0) {
//				dCost += d.getDutyDurationInMins(hbNdx) * augmentionHourPenalty / 60.0;
//				dCost += augmentionDayPenalty;
//			}
//		} else {
//			if (d.getAugmented(hbNdx) > 0) {
//				dCost += d.getDutyDurationInMins(hbNdx) * augmentionHourPenalty / 60.0;
//				dCost += augmentionDayPenalty;
//			}
//		}
//
//		dCost += d.getNumOfSpecialFlights() * specialDhPenalty;
//
////		if (includeDh)
////			dCost += cc * (d.getBlockTimeInMins() / 60.0) * dhPenalty;
////		else
////			dCost -= cc * (d.getBlockTimeInMins() / 60.0) * dhPenalty;
//
//		return dCost;
//	}
//
//	private double getPairCost(int cc, Pair p) {
//		DutyView pd = p.getFirstDuty();
//		if (p.getNumOfDuties() == 1)
//			return getDutyCost(p.getHbNdx(), cc, pd, true, null);
//		else {
//			DutyView nd = null;
//			double pCost = 0.0;
//			for (int i = 1; i < p.getNumOfDuties(); i++) {
//				nd = p.getDuties().get(i);
//				pCost += getDutyCost(p.getHbNdx(), cc, pd, i == 1, nd.getBriefTime(p.getHbNdx()));
//				pd = nd;
//			}
//			pCost += getDutyCost(p.getHbNdx(), cc, nd, false, null);
//			return pCost;
//		}
//	}
}
