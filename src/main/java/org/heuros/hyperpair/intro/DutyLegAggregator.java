package org.heuros.hyperpair.intro;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.heuros.core.rule.intf.Aggregator;
import org.heuros.core.rule.intf.RuleImplementation;
import org.heuros.data.model.Duty;
import org.heuros.data.model.LegView;
import org.heuros.hyperpair.HeurosSystemParam;

@RuleImplementation(ruleName = "Duty Leg aggregator", 
					violationMessage = "Duty Leg aggregator failed", 
					description = "Leg aggregator for duties.")
public class DutyLegAggregator implements Aggregator<Duty, LegView> {

	/*
	 * ER - Extended range logic.
	 */
	private int minTimeZoneDiffForER = 60 * 4;
	private int minFP_ER_1 = 60 * 8;
	private int minFP_ER_3 = 60 * 8 + 30;
	private int minFP_ER_3_1 = 60 * 6;

	private boolean isFlightAnER(LegView l) {
		return ((l.getBlockTimeInMins() >= minFP_ER_1)
				|| (Math.abs(l.getDepOffset() - l.getArrOffset()) > minTimeZoneDiffForER));
	}

	private boolean isFlightAMultiLandER(LegView l) {
		return (l.getBlockTimeInMins() >= minFP_ER_3_1) 
				|| (Math.abs(l.getDepOffset() - l.getArrOffset()) > minTimeZoneDiffForER)
				|| (l.getFlightNo() == 706)
				|| (l.getFlightNo() == 3006);
	}

	private boolean isDutyAnER(Duty d) {
        if (d.getNumOfLegsActive() == 1) {
        	if (d.getFirstLeg().isCover())
        		return isFlightAnER(d.getFirstLeg());
        } else
        	if (d.getNumOfLegsActive() > 1) {
		        if (d.getBlockTimeInMinsActive() >= minFP_ER_3) {
		        	for (int i = 0; i < d.getNumOfLegs(); i++) {
		        		if (d.getLegs().get(i).isCover())
			                if (isFlightAMultiLandER(d.getLegs().get(i)))
			                    return true;
		            }
		        }
        	}
        return false;
	}

	/*
	 * Hotel transport duration.
	 */
	private int hotelTransportTime = 30;

	private int getHotelTransportTime(Duty d) {
		return hotelTransportTime;
	}

	/*
	 * Briefing durations.
	 */
//	private int briefPeriodBeforeDutyHb = 90;
//	private int briefPeriodBeforeDutyNonHb = 60;
//	private int debriefPeriodAfterDuty = 30;

	private int getBriefPeriod(Duty d, int hbNdx) {
		if (d.getFirstDepAirport().getHbNdx() == hbNdx)
			return HeurosSystemParam.briefPeriodBeforeDutyHb;
		else
			return HeurosSystemParam.briefPeriodBeforeDutyNonHb;
	}

	private int getDebriefPeriod(Duty d, int hbNdx) {
		return HeurosSystemParam.debriefPeriodAfterDuty;
	}


	/*
	 * Min rest period calculation.
	 */
	private int minRP_After_OneLegDP_6 = 60 * 10;
	private int minRP_After_DP_11 = 60 * 10;
	private int minRP_After_DP_11plus = 60 * 12;
	private int minRP_After_DP_12_14 = 60 * 14;
	private int minRP_After_DP_ER = 60 * 36;

	private int minRP_HB_After_OneLegDP_6 = 60 * 12;
	private int minRP_HB_After_DP_11 = 60 * 12;
	private int minRP_HB_After_DP_11plus = 60 * 12;
	private int minRP_HB_After_DP_12_14 = 60 * 14;
	private int minRP_HB_After_DP_ER = 60 * 36;

	private int getDutyMinRestPeriod(Duty d, int totalDutyPeriod, boolean hbArr) {
		if (d == null)
			return minRP_After_OneLegDP_6;

    	int res = 0;

    	if (d.isEr()) {
    		if (hbArr)
        		res = minRP_HB_After_DP_ER;
    		else
    			res = minRP_After_DP_ER;
    	} else
    		if ((totalDutyPeriod < 6 * 60) && (d.getNumOfLegs() == 2)) {
	    		if (hbArr)
	    			res = minRP_HB_After_OneLegDP_6;
	    		else
	    			res = minRP_After_OneLegDP_6;
	        } else
		        if (totalDutyPeriod <= 11 * 60) {
		    		if (hbArr)
		    			res = minRP_HB_After_DP_11;
		    		else
		    			res = minRP_After_DP_11;
		        } else
			        if (totalDutyPeriod < 12 * 60) {
			    		if (hbArr)
			    			res = minRP_HB_After_DP_11plus;
			    		else
			    			res = minRP_After_DP_11plus;
			        } else
			            if (totalDutyPeriod <= 14 * 60) {
				    		if (hbArr)
				    			res = minRP_HB_After_DP_12_14;
				    		else
				    			res = minRP_After_DP_12_14;
			            }
    	int btr = (int) (2.0 * d.getBlockTimeInMins());
    	if ((res >= totalDutyPeriod) && (res >= btr))
    		return res + 2 * getHotelTransportTime(d);

    	if (totalDutyPeriod >= btr)
    		return totalDutyPeriod + 2 * getHotelTransportTime(d);
    	else
    		return btr + 2 * getHotelTransportTime(d);
    }

	/*
	 * Duty crew model calculation
	 */
	private int augLimit_ER_Normal = 60 * 14;
	private int augLimit_ER_wNA = 60 * 18;

	private int augLimit_2101_0459_4 = 60 * 12 - 15;
	private int augLimit_0500_1400_4 = 60 * 14 - 20;
	private int augLimit_1401_1700_4 = 60 * 13 - 20;
	private int augLimit_1701_2100_4 = 60 * 12 - 15;

	private int augLimit_2101_0459_5 = augLimit_2101_0459_4 - 60;
	private int augLimit_0500_1400_5 = augLimit_0500_1400_4 - 60;
	private int augLimit_1401_1700_5 = augLimit_1401_1700_4 - 60;
	private int augLimit_1701_2100_5 = augLimit_1701_2100_4 - 45;

	private int maxDP_2101_0459_4_Buffered =  60 * 12 - 15;
	private int maxDP_0500_1400_4_Buffered = 60 * 13;
	private int maxDP_1401_1700_4_Buffered =  60 * 13 - 30;
	private int maxDP_1701_2100_4_Buffered =  60 * 12 - 15;

	private int maxDP_2101_0459_5_Buffered = maxDP_2101_0459_4_Buffered - 60;
	private int maxDP_0500_1400_5_Buffered = maxDP_0500_1400_4_Buffered - 60;
	private int maxDP_1401_1700_5_Buffered = maxDP_1401_1700_4_Buffered - 60;
	private int maxDP_1701_2100_5_Buffered = maxDP_1701_2100_4_Buffered - 60;

	private int getDutyCrewModel(Duty d, int totalDutyPeriod, LocalDateTime briefTime) {
		int res = 0;

        if (d.isEr()) {
            if (totalDutyPeriod <= augLimit_ER_Normal)
            	res = 0;
            else
                if (totalDutyPeriod <= augLimit_ER_wNA)
                	res = 1;
                else
                	res = 2;
        } else {

        	LegView fl = d.getFirstLeg();

        	/*
        	 * TODO: Local hour calculation. Winter and Summer season difference also must be considered!!!
        	 */
        	LocalDateTime lcl = briefTime.plusMinutes(fl.getDepOffset());
        	double localHour = lcl.getHour() + lcl.getMinute() / 60.0; 

        	int numOfLegs = d.getNumOfLegs();

        	if (localHour >= 5 && localHour <= 14) {
                if (numOfLegs < 5) {
                    if (totalDutyPeriod <= augLimit_0500_1400_4)
                   		res = 0;
                    else
                    	if (totalDutyPeriod <= augLimit_0500_1400_4 + 120)
                       		res = 1;
                    	else
                    		if (totalDutyPeriod <= augLimit_0500_1400_4 + 240)
                    			res = 2;
                    		else
                    			res = 10;
                } else {
                    if (totalDutyPeriod <= augLimit_0500_1400_5)
                   		res = 0;
                    else
                    	if (totalDutyPeriod <= augLimit_0500_1400_5 + 120)
                       		res = 1;
                    	else
                        	if (totalDutyPeriod <= augLimit_0500_1400_5 + 240)
                        		res = 2;
                        	else
                        		res = 10;
                }
            } else if (localHour > 14 && localHour <= 17) {
                if (numOfLegs < 5) {
                    if (totalDutyPeriod <= augLimit_1401_1700_4)
                   		res = 0;
                    else
                        if (totalDutyPeriod <= augLimit_1401_1700_4 + 120)
                       		res = 1;
                    	else
                            if (totalDutyPeriod <= augLimit_1401_1700_4 + 240)
                            	res = 2;
                            else
                            	res = 10;
                } else {
                    if (totalDutyPeriod <= augLimit_1401_1700_5)
                   		res = 0;
                    else
                        if (totalDutyPeriod <= augLimit_1401_1700_5 + 120)
                       		res = 1;
                    	else
                            if (totalDutyPeriod <= augLimit_1401_1700_5 + 240)
                            	res = 2;
                            else
                            	res = 10;
                }
            } else if ((localHour > 17) && (localHour <= 21)) {
                if (numOfLegs < 5) {
                    if (totalDutyPeriod <= augLimit_1701_2100_4)
                   		res = 0;
                    else
                        if (totalDutyPeriod <= augLimit_1701_2100_4 + 120)
                       		res = 1;
                    	else
                            if (totalDutyPeriod <= augLimit_1701_2100_4 + 240)
                            	res = 2;
                            else
                            	res = 10;
                } else {
                    if (totalDutyPeriod <= augLimit_1701_2100_5)
                   		res = 0;
                    else
                        if (totalDutyPeriod <= augLimit_1701_2100_5 + 120)
                       		res = 1;
                    	else
                            if (totalDutyPeriod <= augLimit_1701_2100_5 + 240)
                            	res = 2;
                            else
                            	res = 10;
                }
            } else {
                if (numOfLegs < 5) {
                    if (totalDutyPeriod <= augLimit_2101_0459_4)
                   		res = 0;
                    else
                        if (totalDutyPeriod <= augLimit_2101_0459_4 + 120)
                       		res = 1;
                    	else
                            if (totalDutyPeriod <= augLimit_2101_0459_4 + 240)
                            	res = 2;
                            else
                            	res = 10;
                } else {
                    if (totalDutyPeriod <= augLimit_2101_0459_5)
                   		res = 0;
                    else
                        if (totalDutyPeriod <= augLimit_2101_0459_5 + 120)
                       		res = 1;
                    	else
                            if (totalDutyPeriod <= augLimit_2101_0459_5 + 240)
                            	res = 2;
                            else
                            	res = 10;
                }
            }

//        	hour -= (ff.getDepAirport().getGmtDiff() - ff.getDepGmtDiff()) / 60.0;		//	Yaz kýþ farký. 

        	if (localHour >= 5 && localHour <= 14) {
                if (numOfLegs < 5) {

                	if (res == 0) {
                		if (totalDutyPeriod > maxDP_0500_1400_4_Buffered)
                			res = 7;
                	} else 
                    	if (res == 1) {
                    		if (totalDutyPeriod > maxDP_0500_1400_4_Buffered + 120)
                    			res = 8;
                    	} else
                    		if (res == 1) {
	                        	if (totalDutyPeriod > maxDP_0500_1400_4_Buffered + 240)
	                        		res = 9;
	                        } 

                } else {

                	if (res == 0) {
                		if (totalDutyPeriod > maxDP_0500_1400_5_Buffered)
                			res = 7;
                	} else 
                    	if (res == 1) {
                    		if (totalDutyPeriod > maxDP_0500_1400_5_Buffered + 120)
                    			res = 8;
                    	} else
                    		if (res == 1) {
	                        	if (totalDutyPeriod > maxDP_0500_1400_5_Buffered + 240)
	                        		res = 9;
	                        } 

                }
            } else if (localHour > 14 && localHour <= 17) {
                if (numOfLegs < 5) {

                	if (res == 0) {
                		if (totalDutyPeriod > maxDP_1401_1700_4_Buffered)
                			res = 7;
                	} else 
                    	if (res == 1) {
                    		if (totalDutyPeriod > maxDP_1401_1700_4_Buffered + 120)
                    			res = 8;
                    	} else
                    		if (res == 1) {
	                        	if (totalDutyPeriod > maxDP_1401_1700_4_Buffered + 240)
	                        		res = 9;
	                        } 

                } else {

                	if (res == 0) {
                		if (totalDutyPeriod > maxDP_1401_1700_5_Buffered)
                			res = 7;
                	} else 
                    	if (res == 1) {
                    		if (totalDutyPeriod > maxDP_1401_1700_5_Buffered + 120)
                    			res = 8;
                    	} else
                    		if (res == 1) {
	                        	if (totalDutyPeriod > maxDP_1401_1700_5_Buffered + 240)
	                        		res = 9;
	                        } 

                }
            } else if ((localHour > 17) && (localHour < 21)) {
                if (numOfLegs < 5) {

                	if (res == 0) {
                		if (totalDutyPeriod > maxDP_1701_2100_4_Buffered)
                			res = 7;
                	} else 
                    	if (res == 1) {
                    		if (totalDutyPeriod > maxDP_1701_2100_4_Buffered + 120)
                    			res = 8;
                    	} else
                    		if (res == 1) {
	                        	if (totalDutyPeriod > maxDP_1701_2100_4_Buffered + 240)
	                        		res = 9;
	                        } 

                } else {

                	if (res == 0) {
                		if (totalDutyPeriod > maxDP_1701_2100_5_Buffered)
                			res = 7;
                	} else 
                    	if (res == 1) {
                    		if (totalDutyPeriod > maxDP_1701_2100_5_Buffered + 120)
                    			res = 8;
                    	} else
                    		if (res == 1) {
	                        	if (totalDutyPeriod > maxDP_1701_2100_5_Buffered + 240)
	                        		res = 9;
	                        } 

                }
            } else {
                if (numOfLegs < 5) {

                	if (res == 0) {
                		if (totalDutyPeriod > maxDP_2101_0459_4_Buffered)
                			res = 7;
                	} else 
                    	if (res == 1) {
                    		if (totalDutyPeriod > maxDP_2101_0459_4_Buffered + 120)
                    			res = 8;
                    	} else
                    		if (res == 1) {
	                        	if (totalDutyPeriod > maxDP_2101_0459_4_Buffered + 240)
	                        		res = 9;
	                        } 

                } else {

                	if (res == 0) {
                		if (totalDutyPeriod > maxDP_2101_0459_5_Buffered)
                			res = 7;
                	} else 
                    	if (res == 1) {
                    		if (totalDutyPeriod > maxDP_2101_0459_5_Buffered + 120)
                    			res = 8;
                    	} else
                    		if (res == 1) {
	                        	if (totalDutyPeriod > maxDP_2101_0459_5_Buffered + 240)
	                        		res = 9;
	                        } 

                }
            }
        }

        return res;
    }


	/*
	 * TODO
	 * Decides whether a duty is Early.
	 * Current implementation does not consider LOCAL time!!!
	 */
	private int earlyDutyPeriodStartLCL = 21;
	private int earlyDutyPeriodEndLCL = 3;

	private boolean isDutyEarly(LocalDateTime briefingTime) {
		double localHour = briefingTime.getHour() + briefingTime.getMinute() / 60.0; 

		if (earlyDutyPeriodStartLCL > earlyDutyPeriodEndLCL) {
			if ((localHour >= earlyDutyPeriodStartLCL)
					|| (localHour <= earlyDutyPeriodEndLCL))
					return true;
		} else
			if ((localHour >= earlyDutyPeriodStartLCL) && (localHour <= earlyDutyPeriodEndLCL))
				return true;

		return false;
	}

	/*
	 * Decides whether a duty is hard.
	 */
	private int hardDutyPeriodStartUTC = 22;
	private int hardDutyPeriodEndUTC = 2;

	private boolean isDutyHard(Duty d, LocalDateTime briefDayBeginning, LocalDateTime briefTime, LocalDateTime debriefTime) {
		LocalDateTime hardDutyStartTimeUTC = briefDayBeginning.plusHours(hardDutyPeriodStartUTC);
		LocalDateTime hardDutyEndTimeUTC = hardDutyStartTimeUTC.plusHours(hardDutyPeriodEndUTC - hardDutyPeriodStartUTC + (hardDutyPeriodEndUTC < hardDutyPeriodStartUTC ? 24 : 0));

		if ((!briefTime.isAfter(hardDutyStartTimeUTC))
				&& (!debriefTime.isBefore(hardDutyEndTimeUTC)))
			return true;

		return false;
	}

	@Override
	public void appendFw(Duty d, LegView l) {
		d.append(l);
		this.softAppend(d, l);
	}

	@Override
	public void softAppendFw(Duty d, LegView l) {
		d.incNumOfLegs(1);

		LegView connLeg = d.getSecondToLastLeg();

		this.incTotalizers(d, l, connLeg, 1);
		this.setStateVariables(d);

		/*
		 * Max blocktime
		 */
		if (d.getLongestBlockTimeInMins() < l.getBlockTimeInMins())
        	d.setLongestBlockTimeInMins(l.getBlockTimeInMins());
		d.getLongestBlockTimesInMins()[d.getNumOfLegs()] = l.getBlockTimeInMins();
	}

	@Override
	public LegView removeLast(Duty d) {
		LegView l = d.removeLast();
		if (l == null)
			return null;

		this.removeLast(d, l);

       	return l;
	}

	private void removeLast(Duty d, LegView l) {
		d.incNumOfLegs(-1);

		LegView connLeg = d.getLastLeg();

		if (connLeg == null)
			this.reset(d);
		else {
			this.incTotalizers(d, l, connLeg, -1);
			this.setStateVariables(d);
			/*
			 * Max blocktime
			 */
	       	d.setLongestBlockTimeInMins(d.getLongestBlockTimesInMins()[d.getNumOfLegs()]);
		}
	}

	private void incTotalizers(Duty d, LegView l, LegView connLeg,
								int incAmount) {
		/*
		 * Totalizers
		 */
		d.incBlockTimeInMins(incAmount * l.getBlockTimeInMins());
		if (l.isCover()) {
			d.incNumOfLegsActive(incAmount);
			d.incBlockTimeInMinsActive(incAmount * l.getBlockTimeInMins());
		} else {
			d.incNumOfLegsPassive(incAmount);
			d.incBlockTimeInMinsPassive(incAmount * l.getBlockTimeInMins());
		}

		if (l.getDepAirport().isDomestic() && l.getArrAirport().isInternational())
    		d.incNumOfLegsDomToInt(incAmount);
        else
        	if (l.getDepAirport().isInternational() && l.getArrAirport().isDomestic())
        		d.incNumOfLegsIntToDom(incAmount);

		if (l.getArrAirport().isCritical())
			d.incNumOfCriticalLegs(incAmount);
		if (l.getArrAirport().isAgDg() || l.getDepAirport().isAgDg())
			d.incNumOfAgDg(incAmount);
		if (l.isSpecialFlight())
			d.incNumOfSpecialFlights(incAmount);

		if (connLeg != null) {
			if (l.getDepAirport().isAnyHb())
				d.incNumOfAnyHomebaseTouch(incAmount);
			if (l.getDepAirport().isDomestic())
				d.incNumOfDomTouch(incAmount);
			if (l.getDepAirport().isInternational())
				d.incNumOfIntTouch(incAmount);

			if (connLeg.isCover() && l.isCover()
					&& ((connLeg.getAcSequence() != l.getAcSequence())
							|| (!connLeg.getAcType().equals(l.getAcType()))))
				d.incNumOfAcChanges(incAmount);

			int connTime = (int) ChronoUnit.MINUTES.between(connLeg.getSibt(), l.getSobt());
			if (connTime > 120) {
				d.incLongConnDiff(incAmount * (connTime - 120));
			}
		}
	}

	private void setStateVariables(Duty d) {
		/*
		 * States
		 */
		for (int i = 0; i < HeurosSystemParam.homebases.length; i++)
			d.setBriefDurationInMins(i, this.getBriefPeriod(d, i));
		for (int i = 0; i < HeurosSystemParam.homebases.length; i++)
			d.setDebriefDurationInMins(i, this.getDebriefPeriod(d, i));
		for (int i = 0; i < HeurosSystemParam.homebases.length; i++)
			d.setBriefTime(i, d.getFirstLeg().getSobt().minusMinutes(d.getBriefDurationInMins(i)));
		for (int i = 0; i < HeurosSystemParam.homebases.length; i++)
			d.setDebriefTime(i, d.getLastLeg().getSibt().plusMinutes(this.getDebriefPeriod(d, i)));
		for (int i = 0; i < HeurosSystemParam.homebases.length; i++)
			d.setBriefDayBeginning(i, d.getBriefTime(i).truncatedTo(ChronoUnit.DAYS));
		for (int i = 0; i < HeurosSystemParam.homebases.length; i++)
			d.setDebriefDayEnding(i, d.getDebriefTime(i).minusSeconds(1).truncatedTo(ChronoUnit.DAYS).plusHours(23).plusMinutes(59).plusSeconds(59));
		for (int i = 0; i < HeurosSystemParam.homebases.length; i++)
			d.setBriefDay(i, d.getBriefDayBeginning(i).toLocalDate());
		for (int i = 0; i < HeurosSystemParam.homebases.length; i++)
			d.setDebriefDay(i, d.getDebriefDayEnding(i).toLocalDate());
		for (int i = 0; i < HeurosSystemParam.homebases.length; i++)
			d.setDutyDurationInMins(i, (int) ChronoUnit.MINUTES.between(d.getBriefTime(i), d.getDebriefTime(i)));
		for (int i = 0; i < HeurosSystemParam.homebases.length; i++)
			d.setNumOfDaysTouched(i, (int) ChronoUnit.DAYS.between(d.getBriefDay(i), d.getDebriefDay(i)) + 1);

		d.setEr(this.isDutyAnER(d));
		d.setInternational(d.getLastLeg().getArrAirport().isInternational());

		for (int i = 0; i < HeurosSystemParam.homebases.length; i++)
			d.setRestDurationInMins(i, this.getDutyMinRestPeriod(d, d.getDutyDurationInMins(i), d.getLastArrAirport().getHbNdx() == i));
		for (int i = 0; i < HeurosSystemParam.homebases.length; i++)
			d.setNextBriefTime(i, d.getDebriefTime(i).plusMinutes(d.getRestDurationInMins(i)));
		for (int i = 0; i < HeurosSystemParam.homebases.length; i++)
			d.setAugmented(i, this.getDutyCrewModel(d, d.getDutyDurationInMins(i), d.getBriefTime(i)));

		for (int i = 0; i < HeurosSystemParam.homebases.length; i++)
			d.setEarly(i, this.isDutyEarly(d.getBriefTime(i)));
		for (int i = 0; i < HeurosSystemParam.homebases.length; i++)
			d.setHard(i, this.isDutyHard(d, d.getBriefDayBeginning(i), d.getBriefTime(i), d.getDebriefTime(i)));
	}

	@Override
	public void reset(Duty d) {

		d.setBlockTimeInMins(0);
		d.setBlockTimeInMinsActive(0);
		d.setBlockTimeInMinsPassive(0);

		d.setNumOfLegs(0);
		d.setNumOfLegsActive(0);
		d.setNumOfLegsPassive(0);
		d.setNumOfLegsIntToDom(0);
		d.setNumOfLegsDomToInt(0);

		d.setNumOfCriticalLegs(0);
		d.setNumOfAgDg(0);
		d.setNumOfSpecialFlights(0);

		d.setNumOfAnyHomebaseTouch(0);
		d.setNumOfDomTouch(0);
		d.setNumOfIntTouch(0);

		d.setNumOfAcChanges(0);

		d.setLongConnDiff(0);

		for (int i = 0; i < d.getLongestBlockTimesInMins().length; i++)
			d.getLongestBlockTimesInMins()[i] = 0;	
		d.setLongestBlockTimeInMins(0);

		d.setEr(false);
		d.setInternational(false);

		for (int i = 0; i < HeurosSystemParam.homebases.length; i++) {
			d.setBriefTime(i, null);
			d.setDebriefTime(i, null);
			d.setBriefDayBeginning(i, null);
			d.setDebriefDayEnding(i, null);
			d.setBriefDay(i, null);
			d.setDebriefDay(i, null);
			d.setBriefDurationInMins(i, 0);
			d.setDebriefDurationInMins(i, 0);
			d.setDutyDurationInMins(i, 0);
			d.setNumOfDaysTouched(i, 0);
			d.setRestDurationInMins(i, 0);
			d.setNextBriefTime(i, null);
			d.setAugmented(i, 0);
			d.setEarly(i, false);
			d.setHard(i, false);
			d.setValid(i, false);
		}
		d.setValidated(false);
	}

	@Override
	public boolean reCalculate(Duty d) {
		this.reset(d);
		this.softAppend(d, d.getLegs().get(0));
		for (int i = 1; i < d.getLegs().size(); i++)
			this.softAppend(d, d.getLegs().get(i));
		return true;
	}
}
