package org.heuros.pair.heuro;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.heuros.core.data.ndx.OneDimIndexInt;
import org.heuros.core.data.ndx.TwoDimIndexIntXInt;
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

//	private LegRepository legRepository = null;
//	private DutyRepository dutyRepository = null;

	private OneDimIndexInt<Duty> dutyIndexByLegNdx = null;

	private PairingGenerator pairingGenerator = null;

	private List<Leg> legs = null;
	private List<Duty> duties = null;

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

//	private int getNextLegNdxToCover(int prevReOrderedLegNdx, List<Leg> reOrderedLegs, int[] numOfLegCoverings) {
//		for (int i = prevReOrderedLegNdx + 1; i < reOrderedLegs.size(); i++) {
//			if (reOrderedLegs.get(i).isCover()
//					&& reOrderedLegs.get(i).hasPair(hbNdx)
//					&& (numOfLegCoverings[reOrderedLegs.get(i).getNdx()] == 0))
//				return i;
//		}
//		return Integer.MAX_VALUE;
//	}
	private int getNextLegNdxToCover(LegParam[] lps) {
		int res = -1;
		int minNumOfDutiesWoDh = Integer.MAX_VALUE;
		for (int i = 0; i < this.legs.size(); i++) {
			if (this.legs.get(i).isCover()
					&& this.legs.get(i).hasPair(hbNdx)
					&& (lps[i].numOfCoverings == 0)) {
				if (minNumOfDutiesWoDh > lps[i].numOfDutiesWoDh) {
					minNumOfDutiesWoDh = lps[i].numOfDutiesWoDh;
					res = i;
				}
			}
		}
		return res;
	}

	private void udpateStateVectors(Pair p,
									LegParam[] lps,
									DutyParam[] dps) {
		for (int i = 0; i < p.getNumOfDuties(); i++) {
			Duty duty = p.getDuties().get(i);
			for (int j = 0; j < duty.getNumOfLegs(); j++) {
				Leg leg = duty.getLegs().get(j);
				lps[leg.getNdx()].numOfCoverings++;
				Duty[] dutiesOfLeg = this.dutyIndexByLegNdx.getArray(leg.getNdx());
				for (int di = 0; di < dutiesOfLeg.length; di++) {
					Duty dutyOfLeg = dutiesOfLeg[di];
					dps[dutyOfLeg.getNdx()].numOfCoverings++;
					if (lps[leg.getNdx()].numOfCoverings == 1)
						dps[dutyOfLeg.getNdx()].numOfDistinctCoverings++;
					dps[dutyOfLeg.getNdx()].blockTimeOfCoverings += leg.getBlockTimeInMins();

for (int li = 0; li < dutyOfLeg.getLegs().size(); li++) {
lps[dutyOfLeg.getLegs().get(li).getNdx()].numOfDutiesWoDh--;
}
				}
			}
		}
	}

	private double generateSolution(List<Leg> reOrderedLegs,
									List<Pair> solution,
									LegParam[] legParams,
									DutyParam[] dutyParams) {

		logger.info("Solution generation process is started!");

		int uncoveredLegs = 0;

		int reOrderedLegNdx = -1;

for (int i = 0; i < this.duties.size(); i++) {
	Duty duty = this.duties.get(i);
	if (duty.getNumOfLegsPassive() == 0) {
		for (int j = 0; j < duty.getNumOfLegs(); j++) {
			LegParam lp = legParams[duty.getLegs().get(j).getNdx()];
			if (lp == null) {
				legParams[j] = new LegParam();
				lp = legParams[j];
			}
			lp.numOfDutiesWoDh++; 
		}
	}
}

		while (true) {
//			reOrderedLegNdx = this.getNextLegNdxToCover(reOrderedLegNdx, reOrderedLegs, numOfLegCoverings);
//			if (reOrderedLegNdx == Integer.MAX_VALUE)
//				break;
//			Leg legToCover = reOrderedLegs.get(reOrderedLegNdx);

reOrderedLegNdx = this.getNextLegNdxToCover(legParams);
if (reOrderedLegNdx < 0)
	break;
Leg legToCover = this.legs.get(reOrderedLegNdx);

			int heuristicNo = 1;

			Pair p = null;
			try {
				p = this.pairingGenerator.generatePairing(legToCover, 
															heuristicNo,
															dutyParams);
			} catch (CloneNotSupportedException ex) {
				HeuroOptimizer.logger.error(ex);
			}

			if (p != null) {
				this.udpateStateVectors(p, legParams, dutyParams);
				solution.add(p);
			} else {
				HeuroOptimizer.logger.error("Pairing could not be found for " + legToCover);
				uncoveredLegs++;
			}
		}

		double fitness = 0.0;
		int numOfDuties = 0;
		int numOfPairDays = 0;
		int numOfPairs = 0;

		for (int i = 0; i < solution.size(); i++) {
			fitness += getPairCost(2, solution.get(i));
			numOfDuties += solution.get(i).getNumOfDuties();
			numOfPairDays += solution.get(i).getNumOfDaysTouched();
			numOfPairs++;
		}

		int numOfDeadheads = 0;

		for (int i = 0; i < this.legs.size(); i++) {
			if (this.legs.get(i).isCover()) {
				if (legParams[i].numOfCoverings > 1) {
					fitness += (2.0 * (legParams[i].numOfCoverings - 1) * (this.legs.get(i).getBlockTimeInMins() / 60.0) * dhPenalty);
//					fitness += (numOfLegCoverings[i] - 1) * 100;
					numOfDeadheads += (legParams[i].numOfCoverings - 1);
				}
			} else {
				if (legParams[i].numOfCoverings > 0) {
					fitness += (2.0 * legParams[i].numOfCoverings * (this.legs.get(i).getBlockTimeInMins() / 60.0) * dhPenalty);
//					fitness += numOfLegCoverings[i] * 100;
					numOfDeadheads += legParams[i].numOfCoverings;
				}
			}
		}

		logger.info("numOfPairs: " + numOfPairs + 
					", numOfDuties: " + numOfDuties +
					", numOfPairDays:" + numOfPairDays +
					", uncoveredLegs: " + uncoveredLegs + 
					", numOfDeadheads: " + numOfDeadheads + 
					", fitness: " + fitness);

		return fitness;
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
		LegParam[] bestLegParams = null;
		DutyParam[] bestDutyParams = null;

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
			LegParam[] legParams = new LegParam[this.legs.size()];
			for (int j = 0; j < legParams.length; j++)
				legParams[j] = new LegParam();
			DutyParam[] dutyParams = new DutyParam[this.duties.size()];
			for (int j = 0; j < dutyParams.length; j++)
				dutyParams[j] = new DutyParam();

			double cost = this.generateSolution(reOrderedLegs,
												solution,
												legParams,
												dutyParams);

//			for (int j = 0; j < numOfLegCoverings.length; j++) {
//				numOfLegCoveringsCumulative[j] += numOfLegCoverings[j] * (i + 1);
//				numOfLegCoveringsHistory[i][j] = numOfLegCoverings[j];
//			}

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

	/*
	 * COST calculations.
	 */

	private int hotelTransportTime = 30;

	private int hotelCostDomPerCrewPerDay = 10000;	//	1000;
	private int hotelCostIntPerCrewPerDay = 10000;	//	1000;

	private int hotelTransferCost = 40;

	private int getHotelCostPerCrew(AirportView ap, int gmtDiff, LocalDateTime debriefTime, LocalDateTime briefTime) {
		if (briefTime == null)
			return 0;

		LocalDateTime hotelIn = debriefTime.plusMinutes(hotelTransportTime + gmtDiff);
		LocalDateTime hotelOut = briefTime.plusMinutes(- hotelTransportTime + gmtDiff);

		LocalDateTime hotelIn00 = hotelIn.truncatedTo(ChronoUnit.DAYS);
		LocalDateTime hotelOut00 = hotelOut.minusSeconds(1).truncatedTo(ChronoUnit.DAYS);
		int numOfLayNights = (int) ChronoUnit.DAYS.between(hotelIn00, hotelOut00);

		if ((ChronoUnit.DAYS.between(hotelIn, hotelOut) < 1.0)
				|| (numOfLayNights == 0)) {

        	if (ap.isDomestic())
        		return hotelCostDomPerCrewPerDay;
        	else
        		return hotelCostIntPerCrewPerDay;
        }
        else
        	if (ap.isDomestic())
        		return hotelCostDomPerCrewPerDay * numOfLayNights;
        	else
        		return hotelCostIntPerCrewPerDay * numOfLayNights;
    }

	private double dutyDayPenalty = 60000.0;	//	20000.0;
	private double dutyHourPenalty = 1050.0;	//	0.0;
	private double dhPenalty = 500000.0;	//	10000.0;
	private double acChangePenalty = 250.0;	//	20.0;
	private double squareRestPenalty = 2.0;
	private int longRestLimit = 14 * 60;
	private double longRestPenalty = 400;	//	700;
	private double longConnPenalty = 100;	//	40;
	private double augmentionHourPenalty = 100000.0;	//	2000;
	private double specialDhPenalty = 10000.0;
	private double augmentionDayPenalty = 0.0;	//	4000.0;

	private double getDutyCost(int cc, DutyView d, boolean hbDep, LocalDateTime nt) {
		LocalDateTime sh = d.getBriefTime(hbNdx);
		LocalDateTime eh = d.getDebriefTime(hbNdx);

		if (nt == null)
			eh = d.getDebriefDayEnding(hbNdx);
		else
			eh = nt;
		if (hbDep)
			sh = d.getBriefDayBeginning(hbNdx);

		double dutyDurationInMins = ChronoUnit.MINUTES.between(sh, eh);
		double dCost = 0.0;

		dCost += cc * dutyDurationInMins * dutyDayPenalty / (24.0 * 60.0);

		if (hbDep)
			dCost += cc * d.getDutyDurationInMins(hbNdx) * dutyHourPenalty / 60.0;
		else
			dCost += cc * d.getDutyDurationInMins(hbNdx) * dutyHourPenalty / 60.0;

		dCost += acChangePenalty * d.getNumOfAcChanges();

		if (nt != null) {
			double idleTime = ChronoUnit.MINUTES.between(d.getDebriefTime(hbNdx), nt);

			dCost += cc * getHotelCostPerCrew(d.getLastArrAirport(), d.getLastLeg().getArrOffset(), d.getDebriefTime(hbNdx), nt);

			dCost += 2.0 * hotelTransferCost;

			if (squareRestPenalty * idleTime * idleTime / 10000.0 > (squareRestPenalty * cc * dutyDayPenalty / 20.0))
				dCost += squareRestPenalty * idleTime * idleTime / 10000.0;
			else
				dCost += (squareRestPenalty * cc * dutyDayPenalty) / 20.0;

			if (idleTime > longRestLimit) {
				dCost += (idleTime - longRestLimit) * longRestPenalty / 60.0;
			}

		} else
			if (hbDep) {
				dCost += cc * dutyDayPenalty / 20.0;
			}

		dCost += d.getLongConnDiff() * longConnPenalty / 60.0;

		if (hbDep) {
			if (d.getAugmented(hbNdx) > 0) {
				dCost += d.getDutyDurationInMins(hbNdx) * augmentionHourPenalty / 60.0;
				dCost += augmentionDayPenalty;
			}
		} else {
			if (d.getAugmented(hbNdx) > 0) {
				dCost += d.getDutyDurationInMins(hbNdx) * augmentionHourPenalty / 60.0;
				dCost += augmentionDayPenalty;
			}
		}

		dCost += d.getNumOfSpecialFlights() * specialDhPenalty;

//		if (includeDh)
//			dCost += cc * (d.getBlockTimeInMins() / 60.0) * dhPenalty;
//		else
//			dCost -= cc * (d.getBlockTimeInMins() / 60.0) * dhPenalty;

		return dCost;
	}

	private double getPairCost(int cc, Pair p) {
		DutyView pd = p.getFirstDuty();
		if (p.getNumOfDuties() == 1)
			return getDutyCost(cc, pd, true, null);
		else {
			DutyView nd = null;
			double pCost = 0.0;
			for (int i = 1; i < p.getNumOfDuties(); i++) {
				nd = p.getDuties().get(i);
				pCost += getDutyCost(cc, pd, i == 1, nd.getBriefTime(hbNdx));
				pd = nd;
			}
			pCost += getDutyCost(cc, nd, false, null);
			return pCost;
		}
	}
}
