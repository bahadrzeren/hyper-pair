package org.heuros.hyperpair.rule;

import org.heuros.core.rule.intf.RuleImplementation;
import org.heuros.core.rule.intf.FinalChecker;
import org.heuros.data.model.DutyView;

@RuleImplementation(ruleName="DutyLength", 
					description="Duty augmentation check.", 
					violationMessage="Augmentation violation!")
public class DutyLength implements FinalChecker<DutyView> {

//	@Override
//	public boolean _isDutyValid(Duty d) {
//		return d._augm <= 2;
//	}

	@Override
	public boolean acceptable(int hbNdx, DutyView d) {
		return d.getAugmented(hbNdx) <= 2;
	}
}
