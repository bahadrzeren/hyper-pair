package org.heuros.hyperpair.rule;

import org.heuros.core.rule.inf.AbstractRule;
import org.heuros.core.rule.inf.ConnectionChecker;
import org.heuros.core.rule.inf.RuleImplementation;
import org.heuros.data.model.DutyView;

@RuleImplementation(ruleName="PairHardDuty", 
					description="Hard duties rule.", 
					violationMessage="Can not connect two hard duties sequentially!")
public class PairHardDuty extends AbstractRule implements ConnectionChecker<DutyView> {

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
	public boolean areConnectable(DutyView pd, DutyView nd) {
		/*
		 * Do not connect two hard duties back to back.
		 * 
		 * TODO An additonal HB parameter is necessary for accurate HB or NonHb desicion.
		 */
        if (pd.isHardHb() && nd.isHardNonHb())
        	return false;

        return true;
	}
}
