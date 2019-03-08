package org.heuros.pair.heuro.state;

import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;
import org.heuros.core.data.ndx.OneDimIndexInt;
import org.heuros.data.model.Duty;
import org.heuros.data.model.Leg;
import org.heuros.data.model.Pair;
import org.heuros.pair.conf.HeurosDatasetParam;
import org.heuros.pair.conf.HeurosSystemParam;

public class SolutionState {

	private static Logger logger = Logger.getLogger(SolutionState.class);

	private List<Leg> legs = null;
	private List<Duty> duties = null;
	private OneDimIndexInt<Duty> dutyIndexByLegNdx = null;
	private LegState[] legStates = null;
	private DutyState[] dutyStates = null;

	private LegState legMaxState = null;

	public SolutionState(List<Leg> legs,
							List<Duty> duties,
							OneDimIndexInt<Duty> dutyIndexByLegNdx) {
		this.legs = legs;
		this.duties = duties;
		this.dutyIndexByLegNdx = dutyIndexByLegNdx;

		this.legStates = new LegState[this.legs.size()];
		this.dutyStates = new DutyState[this.duties.size()];

		for (int j = 0; j < this.legStates.length; j++)
			this.legStates[j] = new LegState();
		for (int j = 0; j < this.dutyStates.length; j++)
			this.dutyStates[j] = new DutyState();

		this.legMaxState = new LegState();
	}

	public LegState[] getLegStates() {
		return legStates;
	}

	public DutyState[] getDutyStates() {
		return dutyStates;
	}

	public void initializeIteration() {
		for (int j = 0; j < this.legStates.length; j++) {
			this.legStates[j].initialize(this.legs.get(j));
		}
		for (int j = 0; j < this.dutyStates.length; j++) {
			this.dutyStates[j].initialize(this.duties.get(j));
		}
		this.calculateAndSetMaxValuesOfHeuristicsParameters();
	}

	private void calculateAndSetMaxValuesOfHeuristicsParameters() {
		this.legMaxState.numOfIncludingDuties = 0;
		this.legMaxState.numOfIncludingDutiesWoDh = 0;
		this.legMaxState.numOfIncludingEffectiveDuties = 0;
		this.legMaxState.numOfIncludingEffectiveDutiesWoDh = 0;
		this.legMaxState.heuristicModifierValue = 0.0;
		for (int j = 0; j < legStates.length; j++) {
			if (this.legMaxState.numOfIncludingDuties < this.legStates[j].numOfIncludingDuties)
				this.legMaxState.numOfIncludingDuties = this.legStates[j].numOfIncludingDuties;
			if (this.legMaxState.numOfIncludingDutiesWoDh < this.legStates[j].numOfIncludingDutiesWoDh)
				this.legMaxState.numOfIncludingDutiesWoDh = this.legStates[j].numOfIncludingDutiesWoDh;
			if (this.legMaxState.numOfIncludingEffectiveDuties < this.legStates[j].numOfIncludingEffectiveDuties)
				this.legMaxState.numOfIncludingEffectiveDuties = this.legStates[j].numOfIncludingEffectiveDuties;
			if (this.legMaxState.numOfIncludingEffectiveDutiesWoDh < this.legStates[j].numOfIncludingEffectiveDutiesWoDh)
				this.legMaxState.numOfIncludingEffectiveDutiesWoDh = this.legStates[j].numOfIncludingEffectiveDutiesWoDh;
			if (this.legMaxState.heuristicModifierValue < this.legStates[j].heuristicModifierValue)
				this.legMaxState.heuristicModifierValue = this.legStates[j].heuristicModifierValue;
		}
	}

	public int getNextLegNdxToCover(int hbNdx) {
		int res = -1;
		double highestScore = 0.0;
//		boolean isCritical = false;
		for (int i = 0; i < this.legs.size(); i++) {
			if (this.legs.get(i).isCover()
					&& this.legs.get(i).hasPair(hbNdx)
					&& this.legs.get(i).getSobt().isBefore(HeurosDatasetParam.optPeriodEndExc)
					&& (legStates[i].numOfCoverings == 0)) {
//				if (legStates[i].potentialDh) {
//					if (!isCritical) {
//						isCritical = true;
//						minNumOfDutiesWoDh = legStates[i].numOfIncludingDutiesWoDh;
//						res = i;
//					} else
//						if (minNumOfDutiesWoDh > legStates[i].numOfIncludingDutiesWoDh) {
//							minNumOfDutiesWoDh = legStates[i].numOfIncludingDutiesWoDh;
//							res = i;
//						}
//				} else
//					if (!isCritical) {
						if (highestScore < legStates[i].getDifficultyScoreOfTheLeg(this.legMaxState)) {
							highestScore = legStates[i].getDifficultyScoreOfTheLeg(this.legMaxState);
							res = i;
						}
//					}
			}
		}
		return res;
	}

	public void udpateStateVectors(Pair p) {

		HashSet<Integer> legNdxs = new HashSet<Integer>();

		/*
		 * First update COVERING states of the legs and duties that constitute new pairing.
		 */
		for (int i = 0; i < p.getNumOfDuties(); i++) {
			Duty duty = p.getDuties().get(i);
			for (int j = 0; j < duty.getNumOfLegs(); j++) {
				Leg leg = duty.getLegs().get(j);
				legNdxs.add(leg.getNdx());
				legStates[leg.getNdx()].numOfCoverings++;
			}
		}

//		HashSet<Integer> indLegNdxs = new HashSet<Integer>();

		/*
		 * Set dutyOfLeg TOTALIZERs.
		 */
		legNdxs.forEach((legNdx) -> {
			Leg leg = this.legs.get(legNdx);
			Duty[] dutiesOfLeg = this.dutyIndexByLegNdx.getArray(leg.getNdx());
			for (int di = 0; di < dutiesOfLeg.length; di++) {
				Duty dutyOfLeg = dutiesOfLeg[di];
				if (dutyOfLeg.hasPairing(p.getHbNdx())
						&& dutyOfLeg.isValid(p.getHbNdx())) {

					dutyStates[dutyOfLeg.getNdx()].numOfCoverings++;
					dutyStates[dutyOfLeg.getNdx()].blockTimeOfCoverings += leg.getBlockTimeInMins();
					if (leg.isCover()) {
						dutyStates[dutyOfLeg.getNdx()].numOfCoveringsActive++;
						dutyStates[dutyOfLeg.getNdx()].blockTimeOfCoveringsActive += leg.getBlockTimeInMins();
					} else {
						dutyStates[dutyOfLeg.getNdx()].numOfCoveringsPassive++;
						dutyStates[dutyOfLeg.getNdx()].blockTimeOfCoveringsPassive += leg.getBlockTimeInMins();
					}
					if (legStates[leg.getNdx()].numOfCoverings == 1) {
						dutyStates[dutyOfLeg.getNdx()].numOfDistinctCoverings++;
						dutyStates[dutyOfLeg.getNdx()].blockTimeOfDistinctCoverings += leg.getBlockTimeInMins();
						if (leg.isCover()) {
							dutyStates[dutyOfLeg.getNdx()].numOfDistinctCoveringsActive++;
							dutyStates[dutyOfLeg.getNdx()].blockTimeOfDistinctCoveringsActive += leg.getBlockTimeInMins();
						} else {
							dutyStates[dutyOfLeg.getNdx()].numOfDistinctCoveringsPassive++;
							dutyStates[dutyOfLeg.getNdx()].blockTimeOfDistinctCoveringsPassive += leg.getBlockTimeInMins();
						}
					}

					boolean isDhStateChanged = (dutyOfLeg.getNumOfLegsPassive() == 0) && (dutyStates[dutyOfLeg.getNdx()].numOfCoverings == 1);
					boolean isEffectivenessChanged = leg.isCover()
														&& (legStates[leg.getNdx()].numOfCoverings == 1)
														&& (dutyOfLeg.getBlockTimeInMinsActive() 
																- dutyStates[dutyOfLeg.getNdx()].blockTimeOfDistinctCoveringsActive 
																+ leg.getBlockTimeInMins() >= HeurosSystemParam.effectiveDutyBlockHourLimit)
														&& (dutyOfLeg.getBlockTimeInMinsActive() 
																- dutyStates[dutyOfLeg.getNdx()].blockTimeOfDistinctCoveringsActive < HeurosSystemParam.effectiveDutyBlockHourLimit);

					if (isDhStateChanged
							|| isEffectivenessChanged) {
						for (int li = 0; li < dutyOfLeg.getLegs().size(); li++) {
							Leg indLeg = dutyOfLeg.getLegs().get(li);

//							if (indLeg.isCover()
//									&& (legStates[indLeg.getNdx()].numOfCoverings == 0)
//									&& indLeg.hasPair(p.getHbNdx())
//									&& (indLeg.getNdx() != leg.getNdx()))
//								indLegNdxs.add(indLeg.getNdx());

							if (isEffectivenessChanged) {
								legStates[indLeg.getNdx()].numOfIncludingEffectiveDuties--;
							}

							if (isDhStateChanged) {
								legStates[indLeg.getNdx()].numOfIncludingDutiesWoDh--;
								if (dutyOfLeg.getBlockTimeInMinsActive() >= HeurosSystemParam.effectiveDutyBlockHourLimit) {
									legStates[indLeg.getNdx()].numOfIncludingEffectiveDutiesWoDh--;
								}
							}

							Duty[] dutiesOfIndLeg = this.dutyIndexByLegNdx.getArray(indLeg.getNdx());
							for (int idi = 0; idi < dutiesOfIndLeg.length; idi++) {
								Duty dutieOfIndLeg = dutiesOfIndLeg[idi];
								if (dutieOfIndLeg.hasPairing(hbNdx)
										&& dutieOfIndLeg.isValid(hbNdx)) {
									dutyStates[dutieOfIndLeg.getNdx()].totalNumOfAlternativeDutiesWoDh--;
									if (dutyStates[dutieOfIndLeg.getNdx()].minNumOfAlternativeDutiesWoDh > legStates[indLeg.getNdx()].numOfIncludingDutiesWoDh)
										dutyStates[dutieOfIndLeg.getNdx()].minNumOfAlternativeDutiesWoDh = legStates[indLeg.getNdx()].numOfIncludingDutiesWoDh;
									/*
									 * TODO
									 * 
									 * This implementation does not guarantee to set exact maxNumOfAlternativeDutiesWoDh.
									 * We did not want to make the code more complex by adding another state variable that is needed to be maintained during the iterations.
									 * Therefore the number of legs that has the same maxNumOfAlternativeDutiesWoDh might cause small disruptions.
									 *  
									 */
									if (dutyStates[dutieOfIndLeg.getNdx()].maxNumOfAlternativeDutiesWoDh <= legStates[indLeg.getNdx()].numOfIncludingDutiesWoDh)
										dutyStates[dutieOfIndLeg.getNdx()].maxNumOfAlternativeDutiesWoDh = legStates[indLeg.getNdx()].numOfIncludingDutiesWoDh;
								}
							}

						}
					}
				}
			}
		});

//		indLegNdxs.forEach((indLegNdx) -> {
//			Leg indLeg = this.legs.get(indLegNdx);
//			if (legStates[indLeg.getNdx()].numOfCoverings == 0) {
//				int numOfNewDhCritDuties = 0;
//				Duty[] dutiesOfIndLeg = this.dutyIndexByLegNdx.getArray(indLeg.getNdx());
//				int[] numOfLegAssc = new int[this.legs.size()];
//				int maxLegAssc = 0;
//				Leg legWithMaxAssc = null;
//				int numOfDhFreeDuties = 0;
//				for (Duty dutyOfIndLeg : dutiesOfIndLeg) {
//					if (dutyOfIndLeg.hasPairing(p.getHbNdx())
//							&& dutyOfIndLeg.isValid(p.getHbNdx())
//							&& (dutyStates[dutyOfIndLeg.getNdx()].numOfCoverings == 0)
//							) {
//						numOfDhFreeDuties++;
//						for (int ili = 0; ili < dutyOfIndLeg.getLegs().size(); ili++) {
//							Leg ascLeg = dutyOfIndLeg.getLegs().get(ili);
//							if (indLeg.getNdx() != ascLeg.getNdx()) {
//								numOfLegAssc[ascLeg.getNdx()]++;
//								if (maxLegAssc < numOfLegAssc[ascLeg.getNdx()]) {
//									maxLegAssc = numOfLegAssc[ascLeg.getNdx()];
//									legWithMaxAssc = ascLeg;
//								}
//							}
//						}
//					}
//				}
//				if ((maxLegAssc > 0)
//						&& (maxLegAssc == numOfDhFreeDuties)) {
//					legStates[indLeg.getNdx()].potentialDh = true;
//					Duty[] critDutyCands = this.dutyIndexByLegNdx.getArray(legWithMaxAssc.getNdx());
//					for (Duty critDutyCand : critDutyCands) {
//						if (critDutyCand.hasPairing(p.getHbNdx())
//								&& critDutyCand.isValid(p.getHbNdx())
////								&& (dutyStates[critDutyCand.getNdx()].numOfCoverings == 0)
//								) {
//
//							boolean hasIndLeg = false;
//							for (int cli = 0; cli < critDutyCand.getLegs().size(); cli++) {
//								if (critDutyCand.getLegs().get(cli).getNdx() == indLegNdx) {
//									hasIndLeg = true;
//									break;
//								}
//							}
//							if (!hasIndLeg) {
//								if (!dutyStates[critDutyCand.getNdx()].dhCritical)
//									numOfNewDhCritDuties++;
//								dutyStates[critDutyCand.getNdx()].dhCritical = true;
//							}
//						}
//					}
//				}
//				if (numOfNewDhCritDuties > 0) {
//					logger.info(indLeg + " -> newCritDuties: " + numOfNewDhCritDuties);
//				}
//			}
//		});

		this.calculateAndSetMaxValuesOfHeuristicsParameters();
	}

	public double finalizeIteration(int iterationNumber, List<Pair> solution, int uncoveredLegs) {
		double fitness = 0.0;
		int numOfDuties = 0;
		int numOfPairDays = 0;
		int numOfPairs = 0;

		/*
		 * Calculate standard fitness and heuristic cost to be able to calculate value of Heuristic Modifiers.
		 */
		for (int i = 0; i < solution.size(); i++) {
			Pair p = solution.get(i);
//			fitness += getPairCost(2, p);
			numOfDuties += p.getNumOfDuties();
			numOfPairDays += p.getNumOfDaysTouched();
			numOfPairs++;

			for (int j = 0; j < p.getNumOfDuties(); j++) {
				Duty d = p.getDuties().get(j);
				double effectiveCost = 0.0;
				if (this.dutyStates[d.getNdx()].blockTimeOfCoveringsActive < HeurosSystemParam.effectiveDutyBlockHourLimit) {
					effectiveCost += (HeurosSystemParam.effectiveDutyBlockHourLimit - this.dutyStates[d.getNdx()].blockTimeOfCoveringsActive);
					fitness += effectiveCost;
				}
				for (int k = 0; k < d.getNumOfLegs(); k++) {
					Leg l = d.getLegs().get(k);
					if (l.isCover()) {
						legStates[l.getNdx()].numOfIterations = iterationNumber;
						/*
						 * Add effective cost (mins diff.) to heuristicModifier.
						 */
						legStates[l.getNdx()].heuristicModifierValue += effectiveCost;
						/*
						 * Add dh cost (block time of the dh leg) to heuristicModifier.
						 */
						legStates[l.getNdx()].heuristicModifierValue += ((legStates[l.getNdx()].numOfCoverings - 1) * l.getBlockTimeInMins() / 2.0);

						fitness += ((legStates[l.getNdx()].numOfCoverings - 1) * l.getBlockTimeInMins() / 2.0);
					}
				}
			}
		}

		/*
		 * Calculate solution statistics and add standard DH cost to the fitness.
		 */
		int numOfLegsFromTheFleet = 0;
		int numOfDeadheadLegsFromTheFleet = 0;
		int numOfLegsOutsideOfTheFleet = 0;
		int numOfDeadheads = 0;

		for (int i = 0; i < this.legs.size(); i++) {
			if (this.legs.get(i).isCover()) {
				if (this.legStates[i].numOfCoverings > 0) 
					numOfLegsFromTheFleet++;
				if (this.legStates[i].numOfCoverings > 1) {
//					fitness += (2.0 * (this.legStates[i].numOfCoverings - 1) * (this.legs.get(i).getBlockTimeInMins() / 60.0) * dhPenalty);
					numOfDeadheads += (this.legStates[i].numOfCoverings - 1);
					numOfDeadheadLegsFromTheFleet++;
				}
			} else {
				if (this.legStates[i].numOfCoverings > 0) {
//					fitness += (2.0 * this.legStates[i].numOfCoverings * (this.legs.get(i).getBlockTimeInMins() / 60.0) * dhPenalty);
					numOfDeadheads += this.legStates[i].numOfCoverings;
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
//	private double getDutyCost(int hbNdx, int cc, DutyView d, boolean hbDep, LocalDateTime nt) {
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
//			return getDutyCost(p.getHbNdx(), cc, pd, true, null);
//		else {
//			DutyView nd = null;
//			double pCost = 0.0;
//			for (int i = 1; i < p.getNumOfDuties(); i++) {
//				nd = p.getDuties().get(i);
//				pCost += getDutyCost(p.getHbNdx(), cc, pd, i == 1, nd.getBriefTime(p.getHbNdx()));
//				pd = nd;
//			}
//			pCost += getDutyCost(p.getHbNdx(), cc, nd, false, null);
//			return pCost;
//		}
//	}
}
