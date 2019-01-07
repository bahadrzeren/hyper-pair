package org.heuros.pair.rule;

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
		int idleTime = (int) ChronoUnit.MINUTES.between(pd.getNextBriefTime(hbNdx), nd.getBriefTime(hbNdx));
		if (idleTime < 0)
			return false;
		return true;
	}

}
