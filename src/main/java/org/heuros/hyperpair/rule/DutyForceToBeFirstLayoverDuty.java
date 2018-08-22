package org.heuros.hyperpair.rule;

import org.heuros.core.rule.intf.ConnectionChecker;
import org.heuros.core.rule.intf.RuleImplementation;
import org.heuros.core.rule.intf.Validator;
import org.heuros.data.model.DutyView;
import org.heuros.data.model.LegView;

@RuleImplementation(ruleName="DutyForceToBeFirstLayoverDuty", 
					description="Tripteki zorunlu ilk yati kurali.", 
					violationMessage="Tripteki ilk yati olmali!")
public class DutyForceToBeFirstLayoverDuty implements Validator<DutyView>,
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
	public boolean areConnectable(LegView pl, LegView nl) {
		if (nl.getArrAirport().isMandatoryFirstLayover()
				&& nl.isCover())
			return false;
		return true;
	}

	@Override
	public boolean isValid(DutyView d) {
        /*
		 * TODO An additonal HB parameter is necessary for accurate HB or NonHb desicion.
		 */
    	if (d.getLastArrAirport().isMandatoryFirstLayover()
    			&& d.getFirstDepAirport().isNonHb())
    		return false;
    	return true;
	}
}
