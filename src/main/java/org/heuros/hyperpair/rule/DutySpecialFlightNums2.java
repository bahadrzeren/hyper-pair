package org.heuros.hyperpair.rule;

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
	public boolean areConnectable(DutyView pd, DutyView nd) {
        /*
         * Spec Flight Nums.
         *  
		 * TODO An additonal HB parameter is necessary for accurate HB or NonHb desicion.
		 */
        if (pd.getFirstDepAirport().isNonHb()
        		&& pd.getFirstDepAirport().isDomestic()
        		&& nd.getFirstLeg().isSpecialFlight())
        	return false;
        return true;
	}
}
