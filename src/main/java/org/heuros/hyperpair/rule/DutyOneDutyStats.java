package org.heuros.hyperpair.rule;

import org.heuros.core.rule.inf.AbstractRule;
import org.heuros.core.rule.inf.AppendabilityChecker;
import org.heuros.core.rule.inf.ExtensibilityChecker;
import org.heuros.core.rule.inf.RuleImplementation;
import org.heuros.data.model.DutyView;
import org.heuros.data.model.LegView;

@RuleImplementation(ruleName="DutyOneDutyStats", 
					description="One duty stations rule.", 
					violationMessage="One duty station!")
public class DutyOneDutyStats extends AbstractRule implements AppendabilityChecker<DutyView, LegView>,
																ExtensibilityChecker<DutyView> {

//	@Override
//	public boolean _isFlightAddable(Duty d, Flight nf, boolean isActive) {
//		if ((((AirportAddonImpl) nf.getArrAirport()._addon).isOneDutyStat_)
//				&& (
////						(d._totNumOfLegsIntToDom + d._totNumOfLegsDomToInt > 0) || 
//						d._ff().getDepAirport()._nonHB))
//    		return false;
//    	return true;
//	}
//
//	@Override
//	public boolean _canDutyContinue(Duty d) {
//		if (((AirportAddonImpl) d._lf().getDepAirport()._addon).isOneDutyStat_)
//			return false;
//		return true;
//	}

	@Override
	public boolean isExtensible(DutyView d) {
		if (d.getLastLeg().getDepAirport().isOneDutyStation())
			return false;
		return true;
	}

	@Override
	public boolean isAppendable(DutyView d, LegView l) {
        /*
		 * TODO An additonal HB parameter is necessary for accurate HB or NonHb desicion.
		 */
		if ((l.getArrAirport().isOneDutyStation())
				&& (d.getFirstDepAirport().isNonHb()))
    		return false;
    	return true;
	}
}