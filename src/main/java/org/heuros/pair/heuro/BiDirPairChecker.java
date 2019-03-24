package org.heuros.pair.heuro;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.heuros.core.data.ndx.OneDimIndexInt;
import org.heuros.core.data.ndx.OneDimUniqueIndexInt;
import org.heuros.core.data.repo.DataRepository;
import org.heuros.data.DutyLegOvernightConnNetwork;
import org.heuros.data.model.Duty;
import org.heuros.data.model.Leg;
import org.heuros.pair.conf.HeurosSystemParam;

public class BiDirPairChecker implements Callable<Boolean> {

	private static Logger logger = Logger.getLogger(BiDirPairChecker.class);

	/*
	 * TODO Single base assumption!!!
	 */
	private int hbNdx = 0;

	private OneDimIndexInt<Duty> dutyIndexByDepLegNdx = null;
//	private OneDimIndexInt<Duty> dutyIndexByArrLegNdx = null;
	private OneDimUniqueIndexInt<Leg> nextBriefLegIndexByDutyNdx = null;
//	private OneDimUniqueIndexInt<Leg> prevDebriefLegIndexByDutyNdx = null;

//	private List<Leg> legs = null;
	private List<Duty> duties = null;
//	private OneDimIndexInt<Duty> dutyIndexByLegNdx = null;

	public BiDirPairChecker(
//							DataRepository<Leg> legRepository,
							DataRepository<Duty> dutyRepository,
//							OneDimIndexInt<Duty> dutyIndexByLegNdx,
							DutyLegOvernightConnNetwork pricingNetwork) {
//		this.legs = legRepository.getModels();
		this.duties = dutyRepository.getModels();
//		this.dutyIndexByLegNdx = dutyIndexByLegNdx;
		this.dutyIndexByDepLegNdx = pricingNetwork.getDutyIndexByDepLegNdx();
//		this.dutyIndexByArrLegNdx = pricingNetwork.getDutyIndexByArrLegNdx();
		this.nextBriefLegIndexByDutyNdx = pricingNetwork.getNextBriefLegIndexByDutyNdx();
//		this.prevDebriefLegIndexByDutyNdx = pricingNetwork.getPrevDebriefLegIndexByDutyNdx();
	}

	private void setIncludingPairings(Duty[] pairing, int numOfDuties, int numOfDhs, int totalActiveBlockTime) {
		for (int i = 0; i < numOfDuties; i++) {
			for (int j = 0; j < pairing[i].getNumOfLegs(); j++) {
				Leg l = pairing[i].getLegs().get(j);

				if (l.isCover()
						&& l.hasPair(hbNdx)) {
					l.incNumOfIncludingPairs();
					if (totalActiveBlockTime >= HeurosSystemParam.effectiveDutyBlockHourLimit * numOfDuties)
						l.incNumOfIncludingEffectivePairs();
					if (numOfDhs == 0) {
						l.incNumOfIncludingPairsWoDh();
						if (totalActiveBlockTime >= HeurosSystemParam.effectiveDutyBlockHourLimit * numOfDuties)
							l.incNumOfIncludingEffectivePairsWoDh();
					}
				}
			}
		}
	}

	@Override
	public Boolean call() {

		logger.info("Pairings check is started!");

		Duty[] pairing = new Duty[HeurosSystemParam.maxPairingLengthInDays];

		int numOfProbablePairings = 0;

		for (Duty duty : this.duties) {
			if (duty.isValid(this.hbNdx)
				&& duty.hasPairing(this.hbNdx)) {

				pairing[0] = duty;
				int numOfDhs = duty.getNumOfLegsPassive();
				int totalActiveBlockTime = duty.getBlockTimeInMinsActive();

				if (duty.isHbDep(this.hbNdx)) {
					if (duty.isHbArr(this.hbNdx)) {
						setIncludingPairings(pairing, 1, numOfDhs, totalActiveBlockTime);
						numOfProbablePairings++;
					} else {
						LocalDate maxMinDateDept = duty.getBriefDay(this.hbNdx).plusDays(HeurosSystemParam.maxPairingLengthInDays);
						Leg[] nls = this.nextBriefLegIndexByDutyNdx.getArray(duty.getNdx());
						for (Leg nl : nls) {
							Duty[] nds = this.dutyIndexByDepLegNdx.getArray(nl.getNdx());
							for (Duty nd : nds) {
								if (nd.isHbArr(this.hbNdx)
										&& (maxMinDateDept.isAfter(nd.getDebriefDay(this.hbNdx)))
										/*
										 * TODO
										 * This line below are put because of no rule validation code is done here!
										 * Rule validation is done in just briefing time context.
										 */
										&& (ChronoUnit.DAYS.between(duty.getBriefTime(this.hbNdx), nd.getDebriefTime(this.hbNdx).minusSeconds(1)) < 3)) {
									pairing[1] = nd;
									numOfDhs += nd.getNumOfLegsPassive();
									totalActiveBlockTime += nd.getBlockTimeInMinsActive();
									setIncludingPairings(pairing, 2, numOfDhs, totalActiveBlockTime);
									numOfDhs -= nd.getNumOfLegsPassive();
									totalActiveBlockTime -= nd.getBlockTimeInMinsActive();
									pairing[1] = null;
									numOfProbablePairings++;
								}
							}
						}
					}
//				} else
//					if (duty.isHbArr(this.hbNdx)) {
//						maxMinDateDept = duty.getDebriefDay(this.hbNdx).minusDays(HeurosSystemParam.maxPairingLengthInDays - 1);
//						Leg[] pls = this.prevDebriefLegIndexByDutyNdx.getArray(duty.getNdx());
//						for (Leg pl : pls) {
//							Duty[] pds = this.dutyIndexByArrLegNdx.getArray(pl.getNdx());
//							for (Duty pd : pds) {
//								if (pd.isHbDep(this.hbNdx)
//										&& (maxMinDateDept.isBefore(pd.getBriefDay(this.hbNdx))
//												|| maxMinDateDept.isEqual(pd.getBriefDay(this.hbNdx)))) {
//									pairing[1] = pd;
//									numOfDhs += pd.getNumOfLegsPassive();
//									totalActiveBlockTime += pd.getBlockTimeInMinsActive();
//									setIncludingPairings(pairing, 2, numOfDhs, totalActiveBlockTime);
//									numOfDhs -= pd.getNumOfLegsPassive();
//									totalActiveBlockTime -= pd.getBlockTimeInMinsActive();
//									pairing[1] = null;
//									numOfProbablePairings++;
//								}
//							}
//						}
//					} else {
//						//	Not implemented!
					}
			}
		}

		logger.info(numOfProbablePairings + " num of probable pairings are found!");

		return true;
	}
}
