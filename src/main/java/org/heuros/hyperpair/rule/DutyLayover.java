package org.heuros.hyperpair.rule;

import org.heuros.core.rule.intf.RuleImplementation;
import org.heuros.core.rule.intf.StarterChecker;
import org.heuros.core.rule.intf.FinalChecker;
import org.heuros.data.model.DutyView;
import org.heuros.data.model.LegView;

@RuleImplementation(ruleName="DutyLayover", 
					description="Layover stations rule.", 
					violationMessage="No layover permission!")
public class DutyLayover implements StarterChecker<DutyView, LegView>,
															FinalChecker<DutyView> {

//	@Override
//	public boolean _canADutyEndAt(Flight f) {
//    	if (f.getArrAirport()._hb
//    			|| ((AirportAddonImpl) f.getArrAirport()._addon).isLayoverAllowed_)
//    		return true;
//		return false;
//	}
//
//	@Override
//	public boolean _canDutyStartAt(Flight f) {
//    	if (((AirportAddonImpl) f.getDepAirport()._addon).isLayoverAllowed_
//    			|| f.getDepAirport()._hb)
//    		return true;
//		return false;
//	}

	@Override
	public boolean canBeStarter(int hbNdx, LegView l) {
    	if (l.getDepAirport().isLayoverAllowed()
    			|| l.getDepAirport().isHb(hbNdx))
    		return true;
    	return false;
	}

	@Override
	public boolean acceptable(int hbNdx, DutyView d) {
		/*
		 * isAnyHb check is used for backward compatibility.
		 */
		if (d.getLastArrAirport().isAnyHb()
    			|| d.getLastArrAirport().isLayoverAllowed())
    		return true;
    	return false;
	}
}
