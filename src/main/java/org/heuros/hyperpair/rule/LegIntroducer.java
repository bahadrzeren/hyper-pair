package org.heuros.hyperpair.rule;

import org.apache.commons.lang3.ArrayUtils;
import org.heuros.core.rule.inf.Introducer;
import org.heuros.data.model.Leg;

public class LegIntroducer implements Introducer<Leg> {

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
	 * Max flight number to be in the consider in the optimization.
	 */
	private static int maxFlightNumber = 3000;

	@Override
	public boolean introduce(Leg m) {
		/*
		 * Check aircraft numbers.
		 */
		m.setInPlanningArea(ArrayUtils.indexOf(LegIntroducer.acTypes, m.getAcType()) >= 0);

		/*
		 * Check special flight numbers.
		 */
		m.setSpecialFlight(ArrayUtils.indexOf(LegIntroducer.specialFlightNumbers, m.getFlightNo()) >= 0);

		/*
		 * Check flight number ceiling.
		 */
		if (m.getFlightNo() >= LegIntroducer.maxFlightNumber)
			return false;


		return true;
	}

}
