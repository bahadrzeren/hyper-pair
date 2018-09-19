package org.heuros.hyperpair.rule;

import org.heuros.core.rule.intf.ConnectionChecker;
import org.heuros.core.rule.intf.RuleImplementation;
import org.heuros.data.model.DutyView;

@RuleImplementation(ruleName = "PairGrouping",
					description = "Pair grouping rule.",
					violationMessage = "Illegal duty group connection!")
public class PairGrouping implements ConnectionChecker<DutyView> {

//	@Override
//	public boolean _areDutiesConnectable(Duty pd, Duty nd) {
//        /*
//         * GroupId kontrol
//         */
//        if (pd._lf().getArrAirport()._nonHB
//        		&& nd._lf().getArrAirport()._nonHB
//        		&& (((AirportAddonImpl) pd._lf().getArrAirport()._addon).groupId_ > 0)
//        		&& (((AirportAddonImpl) nd._lf().getArrAirport()._addon).groupId_ > 0)
//        		&& (((AirportAddonImpl) pd._lf().getArrAirport()._addon).groupId_
//        				!= ((AirportAddonImpl) nd._lf().getArrAirport()._addon).groupId_))
//        	return false;
//		return true;
//	}

	@Override
	public boolean areConnectable(int hbNdx, DutyView pd, DutyView nd) {
		/*
		 * GroupId kontrol
		 *
		 * isAnyNonHb check is used for backward compatibility.
		 */
		if (pd.getLastArrAirport().isAnyNonHb() && nd.getLastArrAirport().isAnyNonHb()
				&& (pd.getLastArrAirport().getGroupId() > 0)
				&& (nd.getLastArrAirport().getGroupId() > 0)
				&& (pd.getLastArrAirport().getGroupId() != nd.getLastArrAirport().getGroupId()))
			return false;
		return true;
	}
}
