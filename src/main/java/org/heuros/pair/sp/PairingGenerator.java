package org.heuros.pair.sp;

import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.heuros.context.PairOptimizationContext;
import org.heuros.core.data.ndx.OneDimIndexInt;
import org.heuros.data.DutyLegOvernightConnNetwork;
import org.heuros.data.model.Duty;
import org.heuros.data.model.Leg;
import org.heuros.data.model.Pair;
import org.heuros.pair.conf.HeurosSystemParam;
import org.heuros.pair.heuro.state.DutyState;
import org.heuros.rule.PairRuleContext;

public class PairingGenerator implements Callable<PairWithQuality[]> {

	private static Logger logger = Logger.getLogger(PairingGenerator.class);

	/*
	 * TODO Single base assumption!!!
	 */
	private int hbNdx = 0;

	private PairRuleContext pairRuleContext = null;

	private List<Duty> duties = null;
	private OneDimIndexInt<Duty> dutyIndexByLegNdx = null;

	private DutyLegOvernightConnNetwork dutyLegOvernightConnNetwork = null;

	private PairWithQuality currentPair = new PairWithQuality();
	private PairWithQuality[] bestPairs = new PairWithQuality[HeurosSystemParam.maxPairingLengthInDays];

	public PairingGenerator(PairOptimizationContext pairOptimizationContext,
							DutyLegOvernightConnNetwork dutyLegOvernightConnNetwork) {
		this.pairRuleContext = pairOptimizationContext.getPairRuleContext();
		this.dutyIndexByLegNdx = pairOptimizationContext.getDutyIndexByLegNdx();
		this.duties = pairOptimizationContext.getDutyRepository().getModels();

		this.dutyLegOvernightConnNetwork = dutyLegOvernightConnNetwork;

		currentPair.p = Pair.newInstance(this.hbNdx);
		currentPair.qm = new QualityMetric();
		for (int i = 0; i < bestPairs.length; i++) {
			PairWithQuality pairWithQuality = new PairWithQuality();
			pairWithQuality.qm = new QualityMetric();
			bestPairs[i] = pairWithQuality;
		}
	}

	private Leg legToCover = null;
	private DutyState[] dutyStates = null;

	public void setLegForPairGeneration(Leg legToCover, DutyState[] dutyStates) {
		this.legToCover = legToCover;
		this.dutyStates = dutyStates;
	}

	@Override
	public PairWithQuality[] call() throws Exception, CloneNotSupportedException {

		Duty[] coveringDuties = this.dutyIndexByLegNdx.getArray(legToCover.getNdx());

		for (int i = 0; i < bestPairs.length; i++) {
			bestPairs[i].legToCover = this.legToCover;
			bestPairs[i].p = null;
			bestPairs[i].qm.reset();
		}

		if ((coveringDuties != null)
				&& (coveringDuties.length > 0)) {

//			long startTime = System.nanoTime();

//if (legToCover.getNdx() == 5831)
//System.out.println();

			NetworkExplorer networkExplorer = new NetworkExplorer(this.duties, this.dutyLegOvernightConnNetwork)
																.build(legToCover, 
																		coveringDuties, 
																		dutyStates);

//			long subNetworkBuiltTime = System.nanoTime();

			NodeQualityMetric[] sourceDutyNodes = networkExplorer.getSourceNodeQms();

			/**
			 * TEST BLOCK BEGIN
			 * 
			 * Checks QualityMetric TOTALIZERS.
			 * 
			 */
			int failedNodeNdxTot = Integer.MAX_VALUE;
//			for (int k = 0; k < sourceDutyNodes.length; k++) {
//				NodeQualityMetric nodeQualityMetric = sourceDutyNodes[k];
//				QualityMetric qualityMetric = new QualityMetric(nodeQualityMetric.getParent().getNodeOwner(), dutyParams[nodeQualityMetric.getParent().getNodeOwner().getNdx()]);
//				while (nodeQualityMetric.getNextNodeMetric() != null) {
//					nodeQualityMetric = nodeQualityMetric.getNextNodeMetric();
//					qualityMetric.addToQualityMetricFw(nodeQualityMetric.getParent().getNodeOwner(), dutyParams[nodeQualityMetric.getParent().getNodeOwner().getNdx()]);
//				}
//				nodeQualityMetric = sourceDutyNodes[k];
//				if (!nodeQualityMetric.getQual().isTheSame(qualityMetric)) {
////					logger.error("There is difference between shortest path quality found and post calculated quality!");
////					logger.error(legToCover);
////					logger.error(qualityMetric);
////					logger.error(nodeQualityMetric);
//					failedNodeNdxTot = k;
//					break;
//				}
//			}
			/**
			 * TEST BLOCK END
			 */

			/**
			 * TEST BLOCK BEGIN
			 * 
			 * Checks QualityMetric Alternative Duty w/wo DH TOTALIZERS.
			 * 
			 */
			int failedNodeNdxAltD = Integer.MAX_VALUE;

//			for (int k = 0; k < sourceDutyNodes.length; k++) {
//				int numOfDh = 0;
//				int dhDurationInMins = 0;
//				int activeBlocktimeInMins = 0;
//				int numOfDuties = 0;
//				int numOfLegs = 0;
//				int minNumOfAlternativeDuties = Integer.MAX_VALUE;
//				int minNumOfAlternativeDutiesWoDh = Integer.MAX_VALUE;
//				int maxNumOfAlternativeDuties = 0;
//				int maxNumOfAlternativeDutiesWoDh = 0;
//				int totalNumOfAlternativeDuties = 0;
//				int totalNumOfAlternativeDutiesWoDh = 0;
//				int minNumOfAlternativeEffectiveDuties = Integer.MAX_VALUE;
//				int minNumOfAlternativeEffectiveDutiesWoDh = Integer.MAX_VALUE;
//				int maxNumOfAlternativeEffectiveDuties = 0;
//				int maxNumOfAlternativeEffectiveDutiesWoDh = 0;
//				int totalNumOfAlternativeEffectiveDuties = 0;
//				int totalNumOfAlternativeEffectiveDutiesWoDh = 0;
//				double difficultyScore = 0.0;
//
//				NodeQualityMetric nodeQualityMetric = sourceDutyNodes[k];
//
//				Duty d = nodeQualityMetric.getParent().getNodeOwner();
//
//				QualityMetric qualityMetric = new QualityMetric(nodeQualityMetric.getParent().getNodeOwner(), dutyStates[d.getNdx()]);
//
//				numOfDh += d.getNumOfLegsPassive() + dutyStates[d.getNdx()].numOfCoverings;
//				dhDurationInMins += d.getBlockTimeInMinsPassive() + dutyStates[d.getNdx()].blockTimeOfCoverings;
//				activeBlocktimeInMins += d.getBlockTimeInMinsActive() - dutyStates[d.getNdx()].blockTimeOfCoveringsActive;
//				numOfDuties++;
//				numOfLegs += d.getNumOfLegs();
//
//				if (minNumOfAlternativeDuties > dutyStates[d.getNdx()].minNumOfAlternativeDuties)
//					minNumOfAlternativeDuties = dutyStates[d.getNdx()].minNumOfAlternativeDuties;
//				if (minNumOfAlternativeDutiesWoDh > dutyStates[d.getNdx()].minNumOfAlternativeDutiesWoDh)
//					minNumOfAlternativeDutiesWoDh = dutyStates[d.getNdx()].minNumOfAlternativeDutiesWoDh;
////				if (maxNumOfAlternativeDuties < dutyStates[d.getNdx()].maxNumOfAlternativeDuties)
////					maxNumOfAlternativeDuties = dutyStates[d.getNdx()].maxNumOfAlternativeDuties;
////				if (maxNumOfAlternativeDutiesWoDh < dutyStates[d.getNdx()].maxNumOfAlternativeDutiesWoDh)
////					maxNumOfAlternativeDutiesWoDh = dutyStates[d.getNdx()].maxNumOfAlternativeDutiesWoDh;
//				totalNumOfAlternativeDuties += dutyStates[d.getNdx()].totalNumOfAlternativeDuties;
//				totalNumOfAlternativeDutiesWoDh += dutyStates[d.getNdx()].totalNumOfAlternativeDutiesWoDh;
//
//				if (minNumOfAlternativeEffectiveDuties > dutyStates[d.getNdx()].minNumOfAlternativeEffectiveDuties)
//					minNumOfAlternativeEffectiveDuties = dutyStates[d.getNdx()].minNumOfAlternativeEffectiveDuties;
//				if (minNumOfAlternativeEffectiveDutiesWoDh > dutyStates[d.getNdx()].minNumOfAlternativeEffectiveDutiesWoDh)
//					minNumOfAlternativeEffectiveDutiesWoDh = dutyStates[d.getNdx()].minNumOfAlternativeEffectiveDutiesWoDh;
////				if (maxNumOfAlternativeEffectiveDuties < dutyStates[d.getNdx()].maxNumOfAlternativeEffectiveDuties)
////					maxNumOfAlternativeEffectiveDuties = dutyStates[d.getNdx()].maxNumOfAlternativeEffectiveDuties;
////				if (maxNumOfAlternativeEffectiveDutiesWoDh < dutyStates[d.getNdx()].maxNumOfAlternativeEffectiveDutiesWoDh)
////					maxNumOfAlternativeEffectiveDutiesWoDh = dutyStates[d.getNdx()].maxNumOfAlternativeEffectiveDutiesWoDh;
//				totalNumOfAlternativeEffectiveDuties += dutyStates[d.getNdx()].totalNumOfAlternativeEffectiveDuties;
//				totalNumOfAlternativeEffectiveDutiesWoDh += dutyStates[d.getNdx()].totalNumOfAlternativeEffectiveDutiesWoDh;
//
//				difficultyScore += dutyStates[d.getNdx()].getDifficultyScore();
//
//				while (nodeQualityMetric.getNextNodeMetric() != null) {
//					nodeQualityMetric = nodeQualityMetric.getNextNodeMetric();
//
//					d = nodeQualityMetric.getParent().getNodeOwner();
//
//					qualityMetric.addToQualityMetricFw(nodeQualityMetric.getParent().getNodeOwner(), dutyStates[d.getNdx()]);
//
//					numOfDh += d.getNumOfLegsPassive() + dutyStates[d.getNdx()].numOfCoverings;
//					dhDurationInMins += d.getBlockTimeInMinsPassive() + dutyStates[d.getNdx()].blockTimeOfCoverings;
//					activeBlocktimeInMins += d.getBlockTimeInMinsActive() - dutyStates[d.getNdx()].blockTimeOfCoveringsActive;
//					numOfDuties++;
//					numOfLegs += d.getNumOfLegs();
//
//					if (minNumOfAlternativeDuties > dutyStates[d.getNdx()].minNumOfAlternativeDuties)
//						minNumOfAlternativeDuties = dutyStates[d.getNdx()].minNumOfAlternativeDuties;
//					if (minNumOfAlternativeDutiesWoDh > dutyStates[d.getNdx()].minNumOfAlternativeDutiesWoDh)
//						minNumOfAlternativeDutiesWoDh = dutyStates[d.getNdx()].minNumOfAlternativeDutiesWoDh;
////					if (maxNumOfAlternativeDuties < dutyStates[d.getNdx()].maxNumOfAlternativeDuties)
////						maxNumOfAlternativeDuties = dutyStates[d.getNdx()].maxNumOfAlternativeDuties;
////					if (maxNumOfAlternativeDutiesWoDh < dutyStates[d.getNdx()].maxNumOfAlternativeDutiesWoDh)
////						maxNumOfAlternativeDutiesWoDh = dutyStates[d.getNdx()].maxNumOfAlternativeDutiesWoDh;
//					totalNumOfAlternativeDuties += dutyStates[d.getNdx()].totalNumOfAlternativeDuties;
//					totalNumOfAlternativeDutiesWoDh += dutyStates[d.getNdx()].totalNumOfAlternativeDutiesWoDh;
//
//					if (minNumOfAlternativeEffectiveDuties > dutyStates[d.getNdx()].minNumOfAlternativeEffectiveDuties)
//						minNumOfAlternativeEffectiveDuties = dutyStates[d.getNdx()].minNumOfAlternativeEffectiveDuties;
//					if (minNumOfAlternativeEffectiveDutiesWoDh > dutyStates[d.getNdx()].minNumOfAlternativeEffectiveDutiesWoDh)
//						minNumOfAlternativeEffectiveDutiesWoDh = dutyStates[d.getNdx()].minNumOfAlternativeEffectiveDutiesWoDh;
////					if (maxNumOfAlternativeEffectiveDuties < dutyStates[d.getNdx()].maxNumOfAlternativeEffectiveDuties)
////						maxNumOfAlternativeEffectiveDuties = dutyStates[d.getNdx()].maxNumOfAlternativeEffectiveDuties;
////					if (maxNumOfAlternativeEffectiveDutiesWoDh < dutyStates[d.getNdx()].maxNumOfAlternativeEffectiveDutiesWoDh)
////						maxNumOfAlternativeEffectiveDutiesWoDh = dutyStates[d.getNdx()].maxNumOfAlternativeEffectiveDutiesWoDh;
//					totalNumOfAlternativeEffectiveDuties += dutyStates[d.getNdx()].totalNumOfAlternativeEffectiveDuties;
//					totalNumOfAlternativeEffectiveDutiesWoDh += dutyStates[d.getNdx()].totalNumOfAlternativeEffectiveDutiesWoDh;
//
//					difficultyScore += dutyStates[d.getNdx()].getDifficultyScore();
//				}
//				nodeQualityMetric = sourceDutyNodes[k];
//				if (!nodeQualityMetric.getQual().hasTheSameValues(numOfDh, 
//																	dhDurationInMins, 
//																	activeBlocktimeInMins, 
//																	numOfDuties, 
//																	numOfLegs, 
//																	minNumOfAlternativeDuties, 
//																	minNumOfAlternativeDutiesWoDh, 
////																	maxNumOfAlternativeDuties, 
////																	maxNumOfAlternativeDutiesWoDh, 
//																	totalNumOfAlternativeDuties, 
//																	totalNumOfAlternativeDutiesWoDh, 
//																	minNumOfAlternativeEffectiveDuties, 
//																	minNumOfAlternativeEffectiveDutiesWoDh, 
////																	maxNumOfAlternativeEffectiveDuties, 
////																	maxNumOfAlternativeEffectiveDutiesWoDh, 
//																	totalNumOfAlternativeEffectiveDuties, 
//																	totalNumOfAlternativeEffectiveDutiesWoDh, 
//																	difficultyScore)) {
//					logger.error("!");
//					logger.error(legToCover);
//					logger.error(qualityMetric);
//					logger.error(nodeQualityMetric);
//					failedNodeNdxAltD = k;
//					nodeQualityMetric.getQual().hasTheSameValues(numOfDh, 
//																	dhDurationInMins, 
//																	activeBlocktimeInMins, 
//																	numOfDuties, 
//																	numOfLegs, 
//																	minNumOfAlternativeDuties, 
//																	minNumOfAlternativeDutiesWoDh, 
////																	maxNumOfAlternativeDuties, 
////																	maxNumOfAlternativeDutiesWoDh, 
//																	totalNumOfAlternativeDuties, 
//																	totalNumOfAlternativeDutiesWoDh, 
//																	minNumOfAlternativeEffectiveDuties, 
//																	minNumOfAlternativeEffectiveDutiesWoDh, 
////																	maxNumOfAlternativeEffectiveDuties, 
////																	maxNumOfAlternativeEffectiveDutiesWoDh, 
//																	totalNumOfAlternativeEffectiveDuties, 
//																	totalNumOfAlternativeEffectiveDutiesWoDh, 
//																	difficultyScore);
//					break;
//				}
//			}
			/**
			 * TEST BLOCK END
			 */
			int pairingGenerationNodeNdx = 0;
			for (int i = 0; i < sourceDutyNodes.length; i++) {

				NodeQualityMetric nqm = sourceDutyNodes[i];
				int ndxOfCand = nqm.getQual().getNumOfDuties() - 1;

				Duty d = nqm.getParent().getNodeOwner();

				if (d.isNonHbDep(this.hbNdx))
					logger.error("Must be HB departed duty!");

	    		if (this.pairRuleContext.getStarterCheckerProxy().canBeStarter(this.hbNdx, d)) {

	    			currentPair.qm.addToQualityMetricFw(d, dutyStates[d.getNdx()]);
	    			this.pairRuleContext.getAggregatorProxy().appendFw(currentPair.p, d);

					if (d.isHbArr(this.hbNdx)) {
		    			if (this.pairRuleContext.getFinalCheckerProxy().acceptable(this.hbNdx, currentPair.p)) {
		    				if (currentPair.p.isComplete(this.hbNdx)) {
		    					if (currentPair.qm.isBetterThan(bestPairs[ndxOfCand].qm)) {
		    						bestPairs[ndxOfCand].p = (Pair) currentPair.p.clone();
		    						bestPairs[ndxOfCand].qm.injectValues(currentPair.qm);
		    						bestPairs[ndxOfCand].nqm = sourceDutyNodes[i];
		    						pairingGenerationNodeNdx = i;
		    					}
		    				} else
		    					logger.error("Pairing " + d + " must be complete!");
		    			}
					} else {
						/*
						 * Basic cumulative quality info which is calculated during the sub network generation is checked!
						 */
						if (this.pairRuleContext.getExtensibilityCheckerProxy().isExtensible(this.hbNdx, currentPair.p)) {

							int dept = HeurosSystemParam.maxPairingLengthInDays - 1;
							while (nqm.getNextNodeMetric() != null) {
								nqm = nqm.getNextNodeMetric();
								Duty nd = nqm.getParent().getNodeOwner();

								if (this.pairRuleContext.getAppendabilityCheckerProxy().isAppendable(this.hbNdx, currentPair.p, nd, true)) {
									currentPair.qm.addToQualityMetricFw(nd, dutyStates[nd.getNdx()]);
									pairRuleContext.getAggregatorProxy().appendFw(currentPair.p, nd);
									if (nd.isHbArr(this.hbNdx)) {
										if (this.pairRuleContext.getFinalCheckerProxy().acceptable(this.hbNdx, currentPair.p)) {
						    				if (currentPair.p.isComplete(this.hbNdx)) {
						    					if (currentPair.qm.isBetterThan(bestPairs[ndxOfCand].qm)) {
						    						bestPairs[ndxOfCand].p = (Pair) currentPair.p.clone();
						    						bestPairs[ndxOfCand].qm.injectValues(currentPair.qm);
						    						bestPairs[ndxOfCand].nqm = sourceDutyNodes[i];
						    						pairingGenerationNodeNdx = i;
						    						break;
						    					}
						    				} else
						    					logger.error("Pairing " + currentPair.p + " must be complete!");
										}
									} else
										if (!(nd.isNonHbArr(this.hbNdx)
												&& (dept > 1)
												&& this.pairRuleContext.getExtensibilityCheckerProxy().isExtensible(this.hbNdx, currentPair.p))) {
											break;
										}
								}
							}
						}
					}

//	    				if (!currentPair.p.isComplete(this.hbNdx))
//	    					logger.error("Pairing " + currentPair.p + " must be complete!");

					currentPair.qm.reset();
					this.pairRuleContext.getAggregatorProxy().removeAll(currentPair.p);
	    		}
			}

//logger.info(legToCover);
//logger.info(bestPair.pairNq);
//logger.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");

			/*
			 * TODO
			 * 
			 * In some of iterations pairingGenerationNodeNdx is quite large. Why ?
			 * 
			 */
			if ((failedNodeNdxTot < pairingGenerationNodeNdx)
					|| (failedNodeNdxAltD < pairingGenerationNodeNdx)) {
				System.out.println("!!!");
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

		return bestPairs;
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
