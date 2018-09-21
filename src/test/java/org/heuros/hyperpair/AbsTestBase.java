package org.heuros.hyperpair;

import java.time.LocalDateTime;
import java.time.Month;

import org.heuros.core.rule.intf.Rule;
import org.heuros.data.model.Airport;
import org.heuros.data.model.Duty;
import org.heuros.data.model.Leg;
import org.heuros.data.model.Pair;
import org.heuros.hyperpair.intro.AirportIntroducer;
import org.heuros.hyperpair.intro.DutyLegAggregator;
import org.heuros.hyperpair.intro.LegIntroducer;
import org.heuros.hyperpair.intro.PairDutyAggregator;
import org.heuros.rule.AirportRuleContext;
import org.heuros.rule.DutyRuleContext;
import org.heuros.rule.LegRuleContext;
import org.heuros.rule.PairRuleContext;

import junit.framework.TestCase;

/**
 * Airport Introducer test.
 */
public abstract class AbsTestBase extends TestCase {

	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AbsTestBase( String testName )
    {
        super( testName );
    }

    public abstract void runTestProcedure();

	protected AirportRuleContext airportRuleContext = null;
	protected LegRuleContext legRuleContext = null;
	protected DutyRuleContext dutyRuleContext = null;
	protected PairRuleContext pairRuleContext = null;

	protected Airport apIST = null;
	protected Airport apSAW = null;
	protected Airport apESB = null;
	protected Airport apADA = null;
	protected Airport apAYT = null;
	protected Airport apSSH = null;
	protected Airport apEZS = null;
	protected Airport apVAN = null;
	protected Airport apAJI = null;
	protected Airport apECN = null;
	protected Airport apCDG = null;
	protected Airport apBRU = null;
	protected Airport apFRA = null;
	protected Airport apBRE = null;
	protected Airport apHAM = null;
	protected Airport apLHR = null;
	protected Airport apBHX = null;
	protected Airport apTLV = null;
	protected Airport apJED = null;
	protected Airport apCAI = null;
	protected Airport apALA = null;
	protected Airport apJFK = null;
	protected Airport apORY = null;
	protected Airport apHOU = null;

	protected int hbNdxIST = 0;
	protected int hbNdxSAW = 1;

	/**
     * Prepare the framework for unit tests.
     */
    public void testInitializeTheFramework()
    {
    	HeurosDatasetParam.optPeriodStartInc = LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0);
    	HeurosSystemParam.briefPeriodBeforeDutyHb = 90;

    	/*
    	 * Initialize airport context and instances.
    	 */
    	this.airportRuleContext = this.initializeAirportContext(new AirportIntroducer());
    	assertFalse(this.airportRuleContext == null);

    	apIST = this.generateAirportInstance("IST");
    	apSAW = this.generateAirportInstance("SAW");
    	apESB = this.generateAirportInstance("ESB");
    	apADA = this.generateAirportInstance("ADA");
    	apAYT = this.generateAirportInstance("AYT");
    	apSSH = this.generateAirportInstance("SSH");
    	apEZS = this.generateAirportInstance("EZS");
    	apVAN = this.generateAirportInstance("VAN");
    	apAJI = this.generateAirportInstance("AJI");
    	apECN = this.generateAirportInstance("ECN");
    	apCDG = this.generateAirportInstance("CDG");
    	apBRU = this.generateAirportInstance("BRU");
    	apFRA = this.generateAirportInstance("FRA");
    	apBRE = this.generateAirportInstance("BRE");
    	apHAM = this.generateAirportInstance("HAM");
    	apLHR = this.generateAirportInstance("LHR");
    	apBHX = this.generateAirportInstance("BHX");
    	apTLV = this.generateAirportInstance("TLV");
    	apJED = this.generateAirportInstance("JED");
    	apCAI = this.generateAirportInstance("CAI");
    	apALA = this.generateAirportInstance("ALA");
    	apJFK = this.generateAirportInstance("JFK");
    	apORY = this.generateAirportInstance("ORY");
    	apHOU = this.generateAirportInstance("HOU");

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

    	this.legRuleContext = this.initializeLegContext(new LegIntroducer(), HeurosSystemParam.homebases.length);
    	assertFalse(this.legRuleContext == null);

		/*
		 * Initialize Duty context.
		 */
    	this.dutyRuleContext = this.initializeDutyContext(new DutyLegAggregator(), HeurosSystemParam.homebases.length);
    	assertFalse(this.dutyRuleContext == null);

		/*
		 * Initialize Pair context.
		 */
    	this.pairRuleContext = this.initializePairContext(new PairDutyAggregator(), HeurosSystemParam.homebases.length);
    	assertFalse(this.pairRuleContext == null);

    	this.runTestProcedure();
    }

	private AirportRuleContext initializeAirportContext(Rule apIntroducer) {
		this.airportRuleContext = new AirportRuleContext();
    	try {
    		this.airportRuleContext.registerRule(apIntroducer);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    		return null;
    	}
    	return this.airportRuleContext;
	}

	private LegRuleContext initializeLegContext(Rule legIntroducer, int numOfBases) {
		this.legRuleContext = new LegRuleContext(numOfBases);
    	try {
    		this.legRuleContext.registerRule(legIntroducer);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    		return null;
    	}
    	return this.legRuleContext;
	}

	private DutyRuleContext initializeDutyContext(Rule dutyLegAggregator, int numOfBases) {
		this.dutyRuleContext = new DutyRuleContext(numOfBases);
		try {
			this.dutyRuleContext.registerRule(dutyLegAggregator);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    		return null;
    	}
    	return this.dutyRuleContext;
	}

	private PairRuleContext initializePairContext(Rule pairDutyAggregator, int numOfBases) {
		this.pairRuleContext = new PairRuleContext(numOfBases);
		try {
			this.pairRuleContext.registerRule(pairDutyAggregator);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    		return null;
    	}
    	return this.pairRuleContext;
	}

	public Airport generateAirportInstance(String apCode) {
    	Airport apInstance = Airport.newInstance(apCode);
    	if (this.airportRuleContext.getIntroducerProxy().introduce(apInstance))
    		return apInstance;
    	return null;
	}

	public Leg generateLegInstance(int flightNo,
			Airport depAirport,
			Airport arrAirport,
			LocalDateTime sobt,
			LocalDateTime sibt,
			String acType) {
		Leg legInstance = Leg.newInstance();
		legInstance.setFlightNo(flightNo);
		legInstance.setDepAirport(depAirport);
		legInstance.setArrAirport(arrAirport);
		legInstance.setDep(depAirport.getCode());
		legInstance.setArr(arrAirport.getCode());
		legInstance.setSobt(sobt);
		legInstance.setSibt(sibt);
		legInstance.setAcType(acType);

		if (this.legRuleContext.getIntroducerProxy().introduce(legInstance))
			return legInstance;
		return null;
	}

	public Duty generateDutyInstance(int numOfBases) {
    	return Duty.newInstance(numOfBases);
	}

	public Duty generateDutyInstance(int numOfBases, Leg leg) {
    	Duty dutyInstance = Duty.newInstance(numOfBases);
   		this.dutyRuleContext.getAggregatorProxy().appendFw(dutyInstance, leg);
    	return dutyInstance;
	}

	public Duty generateDutyInstance(int numOfBases, Leg[] legs) {
		Duty dutyInstance = Duty.newInstance(numOfBases);
    	for (Leg leg: legs) {
    		this.dutyRuleContext.getAggregatorProxy().appendFw(dutyInstance, leg);
		}
    	return dutyInstance;
	}

	public Pair generatePairInstance(int hbNdx) {
    	return Pair.newInstance(hbNdx);
	}

	public Pair generatePairInstance(int hbNdx, Duty duty) {
		Pair pairInstance = Pair.newInstance(hbNdx);
   		this.pairRuleContext.getAggregatorProxy().appendFw(pairInstance, duty);
    	return pairInstance;
	}

	public Pair generatePairInstance(int hbNdx, Duty[] duties) {
		Pair pairInstance = Pair.newInstance(hbNdx);
    	for (Duty duty: duties) {
    		this.pairRuleContext.getAggregatorProxy().appendFw(pairInstance, duty);
		}
    	return pairInstance;
	}
}
