package org.heuros.hyperpair.rule;

import org.heuros.core.rule.inf.AbstractRule;
import org.heuros.core.rule.inf.AppendabilityChecker;
import org.heuros.core.rule.inf.ConnectionChecker;
import org.heuros.core.rule.inf.RuleImplementation;
import org.heuros.core.rule.inf.Validator;
import org.heuros.data.model.DutyView;
import org.heuros.data.model.LegView;

@RuleImplementation(ruleName="DutyCriticalStations",
					description="Critical stations rule.", 
					violationMessage="Critical station!")
public class CriticalStations2 extends AbstractRule implements AppendabilityChecker<DutyView, LegView>,
																Validator<DutyView>,
																ConnectionChecker<DutyView> {

//	@Override
//	public boolean _isFlightAddable(Duty d, Flight nf, boolean isActive) {
//	    /*
//	     * If CATC layover is not allowed.
//	     */
//    	if (d._ff().getDepAirport()._nonHB
//    			&& ((AirportAddonImpl) nf.getArrAirport()._addon).isCritical_)
//    		return false;
//    	return true;
//	}
//
//	@Override
//	public boolean _canDutyEnd(Duty d) {
//        /*
//    	 * If CATC layover is not allowed.
//    	 */
//    	if (d._lf().getArrAirport()._nonHB
//    			&& (((DutyAddonImpl) d._addon)._totNumOfCritLegs > 0))
//    		return false;
//		return true;
//	}
//
//	@Override
//	public boolean _areDutiesConnectable(Duty pd, Duty nd) {
//		/*
//		 * CATC g�rev yat�l� olamaz.
//		 */
//		if ((((DutyAddonImpl) pd._addon)._totNumOfCritLegs > 0)
//				|| (((DutyAddonImpl) nd._addon)._totNumOfCritLegs > 0))
//			return false;
//		return true;
//	}

	@Override
	public boolean areConnectable(DutyView pd, DutyView nd) {
		/*
		 * CATC duty can have be layover.
		 */
		if ((pd.getNumOfCriticalLegs() > 0)
				|| (nd.getNumOfCriticalLegs() > 0))
			return false;
		return true;
	}

	@Override
	public boolean isValid(DutyView d) {
		/*
		 * If CATC layover is not allowed.
		 */
		/*
		 * TODO An additonal HB parameter is necessary for accurate HB or NonHb desicion.
		 */
		if (d.getLastArrAirport().isNonHb()
				&& (d.getNumOfCriticalLegs() > 0))
			return false;
		return true;
	}

	@Override
	public boolean isAppendable(DutyView d, LegView l) {
	    /*
	     * If CATC layover is not allowed.
	     */
		/*
		 * TODO An additonal HB parameter is necessary for accurate HB or NonHb desicion.
		 */
	   	if (d.getFirstDepAirport().isNonHb()
	   			&& l.getArrAirport().isCritical())
	   		return false;
	   	return true;
	}
}