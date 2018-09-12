package org.heuros.hyperpair.rule;

import org.heuros.core.rule.intf.AppendabilityChecker;
import org.heuros.core.rule.intf.RuleImplementation;
import org.heuros.data.model.DutyView;
import org.heuros.data.model.LegView;

@RuleImplementation(ruleName="DutyAgDgFlights", 
					description="Agdg stations rule.", 
					violationMessage="Duty that touches agdg station can not have international connection flight!")
public class DutyAgDgFlights implements AppendabilityChecker<DutyView, LegView> {

//	@Override
//	public boolean _isFlightAddable(Duty d, Flight nf, boolean isActive) {
//        if (((((DutyAddonImpl) d._addon)._totNumOfAgDg == 2) && (d._totNumOfLegsDomToInt + d._totNumOfLegsIntToDom > 0))
//        		|| ((d._totNumOfLegsDomToInt + d._totNumOfLegsIntToDom >= 2)
//        				&& (((AirportAddonImpl) nf.getDepAirport()._addon).isAgDg_
//        						|| ((AirportAddonImpl) nf.getArrAirport()._addon).isAgDg_)))
//        	return false;
//    	return true;
//	}

	@Override
	public boolean isAppendable(int hbNdx, DutyView d, LegView l) {
        if (((d.getNumOfAgDg() == 2) && (d.getNumOfLegsDomToInt() + d.getNumOfLegsIntToDom() > 0))
        		|| ((d.getNumOfLegsDomToInt() + d.getNumOfLegsIntToDom() >= 2)
        				&& (l.getDepAirport().isAgDg()
        						|| l.getArrAirport().isAgDg())))
        	return false;
    	return true;
	}
}
