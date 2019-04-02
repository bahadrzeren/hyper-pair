package org.heuros.pair.rule;

import org.heuros.core.rule.intf.ConnectionChecker;
import org.heuros.core.rule.intf.RuleImplementation;
import org.heuros.data.model.DutyView;

@RuleImplementation(ruleName="PairEarlyDuty", 
					description="Early duty rule.", 
					violationMessage="Early duties can not be connected sequentially!")
public class PairEarlyDuty implements ConnectionChecker<DutyView> {

//	@Override
//	public boolean _areDutiesConnectable(Duty pd, Duty nd) {
//		if (pd._isEarly && nd._isEarly
//				&& (pd._ff().getDepAirport()._nonHB || nd._lf().getArrAirport()._nonHB))
//			return false;
//		return true;
//	}
//
////	@Override
////	public boolean _isDutyAddable(Pair p, Duty d, boolean fw) {
////        /*
////		 * Early dutyleri ba�lama.
////		 */
////        if ((p._totNumOfEarlyDuties > 1)
////        		&& d._isEarly)
////        	return false;
////		return true;
////	}


	@Override
	public boolean areConnectable(int hbNdx, DutyView pd, DutyView nd) {
		/*
		 * isAnyNonHb check is used for backward compatibility.
		 */
		/*
		 * Cancelled due to Totalizer calculation error fix.
		 */
//		if (pd.isEarly(hbNdx) && nd.isEarly(hbNdx)
//				&& (pd.getFirstDepAirport().isAnyNonHb() || nd.getLastArrAirport().isAnyNonHb()))
//			return false;
		return true;
	}
}
