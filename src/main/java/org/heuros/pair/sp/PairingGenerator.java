package org.heuros.pair.sp;

import java.util.List;

import org.apache.log4j.Logger;
import org.heuros.core.data.ndx.OneDimIndexInt;
import org.heuros.data.DutyLegOvernightConnNetwork;
import org.heuros.data.model.Duty;
import org.heuros.data.model.Leg;
import org.heuros.data.model.Pair;
import org.heuros.data.repo.DutyRepository;
import org.heuros.pair.heuro.DutyParam;
import org.heuros.rule.PairRuleContext;

public class PairingGenerator {

	private static Logger logger = Logger.getLogger(PairingGenerator.class);

	/*
	 * TODO Single base assumption!!!
	 */
	private int hbNdx = 0;
	private int maxPairingLengthInDays = 0;
	private int maxDutyBlockTimeInMins = 0;

	private PairRuleContext pairRuleContext = null;

	private OneDimIndexInt<Duty> dutyIndexByLegNdx = null;
	private DutyLegOvernightConnNetwork dutyLegOvernightConnNetwork = null;

	private List<Duty> duties = null;

	public PairingGenerator(int maxPairingLengthInDays,
							int maxDutyBlockTimeInMins) {
		this.maxPairingLengthInDays = maxPairingLengthInDays;
		this.maxDutyBlockTimeInMins = maxDutyBlockTimeInMins;
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

	private class PairWithQuality {
		public Pair pair = null;
		public QualityMetric pairQ = null;
	}

//	private Random random = new Random();

	public Pair generatePairing(Leg legToCover,
								int heuristicNo,
								DutyParam[] dutyParams) throws CloneNotSupportedException {

		Duty[] coveringDuties = this.dutyIndexByLegNdx.getArray(legToCover.getNdx());

		PairWithQuality currentPair = new PairWithQuality();
		currentPair.pair = Pair.newInstance(this.hbNdx);
		currentPair.pairQ = new QualityMetric();

		PairWithQuality bestPair = new PairWithQuality();
		bestPair.pairQ = new QualityMetric();

		if ((coveringDuties != null)
				&& (coveringDuties.length > 0)) {

//			long startTime = System.nanoTime();

if (legToCover.getNdx() == 1650)
System.out.println();

			NetworkExplorer networkExplorer = new NetworkExplorer(this.duties, 
																		this.maxPairingLengthInDays, 
																		this.maxDutyBlockTimeInMins,
																		this.dutyLegOvernightConnNetwork)
													.build(legToCover, 
															coveringDuties, 
															heuristicNo, 
															dutyParams);

//			long subNetworkBuiltTime = System.nanoTime();

//if (legToCover.getNdx() == 4284)
//System.out.println();

			NodeQualityMetric[] sourceDutyNodes = networkExplorer.getSourceNodeQms();

			/**
			 * TEST BLOCK BEGIN
			 * 
			 * Checks QualityMetric TOTALIZERS.
			 * 
			 */
			for (int k = 0; k < sourceDutyNodes.length; k++) {
				NodeQualityMetric nodeQualityMetric = sourceDutyNodes[k];
				QualityMetric qualityMetric = new QualityMetric(nodeQualityMetric.getNodeOwner(), dutyParams[nodeQualityMetric.getNodeOwner().getNdx()]);
				while (nodeQualityMetric.getNextNodeMetric() != null) {
					nodeQualityMetric = nodeQualityMetric.getNextNodeMetric();
					qualityMetric.addToQualityMetric(nodeQualityMetric.getNodeOwner(), dutyParams[nodeQualityMetric.getNodeOwner().getNdx()]);
				}
				nodeQualityMetric = sourceDutyNodes[k];
				if (!nodeQualityMetric.getQual().isTheSame(qualityMetric)) {
					logger.error("There is difference between shortest path quality found and post calculated quality!");
					logger.error(legToCover);
					logger.error(qualityMetric);
					logger.error(nodeQualityMetric);
				}
			}
			/**
			 * TEST BLOCK END
			 */

			for (int i = 0; i < sourceDutyNodes.length; i++) {
				int j = i;

				NodeQualityMetric nqm = sourceDutyNodes[j];

				if (nqm.getQual().isBetterThan(heuristicNo, bestPair.pairQ)) {

					Duty d = nqm.getNodeOwner();

					if (d.isNonHbDep(this.hbNdx))
						logger.error("Must be HB departed duty!");

		    		if (this.pairRuleContext.getStarterCheckerProxy().canBeStarter(this.hbNdx, d)) {

		    			currentPair.pairQ.addToQualityMetric(d, dutyParams[d.getNdx()]);
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

								int dept = this.maxPairingLengthInDays - 1;
								while (nqm.getNextNodeMetric() != null) {
									nqm = nqm.getNextNodeMetric();
									Duty nd = nqm.getNodeOwner();

									if (this.pairRuleContext.getAppendabilityCheckerProxy().isAppendable(this.hbNdx, currentPair.pair, nd, true)) {
										currentPair.pairQ.addToQualityMetric(nd, dutyParams[nd.getNdx()]);
										pairRuleContext.getAggregatorProxy().appendFw(currentPair.pair, nd);
										if (nd.isHbArr(this.hbNdx)) {
											if (this.pairRuleContext.getFinalCheckerProxy().acceptable(this.hbNdx, currentPair.pair)) {
							    				if (currentPair.pair.isComplete(this.hbNdx)) {
							    					if (currentPair.pairQ.isBetterThan(heuristicNo, bestPair.pairQ)) {
							    						bestPair.pair = (Pair) currentPair.pair.clone();
							    						bestPair.pairQ.injectValues(currentPair.pairQ);
							    						break;
							    					}
							    				} else
							    					logger.error("Pairing " + currentPair.pair + " must be complete!");
											}
										} else
											if (!(nd.isNonHbArr(this.hbNdx)
													&& (dept > 1)
													&& this.pairRuleContext.getExtensibilityCheckerProxy().isExtensible(this.hbNdx, currentPair.pair))) {
												break;
											}
									}
								}
							}
						}

//	    				if (!currentPair.pair.isComplete(this.hbNdx))
//	    					logger.error("Pairing " + currentPair.pair + " must be complete!");

						currentPair.pairQ.reset();
						this.pairRuleContext.getAggregatorProxy().removeAll(currentPair.pair);
		    		}
				}
			}

//			long searchCompletionTime = System.nanoTime();
//
//			logger.info("Heur" + heuristicNo + ", " + 
//							bestPair.pair.getNumOfDuties() + "d, " + bestPair.pair.getNumOfLegs() + "l, Bt" + bestPair.pair.getBlockTimeInMins() +
//							", LNdx" + legToCover.getNdx() + 
//							", " + ((subNetworkBuiltTime - startTime) / 1000000) +
//							"(Roots" + coveringDuties.length + 
//							", Recs" + networkExplorer.getNumOfRecursions() + 
//							", #(" + networkExplorer.getNumOfNodes() + 
//							"/" + networkExplorer.getNumOfNodesChecked() + 
//							"/" + networkExplorer.getNumOfNodesAdded() + 
//							"), Fw(Recs" + networkExplorer.getNumOfFwRecursions() +
//							", Dept" + (this.maxPairingLengthInDays - networkExplorer.getMaxFwDeptReached()) +
//							", " + networkExplorer.getNumOfFwNodes() +
//							"/" + networkExplorer.getNumOfFwNodesChecked() +
//							"/" + networkExplorer.getNumOfFwNodesAdded() +
//							"), Bw(Recs" + networkExplorer.getNumOfBwRecursions() +
//							", Dept" + (this.maxPairingLengthInDays - networkExplorer.getMaxBwDeptReached()) +
//							", " + networkExplorer.getNumOfBwNodes() +
//							"/" + networkExplorer.getNumOfBwNodesChecked() +
//							"/" + networkExplorer.getNumOfBwNodesAdded() +
//							"))->" + 
//						((searchCompletionTime - subNetworkBuiltTime) / 1000000) + 
//							"(NetSrc" + sourceDutyNodes.length + 
//							") - " + bestPair.pair.getNumOfDuties() + "d pair is generated for the leg " + legToCover);
		}

		return bestPair.pair;
	}

//	private boolean validatePath(int heuristicNo,
//			int[] numOfCoveringsInDuties,
//			int[] numOfDistinctCoveringsInDuties,
//			int[] blockTimeOfCoveringsInDuties,
//			NodeQualityMetric nqm,
//			PairWithQuality currentPair,
//			PairWithQuality bestPair) throws CloneNotSupportedException {
//	}

}
