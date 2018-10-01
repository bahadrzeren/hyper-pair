package org.heuros.hyperpair.ga;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.heuros.core.data.ndx.OneDimIndexInt;
import org.heuros.core.data.ndx.TwoDimIndexIntXInt;
import org.heuros.core.data.ndx.TwoDimIndexIntXLocalDateTime;
import org.heuros.core.ga.chromosome.Chromosome;
import org.heuros.core.ga.decoder.Decoder;
import org.heuros.data.model.AirportView;
import org.heuros.data.model.Duty;
import org.heuros.data.model.DutyView;
import org.heuros.data.model.Leg;
import org.heuros.data.model.LegView;
import org.heuros.data.model.Pair;
import org.heuros.data.repo.DutyRepository;
import org.heuros.data.repo.LegRepository;
import org.heuros.hyperpair.HeurosSystemParam;
import org.heuros.rule.PairRuleContext;

public class PairChromosomeDecoder implements Decoder<Integer, Pair> {

	private static Logger logger = Logger.getLogger(PairChromosomeDecoder.class);

	private int numOfHeuristics = 3;
	public int getNumOfHeuristics() {
		return numOfHeuristics;
	}

	/*
	 * TODO Single base assumption!!!
	 */
	private int hbNdx = 0;

	private LegRepository legRepository = null;
	private DutyRepository dutyRepository = null;
	private PairRuleContext pairRuleContext = null;

	private OneDimIndexInt<Duty> dutyIndexByLegNdx = null;
	private TwoDimIndexIntXInt<Duty> hbDepArrDutyIndexByLegNdx = null;
	private TwoDimIndexIntXInt<Duty> hbDepDutyIndexByLegNdx = null;
	private TwoDimIndexIntXInt<Duty> nonHbDutyIndexByLegNdx = null;
	private TwoDimIndexIntXInt<Duty> hbArrDutyIndexByLegNdx = null;
	private TwoDimIndexIntXLocalDateTime<Duty> dutyIndexByDepAirportNdxBrieftime = null;
	private TwoDimIndexIntXLocalDateTime<Duty> dutyIndexByArrAirportNdxDebrieftime = null;
//	private TwoDimIndexIntXLocalDateTime<Duty> hbArrDutyIndexByDepAirportNdxBrieftime = null;

	private List<Leg> reOrderedLegs = null;

//	private static Random random = new Random();

	public PairChromosomeDecoder setLegRepository(LegRepository legRepository) {
		this.legRepository = legRepository;
//		double[] sortValues = new double[this.legRepository.getModels().size()];
//		for (int i = 0; i < sortValues.length; i++)
//			sortValues[i] = random.nextDouble();
		this.reOrderedLegs = this.legRepository.getModels().parallelStream().sorted(new Comparator<Leg>() {
			@Override
			public int compare(Leg a, Leg b) {
//				if (a.getSobt().toLocalDate().isBefore(b.getSobt().toLocalDate()))
//					return -1;
//				else
//					if (a.getSobt().toLocalDate().isAfter(b.getSobt().toLocalDate()))
//						return 1;
//					else
						/*
						 * TODO HB impl will be changed.
						 */
						if (a.getNumOfDutiesIncludesHbDep(hbNdx) < b.getNumOfDutiesIncludesHbDep(hbNdx))
							return -1;
						else
							if (a.getNumOfDutiesIncludesHbDep(hbNdx) > b.getNumOfDutiesIncludesHbDep(hbNdx))
								return 1;
//						if (sortValues[a.getNdx()] < sortValues[b.getNdx()])
//							return -1;
//						else
//							if (sortValues[a.getNdx()] > sortValues[b.getNdx()])
//								return 1;
				return 0;
			}
		}).collect(Collectors.toList());
		PairChromosomeDecoder.logger.info("Leg list is reordered according to their number inclusions by HB departed duties.");
		return this;
	}

	public PairChromosomeDecoder setDutyRepository(DutyRepository dutyRepository) {
		this.dutyRepository = dutyRepository;
		return this;
	}

	public PairChromosomeDecoder setPairRuleContext(PairRuleContext pairRuleContext) {
		this.pairRuleContext = pairRuleContext;
		return this;
	}

	public PairChromosomeDecoder setDutyIndexByLegNdx(OneDimIndexInt<Duty> dutyIndexByLegNdx) {
		this.dutyIndexByLegNdx = dutyIndexByLegNdx;
		return this;
	}

	public PairChromosomeDecoder setHbDepArrDutyIndexByLegNdx(TwoDimIndexIntXInt<Duty> hbDepArrDutyIndexByLegNdx) {
		this.hbDepArrDutyIndexByLegNdx = hbDepArrDutyIndexByLegNdx;
		return this;
	}

	public PairChromosomeDecoder setHbDepDutyIndexByLegNdx(TwoDimIndexIntXInt<Duty> hbDepDutyIndexByLegNdx) {
		this.hbDepDutyIndexByLegNdx = hbDepDutyIndexByLegNdx;
		return this;
	}

	public PairChromosomeDecoder setNonHbDutyIndexByLegNdx(TwoDimIndexIntXInt<Duty> nonHbDutyIndexByLegNdx) {
		this.nonHbDutyIndexByLegNdx = nonHbDutyIndexByLegNdx;
		return this;
	}

	public PairChromosomeDecoder setHbArrDutyIndexByLegNdx(TwoDimIndexIntXInt<Duty> hbArrDutyIndexByLegNdx) {
		this.hbArrDutyIndexByLegNdx = hbArrDutyIndexByLegNdx;
		return this;
	}

	public PairChromosomeDecoder setDutyIndexByDepAirportNdxBrieftime(
			TwoDimIndexIntXLocalDateTime<Duty> dutyIndexByDepAirportNdxBrieftime) {
		this.dutyIndexByDepAirportNdxBrieftime = dutyIndexByDepAirportNdxBrieftime;
		return this;
	}

	public PairChromosomeDecoder setDutyIndexByArrAirportNdxDebrieftime(
			TwoDimIndexIntXLocalDateTime<Duty> dutyIndexByArrAirportNdxDebrieftime) {
		this.dutyIndexByArrAirportNdxDebrieftime = dutyIndexByArrAirportNdxDebrieftime;
		return this;
	}

//	public PairChromosomeDecoder setHbArrDutyIndexByDepAirportNdxBrieftime(
//			TwoDimIndexIntXLocalDateTime<Duty> hbArrDutyIndexByDepAirportNdxBrieftime) {
//		this.hbArrDutyIndexByDepAirportNdxBrieftime = hbArrDutyIndexByDepAirportNdxBrieftime;
//		return this;
//	}

	private int getNextLegNdxToCover(int prevReOrderedLegNdx, int[] numOfLegCoverings) {
		for (int i = prevReOrderedLegNdx + 1; i < this.reOrderedLegs.size(); i++) {
			if (this.reOrderedLegs.get(i).isCover()
					&& (this.reOrderedLegs.get(i).getNumOfDutiesIncludesHbDep(hbNdx) > 0)
					&& (numOfLegCoverings[this.reOrderedLegs.get(i).getNdx()] == 0))
				return i;
		}
		return Integer.MAX_VALUE;
	}

	private DutyView fetchBestDhEffectiveDuty(Pair currentPair, DutyView bestSoFar, DutyView[] candidates, int[] numOfCoveringsInDuties, int[] blockTimeOfCoveringsInDuties, boolean hbDep, boolean hbArr) {
		DutyView best = bestSoFar;
		int bestNumOfDh = Integer.MAX_VALUE;
		int bestDhDurationInMins = Integer.MAX_VALUE;
		double bestAvgNumOfIncludingDutiesOfTheSameLegs = Double.MAX_VALUE;
		if (bestSoFar != null) {
			best = bestSoFar;
			bestNumOfDh = bestSoFar.getNumOfLegsPassive() + numOfCoveringsInDuties[bestSoFar.getNdx()];
			bestDhDurationInMins = bestSoFar.getBlockTimeInMinsPassive() + blockTimeOfCoveringsInDuties[bestSoFar.getNdx()];
			bestAvgNumOfIncludingDutiesOfTheSameLegs = (1.0 * bestSoFar.getTotalNumOfIncludingDutiesOfTheSameLegs()) / bestSoFar.getNumOfLegs();
		}

		for (DutyView d: candidates) {
			int dNumOfDh = d.getNumOfLegsPassive() + numOfCoveringsInDuties[d.getNdx()];
			int dDhDurationInMins = d.getBlockTimeInMinsPassive() + blockTimeOfCoveringsInDuties[d.getNdx()];
			double dAvgNumOfIncludingDutiesOfTheSameLegs = (1.0 * d.getTotalNumOfIncludingDutiesOfTheSameLegs()) / d.getNumOfLegs();

			if ((bestNumOfDh > dNumOfDh)
					|| ((bestNumOfDh == dNumOfDh) && (bestDhDurationInMins > dDhDurationInMins))
					|| ((bestNumOfDh == dNumOfDh) && (bestDhDurationInMins == dDhDurationInMins) && (bestAvgNumOfIncludingDutiesOfTheSameLegs > dAvgNumOfIncludingDutiesOfTheSameLegs))) {
				if (((currentPair != null) && pairRuleContext.getAppendabilityCheckerProxy().isAppendable(hbNdx, currentPair, d, true))
						|| pairRuleContext.getStarterCheckerProxy().canBeStarter(hbNdx, d)) {

					if (currentPair != null) {
						pairRuleContext.getAggregatorProxy().appendFw(currentPair, d);
						if (pairRuleContext.getFinalCheckerProxy().acceptable(hbNdx, currentPair)) {
							best = d;
							bestNumOfDh = dNumOfDh;
							bestDhDurationInMins = dDhDurationInMins;
							bestAvgNumOfIncludingDutiesOfTheSameLegs = dAvgNumOfIncludingDutiesOfTheSameLegs;
						}
						pairRuleContext.getAggregatorProxy().removeLast(currentPair);
					} else {
						best = d;
						bestNumOfDh = dNumOfDh;
						bestDhDurationInMins = dDhDurationInMins;
						bestAvgNumOfIncludingDutiesOfTheSameLegs = dAvgNumOfIncludingDutiesOfTheSameLegs;
					}
				}
			}
		}

		return best;
	}

	private DutyView fetchBestActiveBlockTimeEffectiveDuty(Pair currentPair, DutyView bestSoFar, DutyView[] candidates, int[] numOfCoveringsInDuties, int[] blockTimeOfCoveringsInDuties, boolean hbDep, boolean hbArr) {
		DutyView best = bestSoFar;
		int bestActiveBlocktimeInMins = 0;
		double bestAvgNumOfIncludingDutiesOfTheSameLegs = Double.MAX_VALUE;
		if (bestSoFar != null) {
			best = bestSoFar;
			bestActiveBlocktimeInMins = bestSoFar.getBlockTimeInMinsActive() - blockTimeOfCoveringsInDuties[bestSoFar.getNdx()];
			bestAvgNumOfIncludingDutiesOfTheSameLegs = (1.0 * bestSoFar.getTotalNumOfIncludingDutiesOfTheSameLegs()) / bestSoFar.getNumOfLegs();
		}

		for (DutyView d: candidates) {
			int dActiveBlocktimeInMins = d.getBlockTimeInMinsActive() - blockTimeOfCoveringsInDuties[d.getNdx()];
			double dAvgNumOfIncludingDutiesOfTheSameLegs = (1.0 * d.getTotalNumOfIncludingDutiesOfTheSameLegs()) / d.getNumOfLegs();

			if ((bestActiveBlocktimeInMins < dActiveBlocktimeInMins)
					|| ((bestActiveBlocktimeInMins == dActiveBlocktimeInMins) && (bestAvgNumOfIncludingDutiesOfTheSameLegs > dAvgNumOfIncludingDutiesOfTheSameLegs))) {
				if (((currentPair != null) && pairRuleContext.getAppendabilityCheckerProxy().isAppendable(hbNdx, currentPair, d, true))
						|| pairRuleContext.getStarterCheckerProxy().canBeStarter(hbNdx, d)) {

					if (currentPair != null) {
						pairRuleContext.getAggregatorProxy().appendFw(currentPair, d);
						if (pairRuleContext.getFinalCheckerProxy().acceptable(hbNdx, currentPair)) {
							best = d;
							bestActiveBlocktimeInMins = dActiveBlocktimeInMins;
							bestAvgNumOfIncludingDutiesOfTheSameLegs = dAvgNumOfIncludingDutiesOfTheSameLegs;
						}
						pairRuleContext.getAggregatorProxy().removeLast(currentPair);
					} else {
						best = d;
						bestActiveBlocktimeInMins = dActiveBlocktimeInMins;
						bestAvgNumOfIncludingDutiesOfTheSameLegs = dAvgNumOfIncludingDutiesOfTheSameLegs;
					}
				}
			}
		}

		return best;
	}

	private DutyView fetchBestLayoverEffectiveDuty(Pair currentPair, DutyView bestSoFar, DutyView[] candidates, int[] numOfCoveringsInDuties, int[] blockTimeOfCoveringsInDuties, boolean hbDep, boolean hbArr) {
		DutyView best = bestSoFar;
		int bestNumOfDh = Integer.MAX_VALUE;
		int bestDhDurationInMins = Integer.MAX_VALUE;
		double bestAvgNumOfIncludingDutiesOfTheSameLegs = Double.MAX_VALUE;
		if (bestSoFar != null) {
			best = bestSoFar;
			bestNumOfDh = bestSoFar.getNumOfLegsPassive() + numOfCoveringsInDuties[bestSoFar.getNdx()];
			bestDhDurationInMins = bestSoFar.getBlockTimeInMinsPassive() + blockTimeOfCoveringsInDuties[bestSoFar.getNdx()];
			bestAvgNumOfIncludingDutiesOfTheSameLegs = (1.0 * bestSoFar.getTotalNumOfIncludingDutiesOfTheSameLegs()) / bestSoFar.getNumOfLegs();
		}

		for (DutyView d: candidates) {
			int dNumOfDh = d.getNumOfLegsPassive() + numOfCoveringsInDuties[d.getNdx()];
			int dDhDurationInMins = d.getBlockTimeInMinsPassive() + blockTimeOfCoveringsInDuties[d.getNdx()];
			double dAvgNumOfIncludingDutiesOfTheSameLegs = (1.0 * d.getTotalNumOfIncludingDutiesOfTheSameLegs()) / d.getNumOfLegs();

			if ((bestNumOfDh > dNumOfDh)
					|| ((bestNumOfDh == dNumOfDh) && (bestDhDurationInMins > dDhDurationInMins))
					|| ((bestNumOfDh == dNumOfDh) && (bestDhDurationInMins == dDhDurationInMins) && (bestAvgNumOfIncludingDutiesOfTheSameLegs > dAvgNumOfIncludingDutiesOfTheSameLegs))) {

				/*
				 * TODO HB impl will be changed!
				 */
				if (d.getLastArrAirport().isHb(hbNdx)
						&& (((currentPair != null) && pairRuleContext.getAppendabilityCheckerProxy().isAppendable(hbNdx, currentPair, d, true))
								|| pairRuleContext.getStarterCheckerProxy().canBeStarter(hbNdx, d))) {
					if (currentPair != null) {
						pairRuleContext.getAggregatorProxy().appendFw(currentPair, d);
						if (pairRuleContext.getFinalCheckerProxy().acceptable(hbNdx, currentPair)) {
							best = d;
							bestNumOfDh = dNumOfDh;
							bestDhDurationInMins = dDhDurationInMins;
							bestAvgNumOfIncludingDutiesOfTheSameLegs = dAvgNumOfIncludingDutiesOfTheSameLegs;
						}
						pairRuleContext.getAggregatorProxy().removeLast(currentPair);
					} else {
						best = d;
						bestNumOfDh = dNumOfDh;
						bestDhDurationInMins = dDhDurationInMins;
						bestAvgNumOfIncludingDutiesOfTheSameLegs = dAvgNumOfIncludingDutiesOfTheSameLegs;
					}
				}
			}
		}

		return best;
	}

	private DutyView fetchConnectionDutyFw(Pair currentPair, DutyView previousDuty, int heuristicNo, int[] numOfCoveringsInDuties, int[] blockTimeOfCoveringsInDuties) {

		DutyView res = null;
		DutyView[] nextDuties = null;

		int lastAirportNdx = previousDuty.getLastArrAirport().getNdx();

		int hourCounter = 0;

		while (true) {
		/*
		 * TODO HB impl will be changed!
		 */
			if (heuristicNo == 2)
				nextDuties = this.hbArrDutyIndexByDepAirportNdxBrieftime.getArray(lastAirportNdx, previousDuty.getNextBriefTime(hbNdx).plusHours(hourCounter));
			else
				nextDuties = this.dutyIndexByDepAirportNdxBrieftime.getArray(lastAirportNdx, previousDuty.getNextBriefTime(hbNdx).plusHours(hourCounter));
			if ((nextDuties != null)
					&& (nextDuties.length > 0)) {
				if (heuristicNo == 0)
					res = this.fetchBestDhEffectiveDuty(currentPair, res, nextDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
				else
					if (heuristicNo == 1)
						res = this.fetchBestActiveBlockTimeEffectiveDuty(currentPair, res, nextDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
					else
						if (heuristicNo == 2)
							res = this.fetchBestLayoverEffectiveDuty(currentPair, res, nextDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
			}
			hourCounter++;
			if (hourCounter > HeurosSystemParam.maxIdleTimeInAPairInHours)
				break;
		}
		return res;
	}

	/*
	 * TODO HB impl will be changed.
	 */
	private DutyView fetchHbDepArrDuty(Leg legToCover, int heuristicNo, int[] numOfCoveringsInDuties, int[] blockTimeOfCoveringsInDuties) {
		DutyView[] hbDepDuties = this.hbDepArrDutyIndexByLegNdx.getArray(hbNdx, legToCover.getNdx());
		if (heuristicNo == 0)
			return this.fetchBestDhEffectiveDuty(null, null, hbDepDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, true, true);
		else
			if (heuristicNo == 1)
				return this.fetchBestActiveBlockTimeEffectiveDuty(null, null, hbDepDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, true, true);
			else
				if (heuristicNo == 2)
					return this.fetchBestLayoverEffectiveDuty(null, null, hbDepDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, true, true);
		return null;
	}

	private DutyView fetchHbDepDuty(Leg legToCover, int heuristicNo, int[] numOfCoveringsInDuties, int[] blockTimeOfCoveringsInDuties) {
		DutyView[] hbDepDuties = null;
//		if (heuristicNo == 2)
//			hbDepDuties = this.hbDepArrDutyIndexByLegNdx.getArray(hbNdx, legToCover.getNdx());
//		else
			hbDepDuties = this.hbDepDutyIndexByLegNdx.getArray(hbNdx, legToCover.getNdx());
		if (heuristicNo == 0)
			return this.fetchBestDhEffectiveDuty(null, null, hbDepDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, true, false);
		else
			if (heuristicNo == 1)
				return this.fetchBestActiveBlockTimeEffectiveDuty(null, null, hbDepDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, true, false);
			else
				if (heuristicNo == 2)
					return this.fetchBestLayoverEffectiveDuty(null, null, hbDepDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, true, false);
		return null;
	}

	private DutyView fetchNonHbDuty(Leg legToCover, int heuristicNo, int[] numOfCoveringsInDuties, int[] blockTimeOfCoveringsInDuties) {
		DutyView[] hbDepDuties = this.nonHbDutyIndexByLegNdx.getArray(hbNdx, legToCover.getNdx());
		if (heuristicNo == 0)
			return this.fetchBestDhEffectiveDuty(null, null, hbDepDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, false, false);
		else
			if (heuristicNo == 1)
				return this.fetchBestActiveBlockTimeEffectiveDuty(null, null, hbDepDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, false, false);
			else
				if (heuristicNo == 2)
					return this.fetchBestLayoverEffectiveDuty(null, null, hbDepDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, false, false);
		return null;
	}

	private DutyView fetchHbArrDuty(Leg legToCover, int heuristicNo, int[] numOfCoveringsInDuties, int[] blockTimeOfCoveringsInDuties) {
		DutyView[] hbDepDuties = this.hbArrDutyIndexByLegNdx.getArray(hbNdx, legToCover.getNdx());
		if (heuristicNo == 0)
			return this.fetchBestDhEffectiveDuty(null, null, hbDepDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, false, true);
		else
			if (heuristicNo == 1)
				return this.fetchBestActiveBlockTimeEffectiveDuty(null, null, hbDepDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, false, true);
			else
				if (heuristicNo == 2)
					return this.fetchBestLayoverEffectiveDuty(null, null, hbDepDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, false, true);
		return null;
	}

	@Override
	public List<Pair> decode(Chromosome<Integer> chromosome) {

		List<Pair> solution = new ArrayList<Pair>();

		int geneNdx = 0;
		int uncoveredLegs = 0;

		double fitness = 0.0;

		int reOrderedLegNdx = -1;
		int[] numOfLegCoverings = new int[this.legRepository.getModels().size()];
		int[] numOfCoveringsInDuties = new int[this.dutyRepository.getModels().size()];
		int[] blockTimeOfCoveringsInDuties = new int[this.dutyRepository.getModels().size()];

		while (true) {
			reOrderedLegNdx = this.getNextLegNdxToCover(reOrderedLegNdx, numOfLegCoverings);
			if (reOrderedLegNdx == Integer.MAX_VALUE)
				break;

			Leg legToCover = this.reOrderedLegs.get(reOrderedLegNdx);
			int heuristicNo = chromosome.getGeneValue(geneNdx);
			geneNdx++;

			/*
			 * TODO HB impl will be changed.
			 */
			int fw = 0;
			DutyView duty = null;
			if (legToCover.hasHbDepArrDutyPair(hbNdx)) {
				duty = this.fetchHbDepArrDuty(legToCover, heuristicNo, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
				fw = 3;
			}
			if ((duty == null) && legToCover.hasHbDepDutyPair(hbNdx)) {
				duty = this.fetchHbDepDuty(legToCover, heuristicNo, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
				fw = 1;
			}
			if ((duty == null) && legToCover.hasNonHbDutyPair(hbNdx)) {
				duty = this.fetchNonHbDuty(legToCover, heuristicNo, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
				fw = 0;
			}
			if ((duty == null) && legToCover.hasHbArrDutyPair(hbNdx)) {
				duty = this.fetchHbArrDuty(legToCover, heuristicNo, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
				fw = 2;
			}

			if (duty != null) {
				/*
				 * TODO Single base assumption!!!
				 */
				Pair pair = Pair.newInstance(hbNdx);
				while (true) {
					if (duty != null) {
						//	HB dep!
						if ((fw & 1) == 1) {
							if (pairRuleContext.getAppendabilityCheckerProxy().isAppendable(hbNdx, pair, duty, true))
								pairRuleContext.getAggregatorProxy().appendFw(pair, duty);
							else
								PairChromosomeDecoder.logger.error("Non appendable pairing!");
						} else {
							if (pairRuleContext.getAppendabilityCheckerProxy().isAppendable(hbNdx, pair, duty, false))
								pairRuleContext.getAggregatorProxy().appendBw(pair, duty);
							else
								PairChromosomeDecoder.logger.error("Non appendable pairing!");
						}

						for (int i = 0; i < duty.getNumOfLegs(); i++) {
							LegView leg = duty.getLegs().get(i);
							numOfLegCoverings[leg.getNdx()]++;
							DutyView[] dutiesOfLeg = this.dutyIndexByLegNdx.getArray(leg.getNdx());
							for (int j = 0; j < dutiesOfLeg.length; j++) {
								DutyView dutyOfLeg = dutiesOfLeg[j];
								numOfCoveringsInDuties[dutyOfLeg.getNdx()]++;
								blockTimeOfCoveringsInDuties[dutyOfLeg.getNdx()] += leg.getBlockTimeInMins();
							}
						}



						if (((fw & 1) == 1) && duty.getLastArrAirport().isHb(hbNdx)) {
							fw |= 2;
							if (pairRuleContext.getFinalCheckerProxy().acceptable(hbNdx, pair))
								solution.add(pair);
							else
								PairChromosomeDecoder.logger.error("Non valid pairing!");
							break;
						} else
							if (((fw & 2) == 2) && duty.getFirstDepAirport().isHb(hbNdx)) {
								fw |= 1;
								if (pairRuleContext.getFinalCheckerProxy().acceptable(hbNdx, pair))
									solution.add(pair);
								else
									PairChromosomeDecoder.logger.error("Non valid pairing!");
								break;
							} else {
								if (pairRuleContext.getExtensibilityCheckerProxy().isExtensible(hbNdx, pair)) {
									heuristicNo = chromosome.getGeneValue(geneNdx);
									geneNdx++;
									duty = this.fetchConnectionDuty(pair, duty, heuristicNo, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
//System.out.println();
//System.out.println(duty);
								} else
									PairChromosomeDecoder.logger.error("Non extensible pairing!");
							}
					} else {
//						PairChromosomeDecoder.logger.error("No duty is found!");
						uncoveredLegs++;
						for (int a = 0; a < pair.getNumOfDuties(); a++) {
							DutyView rduty = pair.getDuties().get(a);
							for (int i = 0; i < rduty.getNumOfLegs(); i++) {
								LegView leg = rduty.getLegs().get(i);
								numOfLegCoverings[leg.getNdx()]--;
								DutyView[] dutiesOfLeg = this.dutyIndexByLegNdx.getArray(leg.getNdx());
								for (int j = 0; j < dutiesOfLeg.length; j++) {
									DutyView dutyOfLeg = dutiesOfLeg[j];
									numOfCoveringsInDuties[dutyOfLeg.getNdx()]--;
									blockTimeOfCoveringsInDuties[dutyOfLeg.getNdx()] -= leg.getBlockTimeInMins();
								}
							}
						}
						break;
					}
				}
			} else {
//				PairChromosomeDecoder.logger.error("No hb dep duty is found for " + legToCover);
				uncoveredLegs++;
			}
		}

		for (int i = 0; i < solution.size(); i++) {
			fitness += getPairCost(2, solution.get(i));
		}
		int numOfDeadheads = 0;
		for (int i = 0; i < this.legRepository.getModels().size(); i++) {
			if ((numOfLegCoverings[i] > 1)
					|| (!this.legRepository.getModels().get(i).isCover())) {
				fitness += (2.0 * (this.legRepository.getModels().get(i).getBlockTimeInMins() / 60.0) * dhPenalty);
				numOfDeadheads++;
			}
		}
		chromosome.setFitness(fitness + uncoveredLegs * 100000000);
		chromosome.setInfo("uncoveredLegs: " + uncoveredLegs + ", numOfDeadheads: " + numOfDeadheads + ", fitness: " + fitness);
		return solution;
	}

	private int hotelTransportTime = 30;

	private int hotelCostDomPerCrewPerDay = 10000;	//	1000;
	private int hotelCostIntPerCrewPerDay = 10000;	//	1000;

	private int hotelTransferCost = 40;

	private int getHotelCostPerCrew(AirportView ap, int gmtDiff, LocalDateTime debriefTime, LocalDateTime briefTime) {
		if (briefTime == null)
			return 0;

		LocalDateTime hotelIn = debriefTime.plusMinutes(hotelTransportTime + gmtDiff);
		LocalDateTime hotelOut = briefTime.plusMinutes(- hotelTransportTime + gmtDiff);

		LocalDateTime hotelIn00 = hotelIn.truncatedTo(ChronoUnit.DAYS);
		LocalDateTime hotelOut00 = hotelOut.minusSeconds(1).truncatedTo(ChronoUnit.DAYS);
		int numOfLayNights = (int) ChronoUnit.DAYS.between(hotelIn00, hotelOut00);

		if ((ChronoUnit.DAYS.between(hotelIn, hotelOut) < 1.0)
				|| (numOfLayNights == 0)) {

        	if (ap.isDomestic())
        		return hotelCostDomPerCrewPerDay;
        	else
        		return hotelCostIntPerCrewPerDay;
        }
        else
        	if (ap.isDomestic())
        		return hotelCostDomPerCrewPerDay * numOfLayNights;
        	else
        		return hotelCostIntPerCrewPerDay * numOfLayNights;
    }

	private double dutyDayPenalty = 60000.0;	//	20000.0;
	private double dutyHourPenalty = 1050.0;	//	0.0;
	private double dhPenalty = 500000.0;	//	10000.0;
	private double acChangePenalty = 250.0;	//	20.0;
	private double squareRestPenalty = 2.0;
	private int longRestLimit = 14 * 60;
	private double longRestPenalty = 400;	//	700;
	private double longConnPenalty = 100;	//	40;
	private double augmentionHourPenalty = 100000.0;	//	2000;
	private double specialDhPenalty = 10000.0;
	private double augmentionDayPenalty = 0.0;	//	4000.0;

	/*
	 * TODO HB impl will be changed!
	 */
	public double getDutyCost(int cc, DutyView d, boolean hbDep, LocalDateTime nt) {
		LocalDateTime sh = d.getBriefTime(hbNdx);
		LocalDateTime eh = d.getDebriefTime(hbNdx);

		if (nt == null)
			eh = d.getDebriefDayEnding(hbNdx);
		else
			eh = nt;
		if (hbDep)
			sh = d.getBriefDayBeginning(hbNdx);

		double dutyDurationInMins = ChronoUnit.MINUTES.between(sh, eh);
		double dCost = 0.0;

		dCost += cc * dutyDurationInMins * dutyDayPenalty / (24.0 * 60.0);

		/*
		 * TODO HB impl will be changed!
		 */
		if (hbDep)
			dCost += cc * d.getDutyDurationInMins(hbNdx) * dutyHourPenalty / 60.0;
		else
			dCost += cc * d.getDutyDurationInMins(hbNdx) * dutyHourPenalty / 60.0;

		dCost += acChangePenalty * d.getNumOfAcChanges();

		if (nt != null) {
			double idleTime = ChronoUnit.MINUTES.between(d.getDebriefTime(hbNdx), nt);

			dCost += cc * getHotelCostPerCrew(d.getLastArrAirport(), d.getLastLeg().getArrOffset(), d.getDebriefTime(hbNdx), nt);

			dCost += 2.0 * hotelTransferCost;

			if (squareRestPenalty * idleTime * idleTime / 10000.0 > (squareRestPenalty * cc * dutyDayPenalty / 20.0))
				dCost += squareRestPenalty * idleTime * idleTime / 10000.0;
			else
				dCost += (squareRestPenalty * cc * dutyDayPenalty) / 20.0;

			if (idleTime > longRestLimit) {
				dCost += (idleTime - longRestLimit) * longRestPenalty / 60.0;
			}

		} else
			if (hbDep) {
				dCost += cc * dutyDayPenalty / 20.0;
			}

		dCost += d.getLongConnDiff() * longConnPenalty / 60.0;

		if (hbDep) {
			if (d.getAugmented(hbNdx) > 0) {
				dCost += d.getDutyDurationInMins(hbNdx) * augmentionHourPenalty / 60.0;
				dCost += augmentionDayPenalty;
			}
		} else {
			if (d.getAugmented(hbNdx) > 0) {
				dCost += d.getDutyDurationInMins(hbNdx) * augmentionHourPenalty / 60.0;
				dCost += augmentionDayPenalty;
			}
		}

		dCost += d.getNumOfSpecialFlights() * specialDhPenalty;

//		if (includeDh)
//			dCost += cc * (d.getBlockTimeInMins() / 60.0) * dhPenalty;
//		else
//			dCost -= cc * (d.getBlockTimeInMins() / 60.0) * dhPenalty;

		return dCost;
	}

	public double getPairCost(int cc, Pair p) {
		DutyView pd = p.getFirstDuty();
		if (p.getNumOfDuties() == 1)
			return getDutyCost(cc, pd, true, null);
		else {
			DutyView nd = null;
			double pCost = 0.0;
			for (int i = 1; i < p.getNumOfDuties(); i++) {
				nd = p.getDuties().get(i);
				pCost += getDutyCost(cc, pd, i == 1, nd.getBriefTime(hbNdx));
				pd = nd;
			}
			pCost += getDutyCost(cc, nd, false, null);
			return pCost;
		}
	}
}
