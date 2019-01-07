package org.heuros.pair.ga.ga;

import java.util.List;

import org.heuros.core.base.Processor;
import org.heuros.core.ga.GeneticOptimizer;
import org.heuros.data.model.DutyView;
import org.heuros.data.model.Pair;

public class PairOptimizer extends GeneticOptimizer<Integer, Pair> implements Processor<DutyView, Pair> {

	@Override
	public List<Pair> proceed() {
		this.doMinimize();
		return this.getDecoder().decode(this.getBest());
	}
}
