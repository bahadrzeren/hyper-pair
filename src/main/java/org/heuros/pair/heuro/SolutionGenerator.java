package org.heuros.pair.heuro;

import java.util.List;

import org.apache.log4j.Logger;
import org.heuros.core.data.ndx.OneDimIndexInt;
import org.heuros.data.model.Duty;
import org.heuros.data.model.Leg;
import org.heuros.data.model.Pair;
import org.heuros.pair.conf.HeurosDatasetParam;
import org.heuros.pair.conf.HeurosSystemParam;
import org.heuros.pair.heuro.state.DutyState;
import org.heuros.pair.heuro.state.SolutionState;
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

	public int generateSolution(List<Pair> solution,
									SolutionState solutionState) {

		logger.info("Solution generation process is started!");

		int uncoveredLegs = 0;

		int legNdxToCover = -1;

		while (true) {
			legNdxToCover = solutionState.getNextLegNdxToCover(this.hbNdx);
			if (legNdxToCover < 0)
				break;
			Leg legToCover = this.legs.get(legNdxToCover);

			int heuristicNo = 1;

			Pair p = null;
			try {
				p = this.pairingGenerator.generatePairing(legToCover, 
															heuristicNo,
															solutionState.getDutyStates());
			} catch (CloneNotSupportedException ex) {
				logger.error(ex);
			}

			if (p != null) {
				if (p.getFirstDuty().getBriefTime(hbNdx).isBefore(HeurosDatasetParam.optPeriodEndExc)) {
					solutionState.udpateStateVectors(p);
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
//							DutyState ds = solutionState.getDutyStates()[d.getNdx()];
//							int minNumOfAlternativeDuties = Integer.MAX_VALUE;
//							int minNumOfAlternativeDutiesWoDh = Integer.MAX_VALUE;
//							int maxNumOfAlternativeDuties = 0;
//							int maxNumOfAlternativeDutiesWoDh = 0;
//							int totalNumOfAlternativeDuties = 0;
//							int totalNumOfAlternativeDutiesWoDh = 0;
//							int minNumOfAlternativeEffectiveDuties = Integer.MAX_VALUE;
//							int minNumOfAlternativeEffectiveDutiesWoDh = Integer.MAX_VALUE;
//							int maxNumOfAlternativeEffectiveDuties = 0;
//							int maxNumOfAlternativeEffectiveDutiesWoDh = 0;
//							int totalNumOfAlternativeEffectiveDuties = 0;
//							int totalNumOfAlternativeEffectiveDutiesWoDh = 0;
//							for (int i = 0; i < d.getNumOfLegs(); i++) {
//								Leg l = d.getLegs().get(i);
//								if (minNumOfAlternativeDuties > solutionState.getLegStates()[l.getNdx()].numOfIncludingDuties)
//									minNumOfAlternativeDuties = solutionState.getLegStates()[l.getNdx()].numOfIncludingDuties;
//								if (minNumOfAlternativeDutiesWoDh > solutionState.getLegStates()[l.getNdx()].numOfIncludingDutiesWoDh)
//									minNumOfAlternativeDutiesWoDh = solutionState.getLegStates()[l.getNdx()].numOfIncludingDutiesWoDh;
//								if (maxNumOfAlternativeDuties < solutionState.getLegStates()[l.getNdx()].numOfIncludingDuties)
//									maxNumOfAlternativeDuties = solutionState.getLegStates()[l.getNdx()].numOfIncludingDuties;
//								if (maxNumOfAlternativeDutiesWoDh < solutionState.getLegStates()[l.getNdx()].numOfIncludingDutiesWoDh)
//									maxNumOfAlternativeDutiesWoDh = solutionState.getLegStates()[l.getNdx()].numOfIncludingDutiesWoDh;
//								totalNumOfAlternativeDuties += solutionState.getLegStates()[l.getNdx()].numOfIncludingDuties;
//								totalNumOfAlternativeDutiesWoDh += solutionState.getLegStates()[l.getNdx()].numOfIncludingDutiesWoDh;
//
//								if (minNumOfAlternativeEffectiveDuties > solutionState.getLegStates()[l.getNdx()].numOfIncludingEffectiveDuties)
//									minNumOfAlternativeEffectiveDuties = solutionState.getLegStates()[l.getNdx()].numOfIncludingEffectiveDuties;
//								if (minNumOfAlternativeEffectiveDutiesWoDh > solutionState.getLegStates()[l.getNdx()].numOfIncludingEffectiveDutiesWoDh)
//									minNumOfAlternativeEffectiveDutiesWoDh = solutionState.getLegStates()[l.getNdx()].numOfIncludingEffectiveDutiesWoDh;
//								if (maxNumOfAlternativeEffectiveDuties < solutionState.getLegStates()[l.getNdx()].numOfIncludingEffectiveDuties)
//									maxNumOfAlternativeEffectiveDuties = solutionState.getLegStates()[l.getNdx()].numOfIncludingEffectiveDuties;
//								if (maxNumOfAlternativeEffectiveDutiesWoDh < solutionState.getLegStates()[l.getNdx()].numOfIncludingEffectiveDutiesWoDh)
//									maxNumOfAlternativeEffectiveDutiesWoDh = solutionState.getLegStates()[l.getNdx()].numOfIncludingEffectiveDutiesWoDh;
//								totalNumOfAlternativeEffectiveDuties += solutionState.getLegStates()[l.getNdx()].numOfIncludingEffectiveDuties;
//								totalNumOfAlternativeEffectiveDutiesWoDh += solutionState.getLegStates()[l.getNdx()].numOfIncludingEffectiveDutiesWoDh;
//							}
//							if (!ds.valuesAreOk(minNumOfAlternativeDuties, 
//												minNumOfAlternativeDutiesWoDh, 
//												maxNumOfAlternativeDuties, 
//												maxNumOfAlternativeDutiesWoDh, 
//												totalNumOfAlternativeDuties, 
//												totalNumOfAlternativeDutiesWoDh, 
//												minNumOfAlternativeEffectiveDuties, 
//												minNumOfAlternativeEffectiveDutiesWoDh, 
//												maxNumOfAlternativeEffectiveDuties, 
//												maxNumOfAlternativeEffectiveDutiesWoDh, 
//												totalNumOfAlternativeEffectiveDuties, 
//												totalNumOfAlternativeEffectiveDutiesWoDh)) {
//								logger.error("LegToCover: " + legToCover);
//								logger.error("DutyState values has not been set correctly for " + d);
//								ds.valuesAreOk(minNumOfAlternativeDuties, 
//												minNumOfAlternativeDutiesWoDh, 
//												maxNumOfAlternativeDuties, 
//												maxNumOfAlternativeDutiesWoDh, 
//												totalNumOfAlternativeDuties, 
//												totalNumOfAlternativeDutiesWoDh, 
//												minNumOfAlternativeEffectiveDuties, 
//												minNumOfAlternativeEffectiveDutiesWoDh, 
//												maxNumOfAlternativeEffectiveDuties, 
//												maxNumOfAlternativeEffectiveDutiesWoDh, 
//												totalNumOfAlternativeEffectiveDuties, 
//												totalNumOfAlternativeEffectiveDutiesWoDh);
//							}
//						}
//					});
					/**
					 * TEST BLOCK END
					 * 
					 */

					/**
					 * TEST BLOCK BEGIN
					 * 
					 * Checks state variables on LegState instances.
					 * 
					 */
//					this.legs.forEach((leg) -> {
//						if (leg.isCover()
//								&& leg.hasPair(this.hbNdx)
//								&& leg.getSobt().isBefore(HeurosDatasetParam.optPeriodEndExc)
////								&& (solutionState.getLegStates()[leg.getNdx()].numOfCoverings == 0)
//								) {
//							int numOfIncludingDuties = 0;
//							int numOfIncludingDutiesWoDh = 0;
//							int numOfIncludingEffectiveDuties = 0;
//							int numOfIncludingEffectiveDutiesWoDh = 0;
//
//							Duty[] dutiesOfLeg = dutyIndexByLegNdx.getArray(leg.getNdx());
//							for (Duty dutyOfLeg: dutiesOfLeg) {
//								if (dutyOfLeg.hasPairing(this.hbNdx)
//										&& dutyOfLeg.isValid(this.hbNdx)) {
//									numOfIncludingDuties++;
//									if ((dutyOfLeg.getNumOfLegsPassive() == 0)
//											&& (solutionState.getDutyStates()[dutyOfLeg.getNdx()].numOfCoverings == 0)) {
//										numOfIncludingDutiesWoDh++;
//										if (dutyOfLeg.getBlockTimeInMinsActive() >= HeurosSystemParam.effectiveDutyBlockHourLimit) {
//											numOfIncludingEffectiveDutiesWoDh++;
//										}
//									}
//									if (dutyOfLeg.getBlockTimeInMinsActive() - solutionState.getDutyStates()[dutyOfLeg.getNdx()].blockTimeOfDistinctCoveringsActive >= HeurosSystemParam.effectiveDutyBlockHourLimit) {
//										numOfIncludingEffectiveDuties++;
//									}
//								}
//							}
//
//							if (!solutionState.getLegStates()[leg.getNdx()].valuesAreOk(numOfIncludingDuties, 
//																						numOfIncludingDutiesWoDh, 
//																						numOfIncludingEffectiveDuties, 
//																						numOfIncludingEffectiveDutiesWoDh)) {
//								logger.error("LegState values has not been set correctly for " + leg);
//								solutionState.getLegStates()[leg.getNdx()].valuesAreOk(numOfIncludingDuties, 
//																						numOfIncludingDutiesWoDh, 
//																						numOfIncludingEffectiveDuties, 
//																						numOfIncludingEffectiveDutiesWoDh);
//							}
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

		return uncoveredLegs;
	}
}
