package org.heuros.pair.sp;

import java.util.List;

import org.apache.log4j.Logger;
import org.heuros.core.data.ndx.OneDimIndexInt;
import org.heuros.data.DutyLegOvernightConnNetwork;
import org.heuros.data.model.Duty;
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
								int[] numOfCoveringsInDuties,
								int[] numOfDistinctCoveringsInDuties,
								int[] blockTimeOfCoveringsInDuties,
								int[] dutyPriorities) throws CloneNotSupportedException {

		Duty[] coveringDuties = this.dutyIndexByLegNdx.getArray(legToCover.getNdx());

		PairWithQuality currentPair = new PairWithQuality();
		currentPair.pair = Pair.newInstance(this.hbNdx);
		currentPair.pairQ = new QualityMetric();

		PairWithQuality bestPair = new PairWithQuality();
		bestPair.pairQ = new QualityMetric();

		if ((coveringDuties != null)
				&& (coveringDuties.length > 0)) {

//			long startTime = System.nanoTime();

			NetworkExplorer networkExplorer = new NetworkExplorer(this.duties, 
																		this.maxPairingLengthInDays, 
																		this.maxDutyBlockTimeInMins,
																		this.dutyLegOvernightConnNetwork)
													.build(legToCover, 
															coveringDuties, 
															heuristicNo, 
															numOfCoveringsInDuties, 
															numOfDistinctCoveringsInDuties, 
															blockTimeOfCoveringsInDuties, 
															dutyPriorities);

//			long subNetworkBuiltTime = System.nanoTime();

//if (legToCover.getNdx() == 4284)
//System.out.println();

			NodeQualityMetric[] sourceDutyNodes = networkExplorer.getSourceNodeQms();

//int bitC = 0;
//int maxLen = sourceDutyNodes.length;
//if (maxLen > 3) maxLen = 3;

			for (int i = 0; i < sourceDutyNodes.length; i++) {
				int j = i;

//if (randomize) {
//	if (bestPair.pair != null)
//		break;
//	
//	j = this.random.nextInt(maxLen);
//	if (bitC == ((1 << maxLen) - 1)) {
//		j = i;
//	} else {
//		while ((bitC & (1 << i)) > 0)
//			i = this.random.nextInt(maxLen);
//		bitC = bitC | (1 << i);
//	}
//}

				NodeQualityMetric nqm = sourceDutyNodes[j];

				if (nqm.getQual().isBetterThan(heuristicNo, bestPair.pairQ)) {

					Duty d = nqm.getNodeOwner();

					if (d.isNonHbDep(this.hbNdx))
						logger.error("Must be HB departed duty!");

		    		if (this.pairRuleContext.getStarterCheckerProxy().canBeStarter(this.hbNdx, d)) {

		    			currentPair.pairQ.addToQualityMetric(d, numOfCoveringsInDuties[d.getNdx()], blockTimeOfCoveringsInDuties[d.getNdx()], dutyPriorities[d.getNdx()]);
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
										currentPair.pairQ.addToQualityMetric(nd, numOfCoveringsInDuties[nd.getNdx()], blockTimeOfCoveringsInDuties[nd.getNdx()], dutyPriorities[nd.getNdx()]);
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
