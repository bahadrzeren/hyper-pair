package org.heuros.pair.rule;

import org.heuros.core.rule.intf.ConnectionChecker;
import org.heuros.core.rule.intf.RuleImplementation;
import org.heuros.core.rule.intf.FinalChecker;
import org.heuros.data.model.DutyView;
import org.heuros.data.model.LegView;

@RuleImplementation(ruleName="DutyForceToBeFirstLayoverDuty", 
					description="Tripteki zorunlu ilk yati kurali.", 
					violationMessage="Tripteki ilk yati olmali!")
public class DutyForceToBeFirstLayoverDuty implements FinalChecker<DutyView>,
																			ConnectionChecker<LegView> {

//	@Override
//	public boolean _canDutyEnd(Duty d) {
//    	if (((AirportAddonImpl) d._lf().getArrAirport()._addon).isMandatoryFirstLayover_
//    			&& d._ff().getDepAirport()._nonHB)
//    		return false;
//		return true;
//	}
//
//	@Override
//	public boolean _areFlightsConnectable(Flight pf, boolean isPfActive, Flight nf, boolean isNfActive) {
//    	if (((AirportAddonImpl) nf.getArrAirport()._addon).isMandatoryFirstLayover_
//    			&& isNfActive)
//    		return false;
//		return true;
//	}

	@Override
	public boolean areConnectable(int hbNdx, LegView pl, LegView nl) {
		if (nl.getArrAirport().isMandatoryFirstLayover()
				&& nl.isCover())
			return false;
		return true;
	}

	@Override
	public boolean acceptable(int hbNdx, DutyView d) {
		/*
		 * isAnyNonHb check is used for backward compatibility.
		 */
    	if (d.getLastArrAirport().isMandatoryFirstLayover()
    			&& d.getFirstDepAirport().isAnyNonHb())
    		return false;
    	return true;
	}
}
