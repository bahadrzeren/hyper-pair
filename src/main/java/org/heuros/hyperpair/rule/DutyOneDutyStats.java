package org.heuros.hyperpair.rule;

import org.heuros.core.rule.intf.AppendabilityChecker;
import org.heuros.core.rule.intf.ExtensibilityChecker;
import org.heuros.core.rule.intf.RuleImplementation;
import org.heuros.data.model.DutyView;
import org.heuros.data.model.LegView;

@RuleImplementation(ruleName="DutyOneDutyStats", 
					description="One duty stations rule.", 
					violationMessage="One duty station!")
public class DutyOneDutyStats implements AppendabilityChecker<DutyView, LegView>,
																ExtensibilityChecker<DutyView> {

//	@Override
//	public boolean _isFlightAddable(Duty d, Flight nf, boolean isActive) {
//		if ((((AirportAddonImpl) nf.getArrAirport()._addon).isOneDutyStat_)
//				&& (
////						(d._totNumOfLegsIntToDom + d._totNumOfLegsDomToInt > 0) || 
//						d._ff().getDepAirport()._nonHB))
//    		return false;
//    	return true;
//	}
//
//	@Override
//	public boolean _canDutyContinue(Duty d) {
//		if (((AirportAddonImpl) d._lf().getDepAirport()._addon).isOneDutyStat_)
//			return false;
//		return true;
//	}

	@Override
	public boolean isExtensible(int hbNdx, DutyView d) {
		if (d.getLastLeg().getDepAirport().isOneDutyStation())
			return false;
		return true;
	}

	@Override
	public boolean isAppendable(int hbNdx, DutyView d, LegView l, boolean fw) {
		/*
		 * isAnyNonHb check is used for backward compatibility.
		 */
		if ((l.getArrAirport().isOneDutyStation())
				&& (d.getFirstDepAirport().isAnyNonHb()))
    		return false;
    	return true;
	}
}
