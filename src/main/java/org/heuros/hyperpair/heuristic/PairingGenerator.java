package org.heuros.hyperpair.heuristic;

import java.util.List;

import org.apache.log4j.Logger;
import org.heuros.core.data.ndx.OneDimIndexInt;
import org.heuros.data.DutyLegOvernightConnNetwork;
import org.heuros.data.model.Duty;
import org.heuros.data.model.DutyView;
import org.heuros.data.model.Leg;
import org.heuros.data.model.Pair;
import org.heuros.data.repo.DutyRepository;
import org.heuros.rule.PairRuleContext;

public class PairingGenerator {

	private static Logger logger = Logger.getLogger(PairingGenerator.class);

	/*
	 * TODO Single base assumption!!!
	 */
	private int hbNdx = 0;
//	private int maxIdleTimeInAPairInHours = 0;
	private int maxPairingLengthInDays = 0;

//	private DutyRuleContext dutyRuleContext = null;
	private PairRuleContext pairRuleContext = null;

	private OneDimIndexInt<Duty> dutyIndexByLegNdx = null;
	private DutyLegOvernightConnNetwork dutyLegOvernightConnNetwork = null;

	private List<Duty> duties = null;

	public PairingGenerator(//int maxIdleTimeInAPairInHours, 
			int maxPairingLengthInDays) {
//		this.maxIdleTimeInAPairInHours = maxIdleTimeInAPairInHours;
		this.maxPairingLengthInDays = maxPairingLengthInDays;
	}

//	public PairingGenerator setDutyRuleContext(DutyRuleContext dutyRuleContext) {
//		this.dutyRuleContext = dutyRuleContext;
//		return this;
//	}

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

	private class PairWithQuality {
		public Pair pair = null;
		public NodeQualityMetric pairQ = null;
	}

	public Pair generatePairing(Leg legToCover,
								int heuristicNo,
								int[] numOfCoveringsInDuties,
								int[] numOfDistinctCoveringsInDuties,
								int[] blockTimeOfCoveringsInDuties) throws CloneNotSupportedException {

//		logger.info("Pair search is started for the leg " + legToCover);

		Duty[] coveringDuties = this.dutyIndexByLegNdx.getArray(legToCover.getNdx());

		PairWithQuality currentPair = new PairWithQuality();
		currentPair.pair = Pair.newInstance(this.hbNdx);
		currentPair.pairQ = new NodeQualityMetric();

		PairWithQuality bestPair = new PairWithQuality();
		bestPair.pairQ = new NodeQualityMetric();

		if ((coveringDuties != null)
				&& (coveringDuties.length > 0)) {

			long startTime = System.nanoTime();

			PricingSubNetwork partialNetwork = new PricingSubNetwork(this.duties, 
																		this.maxPairingLengthInDays, 
//																		this.dutyRuleContext, 
//																		this.pairRuleContext, 
																		this.dutyLegOvernightConnNetwork)
													.build(legToCover, coveringDuties, heuristicNo, numOfCoveringsInDuties, numOfDistinctCoveringsInDuties, blockTimeOfCoveringsInDuties);

			long subNetworkBuiltTime = System.nanoTime();

//			logger.info("Subnetwork is built for the leg " + legToCover);

//			int[] sourceDuties = partialNetwork.getSourceDuties();
			DutyView[] sourceDuties = partialNetwork.getSourceDuties();
			NodeQualityMetric[] nodeQs = partialNetwork.getBestBwNodeQuality();

			for (int i = 0; i < sourceDuties.length; i++) {
//				int dNdx = sourceDuties[i];
//				DutyView d = this.duties.get(dNdx);
				DutyView d = sourceDuties[i];

				if (d.isNonHbDep(this.hbNdx))
					logger.error("Must be HB departed duty!");

				if (nodeQs[d.getNdx()].isBetterThan(heuristicNo, bestPair.pairQ)) {
		    		if (this.pairRuleContext.getStarterCheckerProxy().canBeStarter(this.hbNdx, d)) {
	
		    			currentPair.pairQ.addToQualityMetric(d, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
		    			this.pairRuleContext.getAggregatorProxy().appendFw(currentPair.pair, d);
	
						if (d.isHbArr(this.hbNdx)) {
			    			if (this.pairRuleContext.getFinalCheckerProxy().acceptable(this.hbNdx, currentPair.pair)) {
			    				if (currentPair.pair.isComplete(this.hbNdx)) {
			    					if (currentPair.pairQ.isBetterThan(heuristicNo, bestPair.pairQ)) {
			    						bestPair.pair = (Pair) currentPair.pair.clone();
			    						bestPair.pairQ.injectValues(currentPair.pairQ);
			    					}
			    				} else
			    					logger.error("Pairing " + d + " must be complete!");
			    			}
						} else {
							/*
							 * Basic cumulative quality info which is calculated during the sub network generation is checked!
							 */
							if (this.pairRuleContext.getExtensibilityCheckerProxy().isExtensible(this.hbNdx, currentPair.pair)) {
								bestPair = this.searchForPairings(heuristicNo, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, nodeQs,
																	partialNetwork, currentPair, d, bestPair, this.maxPairingLengthInDays - 1);
							}
						}
	
						currentPair.pairQ.removeFromQualityMetric(d, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
						this.pairRuleContext.getAggregatorProxy().removeLast(currentPair.pair);
		    		}
				}
			}

			long searchCompletionTime = System.nanoTime();

			logger.info(((subNetworkBuiltTime - startTime) / 1000000) + 
							"(Roots" + coveringDuties.length + 
							", Recs" + partialNetwork.getNumOfRecursions() + 
							", #(" + partialNetwork.getNumOfNodes() + 
							"/" + partialNetwork.getNumOfNodesChecked() + 
							"/" + partialNetwork.getNumOfNodesAdded() + 
							"), Fw(Recs" + partialNetwork.getNumOfFwRecursions() +
							", Dept" + (this.maxPairingLengthInDays - partialNetwork.getMaxFwDeptReached()) +
							", " + partialNetwork.getNumOfFwNodes() +
							"/" + partialNetwork.getNumOfFwNodesChecked() +
							"/" + partialNetwork.getNumOfFwNodesAdded() +
							"), Bw(Recs" + partialNetwork.getNumOfBwRecursions() +
							", Dept" + (this.maxPairingLengthInDays - partialNetwork.getMaxBwDeptReached()) +
							", " + partialNetwork.getNumOfBwNodes() +
							"/" + partialNetwork.getNumOfBwNodesChecked() +
							"/" + partialNetwork.getNumOfBwNodesAdded() +
							"))->" + 
						((searchCompletionTime - subNetworkBuiltTime) / 1000000) + 
							"(NetSrc" + sourceDuties.length + 
							") - " + bestPair.pair.getNumOfDuties() + "d pair is generated for the leg " + legToCover);
		}

		return bestPair.pair;
	}

	private PairWithQuality searchForPairings(int heuristicNo,
												int[] numOfCoveringsInDuties,
												int[] blockTimeOfCoveringsInDuties,
												NodeQualityMetric[] nodeQs,
												PricingSubNetwork partialNetwork,
												PairWithQuality currentPair, DutyView ld,
												PairWithQuality bestPair, int dept) throws CloneNotSupportedException {
		PairWithQuality res = bestPair;

//		int[] nextDuties = partialNetwork.getNextDuties(ld);
		DutyView[] nextDuties = partialNetwork.getNextDuties(ld);

		for (int i = 0; i < nextDuties.length; i++) {
//			int ndNdx = nextDuties[i];
//			DutyView nd = this.duties.get(ndNdx);
			DutyView nd = nextDuties[i];

			/*
			 * Basic cumulative quality info which is calculated during the sub network generation is checked! 
			 */
			NodeQualityMetric ndQ = nodeQs[nd.getNdx()];
			ndQ.addToQualityMetric(currentPair.pairQ);
			if (ndQ.isBetterThan(heuristicNo, res.pairQ)) {
				ndQ.removeFromQualityMetric(currentPair.pairQ);

				if (this.pairRuleContext.getAppendabilityCheckerProxy().isAppendable(this.hbNdx, currentPair.pair, nd, true)) {

					currentPair.pairQ.addToQualityMetric(nd, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
					pairRuleContext.getAggregatorProxy().appendFw(currentPair.pair, nd);
					if (nd.isHbArr(this.hbNdx)) {
						if (this.pairRuleContext.getFinalCheckerProxy().acceptable(this.hbNdx, currentPair.pair)) {
		    				if (currentPair.pair.isComplete(this.hbNdx)) {
		    					if (currentPair.pairQ.isBetterThan(heuristicNo, res.pairQ)) {
		    						res.pair = (Pair) currentPair.pair.clone();
		    						res.pairQ.injectValues(currentPair.pairQ);
		    					}
		    				} else
		    					logger.error("Pairing " + currentPair.pair + " must be complete!");
						}
					} else
						if (nd.isNonHbArr(this.hbNdx)
								&& (dept > 1)
								&& this.pairRuleContext.getExtensibilityCheckerProxy().isExtensible(this.hbNdx, currentPair.pair)) {
							res = this.searchForPairings(heuristicNo, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, nodeQs,
															partialNetwork, currentPair, nd, res, dept - 1);
						}
					currentPair.pairQ.removeFromQualityMetric(nd, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
					pairRuleContext.getAggregatorProxy().removeLast(currentPair.pair);
				}
			} else
				ndQ.removeFromQualityMetric(currentPair.pairQ);
		}
		return res;
	}
}
