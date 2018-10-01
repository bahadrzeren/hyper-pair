package org.heuros.hyperpair.heuristic;

import org.heuros.data.model.DutyView;
import org.heuros.data.model.Pair;

public interface DutySelectionHeuristic {
	public DutyView fetchBestEffectiveDutyFw(int hbNdx, Pair currentPair, DutyView bestSoFar, DutyView[] candidates, int[] numOfCoveringsInDuties, int[] blockTimeOfCoveringsInDuties);
	public DutyView fetchBestEffectiveDutyBw(int hbNdx, Pair currentPair, DutyView bestSoFar, DutyView[] candidates, int[] numOfCoveringsInDuties, int[] blockTimeOfCoveringsInDuties);
}
