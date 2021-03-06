package org.heuros.pair.rule;

import org.heuros.core.rule.intf.ConnectionChecker;
import org.heuros.core.rule.intf.RuleImplementation;
import org.heuros.data.model.LegView;

@RuleImplementation(ruleName="DutyCriticalStations",
					description="Critical stations rule.", 
					violationMessage="Critical station!")
public class CriticalStations1 implements ConnectionChecker<LegView> {

	private Integer maxBlockTimeBeforeCATC = 60 * 3;

//	@Override
//	public boolean _areFlightsConnectable(Flight pf, boolean isPfActive, Flight nf, boolean isNfActive) {
//	    /*
//    	 * Check whether IST or SAW departed CATC is first flight in duty.
//    	 */
//    	if (//isNfActive && 
//    			nf.getDepAirport()._hb
//    			&& ((AirportAddonImpl) nf.getArrAirport()._addon).isCritical_)
//    		return false;
//
//    	/*
//    	 * Do not connect CATC flight after a long flight.!!!
//    	 */
//    	if (//isNfActive && 
//    			((AirportAddonImpl) nf.getArrAirport()._addon).isCritical_
//        		&& maxBlockTimeBeforeCATC < pf._blockTime)
//        	return false;
//
//    	/*
//    	 * Do not connect two consecutive CATC flights.
//    	 */
//        if (//isPfActive && isNfActive && 
//        		((AirportAddonImpl) pf.getArrAirport()._addon).isCritical_
//        		&& ((AirportAddonImpl) nf.getArrAirport()._addon).isCritical_)
//        	return false;
//
//		return true;
//	}

	@Override
	public boolean areConnectable(int hbNdx, LegView pl, LegView nl) {
	    /*
	   	 * Check whether IST or SAW departed CATC is first flight in duty.
	   	 */
		/*
		 * isAnyHb check is used for backward compatibility.
		 */
	   	if (nl.getDepAirport().isAnyHb()
	   			&& nl.getArrAirport().isCritical())
	   		return false;

	   	/*
	   	 * Do not connect CATC flight after a long flight.!!!
	   	 */
	   	if (nl.getArrAirport().isCritical()
	       		&& (maxBlockTimeBeforeCATC < pl.getBlockTimeInMins()))
	       	return false;

	   	/*
	   	 * Do not connect two consecutive CATC flights.
	   	 */
	   	if (pl.getArrAirport().isCritical()
       		&& nl.getArrAirport().isCritical())
	   		return false;

		return true;
	}
}
