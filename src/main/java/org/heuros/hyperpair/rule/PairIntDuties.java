package org.heuros.hyperpair.rule;

import org.heuros.core.rule.intf.RuleImplementation;
import org.heuros.core.rule.intf.FinalChecker;
import org.heuros.data.model.PairView;

@RuleImplementation(ruleName="PairIntDuties", 
					description="Max number of international duties check.", 
					violationMessage="Max number of international duties limit exceeded!")
public class PairIntDuties implements FinalChecker<PairView> {

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
	public boolean acceptable(int hbNdx, PairView p) {
		/*
		 * Max num of int duties
		 */
       if (p.getNumOfInternationalDuties() > maxNumOfIntDutiesInAPair)
    	   return false;
       return true;
	}

}
