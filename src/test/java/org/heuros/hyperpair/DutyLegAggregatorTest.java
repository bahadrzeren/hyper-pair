package org.heuros.hyperpair;

import java.time.LocalDateTime;
import java.time.Month;

import org.heuros.data.model.Airport;
import org.heuros.data.model.AirportFactory;
import org.heuros.data.model.DutyFactory;
import org.heuros.data.model.LegFactory;
import org.heuros.hyperpair.intro.AirportIntroducer;
import org.heuros.hyperpair.intro.DutyLegAggregator;
import org.heuros.hyperpair.intro.LegIntroducer;
import org.heuros.rule.AirportRuleContext;
import org.heuros.rule.DutyRuleContext;
import org.heuros.rule.LegRuleContext;
import org.heuros.util.test.AirportTestUtil;
import org.heuros.util.test.DutyTestUtil;
import org.heuros.util.test.LegTestUtil;

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
	protected Airport apESB = null;
	protected Airport apAYT = null;
	protected Airport apEZS = null;
	protected Airport apVAN = null;
	protected Airport apECN = null;
	protected Airport apCDG = null;
	protected Airport apBRU = null;
	protected Airport apFRA = null;
	protected Airport apBRE = null;
	protected Airport apLHR = null;
	protected Airport apJED = null;
	protected Airport apJFK = null;
	protected Airport apHOU = null;

//	protected DailyLegsTest[] dailyLegs = null;

	protected AirportRuleContext airportRuleContext = null;
	protected AirportFactory airportFactory = null;
	protected LegRuleContext legRuleContext = null;
	protected LegFactory legFactory = null;
	protected DutyRuleContext dutyRuleContext = null;
	protected DutyFactory dutyFactory = null;

	protected int hbNdxIST = 0;
	protected int hbNdxSAW = 1;

	/**
     * Test Duty Leg append/remove.
     */
    public void testDutyLegAggregation()
    {
    	HeurosDatasetParam.optPeriodStartInc = LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0);
    	HeurosSystemParam.briefPeriodBeforeDutyHb = 90;

    	/*
    	 * Initialize airport context and instances.
    	 */
    	airportRuleContext = AirportTestUtil.initializeAirportContext(new AirportIntroducer());
    	assertFalse(airportRuleContext == null);

    	apIST = AirportTestUtil.generateAirportInstance("IST");
    	apSAW = AirportTestUtil.generateAirportInstance("SAW");
    	apESB = AirportTestUtil.generateAirportInstance("ESB");
    	apAYT = AirportTestUtil.generateAirportInstance("AYT");
    	apEZS = AirportTestUtil.generateAirportInstance("EZS");
    	apVAN = AirportTestUtil.generateAirportInstance("VAN");
    	apECN = AirportTestUtil.generateAirportInstance("ECN");
    	apCDG = AirportTestUtil.generateAirportInstance("CDG");
    	apBRU = AirportTestUtil.generateAirportInstance("BRU");
    	apFRA = AirportTestUtil.generateAirportInstance("FRA");
    	apBRE = AirportTestUtil.generateAirportInstance("BRE");
    	apLHR = AirportTestUtil.generateAirportInstance("LHR");
    	apJED = AirportTestUtil.generateAirportInstance("JED");
    	apJFK = AirportTestUtil.generateAirportInstance("JFK");
    	apHOU = AirportTestUtil.generateAirportInstance("HOU");

    	assertTrue(apIST.isAnyHb());
    	assertTrue(apSAW.isAnyHb());
    	assertTrue(apESB.isAnyNonHb());
    	assertTrue(apAYT.isAnyNonHb());
    	assertTrue(apEZS.isAnyNonHb());
    	assertTrue(apVAN.isAnyNonHb());
    	assertTrue(apECN.isAnyNonHb());
    	assertTrue(apCDG.isAnyNonHb());
    	assertTrue(apBRU.isAnyNonHb());
    	assertTrue(apFRA.isAnyNonHb());
    	assertTrue(apBRE.isAnyNonHb());
    	assertTrue(apLHR.isAnyNonHb());
    	assertTrue(apJED.isAnyNonHb());
    	assertTrue(apJFK.isAnyNonHb());
    	assertTrue(apHOU.isAnyNonHb());

    	assertTrue(apECN.isAgDg());
    	assertTrue(apVAN.isCritical());
    	assertTrue(apLHR.isSpecialEuroStation());

    	/*
    	 * Initialize leg context.
    	 */

    	legRuleContext = LegTestUtil.initializeLegContext(new LegIntroducer(), HeurosSystemParam.homebases.length);
    	assertFalse(legRuleContext == null);

//    	dailyLegs = HeurosLegTestUtil.getTestLegs(5, apIST, apSAW, apAYT, apVAN, apCDG, apBRE, apJED, apJFK);

		/*
		 * Initialize Duty context.
		 */
    	dutyRuleContext = DutyTestUtil.initializeDutyContext(new DutyLegAggregator(), HeurosSystemParam.homebases.length);
    	assertFalse(dutyRuleContext == null);

		this.runTestProcedure();
    }
}
