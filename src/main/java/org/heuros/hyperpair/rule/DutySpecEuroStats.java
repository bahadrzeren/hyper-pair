package org.heuros.hyperpair.rule;

import java.time.temporal.ChronoUnit;

import org.heuros.core.rule.inf.AbstractRule;
import org.heuros.core.rule.inf.ConnectionChecker;
import org.heuros.core.rule.inf.RuleImplementation;
import org.heuros.data.model.LegView;

@RuleImplementation(ruleName="DutySpecEuroStats", 
					description="Special stations rule.", 
					violationMessage="Special station usage violation!")
public class DutySpecEuroStats extends AbstractRule implements ConnectionChecker<LegView> {

	private Integer minLegConnTimeForSpecStats = 105; // 1:45

//	@Override
//	public boolean _areFlightsConnectable(Flight pf, boolean isPfActive, Flight nf, boolean isNfActive) {
//		int legConnTime = DatetimeUtil.getDateDiffInMins(pf.getScheduledInblockUtc(), nf.getScheduledOffblockUtc());
//
//		if (((AirportAddonImpl) nf.getArrAirport()._addon).isSpecEuroStat_
//        		&& nf.getDepAirport()._hb
//        		&& isNfActive
//        		&& (pf.getDepAirport()._int	// && (pf.getDepAirport()._ndx != nf.getArrAirport()._ndx))
//        				|| (legConnTime < minLegConnTimeForSpecStats)))
//        	return false;
//		return true;
//	}

	@Override
	public boolean areConnectable(LegView pl, LegView nl) {

		int legConnTime = (int) ChronoUnit.MINUTES.between(pl.getSibt(), nl.getSobt());

        /*
		 * TODO An additonal HB parameter is necessary for accurate HB or NonHb desicion.
		 */
		if (nl.getArrAirport().isSpecialEuroStation()
        		&& nl.getDepAirport().isHb()
        		&& nl.isCover()
        		&& (pl.getDepAirport().isInternational()
        				|| (legConnTime < minLegConnTimeForSpecStats)))
        	return false;
		return true;
	}
}