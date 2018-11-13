package org.heuros.hyperpair.ga;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.heuros.core.data.ndx.OneDimIndexInt;
import org.heuros.core.ga.chromosome.Chromosome;
import org.heuros.core.ga.decoder.Decoder;
import org.heuros.data.model.AirportView;
import org.heuros.data.model.Duty;
import org.heuros.data.model.DutyView;
import org.heuros.data.model.Leg;
import org.heuros.data.model.LegView;
import org.heuros.data.model.Pair;
import org.heuros.data.repo.DutyRepository;
import org.heuros.data.repo.LegRepository;
import org.heuros.hyperpair.heuristic.PairingGenerator;

public class PairChromosomeDecoder implements Decoder<Integer, Pair> {

	private static Logger logger = Logger.getLogger(PairChromosomeDecoder.class);

	private int numOfHeuristics = 3;
	public int getNumOfHeuristics() {
		return numOfHeuristics;
	}

	/*
	 * TODO Single base assumption!!!
	 */
	private int hbNdx = 0;

	private LegRepository legRepository = null;
	private DutyRepository dutyRepository = null;
//	private DutyRuleContext dutyRuleContext = null;
//	private PairRuleContext pairRuleContext = null;

	private OneDimIndexInt<Duty> dutyIndexByLegNdx = null;
//	private TwoDimIndexIntXInt<Duty> hbDepArrDutyIndexByLegNdx = null;
//	private TwoDimIndexIntXInt<Duty> hbDepDutyIndexByLegNdx = null;
//	private TwoDimIndexIntXInt<Duty> nonHbDutyIndexByLegNdx = null;
//	private TwoDimIndexIntXInt<Duty> hbArrDutyIndexByLegNdx = null;
//	private TwoDimIndexIntXLocalDateTime<Duty> dutyIndexByDepAirportNdxBrieftime = null;
//	private TwoDimIndexIntXLocalDateTime<Duty> dutyIndexByArrAirportNdxNextBrieftime = null;
////	private TwoDimIndexIntXLocalDateTime<Duty> hbArrDutyIndexByDepAirportNdxBrieftime = null;

	private PairingGenerator pairingGenerator = null;

	private List<Leg> reOrderedLegs = null;

//	private static Random random = new Random();
	private double difficultyScoreMax = Integer.MAX_VALUE;
	private int[] difficultyScoreCumulative = null;

	public void orderLegs() {
		if (this.bestNumOfLegCoverings != null) {
			this.difficultyScoreMax = 0;
			for (int i = 0; i < this.bestNumOfLegCoverings.length; i++) {
				if (this.legRepository.getModel(i).isCover()) {
					if (this.bestNumOfLegCoverings[i] > 1)
						this.difficultyScoreCumulative[i]++;
					if (this.difficultyScoreCumulative[i] > this.difficultyScoreMax)
						this.difficultyScoreMax = this.difficultyScoreCumulative[i];
				}
			}
			this.reOrderedLegs = this.legRepository.getModels().parallelStream().sorted(new Comparator<Leg>() {
				@Override
				public int compare(Leg a, Leg b) {
					if (difficultyScoreCumulative[a.getNdx()]/difficultyScoreMax > difficultyScoreCumulative[b.getNdx()]/difficultyScoreMax)
						return -1;
					else
						if (difficultyScoreCumulative[a.getNdx()]/difficultyScoreMax < difficultyScoreCumulative[b.getNdx()]/difficultyScoreMax)
							return 1;
					return 0;
				}
			}).collect(Collectors.toList());
		} else {
			this.reOrderedLegs = this.legRepository.getModels().parallelStream().sorted(new Comparator<Leg>() {
				@Override
				public int compare(Leg a, Leg b) {
					if (a.getNdx() < b.getNdx())
						return -1;
					else
						if (a.getNdx() > b.getNdx())
							return 1;
					return 0;
				}
			}).collect(Collectors.toList());
		}
		PairChromosomeDecoder.logger.info("Leg list is reordered according to difficulty scores provided.");
	}

	public PairChromosomeDecoder setLegRepository(LegRepository legRepository) {
		this.legRepository = legRepository;
		this.difficultyScoreCumulative = new int[legRepository.getModels().size()];
		return this;
	}

	public PairChromosomeDecoder setDutyRepository(DutyRepository dutyRepository) {
		this.dutyRepository = dutyRepository;
		return this;
	}

//	public PairChromosomeDecoder setDutyRuleContext(DutyRuleContext dutyRuleContext) {
//		this.dutyRuleContext = dutyRuleContext;
//		return this;
//	}
//
//	public PairChromosomeDecoder setPairRuleContext(PairRuleContext pairRuleContext) {
//		this.pairRuleContext = pairRuleContext;
//		return this;
//	}

	public PairChromosomeDecoder setDutyIndexByLegNdx(OneDimIndexInt<Duty> dutyIndexByLegNdx) {
		this.dutyIndexByLegNdx = dutyIndexByLegNdx;
		return this;
	}

//	public PairChromosomeDecoder setHbDepArrDutyIndexByLegNdx(TwoDimIndexIntXInt<Duty> hbDepArrDutyIndexByLegNdx) {
//		this.hbDepArrDutyIndexByLegNdx = hbDepArrDutyIndexByLegNdx;
//		return this;
//	}
//
//	public PairChromosomeDecoder setHbDepDutyIndexByLegNdx(TwoDimIndexIntXInt<Duty> hbDepDutyIndexByLegNdx) {
//		this.hbDepDutyIndexByLegNdx = hbDepDutyIndexByLegNdx;
//		return this;
//	}
//
//	public PairChromosomeDecoder setNonHbDutyIndexByLegNdx(TwoDimIndexIntXInt<Duty> nonHbDutyIndexByLegNdx) {
//		this.nonHbDutyIndexByLegNdx = nonHbDutyIndexByLegNdx;
//		return this;
//	}
//
//	public PairChromosomeDecoder setHbArrDutyIndexByLegNdx(TwoDimIndexIntXInt<Duty> hbArrDutyIndexByLegNdx) {
//		this.hbArrDutyIndexByLegNdx = hbArrDutyIndexByLegNdx;
//		return this;
//	}
//
//	public PairChromosomeDecoder setDutyIndexByDepAirportNdxBrieftime(
//			TwoDimIndexIntXLocalDateTime<Duty> dutyIndexByDepAirportNdxBrieftime) {
//		this.dutyIndexByDepAirportNdxBrieftime = dutyIndexByDepAirportNdxBrieftime;
//		return this;
//	}
//
//	public PairChromosomeDecoder setDutyIndexByArrAirportNdxNextBrieftime(
//			TwoDimIndexIntXLocalDateTime<Duty> dutyIndexByArrAirportNdxNextBrieftime) {
//		this.dutyIndexByArrAirportNdxNextBrieftime = dutyIndexByArrAirportNdxNextBrieftime;
//		return this;
//	}
//
////	public PairChromosomeDecoder setHbArrDutyIndexByDepAirportNdxBrieftime(
////			TwoDimIndexIntXLocalDateTime<Duty> hbArrDutyIndexByDepAirportNdxBrieftime) {
////		this.hbArrDutyIndexByDepAirportNdxBrieftime = hbArrDutyIndexByDepAirportNdxBrieftime;
////		return this;
////	}

	public PairChromosomeDecoder setPairingGenerator(PairingGenerator pairingGenerator) {
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
					blockTimeOfCoveringsInDuties[dutyOfLeg.getNdx()] += leg.getBlockTimeInMins();
				}
			}
		}
	}

	@Override
	public List<Pair> decode(Chromosome<Integer> chromosome) {

		List<Pair> solution = new ArrayList<Pair>();

		int geneNdx = 0;
		int uncoveredLegs = 0;

		double fitness = 0.0;

		int reOrderedLegNdx = -1;
		int[] numOfLegCoverings = new int[this.legRepository.getModels().size()];
		int[] numOfCoveringsInDuties = new int[this.dutyRepository.getModels().size()];
		int[] blockTimeOfCoveringsInDuties = new int[this.dutyRepository.getModels().size()];

		while (true) {
			reOrderedLegNdx = this.getNextLegNdxToCover(reOrderedLegNdx, numOfLegCoverings);
			if (reOrderedLegNdx == Integer.MAX_VALUE)
				break;

			Leg legToCover = this.reOrderedLegs.get(reOrderedLegNdx);

			int heuristicNo = chromosome.getGeneValue(geneNdx);

/*
 * TODO Remove the lines after test!
 */
if (legToCover.hasHbDepDutyPair(this.hbNdx)
		|| legToCover.hasHbArrDutyPair(this.hbNdx)
		|| legToCover.hasNonHbDutyPair(this.hbNdx))
heuristicNo = 1;

			Pair p = null;
			try {
				p = this.pairingGenerator.generatePairing(legToCover, 
															heuristicNo, 
															numOfCoveringsInDuties,
															blockTimeOfCoveringsInDuties);
			} catch (CloneNotSupportedException ex) {
				PairChromosomeDecoder.logger.error(ex);
			}

			if (p != null) {
				this.udpateStateVectors(p, numOfLegCoverings, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
				solution.add(p);
				geneNdx++;
			} else {
				PairChromosomeDecoder.logger.error("Pairing could not be found for " + legToCover);
				uncoveredLegs++;
			}
		}

		for (int i = 0; i < solution.size(); i++) {
			fitness += getPairCost(2, solution.get(i));
		}
		int numOfDeadheads = 0;
		for (int i = 0; i < this.legRepository.getModels().size(); i++) {
			if (this.legRepository.getModels().get(i).isCover()) {
				if (numOfLegCoverings[i] > 1) {
					fitness += (2.0 * (this.legRepository.getModels().get(i).getBlockTimeInMins() / 60.0) * dhPenalty);
					numOfDeadheads++;
				}
			} else {
				if (numOfLegCoverings[i] > 0) {
					fitness += (2.0 * (this.legRepository.getModels().get(i).getBlockTimeInMins() / 60.0) * dhPenalty);
					numOfDeadheads++;
				}
			}
		}
		chromosome.setFitness(fitness + uncoveredLegs * 100000000);
		chromosome.setInfo("uncoveredLegs: " + uncoveredLegs + ", numOfDeadheads: " + numOfDeadheads + ", fitness: " + fitness);
		this.checkFitness(chromosome.getFitness(), numOfLegCoverings);
		return solution;
	}

	private double bestFitness = Double.MAX_VALUE;
	private int[] bestNumOfLegCoverings = null;

	private synchronized void checkFitness(double newFitness, int[] newNumOfLegCoverings) {
		if (newFitness < bestFitness) {
			this.bestFitness = newFitness;
			this.bestNumOfLegCoverings = newNumOfLegCoverings;
		}
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

	public double getDutyCost(int cc, DutyView d, boolean hbDep, LocalDateTime nt) {
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

	public double getPairCost(int cc, Pair p) {
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
