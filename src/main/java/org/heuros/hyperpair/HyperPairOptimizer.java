package org.heuros.hyperpair;

import java.util.List;

import org.heuros.core.base.DataContext;
import org.heuros.core.base.Optimizer;
import org.heuros.core.model.Duty;
import org.heuros.core.model.Leg;
import org.heuros.core.model.Pair;
import org.heuros.core.rule.context.ExtendedRuleContext;
import org.heuros.core.rule.context.RuleContext;
import org.heuros.rule.DutyRuleContext;
import org.heuros.rule.LegRuleContext;
import org.heuros.rule.PairRuleContext;

public class HyperPairOptimizer implements Optimizer<Leg, Pair> {

	private RuleContext<Leg> legRuleContext = new LegRuleContext();
	private ExtendedRuleContext<Duty, Leg> dutyRuleContext = new DutyRuleContext();
	private ExtendedRuleContext<Pair, Duty> pairRuleContext = new PairRuleContext();

	private DataContext<Leg, Pair> dataContext = new DataContext<Leg, Pair>();

	public HyperPairOptimizer() {
	}

	@Override
	public List<Pair> optimize(DataContext<Leg, Pair> dataContext) {
		return null;
	}
}
