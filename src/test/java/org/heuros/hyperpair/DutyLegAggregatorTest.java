package org.heuros.hyperpair;

import java.time.LocalDateTime;
import java.time.Month;

import org.heuros.data.model.Airport;
import org.heuros.hyperpair.intro.AirportIntroducer;
import org.heuros.hyperpair.intro.DutyLegAggregator;
import org.heuros.hyperpair.intro.LegIntroducer;
import org.heuros.rule.AirportRuleContext;
import org.heuros.rule.DutyRuleContext;
import org.heuros.rule.LegRuleContext;
import org.heuros.util.test.DailyLegsTest;
import org.heuros.util.test.HeurosAirportTestUtil;
import org.heuros.util.test.HeurosDutyTestUtil;
import org.heuros.util.test.HeurosLegTestUtil;

import junit.framework.TestCase;

/**
 * Airport Introducer test.
 */
public abstract class DutyLegAggregatorTest extends TestCase {

	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DutyLegAggregatorTest( String testName )
    {
        super( testName );
    }

    public abstract void runTestProcedure();

	protected Airport apIST = null;
	protected Airport apSAW = null;
	protected Airport apAYT = null;
	protected Airport apVAN = null;
	protected Airport apCDG = null;
	protected Airport apBRE = null;
	protected Airport apJED = null;
	protected Airport apJFK = null;
	protected Airport apLHR = null;

	protected DailyLegsTest[] dailyLegs = null;

	protected AirportRuleContext airportRuleContext = null;
	protected LegRuleContext legRuleContext = null;
	protected DutyRuleContext dutyRuleContext = null;

	protected int hbNdxIST = 0;
	protected int hbNdxSAW = 1;

	/**
     * Test Duty Leg append/remove.
     */
    public void testDutyLegAggregation()
    {
    	HeurosDatasetParam.optPeriodStartInc = LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0);

    	/*
    	 * Generate airport instances.
    	 */
    	airportRuleContext = HeurosAirportTestUtil.initializeAirportContext(new AirportIntroducer());
    	assertFalse(airportRuleContext == null);

    	apIST = HeurosAirportTestUtil.generateAirportInstance("IST");
    	apSAW = HeurosAirportTestUtil.generateAirportInstance("SAW");
    	apAYT = HeurosAirportTestUtil.generateAirportInstance("AYT");
    	apVAN = HeurosAirportTestUtil.generateAirportInstance("VAN");
    	apCDG = HeurosAirportTestUtil.generateAirportInstance("CDG");
    	apBRE = HeurosAirportTestUtil.generateAirportInstance("BRE");
    	apJED = HeurosAirportTestUtil.generateAirportInstance("JED");
    	apJFK = HeurosAirportTestUtil.generateAirportInstance("JFK");
    	apLHR = HeurosAirportTestUtil.generateAirportInstance("LHR");

    	assertTrue(apIST.isAnyHb());
    	assertTrue(apSAW.isAnyHb());
    	assertTrue(apAYT.isAnyNonHb());
    	assertTrue(apVAN.isAnyNonHb());
    	assertTrue(apCDG.isAnyNonHb());
    	assertTrue(apBRE.isAnyNonHb());
    	assertTrue(apJED.isAnyNonHb());
    	assertTrue(apJFK.isAnyNonHb());
    	assertTrue(apLHR.isAnyNonHb());

    	assertTrue(apLHR.isSpecialEuroStation());

    	/*
    	 * Generate leg instances for the test.
    	 */

    	legRuleContext = HeurosLegTestUtil.initializeLegContext(new LegIntroducer(), HeurosSystemParam.homebases.length);
    	assertFalse(legRuleContext == null);

    	dailyLegs = HeurosLegTestUtil.getTestLegs(5, apIST, apSAW, apAYT, apVAN, apCDG, apBRE, apJED, apJFK);

		/*
		 * Generate Duty context.
		 */
    	dutyRuleContext = HeurosDutyTestUtil.initializeDutyContext(new DutyLegAggregator(), HeurosSystemParam.homebases.length);
    	assertFalse(dutyRuleContext == null);

		this.runTestProcedure();
    }
}
