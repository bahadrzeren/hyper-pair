package org.heuros.pair.heuro;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.heuros.core.data.ndx.OneDimIndexInt;
import org.heuros.data.model.AirportView;
import org.heuros.data.model.Duty;
import org.heuros.data.model.DutyView;
import org.heuros.data.model.Leg;
import org.heuros.data.model.LegView;
import org.heuros.data.model.Pair;
import org.heuros.data.repo.DutyRepository;
import org.heuros.data.repo.LegRepository;
import org.heuros.pair.conf.HeurosGaParameters;
import org.heuros.pair.sp.PairingGenerator;

public class HeuroOptimizer {

	private static Logger logger = Logger.getLogger(HeuroOptimizer.class);

	private int numOfHeuristics = 3;
	public int getNumOfHeuristics() {
		return numOfHeuristics;
	}

	/*
	 * TODO Single base assumption!!!
	 */
	private int hbNdx = 0;

	private List<Leg> legs = null;
	private List<Duty> duties = null;
	private OneDimIndexInt<Duty> dutyIndexByLegNdx = null;
	private PairingGenerator pairingGenerator = null;

	public HeuroOptimizer setLegRepository(LegRepository legRepository) {
		this.legs = legRepository.getModels();
		return this;
	}

	public HeuroOptimizer setDutyRepository(DutyRepository dutyRepository) {
		this.duties = dutyRepository.getModels();
		return this;
	}

	public HeuroOptimizer setDutyIndexByLegNdx(OneDimIndexInt<Duty> dutyIndexByLegNdx) {
		this.dutyIndexByLegNdx = dutyIndexByLegNdx;
		return this;
	}

	public HeuroOptimizer setPairingGenerator(PairingGenerator pairingGenerator) {
		this.pairingGenerator = pairingGenerator;
		return this;
	}

	private OneDimIndexInt<Pair> generateSolutionPairIndex(List<Pair> solution//	, int itrNr, TwoDimIndexIntXInt<Pair> pairIndexByItrNrLegNdx
															) {
		OneDimIndexInt<Pair> pairIndexByLegNdx = new OneDimIndexInt<Pair>(new Pair[this.legs.size()][0]);
		for (int i = 0; i < solution.size(); i++) {
			Pair pair = solution.get(i);
			for (int j = 0; j < pair.getDuties().size(); j++) {
				DutyView duty = pair.getDuties().get(j);
				for (int k = 0; k < duty.getLegs().size(); k++) {
					LegView leg = duty.getLegs().get(k);
					pairIndexByLegNdx.add(leg.getNdx(), pair);
//					pairIndexByItrNrLegNdx.add(itrNr, leg.getNdx(), pair);
				}
			}
		}
		return pairIndexByLegNdx;
	}

	private Leg getLegWithMaxCoverage(int[] numOfLegCoverings, boolean[] rooted) {
		int max = 0;
		int res = -1;
		for (int i = 0; i < numOfLegCoverings.length; i++) {
			if (!rooted[i]) {

				if (this.legs.get(i).isCover()) {
					if (numOfLegCoverings[i] - 1 > max) {
						res = i;
						max = numOfLegCoverings[i] - 1;
					}
				} else
					if (numOfLegCoverings[i] > max) {
						res = i;
						max = numOfLegCoverings[i];
					}

			}
		}
		if (res >= 0) {
			return this.legs.get(res);
		}
		return null;
	}

	private int enhanceReOrderedLegs(int startingNdx, Pair p, boolean[] ordered, boolean[] rooted, int[] numOfLegCoverings, LinkedList<Leg> rootLegList, List<Leg> reOrderedLegs) {
		int res = startingNdx;
		for (int i = 0; i < p.getNumOfDuties(); i++) {
			Duty duty = p.getDuties().get(i);
			for (int j = 0; j < duty.getNumOfLegs(); j++) {
				Leg leg = duty.getLegs().get(j);
				if (!ordered[leg.getNdx()]) {
					reOrderedLegs.set(res, leg);
					res++;
					ordered[leg.getNdx()] = true;
				}
				if (!rooted[leg.getNdx()]) {
					if (leg.isCover()) {
						if (numOfLegCoverings[i] > 1) {
							rootLegList.add(leg);
							rooted[leg.getNdx()] = true;
						}
					} else
						if (numOfLegCoverings[i] > 0) {
							rootLegList.add(leg);
							rooted[leg.getNdx()] = true;
						}

				}
			}
		}
		return res;
	}

	private void checkAndUpdateTheOrder(OneDimIndexInt<Pair> pairIndexByLegNdx, int[] numOfLegCoverings, List<Leg> reOrderedLegs) {
		logger.info("checkAndUpdateTheOrder");

		boolean[] rooted = new boolean[this.legs.size()];
		boolean[] ordered = new boolean[this.legs.size()];
		/*
		 * TODO: Deadheads from other fleets needs to be considered!
		 */
		int reOrderNdx = 0;

		Leg leg = this.getLegWithMaxCoverage(numOfLegCoverings, rooted);
		if (leg.isCover())
			logger.info("Max numOfDh: " + (numOfLegCoverings[leg.getNdx()] - 1) + " - " + leg);
		else
			logger.info("Max numOfDh: " + numOfLegCoverings[leg.getNdx()] + " - " + leg);

		LinkedList<Leg> rootLegList = new LinkedList<Leg>();

		while (leg != null) {
			rootLegList.add(leg);
			rooted[leg.getNdx()] = true;

			while (rootLegList.size() > 0) {

				leg = rootLegList.removeFirst();
				if (!ordered[leg.getNdx()]) {
					reOrderedLegs.set(reOrderNdx, leg);
					reOrderNdx++;
					ordered[leg.getNdx()] = true;
				}

				Pair[] pairs = pairIndexByLegNdx.getArray(leg.getNdx());
				for (Pair pair : pairs) {
					reOrderNdx = this.enhanceReOrderedLegs(reOrderNdx, pair, ordered, rooted, numOfLegCoverings, rootLegList, reOrderedLegs);
				}

//				logger.info("checkAndUpdateTheOrder: " + leg.toString() + " #" + reOrderNdx);

			}
			leg = this.getLegWithMaxCoverage(numOfLegCoverings, rooted);
		}

		logger.info("checkAndUpdateTheOrder: #" + reOrderNdx);

		for (int i = 0; i < ordered.length; i++) {
			if (!ordered[i]) {
				reOrderedLegs.set(reOrderNdx, this.legs.get(i));
				reOrderNdx++;
			}
		}

		logger.info("checkAndUpdateTheOrder: #" + reOrderNdx);
	}

	private Random random = new Random();

	public List<Pair> doMinimize() {
		logger.info("Optimization process is started!");

		double bestCost = Double.MAX_VALUE;
		List<Pair> bestSolution = null;
		LegState[] bestLegParams = null;
		DutyState[] bestDutyParams = null;

		List<Leg> reOrderedLegs = new ArrayList<Leg>();
		for (int i = 0; i < this.legs.size(); i++)
			reOrderedLegs.add(this.legs.get(i));

		int numOfIterationsWOProgress = 0;
		long optStartTime = System.nanoTime();

		/*
		 * CUMULATIVES
		 */
//		TwoDimIndexIntXInt<Pair> pairIndexByItrNrLegNdx = new TwoDimIndexIntXInt<Pair>(new Pair[HeurosGaParameters.maxNumOfIterations][this.legs.size()][0]);
//		int[] numOfLegCoveringsCumulative = new int[this.legs.size()];
//		int[][] numOfLegCoveringsHistory = new int[HeurosGaParameters.maxNumOfIterations][this.legs.size()];

		for (int i = 0; i < HeurosGaParameters.maxNumOfIterations; i++) {

			List<Pair> solution = new ArrayList<Pair>();
			LegState[] legParams = new LegState[this.legs.size()];
			for (int j = 0; j < legParams.length; j++) {
				legParams[j] = new LegState();
				legParams[j].numOfIncludingDuties = this.legs.get(j).getNumOfIncludingDuties();
				legParams[j].numOfIncludingDutiesWoDh = this.legs.get(j).getNumOfIncludingDutiesWoDh();
			}
			DutyState[] dutyParams = new DutyState[this.duties.size()];
			for (int j = 0; j < dutyParams.length; j++) {
				dutyParams[j] = new DutyState();
				dutyParams[j].minNumOfAlternativeDuties = this.duties.get(j).getMinNumOfAlternativeDuties();
				dutyParams[j].minNumOfAlternativeDutiesWoDh = this.duties.get(j).getMinNumOfAlternativeDutiesWoDh();
				dutyParams[j].maxNumOfAlternativeDuties = this.duties.get(j).getMaxNumOfAlternativeDuties();
				dutyParams[j].maxNumOfAlternativeDutiesWoDh = this.duties.get(j).getMaxNumOfAlternativeDutiesWoDh();
				dutyParams[j].totalNumOfAlternativeDuties = this.duties.get(j).getTotalNumOfAlternativeDuties();
				dutyParams[j].totalNumOfAlternativeDutiesWoDh = this.duties.get(j).getTotalNumOfAlternativeDutiesWoDh();
				dutyParams[j].isCritical = this.duties.get(j).getCriticalLeg() != null;
			}

			SolutionGenerator solGen = new SolutionGenerator(this.legs,
																this.duties,
																this.dutyIndexByLegNdx,
																this.pairingGenerator);
			double cost = solGen.generateSolution(reOrderedLegs,
													solution,
													legParams,
													dutyParams);

//			for (int j = 0; j < numOfLegCoverings.length; j++) {
//				numOfLegCoveringsCumulative[j] += numOfLegCoverings[j] * (i + 1);
//				numOfLegCoveringsHistory[i][j] = numOfLegCoverings[j];
//			}

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

			/*
			 * Improvement test.
			 */
			if (cost < bestCost) {
				bestCost = cost;
				bestSolution = solution;
				bestLegParams = legParams;
				bestDutyParams = dutyParams;
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
