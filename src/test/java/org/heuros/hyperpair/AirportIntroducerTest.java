package org.heuros.hyperpair;

import org.heuros.core.rule.intf.Rule;
import org.heuros.data.model.Airport;
import org.heuros.data.model.AirportFactory;
import org.heuros.hyperpair.intro.AirportIntroducer;
import org.heuros.rule.AirportRuleContext;

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
    	AirportFactory apFactory = new AirportFactory();
    	AirportRuleContext apRuleContext = new AirportRuleContext();

    	Rule apIntroducer = new AirportIntroducer();

    	try {
    		apRuleContext.registerRule(apIntroducer);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    		assertTrue(false);
    	}

    	Airport ap1 = apFactory.generateModel();
    	Airport ap2 = apFactory.generateModel();

    	/*
    	 * Homebase
    	 */
    	ap1.setCode("SAW");
    	ap2.setCode("ESB");

    	apRuleContext.getIntroducerProxy().introduce(ap1);
    	apRuleContext.getIntroducerProxy().introduce(ap2);

    	assertTrue(ap1.isHb());
		assertFalse(ap1.isNonHb());
		assertTrue(ap2.isNonHb());
		assertFalse(ap2.isHb());

		/*
		 * Domestic/International
		 */
    	ap1.setCode("EZS");
    	ap2.setCode("JFK");

    	apRuleContext.getIntroducerProxy().introduce(ap1);
    	apRuleContext.getIntroducerProxy().introduce(ap2);

    	assertTrue(ap1.isDomestic());
		assertFalse(ap1.isInternational());
		assertTrue(ap2.isInternational());
		assertFalse(ap2.isDomestic());

		/*
		 * Stations that subject to special deadhead application.
		 */
    	ap1.setCode("JED");
    	ap2.setCode("BRE");

    	apRuleContext.getIntroducerProxy().introduce(ap1);
    	apRuleContext.getIntroducerProxy().introduce(ap2);

    	assertTrue(ap1.isDhNotAllowedIfHBDepOrArr());
		assertFalse(ap2.isDhNotAllowedIfHBDepOrArr());

		/*
		 * Critical stations.
		 */
    	ap1.setCode("VAN");
    	ap2.setCode("BHX");

    	apRuleContext.getIntroducerProxy().introduce(ap1);
    	apRuleContext.getIntroducerProxy().introduce(ap2);

    	assertTrue(ap1.isCritical());
		assertFalse(ap2.isCritical());

		/*
		 * AGDG stations.
		 */
    	ap1.setCode("ECN");
    	ap2.setCode("TLV");

    	apRuleContext.getIntroducerProxy().introduce(ap1);
    	apRuleContext.getIntroducerProxy().introduce(ap2);

    	assertTrue(ap1.isAgDg());
		assertFalse(ap2.isAgDg());

		/*
		 * OneDuty stations.
		 */
    	ap1.setCode("AJI");
    	ap2.setCode("AYT");

    	apRuleContext.getIntroducerProxy().introduce(ap1);
    	apRuleContext.getIntroducerProxy().introduce(ap2);

    	assertTrue(ap1.isOneDutyStation());
		assertFalse(ap2.isOneDutyStation());

		/*
		 * AC change allowed international stations.
		 */
    	ap1.setCode("SSH");
    	ap2.setCode("LHR");

    	apRuleContext.getIntroducerProxy().introduce(ap1);
    	apRuleContext.getIntroducerProxy().introduce(ap2);

    	assertTrue(ap1.isAcChangeAllowed());
		assertFalse(ap2.isAcChangeAllowed());

		/*
		 * Special european stations.
		 */
    	ap1.setCode("BRU");
    	ap2.setCode("HAM");

    	apRuleContext.getIntroducerProxy().introduce(ap1);
    	apRuleContext.getIntroducerProxy().introduce(ap2);

    	assertTrue(ap1.isSpecialEuroStation());
		assertFalse(ap2.isSpecialEuroStation());

		/*
		 * Layover allowed stations.
		 */
    	ap1.setCode("ADA");
    	ap2.setCode("IST");

    	apRuleContext.getIntroducerProxy().introduce(ap1);
    	apRuleContext.getIntroducerProxy().introduce(ap2);

    	assertTrue(ap1.isLayoverAllowed());
		assertFalse(ap2.isLayoverAllowed());

		/*
		 * Mandatory overnighting stations.
		 */
    	ap1.setCode("ALA");
    	ap2.setCode("SAW");

    	apRuleContext.getIntroducerProxy().introduce(ap1);
    	apRuleContext.getIntroducerProxy().introduce(ap2);

    	assertTrue(ap1.isEndDutyIfTouches());
		assertFalse(ap2.isEndDutyIfTouches());

		/*
		 * Stations that must be the first overnight in pairings.
		 */
    	ap1.setCode("LHR");
    	ap2.setCode("CDG");

    	apRuleContext.getIntroducerProxy().introduce(ap1);
    	apRuleContext.getIntroducerProxy().introduce(ap2);

    	assertTrue(ap1.isMandatoryFirstLayover());
		assertFalse(ap2.isMandatoryFirstLayover());

		/*
		 * Max connection exception stations.
		 */
    	ap1.setCode("TLV");
    	ap2.setCode("CAI");

    	apRuleContext.getIntroducerProxy().introduce(ap1);
    	apRuleContext.getIntroducerProxy().introduce(ap2);

    	assertTrue(ap1.isLegConnectionExceptionStation());
		assertFalse(ap2.isLegConnectionExceptionStation());

		/*
		 * Airport group id test.
		 */
    	ap1.setCode("ORY");
    	ap2.setCode("HOU");

    	apRuleContext.getIntroducerProxy().introduce(ap1);
    	apRuleContext.getIntroducerProxy().introduce(ap2);

    	assertTrue(ap1.getGroupId() == 1);
		assertTrue(ap2.getGroupId() == 0);
    }
}
