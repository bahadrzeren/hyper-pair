package org.heuros.hyperpair.rule;

import org.heuros.core.rule.inf.AbstractRule;
import org.heuros.core.rule.inf.ConnectionChecker;
import org.heuros.core.rule.inf.ExtensibilityChecker;
import org.heuros.core.rule.inf.RuleImplementation;
import org.heuros.core.rule.inf.ValidationStatus;
import org.heuros.core.rule.inf.Validator;
import org.heuros.data.model.DutyView;
import org.heuros.data.model.PairView;

@RuleImplementation(ruleName="PairNumOfPassiveLegsLimit", 
					description="Checks total number of passive legs in a trip.", 
					violationMessage="Total number of passive legs exceeded!")
public class PairNumOfPassiveLegsLimit extends AbstractRule implements ExtensibilityChecker<PairView, DutyView>, 
																		ConnectionChecker<DutyView>,
																		Validator<PairView> {

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
	public ValidationStatus isValid(PairView p) {
		if (p.getNumOfLegsPassive() > maxNumOfPassiveLegsInPair)
			return ValidationStatus.invalid;
		return ValidationStatus.valid;
	}

	@Override
	public boolean areConnectable(DutyView prevModel, DutyView nextModel) {
		if (prevModel.getNumOfLegsPassive() + nextModel.getNumOfLegsPassive() > maxNumOfPassiveLegsInPair)
			return false;
		return true;
	}

	@Override
	public boolean isExtensible(PairView p, DutyView d) {
		if ((p.getNumOfLegsPassive() + d.getNumOfLegsPassive() > maxNumOfPassiveLegsInPair))
			return false;
		return true;
	}
}
