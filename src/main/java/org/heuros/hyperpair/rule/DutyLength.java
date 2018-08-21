package org.heuros.hyperpair.rule;

import org.heuros.core.rule.inf.AbstractRule;
import org.heuros.core.rule.inf.RuleImplementation;
import org.heuros.core.rule.inf.Validator;
import org.heuros.data.model.DutyView;

@RuleImplementation(ruleName="DutyLength", 
					description="Duty augmentation check.", 
					violationMessage="Augmentation violation!")
public class DutyLength extends AbstractRule implements Validator<DutyView> {

//	@Override
//	public boolean _isDutyValid(Duty d) {
//		return d._augm <= 2;
//	}

	@Override
	public boolean isValid(DutyView d) {
        /*
		 * TODO An additonal HB parameter is necessary for accurate HB or NonHb desicion.
		 */
		return d.getAugmentedHb() <= 2;
	}
}
