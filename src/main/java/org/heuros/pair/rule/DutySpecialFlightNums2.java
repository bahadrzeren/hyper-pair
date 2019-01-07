package org.heuros.pair.rule;

import org.heuros.core.rule.intf.ConnectionChecker;
import org.heuros.core.rule.intf.RuleImplementation;
import org.heuros.data.model.DutyView;

@RuleImplementation(ruleName="DutySpecialFlightNums", 
					description="Special flight numbers rule.", 
					violationMessage="Special flight violation!")
public class DutySpecialFlightNums2 implements ConnectionChecker<DutyView> {

//	@Override
//	public boolean _areDutiesConnectable(Duty pd, Duty nd) {
//        /*
//         * Spec Flight Nums 
//         */
//            if (pd._ff().getDepAirport()._nonHB
//            		&& pd._ff().getDepAirport()._dom
//            		&& ((FlightAddonImpl) nd._ff()._addon).isSpecFlight_)
//        	return false;
//        return true;
//	}

	@Override
	public boolean areConnectable(int hbNdx, DutyView pd, DutyView nd) {
        /*
         * Spec Flight Nums.
         * 
		 * isAnyHb check is used for backward compatibility.
		 */
        if (pd.getFirstDepAirport().isAnyNonHb()
        		&& pd.getFirstDepAirport().isDomestic()
        		&& nd.getFirstLeg().isSpecialFlight())
        	return false;
        return true;
	}
}
