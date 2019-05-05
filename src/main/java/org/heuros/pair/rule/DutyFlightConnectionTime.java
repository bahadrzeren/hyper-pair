package org.heuros.pair.rule;

import java.time.temporal.ChronoUnit;

import org.heuros.core.rule.intf.ConnectionChecker;
import org.heuros.core.rule.intf.RuleImplementation;
import org.heuros.data.model.LegView;

@RuleImplementation(ruleName="DutyFlightConnectionTime", 
					description="Leg connection time check.", 
					violationMessage="Connection time limit violation!")
public class DutyFlightConnectionTime implements ConnectionChecker<LegView> {

	private int minLegConnTime30 = 30;
	private int minLegConnTime40 = 40;
	private int minLegConnTime45 = 45;
	private int minLegConnTime50 = 50;
	private int minLegConnTime55 = 55;
	private int minLegConnTime60 = 60;
	private int minLegConnTime70 = 70;

	public static int minLegConnTimeForIST = 90;
	public static int minLegConnTimeForSAW = 60;
	public static int minLegConnTimeForISTESB = 90;

	private int getMinConnTime(int hbNdx, LegView pl, boolean isPlActive, LegView nl, boolean isNlActive) {
		if (pl.getFlightNo() == 845)
			return minLegConnTime30;
		/*
		 * isAnyHb check is used for backward compatibility.
		 */
		if (pl.getArrAirport().isAnyHb()) {
    		if (pl.getArrAirport().getCode().equals("IST")) {
    			if (nl.getArrAirport().getCode().equals("ESB")
    					&& isPlActive && isNlActive)
        			return minLegConnTimeForISTESB;
    			else
   					return minLegConnTimeForIST;
    		} else {												//	SAW ise
    			if (pl.hasAcChangeWith(nl)) {						//	AC change var
    				if (pl.getDepAirport().isDomestic()) {					//	Incoming domestic
    					if (nl.getArrAirport().isDomestic()) {					//	Outgoing domestic
    						if (isPlActive && isNlActive) {
    							return minLegConnTime60;
    						} else
    							if (isPlActive && (!isNlActive)) {
    								return minLegConnTime40;
    							} else
    								if ((!isPlActive) && isNlActive) {
    									return minLegConnTime50;
    								}
    					} else {										//	Outgoing international
    						if (isPlActive && isNlActive) {
    							return minLegConnTime60;
    						} else
    							if (isPlActive && (!isNlActive)) {
    								return minLegConnTime50;
    							} else
    								if ((!isPlActive) && isNlActive) {
    									return minLegConnTime60;
    								}
    					}
    				} else {											//	Incoming international
    					if (nl.getArrAirport().isDomestic()) {					//	Outgoing domestic
    						if (isPlActive && isNlActive) {
    							return minLegConnTime60;
    						} else
    							if (isPlActive && (!isNlActive)) {
    								return minLegConnTime50;
    							} else
    								if ((!isPlActive) && isNlActive) {
    									return minLegConnTime60;
    								}
    					} else {										//	Outgoing international
    						if (isPlActive && isNlActive) {
    							return minLegConnTime60;
    						} else
    							if (isPlActive && (!isNlActive)) {
    								return minLegConnTime50;
    							} else
    								if ((!isPlActive) && isNlActive) {
    									return minLegConnTime60;
    								}
    					}
    				}
    			} else {												//	AC change yok
    				if (pl.getDepAirport().isDomestic()) {					//	Incoming domestic
    					if (nl.getArrAirport().isDomestic()) {					//	Outgoing domestic
    						if (isPlActive && isNlActive) {
    							return minLegConnTime40;
    						} else
    							if (isPlActive && (!isNlActive)) {
    								return minLegConnTime40;
    							} else
    								if ((!isPlActive) && isNlActive) {
    									return minLegConnTime40;
    								}
    					} else {										//	Outgoing international
    						if (isPlActive && isNlActive) {
    							return minLegConnTime45;
    						} else
    							if (isPlActive && (!isNlActive)) {
    								return minLegConnTime40;
    							} else
    								if ((!isPlActive) && isNlActive) {
    									return minLegConnTime45;
    								}
    					}
    				} else {											//	Incoming international
    					if (nl.getArrAirport().isDomestic()) {					//	Outgoing domestic
    						if (isPlActive && isNlActive) {
    							return minLegConnTime55;
    						} else
    							if (isPlActive && (!isNlActive)) {
    								return minLegConnTime50;
    							} else
    								if ((!isPlActive) && isNlActive) {
    									return minLegConnTime55;
    								}
    					} else {										//	Outgoing international
    						if (isPlActive && isNlActive) {
    							return minLegConnTime55;
    						} else
    							if (isPlActive && (!isNlActive)) {
    								return minLegConnTime50;
    							} else
    								if ((!isPlActive) && isNlActive) {
    									return minLegConnTime55;
    								}
    					}
    				}
    			}
    			return minLegConnTimeForSAW;
			} 
		} else {
			if (pl.getArrAirport().getCode().equals("ESB")) {

				if (pl.hasAcChangeWith(nl)) {
					if (pl.getDepAirport().isDomestic()) {					//	Incoming domestic
						if (nl.getArrAirport().isDomestic()) {					//	Outgoing domestic
							if (isPlActive && isNlActive) {
								return minLegConnTime60;
							} else
								if (isPlActive && (!isNlActive)) {
									return minLegConnTime70;
								} else
									if ((!isPlActive) && isNlActive) {
										return minLegConnTime70;
									}
						} else {										//	Outgoing international
							if (isPlActive && isNlActive) {
								return minLegConnTime70;
							} else
								if (isPlActive && (!isNlActive)) {
									return minLegConnTime70;
								} else
									if ((!isPlActive) && isNlActive) {
										return minLegConnTime70;
									}
						}
					} else {											//	Incoming international
						if (nl.getArrAirport().isDomestic()) {					//	Outgoing domestic
							if (isPlActive && isNlActive) {
								return minLegConnTime70;
							} else
								if (isPlActive && (!isNlActive)) {
									return minLegConnTime70;
								} else
									if ((!isPlActive) && isNlActive) {
										return minLegConnTime70;
									}
						} else {										//	Outgoing international
							if (isPlActive && isNlActive) {
								return minLegConnTime70;
							} else
								if (isPlActive && (!isNlActive)) {
									return minLegConnTime60;
								} else
									if ((!isPlActive) && isNlActive) {
										return minLegConnTime60;
									}
						}
					}
				} else {
					if (pl.getDepAirport().isDomestic()) {					//	Incoming domestic
						if (nl.getArrAirport().isDomestic()) {					//	Outgoing domestic
							if (isPlActive && isNlActive) {
								return minLegConnTime45;
							} else
								if (isPlActive && (!isNlActive)) {
									return minLegConnTime45;
								} else
									if ((!isPlActive) && isNlActive) {
										return minLegConnTime45;
									}
						} else {										//	Outgoing international
							if (isPlActive && isNlActive) {
								return minLegConnTime60;
							} else
								if (isPlActive && (!isNlActive)) {
									return minLegConnTime60;
								} else
									if ((!isPlActive) && isNlActive) {
										return minLegConnTime60;
									}
						}
					} else {											//	Incoming international
						if (nl.getArrAirport().isDomestic()) {					//	Outgoing domestic
							if (isPlActive && isNlActive) {
								return minLegConnTime60;
							} else
								if (isPlActive && (!isNlActive)) {
									return minLegConnTime60;
								} else
									if ((!isPlActive) && isNlActive) {
										return minLegConnTime60;
									}
						} else {										//	Outgoing international
							if (isPlActive && isNlActive) {
								return minLegConnTime60;
							} else
								if (isPlActive && (!isNlActive)) {
									return minLegConnTime60;
								} else
									if ((!isPlActive) && isNlActive) {
										return minLegConnTime60;
									}
						}
					}
				}
				return minLegConnTime45;

			} else {

				if (pl.hasAcChangeWith(nl)) {
					if (pl.getDepAirport().isDomestic()) {					//	Incoming domestic
						if (nl.getArrAirport().isDomestic()) {					//	Outgoing domestic
							if (isPlActive && isNlActive) {
								return minLegConnTime50;
							} else
								if (isPlActive && (!isNlActive)) {
									return minLegConnTime50;
								} else
									if ((!isPlActive) && isNlActive) {
										return minLegConnTime50;
									}
						} else {										//	Outgoing international
							if (isPlActive && isNlActive) {
								return minLegConnTime55;
							} else
								if (isPlActive && (!isNlActive)) {
									return minLegConnTime55;
								} else
									if ((!isPlActive) && isNlActive) {
										return minLegConnTime55;
									}
						}
					} else {											//	Incoming international
						if (nl.getArrAirport().isDomestic()) {					//	Outgoing domestic
							if (isPlActive && isNlActive) {
								return minLegConnTime55;
							} else
								if (isPlActive && (!isNlActive)) {
									return minLegConnTime55;
								} else
									if ((!isPlActive) && isNlActive) {
										return minLegConnTime55;
									}
						} else {										//	Outgoing international
							if (isPlActive && isNlActive) {
								return minLegConnTime55;
							} else
								if (isPlActive && (!isNlActive)) {
									return minLegConnTime55;
								} else
									if ((!isPlActive) && isNlActive) {
										return minLegConnTime55;
									}
						}
					}
				} else {
					if (pl.getDepAirport().isDomestic()) {					//	Incoming domestic
						if (nl.getArrAirport().isDomestic()) {					//	Outgoing domestic
							if (isPlActive && isNlActive) {
								return minLegConnTime30;
							} else
								if (isPlActive && (!isNlActive)) {
									return minLegConnTime30;
								} else
									if ((!isPlActive) && isNlActive) {
										return minLegConnTime30;
									}
						} else {										//	Outgoing international
							if (isPlActive && isNlActive) {
								return minLegConnTime30;
							} else
								if (isPlActive && (!isNlActive)) {
									return minLegConnTime30;
								} else
									if ((!isPlActive) && isNlActive) {
										return minLegConnTime30;
									}
						}
					} else {											//	Incoming international
						if (nl.getArrAirport().isDomestic()) {					//	Outgoing domestic
							if (isPlActive && isNlActive) {
								return minLegConnTime45;
							} else
								if (isPlActive && (!isNlActive)) {
									return minLegConnTime45;
								} else
									if ((!isPlActive) && isNlActive) {
										return minLegConnTime45;
									}
						} else {										//	Outgoing international
							if (isPlActive && isNlActive) {
								return minLegConnTime30;
							} else
								if (isPlActive && (!isNlActive)) {
									return minLegConnTime30;
								} else
									if ((!isPlActive) && isNlActive) {
										return minLegConnTime30;
									}
						}
					}
				}
				return minLegConnTime30;

			}
		}
	}

	private int maxLegConnTimeHB = 60 * 3;
	private int maxLegConnTimeESB = 60 * 3 + 59;
	private int maxLegConnTimeDom = 60 * 3 + 59;
	private int maxLegConnTimeInt = 60 * 7;	//	3;
	private int maxLegConnTimeExc = 60 * 8;

	private int getMaxConnTime(int hbNdx, LegView pl, LegView nl) {
		/*
		 * isAnyHb check is used for backward compatibility.
		 */
        if (pl.getArrAirport().isAnyHb())
        	return maxLegConnTimeHB;

        if (pl.getArrAirport().isLegConnectionExceptionStation())
        	return maxLegConnTimeExc;

        if (pl.getArrAirport().isDomestic()) {
        	if (pl.getArrAirport().getCode().equals("ESB"))
        		return maxLegConnTimeESB;
        	else
        		return maxLegConnTimeDom;
        }

        return maxLegConnTimeInt;
	}

//	@Override
//	public boolean _areFlightsConnectable(Flight pf, boolean isPfActive, Flight nf, boolean isNfActive) {
//		int legConnTime = DatetimeUtil.getDateDiffInMins(pf.getScheduledInblockUtc(), nf.getScheduledOffblockUtc());
//
//        if (legConnTime < functionManager.getMinConnTime(pf, isPfActive, nf, isNfActive))
//        	return false;
//
//        if (legConnTime > functionManager.getMaxConnTime(pf, nf))
//        	return false;
//
//        return true;
//	}

	@Override
	public boolean areConnectable(int hbNdx, LegView pl, LegView nl) {
		int legConnTime = (int) ChronoUnit.MINUTES.between(pl.getSibt(), nl.getSobt());

        if (legConnTime < this.getMinConnTime(hbNdx, pl, pl.isCover(), nl, nl.isCover()))
        	return false;

        if (legConnTime > this.getMaxConnTime(hbNdx, pl, nl))
        	return false;

        return true;
	}

}
