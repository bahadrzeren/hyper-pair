package org.heuros.hyperpair.rule;

import org.heuros.core.rule.inf.AbstractRule;
import org.heuros.core.rule.inf.ConnectionChecker;
import org.heuros.core.rule.inf.RuleImplementation;
import org.heuros.data.model.DutyView;

@RuleImplementation(ruleName = "PairGrouping",
					description = "Pair grouping rule.",
					violationMessage = "Illegal duty group connection!")
public class PairGrouping extends AbstractRule implements ConnectionChecker<DutyView> {

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
	public boolean areConnectable(DutyView pd, DutyView nd) {
		/*
		 * GroupId kontrol
		 * 
		 * TODO An additonal HB parameter is necessary for accurate HB or NonHb desicion.
		 */
		if (pd.getLastArrAirport().isNonHb() && nd.getLastArrAirport().isNonHb()
				&& (pd.getLastArrAirport().getGroupId() > 0)
				&& (nd.getLastArrAirport().getGroupId() > 0)
				&& (pd.getLastArrAirport().getGroupId() != nd.getLastArrAirport().getGroupId()))
			return false;
		return true;
	}
}
