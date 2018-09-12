package org.heuros.hyperpair.rule;

import java.time.temporal.ChronoUnit;

import org.heuros.core.rule.intf.ConnectionChecker;
import org.heuros.core.rule.intf.RuleImplementation;
import org.heuros.data.model.DutyView;

@RuleImplementation(ruleName="PairDutyRestCheck", 
					description="Rest period control.", 
					violationMessage="Rest period violation!")
public class PairDutyRestCheck implements ConnectionChecker<DutyView> {

//	@Override
//	public boolean _areDutiesConnectable(Duty pd, Duty nd) {
//		int idleTime = DatetimeUtil.getDateDiffInMins(pd._nextBriefTime, nd._briefTime);
//		if (idleTime < 0)
//			return false;
//		return true;
//	}

	@Override
	public boolean areConnectable(int hbNdx, DutyView pd, DutyView nd) {
		/*
		 * TODO An additonal HB parameter is necessary for accurate HB or NonHb desicion.
		 */
		int idleTime = (int) ChronoUnit.MINUTES.between(pd.getNextBriefTime(hbNdx), nd.getBriefTime(hbNdx));
		if (idleTime < 0)
			return false;
		return true;
	}

}
