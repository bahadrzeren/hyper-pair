package org.heuros.pair;

import org.heuros.core.ga.ISolutionCost;
import org.heuros.pair.conf.HeurosSystemParam;

public class SolutionCost implements ISolutionCost {
	private double totalHeurModDh = 0.0;
	private double totalHeurModEf = 0.0;

	public SolutionCost(double totalHeurModDh, double totalHeurModEf) {
		this.totalHeurModDh = totalHeurModDh;
		this.totalHeurModEf = totalHeurModEf;
	}

	public double getTotalHeurModDh() {
		return totalHeurModDh;
	}

	public double getTotalHeurModEf() {
		return totalHeurModEf;
	}

	@Override
	public boolean doesPerformBetterThan(ISolutionCost c) {
		if (c == null)
			return true;

		SolutionCost sc = (SolutionCost) c;

		double maxHeurModDh = 0.0;
		double maxHeurModEf = 0.0;

		if (this.totalHeurModDh > sc.totalHeurModDh)
			maxHeurModDh = this.totalHeurModDh;
		else
			maxHeurModDh = sc.totalHeurModDh;

		if (this.totalHeurModEf > sc.totalHeurModEf)
			maxHeurModEf = this.totalHeurModEf;
		else
			maxHeurModEf = sc.totalHeurModEf;

		return (HeurosSystemParam.weightHeurModDh * (this.totalHeurModDh - sc.totalHeurModDh) / maxHeurModDh +
				HeurosSystemParam.weightHeurModEf * (this.totalHeurModEf - sc.totalHeurModEf) / maxHeurModEf) < 0.0;
	}

	@Override
	public double getDistance(ISolutionCost worst) {
		SolutionCost wc = (SolutionCost) worst;
		return (HeurosSystemParam.weightHeurModDh * (this.totalHeurModDh - wc.totalHeurModDh) / wc.totalHeurModDh +
				HeurosSystemParam.weightHeurModEf * (this.totalHeurModEf - wc.totalHeurModEf) / wc.totalHeurModEf);		
	}

	@Override
	public String toString() {
		return "[TotHM_Dh: " + totalHeurModDh + ", TotHM_Ef: " + totalHeurModEf + "]";
	}
}
