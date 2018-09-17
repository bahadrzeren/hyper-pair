package org.heuros.hyperpair;

import org.heuros.data.model.Airport;
import org.heuros.hyperpair.intro.AirportIntroducer;
import org.heuros.util.test.AirportTestUtil;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Airport Introducer test.
 */
public class AirportIntroducerTest extends TestCase {

	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AirportIntroducerTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AirportIntroducerTest.class );
    }

    /**
     * Test Airport initialization.
     */
    public void testAirportInitialization()
    {
    	/*
    	 * Generate airport instances.
    	 */
    	assertTrue(AirportTestUtil.initializeAirportContext(new AirportIntroducer()) != null);

    	Airport apIST = AirportTestUtil.generateAirportInstance("IST");
    	Airport apSAW = AirportTestUtil.generateAirportInstance("SAW");
    	Airport apAYT = AirportTestUtil.generateAirportInstance("AYT");
    	Airport apEZS = AirportTestUtil.generateAirportInstance("EZS");
    	Airport apESB = AirportTestUtil.generateAirportInstance("ESB");
    	Airport apADA = AirportTestUtil.generateAirportInstance("ADA");
    	Airport apCDG = AirportTestUtil.generateAirportInstance("CDG");
    	Airport apBRE = AirportTestUtil.generateAirportInstance("BRE");
    	Airport apHAM = AirportTestUtil.generateAirportInstance("HAM");
    	Airport apJED = AirportTestUtil.generateAirportInstance("JED");
    	Airport apJFK = AirportTestUtil.generateAirportInstance("JFK");
    	Airport apLHR = AirportTestUtil.generateAirportInstance("LHR");
    	Airport apVAN = AirportTestUtil.generateAirportInstance("VAN");
    	Airport apBHX = AirportTestUtil.generateAirportInstance("BHX");
    	Airport apECN = AirportTestUtil.generateAirportInstance("ECN");
    	Airport apTLV = AirportTestUtil.generateAirportInstance("TLV");
    	Airport apAJI = AirportTestUtil.generateAirportInstance("AJI");
    	Airport apSSH = AirportTestUtil.generateAirportInstance("SSH");
    	Airport apBRU = AirportTestUtil.generateAirportInstance("BRU");
    	Airport apALA = AirportTestUtil.generateAirportInstance("ALA");
    	Airport apCAI = AirportTestUtil.generateAirportInstance("CAI");
    	Airport apORY = AirportTestUtil.generateAirportInstance("ORY");
    	Airport apHOU = AirportTestUtil.generateAirportInstance("HOU");

    	/*
    	 * Homebase
    	 */
    	int istHbNdx = 0;
    	int sawHbNdx = 1;

    	assertTrue(apIST.isHb(istHbNdx));
    	assertTrue(apSAW.isHb(sawHbNdx));
    	assertFalse(apIST.isNonHb(istHbNdx));
    	assertFalse(apSAW.isNonHb(sawHbNdx));
    	assertTrue(apIST.isAnyHb());
    	assertTrue(apSAW.isAnyHb());
    	assertTrue(apESB.isNonHb(istHbNdx));
    	assertTrue(apAYT.isNonHb(istHbNdx));
    	assertTrue(apCDG.isNonHb(istHbNdx));
    	assertTrue(apESB.isNonHb(sawHbNdx));
    	assertTrue(apAYT.isNonHb(sawHbNdx));
    	assertTrue(apCDG.isNonHb(sawHbNdx));
    	assertTrue(apESB.isAnyNonHb());
    	assertTrue(apAYT.isAnyNonHb());
    	assertTrue(apCDG.isAnyNonHb());
    	assertFalse(apESB.isAnyHb());
    	assertFalse(apAYT.isAnyHb());
    	assertFalse(apCDG.isAnyHb());

		/*
		 * Domestic/International
		 */

    	assertTrue(apESB.isDomestic());
		assertFalse(apESB.isInternational());
    	assertTrue(apEZS.isDomestic());
		assertFalse(apEZS.isInternational());
		assertTrue(apJFK.isInternational());
		assertFalse(apJFK.isDomestic());

		/*
		 * Stations that subject to special deadhead application.
		 */
    	assertTrue(apJED.isDhNotAllowedIfHBDepOrArr());
		assertFalse(apBRE.isDhNotAllowedIfHBDepOrArr());
		assertFalse(apJFK.isDhNotAllowedIfHBDepOrArr());
		assertFalse(apLHR.isDhNotAllowedIfHBDepOrArr());
		assertFalse(apIST.isDhNotAllowedIfHBDepOrArr());
		assertFalse(apSAW.isDhNotAllowedIfHBDepOrArr());
		assertFalse(apESB.isDhNotAllowedIfHBDepOrArr());
		assertFalse(apVAN.isDhNotAllowedIfHBDepOrArr());

		/*
		 * Critical stations.
		 */
    	assertTrue(apVAN.isCritical());
		assertFalse(apBHX.isCritical());
		assertFalse(apIST.isCritical());
		assertFalse(apSAW.isCritical());
		assertFalse(apESB.isCritical());

		/*
		 * AGDG stations.
		 */
    	assertTrue(apECN.isAgDg());
		assertFalse(apTLV.isAgDg());
		assertFalse(apESB.isAgDg());

		/*
		 * OneDuty stations.
		 */
    	assertTrue(apAJI.isOneDutyStation());
		assertFalse(apESB.isOneDutyStation());
		assertFalse(apAYT.isOneDutyStation());

		/*
		 * AC change allowed international stations.
		 */
    	assertTrue(apSSH.isAcChangeAllowed());
		assertTrue(apESB.isAcChangeAllowed());
		assertFalse(apLHR.isAcChangeAllowed());

		/*
		 * Special european stations.
		 */
    	assertTrue(apBRU.isSpecialEuroStation());
		assertFalse(apHAM.isSpecialEuroStation());

		/*
		 * Layover allowed stations.
		 */
    	assertTrue(apADA.isLayoverAllowed());
		assertFalse(apIST.isLayoverAllowed());
		assertFalse(apSAW.isLayoverAllowed());
		assertTrue(apESB.isLayoverAllowed());

		/*
		 * Mandatory overnighting stations.
		 */
    	assertTrue(apALA.isEndDutyIfTouches());
		assertFalse(apIST.isEndDutyIfTouches());
		assertFalse(apSAW.isEndDutyIfTouches());
		assertFalse(apESB.isEndDutyIfTouches());

		/*
		 * Stations that must be the first overnight in pairings.
		 */
    	assertTrue(apLHR.isMandatoryFirstLayover());
		assertFalse(apCDG.isMandatoryFirstLayover());

		/*
		 * Max connection exception stations.
		 */
    	assertTrue(apTLV.isLegConnectionExceptionStation());
		assertFalse(apCAI.isLegConnectionExceptionStation());

		/*
		 * Airport group id test.
		 */
    	assertTrue(apORY.getGroupId() == 1);
		assertTrue(apHOU.getGroupId() == 0);
    }
}
