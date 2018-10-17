package org.heuros.hyperpair.ga;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.heuros.core.data.ndx.OneDimIndexInt;
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
import org.heuros.hyperpair.heuristic.PairingGenerator;

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
//	private DutyRuleContext dutyRuleContext = null;
//	private PairRuleContext pairRuleContext = null;

	private OneDimIndexInt<Duty> dutyIndexByLegNdx = null;
//	private TwoDimIndexIntXInt<Duty> hbDepArrDutyIndexByLegNdx = null;
//	private TwoDimIndexIntXInt<Duty> hbDepDutyIndexByLegNdx = null;
//	private TwoDimIndexIntXInt<Duty> nonHbDutyIndexByLegNdx = null;
//	private TwoDimIndexIntXInt<Duty> hbArrDutyIndexByLegNdx = null;
//	private TwoDimIndexIntXLocalDateTime<Duty> dutyIndexByDepAirportNdxBrieftime = null;
//	private TwoDimIndexIntXLocalDateTime<Duty> dutyIndexByArrAirportNdxNextBrieftime = null;
////	private TwoDimIndexIntXLocalDateTime<Duty> hbArrDutyIndexByDepAirportNdxBrieftime = null;

	private PairingGenerator pairingGenerator = null;

	private List<Leg> reOrderedLegs = null;

//	private static Random random = new Random();
	private double difficultyScoreMax = Integer.MAX_VALUE;
	private int[] difficultyScoreCumulative = null;

	public void orderLegs() {
		if (this.bestNumOfLegCoverings != null) {
			this.difficultyScoreMax = 0;
			for (int i = 0; i < this.bestNumOfLegCoverings.length; i++) {
				if (this.bestNumOfLegCoverings[i] > 0)
					this.difficultyScoreCumulative[i]++;
				if (this.difficultyScoreCumulative[i] > this.difficultyScoreMax)
					this.difficultyScoreMax = this.difficultyScoreCumulative[i];
			}
			this.reOrderedLegs = this.legRepository.getModels().parallelStream().sorted(new Comparator<Leg>() {
				@Override
				public int compare(Leg a, Leg b) {
					if (difficultyScoreCumulative[a.getNdx()]/difficultyScoreMax > difficultyScoreCumulative[b.getNdx()]/difficultyScoreMax)
						return -1;
					else
						if (difficultyScoreCumulative[a.getNdx()]/difficultyScoreMax < difficultyScoreCumulative[b.getNdx()]/difficultyScoreMax)
							return 1;
					return 0;
				}
			}).collect(Collectors.toList());
		} else {
			this.reOrderedLegs = this.legRepository.getModels().parallelStream().sorted(new Comparator<Leg>() {
				@Override
				public int compare(Leg a, Leg b) {
					if (a.getNdx() < b.getNdx())
						return -1;
					else
						if (a.getNdx() > b.getNdx())
							return 1;
					return 0;
				}
			}).collect(Collectors.toList());
		}
		PairChromosomeDecoder.logger.info("Leg list is reordered according to difficulty scores provided.");
	}

	public PairChromosomeDecoder setLegRepository(LegRepository legRepository) {
		this.legRepository = legRepository;
		this.difficultyScoreCumulative = new int[legRepository.getModels().size()];
		return this;
	}

	public PairChromosomeDecoder setDutyRepository(DutyRepository dutyRepository) {
		this.dutyRepository = dutyRepository;
		return this;
	}

//	public PairChromosomeDecoder setDutyRuleContext(DutyRuleContext dutyRuleContext) {
//		this.dutyRuleContext = dutyRuleContext;
//		return this;
//	}
//
//	public PairChromosomeDecoder setPairRuleContext(PairRuleContext pairRuleContext) {
//		this.pairRuleContext = pairRuleContext;
//		return this;
//	}

	public PairChromosomeDecoder setDutyIndexByLegNdx(OneDimIndexInt<Duty> dutyIndexByLegNdx) {
		this.dutyIndexByLegNdx = dutyIndexByLegNdx;
		return this;
	}

//	public PairChromosomeDecoder setHbDepArrDutyIndexByLegNdx(TwoDimIndexIntXInt<Duty> hbDepArrDutyIndexByLegNdx) {
//		this.hbDepArrDutyIndexByLegNdx = hbDepArrDutyIndexByLegNdx;
//		return this;
//	}
//
//	public PairChromosomeDecoder setHbDepDutyIndexByLegNdx(TwoDimIndexIntXInt<Duty> hbDepDutyIndexByLegNdx) {
//		this.hbDepDutyIndexByLegNdx = hbDepDutyIndexByLegNdx;
//		return this;
//	}
//
//	public PairChromosomeDecoder setNonHbDutyIndexByLegNdx(TwoDimIndexIntXInt<Duty> nonHbDutyIndexByLegNdx) {
//		this.nonHbDutyIndexByLegNdx = nonHbDutyIndexByLegNdx;
//		return this;
//	}
//
//	public PairChromosomeDecoder setHbArrDutyIndexByLegNdx(TwoDimIndexIntXInt<Duty> hbArrDutyIndexByLegNdx) {
//		this.hbArrDutyIndexByLegNdx = hbArrDutyIndexByLegNdx;
//		return this;
//	}
//
//	public PairChromosomeDecoder setDutyIndexByDepAirportNdxBrieftime(
//			TwoDimIndexIntXLocalDateTime<Duty> dutyIndexByDepAirportNdxBrieftime) {
//		this.dutyIndexByDepAirportNdxBrieftime = dutyIndexByDepAirportNdxBrieftime;
//		return this;
//	}
//
//	public PairChromosomeDecoder setDutyIndexByArrAirportNdxNextBrieftime(
//			TwoDimIndexIntXLocalDateTime<Duty> dutyIndexByArrAirportNdxNextBrieftime) {
//		this.dutyIndexByArrAirportNdxNextBrieftime = dutyIndexByArrAirportNdxNextBrieftime;
//		return this;
//	}
//
////	public PairChromosomeDecoder setHbArrDutyIndexByDepAirportNdxBrieftime(
////			TwoDimIndexIntXLocalDateTime<Duty> hbArrDutyIndexByDepAirportNdxBrieftime) {
////		this.hbArrDutyIndexByDepAirportNdxBrieftime = hbArrDutyIndexByDepAirportNdxBrieftime;
////		return this;
////	}

	public PairChromosomeDecoder setPairingGenerator(PairingGenerator pairingGenerator) {
		this.pairingGenerator = pairingGenerator;
		return this;
	}

	private int getNextLegNdxToCover(int prevReOrderedLegNdx, int[] numOfLegCoverings) {
		for (int i = prevReOrderedLegNdx + 1; i < this.reOrderedLegs.size(); i++) {
			if (this.reOrderedLegs.get(i).isCover()
					&& (this.reOrderedLegs.get(i).hasPair(hbNdx))
					&& (numOfLegCoverings[this.reOrderedLegs.get(i).getNdx()] == 0))
				return i;
		}
		return Integer.MAX_VALUE;
	}

//	/*
//	 * HEURISTICS
//	 */
//	private boolean evaluateRules(Pair currentPair, DutyView connDuty, DutyView d, boolean fw) {
//		if (currentPair == null) {
//			return pairRuleContext.getStarterCheckerProxy().canBeStarter(hbNdx, d);
//		} else
//			if (fw) {
//				return this.evaluateFwRules(currentPair, connDuty, d);
//			} else
//				return this.evaluateBwRules(currentPair, connDuty, d);
//	}
//
//	private boolean evaluateFwRules(Pair currentPair, DutyView lastDuty, DutyView d) {
//		if (dutyRuleContext.getConnectionCheckerProxy().areConnectable(hbNdx, lastDuty, d)) {
//			if (pairRuleContext.getAppendabilityCheckerProxy().isAppendable(hbNdx, currentPair, d, true)) {
//				pairRuleContext.getAggregatorProxy().appendFw(currentPair, d);
//				if (pairRuleContext.getFinalCheckerProxy().acceptable(hbNdx, currentPair)) {
//					pairRuleContext.getAggregatorProxy().removeLast(currentPair);
//					return true;
//				}
//				pairRuleContext.getAggregatorProxy().removeLast(currentPair);
//			}
//		}
//		return false;
//	}
//
//	private boolean evaluateBwRules(Pair currentPair, DutyView firstDuty, DutyView d) {
//		if (dutyRuleContext.getConnectionCheckerProxy().areConnectable(hbNdx, d, firstDuty)) {
//			if (pairRuleContext.getAppendabilityCheckerProxy().isAppendable(hbNdx, currentPair, d, false)) {
//				pairRuleContext.getAggregatorProxy().appendBw(currentPair, d);
//				if (pairRuleContext.getFinalCheckerProxy().acceptable(hbNdx, currentPair)) {
//					pairRuleContext.getAggregatorProxy().removeFirst(currentPair);
//					return true;
//				}
//				pairRuleContext.getAggregatorProxy().removeFirst(currentPair);
//			}
//		}
//		return false;
//	}
//
//	private DutyView fetchBestDhEffectiveDuty(Pair currentPair, DutyView connDuty, 
//												DutyView bestSoFar, DutyView[] candidates, int[] numOfCoveringsInDuties, int[] blockTimeOfCoveringsInDuties, 
//												boolean fw, boolean mandHbDep, boolean mandHbArr) {
//		DutyView best = bestSoFar;
//		int bestNumOfDh = Integer.MAX_VALUE;
//		int bestDhDurationInMins = Integer.MAX_VALUE;
//		double bestAvgNumOfIncludingDutiesOfTheSameLegs = Double.MAX_VALUE;
//		if (bestSoFar != null) {
//			best = bestSoFar;
//			bestNumOfDh = bestSoFar.getNumOfLegsPassive() + numOfCoveringsInDuties[bestSoFar.getNdx()];
//			bestDhDurationInMins = bestSoFar.getBlockTimeInMinsPassive() + blockTimeOfCoveringsInDuties[bestSoFar.getNdx()];
//			bestAvgNumOfIncludingDutiesOfTheSameLegs = (1.0 * bestSoFar.getTotalNumOfIncludingDutiesOfTheSameLegs()) / bestSoFar.getNumOfLegs();
//		}
//
//		for (DutyView d: candidates) {
//			if (d.hasPairing(hbNdx)
//					&& ((!mandHbDep) || (d.getFirstDepAirport().isHb(hbNdx)))
//					&& ((!mandHbArr) || (d.getLastArrAirport().isHb(hbNdx)))) {
//
//				int dNumOfDh = d.getNumOfLegsPassive() + numOfCoveringsInDuties[d.getNdx()];
//				int dDhDurationInMins = d.getBlockTimeInMinsPassive() + blockTimeOfCoveringsInDuties[d.getNdx()];
//				double dAvgNumOfIncludingDutiesOfTheSameLegs = (1.0 * d.getTotalNumOfIncludingDutiesOfTheSameLegs()) / d.getNumOfLegs();
//
//				if ((bestNumOfDh > dNumOfDh)
//						|| ((bestNumOfDh == dNumOfDh) && (bestDhDurationInMins > dDhDurationInMins))
//						|| ((bestNumOfDh == dNumOfDh) && (bestDhDurationInMins == dDhDurationInMins) && (bestAvgNumOfIncludingDutiesOfTheSameLegs > dAvgNumOfIncludingDutiesOfTheSameLegs))) {
//					if (this.evaluateRules(currentPair, connDuty, d, fw)) {
//						best = d;
//						bestNumOfDh = dNumOfDh;
//						bestDhDurationInMins = dDhDurationInMins;
//						bestAvgNumOfIncludingDutiesOfTheSameLegs = dAvgNumOfIncludingDutiesOfTheSameLegs;
//					}
//				}
//			}
//		}
//
//		return best;
//	}
//
//	private DutyView fetchBestActiveBlockTimeEffectiveDuty(Pair currentPair, DutyView connDuty,
//															DutyView bestSoFar, DutyView[] candidates, int[] numOfCoveringsInDuties, int[] blockTimeOfCoveringsInDuties, 
//															boolean fw, boolean mandHbDep, boolean mandHbArr) {
//		DutyView best = bestSoFar;
//		int bestActiveBlocktimeInMins = 0;
//		double bestAvgNumOfIncludingDutiesOfTheSameLegs = Double.MAX_VALUE;
//		if (bestSoFar != null) {
//			best = bestSoFar;
//			bestActiveBlocktimeInMins = bestSoFar.getBlockTimeInMinsActive() - blockTimeOfCoveringsInDuties[bestSoFar.getNdx()];
//			bestAvgNumOfIncludingDutiesOfTheSameLegs = (1.0 * bestSoFar.getTotalNumOfIncludingDutiesOfTheSameLegs()) / bestSoFar.getNumOfLegs();
//		}
//
//		for (DutyView d: candidates) {
//			if (d.hasPairing(hbNdx)
//					&& ((!mandHbDep) || (d.getFirstDepAirport().isHb(hbNdx)))
//					&& ((!mandHbArr) || (d.getLastArrAirport().isHb(hbNdx)))) {
//				int dActiveBlocktimeInMins = d.getBlockTimeInMinsActive() - blockTimeOfCoveringsInDuties[d.getNdx()];
//				double dAvgNumOfIncludingDutiesOfTheSameLegs = (1.0 * d.getTotalNumOfIncludingDutiesOfTheSameLegs()) / d.getNumOfLegs();
//
//				if ((bestActiveBlocktimeInMins < dActiveBlocktimeInMins)
//						|| ((bestActiveBlocktimeInMins == dActiveBlocktimeInMins) && (bestAvgNumOfIncludingDutiesOfTheSameLegs > dAvgNumOfIncludingDutiesOfTheSameLegs))) {
//					if (this.evaluateRules(currentPair, connDuty, d, fw)) {
//						best = d;
//						bestActiveBlocktimeInMins = dActiveBlocktimeInMins;
//						bestAvgNumOfIncludingDutiesOfTheSameLegs = dAvgNumOfIncludingDutiesOfTheSameLegs;
//					}
//				}
//			}
//		}
//
//		return best;
//	}
//
//	/*
//	 * FW/BW search for duties.
//	 */
//	private DutyView fetchConnectionDutyFw(Pair currentPair, DutyView lastDuty, 
//											int heuristicNo, int[] numOfCoveringsInDuties, int[] blockTimeOfCoveringsInDuties, 
//											boolean fw, boolean mandHbArr) {
//
//		DutyView res = null;
//
//		int lastAirportNdx = lastDuty.getLastArrAirport().getNdx();
//
//		int hourCounter = 0;
//
//		while (true) {
//			DutyView[] nextDuties = this.dutyIndexByDepAirportNdxBrieftime.getArray(lastAirportNdx, lastDuty.getNextBriefTime(hbNdx).plusHours(hourCounter));
//			if ((nextDuties != null)
//					&& (nextDuties.length > 0)) {
//				if (heuristicNo == 0)
////					res = this.fetchBestLayoverEffectiveDuty(currentPair, res, nextDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, false, true);
//					res = this.fetchBestDhEffectiveDuty(currentPair, lastDuty, res, nextDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, fw, false, true);
//				else
//					if (heuristicNo == 1)
//						res = this.fetchBestDhEffectiveDuty(currentPair, lastDuty, res, nextDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, fw, false, mandHbArr);
//					else
//						if (heuristicNo == 2)
//							res = this.fetchBestActiveBlockTimeEffectiveDuty(currentPair, lastDuty, res, nextDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, fw, false, mandHbArr);
//			}
//			hourCounter++;
//			if (hourCounter > HeurosSystemParam.maxIdleTimeInAPairInHours)
//				break;
//		}
//		return res;
//	}
//
//	private DutyView fetchConnectionDutyBw(Pair currentPair, DutyView firstDuty, 
//											int heuristicNo, int[] numOfCoveringsInDuties, int[] blockTimeOfCoveringsInDuties, 
//											boolean fw, boolean mandHbDep) {
//
//		DutyView res = null;
//
//		int firstAirportNdx = firstDuty.getFirstDepAirport().getNdx();
//
//		int hourCounter = 0;
//
//		while (true) {
//			DutyView[] prevDuties = this.dutyIndexByArrAirportNdxNextBrieftime.getArray(firstAirportNdx, firstDuty.getBriefTime(hbNdx).minusHours(hourCounter));
//			if ((prevDuties != null)
//					&& (prevDuties.length > 0)) {
//				if (heuristicNo == 0)
////					res = this.fetchBestLayoverEffectiveDuty(currentPair, res, prevDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, true, false);
//					res = this.fetchBestDhEffectiveDuty(currentPair, firstDuty, res, prevDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, fw, true, false);
//				else
//					if (heuristicNo == 1)
//						res = this.fetchBestDhEffectiveDuty(currentPair, firstDuty, res, prevDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, fw, mandHbDep, false);
//					else
//						if (heuristicNo == 2)
//							res = this.fetchBestActiveBlockTimeEffectiveDuty(currentPair, firstDuty, res, prevDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, fw, mandHbDep, false);
//			}
//			hourCounter++;
//			if (hourCounter > HeurosSystemParam.maxIdleTimeInAPairInHours)
//				break;
//		}
//		return res;
//	}
//
//	/*
//	 * Select INITIAL Duties.
//	 */
//	private DutyView fetchInitialHbDepArrDuty(Leg legToCover, int heuristicNo, int[] numOfCoveringsInDuties, int[] blockTimeOfCoveringsInDuties) {
//		DutyView[] hbDepDuties = this.hbDepArrDutyIndexByLegNdx.getArray(hbNdx, legToCover.getNdx());
//		if (heuristicNo == 0)
////			return this.fetchBestLayoverEffectiveDuty(null, null, hbDepDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, true, true);
//			return this.fetchBestDhEffectiveDuty(null, null, null, hbDepDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, true, true, true);
//		else
//			if (heuristicNo == 1)
//				return this.fetchBestDhEffectiveDuty(null, null, null, hbDepDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, true, true, true);
//			else
//				if (heuristicNo == 2)
//					return this.fetchBestActiveBlockTimeEffectiveDuty(null, null, null, hbDepDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, true, true, true);
//		return null;
//	}
//
//	private DutyView fetchInitialHbDepDuty(Leg legToCover, int heuristicNo, int[] numOfCoveringsInDuties, int[] blockTimeOfCoveringsInDuties) {
//		DutyView[] hbDepDuties = this.hbDepDutyIndexByLegNdx.getArray(hbNdx, legToCover.getNdx());
//		if (heuristicNo == 0)
////			return this.fetchBestLayoverEffectiveDuty(null, null, hbDepDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, true, false);
//			return this.fetchBestDhEffectiveDuty(null, null, null, hbDepDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, true, true, false);
//		else
//			if (heuristicNo == 1)
//				return this.fetchBestDhEffectiveDuty(null, null, null, hbDepDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, true, true, false);
//			else
//				if (heuristicNo == 2)
//					return this.fetchBestActiveBlockTimeEffectiveDuty(null, null, null, hbDepDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, true, true, false);
//		return null;
//	}
//
//	private DutyView fetchInitialNonHbDuty(Leg legToCover, int heuristicNo, int[] numOfCoveringsInDuties, int[] blockTimeOfCoveringsInDuties) {
//		DutyView[] hbDepDuties = this.nonHbDutyIndexByLegNdx.getArray(hbNdx, legToCover.getNdx());
//		if (heuristicNo == 0)
////			return this.fetchBestLayoverEffectiveDuty(null, null, hbDepDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, false, false);
//			return this.fetchBestDhEffectiveDuty(null, null, null, hbDepDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, false, false, false);
//		else
//			if (heuristicNo == 1)
//				return this.fetchBestDhEffectiveDuty(null, null, null, hbDepDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, false, false, false);
//			else
//				if (heuristicNo == 2)
//					return this.fetchBestActiveBlockTimeEffectiveDuty(null, null, null, hbDepDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, false, false, false);
//		return null;
//	}
//
//	private DutyView fetchInitialHbArrDuty(Leg legToCover, int heuristicNo, int[] numOfCoveringsInDuties, int[] blockTimeOfCoveringsInDuties) {
//		DutyView[] hbDepDuties = this.hbArrDutyIndexByLegNdx.getArray(hbNdx, legToCover.getNdx());
//		if (heuristicNo == 0)
////			return this.fetchBestLayoverEffectiveDuty(null, null, hbDepDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, false, true);
//			return this.fetchBestDhEffectiveDuty(null, null, null, hbDepDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, false, false, true);
//		else
//			if (heuristicNo == 1)
//				return this.fetchBestDhEffectiveDuty(null, null, null, hbDepDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, false, false, true);
//			else
//				if (heuristicNo == 2)
//					return this.fetchBestActiveBlockTimeEffectiveDuty(null, null, null, hbDepDuties, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, false, false, true);
//		return null;
//	}

	private void udpateStateVectors(Pair p,
									int[] numOfLegCoverings,
									int[] numOfCoveringsInDuties,
									int[] blockTimeOfCoveringsInDuties) {
		for (int i = 0; i < p.getNumOfDuties(); i++) {
			DutyView duty = p.getDuties().get(i);
			for (int j = 0; j < duty.getNumOfLegs(); j++) {
				LegView leg = duty.getLegs().get(j);
				numOfLegCoverings[leg.getNdx()]++;
				DutyView[] dutiesOfLeg = this.dutyIndexByLegNdx.getArray(leg.getNdx());
				for (int di = 0; di < dutiesOfLeg.length; di++) {
					DutyView dutyOfLeg = dutiesOfLeg[di];
					numOfCoveringsInDuties[dutyOfLeg.getNdx()]++;
					blockTimeOfCoveringsInDuties[dutyOfLeg.getNdx()] += leg.getBlockTimeInMins();
				}
			}
		}
	}

	@Override
	public List<Pair> decode(Chromosome<Integer> chromosome) {

for (int i = 0; i < chromosome.getChromosomeLength(); i++)
chromosome.setGeneValue(i, 0);

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

			int dept = HeurosSystemParam.maxPairingLengthInDays;

			Leg legToCover = this.reOrderedLegs.get(reOrderedLegNdx);

			int heuristicNo = chromosome.getGeneValue(geneNdx);

			Pair p = this.pairingGenerator.generatePairing(legToCover, 
															heuristicNo, 
															HeurosSystemParam.maxPairingLengthInDays,
															numOfCoveringsInDuties,
															blockTimeOfCoveringsInDuties);

			if (p != null) {
				this.udpateStateVectors(p, numOfLegCoverings, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
				geneNdx += p.getNumOfDuties();
			} else {
				PairChromosomeDecoder.logger.error("Pairing could not be found for " + legToCover);
				uncoveredLegs++;
			}
				



//			boolean hbDepStatus = false;
//			boolean hbArrStatus = false;
//			DutyView duty = null;
//			if ((heuristicNo == 0)
//					|| (legToCover.hasHbDepArrDutyPair(hbNdx)
//							&& (!legToCover.hasHbDepDutyPair(hbNdx)))) {
//				duty = this.fetchInitialHbDepArrDuty(legToCover, heuristicNo, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
//				if (duty != null) {
//					hbDepStatus = true;
//					hbArrStatus = true;
//				}
//			}
//			if ((duty == null) && legToCover.hasHbDepDutyPair(hbNdx)) {
//				duty = this.fetchInitialHbDepDuty(legToCover, heuristicNo, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
//				if (duty != null) {
//					hbDepStatus = true;
//				}
//			}
//			if ((duty == null) && legToCover.hasNonHbDutyPair(hbNdx)) {
//				duty = this.fetchInitialNonHbDuty(legToCover, heuristicNo, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
//			}
//			if ((duty == null) && legToCover.hasHbArrDutyPair(hbNdx)) {
//				duty = this.fetchInitialHbArrDuty(legToCover, heuristicNo, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
//				if (duty != null) {
//					hbArrStatus = true;
//				}
//			}

//			if (duty != null) {
//				/*
//				 * TODO Single base assumption!!!
//				 */
//				Pair pair = Pair.newInstance(hbNdx);
//				while (true) {
//					if (duty != null) {
//						//	HB dep!
//						if (hbDepStatus) {
//							if (pairRuleContext.getAppendabilityCheckerProxy().isAppendable(hbNdx, pair, duty, true)) {
//								pairRuleContext.getAggregatorProxy().appendFw(pair, duty);
//								dept--;
//								if (duty.getLastArrAirport().isHb(hbNdx))
//									hbArrStatus = true;
//							} else
//								PairChromosomeDecoder.logger.error("Non appendable hbDep pairing!");
//						} else
//							if (hbArrStatus) {
//								if (pairRuleContext.getAppendabilityCheckerProxy().isAppendable(hbNdx, pair, duty, false)) {
//									pairRuleContext.getAggregatorProxy().appendBw(pair, duty);
//									dept--;
//									if (duty.getFirstDepAirport().isHb(hbNdx))
//										hbDepStatus = true;
//								} else
//									PairChromosomeDecoder.logger.error("Non appendable hbArr pairing!");
//							} else {
//								if (pairRuleContext.getAppendabilityCheckerProxy().isAppendable(hbNdx, pair, duty, false)) {
//									pairRuleContext.getAggregatorProxy().appendBw(pair, duty);
//									dept--;
//									if (duty.getFirstDepAirport().isHb(hbNdx))
//										hbDepStatus = true;
//									if (duty.getLastArrAirport().isHb(hbNdx))
//										hbArrStatus = true;
//								} else
//									PairChromosomeDecoder.logger.error("Non appendable nonHb pairing!");
//							}
//
//						for (int i = 0; i < duty.getNumOfLegs(); i++) {
//							LegView leg = duty.getLegs().get(i);
//							numOfLegCoverings[leg.getNdx()]++;
//							DutyView[] dutiesOfLeg = this.dutyIndexByLegNdx.getArray(leg.getNdx());
//							for (int j = 0; j < dutiesOfLeg.length; j++) {
//								DutyView dutyOfLeg = dutiesOfLeg[j];
//								numOfCoveringsInDuties[dutyOfLeg.getNdx()]++;
//								blockTimeOfCoveringsInDuties[dutyOfLeg.getNdx()] += leg.getBlockTimeInMins();
//							}
//						}
//
//						if (hbDepStatus && duty.getLastArrAirport().isHb(hbNdx)) {
//							if (pairRuleContext.getFinalCheckerProxy().acceptable(hbNdx, pair)) {
//								solution.add(pair);
////								PairChromosomeDecoder.logger.debug(legToCover);
////								PairChromosomeDecoder.logger.debug(pair);
//							} else
//								PairChromosomeDecoder.logger.error("Non valid pairing!");
//							break;
//						} else
//							if (hbArrStatus && duty.getFirstDepAirport().isHb(hbNdx)) {
//								if (pairRuleContext.getFinalCheckerProxy().acceptable(hbNdx, pair)) {
//									solution.add(pair);
////									PairChromosomeDecoder.logger.debug(legToCover);
////									PairChromosomeDecoder.logger.debug(pair);
//								} else
//									PairChromosomeDecoder.logger.error("Non valid pairing!");
//								break;
//							} else {
//								if (pairRuleContext.getExtensibilityCheckerProxy().isExtensible(hbNdx, pair)) {
//									heuristicNo = chromosome.getGeneValue(geneNdx);
//									geneNdx++;
//									if (hbDepStatus)
//										duty = this.fetchConnectionDutyFw(pair, pair.getLastDuty(), heuristicNo, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, hbDepStatus, dept < 2);
//									else
//										if (hbArrStatus)
//											duty = this.fetchConnectionDutyBw(pair, pair.getFirstDuty(), heuristicNo, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, hbDepStatus, dept < 2);
//										else
//											duty = this.fetchConnectionDutyBw(pair, pair.getFirstDuty(), heuristicNo, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties, hbDepStatus, dept < 3);
//								} else
//									PairChromosomeDecoder.logger.error("Non extensible pairing!");
//							}
//					} else {
//						PairChromosomeDecoder.logger.error("No connection duty is found for " + legToCover);
//						for (int a = 0; a < pair.getNumOfDuties(); a++) {
//							DutyView rduty = pair.getDuties().get(a);
//							PairChromosomeDecoder.logger.error("Duty" + (a + 1));
//							PairChromosomeDecoder.logger.error(rduty);
//							for (int i = 0; i < rduty.getNumOfLegs(); i++) {
//								LegView leg = rduty.getLegs().get(i);
//								numOfLegCoverings[leg.getNdx()]--;
//								DutyView[] dutiesOfLeg = this.dutyIndexByLegNdx.getArray(leg.getNdx());
//								for (int j = 0; j < dutiesOfLeg.length; j++) {
//									DutyView dutyOfLeg = dutiesOfLeg[j];
//									numOfCoveringsInDuties[dutyOfLeg.getNdx()]--;
//									blockTimeOfCoveringsInDuties[dutyOfLeg.getNdx()] -= leg.getBlockTimeInMins();
//								}
//							}
//						}
//						uncoveredLegs++;
//						break;
//					}
//				}
//			} else {
//				PairChromosomeDecoder.logger.error("No initial duty is found for " + legToCover);
//				uncoveredLegs++;
//			}
		}

		for (int i = 0; i < solution.size(); i++) {
			fitness += getPairCost(2, solution.get(i));
		}
		int numOfDeadheads = 0;
		for (int i = 0; i < this.legRepository.getModels().size(); i++) {
			if (((numOfLegCoverings[i] > 0) && (!this.legRepository.getModels().get(i).isCover()))
					|| (((numOfLegCoverings[i] > 1) && this.legRepository.getModels().get(i).isCover()))) {
				fitness += (2.0 * (this.legRepository.getModels().get(i).getBlockTimeInMins() / 60.0) * dhPenalty);
				numOfDeadheads++;
			}
		}
		chromosome.setFitness(fitness + uncoveredLegs * 100000000);
		chromosome.setInfo("uncoveredLegs: " + uncoveredLegs + ", numOfDeadheads: " + numOfDeadheads + ", fitness: " + fitness);
		this.checkFitness(chromosome.getFitness(), numOfLegCoverings);
		return solution;
	}

	private double bestFitness = Double.MAX_VALUE;
	private int[] bestNumOfLegCoverings = null;

	private synchronized void checkFitness(double newFitness, int[] newNumOfLegCoverings) {
		if (newFitness < bestFitness) {
			this.bestFitness = newFitness;
			this.bestNumOfLegCoverings = newNumOfLegCoverings;
		}
	}

	/*
	 * COST calculations.
	 */

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
