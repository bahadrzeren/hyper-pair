package org.heuros.hyperpair;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Airport Introducer test.
 */
public class AirportIntroducerTest extends AbsTestBase {

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
     * Test Airport introducer.
     */
    public void runTestProcedure()
    {
    	/*
    	 * Homebase
    	 */
    	int istHbNdx = 0;
//    	int sawHbNdx = 1;

    	assertTrue(apIST.isHb(istHbNdx));
//    	assertTrue(apSAW.isHb(sawHbNdx));
    	assertFalse(apIST.isNonHb(istHbNdx));
//    	assertFalse(apSAW.isNonHb(sawHbNdx));
    	assertTrue(apIST.isAnyHb());
    	assertTrue(apSAW.isAnyHb());
    	assertTrue(apESB.isNonHb(istHbNdx));
    	assertTrue(apAYT.isNonHb(istHbNdx));
    	assertTrue(apCDG.isNonHb(istHbNdx));
//    	assertTrue(apESB.isNonHb(sawHbNdx));
//    	assertTrue(apAYT.isNonHb(sawHbNdx));
//    	assertTrue(apCDG.isNonHb(sawHbNdx));
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
