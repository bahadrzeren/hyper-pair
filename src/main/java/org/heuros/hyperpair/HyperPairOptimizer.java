package org.heuros.hyperpair;

import java.util.List;

import org.heuros.core.base.Processor;
import org.heuros.core.data.model.Duty;
import org.heuros.core.data.model.Leg;
import org.heuros.core.data.model.Pair;
import org.heuros.core.rule.ExtendedRuleContext;
import org.heuros.core.rule.RuleContext;
import org.heuros.rule.DutyRuleContext;
import org.heuros.rule.LegRuleContext;
import org.heuros.rule.PairRuleContext;

public class HyperPairOptimizer implements Processor<Pair> {

	private RuleContext<Leg> legRuleContext = new LegRuleContext();
	private ExtendedRuleContext<Duty, Leg> dutyRuleContext = new DutyRuleContext();
	private ExtendedRuleContext<Pair, Duty> pairRuleContext = new PairRuleContext();

//	private DataContext<Leg, Pair> dataContext = new DataContext<Leg, Pair>();

	public HyperPairOptimizer() {
	}

	@Override
	public List<Pair> startProcess() {
		return null;
	}
}
