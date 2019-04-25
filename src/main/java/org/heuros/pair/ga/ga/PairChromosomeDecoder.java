package org.heuros.pair.ga.ga;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.heuros.context.PairOptimizationContext;
import org.heuros.core.data.ndx.OneDimIndexInt;
import org.heuros.core.ga.chromosome.Chromosome;
import org.heuros.core.ga.decoder.Decoder;
import org.heuros.data.DutyLegOvernightConnNetwork;
import org.heuros.data.model.Duty;
import org.heuros.data.model.DutyView;
import org.heuros.data.model.Leg;
import org.heuros.data.model.LegView;
import org.heuros.data.model.Pair;
import org.heuros.data.repo.DutyRepository;
import org.heuros.data.repo.LegRepository;
import org.heuros.pair.heuro.state.SolutionState;
import org.heuros.pair.sp.PairingGenerator;

public class PairChromosomeDecoder implements Decoder<Integer, Pair> {

	private static Logger logger = Logger.getLogger(PairChromosomeDecoder.class);

	/*
	 * TODO Single base assumption!!!
	 */
	private int hbNdx = 0;

	public PairChromosomeDecoder(PairOptimizationContext pairOptimizationContext,
									DutyLegOvernightConnNetwork pricingNetwork) {
	}

	@Override
	public List<Pair> decode(Chromosome<Integer> chromosome) {

		logger.info("Decoding process is started!");

		PairChromosome pC = (PairChromosome) chromosome;

		List<Pair> solution = new ArrayList<Pair>();

		int geneNdx = 0;
		while (true) {

			int legToCoverNdx = -1;

			while (geneNdx < chromosome.getChromosomeLength()) {
				int hNdx = chromosome.getGeneValue(geneNdx);
				geneNdx++;
			}
			if (legToCoverNdx < 0)
				break;

		}

//		chromosome.setFitness(fitness + uncoveredLegs * 100000000);
//		chromosome.setInfo("numOfPairs:" + numOfPairs + 
//							", numOfDuties:" + numOfDuties +
//							", numOfPairDays:" + numOfPairDays + 
//							", uncoveredLegs: " + uncoveredLegs + 
//							", numOfDeadheads: " + numOfDeadheads + 
//							", fitness: " + fitness);
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
