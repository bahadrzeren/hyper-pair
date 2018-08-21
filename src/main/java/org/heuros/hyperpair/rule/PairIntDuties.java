package org.heuros.hyperpair.rule;

import org.heuros.core.rule.inf.AbstractRule;
import org.heuros.core.rule.inf.RuleImplementation;
import org.heuros.core.rule.inf.Validator;
import org.heuros.data.model.PairView;

@RuleImplementation(ruleName="PairIntDuties", 
					description="Max number of international duties check.", 
					violationMessage="Max number of international duties limit exceeded!")
public class PairIntDuties extends AbstractRule implements Validator<PairView> {

	private Integer maxNumOfIntDutiesInAPair = 2; // Int layover ?

//	@Override
//	public boolean _isPairValid(Pair p) {
//		/*
//		 * Max num of int duties
//		 */
//        if (p._totNumOfIntDuties > maxNumOfIntDutiesInAPair)
//        	return false;
//
//        return true;
//	}

	@Override
	public boolean isValid(PairView p) {
		/*
		 * Max num of int duties
		 */
       if (p.getNumOfInternationalDuties() > maxNumOfIntDutiesInAPair)
    	   return false;
       return true;
	}

}
