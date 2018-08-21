package org.heuros.hyperpair.rule;

import org.heuros.core.rule.inf.AbstractRule;
import org.heuros.core.rule.inf.ConnectionChecker;
import org.heuros.core.rule.inf.RuleImplementation;
import org.heuros.data.model.DutyView;

@RuleImplementation(ruleName="PairEarlyDuty", 
					description="Early duty rule.", 
					violationMessage="Early duties can not be connected sequentially!")
public class PairEarlyDuty extends AbstractRule implements ConnectionChecker<DutyView> {

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
	public boolean areConnectable(DutyView pd, DutyView nd) {
		/*
		 * TODO An additonal HB parameter is necessary for accurate HB or NonHb desicion.
		 */
		if (pd.isEarlyHb() && nd.isEarlyNonHb()
				&& (pd.getFirstDepAirport().isNonHb() || nd.getLastArrAirport().isNonHb()))
			return false;
		return true;
	}
}
