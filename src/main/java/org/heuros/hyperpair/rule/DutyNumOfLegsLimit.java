package org.heuros.hyperpair.rule;

import java.time.temporal.ChronoUnit;

import org.heuros.core.rule.intf.ExtensibilityChecker;
import org.heuros.core.rule.intf.RuleImplementation;
import org.heuros.core.rule.intf.Validator;
import org.heuros.data.model.DutyView;

@RuleImplementation(ruleName="DutyNumOfLegsLimit", 
					description="Max number of legs in a duty check.",
					violationMessage="Max number of legs exceeded!")
public class DutyNumOfLegsLimit implements ExtensibilityChecker<DutyView>,
																	Validator<DutyView> {

	private Integer maxNumOfActiveLegsInDuty = 4;
	private Integer maxNumOfPassiveLegsInDuty = 1;

	private Integer maxNumOfLegsInERDuty = 3; // *****
//	@AnntParam(fieldName="maxNumOfActiveLegsInOvernightDuty3", description="Gece gorevindeki max aktif ucus sayisi (Ozel durum).")
//	private Integer maxNumOfActiveLegsInOvernightDuty3 = 3;
	private Integer maxNumOfActiveLegsInOvernightDuty2 = 2;

//	@Override
//	public boolean _canDutyContinue(Duty d) {
////    	if (d.getDutyFlights().size() >= maxNumOfActiveLegsInDuty + maxNumOfPassiveLegsInDuty)
//		if (d.getDutyFlightCount() >= maxNumOfActiveLegsInDuty + maxNumOfPassiveLegsInDuty)
//    		return false;
//    	if ((d._totNumOfLegsPassive > 0)
////    			&& (d.getDutyFlights().size() == maxNumOfActiveLegsInDuty))
//    			&& (d.getDutyFlightCount() == maxNumOfActiveLegsInDuty))
//    		return false;
//
//		return true;
//	}

//	@Override
//	public boolean _isDutyValid(Duty d) {
//		if (d._totNumOfLegsPassive > maxNumOfPassiveLegsInDuty)
//    		return false;
//
//        if (d._er) {
////            if (d.getDutyFlights().size() > maxNumOfLegsInERDuty)
//            if (d.getDutyFlightCount() > maxNumOfLegsInERDuty)
//            	return false;
//        } else {
////            if (d.getDutyFlights().size() > maxNumOfActiveLegsInDuty + maxNumOfPassiveLegsInDuty)
//        	if (d.getDutyFlightCount() > maxNumOfActiveLegsInDuty + maxNumOfPassiveLegsInDuty)
//        		return false;
//        }
//
////		if ((d.getDutyFlights().size() - d._totNumOfLegsPassive > maxNumOfActiveLegsInDuty)
//        if ((d.getDutyFlightCount() - d._totNumOfLegsPassive > maxNumOfActiveLegsInDuty)
//				|| (d._totNumOfLegsPassive > maxNumOfPassiveLegsInDuty))
//			return false;
//
//		/*
//		 * Gecisli
//		 */
//		if (d._briefDay != DatetimeUtil.getDateInDays(d._lf().getScheduledInblockUtc(), -1000)) {
////			if (((!d._lf()._need.isGtZero()) && d._lf().getArrAirport()._hb)
////				|| (d._ff().getArrAirport().getRid().equals("NAJ"))
////				|| ((d._ff().getFlightNo() == 840) && (d._lf().getFlightNo() == 841))) {
//////				if (d.getDutyFlights().size() > maxNumOfActiveLegsInOvernightDuty3)
////				if (d._totNumOfFlights > maxNumOfActiveLegsInOvernightDuty3)
////					return false;
////			} else
//////				if (d.getDutyFlights().size() > maxNumOfActiveLegsInOvernightDuty2)
////				if (d._totNumOfFlights > maxNumOfActiveLegsInOvernightDuty2)
////					return false;
//			if (d.getDutyFlightCount() > maxNumOfActiveLegsInOvernightDuty2) {
//				if ((d._augm > 0)
//					|| ((d.getDutyFlightCount() - d._totNumOfLegsPassive) > maxNumOfActiveLegsInOvernightDuty2)
//					|| (((d.getDutyFlightCount() - d._totNumOfLegsPassive) ==  maxNumOfActiveLegsInOvernightDuty2)
//							&& (d._ldf().isActive()
//								|| d._lf().getArrAirport()._nonHB)))
//				return false;
//			}
//		}
//
//		return true;
//	}


	@Override
	public boolean isExtensible(DutyView d) {
		if (d.getNumOfLegs() >= maxNumOfActiveLegsInDuty + maxNumOfPassiveLegsInDuty)
    		return false;
    	if ((d.getNumOfLegsPassive() > 0)
    			&& (d.getNumOfLegs() >= maxNumOfActiveLegsInDuty))
    		return false;
		return true;
	}

	@Override
	public boolean isValid(DutyView d) {

		if (d.getNumOfLegsPassive() > maxNumOfPassiveLegsInDuty)
    		return false;

        if (d.isEr()) {
            if (d.getNumOfLegs() > maxNumOfLegsInERDuty)
            	return false;
        } else {
        	if (d.getNumOfLegs() > maxNumOfActiveLegsInDuty + maxNumOfPassiveLegsInDuty)
        		return false;
        }

        if ((d.getNumOfLegsActive() > maxNumOfActiveLegsInDuty)
				|| (d.getNumOfLegsPassive() > maxNumOfPassiveLegsInDuty))
			return false;

		/*
		 * Gecisli
		 * 
		 * TODO An additonal HB parameter is necessary for accurate HB or NonHb desicion.
		 */
		if (d.getBriefDayNonHb().isBefore( d.getLastLeg().getSibt().minusSeconds(1).truncatedTo(ChronoUnit.DAYS).toLocalDate() )) {
			if (d.getNumOfLegs() > maxNumOfActiveLegsInOvernightDuty2) {
				if ((d.getAugmentedNonHb() > 0)
					|| (d.getNumOfLegsActive() > maxNumOfActiveLegsInOvernightDuty2)
					|| ((d.getNumOfLegsActive() ==  maxNumOfActiveLegsInOvernightDuty2)
							&& (d.getLastLeg().isCover()
								|| d.getLastArrAirport().isNonHb())))
				return false;
			}
		}

		return true;
	}
}
