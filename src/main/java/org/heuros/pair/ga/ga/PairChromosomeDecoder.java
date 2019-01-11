package org.heuros.pair.ga.ga;

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

public class PairChromosomeDecoder implements Decoder<Integer, Pair> {

	private static Logger logger = Logger.getLogger(PairChromosomeDecoder.class);

	/*
	 * TODO Single base assumption!!!
	 */
	private int hbNdx = 0;

	private LegRepository legRepository = null;
	private DutyRepository dutyRepository = null;

	private OneDimIndexInt<Duty> dutyIndexByLegNdx = null;

	private PairingGenerator pairingGenerator = null;

	private List<Leg> legs = null;

	public PairChromosomeDecoder setLegRepository(LegRepository legRepository) {
		this.legRepository = legRepository;
		this.legs = this.legRepository.getModels();
		return this;
	}

	public PairChromosomeDecoder setDutyRepository(DutyRepository dutyRepository) {
		this.dutyRepository = dutyRepository;
		return this;
	}

	public PairChromosomeDecoder setDutyIndexByLegNdx(OneDimIndexInt<Duty> dutyIndexByLegNdx) {
		this.dutyIndexByLegNdx = dutyIndexByLegNdx;
		return this;
	}

	public PairChromosomeDecoder setPairingGenerator(PairingGenerator pairingGenerator) {
		this.pairingGenerator = pairingGenerator;
		return this;
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

		PairChromosome pC = (PairChromosome) chromosome;

		List<Pair> solution = new ArrayList<Pair>();

		int geneNdx = 0;
		int uncoveredLegs = 0;
		int[] numOfLegCoverings = new int[this.legRepository.getModels().size()];
		int[] numOfCoveringsInDuties = new int[this.dutyRepository.getModels().size()];
		int[] numOfDistinctCoveringsInDuties = new int[this.dutyRepository.getModels().size()];
		int[] blockTimeOfCoveringsInDuties = new int[this.dutyRepository.getModels().size()];

int[] dutyPriorityCumulative = new int[this.dutyRepository.getModels().size()];
for (int i = 0; i < this.legs.size(); i++) {
	LegView l = this.legs.get(i);
	int lOrder = pC.getPosValue(l.getNdx());
	DutyView[] lDuties = this.dutyIndexByLegNdx.getArray(l.getNdx());
	for (DutyView dutyView : lDuties) {
		dutyPriorityCumulative[dutyView.getNdx()] += lOrder;
	}
}

		while (true) {

			int legToCoverNdx = -1;

			while (geneNdx < chromosome.getChromosomeLength()) {
				int hNdx = chromosome.getGeneValue(geneNdx);
				geneNdx++;
				if (this.legs.get(hNdx).isCover()
						&& (this.legs.get(hNdx).hasPair(hbNdx))
						&& (numOfLegCoverings[this.legs.get(hNdx).getNdx()] == 0)) {
					legToCoverNdx = hNdx;
					break;
				}
			}
			if (legToCoverNdx < 0)
				break;

			Leg legToCover = this.legs.get(legToCoverNdx);


//			int heuristicNo = chromosome.getGeneValue(geneNdx);
//			if (heuristicNo == 0)
//				if (!legToCover.hasHbDepArrDutyPair(this.hbNdx))
//					heuristicNo = 1;
			int heuristicNo = 1;

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
				PairChromosomeDecoder.logger.error(ex);
			}

//if (legToCover.getNdx() == 1768)
//System.out.println(p);
			if (p != null) {
				this.udpateStateVectors(p, numOfLegCoverings, numOfCoveringsInDuties, numOfDistinctCoveringsInDuties, blockTimeOfCoveringsInDuties);
				solution.add(p);
			} else {
				PairChromosomeDecoder.logger.error("Pairing could not be found for " + legToCover);
				uncoveredLegs++;
			}
		}

		double fitness = 0.0;
		int numOfDuties = 0;
		int numOfPairDays = 0;
		int numOfPairs = 0;

		for (int i = 0; i < solution.size(); i++) {
//			fitness += getPairCost(2, solution.get(i));
//			fitness += solution.get(i).getNumOfDaysTouched();
			numOfDuties += solution.get(i).getNumOfDuties();
			numOfPairDays += solution.get(i).getNumOfDaysTouched();
			numOfPairs++;
		}

		int numOfDeadheads = 0;

		for (int i = 0; i < this.legRepository.getModels().size(); i++) {
			if (this.legRepository.getModels().get(i).isCover()) {
				if (numOfLegCoverings[i] > 1) {
//					fitness += (2.0 * (numOfLegCoverings[i] - 1) * (this.legRepository.getModels().get(i).getBlockTimeInMins() / 60.0) * dhPenalty);
					fitness += (numOfLegCoverings[i] - 1) * 100;
					numOfDeadheads += (numOfLegCoverings[i] - 1);
				}
			} else {
				if (numOfLegCoverings[i] > 0) {
//					fitness += (2.0 * numOfLegCoverings[i] * (this.legRepository.getModels().get(i).getBlockTimeInMins() / 60.0) * dhPenalty);
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

		logger.info("numOfPairs: " + numOfPairs + 
					", numOfDuties: " + numOfDuties +
					", numOfDutyDays:" + numOfPairDays + 
					", uncoveredLegs: " + uncoveredLegs + 
					", numOfDeadheads: " + numOfDeadheads + 
					", fitness: " + fitness);
		logger.info(chromosome);
		logger.info("Decoding process is ended!");
		return solution;
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
