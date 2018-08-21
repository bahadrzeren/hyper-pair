package org.heuros.hyperpair.rule;

import org.heuros.core.rule.inf.AbstractRule;
import org.heuros.core.rule.inf.RuleImplementation;
import org.heuros.core.rule.inf.Validator;
import org.heuros.data.model.PairView;
import org.heuros.hyperpair.HeurosSystemParam;

@RuleImplementation(ruleName="PairPeriodLength", 
					description="Checks length of the trip.", 
					violationMessage="Trip length limit violation!")
public class PairPeriodLength extends AbstractRule implements Validator<PairView> {

//	@Override
//	public boolean _isPairValid(Pair p) {
//        if (60 * 24 * (p._ld()._debriefDay - p._fd()._briefDay + 1) > maxPP)
//        	return false;
//		return true;
//	}

	@Override
	public boolean isValid(PairView p) {
        if (p.getNumOfDaysTouched() > HeurosSystemParam.maxPairingLengthInDays)
        	return false;
		return true;
	}
}
