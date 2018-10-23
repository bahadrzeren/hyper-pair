package org.heuros.hyperpair.heuristic;

import java.util.List;

import org.apache.log4j.Logger;
import org.heuros.core.data.ndx.OneDimIndexInt;
import org.heuros.data.PairingPricingNetwork;
import org.heuros.data.PartialPairingPricingNetwork;
import org.heuros.data.model.Duty;
import org.heuros.data.model.DutyView;
import org.heuros.data.model.Leg;
import org.heuros.data.model.Pair;
import org.heuros.data.repo.DutyRepository;
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

	private List<Duty> duties = null;

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

	public PairingGenerator setDutyRepository(DutyRepository dutyRepository) {
		this.duties = dutyRepository.getModels();
		return this;
	}

	public Pair generatePairing(Leg legToCover,
								int heuristicNo,
								int[] numOfCoveringsInDuties,
								int[] blockTimeOfCoveringsInDuties) {

		Duty[] coveringDuties = this.dutyIndexByLegNdx.getArray(legToCover.getNdx());

		Pair pair = Pair.newInstance(this.hbNdx);
		QualityMetric bestValuesSoFar = new QualityMetric();

		if ((coveringDuties != null)
				&& (coveringDuties.length > 0)) {
			PartialPairingPricingNetwork partialNetwork = this.dutyNetwork.generatePartialNetwork(heuristicNo, coveringDuties);

			partialNetwork.getSourceDuties().forEach(sdNdx -> {
				DutyView sd = this.duties.get(sdNdx);
				this.pairRuleContext.getAggregatorProxy().appendFw(pair, sd);
				this.searchForPairings(pair);
			});
		}
		return null;
	}

	private void searchForPairings(Pair p, DutyView ld) {
		
	}
}
