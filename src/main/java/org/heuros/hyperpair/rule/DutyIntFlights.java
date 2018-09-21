package org.heuros.hyperpair.rule;

import org.heuros.core.rule.intf.AppendabilityChecker;
import org.heuros.core.rule.intf.RuleImplementation;
import org.heuros.data.model.DutyView;
import org.heuros.data.model.LegView;

@RuleImplementation(ruleName="DutyIntFlights", 
					description="Max number of International legs.", 
					violationMessage="Max number of International legs exceeded!")
public class DutyIntFlights implements AppendabilityChecker<DutyView, LegView> {

	private Integer maxNumOfIntToFromDomLegsInDuty = 2;

	private Integer minFlightTimeLimitForIntDuties = 135;	//	105;

//	@Override
//	public boolean _isFlightAddable(Duty d, Flight nf, boolean isActive) {
//       if (((d._totNumOfLegsDomToInt + d._totNumOfLegsIntToDom) >= maxNumOfIntToFromDomLegsInDuty)
//    		   && (nf.getDepAirport()._dom != nf.getArrAirport()._dom)
//    		   && ((d._longestBlockTime >= minFlightTimeLimitForIntDuties)
//    				   || (nf._blockTime >= minFlightTimeLimitForIntDuties)))
//    	   return false;
//    	return true;
//	}

	@Override
	public boolean isAppendable(int hbNdx, DutyView d, LegView l, boolean fw) {
       if (((d.getNumOfLegsDomToInt() + d.getNumOfLegsIntToDom()) >= maxNumOfIntToFromDomLegsInDuty)
    		   && (l.getDepAirport().isDomestic() != l.getArrAirport().isDomestic())
    		   && ((d.getLongestBlockTimeInMins() >= minFlightTimeLimitForIntDuties)
    				   || (l.getBlockTimeInMins() >= minFlightTimeLimitForIntDuties)))
    	   return false;
    	return true;
	}
}
