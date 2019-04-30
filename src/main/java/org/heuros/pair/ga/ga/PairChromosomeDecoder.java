package org.heuros.pair.ga.ga;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.heuros.context.PairOptimizationContext;
import org.heuros.core.ga.chromosome.Chromosome;
import org.heuros.core.ga.decoder.Decoder;
import org.heuros.data.DutyLegOvernightConnNetwork;
import org.heuros.data.model.Leg;
import org.heuros.data.model.Pair;
import org.heuros.pair.conf.HeurosDatasetParam;
import org.heuros.pair.heuro.state.SolutionState;
import org.heuros.pair.sp.PairWithQuality;
import org.heuros.pair.sp.PairingGenerator;

public class PairChromosomeDecoder implements Decoder<Integer, Pair> {

	private static Logger logger = Logger.getLogger(PairChromosomeDecoder.class);

	/*
	 * TODO Single base assumption!!!
	 */
	private int hbNdx = 0;
	private PairOptimizationContext pairOptimizationContext = null;
	private DutyLegOvernightConnNetwork pricingNetwork = null;
	private List<Leg> legs = null;

	public PairChromosomeDecoder(PairOptimizationContext pairOptimizationContext,
									DutyLegOvernightConnNetwork pricingNetwork) {
		this.pairOptimizationContext = pairOptimizationContext;
		this.pricingNetwork = pricingNetwork;
		this.legs = this.pairOptimizationContext.getLegRepository().getModels();
	}

	public int getNextLegNdxToCover(int lastGenNdx, PairChromosome pC, SolutionState solutionState) {
		for (int i = lastGenNdx; i < this.legs.size(); i++) {
			int legNdx = pC.getGeneValue(i);
			if (this.legs.get(legNdx).isCover()
					&& this.legs.get(legNdx).hasPair(hbNdx)
					&& this.legs.get(legNdx).getSobt().isBefore(HeurosDatasetParam.optPeriodEndExc)
					&& (solutionState.getActiveLegStates()[legNdx].numOfCoverings == 0)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public List<Pair> decode(Chromosome<Integer> chromosome) {

		logger.info("Decoding process is started!");

		PairChromosome pC = (PairChromosome) chromosome;

		List<Pair> solution = new ArrayList<Pair>();

		try {
			SolutionState solutionState = new SolutionState(pairOptimizationContext, pricingNetwork);
			PairingGenerator pairingGenerator = new PairingGenerator(pairOptimizationContext, pricingNetwork, solutionState);

			int geneNdx = -1;
			while (true) {
	
				geneNdx = this.getNextLegNdxToCover(geneNdx + 1, pC, solutionState);
	
				if (geneNdx < 0)
					break;

				int legNdxToCover = pC.getGeneValue(geneNdx);
				Leg legToCover = this.legs.get(legNdxToCover);
				pairingGenerator.setLegForPairGeneration(legToCover);
				PairWithQuality[] pqs = pairingGenerator.generatePairings();
				PairWithQuality pwq = solutionState.chooseBestPairing(pqs);
				if (pwq != null) {
					solution.add(pwq.p);
				} else {
					logger.error("Pairing could not be found for " + legToCover);
				}
			}

			solutionState.finalizeIteration(0.0, 0.0, solution);

			chromosome.setFitness(solutionState.getFinalCost());
			chromosome.setInfo("#Pairs: " + solutionState.getNumOfPairs() +
								" #Duties: " + solutionState.getNumOfDuties() +
								" #PairDays: " + solutionState.getNumOfPairDays() +
								" #DutyDays: " + solutionState.getNumOfDutyDays() +
								" #Dh: " + solutionState.getNumOfDeadheads() +
								" #LegsInt: " + solutionState.getNumOfDistinctLegsFromTheFleet() +
								" #LegsIntDh: " + solutionState.getNumOfDistinctDeadheadLegsFromTheFleet() +
								" #LegsFltExt: " + solutionState.getNumOfDistinctLegsOutsideOfTheFleet() +
								" TotHM_Dh: " + solutionState.getTotalHeurModDh() +
								" TotHM_Ef: " + solutionState.getTotalHeurModEf() +
								" FinalCost: " + solutionState.getFinalCost());

		} catch (Exception ex) {
			logger.error(ex);
		}
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
