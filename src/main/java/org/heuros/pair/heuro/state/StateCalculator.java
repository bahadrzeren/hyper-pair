package org.heuros.pair.heuro.state;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.heuros.core.data.ndx.OneDimIndexInt;
import org.heuros.core.data.ndx.OneDimUniqueIndexInt;
import org.heuros.data.DutyLegOvernightConnNetwork;
import org.heuros.data.model.Duty;
import org.heuros.data.model.Leg;
import org.heuros.pair.conf.HeurosSystemParam;
import org.heuros.pair.sp.PairWithQuality;

public class StateCalculator implements Callable<Double> {
	private static Logger logger = Logger.getLogger(StateCalculator.class);

	/*
	 * TODO Single base assumption!!!
	 */
	private int hbNdx = 0;

	private List<Leg> legs = null;
	private OneDimIndexInt<Duty> dutyIndexByLegNdx = null;
	private LegState[] activeLegStates = null;
	private DutyState[] activeDutyStates = null;
	private LegState[] tempLegStates = null;
	private DutyState[] tempDutyStates = null;
	private PairWithQuality pwq = null;
	private Leg legToCover = null;

	private OneDimIndexInt<Duty> dutyIndexByDepLegNdx = null;
	private OneDimIndexInt<Duty> dutyIndexByArrLegNdx = null;
	private OneDimUniqueIndexInt<Leg> nextBriefLegIndexByDutyNdx = null;
	private OneDimUniqueIndexInt<Leg> prevDebriefLegIndexByDutyNdx = null;

	public StateCalculator(List<Leg> legs,
							OneDimIndexInt<Duty> dutyIndexByLegNdx,
							DutyLegOvernightConnNetwork pricingNetwork,
							Leg legToCover,
							LegState[] activeLegStates,
							DutyState[] activeDutyStates,
							PairWithQuality pwq) {
		this.legs = legs;
		this.dutyIndexByLegNdx = dutyIndexByLegNdx;
		this.dutyIndexByDepLegNdx = pricingNetwork.getDutyIndexByDepLegNdx();
		this.dutyIndexByArrLegNdx = pricingNetwork.getDutyIndexByArrLegNdx();
		this.nextBriefLegIndexByDutyNdx = pricingNetwork.getNextBriefLegIndexByDutyNdx();
		this.prevDebriefLegIndexByDutyNdx = pricingNetwork.getPrevDebriefLegIndexByDutyNdx();
		this.legToCover = legToCover;
		this.activeLegStates = activeLegStates;
		this.activeDutyStates = activeDutyStates;
		this.pwq = pwq;
	}

	public LegState[] getTempLegStates() {
		return tempLegStates;
	}

	public DutyState[] getTempDutyStates() {
		return tempDutyStates;
	}

	public PairWithQuality getPwq() {
		return pwq;
	}

	private LegState[] cloneLegStates() throws CloneNotSupportedException {
		LegState[] res = this.activeLegStates.clone();
		for (int i = 0; i < res.length; i++)
			res[i] = (LegState) res[i].clone();
		return res;
	}

	private DutyState[] cloneDutyStates() throws CloneNotSupportedException {
		DutyState[] res = this.activeDutyStates.clone();
		for (int i = 0; i < res.length; i++)
			res[i] = (DutyState) res[i].clone();
		return res;
	}

	private double decreasePairingTotalizers(double maxDifficultyScoreObtained,
												Leg leg,
												Duty[] pairing,
												int numOfDuties,
												boolean isEffectivenessChanged,
												boolean isDhStateChanged,
												boolean isEffectivenessWoDhChanged) {
		double res = maxDifficultyScoreObtained;
		for (int i = 0; i < numOfDuties; i++) {
			for (int j = 0; j < pairing[i].getNumOfLegs(); j++) {
				Leg l = pairing[i].getLegs().get(j);
				if (l.isCover()
						&& l.hasPair(hbNdx)) {

//if (l.getNdx() == 97) {
//	logger.info("--------------------");
//	if (numOfDuties == 1)
//		logger.info(this.legToCover.getNdx() + "-" + pwq.pair.getNumOfDuties() + ": " + pairing[0].getNdx());
//	else
//		logger.info(this.legToCover.getNdx() + "-" + pwq.pair.getNumOfDuties() + ": " + pairing[0].getNdx() + ", " + pairing[1].getNdx());
//	logger.info("Leg covered: " + leg);
//	if (numOfDuties == 1)
//		logger.info(pairing[0]);
//	else {
//		logger.info(pairing[0]);
//		logger.info(pairing[1]);
//	}
//	logger.info(this.legToCover.getNdx() + "-" + pwq.pair.getNumOfDuties() + ": isEffectivenessChanged: " + isEffectivenessChanged +
//																				", isDhStateChanged: " + isDhStateChanged +
//																				", isEffectivenessWoDhChanged: " + isEffectivenessWoDhChanged);
//	logger.info(this.legToCover.getNdx() + "-" + pwq.pair.getNumOfDuties() + ": Before PairTots: " + this.tempLegStates[l.getNdx()].numOfIncludingPairs + ", " +
//																										this.tempLegStates[l.getNdx()].numOfIncludingEffectivePairs + ", " +
//																										this.tempLegStates[l.getNdx()].numOfIncludingPairsWoDh + ", " +
//																										this.tempLegStates[l.getNdx()].numOfIncludingEffectivePairsWoDh);
//}

					if (isEffectivenessChanged)
						this.tempLegStates[l.getNdx()].numOfIncludingEffectivePairs--;
					if (isDhStateChanged) {
						this.tempLegStates[l.getNdx()].numOfIncludingPairsWoDh--;
						if (isEffectivenessWoDhChanged) {
							this.tempLegStates[l.getNdx()].numOfIncludingEffectivePairsWoDh--;
						}
					}

//if (l.getNdx() == 97) {
//	logger.info(this.legToCover.getNdx() + "-" + pwq.pair.getNumOfDuties() + ": After PairTots: " + this.tempLegStates[l.getNdx()].numOfIncludingPairs + ", " +
//																										this.tempLegStates[l.getNdx()].numOfIncludingEffectivePairs + ", " +
//																										this.tempLegStates[l.getNdx()].numOfIncludingPairsWoDh + ", " +
//																										this.tempLegStates[l.getNdx()].numOfIncludingEffectivePairsWoDh);
//}

					double newDifficultyScore = this.tempLegStates[l.getNdx()].getDifficultyScoreOfTheLeg();
					if (newDifficultyScore > res)
						res = newDifficultyScore;
				}
			}
		}
		return res;
	}

	private double maxDifficultyScoreObtained = 0.0;

	public double getMaxDifficultyScoreObtained() {
		return maxDifficultyScoreObtained;
	}

	@Override
	public Double call() throws Exception {

		this.tempLegStates = this.cloneLegStates();
		this.tempDutyStates = this.cloneDutyStates();

//		double maxDifficultyScoreObtained = 0.0;

		HashSet<Integer> legNdxs = new HashSet<Integer>();

		/*
		 * First update COVERING states of the legs and duties that constitute new pairing.
		 */
		for (int i = 0; i < pwq.pair.getNumOfDuties(); i++) {
			Duty duty = pwq.pair.getDuties().get(i);

//if (duty.getNdx() == 20464)
//System.out.println();

			for (int j = 0; j < duty.getNumOfLegs(); j++) {
				Leg leg = duty.getLegs().get(j);
				legNdxs.add(leg.getNdx());
				this.tempLegStates[leg.getNdx()].numOfCoverings++;
			}
		}

		/*
		 * Set dutyOfLeg TOTALIZERs.
		 */
		legNdxs.forEach((legNdx) -> {
			Leg leg = this.legs.get(legNdx);
			Duty[] dutiesOfLeg = this.dutyIndexByLegNdx.getArray(leg.getNdx());
			for (int di = 0; di < dutiesOfLeg.length; di++) {
				Duty dutyOfLeg = dutiesOfLeg[di];
				if (dutyOfLeg.hasPairing(pwq.pair.getHbNdx())
						&& dutyOfLeg.isValid(pwq.pair.getHbNdx())) {

//if (dutyOfLeg.getNdx() == 822)
//System.out.println();

					DutyState dutyOfLegStat = this.tempDutyStates[dutyOfLeg.getNdx()];

					int prevNumOfDhs = dutyOfLeg.getNumOfLegsPassive()
										+ dutyOfLegStat.numOfCoveringsActive
										+ dutyOfLegStat.numOfCoveringsPassiveInt
										+ dutyOfLegStat.numOfCoveringsPassiveExt;
					int prevActiveBlockTime = dutyOfLeg.getBlockTimeInMinsActive() - dutyOfLegStat.blockTimeOfCoveringsActive;
					int prevTotalNumOfDhs = prevNumOfDhs;
					int prevTotalActiveBlockTime = prevActiveBlockTime;

					dutyOfLegStat.numOfCoverings++;
					dutyOfLegStat.blockTimeOfCoverings += leg.getBlockTimeInMins();
//					if (leg.isCover()
//							&& (this.tempLegStates[leg.getNdx()].numOfCoverings == 1)) {
//						dutyOfLegStat.numOfCoveringsActive++;
//						dutyOfLegStat.blockTimeOfCoveringsActive += leg.getBlockTimeInMins();
//					} else {
//						if (leg.isCover()) {
//							dutyOfLegStat.numOfCoveringsPassiveInt++;
//							dutyOfLegStat.blockTimeOfCoveringsPassiveInt += leg.getBlockTimeInMins();
//						} else {
//							dutyOfLegStat.numOfCoveringsPassiveExt++;
//							dutyOfLegStat.blockTimeOfCoveringsPassiveExt += leg.getBlockTimeInMins();
//						}
//					}
//					if (this.tempLegStates[leg.getNdx()].numOfCoverings == 1) {
//						dutyOfLegStat.numOfDistinctCoverings++;
//						dutyOfLegStat.blockTimeOfDistinctCoverings += leg.getBlockTimeInMins();
//						if (leg.isCover()) {
//							dutyOfLegStat.numOfDistinctCoveringsActive++;
//							dutyOfLegStat.blockTimeOfDistinctCoveringsActive += leg.getBlockTimeInMins();
//						} else {
//							dutyOfLegStat.numOfDistinctCoveringsPassive++;
//							dutyOfLegStat.blockTimeOfDistinctCoveringsPassive += leg.getBlockTimeInMins();
//						}
//					}

					if (this.tempLegStates[leg.getNdx()].numOfCoverings == 1) {
						dutyOfLegStat.numOfDistinctCoverings++;
						dutyOfLegStat.blockTimeOfDistinctCoverings += leg.getBlockTimeInMins();
						if (leg.isCover()) {
							dutyOfLegStat.numOfCoveringsActive++;
							dutyOfLegStat.blockTimeOfCoveringsActive += leg.getBlockTimeInMins();

							dutyOfLegStat.numOfDistinctCoveringsActive++;
							dutyOfLegStat.blockTimeOfDistinctCoveringsActive += leg.getBlockTimeInMins();
						} else {
							dutyOfLegStat.numOfCoveringsPassiveExt++;
							dutyOfLegStat.blockTimeOfCoveringsPassiveExt += leg.getBlockTimeInMins();

							dutyOfLegStat.numOfDistinctCoveringsPassive++;
							dutyOfLegStat.blockTimeOfDistinctCoveringsPassive += leg.getBlockTimeInMins();
						}
					} else {
						if (leg.isCover()) {
							dutyOfLegStat.numOfCoveringsPassiveInt++;
							dutyOfLegStat.blockTimeOfCoveringsPassiveInt += leg.getBlockTimeInMins();
							if (this.tempLegStates[leg.getNdx()].numOfCoverings == 2) {
								dutyOfLegStat.numOfDistinctCoveringsPassive++;
								dutyOfLegStat.blockTimeOfDistinctCoveringsPassive += leg.getBlockTimeInMins();
							}
						} else {
							dutyOfLegStat.numOfCoveringsPassiveExt++;
							dutyOfLegStat.blockTimeOfCoveringsPassiveExt += leg.getBlockTimeInMins();
						}
					}

					boolean isEffectivenessChanged = leg.isCover()
														&& (this.tempLegStates[leg.getNdx()].numOfCoverings == 1)
														&& (dutyOfLeg.getBlockTimeInMinsActive() 
																- dutyOfLegStat.blockTimeOfDistinctCoveringsActive 
																+ leg.getBlockTimeInMins() >= HeurosSystemParam.effectiveDutyBlockHourLimit)
														&& (dutyOfLeg.getBlockTimeInMinsActive() 
																- dutyOfLegStat.blockTimeOfDistinctCoveringsActive < HeurosSystemParam.effectiveDutyBlockHourLimit);
					boolean isDhStateChanged = (dutyOfLeg.getNumOfLegsPassive() == 0) && (dutyOfLegStat.numOfCoverings == 1);
					boolean isEffectivenessWoDhChanged = isDhStateChanged && (dutyOfLeg.getBlockTimeInMinsActive() >= HeurosSystemParam.effectiveDutyBlockHourLimit);

					/*
					 * Calculate decrease in pairing totalizers.
					 */

//					int decInNumOfProbableEffectivePairings = 0;
//					int decInNumOfProbablePairingsWoDh = 0;
//					int decInNumOfProbableEffectivePairingsWoDh = 0;
	
					Duty[] pairing = new Duty[HeurosSystemParam.maxPairingLengthInDays];
					pairing[0] = dutyOfLeg;

					LocalDate maxMinDateDept = null;
					int numOfDhs = dutyOfLeg.getNumOfLegsPassive()
									+ dutyOfLegStat.numOfCoveringsActive
									+ dutyOfLegStat.numOfCoveringsPassiveInt
									+ dutyOfLegStat.numOfCoveringsPassiveExt;
					int activeBlockTime = dutyOfLeg.getBlockTimeInMinsActive() - dutyOfLegStat.blockTimeOfCoveringsActive;
					int totalNumOfDhs = numOfDhs;
					int totalActiveBlockTime = activeBlockTime;

					if (dutyOfLeg.isHbDep(this.hbNdx)) {
						if (dutyOfLeg.isHbArr(this.hbNdx)) {
							maxDifficultyScoreObtained = this.decreasePairingTotalizers(maxDifficultyScoreObtained,
																						leg, pairing, 1,
																						isEffectivenessChanged,
																						isDhStateChanged,
																						isEffectivenessWoDhChanged);
						} else {
							maxMinDateDept = dutyOfLeg.getBriefDay(this.hbNdx).plusDays(HeurosSystemParam.maxPairingLengthInDays);
							Leg[] nls = this.nextBriefLegIndexByDutyNdx.getArray(dutyOfLeg.getNdx());
							for (Leg nl : nls) {
								Duty[] nds = this.dutyIndexByDepLegNdx.getArray(nl.getNdx());
								for (Duty nd : nds) {
									if (nd.isHbArr(this.hbNdx)
											&& (maxMinDateDept.isAfter(nd.getDebriefDay(this.hbNdx)))) {

										numOfDhs = nd.getNumOfLegsPassive()
													+ this.tempDutyStates[nd.getNdx()].numOfCoveringsActive
													+ this.tempDutyStates[nd.getNdx()].numOfCoveringsPassiveInt
													+ this.tempDutyStates[nd.getNdx()].numOfCoveringsPassiveExt;
										activeBlockTime = nd.getBlockTimeInMinsActive() - this.tempDutyStates[nd.getNdx()].blockTimeOfCoveringsActive;
										prevTotalNumOfDhs += numOfDhs;
										prevTotalActiveBlockTime += activeBlockTime;
										totalNumOfDhs += numOfDhs;
										totalActiveBlockTime += activeBlockTime;
										pairing[1] = nd;

										boolean isEffectivenessOfThePairChanged = leg.isCover()
																					&& (this.tempLegStates[leg.getNdx()].numOfCoverings == 1)
																					&& (prevTotalActiveBlockTime >= HeurosSystemParam.effectiveDutyBlockHourLimit * 2.0)
																					&& (totalActiveBlockTime < HeurosSystemParam.effectiveDutyBlockHourLimit * 2.0);
										boolean isDhStateOfThePairChanged = isDhStateChanged && (numOfDhs == 0);
										boolean isEffectivenessWoDhOfThePairChanged = isDhStateOfThePairChanged
																						&& (prevTotalActiveBlockTime >= HeurosSystemParam.effectiveDutyBlockHourLimit * 2.0);

										if (isEffectivenessOfThePairChanged
												|| isDhStateOfThePairChanged
												|| isEffectivenessWoDhOfThePairChanged) {
											maxDifficultyScoreObtained = this.decreasePairingTotalizers(maxDifficultyScoreObtained,
																										leg, pairing, 2,
																										isEffectivenessOfThePairChanged,
																										isDhStateOfThePairChanged,
																										isEffectivenessWoDhOfThePairChanged);
										}

										prevTotalNumOfDhs -= numOfDhs;
										prevTotalActiveBlockTime -= activeBlockTime;
										totalNumOfDhs -= numOfDhs;
										totalActiveBlockTime -= activeBlockTime;
										pairing[1] = null;
									}
								}
							}
						}
					} else {
						if (dutyOfLeg.isHbArr(this.hbNdx)) {
							maxMinDateDept = dutyOfLeg.getDebriefDay(this.hbNdx).minusDays(HeurosSystemParam.maxPairingLengthInDays - 1);
							Leg[] pls = this.prevDebriefLegIndexByDutyNdx.getArray(dutyOfLeg.getNdx());
							for (Leg pl : pls) {
								Duty[] pds = this.dutyIndexByArrLegNdx.getArray(pl.getNdx());
								for (Duty pd : pds) {
									if (pd.isHbDep(this.hbNdx)
											&& (!pd.getNextBriefTime(hbNdx).isAfter(dutyOfLeg.getBriefTime(hbNdx)))	//	This line is put because of no rule validation code is invoked here!
											&& (maxMinDateDept.isBefore(pd.getBriefDay(this.hbNdx))
													|| maxMinDateDept.isEqual(pd.getBriefDay(this.hbNdx)))) {
										numOfDhs = pd.getNumOfLegsPassive()
													+ this.tempDutyStates[pd.getNdx()].numOfCoveringsActive
													+ this.tempDutyStates[pd.getNdx()].numOfCoveringsPassiveInt
													+ this.tempDutyStates[pd.getNdx()].numOfCoveringsPassiveExt;
										activeBlockTime = pd.getBlockTimeInMinsActive() - this.tempDutyStates[pd.getNdx()].blockTimeOfCoveringsActive;
										prevTotalNumOfDhs += numOfDhs;
										prevTotalActiveBlockTime += activeBlockTime;
										totalNumOfDhs += numOfDhs;
										totalActiveBlockTime += activeBlockTime;
										pairing[1] = pd;

										boolean isEffectivenessOfThePairChanged = leg.isCover()
																					&& (this.tempLegStates[leg.getNdx()].numOfCoverings == 1)
																					&& (prevTotalActiveBlockTime >= HeurosSystemParam.effectiveDutyBlockHourLimit * 2.0)
																					&& (totalActiveBlockTime < HeurosSystemParam.effectiveDutyBlockHourLimit * 2.0);
										boolean isDhStateOfThePairChanged = isDhStateChanged && (numOfDhs == 0);
										boolean isEffectivenessWoDhOfThePairChanged = isDhStateOfThePairChanged
																						&& (prevTotalActiveBlockTime >= HeurosSystemParam.effectiveDutyBlockHourLimit * 2.0);

										if (isEffectivenessOfThePairChanged
												|| isDhStateOfThePairChanged
												|| isEffectivenessWoDhOfThePairChanged) {
											maxDifficultyScoreObtained = this.decreasePairingTotalizers(maxDifficultyScoreObtained,
																										leg, pairing, 2,
																										isEffectivenessOfThePairChanged,
																										isDhStateOfThePairChanged,
																										isEffectivenessWoDhOfThePairChanged);
										}

										prevTotalNumOfDhs -= numOfDhs;
										prevTotalActiveBlockTime -= activeBlockTime;
										totalNumOfDhs -= numOfDhs;
										totalActiveBlockTime -= activeBlockTime;
										pairing[1] = null;
									}
								}
							}
//						} else {
//							Not implemented!
						}
					}

					if (isDhStateChanged
							|| isEffectivenessChanged) {
						/*
						 * Calculate decrease in duty totalizers.
						 */
						for (int li = 0; li < dutyOfLeg.getLegs().size(); li++) {
							Leg indLeg = dutyOfLeg.getLegs().get(li);

//							if (indLeg.isCover()
//									&& (this.tempLegStates[indLeg.getNdx()].numOfCoverings == 0)
//									&& indLeg.hasPair(p.getHbNdx())
//									&& (indLeg.getNdx() != leg.getNdx()))
//								indLegNdxs.add(indLeg.getNdx());
							if (indLeg.isCover()) {
								if (isEffectivenessChanged) {
									this.tempLegStates[indLeg.getNdx()].numOfIncludingEffectiveDuties--;
								}

								if (isDhStateChanged) {
									this.tempLegStates[indLeg.getNdx()].numOfIncludingDutiesWoDh--;
									if (isEffectivenessWoDhChanged) {
										this.tempLegStates[indLeg.getNdx()].numOfIncludingEffectiveDutiesWoDh--;
									}
								}

//								Duty[] dutiesOfIndLeg = this.dutyIndexByLegNdx.getArray(indLeg.getNdx());
//								for (int idi = 0; idi < dutiesOfIndLeg.length; idi++) {
//									Duty dutieOfIndLeg = dutiesOfIndLeg[idi];
//									if (dutieOfIndLeg.hasPairing(p.getHbNdx())
//											&& dutieOfIndLeg.isValid(p.getHbNdx())) {
//										if (isEffectivenessChanged) {
//											this.tempDutyStates[dutieOfIndLeg.getNdx()].totalNumOfAlternativeEffectiveDuties--;
//											if (this.tempDutyStates[dutieOfIndLeg.getNdx()].minNumOfAlternativeEffectiveDuties > this.tempLegStates[indLeg.getNdx()].numOfIncludingEffectiveDuties)
//												this.tempDutyStates[dutieOfIndLeg.getNdx()].minNumOfAlternativeEffectiveDuties = this.tempLegStates[indLeg.getNdx()].numOfIncludingEffectiveDuties;
////											/*
////											 * TODO
////											 * 
////											 * This implementation does not guarantee to set exact maxNumOfAlternativeEffectiveDutiesWoDh.
////											 * We did not want to make the code more complex by adding another state variable that is needed to be maintained during the iterations.
////											 * Therefore the number of legs that has the same maxNumOfAlternativeEffectiveDutiesWoDh might cause small disruptions.
////											 *  
////											 */
////											if (this.tempDutyStates[dutieOfIndLeg.getNdx()].maxNumOfAlternativeEffectiveDuties <= this.tempLegStates[indLeg.getNdx()].numOfIncludingEffectiveDuties)
////												this.tempDutyStates[dutieOfIndLeg.getNdx()].maxNumOfAlternativeEffectiveDuties = this.tempLegStates[indLeg.getNdx()].numOfIncludingEffectiveDuties;
//										}
//										if (isDhStateChanged) {
//											this.tempDutyStates[dutieOfIndLeg.getNdx()].totalNumOfAlternativeDutiesWoDh--;
//											if (isEffectivenessWoDhChanged) {
//												this.tempDutyStates[dutieOfIndLeg.getNdx()].totalNumOfAlternativeEffectiveDutiesWoDh--;
//												if (this.tempDutyStates[dutieOfIndLeg.getNdx()].minNumOfAlternativeEffectiveDutiesWoDh > this.tempLegStates[indLeg.getNdx()].numOfIncludingEffectiveDutiesWoDh)
//													this.tempDutyStates[dutieOfIndLeg.getNdx()].minNumOfAlternativeEffectiveDutiesWoDh = this.tempLegStates[indLeg.getNdx()].numOfIncludingEffectiveDutiesWoDh;
////												/*
////												 * TODO
////												 * 
////												 * This implementation does not guarantee to set exact maxNumOfAlternativeEffectiveDutiesWoDh.
////												 * We did not want to make the code more complex by adding another state variable that is needed to be maintained during the iterations.
////												 * Therefore the number of legs that has the same maxNumOfAlternativeEffectiveDutiesWoDh might cause small disruptions.
////												 *  
////												 */
////												if (this.tempDutyStates[dutieOfIndLeg.getNdx()].maxNumOfAlternativeEffectiveDutiesWoDh <= this.tempLegStates[indLeg.getNdx()].numOfIncludingEffectiveDutiesWoDh)
////													this.tempDutyStates[dutieOfIndLeg.getNdx()].maxNumOfAlternativeEffectiveDutiesWoDh = this.tempLegStates[indLeg.getNdx()].numOfIncludingEffectiveDutiesWoDh;
//											}
//											if (this.tempDutyStates[dutieOfIndLeg.getNdx()].minNumOfAlternativeDutiesWoDh > this.tempLegStates[indLeg.getNdx()].numOfIncludingDutiesWoDh)
//												this.tempDutyStates[dutieOfIndLeg.getNdx()].minNumOfAlternativeDutiesWoDh = this.tempLegStates[indLeg.getNdx()].numOfIncludingDutiesWoDh;
////											/*
////											 * TODO
////											 * 
////											 * This implementation does not guarantee to set exact maxNumOfAlternativeDutiesWoDh.
////											 * We did not want to make the code more complex by adding another state variable that is needed to be maintained during the iterations.
////											 * Therefore the number of legs that has the same maxNumOfAlternativeDutiesWoDh might cause small disruptions.
////											 *  
////											 */
////											if (this.tempDutyStates[dutieOfIndLeg.getNdx()].maxNumOfAlternativeDutiesWoDh <= this.tempLegStates[indLeg.getNdx()].numOfIncludingDutiesWoDh)
////												this.tempDutyStates[dutieOfIndLeg.getNdx()].maxNumOfAlternativeDutiesWoDh = this.tempLegStates[indLeg.getNdx()].numOfIncludingDutiesWoDh;
//										}
//									}
//								}
							}
						}
					}
				}
			}
		});

		return maxDifficultyScoreObtained;
	}

}
