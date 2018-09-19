package org.heuros.hyperpair.rule;

import org.heuros.core.rule.intf.ConnectionChecker;
import org.heuros.core.rule.intf.RuleImplementation;
import org.heuros.data.model.LegView;

@RuleImplementation(ruleName="DutyMaxBlockTimeForIntLayovers", 
					description="Max blocktime for international flights rule.", 
					violationMessage="Max blocktime violation for international flight!")
public class DutyMaxBlockTimeForIntLayovers implements ConnectionChecker<LegView> {

	private Integer maxBlockTimeInDutyForIntLayovers = 300;

//	@Override
//	public boolean _areFlightsConnectable(Flight pf, boolean isPfActive, Flight nf, boolean isNfActive) {
//
//        if ((pf.getDepAirport()._int
//        		&& pf.getArrAirport()._hb
//        		&& isPfActive
//        		&& (pf._blockTime >= maxBlockTimeInDutyForIntLayovers))
//        		|| (nf.getArrAirport()._int
//                		&& nf.getDepAirport()._hb
//                		&& isNfActive
//                		&& (nf._blockTime >= maxBlockTimeInDutyForIntLayovers)))
//        	return false;
//
//		return true;
//	}
//
////	@Override
////	public boolean _canDutyEnd(Duty d) {
////		if (d._totNumOfFlights > 1) {
////			Flight lf = d._lf();
////			if (lf.getArrAirport()._int
////				&& lf._need.isGtZero()
////				&& (lf._blockTime >= maxBlockTimeInDutyForIntLayovers))
////				return false;
////		}
////		return true;
////	}

	@Override
	public boolean areConnectable(int hbNdx, LegView pl, LegView nl) {
		/*
		 * isAnyHb check is used for backward compatibility.
		 */
        if ((pl.getDepAirport().isInternational()
        		&& pl.getArrAirport().isAnyHb()
        		&& pl.isCover()
        		&& (pl.getBlockTimeInMins() >= maxBlockTimeInDutyForIntLayovers))
        		|| (nl.getArrAirport().isInternational()
                		&& nl.getDepAirport().isAnyHb()
                		&& nl.isCover()
                		&& (nl.getBlockTimeInMins() >= maxBlockTimeInDutyForIntLayovers)))
        	return false;

		return true;
	}
}
