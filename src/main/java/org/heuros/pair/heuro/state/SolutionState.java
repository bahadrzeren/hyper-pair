package org.heuros.pair.heuro.state;

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
import org.heuros.data.model.Duty;
import org.heuros.data.model.Leg;
import org.heuros.data.model.Pair;
import org.heuros.pair.conf.HeurosDatasetParam;
import org.heuros.pair.conf.HeurosSystemParam;
import org.heuros.pair.enumerator.PairEnumeratorWoRuleCheck;
import org.heuros.pair.enumerator.PairListener;
import org.heuros.pair.sp.PairWithQuality;

public class SolutionState implements PairListener {

	private static Logger logger = Logger.getLogger(SolutionState.class);

	/*
	 * TODO Single base assumption!!!
	 */
	private int hbNdx = 0;

	private PairOptimizationContext pairOptimizationContext = null;
	private List<Leg> legs = null;
	private List<Duty> duties = null;
//	private OneDimIndexInt<Duty> dutyIndexByLegNdx = null;
	private DutyLegOvernightConnNetwork pricingNetwork = null;
	private LegState[] activeLegStates = null;
	private DutyState[] activeDutyStates = null;

//	private List<Leg> orderedLegs = null;

	private PairEnumeratorWoRuleCheck pairEnumerator = null;

	public SolutionState(PairOptimizationContext pairOptimizationContext,
							DutyLegOvernightConnNetwork pricingNetwork) {
		this.pairOptimizationContext = pairOptimizationContext;
		this.legs = pairOptimizationContext.getLegRepository().getModels();
		this.duties = pairOptimizationContext.getDutyRepository().getModels();
//		this.dutyIndexByLegNdx = pairOptimizationContext.getDutyIndexByLegNdx();
		this.pricingNetwork = pricingNetwork;

		this.activeLegStates = new LegState[this.legs.size()];
		this.activeDutyStates = new DutyState[this.duties.size()];

		for (int j = 0; j < this.activeLegStates.length; j++)
			this.activeLegStates[j] = new LegState(this.legs.get(j));
		for (int j = 0; j < this.activeDutyStates.length; j++)
			this.activeDutyStates[j] = new DutyState(this.duties.get(j));

		pairEnumerator = new PairEnumeratorWoRuleCheck(pairOptimizationContext,
														pricingNetwork,
														this);
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

			if (LegState.maxHeurModDh < this.activeLegStates[j].heurModDh)
				LegState.maxHeurModDh = this.activeLegStates[j].heurModDh;
			if (LegState.maxHeurModEf < this.activeLegStates[j].heurModEf)
				LegState.maxHeurModEf = this.activeLegStates[j].heurModEf;
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

//	private void setIncludingPairingsForTest(Leg legToCover,
//												int numOfDutiesOfChosenPair,
//												Duty[] pairing,
//												int numOfDuties,
//												int beforeNumOfDhs,
//												int beforeTotalActiveBlockTime,
//												int numOfDhs,
//												int totalActiveBlockTime,
//												int[] numOfPairs,
//												int[] numOfEffectivePairs,
//												int[] numOfPairsWoDh,
//												int[] numOfEffectivePairsWoDh) {
//		for (int i = 0; i < numOfDuties; i++) {
//			for (int j = 0; j < pairing[i].getNumOfLegs(); j++) {
//				Leg l = pairing[i].getLegs().get(j);
//				if (l.isCover()
//						&& l.hasPair(hbNdx)) {
//
////boolean beforeEffectivenessState = (beforeTotalActiveBlockTime >= HeurosSystemParam.effectiveDutyBlockHourLimit * numOfDuties);
////boolean beforeDhState = (beforeNumOfDhs == 0);
////boolean beforeEffectivenessWoDhState = beforeDhState && (beforeTotalActiveBlockTime >= HeurosSystemParam.effectiveDutyBlockHourLimit * numOfDuties);
////
////boolean effectivenessState = (totalActiveBlockTime >= HeurosSystemParam.effectiveDutyBlockHourLimit * numOfDuties);
////boolean dhState = (numOfDhs == 0);
////boolean effectivenessWoDhState = dhState && (totalActiveBlockTime >= HeurosSystemParam.effectiveDutyBlockHourLimit * numOfDuties);
////
////if (beforeEffectivenessState && (!effectivenessState) && 
//////		beforeDhState && (!dhState) && 
////		l.getNdx() == 356) {
////	logger.info("xxxxxxxxxxxxxxxxxxxxxxx");
////	if (numOfDuties == 1)
////		logger.info(legToCover.getNdx() + "-" + numOfDutiesOfChosenPair + ": " + pairing[0].getNdx());
////	else
////		logger.info(legToCover.getNdx() + "-" + numOfDutiesOfChosenPair + ": " + pairing[0].getNdx() + ", " + pairing[1].getNdx());
////	if (numOfDuties == 1)
////		logger.info(pairing[0]);
////	else {
////		logger.info(pairing[0]);
////		logger.info(pairing[1]);
////	}
////	logger.info(legToCover.getNdx() + "-" + numOfDutiesOfChosenPair + ": effectivenessState: " + effectivenessState +
////																		", dhState: " + dhState +
////																		", effectivenessWoDhState: " + effectivenessWoDhState);
////	logger.info(legToCover.getNdx() + "-" + numOfDutiesOfChosenPair + ": Before PairTots: " + numOfPairs[l.getNdx()] + ", " +
////																								numOfEffectivePairs[l.getNdx()] + ", " +
////																								numOfPairsWoDh[l.getNdx()] + ", " +
////																								numOfEffectivePairsWoDh[l.getNdx()]);
////}
//
//					numOfPairs[l.getNdx()]++;
//					if (totalActiveBlockTime >= HeurosSystemParam.effectiveDutyBlockHourLimit * numOfDuties)
//						numOfEffectivePairs[l.getNdx()]++;
//					if (numOfDhs == 0) {
//						numOfPairsWoDh[l.getNdx()]++;
//						if (totalActiveBlockTime >= HeurosSystemParam.effectiveDutyBlockHourLimit * numOfDuties)
//							numOfEffectivePairsWoDh[l.getNdx()]++;
//					}
//
////if (beforeEffectivenessState && (!effectivenessState) && 
//////		beforeDhState && (!dhState) && 
////		l.getNdx() == 356) {
////	logger.info(legToCover.getNdx() + "-" + numOfDutiesOfChosenPair + ": After PairTots: " + numOfPairs[l.getNdx()] + ", " +
////																								numOfEffectivePairs[l.getNdx()] + ", " +
////																								numOfPairsWoDh[l.getNdx()] + ", " +
////																								numOfEffectivePairsWoDh[l.getNdx()]);
////}
//
//				}
//			}
//		}
//	}

	public int[][] pairControlArray1 = null;
	public int[][] pairControlArray2 = null;

	private int[] numOfPairsWoDh = null;
	private int[] numOfEffectivePairsWoDh = null;

	@Override
	public void onPairingFound(Duty[] pairing, int fromNdxInc, int toNdxExc, int numOfDhs, int totalActiveBlockTime) {
		for (int i = fromNdxInc; i < toNdxExc; i++) {
			for (int j = 0; j < pairing[i].getNumOfLegs(); j++) {
				Leg l = pairing[i].getLegs().get(j);
				if (l.isCover()
						&& l.hasPair(hbNdx)) {

if (l.getNdx() == 498) {

	pairControlArray2 = ArrayUtils.add(pairControlArray2, new int[toNdxExc - fromNdxInc]);
	int h = 0;
	for (int k = fromNdxInc; k < toNdxExc; k++) {
		pairControlArray2[pairControlArray2.length - 1][h] = pairing[k].getNdx();
		h++;
	}



	logger.info("XXXXXXXXXXXXXXXXXXXX");
	if (toNdxExc - fromNdxInc == 1)
		logger.info((toNdxExc - fromNdxInc) + ": " + pairing[fromNdxInc].getNdx());
	else
		if (toNdxExc - fromNdxInc == 2)
			logger.info((toNdxExc - fromNdxInc) + ": " + pairing[fromNdxInc].getNdx() + ", " + 
															pairing[fromNdxInc + 1].getNdx());
		else
			if (toNdxExc - fromNdxInc == 3)
				logger.info((toNdxExc - fromNdxInc) + ": " + pairing[fromNdxInc].getNdx() + ", " + 
																pairing[fromNdxInc + 1].getNdx() + ", " + 
																pairing[fromNdxInc + 2].getNdx());
			else
				if (toNdxExc - fromNdxInc == 4)
					logger.info((toNdxExc - fromNdxInc) + ": " + pairing[fromNdxInc].getNdx() + ", " + 
																	pairing[fromNdxInc + 1].getNdx() + ", " +
																	pairing[fromNdxInc + 2].getNdx() + ", " +
																	pairing[fromNdxInc + 3].getNdx());

	if (bestStateCalculator.pairControlArray != null) {
		for (int[] dutyNdxs: bestStateCalculator.pairControlArray) {
			if (dutyNdxs.length == (toNdxExc - fromNdxInc)) {
				boolean f = true;
				for (int k = 0; k < dutyNdxs.length; k++) {
					if ((dutyNdxs[k] == 0)
							|| (pairing[fromNdxInc - 1] != null)
							|| (pairing[toNdxExc] != null))
						System.out.println("Must be an unreachable line!");
					if (dutyNdxs[k] != pairing[fromNdxInc + k].getNdx()) {
						f = false;
						break;
					}
				}
				if (f)
					System.out.println("Must be an unreachable line!");
			} else
				if ((pairing[fromNdxInc - 1] != null)
						|| (pairing[toNdxExc] != null))
					System.out.println("Must be an unreachable line!");
		}
	}

	for (int k = fromNdxInc; k < toNdxExc; k++) {
		logger.info(pairing[k]);
	}

	logger.info((toNdxExc - fromNdxInc) + ": wasEffective: " + (totalActiveBlockTime >= HeurosSystemParam.effectiveDutyBlockHourLimit * (toNdxExc - fromNdxInc)));
}

					if (numOfDhs == 0) {
						numOfPairsWoDh[l.getNdx()]++;
						if (totalActiveBlockTime >= HeurosSystemParam.effectiveDutyBlockHourLimit * (toNdxExc - fromNdxInc))
							numOfEffectivePairsWoDh[l.getNdx()]++;
					} else
						System.out.println("Must be an unreachable line!");

				}
			}
		}
	}

	private ExecutorService pairingProcessExecutor = Executors.newFixedThreadPool(HeurosSystemParam.maxPairingLengthInDays * HeurosSystemParam.maxNumOfPairingSetsToEval);

	private StateCalculator bestStateCalculator = null;

	public Pair chooseBestPairing(Leg legToCover, PairWithQuality[][] pqs) throws InterruptedException, ExecutionException {

//if (legToCover.getNdx() == 5204)
//System.out.println();

//		double worstDifficutlyScore = 0.0;
		double bestDifficutlyScore = Integer.MAX_VALUE;
//		StateCalculator bestStateCalculator = null;
		bestStateCalculator = null;

		List<StateCalculator> stateCalculators = new ArrayList<StateCalculator>(pqs.length);
		List<Future<Double>> stateProcessL = new ArrayList<Future<Double>>(pqs.length);
		for (int i = 0; i < pqs.length; i++) {
			for (int j = 0; j < pqs[i].length; j++) {
			PairWithQuality pwq = pqs[i][j];
			if (pwq.pair != null) {
				if (pwq.pair.getFirstDuty().getBriefTime(pwq.pair.getHbNdx()).isBefore(HeurosDatasetParam.optPeriodEndExc)) {
					StateCalculator stateCalculator = new StateCalculator(pairOptimizationContext,
																			pricingNetwork,
																			legToCover,
																			activeLegStates,
																			activeDutyStates,
																			pwq);
					stateCalculators.add(stateCalculator);
					Future<Double> doubleFuture = pairingProcessExecutor.submit(stateCalculator);
					stateProcessL.add(doubleFuture);

					/*
					 * Temoporary
					 */
					double maxLegDifficultyScore = doubleFuture.get();
					if ((bestStateCalculator == null)
							|| (stateCalculator.getPwq().pairQ.isBetterInTermsOfDh(bestStateCalculator.getPwq().pairQ, maxLegDifficultyScore, bestDifficutlyScore))) {
						bestDifficutlyScore = maxLegDifficultyScore;
						bestStateCalculator = stateCalculator;
					}
//					if (worstDifficutlyScore < maxLegDifficultyScore) {
//						worstDifficutlyScore = maxLegDifficultyScore;
//					}
				}
			}
			}
		}

//		for (int i = 0; i < stateProcessL.size(); i++) {
//			double maxLegDifficultyScore = stateProcessL.get(i).get();
//			if ((bestStateCalculator == null)
//					|| (stateCalculators.get(i).getPwq().pairQ.isBetterInTermsOfDh(bestStateCalculator.getPwq().pairQ, maxLegDifficultyScore, bestDifficutlyScore))) {
//				bestDifficutlyScore = maxLegDifficultyScore;
//				bestStateCalculator = stateCalculators.get(i);
//			}
////			if (worstDifficutlyScore < maxLegDifficultyScore) {
////				worstDifficutlyScore = maxLegDifficultyScore;
////			}
//		}

//		StateCalculator lessStateCalculator = stateCalculators.get(0);
//		double lessDifficutlyScore = lessStateCalculator.getMaxDifficultyScoreObtained();
//		for (int i = 1; i < stateProcessL.size(); i++) {
//			if (stateCalculators.get(i).getPwq().pairQ.isBetterWithLessDuties(lessStateCalculator.getPwq().pairQ)) {
//				lessStateCalculator = stateCalculators.get(i);
//				lessDifficutlyScore = lessStateCalculator.getMaxDifficultyScoreObtained();
//			}
//		}

		/**
		 * TEST BLOCK BEGIN
		 * 
		 * Checks probable pairing numbers of Legs.
		 * 
		 */
		numOfPairsWoDh = new int[this.legs.size()];
		numOfEffectivePairsWoDh = new int[this.legs.size()];

		pairControlArray1 = pairControlArray2;
		pairControlArray2 = new int[0][0];

		this.pairEnumerator.enumerateAllPairings(bestStateCalculator.getTempDutyStates());

		if (pairControlArray1 != null) {
			for (int i = 0; i < pairControlArray1.length; i++) {
				int[] pair1 = pairControlArray1[i];
				boolean found = false;
				for (int j = 0; j < pairControlArray2.length; j++) {
					int[] pair2 = pairControlArray2[j];
					if (pair1.length == pair2.length) {
						boolean f = true; 
						for (int k = 0; k < pair1.length; k++) {
							if (pair1[k] != pair2[k]) {
								f = false;
								break;
							}
						}
						if (f) {
							found = true;
							break;
						}
					}
				}
				if (!found) {
					for (int j = 0; j < bestStateCalculator.pairControlArray.length; j++) {
						int[] pairT = bestStateCalculator.pairControlArray[0];
						if (pair1.length == pairT.length) {
							boolean f = true; 
							for (int k = 0; k < pair1.length; k++) {
								if (pair1[k] != pairT[k]) {
									f = false;
									break;
								}
							}
							if (f) {
								found = true;
								break;
							}
						}
					}
					if (!found) {
						System.out.println("Pairing could not be reproduced!");
						System.out.println("Pair1 :" + ArrayUtils.toString(pair1));
					}
				}
			}
		}
		for (int i = 0; i < this.legs.size(); i++) {
			Leg l = this.legs.get(i);
			if (l.isCover()
				&& l.hasPair(hbNdx)) {
				LegState ls = bestStateCalculator.getTempLegStates()[l.getNdx()];
				if (!ls.arePairTotalizersOk(0,
											0,
											numOfPairsWoDh[l.getNdx()],
											numOfEffectivePairsWoDh[l.getNdx()])) {
					logger.error("LegToCover: " + legToCover);
					logger.error("Leg pair totalizers are not set correctly! " + l);
					ls.arePairTotalizersOk(0,
											0,
											numOfPairsWoDh[l.getNdx()],
											numOfEffectivePairsWoDh[l.getNdx()]);
				}
			}
		}
		/**
		 * TEST BLOCK END
		 * 
		 */

		this.activeLegStates = bestStateCalculator.getTempLegStates();
		this.activeDutyStates = bestStateCalculator.getTempDutyStates();

//		this.activeLegStates = lessStateCalculator.getTempLegStates();
//		this.activeDutyStates = lessStateCalculator.getTempDutyStates();

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

		return bestStateCalculator.getPwq().pair;
	}

//	private int[][] numOfLegsThatCoveredDutyHas = null;
//	private int[][] numOfLegsThatCoveredPairHas = null;
//	private int[][] numOfDutiesThatCoveredPairHas = null;
//	private int[][] numOfDh = null;
//	private int[][] numOfDhThatCoveredDutyHas = null;
//	private int[][] numOfDhThatCoveredPairHas = null;
//	private int[][] nextLeg = null;
//	private int[][] prevLeg = null;
//	private int[][] dutyCovers = null;
//	private int[][] nextDuty = null;
//	private int[][] prevDuty = null;

	public double finalizeIteration(int iterationNumber, List<Pair> solution, int uncoveredLegs) {

//		if (numOfLegsThatCoveredDutyHas == null) {
//			numOfLegsThatCoveredDutyHas = new int[this.legs.size()][100];
//			numOfLegsThatCoveredPairHas = new int[this.legs.size()][100];
//			numOfDutiesThatCoveredPairHas = new int[this.legs.size()][100];
//			numOfDh = new int[this.legs.size()][100];
//			numOfDhThatCoveredDutyHas = new int[this.legs.size()][100];
//			numOfDhThatCoveredPairHas = new int[this.legs.size()][100];
//			nextLeg = new int[this.legs.size()][100];
//			prevLeg = new int[this.legs.size()][100];
//			dutyCovers = new int[this.legs.size()][100];
//			nextDuty = new int[this.legs.size()][100];
//			prevDuty = new int[this.legs.size()][100];
//		}

//		double fitness = 0.0;
		int numOfDuties = 0;
		int numOfPairDays = 0;
		int numOfPairs = 0;

		double totalHeurModDh = 0.0;
		double totalHeurModEf = 0.0;

		/*
		 * Calculate standard fitness and heuristic cost to be able to calculate value of Heuristic Modifiers.
		 */
		for (int i = 0; i < solution.size(); i++) {
			Pair p = solution.get(i);
//			fitness += getPairCost(2, p);
			numOfDuties += p.getNumOfDuties();
			numOfPairDays += p.getNumOfDaysTouched();
			numOfPairs++;

//			/*
//			 * Effectiveness cost!
//			 */
//			double totalActiveTime = 0.0;
//			for (int j = 0; j < p.getNumOfDuties(); j++) {
//				Duty d = p.getDuties().get(j);
//				totalActiveTime += this.activeDutyStates[d.getNdx()].blockTimeOfCoveringsActive;
//			}
//			double modValueEf = 0.0;
//			if (totalActiveTime < HeurosSystemParam.effectiveDutyBlockHourLimit * p.getNumOfDuties()) {
//				modValueEf = (HeurosSystemParam.effectiveDutyBlockHourLimit - totalActiveTime / p.getNumOfDuties());
//			}

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

//				double modValueDh = activeDutyStates[d.getNdx()].numOfCoveringsPassiveExt + activeDutyStates[d.getNdx()].numOfCoveringsPassiveInt;
				double modValueDh = activeDutyStates[d.getNdx()].numOfCoveringsPassiveExt;
				for (int k = 0; k < d.getNumOfLegs(); k++) {
					Leg l = d.getLegs().get(k);
					if (l.isCover()) {
						modValueDh += (activeLegStates[l.getNdx()].numOfCoverings - 1);
					}
				}
				for (int k = 0; k < d.getNumOfLegs(); k++) {
					Leg l = d.getLegs().get(k);
					if (l.isCover()) {
						activeLegStates[l.getNdx()].numOfIterations = iterationNumber;
						/*
						 * Set dh related heuristicModifier.
						 */
						activeLegStates[l.getNdx()].heurModDh = activeLegStates[l.getNdx()].heurModDh + modValueDh + (activeLegStates[l.getNdx()].numOfCoverings - 1);
						totalHeurModDh += modValueDh + (activeLegStates[l.getNdx()].numOfCoverings - 1);
						/*
						 * Set ef related heuristicModifier.
						 */
						activeLegStates[l.getNdx()].heurModEf = activeLegStates[l.getNdx()].heurModEf + modValueEf;
						totalHeurModEf += modValueEf;
					}
				}
			}
		}

		/*
		 * Calculate solution statistics and add standard DH cost to the fitness.
		 */
		int numOfDistinctLegsFromTheFleet = 0;
		int numOfDistinctDeadheadLegsFromTheFleet = 0;
		int numOfDistinctLegsOutsideOfTheFleet = 0;
		int numOfDeadheads = 0;

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

		logger.info("numOfPairs: " + numOfPairs + 
					", numOfDuties: " + numOfDuties +
					", numOfPairDays:" + numOfPairDays +
					", uncoveredLegs: " + uncoveredLegs + 
					", numOfDeadheads: " + numOfDeadheads + 
					", numOfDistinctLegsFromTheFleet: " + numOfDistinctLegsFromTheFleet +
					", numOfDistinctDeadheadLegsFromTheFleet: " + numOfDistinctDeadheadLegsFromTheFleet +
					", numOfDistinctLegsOutsideOfTheFleet: " + numOfDistinctLegsOutsideOfTheFleet +
					", totalHeurModDh: " + totalHeurModDh +
					", totalHeurModEf: " + totalHeurModEf);

		return totalHeurModDh * HeurosSystemParam.weightHeurModDh + totalHeurModEf * HeurosSystemParam.weightHeurModEf;
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
