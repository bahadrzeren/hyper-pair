package org.heuros.hyperpair.rule;

import org.heuros.core.rule.intf.ConnectionChecker;
import org.heuros.core.rule.intf.RuleImplementation;
import org.heuros.data.model.LegView;

@RuleImplementation(ruleName="DutySpecialFlightNums", 
					description="Special flight numbers rule.", 
					violationMessage="Special flight violation!")
public class DutySpecialFlightNums1 implements ConnectionChecker<LegView> {

//	@Override
//	public boolean _areFlightsConnectable(Flight pf, boolean isPfActive, Flight nf, boolean isNfActive) {
//        if (pf.getDepAirport()._hb && 
//        		((FlightAddonImpl) nf._addon).isSpecFlight_)
//        	return false;
//		return true;
//	}

	@Override
	public boolean areConnectable(LegView pl, LegView nl) {
		/*
		 * TODO An additonal HB parameter is necessary for accurate HB or NonHb desicion.
		 */
        if (pl.getDepAirport().isHb() && nl.isSpecialFlight())
        	return false;
		return true;
	}
}
