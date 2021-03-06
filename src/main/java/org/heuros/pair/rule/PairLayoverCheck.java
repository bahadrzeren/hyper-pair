package org.heuros.pair.rule;

import java.time.temporal.ChronoUnit;

import org.heuros.core.rule.intf.ConnectionChecker;
import org.heuros.core.rule.intf.RuleImplementation;
import org.heuros.data.model.DutyView;

@RuleImplementation(ruleName="PairLayoverCheck", 
					description="Max layover length check.", 
							violationMessage="Max layover length limit violation!")
public class PairLayoverCheck implements ConnectionChecker<DutyView> {

	private Integer maxIntLay = 60 * 60; // 56;
	private Integer maxDomLay = 30 * 60; // 56;
	private Integer incRestTimeForSpecStats = 300;

//	@Override
//	public boolean _areDutiesConnectable(Duty pd, Duty nd) {
//
//		/*
//		 * Return after ER IY to homebase. 
//		 */
//		if (nd._ff().getDepAirport()._dom
//				&& nd._lf().getArrAirport()._nonHB
//				&& (pd._er))
//			return false;
//
//		/*
//		 * Layover period
//		 */
//		int layoverDuration = DatetimeUtil.getDateDiffInMins(pd._debriefTime, nd._briefTime);
//
//    	if (pd._lf().getArrAirport()._dom) {
//        	if (layoverDuration > maxDomLay)
//        		return false;
//        } else {
//        	if (((AirportAddonImpl) pd._lf().getArrAirport()._addon).isSpecEuroStat_)
//        		layoverDuration -= incRestTimeForSpecStats;
//        	if (layoverDuration > maxIntLay)
//        		return false;
//        }
//
//        return true;
//	}

	@Override
	public boolean areConnectable(int hbNdx, DutyView pd, DutyView nd) {
		/*
		 * Return after ER IY to homebase.
		 * 
		 * isAnyNonHb check is used for backward compatibility.
		 */
		if (nd.getFirstLeg().getDepAirport().isDomestic()
				&& nd.getLastArrAirport().isAnyNonHb()
				&& (pd.isEr()))
			return false;

		/*
		 * Layover period
		 */
		int layoverDuration = (int) ChronoUnit.MINUTES.between(pd.getDebriefTime(hbNdx), nd.getBriefTime(hbNdx));

    	if (pd.getLastLeg().getArrAirport().isDomestic()) {
        	if (layoverDuration > maxDomLay)
        		return false;
        } else {
        	if (pd.getLastArrAirport().isSpecialEuroStation())
        		layoverDuration -= incRestTimeForSpecStats;
        	if (layoverDuration > maxIntLay)
        		return false;
        }

        return true;
	}

}
