package org.heuros.hyperpair;

import java.util.List;

import org.heuros.core.base.Processor;
import org.heuros.data.model.Duty;
import org.heuros.data.model.PairView;
import org.heuros.rule.DutyRuleContext;
import org.heuros.rule.PairRuleContext;

public class HyperPairOptimizer implements Processor<Duty, PairView> {

	protected DutyRuleContext dutyRuleContext;

	protected PairRuleContext pairRuleContext;

	public HyperPairOptimizer setDutyRuleContext(DutyRuleContext dutyRuleContext) {
		this.dutyRuleContext = dutyRuleContext;
		return this;
	}

	public HyperPairOptimizer setPairRuleContext(PairRuleContext pairRuleContext) {
		this.pairRuleContext = pairRuleContext;
		return this;
	}

	@Override
	public List<PairView> proceed() {

		return null;
	}
}
