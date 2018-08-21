package org.heuros.hyperpair.rule;

import org.heuros.core.rule.inf.AbstractRule;
import org.heuros.core.rule.inf.RuleImplementation;
import org.heuros.core.rule.inf.StarterChecker;
import org.heuros.core.rule.inf.Validator;
import org.heuros.data.model.DutyView;
import org.heuros.data.model.LegView;

@RuleImplementation(ruleName="DutyLayover", 
					description="Layover stations rule.", 
					violationMessage="No layover permission!")
public class DutyLayover extends AbstractRule implements StarterChecker<DutyView, LegView>,
															Validator<DutyView> {

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
	public boolean canBeStarter(LegView l) {
        /*
		 * TODO An additonal HB parameter is necessary for accurate HB or NonHb desicion.
		 */
    	if (l.getDepAirport().isLayoverAllowed()
    			|| l.getDepAirport().isHb())
    		return true;
    	return false;
	}

	@Override
	public boolean isValid(DutyView d) {
        /*
		 * TODO An additonal HB parameter is necessary for accurate HB or NonHb desicion.
		 */
    	if (d.getLastArrAirport().isHb()
    			|| d.getLastArrAirport().isLayoverAllowed())
    		return true;
    	return false;
	}
}
