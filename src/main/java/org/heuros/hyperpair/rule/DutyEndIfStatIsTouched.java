package org.heuros.hyperpair.rule;

import org.heuros.core.rule.inf.AbstractRule;
import org.heuros.core.rule.inf.ExtensibilityChecker;
import org.heuros.core.rule.inf.RuleImplementation;
import org.heuros.data.model.DutyView;

@RuleImplementation(ruleName="DutyEndIfStatIsTouched", 
					description="Mandatory layover stations.", 
					violationMessage="Layover is mandatory!")
public class DutyEndIfStatIsTouched extends AbstractRule implements ExtensibilityChecker<DutyView> {

//	@Override
//	public boolean _canDutyContinue(Duty d) {
//    	if (((AirportAddonImpl) d._lf().getArrAirport()._addon).isEndDutyIfTouches_)
//    		return false;
//		return true;
//	}

	@Override
	public boolean isExtensible(DutyView d) {
    	if (d.getLastArrAirport().isEndDutyIfTouches())
    		return false;
		return true;
	}
}
