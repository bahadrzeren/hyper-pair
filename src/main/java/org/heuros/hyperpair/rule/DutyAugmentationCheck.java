package org.heuros.hyperpair.rule;

import org.heuros.core.rule.intf.RuleImplementation;
import org.heuros.core.rule.intf.FinalChecker;
import org.heuros.data.model.DutyView;

@RuleImplementation(ruleName="DutyAugmentationCheck", 
					description="Duty with augmented crew rule.", 
					violationMessage="Augmentation is not allowed!")
public class DutyAugmentationCheck implements FinalChecker<DutyView> {

//	@Override
//	public boolean _isDutyValid(Duty d) {
//    	if (d._augm > 1)
//    		return false;
//    	if (d._augm > 0) {
//    		if (d._numOfDomTouch > 0)
//    			return false;
//    		if ((d._numOfBaseTouch > 0)
//    				&& (!((AirportAddonImpl) d._ff().getDepAirport()._addon).isMandatoryFirstLayover_)
//    				)
//    			return false;
//    	}
//		return true;
//	}

	@Override
	public boolean acceptable(int hbNdx, DutyView d) {
		/*
		 * numOfAnyHbTouch check is used for backward compatibility.
		 */
    	if (d.getAugmented(hbNdx) > 1)
    		return false;
    	if (d.getAugmented(hbNdx) > 0) {
    		if (d.getNumOfDomTouch() > 0)
    			return false;
    		if ((d.getNumOfAnyHomebaseTouch() > 0)
    				&& (!d.getFirstDepAirport().isMandatoryFirstLayover()))
    			return false;
    	}
		return true;
	}
}
