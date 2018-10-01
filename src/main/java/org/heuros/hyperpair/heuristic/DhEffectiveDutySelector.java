package org.heuros.hyperpair.heuristic;

import org.heuros.data.model.DutyView;
import org.heuros.data.model.Pair;
import org.heuros.rule.PairRuleContext;

public class DhEffectiveDutySelector implements DutySelectionHeuristic {

	private PairRuleContext pairRuleContext = null;

	public DhEffectiveDutySelector setPairRuleContext(PairRuleContext pairRuleContext) {
		this.pairRuleContext = pairRuleContext;
		return this;
	}

	@Override
	public DutyView fetchBestEffectiveDutyFw(int hbNdx, Pair currentPair, DutyView bestSoFar, DutyView[] candidates,
			int[] numOfCoveringsInDuties, int[] blockTimeOfCoveringsInDuties) {
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

	@Override
	public DutyView fetchBestEffectiveDutyBw(int hbNdx, Pair currentPair, DutyView bestSoFar, DutyView[] candidates,
			int[] numOfCoveringsInDuties, int[] blockTimeOfCoveringsInDuties) {
		// TODO Auto-generated method stub
		return null;
	}

}
