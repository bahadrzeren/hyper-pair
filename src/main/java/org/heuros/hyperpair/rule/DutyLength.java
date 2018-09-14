package org.heuros.hyperpair.rule;

import org.heuros.core.rule.intf.RuleImplementation;
import org.heuros.core.rule.intf.TotalizerChecker;
import org.heuros.data.model.DutyView;

@RuleImplementation(ruleName="DutyLength", 
					description="Duty augmentation check.", 
					violationMessage="Augmentation violation!")
public class DutyLength implements TotalizerChecker<DutyView> {

//	@Override
//	public boolean _isDutyValid(Duty d) {
//		return d._augm <= 2;
//	}

	@Override
	public boolean acceptable(int hbNdx, DutyView d) {
        /*
		 * TODO An additonal HB parameter is necessary for accurate HB or NonHb desicion.
		 */
		return d.getAugmented(hbNdx) <= 2;
	}
}
