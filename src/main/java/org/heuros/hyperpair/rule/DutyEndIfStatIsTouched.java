package org.heuros.hyperpair.rule;

import org.heuros.core.rule.intf.ExtensibilityChecker;
import org.heuros.core.rule.intf.RuleImplementation;
import org.heuros.data.model.DutyView;

@RuleImplementation(ruleName="DutyEndIfStatIsTouched", 
					description="Mandatory layover stations.", 
					violationMessage="Layover is mandatory!")
public class DutyEndIfStatIsTouched implements ExtensibilityChecker<DutyView> {

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
