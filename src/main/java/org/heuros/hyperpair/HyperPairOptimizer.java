package org.heuros.hyperpair;

import java.util.List;

import org.heuros.core.base.Processor;
import org.heuros.data.model.DutyExtensionFactory;
import org.heuros.data.model.DutyWrapperFactory;
import org.heuros.data.model.LegExtensionFactory;
import org.heuros.data.model.LegWrapper;
import org.heuros.data.model.LegWrapperFactory;
import org.heuros.data.model.PairExtension;
import org.heuros.data.model.PairExtensionFactory;
import org.heuros.data.model.PairModel;
import org.heuros.data.model.PairWrapper;
import org.heuros.data.model.PairWrapperFactory;
import org.heuros.rule.DutyRuleContext;
import org.heuros.rule.LegRuleContext;
import org.heuros.rule.PairRuleContext;

public class HyperPairOptimizer implements Processor<LegWrapper, 
														PairWrapper, 
														PairModel, 
														PairExtension> {

	protected LegRuleContext legRuleContext;
	protected LegExtensionFactory legExtensionFactory;
	protected LegWrapperFactory legWrapperFactory;

	protected DutyRuleContext dutyRuleContext;
	protected DutyExtensionFactory dutyExtensionFactory;
	protected DutyWrapperFactory dutyWrapperFactory;

	protected PairRuleContext pairRuleContext;
	protected PairExtensionFactory pairExtensionFactory;
	protected PairWrapperFactory pairWrapperFactory;

	public HyperPairOptimizer setLegRuleContext(LegRuleContext legRuleContext) {
		this.legRuleContext = legRuleContext;
		return this;
	}

	public HyperPairOptimizer setLegExtensionFactory(LegExtensionFactory legExtensionFactory) {
		this.legExtensionFactory = legExtensionFactory;
		return this;
	}

	public HyperPairOptimizer setLegWrapperFactory(LegWrapperFactory legWrapperFactory) {
		this.legWrapperFactory = legWrapperFactory;
		return this;
	}

	public HyperPairOptimizer setDutyRuleContext(DutyRuleContext dutyRuleContext) {
		this.dutyRuleContext = dutyRuleContext;
		return this;
	}

	public HyperPairOptimizer setDutyExtensionFactory(DutyExtensionFactory dutyExtensionFactory) {
		this.dutyExtensionFactory = dutyExtensionFactory;
		return this;
	}

	public HyperPairOptimizer setDutyWrapperFactory(DutyWrapperFactory dutyWrapperFactory) {
		this.dutyWrapperFactory = dutyWrapperFactory;
		return this;
	}

	public HyperPairOptimizer setPairRuleContext(PairRuleContext pairRuleContext) {
		this.pairRuleContext = pairRuleContext;
		return this;
	}

	public HyperPairOptimizer setPairExtensionFactory(PairExtensionFactory pairExtensionFactory) {
		this.pairExtensionFactory = pairExtensionFactory;
		return this;
	}

	public HyperPairOptimizer setPairWrapperFactory(PairWrapperFactory pairWrapperFactory) {
		this.pairWrapperFactory = pairWrapperFactory;
		return this;
	}

	@Override
	public List<PairWrapper> proceed(List<LegWrapper> input) {

		return null;
	}
}
