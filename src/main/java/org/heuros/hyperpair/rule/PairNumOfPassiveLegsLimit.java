package org.heuros.hyperpair.rule;

import org.heuros.core.rule.intf.AppendabilityChecker;
import org.heuros.core.rule.intf.ConnectionChecker;
import org.heuros.core.rule.intf.RuleImplementation;
import org.heuros.core.rule.intf.FinalChecker;
import org.heuros.data.model.DutyView;
import org.heuros.data.model.PairView;

@RuleImplementation(ruleName="PairNumOfPassiveLegsLimit", 
					description="Checks total number of passive legs in a trip.", 
					violationMessage="Total number of passive legs exceeded!")
public class PairNumOfPassiveLegsLimit implements AppendabilityChecker<PairView, DutyView>, 
																		ConnectionChecker<DutyView>,
																		FinalChecker<PairView> {

	private Integer maxNumOfPassiveLegsInPair = 3;

//	private Integer maxNumOfConsecutiveEarlyDutiesInAPair = 2;
//
//	@Override
//	public boolean _isDutyAddable(Pair p, Duty d, boolean fw) {
//		if ((p._totNumOfLegsPassive + d._totNumOfLegsPassive > maxNumOfPassiveLegsInPair)
////				|| (d._isEarly && (p._totNumOfEarlyDuties >= maxNumOfConsecutiveEarlyDutiesInAPair))
//				)
//				return false;
//		return true;
//	}
//
//	@Override
//	public boolean _isPairValid(Pair p) {
//    	if (p._totNumOfLegsPassive > maxNumOfPassiveLegsInPair)
//    		return false;
//		return true;
//	}
//
//	@Override
//	public boolean _areDutiesConnectable(Duty pd, Duty nd) {
//		if (pd._totNumOfLegsPassive + nd._totNumOfLegsPassive > maxNumOfPassiveLegsInPair)
//			return false;
//		return true;
//	}

	@Override
	public boolean acceptable(int hbNdx, PairView p) {
		if (p.getNumOfLegsPassive() > maxNumOfPassiveLegsInPair)
			return false;
		return true;
	}

	@Override
	public boolean areConnectable(int hbNdx, DutyView pd, DutyView nd) {
		if (pd.getNumOfLegsPassive() + nd.getNumOfLegsPassive() > maxNumOfPassiveLegsInPair)
			return false;
		return true;
	}

	@Override
	public boolean isAppendable(int hbNdx, PairView p, DutyView d, boolean fw) {
		if ((p.getNumOfLegsPassive() + d.getNumOfLegsPassive() > maxNumOfPassiveLegsInPair))
			return false;
		return true;
	}
}
