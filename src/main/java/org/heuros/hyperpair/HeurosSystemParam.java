package org.heuros.hyperpair;

public class HeurosSystemParam {
	/*
	 * Homebase airports. For now the problem is a single based one.
	 */
	public static String[] homebases = {"IST", "SAW"};


	public static int maxPairingLengthInDays = 4;
	public static int maxIntLay = 60 * 60;
	public static int maxDomLay = 30 * 60;
	public static int maxIdleTimeInAPairInHours = 10;

	public static int briefPeriodBeforeDutyHb = 60;
	public static int briefPeriodBeforeDutyNonHb = 60;
	public static int debriefPeriodAfterDuty = 30;

	/*
	 * Minimum connection time limits that are designated in rule implementations can not be smaller than this value.
	 */
	public static int minLegConnectionTimeInMins = 30;

	/*
	 * Maximum connection time limits that are designated in rule implementations can not exceed this value.
	 */
	public static int maxLegConnectionTimeInMins = 10 * 60;
}
