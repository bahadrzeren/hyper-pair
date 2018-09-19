package org.heuros.hyperpair.rule;

import org.heuros.core.rule.intf.ConnectionChecker;
import org.heuros.core.rule.intf.RuleImplementation;
import org.heuros.data.model.DutyView;

@RuleImplementation(ruleName="PairHardDuty", 
					description="Hard duties rule.", 
					violationMessage="Can not connect two hard duties sequentially!")
public class PairHardDuty implements ConnectionChecker<DutyView> {

//	@Override
//	public boolean _areDutiesConnectable(Duty pd, Duty nd) {
//		/*
//		 * Hard dutyleri baglama.
//		 */
//        if (pd._isHard && nd._isHard)
//        	return false;
//
//        return true;
//	}

	@Override
	public boolean areConnectable(int hbNdx, DutyView pd, DutyView nd) {
		/*
		 * Do not connect two hard duties back to back.
		 */
        if (pd.isHard(hbNdx) && nd.isHard(hbNdx))
        	return false;

        return true;
	}
}
