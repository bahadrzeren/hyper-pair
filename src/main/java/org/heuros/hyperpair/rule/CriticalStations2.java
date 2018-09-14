package org.heuros.hyperpair.rule;

import org.heuros.core.rule.intf.AppendabilityChecker;
import org.heuros.core.rule.intf.ConnectionChecker;
import org.heuros.core.rule.intf.RuleImplementation;
import org.heuros.core.rule.intf.TotalizerChecker;
import org.heuros.data.model.DutyView;
import org.heuros.data.model.LegView;

@RuleImplementation(ruleName="DutyCriticalStations",
					description="Critical stations rule.", 
					violationMessage="Critical station!")
public class CriticalStations2 implements AppendabilityChecker<DutyView, LegView>,
																TotalizerChecker<DutyView>,
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
	public boolean areConnectable(int hbNdx, DutyView pd, DutyView nd) {
		/*
		 * CATC duty can have be layover.
		 */
		if ((pd.getNumOfCriticalLegs() > 0)
				|| (nd.getNumOfCriticalLegs() > 0))
			return false;
		return true;
	}

	@Override
	public boolean acceptable(int hbNdx, DutyView d) {
		/*
		 * If CATC layover is not allowed.
		 */
		/*
		 * TODO An additonal HB parameter is necessary for accurate HB or NonHb desicion.
		 */
		if (d.getLastArrAirport().isNonHb(hbNdx)
				&& (d.getNumOfCriticalLegs() > 0))
			return false;
		return true;
	}

	@Override
	public boolean isAppendable(int hbNdx, DutyView d, LegView l) {
	    /*
	     * If CATC layover is not allowed.
	     */
		/*
		 * TODO An additonal HB parameter is necessary for accurate HB or NonHb desicion.
		 */
	   	if (d.getFirstDepAirport().isNonHb(hbNdx)
	   			&& l.getArrAirport().isCritical())
	   		return false;
	   	return true;
	}
}
