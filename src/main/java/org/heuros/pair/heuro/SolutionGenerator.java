package org.heuros.pair.heuro;

import java.util.List;

import org.apache.log4j.Logger;
import org.heuros.data.model.Leg;
import org.heuros.data.model.Pair;
import org.heuros.pair.conf.HeurosDatasetParam;
import org.heuros.pair.heuro.state.SolutionState;
import org.heuros.pair.sp.PairingGenerator;

public class SolutionGenerator {

	private static Logger logger = Logger.getLogger(SolutionGenerator.class);

	/*
	 * TODO Single base assumption!!!
	 */
	private int hbNdx = 0;

	private List<Leg> legs = null;
//	private List<Duty> duties = null;
//	private OneDimIndexInt<Duty> dutyIndexByLegNdx = null;
	private PairingGenerator pairingGenerator = null;

	public SolutionGenerator(List<Leg> legs,
//								List<Duty> duties,
//								OneDimIndexInt<Duty> dutyIndexByLegNdx,
								PairingGenerator pairingGenerator) {
		this.legs = legs;
//		this.duties = duties;
//		this.dutyIndexByLegNdx = dutyIndexByLegNdx;
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

		return uncoveredLegs;
	}
}
