package org.heuros.hyperpair;

import java.time.LocalDateTime;
import java.time.Month;

import org.heuros.data.model.Airport;
import org.heuros.data.model.Leg;
import org.heuros.hyperpair.intro.AirportIntroducer;
import org.heuros.hyperpair.intro.LegIntroducer;
import org.heuros.util.test.HeurosAirportTestUtil;
import org.heuros.util.test.HeurosLegTestUtil;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Airport Introducer test.
 */
public class LegIntroducerTest extends TestCase {

	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public LegIntroducerTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( LegIntroducerTest.class );
    }

    /**
     * Test Leg initialization.
     */
    public void testLegInitialization()
    {
    	HeurosDatasetParam.optPeriodStartInc = LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0);

    	/*
    	 * Generate airport instances.
    	 */
    	assertTrue(HeurosAirportTestUtil.initializeAirportContext(new AirportIntroducer()));

    	/*
    	 * Generate leg instances for the test.
    	 */

    	/*
    	 * Generate leg instances for the test.
    	 */

    	assertTrue(HeurosLegTestUtil.initializeLegContext(new LegIntroducer(), HeurosSystemParam.homebases.length));

    	Airport apIST = HeurosAirportTestUtil.generateAirportInstance("IST");
    	Airport apSAW = HeurosAirportTestUtil.generateAirportInstance("SAW");
    	Airport apADA = HeurosAirportTestUtil.generateAirportInstance("ADA");
    	Airport apEZS = HeurosAirportTestUtil.generateAirportInstance("EZS");

    	Leg legIstDep = HeurosLegTestUtil.generateLegInstance(100, apIST, apADA, LocalDateTime.of(2014, Month.JANUARY, 1, 10, 0), LocalDateTime.of(2014, Month.JANUARY, 1, 11, 13), "32J");
    	Leg legSawDep = HeurosLegTestUtil.generateLegInstance(101, apSAW, apEZS, LocalDateTime.of(2014, Month.JANUARY, 1, 20, 0), LocalDateTime.of(2014, Month.JANUARY, 2, 0, 1), "320");
    	Leg legIstDepDh = HeurosLegTestUtil.generateLegInstance(102, apIST, apADA, LocalDateTime.of(2014, Month.JANUARY, 1, 10, 0), LocalDateTime.of(2014, Month.JANUARY, 1, 11, 13), "737");
    	Leg legSawDepDh = HeurosLegTestUtil.generateLegInstance(103, apSAW, apEZS, LocalDateTime.of(2014, Month.JANUARY, 1, 20, 0), LocalDateTime.of(2014, Month.JANUARY, 2, 0, 1), "737");

    	/*
    	 * Flight number not to consider.
    	 */
    	Airport apESB = HeurosAirportTestUtil.generateAirportInstance("ESB");

    	Leg leg9000 = HeurosLegTestUtil.generateLegInstance(9000, apIST, apESB, LocalDateTime.of(2014, Month.JANUARY, 1, 8, 0), LocalDateTime.of(2014, Month.JANUARY, 1, 9, 0), "320");

    	assertTrue(leg9000 == null);

		/*
		 * Deadhead state. (Cover Flag).
		 */
    	assertTrue(legIstDep.isCover());
    	assertTrue(legSawDep.isCover());
    	assertFalse(legIstDepDh.isCover());
    	assertFalse(legSawDepDh.isCover());

		/*
    	 * Block time.
    	 */
    	assertTrue(legIstDep.getBlockTimeInMins() == 73);
    	assertTrue(legIstDepDh.getBlockTimeInMins() == 73);
    	assertTrue(legSawDep.getBlockTimeInMins() == 241);
    	assertTrue(legSawDepDh.getBlockTimeInMins() == 241);

    	/*
    	 * Fleets.
    	 */
    	assertTrue(legIstDep.isInFleet());
		assertTrue(legSawDep.isInFleet());
    	assertFalse(legIstDepDh.isInFleet());
		assertFalse(legSawDepDh.isInFleet());

    	/*
    	 * Special flights.
    	 */
    	Airport apLHR = HeurosAirportTestUtil.generateAirportInstance("LHR");

		Leg legSpecFlight = HeurosLegTestUtil.generateLegInstance(1942, apIST, apLHR, LocalDateTime.of(2014, Month.JANUARY, 1, 8, 0), LocalDateTime.of(2014, Month.JANUARY, 1, 12, 0), "320");

    	assertTrue(legSpecFlight.isSpecialFlight());
		assertFalse(legIstDep.isSpecialFlight());
		assertFalse(legSawDep.isSpecialFlight());
		assertFalse(legIstDepDh.isSpecialFlight());
		assertFalse(legSawDepDh.isSpecialFlight());

    	/*
    	 * No deadhead flight numbers.
    	 */
    	Airport apCDG = HeurosAirportTestUtil.generateAirportInstance("CDG");

    	Leg legNoDhFlightNo = HeurosLegTestUtil.generateLegInstance(1967, apIST, apCDG, LocalDateTime.of(2014, Month.JANUARY, 1, 8, 0), LocalDateTime.of(2014, Month.JANUARY, 1, 10, 0), "320");

    	assertFalse(legNoDhFlightNo.isDeadheadable());
		assertTrue(legIstDep.isDeadheadable());
		assertTrue(legSawDep.isDeadheadable());
    	assertTrue(legIstDepDh.isDeadheadable());
		assertTrue(legSawDepDh.isDeadheadable());

    	/*
    	 * No deadhead flight number range.
    	 */
    	Airport apBRU = HeurosAirportTestUtil.generateAirportInstance("BRU");

    	Leg legNoDhFlightRange = HeurosLegTestUtil.generateLegInstance(3500, apIST, apBRU, LocalDateTime.of(2014, Month.JANUARY, 1, 8, 0), LocalDateTime.of(2014, Month.JANUARY, 1, 13, 0), "320");

    	assertTrue((legNoDhFlightRange == null) || (!legNoDhFlightRange.isDeadheadable()));

    	/*
    	 * No deadhead on special stations if HB dep or arr.
    	 */
    	Airport apJED = HeurosAirportTestUtil.generateAirportInstance("JED");

    	Leg legNoDhStation = HeurosLegTestUtil.generateLegInstance(300, apIST, apJED, LocalDateTime.of(2014, Month.JANUARY, 1, 8, 0), LocalDateTime.of(2014, Month.JANUARY, 1, 13, 0), "320");

		assertFalse(legNoDhStation.isDeadheadable());
    }
}
