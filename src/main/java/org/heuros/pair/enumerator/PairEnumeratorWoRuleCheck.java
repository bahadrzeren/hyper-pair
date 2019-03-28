package org.heuros.pair.enumerator;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.apache.log4j.Logger;
import org.heuros.context.PairOptimizationContext;
import org.heuros.core.data.ndx.OneDimIndexInt;
import org.heuros.core.data.ndx.OneDimUniqueIndexInt;
import org.heuros.data.DutyLegOvernightConnNetwork;
import org.heuros.data.model.Duty;
import org.heuros.data.model.Leg;
import org.heuros.pair.heuro.state.DutyState;

public class PairEnumeratorWoRuleCheck {

	private static Logger logger = Logger.getLogger(PairEnumeratorWoRuleCheck.class);

	/*
	 * TODO Single base assumption!!!
	 */
	private int hbNdx = 0;

	private DutyLegOvernightConnNetwork pricingNetwork = null;
	private PairListener pairListener = null;
	private int maxPairingLengthInDays = 0;
	private int maxSearchDept = 0;

	private OneDimIndexInt<Duty> dutyIndexByDepLegNdx = null;
	private OneDimIndexInt<Duty> dutyIndexByArrLegNdx = null;
	private OneDimUniqueIndexInt<Leg> nextBriefLegIndexByDutyNdx = null;
	private OneDimUniqueIndexInt<Leg> prevDebriefLegIndexByDutyNdx = null;

	private List<Duty> duties = null;
	private OneDimIndexInt<Duty> dutyIndexByLegNdx = null;

	private Duty[] pairing = null;
	private int[] numOfProbablePairings = null;

	public PairEnumeratorWoRuleCheck(PairOptimizationContext pairOptimizationContext,
										DutyLegOvernightConnNetwork pricingNetwork,
										PairListener pairListener,
										int maxPairingLengthInDays,
										int maxSearchDept) {
		this.duties = pairOptimizationContext.getDutyRepository().getModels();
		this.dutyIndexByLegNdx = pairOptimizationContext.getDutyIndexByLegNdx();

		this.pricingNetwork = pricingNetwork;
		this.pairListener = pairListener;
		this.maxPairingLengthInDays = maxPairingLengthInDays;
		this.maxSearchDept = maxSearchDept;

		this.dutyIndexByDepLegNdx = pricingNetwork.getDutyIndexByDepLegNdx();
		this.dutyIndexByArrLegNdx = pricingNetwork.getDutyIndexByArrLegNdx();
		this.nextBriefLegIndexByDutyNdx = pricingNetwork.getNextBriefLegIndexByDutyNdx();
		this.prevDebriefLegIndexByDutyNdx = pricingNetwork.getPrevDebriefLegIndexByDutyNdx();

		this.pairing = new Duty[maxPairingLengthInDays * 2 - 1];
		this.numOfProbablePairings = new int[maxPairingLengthInDays];
	}

	public void enumerateAllPairings() {

		logger.info("Pairings enumeration is started!");

		for (Duty duty : this.duties) {
			if (duty.isValid(this.hbNdx)
				&& duty.hasPairing(this.hbNdx)
				&& (duty.getNumOfLegsPassive() == 0)) {

				pairing[maxPairingLengthInDays - 1] = duty;

				int numOfDhs = duty.getNumOfLegsPassive();
				int totalActiveBlockTime = duty.getBlockTimeInMinsActive();

				if (duty.isHbDep(this.hbNdx)) {
					if (duty.isHbArr(this.hbNdx)) {
						this.pairListener.onPairingFound(pairing,
															maxPairingLengthInDays - 1, maxPairingLengthInDays,
															numOfDhs, totalActiveBlockTime);
						numOfProbablePairings[0]++;
					} else
						if (this.maxSearchDept > 1) {
							this.fwSearch(numOfProbablePairings,
											duty, duty, maxPairingLengthInDays - 1, maxPairingLengthInDays + 1,
											numOfDhs, totalActiveBlockTime, duty.getBriefDay(this.hbNdx).plusDays(maxPairingLengthInDays));
						}
				}
			}
		}

		for (int i = 0; i < numOfProbablePairings.length; i++) {
			logger.info(numOfProbablePairings[i] + " num of probable " + (i + 1) + "th day pairings are found!");
		}
	}

	public void enumerateAllPairings(Duty duty, DutyState dutyState) {

		pairing[maxPairingLengthInDays - 1] = duty;

		int numOfDhs = duty.getNumOfLegsPassive() + dutyState.numOfCoverings;
		int totalActiveBlockTime = duty.getBlockTimeInMinsActive() - dutyState.blockTimeOfCoveringsActive;

		if (duty.isHbDep(this.hbNdx)) {
			if (duty.isHbArr(this.hbNdx)) {
				this.pairListener.onPairingFound(pairing,
													maxPairingLengthInDays - 1, maxPairingLengthInDays,
													numOfDhs, totalActiveBlockTime);
				numOfProbablePairings[0]++;
			} else
				if (this.maxSearchDept > 1) {
					this.fwSearch(numOfProbablePairings,
									duty, duty, maxPairingLengthInDays - 1, maxPairingLengthInDays + 1,
									numOfDhs, totalActiveBlockTime, duty.getBriefDay(this.hbNdx).plusDays(maxPairingLengthInDays));
				}
		}
	}

	private void fwSearch(int[] numOfProbablePairings,
							Duty fd, Duty ld, int fromNdxInc, int toNdxExc,
							int numOfDhs, int totalActiveBlockTime,
							LocalDate maxMinDateDept) {
		Leg[] nls = this.nextBriefLegIndexByDutyNdx.getArray(ld.getNdx());
		for (Leg nl : nls) {
			Duty[] nds = this.dutyIndexByDepLegNdx.getArray(nl.getNdx());
			for (Duty nd : nds) {
				if (nd.getNumOfLegsPassive() == 0) {
					if (nd.isHbArr(this.hbNdx)
							&& maxMinDateDept.isAfter(nd.getDebriefDay(this.hbNdx))
							/*
							 * TODO
							 * This line below are put because of no rule validation code is done here!
							 * Rule validation is done in just briefing time context.
							 */
							&& (ChronoUnit.DAYS.between(fd.getBriefTime(this.hbNdx), nd.getDebriefTime(this.hbNdx).minusSeconds(1)) < (this.maxPairingLengthInDays - 1))) {
						pairing[toNdxExc - 1] = nd;
						this.pairListener.onPairingFound(pairing, fromNdxInc, toNdxExc, numOfDhs + nd.getNumOfLegsPassive(), totalActiveBlockTime + nd.getBlockTimeInMinsActive());
						numOfProbablePairings[toNdxExc - fromNdxInc - 1]++;
						pairing[toNdxExc - 1] = null;
					} else
						if (nd.isNonHbArr(this.hbNdx)
								&& (toNdxExc - fromNdxInc < this.maxSearchDept)
								&& maxMinDateDept.isAfter(nd.getDebriefDay(this.hbNdx))
								/*
								 * TODO
								 * This line below are put because of no rule validation code is done here!
								 * Rule validation is done in just briefing time context.
								 */
								&& (ChronoUnit.DAYS.between(fd.getBriefTime(this.hbNdx), nd.getDebriefTime(this.hbNdx).minusSeconds(1)) < (this.maxPairingLengthInDays - 2))) {
							pairing[toNdxExc - 1] = nd;
							this.fwSearch(numOfProbablePairings,
											fd, nd, fromNdxInc, toNdxExc + 1,
											numOfDhs + nd.getNumOfLegsPassive(), totalActiveBlockTime + nd.getBlockTimeInMinsActive(),
											maxMinDateDept);
							pairing[toNdxExc - 1] = null;
						}
				}
			}
		}
	}
}
