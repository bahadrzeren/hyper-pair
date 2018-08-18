package org.heuros.hyperpair.rule;

import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.ArrayUtils;
import org.heuros.core.rule.inf.AbstractRule;
import org.heuros.core.rule.inf.Introducer;
import org.heuros.core.rule.inf.RuleImplementation;
import org.heuros.data.model.Leg;
import org.heuros.hyperpair.HeurosDatasetParam;

@RuleImplementation(ruleName = "Leg introducer", 
					violationMessage = "Leg introducer failed", 
					description = "Leg model initializer.")
public class LegIntroducer extends AbstractRule implements Introducer<Leg> {

	/*
	 * Aircraft types to to consider during the optimization.
	 */
	private static String[] acTypes = {"315", "316", "319", "320", "321", "31D", "31E", "32C", 
										"32D", "32E", "32G", "32J", "32K", "32L", "32N", "32R"}; 

	/*
	 * Special flight numbers that must be he first leg in its duty and its duty must depart from homebase or an international layover station. 
	 */
	private static Integer[] specialFlightNumbers = {1942, 1710, 1852};

	/*
	 * No deadhead flight numbers.
	 */
	private static Integer[] noDeadheadFlightNumbers = {1967, 1968, 1969, 1970, 1993, 1994, 1995, 1996, 1343, 1344, 97, 107, 451, 407, 
														305, 1979, 1587, 1413, 1825, 168, 472, 426, 346, 1992, 1590, 1416, 1828, 845, 
														841, 844, 840, 843, 840, 840, 842, 843, 844, 845, 2822, 2839, 2837, 2836};

	/*
	 * No deadhead flight number ranges.
	 */
	private Integer dhRangeStart1 = 3000;
	private Integer dhRangeEnd1 = 3999;
	private Integer dhRangeStart2 = 6000;
	private Integer dhRangeEnd2 = 6799;
	private Integer dhRangeStart3 = 4000;
	private Integer dhRangeEnd3 = 5399;
	private Integer dhRangeStart4 = 7780;
	private Integer dhRangeEnd4 = 8999;

	/*
	 * Max flight number to be covered during the optimization.
	 */
	private static int maxFlightNumberToCover = 3000;

	/*
	 * Max flight number to be consider.
	 */
	private static int maxFlightNumberToConsider = 8999;

	@Override
	public boolean introduce(Leg m) {

		if (HeurosDatasetParam.optPeriodStartInc.isAfter(m.getSobt()))
			return false;

		if (m.getFlightNo() > LegIntroducer.maxFlightNumberToConsider)
			return false;

		/*
		 * Calculate block time.
		 */
		m.setBlockTimeInMins((int) ChronoUnit.MINUTES.between(m.getSobt(), m.getSibt()));

		/*
		 * Check aircraft numbers.
		 */
		m.setInFleet(ArrayUtils.indexOf(LegIntroducer.acTypes, m.getAcType()) >= 0);

		/*
		 * Check special flight numbers.
		 */
		m.setSpecialFlight(ArrayUtils.indexOf(LegIntroducer.specialFlightNumbers, m.getFlightNo()) >= 0);

		/*
		 * Deadhead info set.
		 */
		m.setDeadheadable(ArrayUtils.indexOf(LegIntroducer.noDeadheadFlightNumbers, m.getFlightNo()) < 0);
		if (m.isDeadheadable()) {
			if ((m.getFlightNo() >= dhRangeStart1) && (m.getFlightNo() <= dhRangeEnd1))
				m.setDeadheadable(false);
			if ((m.getFlightNo() >= dhRangeStart2) && (m.getFlightNo() <= dhRangeEnd2))
				m.setDeadheadable(false);
			if ((m.getFlightNo() >= dhRangeStart3) && (m.getFlightNo() <= dhRangeEnd3))
				m.setDeadheadable(false);
			if ((m.getFlightNo() >= dhRangeStart4) && (m.getFlightNo() <= dhRangeEnd4))
				m.setDeadheadable(false);
		}
		if (m.isDeadheadable()) {
			if (m.getDepAirport().isHb()
		               && m.getArrAirport().isDhNotAllowedIfHBDepOrArr()) {
					m.setDeadheadable(false);
			} else
				if (m.getArrAirport().isHb()
		               && m.getDepAirport().isDhNotAllowedIfHBDepOrArr()) {
					m.setDeadheadable(false);
				}
		}

		/*
		 * Check max flight number to cover.
		 */
		m.setCover(m.isInFleet() && m.isNeedsCockpitCrew());

		if (m.getFlightNo() >= LegIntroducer.maxFlightNumberToCover)
			m.setCover(false);

		if (m.isCover() || m.isDeadheadable())
			return true;
		else
			return false;
	}
}
