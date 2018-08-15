package org.heuros.hyperpair.rule;

import org.apache.commons.lang3.ArrayUtils;
import org.heuros.core.rule.inf.Introducer;
import org.heuros.data.model.Airport;

public class AirportIntroducer implements Introducer<Airport> {

	private static String[] domesticAirports = {"ADA", "ESB", "HTY", "AYT", "BJV", "YEI", "DLM", "DIY", "ERZ", "AOE"
												, "OGU", "GZT", "IST", "SAW", "ADB", "ASR", "KYA", "KZR", "MLX", "NAV"
												, "SZF", "GNY", "TZX", "ADF", "AJI", "MZH", "GZP", "BZI", "EDO", "BAL"
												, "BGG", "CKZ", "DNZ", "EZS", "ERC", "GKD", "YKO", "IGD", "ISE", "KCM"
												, "KSY", "KFS", "KCO", "MQM", "MSR", "SXZ", "NOP", "VAS", "NKT", "TEQ"
												, "TJK", "USQ", "VAN", "ONQ", "ANK", "BZI", "BDM", "DIY", "ESK", "IGL"
												, "ASR", "KCO", "KYA", "MLX", "MZH", "AFY"};

	private static String[] homebases = {"IST"};

	private static String[] noDhStats = {"JED"};

	@Override
	public boolean introduce(Airport m) {
		/*
		 * Check airport location.
		 */
//		m.setDomestic(false);
//		m.setInternational(true);
//		for (String airport: AirportIntroducer.domesticAirports) {
//			if (m.getCode().toUpperCase().equals(airport.toUpperCase())) {
//				m.setDomestic(true);
//				m.setInternational(false);
//				break;
//			}
//		}
		m.setDomestic(ArrayUtils.indexOf(AirportIntroducer.domesticAirports, m.getCode()) >= 0);
		m.setInternational(!m.isDomestic());

		/*
		 * Check homebase.
		 */
//		m.setHb(false);
//		m.setNonHB(true);
//		for (String airport: AirportIntroducer.homebases) {
//			if (m.getCode().toUpperCase().equals(airport.toUpperCase())) {
//				m.setHb(true);
//				m.setNonHB(false);
//				break;
//			}
//		}
		m.setHb(ArrayUtils.indexOf(AirportIntroducer.homebases, m.getCode()) >= 0);
		m.setNonHB(!m.isHb());

		/*
		 * Check no deadhead stations.
		 */
		m.setDhNotAllowedIfHBDepOrArr(ArrayUtils.indexOf(AirportIntroducer.noDhStats, m.getCode()) >= 0);		
		return true;
	}

}
