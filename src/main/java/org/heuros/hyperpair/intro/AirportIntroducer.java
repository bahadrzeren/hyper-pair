package org.heuros.hyperpair.intro;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.heuros.core.rule.inf.AbstractRule;
import org.heuros.core.rule.inf.Introducer;
import org.heuros.core.rule.inf.RuleImplementation;
import org.heuros.data.model.Airport;

@RuleImplementation(ruleName = "Airport introducer", 
					violationMessage = "Airport introducer failed", 
					description = "Airport model initializer.")
public class AirportIntroducer extends AbstractRule implements Introducer<Airport> {

	/*
	 * Homebase airports. For now the problem is a single based one.
	 */
	private static String[] homebases = {"IST", "SAW"};

	/*
	 * Domestic airports. All of the others are going to be flagged as international.
	 */
	private static String[] domesticAirports = {"ADA", "ESB", "HTY", "AYT", "BJV", "YEI", "DLM", "DIY", "ERZ", "AOE"
												, "OGU", "GZT", "IST", "SAW", "ADB", "ASR", "KYA", "KZR", "MLX", "NAV"
												, "SZF", "GNY", "TZX", "ADF", "AJI", "MZH", "GZP", "BZI", "EDO", "BAL"
												, "BGG", "CKZ", "DNZ", "EZS", "ERC", "GKD", "YKO", "IGD", "ISE", "KCM"
												, "KSY", "KFS", "KCO", "MQM", "MSR", "SXZ", "NOP", "VAS", "NKT", "TEQ"
												, "TJK", "USQ", "VAN", "ONQ", "ANK", "BZI", "BDM", "DIY", "ESK", "IGL"
												, "ASR", "KCO", "KYA", "MLX", "MZH", "AFY"};

	/*
	 * Stations whose flights are not allowed to be deadhead if it departs from or arrives to the homebase.
	 */
	private static String[] noDhHbDepOrArrStats = {"JED"};

	/*
	 * Critical stations that requires additional CAT certificates for pilots.
	 */
	private static String[] criticalStations = {"BUS", "CKZ", "EDO", "ERC", "KAD", "KAN", "KBL", "KCM", "KLU", "LCJ", "MGQ"
												,/* "MRA", */"NOP", "OMO", "OSB", "PSR", "SIC", "SJJ", "SXZ", "TBZ", "ULN", "UYL"
												, "VAN", "ZAH"};

	/*
	 * Stations whose flighs must not link to other international flights.
	 */
	private static String[] agdgStations = {"ECN"};

	/*
	 * Stations whose flights must be in single day pairings.
	 */
	private static String[] oneDutyStations = { "AJI" };

	/*
	 * Aircraft change allowed international stations.
	 */
	private static String[] acChangeAllowedInternationaStations = {"AMM", "BEY", "BSR", "CAI", "DAM" , "DOH", "TLV", "KWI", "TBS", "TIF"
															, "SAH", "GYD", "HBE", "IFN", "IKA", "ISU", "KSH", "NJF", "SJJ", "SSH"
															//	, "MED"
															};

	/*
	 * Special european stations.
	 */
	private static String[] specialEuropeanStations = {"BRU", "CDG", "DUS", "FRA", "LHR", "MUC", "VIE", "ZRH"};

	/*
	 * Overnighting allowed stations.
	 */
	private static String[] layoverAllowedStations = {// Domestic
														"ADA", "ADB", "AOE", "ASR", "AYT", "BJV", "DIY", "DLM", "DNZ", "ERZ",
														"ESB", "EZS", "GNY", "GZT", "HTY", "ISE", "KYA", "MLX", "MQM", "MSR",
														"MZH", "SZF", "TZX", "VAN", "VAS", "YEI", //, "ECN", "NAV",
														// International
														"ADE", "ALA", "ALG", "AMM", "AMS", "BAH", "BCN", "BER", "BEY", "BRU",
														"CDG", "CMN", "CPH", "DEL", "DUS", "FRA", "FRU", "GVA", "GYD", "HAM",
														"JIB", "LHR", "MCT", "MUC", "MXP", "NBO", "RUH", "SJJ", "STR", "TAS",
														"TSE", "TXL", "VIE", "ZRH", "DOH", //, "LIS"
														"FCO", "LOS", "FIH", "DLA", "EBB", "DKR", "ACC", "OUA", "MBA", "DAR"
											//			, "KRT", "LBV", "NSI", "ULN", "YNB"
														};

	/*
	 * Stations where overnighting is mandatory.
	 */
	private static String[] endDutyIfTouches = {"ALA", "BKK", "DEL", "FRU", "KHI", "SHA", "TAS", "TSE", "NBO"};

	/*
	 * Stations that must be first layover in pairings. Therefore duties that touches that station must depart from homebase.
	 */
	private static String[] mandatoryFirstLayoverStations = {"LHR"};

	/*
	 * Stations that leg connection rule is not directly applied.
	 */
	private static String[] maxConnectionExceptionStations = {"TLV"
																//	"AMM", "ASR", "ATH", "BJV", "CKZ", "DIY", "DLM", "DNZ", "ECN", "ERZ",
																//	"GZT", "HTY", "ISE", "KYA", "MLX", "MXP", "MZH", "NAV", "ROV", "SZF",
																//	"TZX", "USQ", "VAS"
																};

	/*
	 * Pair grouping.
	 */
	private Map<String, Integer> groupIds = new HashMap<String, Integer>();

	public AirportIntroducer() {
		groupIds.put("ORY", 1);
		groupIds.put("CDG", 1);
		groupIds.put("ROV", 2);
		groupIds.put("SVO", 2);
		groupIds.put("TLV", 2);
		groupIds.put("AMM", 2);
		groupIds.put("ATH", 2);
		groupIds.put("BEY", 2);
		groupIds.put("PRN", 2);
		groupIds.put("SOF", 2);
		groupIds.put("FRA", 3);
		groupIds.put("DUS", 3);
		groupIds.put("STR", 3);
		groupIds.put("MUC", 3);
		groupIds.put("VIE", 3);
		groupIds.put("BCN", 3);
		groupIds.put("ZRH", 3);
		groupIds.put("TXL", 3);
		groupIds.put("BER", 3);
		groupIds.put("HAM", 3);
		groupIds.put("FCO", 3);
		groupIds.put("LIS", 3);
		groupIds.put("LHR", 4);
		groupIds.put("MXP", 5);
		groupIds.put("BRU", 5);
		groupIds.put("AMS", 5);
		groupIds.put("CPH", 5);
		groupIds.put("GVA", 5);
		groupIds.put("ADD", 6);
		groupIds.put("GYD", 6);
		groupIds.put("JED", 7);
		groupIds.put("SJJ", 7);
		groupIds.put("RUH", 7);
		groupIds.put("MCT", 7);
		groupIds.put("BAH", 7);
		groupIds.put("ADE", 7);
		groupIds.put("KRT", 7);
		groupIds.put("ALA", 8);
		groupIds.put("FRU", 8);
		groupIds.put("TSE", 8);
		groupIds.put("TAS", 8);
		groupIds.put("ULN", 8);
		groupIds.put("DXB", 9);
		groupIds.put("CMN", 9);
		groupIds.put("ALG", 9);
		groupIds.put("JIB", 8);
	}


	@Override
	public boolean introduce(Airport m) {

		/*
		 * Set homebase flags.
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
		m.setNonHb(!m.isHb());

		/*
		 * Set flags that will indicate whether airport is domestic or international.
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
		 * Check stations that are not allowed to be deadhead if it departs from or arrives to the homebase.
		 */
		m.setDhNotAllowedIfHBDepOrArr(ArrayUtils.indexOf(AirportIntroducer.noDhHbDepOrArrStats, m.getCode()) >= 0);		

		/*
		 * Check critical stations.
		 */
		m.setCritical(ArrayUtils.indexOf(AirportIntroducer.criticalStations, m.getCode()) >= 0);

		/*
		 * Check agdg stations which must not be connectted to other international flights.
		 */
		m.setAgDg(ArrayUtils.indexOf(AirportIntroducer.agdgStations, m.getCode()) >= 0);

		/*
		 * Check one duty stations.
		 */
		m.setOneDutyStation(ArrayUtils.indexOf(AirportIntroducer.oneDutyStations, m.getCode()) >= 0);

		/*
		 * Check ac change allowed international stations.
		 */
		m.setAcChangeAllowed(m.isDomestic());
		if (m.isInternational())
			m.setAcChangeAllowed(ArrayUtils.indexOf(AirportIntroducer.acChangeAllowedInternationaStations, m.getCode()) >= 0);

		/*
		 * Check special european stations.
		 */
		m.setSpecialEuroStation(ArrayUtils.indexOf(AirportIntroducer.specialEuropeanStations, m.getCode()) >= 0);

		/*
		 * Check layover allowed stations.
		 */
		m.setLayoverAllowed(ArrayUtils.indexOf(AirportIntroducer.layoverAllowedStations, m.getCode()) >= 0);

		/*
		 * Check stations where overnighting is mandatory.
		 */
		m.setEndDutyIfTouches(ArrayUtils.indexOf(AirportIntroducer.endDutyIfTouches, m.getCode()) >= 0);

		/*
		 * Check mandatory first layover stations.
		 */
		m.setMandatoryFirstLayover(ArrayUtils.indexOf(AirportIntroducer.mandatoryFirstLayoverStations, m.getCode()) >= 0);
	
		/*
		 * Check leg connection rule exception stations.
		 */
		m.setLegConnectionExceptionStation(ArrayUtils.indexOf(AirportIntroducer.maxConnectionExceptionStations, m.getCode()) >= 0);

		/*
		 * Set pairing group id for the station. 
		 */
		m.setGroupId(groupIds.get(m.getCode()) == null ? 0 : groupIds.get(m.getCode()));

		return true;
	}

}
