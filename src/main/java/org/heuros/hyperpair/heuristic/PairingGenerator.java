package org.heuros.hyperpair.heuristic;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.heuros.core.data.ndx.OneDimIndexInt;
import org.heuros.core.data.ndx.TwoDimIndexIntXInt;
import org.heuros.core.data.ndx.TwoDimIndexIntXLocalDateTime;
import org.heuros.data.PairingPricingNetwork;
import org.heuros.data.model.Duty;
import org.heuros.data.model.DutyView;
import org.heuros.data.model.Leg;
import org.heuros.data.model.LegView;
import org.heuros.data.model.Pair;
import org.heuros.rule.DutyRuleContext;
import org.heuros.rule.PairRuleContext;

public class PairingGenerator {

	private static Logger logger = Logger.getLogger(PairingGenerator.class);

	/*
	 * TODO Single base assumption!!!
	 */
	private int hbNdx = 0;
	private int maxIdleTimeInAPairInHours = 0;
	private int maxPairingLengthInDays = 0;

	private DutyRuleContext dutyRuleContext = null;
	private PairRuleContext pairRuleContext = null;

	private OneDimIndexInt<Duty> dutyIndexByLegNdx = null;
	private PairingPricingNetwork dutyNetwork = null;

	public PairingGenerator(int maxIdleTimeInAPairInHours, int maxPairingLengthInDays) {
		this.maxIdleTimeInAPairInHours = maxIdleTimeInAPairInHours;
		this.maxPairingLengthInDays = maxPairingLengthInDays;
	}

	public PairingGenerator setDutyRuleContext(DutyRuleContext dutyRuleContext) {
		this.dutyRuleContext = dutyRuleContext;
		return this;
	}

	public PairingGenerator setPairRuleContext(PairRuleContext pairRuleContext) {
		this.pairRuleContext = pairRuleContext;
		return this;
	}

	public PairingGenerator setDutyIndexByLegNdx(OneDimIndexInt<Duty> dutyIndexByLegNdx) {
		this.dutyIndexByLegNdx = dutyIndexByLegNdx;
		return this;
	}

	public PairingGenerator setPairingPricingNetwork(PairingPricingNetwork dutyNetwork) {
		this.dutyNetwork = dutyNetwork;
		return this;
	}

	private 

	public Pair generatePairing(Leg legToCover,
								int heuristicNo,
								int[] numOfCoveringsInDuties,
								int[] blockTimeOfCoveringsInDuties) {
		Duty[] duties = this.dutyIndexByLegNdx.getArray(legToCover.getNdx());

		Pair pair = Pair.newInstance(this.hbNdx);
		QualityMetric bestValuesSoFar = new QualityMetric();

		if ((duties != null)
				&& (duties.length > 0)) {

			for (Duty duty: duties) {

				LegView fl = duty.getFirstLeg();
				LegView ll = duty.getLastLeg();

				if (duty.isHbDep(this.hbNdx)) {
					if (duty.isHbArr(this.hbNdx)) {
			    		if (this.pairRuleContext.getStarterCheckerProxy().canBeStarter(this.hbNdx, duty)) {
				    		if (this.pairRuleContext.getAppendabilityCheckerProxy().isAppendable(this.hbNdx, pair, duty, true)) {
				    			this.pairRuleContext.getAggregatorProxy().appendFw(pair, duty);
				    			if (this.pairRuleContext.getFinalCheckerProxy().acceptable(this.hbNdx, pair)) {
				    				if (pair.isComplete(this.hbNdx)) {
				    					/*
				    					 * Set related leg flags!
				    					 */
				    					
				    				} else
				    					logger.error("Pairing " + d + " must be complete!");
				    			}
				    			this.pairRuleContext.getAggregatorProxy().removeLast(p);
				    		}
			    		}
					} else {
//						bestSoFar = this.fwNetworkSearch(duty, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
					}
				} else
					if (heuristicNo > 0) {
						if (duty.isHbArr(this.hbNdx)) {
							
						} else {
							
						}
					}
			}
		}
		return null;
	}

	private QualityMetric fwNetworkSearch(Pair p,
											QualityMetric bestSoFar,
											Duty fd, 
											Duty ld, 
											LegView fl, 
											LegView ll, 
											boolean hbDep, 
											boolean hbArr, 
											int dept,
											int[] numOfCoveringsInDuties,
											int[] blockTimeOfCoveringsInDuties) {
		LegView[] nextLegs = this.dutyNetwork.getNextBriefLegIndexByDutyNdx().getArray(duty.getNdx());
		for (LegView leg : nextLegs) {
			DutyView[] nextDuties = this.dutyNetwork.getDutyIndexByDepLegNdx().getArray(leg.getNdx());
			nextDuties.
		}
	}
}
