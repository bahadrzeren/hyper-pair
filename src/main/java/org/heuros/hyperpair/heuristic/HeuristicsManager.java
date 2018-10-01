package org.heuros.hyperpair.heuristic;

import java.util.ArrayList;
import java.util.List;

import org.heuros.data.model.DutyView;
import org.heuros.data.model.Pair;

public class HeuristicsManager {
	private List<DutySelectionHeuristic> heuristics = new  ArrayList<DutySelectionHeuristic>();

	public HeuristicsManager addHeuristic(DutySelectionHeuristic heuristic) {
		this.heuristics.add(heuristic);
		return this;
	}

	public DutyView fetchBestEffectiveDutyFw(int heuristicsNdx, int hbNdx, Pair currentPair, DutyView bestSoFar, DutyView[] candidates, int[] numOfCoveringsInDuties, int[] blockTimeOfCoveringsInDuties) {
		return this.heuristics.get(heuristicsNdx).fetchBestEffectiveDutyFw(hbNdx, currentPair, bestSoFar, candidates, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
	}
	public DutyView fetchBestEffectiveDutyBw(int heuristicsNdx, int hbNdx, Pair currentPair, DutyView bestSoFar, DutyView[] candidates, int[] numOfCoveringsInDuties, int[] blockTimeOfCoveringsInDuties) {
		return this.heuristics.get(heuristicsNdx).fetchBestEffectiveDutyBw(hbNdx, currentPair, bestSoFar, candidates, numOfCoveringsInDuties, blockTimeOfCoveringsInDuties);
	}
}
