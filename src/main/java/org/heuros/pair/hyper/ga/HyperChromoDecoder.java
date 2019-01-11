package org.heuros.pair.hyper.ga;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.heuros.core.data.ndx.OneDimIndexInt;
import org.heuros.core.ga.chromosome.Chromosome;
import org.heuros.core.ga.decoder.Decoder;
import org.heuros.data.model.Duty;
import org.heuros.data.model.DutyView;
import org.heuros.data.model.Leg;
import org.heuros.data.model.LegView;
import org.heuros.data.model.Pair;
import org.heuros.data.repo.DutyRepository;
import org.heuros.data.repo.LegRepository;
import org.heuros.pair.sp.PairingGenerator;

public class HyperChromoDecoder implements Decoder<Integer, Pair> {

	private static Logger logger = Logger.getLogger(HyperChromoDecoder.class);

	private int numOfHeuristics = 3;
	public int getNumOfHeuristics() {
		return numOfHeuristics;
	}

	/*
	 * TODO Single base assumption!!!
	 */
	private int hbNdx = 0;

//	private LegRepository legRepository = null;
	private DutyRepository dutyRepository = null;

	private OneDimIndexInt<Duty> dutyIndexByLegNdx = null;

	private PairingGenerator pairingGenerator = null;

	private List<Leg> legs = null;
	private List<Leg> reOrderedLegs = null;

	public HyperChromoDecoder setLegRepository(LegRepository legRepository) {
//		this.legRepository = legRepository;
		this.legs = legRepository.getModels();
		this.reOrderedLegs = new ArrayList<Leg>();
		for (int i = 0; i < this.legs.size(); i++) {
			this.reOrderedLegs.add(this.legs.get(i));
		}
		return this;
	}

	public HyperChromoDecoder setDutyRepository(DutyRepository dutyRepository) {
		this.dutyRepository = dutyRepository;
		return this;
	}

	public HyperChromoDecoder setDutyIndexByLegNdx(OneDimIndexInt<Duty> dutyIndexByLegNdx) {
		this.dutyIndexByLegNdx = dutyIndexByLegNdx;
		return this;
	}

	public HyperChromoDecoder setPairingGenerator(PairingGenerator pairingGenerator) {
		this.pairingGenerator = pairingGenerator;
		return this;
	}

	private int getNextLegNdxToCover(int prevReOrderedLegNdx, int[] numOfLegCoverings) {
		for (int i = prevReOrderedLegNdx + 1; i < this.reOrderedLegs.size(); i++) {
			if (this.reOrderedLegs.get(i).isCover()
					&& (this.reOrderedLegs.get(i).hasPair(hbNdx))
					&& (numOfLegCoverings[this.reOrderedLegs.get(i).getNdx()] == 0))
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
			DutyView duty = p.getDuties().get(i);
			for (int j = 0; j < duty.getNumOfLegs(); j++) {
				LegView leg = duty.getLegs().get(j);
				numOfLegCoverings[leg.getNdx()]++;
				DutyView[] dutiesOfLeg = this.dutyIndexByLegNdx.getArray(leg.getNdx());
				for (int di = 0; di < dutiesOfLeg.length; di++) {
					DutyView dutyOfLeg = dutiesOfLeg[di];
					numOfCoveringsInDuties[dutyOfLeg.getNdx()]++;
					if (numOfLegCoverings[leg.getNdx()] == 1)
						numOfDistinctCoveringsInDuties[dutyOfLeg.getNdx()]++;
					blockTimeOfCoveringsInDuties[dutyOfLeg.getNdx()] += leg.getBlockTimeInMins();
				}
			}
		}
	}

	@Override
	public List<Pair> decode(Chromosome<Integer> chromosome) {

		logger.info("Decoding process is started!");

		List<Pair> solution = new ArrayList<Pair>();

		int geneNdx = 0;
		int uncoveredLegs = 0;

		int reOrderedLegNdx = -1;
		int[] numOfLegCoverings = new int[this.legs.size()];
		int[] numOfCoveringsInDuties = new int[this.dutyRepository.getModels().size()];
		int[] numOfDistinctCoveringsInDuties = new int[this.dutyRepository.getModels().size()];
		int[] blockTimeOfCoveringsInDuties = new int[this.dutyRepository.getModels().size()];
		int[] dutyPriorityCumulative = new int[this.dutyRepository.getModels().size()];

		while (true) {
			reOrderedLegNdx = this.getNextLegNdxToCover(reOrderedLegNdx, numOfLegCoverings);
			if (reOrderedLegNdx == Integer.MAX_VALUE)
				break;

			Leg legToCover = this.reOrderedLegs.get(reOrderedLegNdx);

			int heuristicNo = chromosome.getGeneValue(geneNdx);
			if (heuristicNo == 0)
				if (!legToCover.hasHbDepArrDutyPair(this.hbNdx))
					heuristicNo = 1;

///*
// * TODO Remove the lines after test!
// */
//heuristicNo = 0;
//if (legToCover.hasHbDepDutyPair(this.hbNdx)
//		|| legToCover.hasHbArrDutyPair(this.hbNdx)
//		|| legToCover.hasNonHbDutyPair(this.hbNdx))
//heuristicNo = 2;

			Pair p = null;
			try {
				p = this.pairingGenerator.generatePairing(legToCover, 
															heuristicNo, 
															numOfCoveringsInDuties,
															numOfDistinctCoveringsInDuties,
															blockTimeOfCoveringsInDuties,
															dutyPriorityCumulative);
			} catch (CloneNotSupportedException ex) {
				HyperChromoDecoder.logger.error(ex);
			}

//if (legToCover.getNdx() == 1768)
//System.out.println(p);
			if (p != null) {
				this.udpateStateVectors(p, numOfLegCoverings, numOfCoveringsInDuties, numOfDistinctCoveringsInDuties, blockTimeOfCoveringsInDuties);
				solution.add(p);
				geneNdx++;
			} else {
				HyperChromoDecoder.logger.error("Pairing could not be found for " + legToCover);
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
		chromosome.setFitness(fitness + uncoveredLegs * 100000000);
		chromosome.setInfo("numOfPairs:" + numOfPairs + 
							", numOfDuties:" + numOfDuties + 
							", numOfPairDays:" + numOfPairDays + 
							", uncoveredLegs: " + uncoveredLegs + 
							", numOfDeadheads: " + numOfDeadheads + 
							", fitness: " + fitness);

		this.checkAndUpdateTheBest(chromosome.getFitness(), solution, numOfLegCoverings);

		logger.info("numOfPairs: " + numOfPairs + 
					", numOfDuties: " + numOfDuties + 
					", numOfPairDays:" + numOfPairDays +
					", uncoveredLegs: " + uncoveredLegs + 
					", numOfDeadheads: " + numOfDeadheads + 
					", fitness: " + fitness);
		logger.info(chromosome);
		logger.info("Decoding process is ended!");
		return solution;
	}

	private boolean thereIsImprovement = false;
	private double bestFitness = Integer.MAX_VALUE;
	private List<Pair> bestSolution = null;
	private int[] bestLegCoverings = null;

	private synchronized void checkAndUpdateTheBest(double newFitness, List<Pair> newSolution, int[] newLegCoverings) {
		if (newFitness < bestFitness) {
			thereIsImprovement = true;
			this.bestFitness = newFitness;
			this.bestSolution = newSolution;
			this.bestLegCoverings = newLegCoverings;
		}
	}

	private OneDimIndexInt<Pair> generateSolutionPairIndex() {
		OneDimIndexInt<Pair> pairIndexByLegNdx = new OneDimIndexInt<Pair>(new Pair[this.legs.size()][0]);
		for (int i = 0; i < bestSolution.size(); i++) {
			Pair pair = bestSolution.get(i);
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

	private Leg getNdxWithMaxValue(boolean[] legsConsidered) {
		int max = 0;
		int res = -1;
		for (int i = 0; i < bestLegCoverings.length; i++) {
			if ((!legsConsidered[i])
					&& (bestLegCoverings[i] > max)) {
				res = i;
				max = bestLegCoverings[i];
			}
		}
		if (res >= 0) {
			bestLegCoverings[res] = 0;
			return this.legs.get(res);
		}
		return null;
	}

	private int enhanceReOrderedLegs(int startingNdx, Pair p, boolean[] legsConsidered) {
		int res = startingNdx;
		for (int i = 0; i < p.getNumOfDuties(); i++) {
			DutyView duty = p.getDuties().get(i);
			for (int j = 0; j < duty.getNumOfLegs(); j++) {
				LegView leg = duty.getLegs().get(j);
				if (!legsConsidered[leg.getNdx()]) {
					reOrderedLegs.set(res, (Leg) leg);
					res++;
					legsConsidered[leg.getNdx()] = true;
				}
			}
		}
		return res;
	}

	public void checkAndUpdateTheOrder() {
		if (thereIsImprovement) {
			logger.info("checkAndUpdateTheOrder");

			thereIsImprovement = false;

			OneDimIndexInt<Pair> pairIndexByLegNdx = this.generateSolutionPairIndex();

			boolean[] legsConsidered = new boolean[this.legs.size()];
			/*
			 * TODO: Deadheads from other fleets needs to be considered!
			 */
			int reOrderNdx = 0;

			Leg leg = getNdxWithMaxValue(legsConsidered);

			while (leg != null) {
				legsConsidered[leg.getNdx()] = true;
				reOrderedLegs.set(reOrderNdx, leg);
				reOrderNdx++;

				Pair[] pairs = pairIndexByLegNdx.getArray(leg.getNdx());
				for (Pair pair : pairs) {
					reOrderNdx = this.enhanceReOrderedLegs(reOrderNdx, pair, legsConsidered);
				}

//				logger.info("checkAndUpdateTheOrder: " + leg.toString() + " #" + reOrderNdx);

				leg = getNdxWithMaxValue(legsConsidered);
			}

			logger.info("checkAndUpdateTheOrder: #" + reOrderNdx);

			for (int i = 0; i < legsConsidered.length; i++) {
				if (!legsConsidered[i]) {
					reOrderedLegs.set(reOrderNdx, this.legs.get(i));
					reOrderNdx++;
				}
			}

			logger.info("checkAndUpdateTheOrder: #" + reOrderNdx);
		}
	}

//	public void orderLegs() {
//		if (this.legPriorities != null) {
//			this.reOrderedLegs = this.legs.parallelStream().sorted(new Comparator<Leg>() {
//				@Override
//				public int compare(Leg a, Leg b) {
//					if (difficultyScoreCumulative[a.getNdx()]/difficultyScoreMax > difficultyScoreCumulative[b.getNdx()]/difficultyScoreMax)
//						return -1;
//					else
//						if (difficultyScoreCumulative[a.getNdx()]/difficultyScoreMax < difficultyScoreCumulative[b.getNdx()]/difficultyScoreMax)
//							return 1;
//					return 0;
//				}
//			}).collect(Collectors.toList());
//		} else {
//			this.reOrderedLegs = this.legs.parallelStream().sorted(new Comparator<Leg>() {
//				@Override
//				public int compare(Leg a, Leg b) {
//					if (a.getNdx() < b.getNdx())
//						return -1;
//					else
//						if (a.getNdx() > b.getNdx())
//							return 1;
//					return 0;
//				}
//			}).collect(Collectors.toList());
//		}
//
//		this.bestFitness = Integer.MAX_VALUE;
//
//		HyperChromoDecoder.logger.info("Leg list is reordered according to difficulty scores provided.");
//	}


	/*
	 * COST calculations.
	 */

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
//	private double getDutyCost(int cc, DutyView d, boolean hbDep, LocalDateTime nt) {
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
//			return getDutyCost(cc, pd, true, null);
//		else {
//			DutyView nd = null;
//			double pCost = 0.0;
//			for (int i = 1; i < p.getNumOfDuties(); i++) {
//				nd = p.getDuties().get(i);
//				pCost += getDutyCost(cc, pd, i == 1, nd.getBriefTime(hbNdx));
//				pd = nd;
//			}
//			pCost += getDutyCost(cc, nd, false, null);
//			return pCost;
//		}
//	}
}
