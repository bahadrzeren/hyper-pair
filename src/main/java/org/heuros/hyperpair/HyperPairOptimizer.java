package org.heuros.hyperpair;

import java.util.List;

import org.heuros.core.base.Processor;
import org.heuros.data.model.DutyView;
import org.heuros.data.model.Pair;
import org.heuros.rule.DutyRuleContext;
import org.heuros.rule.PairRuleContext;

public class HyperPairOptimizer implements Processor<DutyView, Pair> {

	protected DutyRuleContext dutyRuleContext = null;
	protected PairRuleContext pairRuleContext = null;

	public HyperPairOptimizer setDutyRuleContext(DutyRuleContext dutyRuleContext) {
		this.dutyRuleContext = dutyRuleContext;
		return this;
	}

	public HyperPairOptimizer setPairRuleContext(PairRuleContext pairRuleContext) {
		this.pairRuleContext = pairRuleContext;
		return this;
	}

	@Override
	public List<Pair> proceed() {

		return null;
	}
}
