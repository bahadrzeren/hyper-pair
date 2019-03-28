package org.heuros.pair.heuro;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.heuros.context.PairOptimizationContext;
import org.heuros.data.DutyLegOvernightConnNetwork;
import org.heuros.data.model.Pair;
import org.heuros.pair.conf.HeurosGaParameters;
import org.heuros.pair.heuro.state.SolutionState;
import org.heuros.pair.sp.PairingGenerator;

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
	private PairingGenerator pairingGenerator = null;

	private SolutionState solutionState = null;

	public HeuroOptimizer(PairOptimizationContext pairOptimizationContext,
							DutyLegOvernightConnNetwork pricingNetwork,
							PairingGenerator pairingGenerator) {
		this.pairOptimizationContext = pairOptimizationContext;
//		this.legs = pairOptimizationContext.getLegRepository().getModels();
//		this.duties = pairOptimizationContext.getDutyRepository().getModels();
//		this.dutyIndexByLegNdx = pairOptimizationContext.getDutyIndexByLegNdx();
		this.pricingNetwork = pricingNetwork;
		this.pairingGenerator = pairingGenerator;
	}

	public List<Pair> doMinimize() throws InterruptedException, ExecutionException {
		logger.info("Optimization process is started!");

		double bestCost = Double.MAX_VALUE;
		List<Pair> bestSolution = null;

		int numOfIterationsWOProgress = 0;
		long optStartTime = System.nanoTime();

		this.solutionState = new SolutionState(this.pairOptimizationContext,
												this.pricingNetwork);

		for (int i = 0; i < HeurosGaParameters.maxNumOfIterations; i++) {

			List<Pair> solution = new ArrayList<Pair>();

			this.solutionState.initializeForNewIteration();

			SolutionGenerator solGen = new SolutionGenerator(this.pairOptimizationContext, this.pairingGenerator);
			int uncoveredLegs = solGen.generateSolution(solution, this.solutionState);

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

			double cost = solutionState.finalizeIteration(i + 1, solution, uncoveredLegs);

			/*
			 * Improvement test.
			 */
			if (cost < bestCost) {
				bestCost = cost;
				bestSolution = solution;
			}

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

			if ((numOfIterationsWOProgress >= HeurosGaParameters.maxNumOfIterationsWOProgress)
                    || ((System.nanoTime() - optStartTime) >= HeurosGaParameters.maxElapsedTimeInNanoSecs))
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

		return bestSolution;
	}
}
