package org.heuros.hyperpair.rule;

import java.time.temporal.ChronoUnit;

import org.heuros.core.rule.inf.AbstractRule;
import org.heuros.core.rule.inf.Aggregator;
import org.heuros.core.rule.inf.RuleImplementation;
import org.heuros.data.model.Duty;
import org.heuros.data.model.DutyLeg;
import org.heuros.data.model.DutyLegFactory;
import org.heuros.data.model.DutyLegView;
import org.heuros.data.model.LegView;

@RuleImplementation(ruleName = "Duty Leg aggregator", 
					violationMessage = "Duty Leg aggregator failed", 
					description = "Leg aggregator for duties.")
public class DutyLegAggregator extends AbstractRule implements Aggregator<Duty, LegView> {

//	/*
//	 * Hotel transport duration.
//	 */
//	private int hotelTransportTime = 30;
//
//	private int getHotelTransportTime(Duty d) {
//		return hotelTransportTime;
//	}
//
//	/*
//	 * Briefing durations.
//	 */
//	private int briefPeriodBeforeDuty = 60;
//	private int debriefPeriodAfterDuty = 30;
//
//	private int getBriefPeriod(Duty d) {
//		return briefPeriodBeforeDuty;
//	}
//
//	private int getDebriefPeriod(Duty d) {
//		return debriefPeriodAfterDuty;
//	}
//
//	/*
//	 * Decides whether a duty is Early.
//	 */
//	private int earlyDutyPeriodStartLCL = 21;
//	private int earlyDutyPeriodEndLCL = 3;
//
//	private boolean isDutyEarly(Duty d) {
//		d.calendar.setTime(d._briefTime);
//		double hour = d.calendar.get(Calendar.HOUR_OF_DAY);
//		hour += d.calendar.get(Calendar.MINUTE) / 60.0;
//
//		if (earlyDutyPeriodStartLCL > earlyDutyPeriodEndLCL) {
//			if ((hour >= earlyDutyPeriodStartLCL)
//					|| (hour <= earlyDutyPeriodEndLCL))
//					return true;
//		} else
//			if ((hour >= earlyDutyPeriodStartLCL) && (hour <= earlyDutyPeriodEndLCL))
//				return true;
//
//		return false;
//	}
//
//	/*
//	 * ER - Extended range logic.
//	 */
//	private int minTimeZoneDiffForER = 60 * 4;
//	private int minFP_ER_1 = 60 * 8;
//	private int minFP_ER_3 = 60 * 8 + 30;
//	private int minFP_ER_3_1 = 60 * 6;
//
//	private boolean isFlightAnER(Flight f) {
//		return ((f._blockTime >= minFP_ER_1)
//				|| (Math.abs(f.getDepGmtDiff() - f.getArrGmtDiff()) > minTimeZoneDiffForER));
//	}
//
//	private boolean isFlightAMultiLandER(Flight f) {
//		return (f._blockTime >= minFP_ER_3_1) 
//				|| (Math.abs(f.getDepGmtDiff() - f.getArrGmtDiff()) > minTimeZoneDiffForER)
//				|| (f.getFlightNo() == 706)
//				|| (f.getFlightNo() == 3006);
//	}
//
//	private boolean isDutyAnER(Duty d) {
//        if (d.getDutyFlightCount() - d._totNumOfLegsPassive == 1) {
//        	if (d._fdf().isActive())
//        		return isFlightAnER(d.getDutyFlight(0).getFlight());
//        } else
//        	if (d.getDutyFlightCount() - d._totNumOfLegsPassive > 1) {
//		        if (d._totBlockTimeActive >= minFP_ER_3) {
//		        	for (int i = 0; i < d.getDutyFlightCount(); i++) {
//		        		if (d.getDutyFlight(i).isActive())
//			                if (isFlightAMultiLandER(d.getDutyFlight(i).getFlight()))
//			                    return true;
//		            }
//		        }
//        	}
//        return false;
//	}
//
//	/*
//	 * Min rest period calculation.
//	 */
//	private int minRP_After_OneLegDP_6 = 60 * 10;
//	private int minRP_After_DP_11 = 60 * 10;
//	private int minRP_After_DP_11plus = 60 * 12;
//	private int minRP_After_DP_12_14 = 60 * 14;
//	private int minRP_After_DP_ER = 60 * 36;
//
//	private int minRP_HB_After_OneLegDP_6 = 60 * 12;
//	private int minRP_HB_After_DP_11 = 60 * 12;
//	private int minRP_HB_After_DP_11plus = 60 * 12;
//	private int minRP_HB_After_DP_12_14 = 60 * 14;
//	private int minRP_HB_After_DP_ER = 60 * 36;
//
//	private int getDutyMinRestPeriod(Duty d) {
//		if (d == null)
//			return minRP_After_OneLegDP_6;
//		int totalDutyPeriod = d._totDutyDuration;
//
//    	boolean hb = d._lf().getArrAirport()._hb;
//
//    	int res = 0;
//
//    	if (d._er) {
//    		if (hb)
//        		res = minRP_HB_After_DP_ER;
//    		else
//    			res = minRP_After_DP_ER;
//    	} else
//    		if ((totalDutyPeriod < 6 * 60) && (d.getDutyFlightCount() == 2)) {
//	    		if (hb)
//	    			res = minRP_HB_After_OneLegDP_6;
//	    		else
//	    			res = minRP_After_OneLegDP_6;
//	        } else
//		        if (totalDutyPeriod <= 11 * 60) {
//		    		if (hb)
//		    			res = minRP_HB_After_DP_11;
//		    		else
//		    			res = minRP_After_DP_11;
//		        } else
//			        if (totalDutyPeriod < 12 * 60) {
//			    		if (hb)
//			    			res = minRP_HB_After_DP_11plus;
//			    		else
//			    			res = minRP_After_DP_11plus;
//			        } else
//			            if (totalDutyPeriod <= 14 * 60) {
//				    		if (hb)
//				    			res = minRP_HB_After_DP_12_14;
//				    		else
//				    			res = minRP_After_DP_12_14;
//			            }
//    	int btr = (int) (2.0 * d._totBlockTimeActive);
//    	if ((res >= totalDutyPeriod) && (res >= btr))
//    		return res + 2 * getHotelTransportTime(d);
//
//    	if (totalDutyPeriod >= btr)
//    		return totalDutyPeriod + 2 * getHotelTransportTime(d);
//    	else
//    		return btr + 2 * getHotelTransportTime(d);
//    }
//
//	/*
//	 * Duty crew model calculation
//	 */
//	private int augLimit_ER_Normal = 60 * 14;
//	private int augLimit_ER_wNA = 60 * 18;
//
//	private int augLimit_2101_0459_4 = 60 * 12 - 15;
//	private int augLimit_0500_1400_4 = 60 * 14 - 20;
//	private int augLimit_1401_1700_4 = 60 * 13 - 20;
//	private int augLimit_1701_2100_4 = 60 * 12 - 15;
//
//	private int augLimit_2101_0459_5 = augLimit_2101_0459_4 - 60;
//	private int augLimit_0500_1400_5 = augLimit_0500_1400_4 - 60;
//	private int augLimit_1401_1700_5 = augLimit_1401_1700_4 - 60;
//	private int augLimit_1701_2100_5 = augLimit_1701_2100_4 - 45;
//
//	private int maxDP_2101_0459_4_Buffered =  60 * 12 - 15;
//	private int maxDP_0500_1400_4_Buffered = 60 * 13;
//	private int maxDP_1401_1700_4_Buffered =  60 * 13 - 30;
//	private int maxDP_1701_2100_4_Buffered =  60 * 12 - 15;
//
//	private int maxDP_2101_0459_5_Buffered = maxDP_2101_0459_4_Buffered - 60;
//	private int maxDP_0500_1400_5_Buffered = maxDP_0500_1400_4_Buffered - 60;
//	private int maxDP_1401_1700_5_Buffered = maxDP_1401_1700_4_Buffered - 60;
//	private int maxDP_1701_2100_5_Buffered = maxDP_1701_2100_4_Buffered - 60;
//
//	@Override
//	private int getDutyCrewModel(Duty d) {
//		int res = 0;
//
//		int totalDutyPeriod = d._totDutyDuration;
//
//        if (d._er) {
//            if (totalDutyPeriod <= augLimit_ER_Normal)
//            	res = 0;
//            else
//                if (totalDutyPeriod <= augLimit_ER_wNA)
//                	res = 1;
//                else
//                	res = 2;
//        } else {
//
//        	Flight ff = d._ff();
//
//        	Date lcl = DatetimeUtil.incDate(d.calendar
//        									, ff.getScheduledOffblockUtc()
//											, - briefPeriodBeforeDuty + ff.getDepGmtDiff()
//											, Calendar.MINUTE);
//        	d.calendar.setTime(lcl);
//        	double hour = d.calendar.get(Calendar.HOUR_OF_DAY);
//        	hour += d.calendar.get(Calendar.MINUTE) / 60.0;
//        	hour += (ff.getDepAirport().getGmtDiff() - ff.getDepGmtDiff()) / 60.0;		//	Yaz kýþ farký. 
//
////        	if (ff.getDepAirport()._hb) hour--;
//
//        	int numOfLegs = d.getDutyFlightCount();
//
//        	if (hour >= 5 && hour <= 14) {
//                if (numOfLegs < 5) {
//                    if (totalDutyPeriod <= augLimit_0500_1400_4)
//                   		res = 0;
//                    else
//                    	if (totalDutyPeriod <= augLimit_0500_1400_4 + 120)
//                       		res = 1;
//                    	else
//                    		if (totalDutyPeriod <= augLimit_0500_1400_4 + 240)
//                    			res = 2;
//                    		else
//                    			res = 10;
//                } else {
//                    if (totalDutyPeriod <= augLimit_0500_1400_5)
//                   		res = 0;
//                    else
//                    	if (totalDutyPeriod <= augLimit_0500_1400_5 + 120)
//                       		res = 1;
//                    	else
//                        	if (totalDutyPeriod <= augLimit_0500_1400_5 + 240)
//                        		res = 2;
//                        	else
//                        		res = 10;
//                }
//            } else if (hour > 14 && hour <= 17) {
//                if (numOfLegs < 5) {
//                    if (totalDutyPeriod <= augLimit_1401_1700_4)
//                   		res = 0;
//                    else
//                        if (totalDutyPeriod <= augLimit_1401_1700_4 + 120)
//                       		res = 1;
//                    	else
//                            if (totalDutyPeriod <= augLimit_1401_1700_4 + 240)
//                            	res = 2;
//                            else
//                            	res = 10;
//                } else {
//                    if (totalDutyPeriod <= augLimit_1401_1700_5)
//                   		res = 0;
//                    else
//                        if (totalDutyPeriod <= augLimit_1401_1700_5 + 120)
//                       		res = 1;
//                    	else
//                            if (totalDutyPeriod <= augLimit_1401_1700_5 + 240)
//                            	res = 2;
//                            else
//                            	res = 10;
//                }
//            } else if ((hour > 17) && (hour <= 21)) {
//                if (numOfLegs < 5) {
//                    if (totalDutyPeriod <= augLimit_1701_2100_4)
//                   		res = 0;
//                    else
//                        if (totalDutyPeriod <= augLimit_1701_2100_4 + 120)
//                       		res = 1;
//                    	else
//                            if (totalDutyPeriod <= augLimit_1701_2100_4 + 240)
//                            	res = 2;
//                            else
//                            	res = 10;
//                } else {
//                    if (totalDutyPeriod <= augLimit_1701_2100_5)
//                   		res = 0;
//                    else
//                        if (totalDutyPeriod <= augLimit_1701_2100_5 + 120)
//                       		res = 1;
//                    	else
//                            if (totalDutyPeriod <= augLimit_1701_2100_5 + 240)
//                            	res = 2;
//                            else
//                            	res = 10;
//                }
//            } else {
//                if (numOfLegs < 5) {
//                    if (totalDutyPeriod <= augLimit_2101_0459_4)
//                   		res = 0;
//                    else
//                        if (totalDutyPeriod <= augLimit_2101_0459_4 + 120)
//                       		res = 1;
//                    	else
//                            if (totalDutyPeriod <= augLimit_2101_0459_4 + 240)
//                            	res = 2;
//                            else
//                            	res = 10;
//                } else {
//                    if (totalDutyPeriod <= augLimit_2101_0459_5)
//                   		res = 0;
//                    else
//                        if (totalDutyPeriod <= augLimit_2101_0459_5 + 120)
//                       		res = 1;
//                    	else
//                            if (totalDutyPeriod <= augLimit_2101_0459_5 + 240)
//                            	res = 2;
//                            else
//                            	res = 10;
//                }
//            }
//
////        	hour -= (ff.getDepAirport().getGmtDiff() - ff.getDepGmtDiff()) / 60.0;		//	Yaz kýþ farký. 
//
//        	if (hour >= 5 && hour <= 14) {
//                if (numOfLegs < 5) {
//
//                	if (res == 0) {
//                		if (totalDutyPeriod > maxDP_0500_1400_4_Buffered)
//                			res = 7;
//                	} else 
//                    	if (res == 1) {
//                    		if (totalDutyPeriod > maxDP_0500_1400_4_Buffered + 120)
//                    			res = 8;
//                    	} else
//                    		if (res == 1) {
//	                        	if (totalDutyPeriod > maxDP_0500_1400_4_Buffered + 240)
//	                        		res = 9;
//	                        } 
//
//                } else {
//
//                	if (res == 0) {
//                		if (totalDutyPeriod > maxDP_0500_1400_5_Buffered)
//                			res = 7;
//                	} else 
//                    	if (res == 1) {
//                    		if (totalDutyPeriod > maxDP_0500_1400_5_Buffered + 120)
//                    			res = 8;
//                    	} else
//                    		if (res == 1) {
//	                        	if (totalDutyPeriod > maxDP_0500_1400_5_Buffered + 240)
//	                        		res = 9;
//	                        } 
//
//                }
//            } else if (hour > 14 && hour <= 17) {
//                if (numOfLegs < 5) {
//
//                	if (res == 0) {
//                		if (totalDutyPeriod > maxDP_1401_1700_4_Buffered)
//                			res = 7;
//                	} else 
//                    	if (res == 1) {
//                    		if (totalDutyPeriod > maxDP_1401_1700_4_Buffered + 120)
//                    			res = 8;
//                    	} else
//                    		if (res == 1) {
//	                        	if (totalDutyPeriod > maxDP_1401_1700_4_Buffered + 240)
//	                        		res = 9;
//	                        } 
//
//                } else {
//
//                	if (res == 0) {
//                		if (totalDutyPeriod > maxDP_1401_1700_5_Buffered)
//                			res = 7;
//                	} else 
//                    	if (res == 1) {
//                    		if (totalDutyPeriod > maxDP_1401_1700_5_Buffered + 120)
//                    			res = 8;
//                    	} else
//                    		if (res == 1) {
//	                        	if (totalDutyPeriod > maxDP_1401_1700_5_Buffered + 240)
//	                        		res = 9;
//	                        } 
//
//                }
//            } else if ((hour > 17) && (hour < 21)) {
//                if (numOfLegs < 5) {
//
//                	if (res == 0) {
//                		if (totalDutyPeriod > maxDP_1701_2100_4_Buffered)
//                			res = 7;
//                	} else 
//                    	if (res == 1) {
//                    		if (totalDutyPeriod > maxDP_1701_2100_4_Buffered + 120)
//                    			res = 8;
//                    	} else
//                    		if (res == 1) {
//	                        	if (totalDutyPeriod > maxDP_1701_2100_4_Buffered + 240)
//	                        		res = 9;
//	                        } 
//
//                } else {
//
//                	if (res == 0) {
//                		if (totalDutyPeriod > maxDP_1701_2100_5_Buffered)
//                			res = 7;
//                	} else 
//                    	if (res == 1) {
//                    		if (totalDutyPeriod > maxDP_1701_2100_5_Buffered + 120)
//                    			res = 8;
//                    	} else
//                    		if (res == 1) {
//	                        	if (totalDutyPeriod > maxDP_1701_2100_5_Buffered + 240)
//	                        		res = 9;
//	                        } 
//
//                }
//            } else {
//                if (numOfLegs < 5) {
//
//                	if (res == 0) {
//                		if (totalDutyPeriod > maxDP_2101_0459_4_Buffered)
//                			res = 7;
//                	} else 
//                    	if (res == 1) {
//                    		if (totalDutyPeriod > maxDP_2101_0459_4_Buffered + 120)
//                    			res = 8;
//                    	} else
//                    		if (res == 1) {
//	                        	if (totalDutyPeriod > maxDP_2101_0459_4_Buffered + 240)
//	                        		res = 9;
//	                        } 
//
//                } else {
//
//                	if (res == 0) {
//                		if (totalDutyPeriod > maxDP_2101_0459_5_Buffered)
//                			res = 7;
//                	} else 
//                    	if (res == 1) {
//                    		if (totalDutyPeriod > maxDP_2101_0459_5_Buffered + 120)
//                    			res = 8;
//                    	} else
//                    		if (res == 1) {
//	                        	if (totalDutyPeriod > maxDP_2101_0459_5_Buffered + 240)
//	                        		res = 9;
//	                        } 
//
//                }
//            }
//        }
//
//        return res;
//    }
//
//	/*
//	 * Decides whether a duty is hard.
//	 */
//	private int hardDutyPeriodStartUTC = 22;
//	private int hardDutyPeriodEndUTC = 2;
//
//	public boolean isDutyHard(Duty d) {
//		Date hardDutyStartTimeUTC = DatetimeUtil.incDate(d.calendar, d._briefDayBeginning, hardDutyPeriodStartUTC, Calendar.HOUR_OF_DAY);
//		Date hardDutyEndTimeUTC = DatetimeUtil.incDate(d.calendar
//														, hardDutyStartTimeUTC
//														, hardDutyPeriodEndUTC - hardDutyPeriodStartUTC + (hardDutyPeriodEndUTC < hardDutyPeriodStartUTC ? 24 : 0)
//														, Calendar.HOUR_OF_DAY);
//
//		if ((!d._briefTime.after(hardDutyStartTimeUTC))
//				&& (!d._debriefTime.before(hardDutyEndTimeUTC)))
//			return true;
//
//		return false;
//	}

	private DutyLegFactory dutyLegFactory = new DutyLegFactory();

	@Override
	public void append(Duty d, LegView l) {
		/*
		 * Append leg to the duty.
		 */
		LegView connLeg = d.getLastLeg();
		DutyLeg dutyLeg = this.dutyLegFactory.generateModel();
		dutyLeg.setDuty(d);
		dutyLeg.setLeg(l);
		dutyLeg.setActive(l.isCover());
		d.getDutyLegs().add(dutyLeg);

		/*
		 * Totalizers
		 */
		d.incBlockTimeInMins(l.getBlockTimeInMins());
		d.incNumOfLegs(1);
		if (l.isCover()) {
			d.incBlockTimeInMinsActive(l.getBlockTimeInMins());
			d.incNumOfLegsActive(1);
		} else {
			d.incBlockTimeInMinsPassive(l.getBlockTimeInMins());
			d.incNumOfLegsPassive(1);
		}

		if (l.getDepAirport().isDomestic() && l.getArrAirport().isInternational())
    		d.incNumOfLegsDomToInt(1);
        else
        	if (l.getDepAirport().isInternational() && l.getArrAirport().isDomestic())
        		d.incNumOfLegsIntToDom(1);

		if (l.getArrAirport().isCritical())
			d.incNumOfCriticalLegs(1);
		if (l.getArrAirport().isAgDg() || l.getDepAirport().isAgDg())
			d.incNumOfAgDg(1);
		if (l.isSpecialFlight())
			d.incNumOfSpecFlights(1);

		if (connLeg != null) {
			if (l.getDepAirport().isHb())
				d.incNumOfAnyHomebaseTouch(1);
			if (l.getDepAirport().isDomestic())
				d.incNumOfDomTouch(1);
			if (l.getDepAirport().isInternational())
				d.incNumOfIntTouch(1);

			if (connLeg.isCover() && l.isCover()
					&& ((connLeg.getAcSequence() != l.getAcSequence())
							|| (!connLeg.getAcType().equals(l.getAcType()))))
				d.incNumOfAcChanges(1);

			int connTime = (int) ChronoUnit.MINUTES.between(connLeg.getSibt(), l.getSobt());
			if (connTime > 120) {
				d.incLongConnDiff(connTime - 120);
			}
		}


		/*
		 * Max blocktime
		 */
		if (d.getLongestBlockTimeInMins() < l.getBlockTimeInMins())
        	d.setLongestBlockTimeInMins(l.getBlockTimeInMins());
		d.getLongestBlockTimesInMins()[d.getNumOfLegs()] = l.getBlockTimeInMins();

	}

	@Override
	public LegView removeLast(Duty d) {
		DutyLegView dutyLeg = d.removeLast();
		if (dutyLeg == null)
			return null;

		LegView l = dutyLeg.getLeg();
		LegView connLeg = d.getLastLeg();

		/*
		 * Totalizers
		 */
		d.incBlockTimeInMins(-l.getBlockTimeInMins());
		d.incNumOfLegs(-1);
		if (l.isCover()) {
			d.incBlockTimeInMinsActive(-l.getBlockTimeInMins());
			d.incNumOfLegsActive(-1);
		} else {
			d.incBlockTimeInMinsPassive(-l.getBlockTimeInMins());
			d.incNumOfLegsPassive(-1);
		}

		if (l.getDepAirport().isDomestic() && l.getArrAirport().isInternational())
    		d.incNumOfLegsDomToInt(-1);
        else
        	if (l.getDepAirport().isInternational() && l.getArrAirport().isDomestic())
        		d.incNumOfLegsIntToDom(-1);

		if (l.getArrAirport().isCritical())
			d.incNumOfCriticalLegs(-1);
		if (l.getArrAirport().isAgDg() || l.getDepAirport().isAgDg())
			d.incNumOfAgDg(-1);
		if (l.isSpecialFlight())
			d.incNumOfSpecFlights(-1);

		if (connLeg != null) {
			if (l.getDepAirport().isHb())
				d.incNumOfAnyHomebaseTouch(-1);
			if (l.getDepAirport().isDomestic())
				d.incNumOfDomTouch(-1);
			if (l.getDepAirport().isInternational())
				d.incNumOfIntTouch(-1);

			if (connLeg.isCover() && l.isCover()
					&& ((connLeg.getAcSequence() != l.getAcSequence())
							|| (!connLeg.getAcType().equals(l.getAcType()))))
				d.incNumOfAcChanges(-1);

			int connTime = (int) ChronoUnit.MINUTES.between(connLeg.getSibt(), l.getSobt());
			if (connTime > 120) {
				d.incLongConnDiff(120 - connTime);
			}
		}

		/*
		 * Max blocktime
		 */
       	d.setLongestBlockTimeInMins(d.getLongestBlockTimesInMins()[d.getNumOfLegs()]);

       	return l;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}

}
