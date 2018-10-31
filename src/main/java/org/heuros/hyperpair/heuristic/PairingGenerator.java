package org.heuros.hyperpair.heuristic;

import java.util.List;

import org.apache.log4j.Logger;
import org.heuros.core.data.ndx.OneDimIndexInt;
import org.heuros.data.DutyLegOvernightConnNetwork;
import org.heuros.data.model.Duty;
import org.heuros.data.model.DutyView;
import org.heuros.data.model.Leg;
import org.heuros.data.model.LegView;
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
	private DutyLegOvernightConnNetwork dutyLegOvernightConnNetwork = null;

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

	public PairingGenerator setPricingNetwork(DutyLegOvernightConnNetwork dutyLegOvernightConnNetwork) {
		this.dutyLegOvernightConnNetwork = dutyLegOvernightConnNetwork;
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

		Pair p = Pair.newInstance(this.hbNdx);

		QualityMetric[] bestSoFar = new QualityMetric[this.duties.size()];
		int[] maxSearchDeptSoFar = new int[this.duties.size()];

		QualityMetric bestQm = new QualityMetric();

		if ((coveringDuties != null)
				&& (coveringDuties.length > 0)) {
			PricingSubNetwork partialNetwork = new PricingSubNetwork(this.duties, 
																		this.maxPairingLengthInDays, 
																		this.dutyRuleContext, 
																		this.pairRuleContext, 
																		this.dutyLegOvernightConnNetwork)
													.build(heuristicNo, coveringDuties);

			int[] sourceDuties = partialNetwork.getSourceDuties();

			for (int i = 0; i < sourceDuties.length; i++) {
				int dNdx = sourceDuties[i];
				DutyView d = this.duties.get(dNdx);

				if (d.isNonHbDep(this.hbNdx))
					logger.error("Must be HB departed duty!");

	    		if (this.pairRuleContext.getStarterCheckerProxy().canBeStarter(this.hbNdx, d)) {
	    			this.pairRuleContext.getAggregatorProxy().appendFw(p, d);

					QualityMetric dqm = QualityMetric.calculateQualityMetric(d, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);

					if (d.isHbArr(this.hbNdx)) {
		    			if (this.pairRuleContext.getFinalCheckerProxy().acceptable(this.hbNdx, p)) {
		    				if (p.isComplete(this.hbNdx)) {
		    					if (dqm.isBetterThan(heuristicNo, bestQm))
		    						bestQm.injectValues(dqm);
		    					this.checkAndSetDutyContributionStatus(heuristicNo, d, dqm, 0, bestSoFar, maxSearchDeptSoFar);
		    				} else
		    					logger.error("Pairing " + d + " must be complete!");
		    			}
					} else {
						bestQm = this.searchForPairings(heuristicNo, partialNetwork, bestQm, p, d, dqm, this.maxPairingLengthInDays - 1);
					}
					this.pairRuleContext.getAggregatorProxy().removeLast(p);
	    		}
			}
		}
		return null;
	}

	private QualityMetric searchForPairings(int heuristicNo, 
											PartialPairingPricingNetwork partialNetwork, QualityMetric bestQmSoFar, 
											Pair p, DutyView ld, QualityMetric cqm, int dept) {

		int[] nextDuties = partialNetwork.getNextDuties(ld);

		for (int i = 0; i < nextDuties.length; i++) {
			int ndNdx = nextDuties[i];
			DutyView nd = this.duties.get(ndNdx);
			if (this.dutyRuleContext.getConnectionCheckerProxy().areConnectable(this.hbNdx, ld, nd)) {
				if (this.pairRuleContext.getAppendabilityCheckerProxy().isAppendable(this.hbNdx, p, nd, true)) {
					pairRuleContext.getAggregatorProxy().appendFw(p, nd);
					if (nd.isHbArr(this.hbNdx)) {
						if (this.pairRuleContext.getFinalCheckerProxy().acceptable(this.hbNdx, p)) {

						}
					} else
						if (nd.isNonHbArr(this.hbNdx)
								&& (dept > 1)
								&& this.pairRuleContext.getExtensibilityCheckerProxy().isExtensible(this.hbNdx, p)) {
							if (this.examinePairFW(p, fd, nd, fl, nd.getLastLeg(), true, false, dept - 1)) {
								
							}
						}
					pairRuleContext.getAggregatorProxy().removeLast(p);
				}
			}
		});
	}

	private void checkAndSetDutyContributionStatus(int heuristicNo,
													DutyView d,
													QualityMetric qm,
													int dept,
													QualityMetric[] bestSoFar,
													int[] maxSearchDeptSoFar) {
		if ((bestSoFar[d.getNdx()] == null)
				|| (qm.isBetterThan(heuristicNo, bestSoFar[d.getNdx()]))) {
			bestSoFar[d.getNdx()] = qm;
		}
		if (maxSearchDeptSoFar[d.getNdx()] < dept) {
			maxSearchDeptSoFar[d.getNdx()] = dept;
		}
	}
}
