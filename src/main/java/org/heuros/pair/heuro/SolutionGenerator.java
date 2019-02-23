package org.heuros.pair.heuro;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.apache.log4j.Logger;
import org.heuros.core.data.ndx.OneDimIndexInt;
import org.heuros.data.model.AirportView;
import org.heuros.data.model.Duty;
import org.heuros.data.model.DutyView;
import org.heuros.data.model.Leg;
import org.heuros.data.model.Pair;
import org.heuros.pair.conf.HeurosDatasetParam;
import org.heuros.pair.sp.PairingGenerator;

public class SolutionGenerator {

	private static Logger logger = Logger.getLogger(SolutionGenerator.class);

	/*
	 * TODO Single base assumption!!!
	 */
	private int hbNdx = 0;

	private List<Leg> legs = null;
	private List<Duty> duties = null;
	private OneDimIndexInt<Duty> dutyIndexByLegNdx = null;
	private PairingGenerator pairingGenerator = null;

	public SolutionGenerator(List<Leg> legs,
								List<Duty> duties,
								OneDimIndexInt<Duty> dutyIndexByLegNdx,
								PairingGenerator pairingGenerator) {
		this.legs = legs;
		this.duties = duties;
		this.dutyIndexByLegNdx = dutyIndexByLegNdx;
		this.pairingGenerator = pairingGenerator;
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

	private int getNextLegNdxToCover(LegState[] lps) {
		int res = -1;
		int minNumOfDutiesWoDh = Integer.MAX_VALUE;
		boolean isCritical = false;
		for (int i = 0; i < this.legs.size(); i++) {
			if (this.legs.get(i).isCover()
					&& this.legs.get(i).hasPair(hbNdx)
					&& this.legs.get(i).getSobt().isBefore(HeurosDatasetParam.optPeriodEndExc)
					&& (lps[i].numOfCoverings == 0)) {
//				if (this.legs.get(i).isCriticalWoDh()) {
				if (this.legs.get(i).getPotentialDhLevel() == 1) {
					if (!isCritical) {
						isCritical = true;
						res = i;
					} else
						if (minNumOfDutiesWoDh > lps[i].numOfIncludingDutiesWoDh) {
							minNumOfDutiesWoDh = lps[i].numOfIncludingDutiesWoDh;
							res = i;
						}
				} else
					if (!isCritical) {
						if (minNumOfDutiesWoDh > lps[i].numOfIncludingDutiesWoDh) {
							minNumOfDutiesWoDh = lps[i].numOfIncludingDutiesWoDh;
							res = i;
						}
					}
			}
		}
		return res;
	}

//	private int getNextLegNdxToCover(int prevReOrderedLegNdx, List<Leg> reOrderedLegs, LegParam[] lps) {
//		for (int i = prevReOrderedLegNdx + 1; i < reOrderedLegs.size(); i++) {
//			if (reOrderedLegs.get(i).isCover()
//					&& reOrderedLegs.get(i).hasPair(hbNdx)
//					&& (lps[reOrderedLegs.get(i).getNdx()].numOfCoverings == 0))
//				return i;
//		}
//		return Integer.MAX_VALUE;
//	}

	private void udpateStateVectors(Pair p,
									LegState[] lps,
									DutyState[] dps) {
		for (int i = 0; i < p.getNumOfDuties(); i++) {
			Duty duty = p.getDuties().get(i);
			for (int j = 0; j < duty.getNumOfLegs(); j++) {
				Leg leg = duty.getLegs().get(j);
//if (leg.getNdx() == 326)
//System.out.println();
				Duty[] dutiesOfLeg = this.dutyIndexByLegNdx.getArray(leg.getNdx());
				for (int di = 0; di < dutiesOfLeg.length; di++) {
					Duty dutyOfLeg = dutiesOfLeg[di];

					if (dutyOfLeg.hasPairing(hbNdx)
							&& dutyOfLeg.isValid(hbNdx)) {
						if (leg.isCover()
								&& (lps[leg.getNdx()].numOfCoverings == 0)) {
							if ((dutyOfLeg.getNumOfLegsPassive() == 0)
									&& (dps[dutyOfLeg.getNdx()].numOfCoverings == 0)) {
								for (int li = 0; li < dutyOfLeg.getLegs().size(); li++) {
									Leg indLeg = dutyOfLeg.getLegs().get(li);
									if (indLeg.isCover()
											&& (lps[indLeg.getNdx()].numOfCoverings == 0)) {
//if (indLeg.getNdx() == 326)
//System.out.println();
										lps[indLeg.getNdx()].numOfIncludingDutiesWoDh--;
//boolean becameCritical = false;
//if (indLeg.getNdx() != leg.getNdx()) {
//leg.getCouplingDutiesInDutiesWoDh()[indLeg.getNdx()]--;
//indLeg.getCouplingDutiesInDutiesWoDh()[leg.getNdx()]--;
//if (indLeg.getCouplingDutiesInDutiesWoDh()[indLeg.getNdx()] == lps[indLeg.getNdx()].numOfIncludingDutiesWoDh)
//	
//}
										Duty[] dutiesOfIndLeg = this.dutyIndexByLegNdx.getArray(indLeg.getNdx());
										for (int idi = 0; idi < dutiesOfIndLeg.length; idi++) {
											Duty dutieOfIndLeg = dutiesOfIndLeg[idi];
											if (dutieOfIndLeg.hasPairing(hbNdx)
													&& dutieOfIndLeg.isValid(hbNdx)) {
												dps[dutieOfIndLeg.getNdx()].totalNumOfAlternativeDutiesWoDh--;
												if (dps[dutieOfIndLeg.getNdx()].minNumOfAlternativeDutiesWoDh > lps[indLeg.getNdx()].numOfIncludingDutiesWoDh)
													dps[dutieOfIndLeg.getNdx()].minNumOfAlternativeDutiesWoDh = lps[indLeg.getNdx()].numOfIncludingDutiesWoDh;
												/*
												 * TODO
												 * 
												 * This implementation does not guarantee to set exact maxNumOfAlternativeDutiesWoDh.
												 * We did not want to make the code more complex by adding another state variable that is needed to be maintained during the iterations.
												 * Therefore the number of legs that has the same maxNumOfAlternativeDutiesWoDh might cause small disruptions.
												 *  
												 */
												if (dps[dutieOfIndLeg.getNdx()].maxNumOfAlternativeDutiesWoDh <= lps[indLeg.getNdx()].numOfIncludingDutiesWoDh)
													dps[dutieOfIndLeg.getNdx()].maxNumOfAlternativeDutiesWoDh = lps[indLeg.getNdx()].numOfIncludingDutiesWoDh;
											}
										}
									}
								}
							}
						}
					}

					dps[dutyOfLeg.getNdx()].numOfCoverings++;
					if (lps[leg.getNdx()].numOfCoverings == 0)
						dps[dutyOfLeg.getNdx()].numOfDistinctCoverings++;
					dps[dutyOfLeg.getNdx()].blockTimeOfCoverings += leg.getBlockTimeInMins();
				}
				lps[leg.getNdx()].numOfCoverings++;
			}
		}
	}

	public double generateSolution(List<Leg> reOrderedLegs,
									List<Pair> solution,
									LegState[] legParams,
									DutyState[] dutyParams) {

		logger.info("Solution generation process is started!");

		int uncoveredLegs = 0;

		int reOrderedLegNdx = -1;

		while (true) {
//			reOrderedLegNdx = this.getNextLegNdxToCover(reOrderedLegNdx, reOrderedLegs, legParams);
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
				logger.error(ex);
			}

			if (p != null) {
				if (p.getFirstDuty().getBriefTime(hbNdx).isBefore(HeurosDatasetParam.optPeriodEndExc)) {
					this.udpateStateVectors(p, legParams, dutyParams);
					solution.add(p);

					/**
					 * TEST BLOCK BEGIN
					 * 
					 * Checks QualityMetric TOTALIZERS.
					 * 
					 */
//					this.duties.forEach((d) -> {
//						if (d.isValid(this.hbNdx)
//								&& d.hasPairing(this.hbNdx)) {
//							DutyState ds = dutyParams[d.getNdx()];
//							int minNumOfAlternativeDuties = Integer.MAX_VALUE;
//							int minNumOfAlternativeDutiesWoDh = Integer.MAX_VALUE;
//							int totalNumOfAlternativeDuties = 0;
//							int totalNumOfAlternativeDutiesWoDh = 0;
//							for (int i = 0; i < d.getNumOfLegs(); i++) {
//								Leg l = d.getLegs().get(i);
//								if (minNumOfAlternativeDuties > legParams[l.getNdx()].numOfIncludingDuties)
//									minNumOfAlternativeDuties = legParams[l.getNdx()].numOfIncludingDuties;
//								if (minNumOfAlternativeDutiesWoDh > legParams[l.getNdx()].numOfIncludingDutiesWoDh)
//									minNumOfAlternativeDutiesWoDh = legParams[l.getNdx()].numOfIncludingDutiesWoDh;
//								totalNumOfAlternativeDuties += legParams[l.getNdx()].numOfIncludingDuties;
//								totalNumOfAlternativeDutiesWoDh += legParams[l.getNdx()].numOfIncludingDutiesWoDh;
//							}
//							if (ds.minNumOfAlternativeDuties != minNumOfAlternativeDuties)
//								System.out.println("!" + legToCover);
//							if (ds.minNumOfAlternativeDutiesWoDh != minNumOfAlternativeDutiesWoDh)
//								System.out.println("!" + legToCover);
//							if (ds.totalNumOfAlternativeDuties != totalNumOfAlternativeDuties)
//								System.out.println("!" + legToCover);
//							if (ds.totalNumOfAlternativeDutiesWoDh != totalNumOfAlternativeDutiesWoDh)
//								System.out.println("!" + legToCover);
//						}
//					});
					/**
					 * TEST BLOCK END
					 * 
					 */
				}
			} else {
				logger.error("Pairing could not be found for " + legToCover);
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

		int numOfLegsFromTheFleet = 0;
		int numOfDeadheadLegsFromTheFleet = 0;
		int numOfLegsOutsideOfTheFleet = 0;
		int numOfDeadheads = 0;

		for (int i = 0; i < this.legs.size(); i++) {
			if (this.legs.get(i).isCover()) {
				if (legParams[i].numOfCoverings > 0) 
					numOfLegsFromTheFleet++;
				if (legParams[i].numOfCoverings > 1) {
					fitness += (2.0 * (legParams[i].numOfCoverings - 1) * (this.legs.get(i).getBlockTimeInMins() / 60.0) * dhPenalty);
//					fitness += (numOfLegCoverings[i] - 1) * 100;
					numOfDeadheads += (legParams[i].numOfCoverings - 1);
					numOfDeadheadLegsFromTheFleet++;
				}
			} else {
				if (legParams[i].numOfCoverings > 0) {
					fitness += (2.0 * legParams[i].numOfCoverings * (this.legs.get(i).getBlockTimeInMins() / 60.0) * dhPenalty);
//					fitness += numOfLegCoverings[i] * 100;
					numOfDeadheads += legParams[i].numOfCoverings;
					numOfLegsOutsideOfTheFleet++;
				}
			}
		}

		logger.info("numOfPairs: " + numOfPairs + 
					", numOfDuties: " + numOfDuties +
					", numOfPairDays:" + numOfPairDays +
					", uncoveredLegs: " + uncoveredLegs + 
					", numOfDeadheads: " + numOfDeadheads + 
					", numOfLegsFromTheFleet: " + numOfLegsFromTheFleet +
					", numOfDeadheadLegsFromTheFleet: " + numOfDeadheadLegsFromTheFleet +
					", numOfLegsOutsideOfTheFleet: " + numOfLegsOutsideOfTheFleet +
					", fitness: " + fitness);

		return fitness;
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
