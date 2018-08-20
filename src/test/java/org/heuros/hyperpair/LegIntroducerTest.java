package org.heuros.hyperpair;

import java.time.LocalDateTime;
import java.time.Month;

import org.heuros.core.rule.inf.Rule;
import org.heuros.data.model.Airport;
import org.heuros.data.model.AirportFactory;
import org.heuros.data.model.Leg;
import org.heuros.data.model.LegFactory;
import org.heuros.hyperpair.intro.AirportIntroducer;
import org.heuros.hyperpair.intro.LegIntroducer;
import org.heuros.rule.AirportRuleContext;
import org.heuros.rule.LegRuleContext;

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
    	 * Generate airport instances for the leg introducer test.
    	 */
    	AirportFactory apFactory = new AirportFactory();
    	AirportRuleContext apRuleContext = new AirportRuleContext();

    	Rule apIntroducer = new AirportIntroducer();

    	try {
    		apRuleContext.registerRule(apIntroducer);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    		assertTrue(false);
    	}

    	Airport apIST = apFactory.generateModel();
    	Airport apADA = apFactory.generateModel();
    	Airport apJED = apFactory.generateModel();

    	apIST.setCode("IST");
    	apADA.setCode("ADA");
    	apJED.setCode("JED");

    	apRuleContext.getIntroducerProxy().introduce(apIST);
    	apRuleContext.getIntroducerProxy().introduce(apADA);
    	apRuleContext.getIntroducerProxy().introduce(apJED);

    	/*
    	 * Generate leg instances for the test.
    	 */

    	LegFactory legFactory = new LegFactory();
    	LegRuleContext legRuleContext = new LegRuleContext();

    	Rule legIntroducer = new LegIntroducer();

    	try {
    		legRuleContext.registerRule(legIntroducer);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    		assertTrue(false);
    	}

    	Leg legInFleetHb = legFactory.generateModel();
    	Leg legNonFleetNonHb = legFactory.generateModel();

    	legInFleetHb.setFlightNo(100);
    	legInFleetHb.setDepAirport(apIST);
    	legInFleetHb.setArrAirport(apADA);
    	legInFleetHb.setSobt(LocalDateTime.of(2014, Month.JANUARY, 1, 10, 0));
    	legInFleetHb.setSibt(LocalDateTime.of(2014, Month.JANUARY, 1, 11, 13));

    	legNonFleetNonHb.setFlightNo(200);
    	legNonFleetNonHb.setDepAirport(apADA);
    	legNonFleetNonHb.setArrAirport(apIST);
    	legNonFleetNonHb.setSobt(LocalDateTime.of(2014, Month.JANUARY, 1, 20, 0));
    	legNonFleetNonHb.setSibt(LocalDateTime.of(2014, Month.JANUARY, 2, 0, 1));

    	/*
    	 * Flight number not to consider.
    	 */
    	int flightNo = legInFleetHb.getFlightNo();
    	legInFleetHb.setFlightNo(9000);
		assertFalse(legRuleContext.getIntroducerProxy().introduce(legInFleetHb));
    	legInFleetHb.setFlightNo(flightNo);

    	/*
    	 * Block time.
    	 */
    	legRuleContext.getIntroducerProxy().introduce(legInFleetHb);
    	legRuleContext.getIntroducerProxy().introduce(legNonFleetNonHb);
    	assertTrue(legInFleetHb.getBlockTimeInMins() == 73);
    	assertTrue(legNonFleetNonHb.getBlockTimeInMins() == 241);

    	/*
    	 * Fleets.
    	 */
    	legInFleetHb.setAcType("32J");
    	legNonFleetNonHb.setAcType("737");

    	legRuleContext.getIntroducerProxy().introduce(legInFleetHb);
    	legRuleContext.getIntroducerProxy().introduce(legNonFleetNonHb);

    	assertTrue(legInFleetHb.isInFleet());
		assertFalse(legNonFleetNonHb.isInFleet());

    	/*
    	 * Special flights.
    	 */
    	legInFleetHb.setFlightNo(1942);

    	legRuleContext.getIntroducerProxy().introduce(legInFleetHb);

    	assertTrue(legInFleetHb.isSpecialFlight());
		assertFalse(legNonFleetNonHb.isSpecialFlight());

    	/*
    	 * No deadhead flight numbers.
    	 */
    	legInFleetHb.setFlightNo(1967);

    	legRuleContext.getIntroducerProxy().introduce(legInFleetHb);

    	assertFalse(legInFleetHb.isDeadheadable());
		assertTrue(legNonFleetNonHb.isDeadheadable());

    	/*
    	 * No deadhead flight number range.
    	 */
    	legInFleetHb.setFlightNo(3500);

    	legRuleContext.getIntroducerProxy().introduce(legInFleetHb);

    	assertFalse(legInFleetHb.isDeadheadable());
		assertTrue(legNonFleetNonHb.isDeadheadable());

    	/*
    	 * Cover flag.
    	 */
    	assertFalse(legInFleetHb.isCover());
		assertFalse(legNonFleetNonHb.isCover());

    	/*
    	 * No deadhead on special stations if HB dep or arr.
    	 */
		legNonFleetNonHb.setDepAirport(apJED);
		legRuleContext.getIntroducerProxy().introduce(legNonFleetNonHb);
		assertFalse(legNonFleetNonHb.isDeadheadable());
    }
}
