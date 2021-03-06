package org.heuros.pair.heuro;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.heuros.context.PairOptimizationContext;
import org.heuros.data.DutyLegOvernightConnNetwork;
import org.heuros.data.model.Pair;
import org.heuros.pair.SolutionCost;
import org.heuros.pair.conf.HeurosAlgParameters;
import org.heuros.pair.heuro.state.SolutionState;

public class HeuroOptimizer {

	private static Logger logger = Logger.getLogger(HeuroOptimizer.class);

	private int numOfHeuristics = 3;
	public int getNumOfHeuristics() {
		return numOfHeuristics;
	}

//	/*
//	 * TODO Single base assumption!!!
//	 */
//	private int hbNdx = 0;

	private PairOptimizationContext pairOptimizationContext = null;
//	private List<Leg> legs = null;
//	private List<Duty> duties = null;
//	private OneDimIndexInt<Duty> dutyIndexByLegNdx = null;

	private DutyLegOvernightConnNetwork pricingNetwork = null;

	public HeuroOptimizer(PairOptimizationContext pairOptimizationContext,
							DutyLegOvernightConnNetwork pricingNetwork) {
		this.pairOptimizationContext = pairOptimizationContext;
//		this.legs = pairOptimizationContext.getLegRepository().getModels();
//		this.duties = pairOptimizationContext.getDutyRepository().getModels();
//		this.dutyIndexByLegNdx = pairOptimizationContext.getDutyIndexByLegNdx();
		this.pricingNetwork = pricingNetwork;
	}

	public String doMinimize() throws InterruptedException, ExecutionException, CloneNotSupportedException {
		logger.info("Optimization process is started!");

		SolutionCost prevCost = null;
		SolutionCost bestCost = null;
		String bestStr = null;

		int numOfIterationsWOProgress = 0;
		long optStartTime = System.nanoTime();

		SolutionState solutionState = null;

		for (int i = 0; i < HeurosAlgParameters.maxNumOfIterations; i++) {

			List<Pair> solution = new ArrayList<Pair>();

			SolutionGenerator solGen = new SolutionGenerator(this.pairOptimizationContext, this.pricingNetwork, solutionState);
			solutionState = solGen.generateSolution(bestCost, prevCost, solution);

			/**
			 * TEST BLOCK BEGIN
			 * 
			 * Checks DEADHEAD TOTALIZERS!
			 * 
			 */
//			for (int j = 0; j < legParams.length; j++) {
//				if (((legParams[j].numOfCoverings == 0)
//						&& this.legs.get(j).isCover()
//						&& (this.legs.get(j).getNumOfDutiesWoDh() > 0)
//						&& (legParams[j].numOfDutiesWoDh < 1 || legParams[j].numOfDutiesWoDh > this.legs.get(j).getNumOfDutiesWoDh()))
//					|| ((legParams[j].numOfCoverings != 0) && (legParams[j].numOfDutiesWoDh != 0)))
//					logger.error("#Cvr: " + legParams[j].numOfCoverings + ", #DwoDh: " + legParams[j].numOfDutiesWoDh + "/" + this.legs.get(j).getNumOfDutiesWoDh() + " -> " + this.legs.get(j));
//			}
//			for (int j = 0; j < dutyParams.length; j++) {
//				if ((dutyParams[j].numOfAlternativeDutiesWoDh < 0)
//					|| ((dutyParams[j].numOfAlternativeDutiesWoDh != 0)
//						&& (dutyParams[j].numOfDistinctCoverings == this.duties.get(j).getNumOfLegs()))) {
//					logger.error("#Cvr: " + dutyParams[j].numOfCoverings + ", #DwoDh: " + dutyParams[j].numOfAlternativeDutiesWoDh + "/" + this.duties.get(j).getTotalNumOfAlternativeDutiesWoDh());
//					logger.error(this.duties.get(j));
//				}
//			}
//			for (int j = 0; j < dutyParams.length; j++) {
//				Duty d = this.duties.get(j);
//				int totalNumOfDutiesWoDh = 0; 
//				for (int k = 0; k < d.getNumOfLegs(); k++) {
//					totalNumOfDutiesWoDh += legParams[d.getLegs().get(k).getNdx()].numOfDutiesWoDh;
//				}
//				if (dutyParams[j].numOfAlternativeDutiesWoDh != totalNumOfDutiesWoDh) {
//					logger.error("#Cvr: " + dutyParams[j].numOfCoverings + ", #DwoDh: " + dutyParams[j].numOfAlternativeDutiesWoDh + " != " + totalNumOfDutiesWoDh);
//					logger.error(this.duties.get(j));
//				}
//			}
			/**
			 * TEST BLOCK END
			 */
			
			if (bestStr != null)
				logger.info(bestStr);
			logger.info(i + ".th itr" +
						" #Pairs: " + solutionState.getNumOfPairs() +
						" #Duties: " + solutionState.getNumOfDuties() +
						" #PairDays: " + solutionState.getNumOfPairDays() +
						" #DutyDays: " + solutionState.getNumOfDutyDays() +
						" #Dh: " + solutionState.getNumOfDeadheads() +
						" #LegsInt: " + solutionState.getNumOfDistinctLegsFromTheFleet() +
						" #LegsIntDh: " + solutionState.getNumOfDistinctDeadheadLegsFromTheFleet() +
						" #LegsFltExt: " + solutionState.getNumOfDistinctLegsOutsideOfTheFleet() +
						" TotHM_Dh: " + solutionState.getFinalCost().getTotalHeurModDh() +
						" TotHM_Ef: " + solutionState.getFinalCost().getTotalHeurModEf() +
						" FinalCost: " + solutionState.getFinalCost());

			/*
			 * Improvement test.
			 */
			if (solutionState.getFinalCost().doesPerformBetterThan(bestCost)) {
				bestCost = solutionState.getFinalCost();
				bestStr = i + ".th itr" +
							" #Pairs: " + solutionState.getNumOfPairs() +
							" #Duties: " + solutionState.getNumOfDuties() +
							" #PairDays: " + solutionState.getNumOfPairDays() +
							" #DutyDays: " + solutionState.getNumOfDutyDays() +
							" #Dh: " + solutionState.getNumOfDeadheads() +
							" #LegsInt: " + solutionState.getNumOfDistinctLegsFromTheFleet() +
							" #LegsIntDh: " + solutionState.getNumOfDistinctDeadheadLegsFromTheFleet() +
							" #LegsFltExt: " + solutionState.getNumOfDistinctLegsOutsideOfTheFleet() +
							" TotHM_Dh: " + solutionState.getFinalCost().getTotalHeurModDh() +
							" TotHM_Ef: " + solutionState.getFinalCost().getTotalHeurModEf() +
							" FinalCost: " + solutionState.getFinalCost();
				logger.info("Best found!!!");
			}
			if (solutionState.getFinalCost().doesPerformBetterThan(prevCost)) {
				logger.info("Solution is improved!!!");
			}
			prevCost = solutionState.getFinalCost();

			/*
			 * DH HEURISTIC SEARCH ORDER
			 */
//			OneDimIndexInt<Pair> pairIndexByLegNdx = this.generateSolutionPairIndex(solution, i, pairIndexByItrNrLegNdx);
//			this.checkAndUpdateTheOrder(pairIndexByLegNdx, numOfLegCoverings, reOrderedLegs);

			/*
			 * RANDOM ORDER
			 */
//			for (int j = 0; j < reOrderedLegs.size(); j++) {
//				int fNdx = random.nextInt(reOrderedLegs.size());
//				int sNdx = random.nextInt(reOrderedLegs.size());
//				while (fNdx == sNdx)
//					sNdx = random.nextInt(reOrderedLegs.size());
//				Leg hL = reOrderedLegs.get(sNdx);
//				reOrderedLegs.set(sNdx, reOrderedLegs.get(fNdx));
//				reOrderedLegs.set(fNdx, hL);
//			}

			/*
			 * DH CUMULATIVE ORDER
			 */
//			Collections.sort(reOrderedLegs, new Comparator<Leg>() {
//				@Override
//				public int compare(Leg o1, Leg o2) {
//					if (numOfLegCoveringsCumulative[o1.getNdx()] > numOfLegCoveringsCumulative[o2.getNdx()])
//						return -1;
//					else
//						if (numOfLegCoveringsCumulative[o1.getNdx()] < numOfLegCoveringsCumulative[o2.getNdx()])
//							return 1;
//						else
//							return 0;
//				}
//			});

			if ((numOfIterationsWOProgress >= HeurosAlgParameters.maxNumOfIterationsWOProgress)
                    || ((System.nanoTime() - optStartTime) >= HeurosAlgParameters.maxElapsedTimeInNanoSecs))
				break;
        }

		/*
		 * REPORT
		 */
//		for (int j = 0; j < this.legs.size(); j++) {
//			boolean log = false;
//			StringBuilder sb = new StringBuilder();
//			sb.append(this.legs.get(j))
//				.append(": ");
//			for (int i = 0; i < numOfLegCoveringsHistory.length; i++) {
//				if (this.legs.get(j).isCover()) {
//					if (numOfLegCoveringsHistory[i][j] > 1)
//						log = true;
//					sb.append(", ");
//					if (numOfLegCoveringsHistory[i][j] > 0)
//						sb.append(numOfLegCoveringsHistory[i][j] - 1);
//					else
//						sb.append(0);
//				} else {
//					if (numOfLegCoveringsHistory[i][j] > 0)
//						log = true;
//					sb.append(", ").append(numOfLegCoveringsHistory[i][j]);
//				}
//			}
//			if (log)
//				logger.info(sb.toString());
//		}			

		return bestStr;
	}
}
