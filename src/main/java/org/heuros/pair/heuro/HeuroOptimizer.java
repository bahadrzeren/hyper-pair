package org.heuros.pair.heuro;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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

	private int getNextLegNdxToCover(int prevReOrderedLegNdx, List<Leg> reOrderedLegs, int[] numOfLegCoverings) {
		for (int i = prevReOrderedLegNdx + 1; i < reOrderedLegs.size(); i++) {
			if (reOrderedLegs.get(i).isCover()
					&& reOrderedLegs.get(i).hasPair(hbNdx)
					&& (numOfLegCoverings[reOrderedLegs.get(i).getNdx()] == 0))
				return i;
		}
		return Integer.MAX_VALUE;
	}

	private void udpateStateVectors(Pair p,
									int[] numOfLegCoverings,
									int[] numOfCoveringsInDuties,
									int[] numOfDistinctCoveringsInDuties,
									int[] blockTimeOfCoveringsInDuties) {
		for (int i = 0; i < p.getNumOfDuties(); i++) {
			Duty duty = p.getDuties().get(i);
			for (int j = 0; j < duty.getNumOfLegs(); j++) {
				Leg leg = duty.getLegs().get(j);
				numOfLegCoverings[leg.getNdx()]++;
				Duty[] dutiesOfLeg = this.dutyIndexByLegNdx.getArray(leg.getNdx());
				for (int di = 0; di < dutiesOfLeg.length; di++) {
					Duty dutyOfLeg = dutiesOfLeg[di];
					numOfCoveringsInDuties[dutyOfLeg.getNdx()]++;
					if (numOfLegCoverings[leg.getNdx()] == 1)
						numOfDistinctCoveringsInDuties[dutyOfLeg.getNdx()]++;
					blockTimeOfCoveringsInDuties[dutyOfLeg.getNdx()] += leg.getBlockTimeInMins();
				}
			}
		}
	}

	private List<Pair> generateSolution(List<Leg> reOrderedLegs) {

		logger.info("Initialization process is started!");

		List<Pair> solution = new ArrayList<Pair>();

		int uncoveredLegs = 0;

		int reOrderedLegNdx = -1;
		int[] numOfLegCoverings = new int[this.legs.size()];
		int[] numOfCoveringsInDuties = new int[this.duties.size()];
		int[] numOfDistinctCoveringsInDuties = new int[this.duties.size()];
		int[] blockTimeOfCoveringsInDuties = new int[this.duties.size()];
		int[] dutyPriorityCumulative = new int[this.duties.size()];

		while (true) {
			reOrderedLegNdx = this.getNextLegNdxToCover(reOrderedLegNdx, reOrderedLegs, numOfLegCoverings);
			if (reOrderedLegNdx == Integer.MAX_VALUE)
				break;

			Leg legToCover = reOrderedLegs.get(reOrderedLegNdx);

			int heuristicNo = 1;

			Pair p = null;
			try {
				p = this.pairingGenerator.generatePairing(legToCover, 
															heuristicNo, 
															numOfCoveringsInDuties,
															numOfDistinctCoveringsInDuties,
															blockTimeOfCoveringsInDuties,
															dutyPriorityCumulative);
			} catch (CloneNotSupportedException ex) {
				HeuroOptimizer.logger.error(ex);
			}

			if (p != null) {
				this.udpateStateVectors(p, numOfLegCoverings, numOfCoveringsInDuties, numOfDistinctCoveringsInDuties, blockTimeOfCoveringsInDuties);
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
//			fitness += getPairCost(2, solution.get(i));
			numOfDuties += solution.get(i).getNumOfDuties();
			numOfPairDays += solution.get(i).getNumOfDaysTouched();
			numOfPairs++;
		}

		int numOfDeadheads = 0;

		for (int i = 0; i < this.legs.size(); i++) {
			if (this.legs.get(i).isCover()) {
				if (numOfLegCoverings[i] > 1) {
//					fitness += (2.0 * (numOfLegCoverings[i] - 1) * (this.legs.get(i).getBlockTimeInMins() / 60.0) * dhPenalty);
					fitness += (numOfLegCoverings[i] - 1) * 100;
					numOfDeadheads += (numOfLegCoverings[i] - 1);
				}
			} else {
				if (numOfLegCoverings[i] > 0) {
//					fitness += (2.0 * numOfLegCoverings[i] * (this.legs.get(i).getBlockTimeInMins() / 60.0) * dhPenalty);
					fitness += numOfLegCoverings[i] * 100;
					numOfDeadheads += numOfLegCoverings[i];
				}
			}
		}

		logger.info("numOfPairs: " + numOfPairs + 
					", numOfDuties: " + numOfDuties +
					", numOfPairDays:" + numOfPairDays +
					", uncoveredLegs: " + uncoveredLegs + 
					", numOfDeadheads: " + numOfDeadheads + 
					", fitness: " + fitness);
		logger.info("Initialization process is ended!");
		return solution;
	}

	private OneDimIndexInt<Pair> generateSolutionPairIndex(List<Pair> solution) {
		OneDimIndexInt<Pair> pairIndexByLegNdx = new OneDimIndexInt<Pair>(new Pair[this.legs.size()][0]);
		for (int i = 0; i < solution.size(); i++) {
			Pair pair = solution.get(i);
			for (int j = 0; j < pair.getDuties().size(); j++) {
				DutyView duty = pair.getDuties().get(j);
				for (int k = 0; k < duty.getLegs().size(); k++) {
					LegView leg = duty.getLegs().get(k);
					pairIndexByLegNdx.add(leg.getNdx(), pair);
				}
			}
		}
		return pairIndexByLegNdx;
	}

	public List<Pair> doMinimize() {
		logger.info("Optimization process is started!");

		List<Leg> reOrderedLegs = new ArrayList<Leg>();
		for (int i = 0; i < this.legs.size(); i++)
			reOrderedLegs.add(this.legs.get(i));

		int numOfIterationsWOProgress = 0;
		long optStartTime = System.nanoTime();

		for (int i = 1; i <= HeurosGaParameters.maxNumOfIterations; i++) {

			List<Pair> solution = this.generateSolution(reOrderedLegs);
			OneDimIndexInt<Pair> pairIndexByLegNdx = this.generateSolutionPairIndex(solution);

			int initialLegNdx = this.getFirstLegNdxToCover();
			Leg initialLeg = this.reOrderedLegs.get(initialLegNdx);

			LinkedList<LegView> legs = new LinkedList<LegView>();
			legs.add(initialLeg);

			boolean[] legAdded = new boolean[this.legs.size()];

			int[] numOfLegCoverings = new int[this.legs.size()];
			int[] numOfCoveringsInDuties = new int[this.duties.size()];
			int[] numOfDistinctCoveringsInDuties = new int[this.duties.size()];
			int[] blockTimeOfCoveringsInDuties = new int[this.duties.size()];
			int[] dutyPriorityCumulative = new int[this.duties.size()];

			solution = new ArrayList<Pair>();

			while (legs.size() > 0) {

				LegView legToCover = legs.removeFirst();
				while (numOfLegCoverings[legToCover.getNdx()] > 0) {
					legToCover = legs.removeFirst();
				}

				Pair[] pairsFromPrevSol = pairIndexByLegNdx.getArray(legToCover.getNdx());
				for (Pair pair: pairsFromPrevSol) {
					for (int j = 0; j < pair.getDuties().size(); j++) {
						DutyView duty = pair.getDuties().get(j);
						for (int k = 0; k < duty.getLegs().size(); k++) {
							LegView leg = duty.getLegs().get(k);
							if ((leg.getNdx() != legToCover.getNdx())
									&& (numOfLegCoverings[leg.getNdx()] == 0)
									&& (!legAdded[leg.getNdx()])) {
								legs.add(leg);
								legAdded[leg.getNdx()] = true;
							}
						}
					}
				}

				Pair p = null;
				try {
					p = this.pairingGenerator.generatePairing(legToCover, 
																1, 
																numOfCoveringsInDuties,
																numOfDistinctCoveringsInDuties,
																blockTimeOfCoveringsInDuties,
																dutyPriorityCumulative);
				} catch (CloneNotSupportedException ex) {
					logger.error(ex);
				}

			}

			if ((numOfIterationsWOProgress >= HeurosGaParameters.maxNumOfIterationsWOProgress)
                    || ((System.nanoTime() - optStartTime) >= HeurosGaParameters.maxElapsedTimeInNanoSecs))
                break;
        }

		return solution;
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
