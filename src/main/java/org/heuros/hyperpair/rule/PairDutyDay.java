package org.heuros.hyperpair.rule;

import org.heuros.core.rule.inf.AbstractRule;
import org.heuros.core.rule.inf.ConnectionChecker;
import org.heuros.core.rule.inf.RuleImplementation;
import org.heuros.data.model.DutyView;

@RuleImplementation(ruleName="PairOneDutyPerDay", 
					description="Duty day rule.",
					violationMessage="Duty day violation!")
public class PairDutyDay extends AbstractRule implements ConnectionChecker<DutyView> {

//	@Override
//	public boolean _areDutiesConnectable(Duty pd, Duty nd) {
//		/*
//		 *	Max one duty can touch to a calendar day for IY. 
//		 */
//		if ((pd._lf().getArrAirport()._dom)
//				&& (pd._debriefDay == nd._briefDay)
////				&& (DatetimeUtil.getDateInMins(pd._debriefTime) > DatetimeUtil.getDateInMins(DatetimeUtil.getDayMinimum(pd._debriefTime)))
//				)
//			return false;
//
//		/*
//		 *	Max one duty can start per calendar day. 
//		 */
//		if (pd._briefDay == nd._briefDay)
//			return false;
//
//		return true;
//	}

	@Override
	public boolean areConnectable(DutyView pd, DutyView nd) {
		/*
		 *	Max one duty can touch to a calendar day for IY. 
		 */
		if (pd.getLastArrAirport().isDomestic()
				&& pd.getDebriefDay().isEqual(nd.getBriefDayNonHb()))
			return false;

		/*
		 *	Max one duty can start per calendar day. 
		 */
		if (pd.getBriefDayHb() == nd.getBriefDayNonHb())
			return false;

		return true;
	}

}