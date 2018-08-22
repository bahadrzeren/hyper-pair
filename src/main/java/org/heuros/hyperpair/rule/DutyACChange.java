package org.heuros.hyperpair.rule;

import org.heuros.core.rule.intf.ConnectionChecker;
import org.heuros.core.rule.intf.RuleImplementation;
import org.heuros.data.model.LegView;

@RuleImplementation(ruleName="DutyACChange", 
					description="AC change rule.", 
					violationMessage="Ac change is not allowed!")
public class DutyACChange implements ConnectionChecker<LegView> {

//	@Override
//	public boolean _areFlightsConnectable(Flight pf, boolean isPfActive, Flight nf, boolean isNfActive) {
//    	if (isPfActive && isNfActive	//	Both flights must be active.
//    			&& nf.getDepAirport()._int
//        		&& (!((AirportAddonImpl) nf.getDepAirport()._addon).isAcChangeAllowed_)
//        		&& pf.hasAcChangeWith(nf)
////				&& (nf.getRid().intValue() != 6114362)
//        		)
//        	return false;
//		return true;
//	}

	@Override
	public boolean areConnectable(LegView pl, LegView nl) {
    	if (pl.isCover() && nl.isCover() //	Both flights must be active.
    			&& nl.getDepAirport().isInternational()
        		&& (!nl.getDepAirport().isAcChangeAllowed())
        		&& pl.hasAcChangeWith(nl))
        	return false;
		return true;
	}
}
