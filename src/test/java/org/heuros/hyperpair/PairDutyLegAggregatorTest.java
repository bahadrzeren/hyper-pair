package org.heuros.hyperpair;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import org.heuros.core.rule.intf.Rule;
import org.heuros.data.model.Airport;
import org.heuros.data.model.AirportFactory;
import org.heuros.data.model.Duty;
import org.heuros.data.model.DutyFactory;
import org.heuros.data.model.Leg;
import org.heuros.data.model.LegFactory;
import org.heuros.data.model.Pair;
import org.heuros.data.model.PairFactory;
import org.heuros.hyperpair.intro.AirportIntroducer;
import org.heuros.hyperpair.intro.DutyLegAggregator;
import org.heuros.hyperpair.intro.LegIntroducer;
import org.heuros.hyperpair.intro.PairDutyAggregator;
import org.heuros.hyperpair.rule.PairNumOfPassiveLegsLimit;
import org.heuros.hyperpair.rule.PairPeriodLength;
import org.heuros.rule.AirportRuleContext;
import org.heuros.rule.DutyRuleContext;
import org.heuros.rule.LegRuleContext;
import org.heuros.rule.PairRuleContext;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Airport Introducer test.
 */
public class PairDutyLegAggregatorTest extends TestCase {

	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public PairDutyLegAggregatorTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( PairDutyLegAggregatorTest.class );
    }

    /**
     * Test Duty Leg append/remove.
     */
    public void testDutyLegAggregation()
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
    	Airport apSAW = apFactory.generateModel();
    	Airport apESB = apFactory.generateModel();
    	Airport apADA = apFactory.generateModel();
    	Airport apAYT = apFactory.generateModel();
    	Airport apEZS = apFactory.generateModel();
    	Airport apCDG = apFactory.generateModel();
    	Airport apBRE = apFactory.generateModel();
    	Airport apHAM = apFactory.generateModel();
    	Airport apLHR = apFactory.generateModel();
    	Airport apJED = apFactory.generateModel();
    	Airport apJFK = apFactory.generateModel();

    	apIST.setCode("IST");
    	apSAW.setCode("SAW");
    	apESB.setCode("ESB");
    	apADA.setCode("ADA");
    	apAYT.setCode("AYT");
    	apEZS.setCode("EZS");
    	apCDG.setCode("CDG");
    	apBRE.setCode("BRE");
    	apHAM.setCode("HAM");
    	apLHR.setCode("LHR");
    	apJED.setCode("JED");
    	apJFK.setCode("JFK");

    	apRuleContext.getIntroducerProxy().introduce(apIST);
    	apRuleContext.getIntroducerProxy().introduce(apSAW);
    	apRuleContext.getIntroducerProxy().introduce(apESB);
    	apRuleContext.getIntroducerProxy().introduce(apADA);
    	apRuleContext.getIntroducerProxy().introduce(apAYT);
    	apRuleContext.getIntroducerProxy().introduce(apEZS);
    	apRuleContext.getIntroducerProxy().introduce(apCDG);
    	apRuleContext.getIntroducerProxy().introduce(apBRE);
    	apRuleContext.getIntroducerProxy().introduce(apHAM);
    	apRuleContext.getIntroducerProxy().introduce(apLHR);
    	apRuleContext.getIntroducerProxy().introduce(apJED);
    	apRuleContext.getIntroducerProxy().introduce(apJFK);

    	/*
    	 * Generate leg instances for the test.
    	 */

    	LegFactory legFactory = new LegFactory();
    	LegRuleContext legRuleContext = new LegRuleContext(HeurosSystemParam.homebases.length);

    	Rule legIntroducer = new LegIntroducer();

    	try {
    		legRuleContext.registerRule(legIntroducer);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    		assertTrue(false);
    	}

    	Leg legAct11HbToDom = legFactory.generateModel();
    	Leg legAct12DomToDom = legFactory.generateModel();
    	Leg legAct13DomToInt = legFactory.generateModel();
    	Leg legAct21IntToDom = legFactory.generateModel();
    	Leg legAct22DomToHb = legFactory.generateModel();
    	Leg legAct31HbToInt = legFactory.generateModel();
    	Leg legAct32IntToHb = legFactory.generateModel();
    	Leg legAct41HbToHb = legFactory.generateModel();
    	Leg legPss11HbToDom = legFactory.generateModel();
    	Leg legPss12DomToDom = legFactory.generateModel();
    	Leg legPss13DomToInt = legFactory.generateModel();
    	Leg legPss21IntToDom = legFactory.generateModel();
    	Leg legPss22DomToHb = legFactory.generateModel();
    	Leg legPss31HbToInt = legFactory.generateModel();
    	Leg legPss32IntToHb = legFactory.generateModel();
    	Leg legPss41HbToHb = legFactory.generateModel();

    	PairDutyLegAggregatorTest.setLegFields(legAct11HbToDom, 101, apIST, apAYT, LocalDateTime.of(2014, Month.JANUARY, 1, 10, 0), LocalDateTime.of(2014, Month.JANUARY, 1, 11, 30), "320");	//	90
    	PairDutyLegAggregatorTest.setLegFields(legAct12DomToDom, 102, apAYT, apEZS, LocalDateTime.of(2014, Month.JANUARY, 1, 13, 0), LocalDateTime.of(2014, Month.JANUARY, 1, 15, 0), "320");	//	120
    	PairDutyLegAggregatorTest.setLegFields(legAct13DomToInt, 103, apEZS, apCDG, LocalDateTime.of(2014, Month.JANUARY, 1, 16, 0), LocalDateTime.of(2014, Month.JANUARY, 1, 19, 0), "320");	//	180
    	PairDutyLegAggregatorTest.setLegFields(legAct21IntToDom, 104, apCDG, apADA, LocalDateTime.of(2014, Month.JANUARY, 2, 20, 30), LocalDateTime.of(2014, Month.JANUARY, 2, 22, 15), "320");	//	105
    	PairDutyLegAggregatorTest.setLegFields(legAct22DomToHb, 105, apADA, apIST, LocalDateTime.of(2014, Month.JANUARY, 2, 23, 0), LocalDateTime.of(2014, Month.JANUARY, 3, 1, 50), "320");		//	170
    	PairDutyLegAggregatorTest.setLegFields(legAct31HbToInt, 106, apIST, apLHR, LocalDateTime.of(2014, Month.JANUARY, 3, 14, 0), LocalDateTime.of(2014, Month.JANUARY, 3, 15, 50), "320");		//	110
    	PairDutyLegAggregatorTest.setLegFields(legAct32IntToHb, 107, apLHR, apSAW, LocalDateTime.of(2014, Month.JANUARY, 3, 17, 0), LocalDateTime.of(2014, Month.JANUARY, 3, 20, 10), "320");		//	190
    	PairDutyLegAggregatorTest.setLegFields(legAct41HbToHb, 108, apIST, apSAW, LocalDateTime.of(2014, Month.JANUARY, 4, 20, 0), LocalDateTime.of(2014, Month.JANUARY, 5, 4, 0), "320");			//	480

    	PairDutyLegAggregatorTest.setLegFields(legPss11HbToDom, 101, apIST, apAYT, LocalDateTime.of(2014, Month.JANUARY, 1, 10, 0), LocalDateTime.of(2014, Month.JANUARY, 1, 11, 30), "737");
    	PairDutyLegAggregatorTest.setLegFields(legPss12DomToDom, 102, apAYT, apEZS, LocalDateTime.of(2014, Month.JANUARY, 1, 13, 0), LocalDateTime.of(2014, Month.JANUARY, 1, 15, 0), "737");
    	PairDutyLegAggregatorTest.setLegFields(legPss13DomToInt, 103, apEZS, apCDG, LocalDateTime.of(2014, Month.JANUARY, 1, 16, 0), LocalDateTime.of(2014, Month.JANUARY, 1, 19, 0), "737");
    	PairDutyLegAggregatorTest.setLegFields(legPss21IntToDom, 104, apCDG, apADA, LocalDateTime.of(2014, Month.JANUARY, 2, 20, 30), LocalDateTime.of(2014, Month.JANUARY, 2, 22, 15), "737");
    	PairDutyLegAggregatorTest.setLegFields(legPss22DomToHb, 105, apADA, apIST, LocalDateTime.of(2014, Month.JANUARY, 2, 23, 0), LocalDateTime.of(2014, Month.JANUARY, 3, 1, 50), "737");
    	PairDutyLegAggregatorTest.setLegFields(legPss31HbToInt, 106, apIST, apLHR, LocalDateTime.of(2014, Month.JANUARY, 3, 14, 0), LocalDateTime.of(2014, Month.JANUARY, 3, 15, 50), "737");
    	PairDutyLegAggregatorTest.setLegFields(legPss32IntToHb, 107, apLHR, apSAW, LocalDateTime.of(2014, Month.JANUARY, 3, 17, 0), LocalDateTime.of(2014, Month.JANUARY, 3, 20, 10), "737");
    	PairDutyLegAggregatorTest.setLegFields(legPss41HbToHb, 108, apIST, apSAW, LocalDateTime.of(2014, Month.JANUARY, 4, 20, 0), LocalDateTime.of(2014, Month.JANUARY, 5, 4, 0), "737");

    	legRuleContext.getIntroducerProxy().introduce(legAct11HbToDom);
    	legRuleContext.getIntroducerProxy().introduce(legAct12DomToDom);
    	legRuleContext.getIntroducerProxy().introduce(legAct13DomToInt);
    	legRuleContext.getIntroducerProxy().introduce(legAct21IntToDom);
    	legRuleContext.getIntroducerProxy().introduce(legAct22DomToHb);
    	legRuleContext.getIntroducerProxy().introduce(legAct31HbToInt);
    	legRuleContext.getIntroducerProxy().introduce(legAct32IntToHb);
    	legRuleContext.getIntroducerProxy().introduce(legAct41HbToHb);

    	legRuleContext.getIntroducerProxy().introduce(legPss11HbToDom);
    	legRuleContext.getIntroducerProxy().introduce(legPss12DomToDom);
    	legRuleContext.getIntroducerProxy().introduce(legPss13DomToInt);
    	legRuleContext.getIntroducerProxy().introduce(legPss21IntToDom);
    	legRuleContext.getIntroducerProxy().introduce(legPss22DomToHb);
    	legRuleContext.getIntroducerProxy().introduce(legPss31HbToInt);
    	legRuleContext.getIntroducerProxy().introduce(legPss32IntToHb);
    	legRuleContext.getIntroducerProxy().introduce(legPss41HbToHb);

		/*
		 * Generate Duty context.
		 */
		DutyFactory dutyFactory = new DutyFactory(HeurosSystemParam.homebases.length);
		DutyRuleContext dutyRuleContext = new DutyRuleContext(HeurosSystemParam.homebases.length);
		DutyLegAggregator dutyLegAggregator = new DutyLegAggregator();

		try {
			dutyRuleContext.registerRule(dutyLegAggregator);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    		assertTrue(false);
    	}

		/*
    	 * Aggregate Legs on duty.
    	 */
		Duty d = dutyFactory.generateModel();
		dutyRuleContext.getAggregatorImpl().append(d, legAct11HbToDom);
		dutyRuleContext.getAggregatorImpl().append(d, legAct12DomToDom);
		dutyRuleContext.getAggregatorImpl().append(d, legAct13DomToInt);

		assertTrue(d.getBlockTimeInMins() == 390);
		assertTrue(d.getNumOfLegs() == 3);
		assertTrue(d.getBlockTimeInMinsActive() == 390);
		assertTrue(d.getNumOfLegsActive() == 3);
		assertTrue(d.getNumOfLegsIntToDom() == 0);
		assertTrue(d.getNumOfLegsDomToInt() == 1);
		assertTrue(d.getNumOfCriticalLegs() == 0);
		assertTrue(d.getNumOfAgDg() == 0);
		assertTrue(d.getNumOfSpecialFlights() == 0);
		assertTrue(d.getNumOfAnyHomebaseTouch() == 0);
		assertTrue(d.getNumOfDomTouch() == 2);
		assertTrue(d.getNumOfIntTouch() == 0);
		assertTrue(d.getNumOfAcChanges() == 0);
		assertTrue(d.getLongConnDiff() == 0);
		int hbNdx = 0;
		int nonHbNdx = 5;
		assertTrue(d.getBriefDurationInMins(hbNdx) == 60);	//	90
		assertTrue(d.getBriefDurationInMins(nonHbNdx) == 60);
		assertTrue(d.getDebriefDurationInMins(hbNdx) == 30);

		assertTrue(d.getBriefTime(hbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 9, 0)));
		assertTrue(d.getBriefTime(nonHbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 9, 0)));
		assertTrue(d.getDebriefTime(hbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 19, 30)));

		assertTrue(d.getBriefDayBeginning(hbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
		assertTrue(d.getBriefDayBeginning(nonHbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
		assertTrue(d.getDebriefDayEnding(hbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 23, 59, 59)));

		assertTrue(d.getBriefDay(hbNdx).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
		assertTrue(d.getBriefDay(nonHbNdx).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
		assertTrue(d.getDebriefDay(hbNdx).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));

		assertTrue(d.getDutyDurationInMins(hbNdx) == 630);
		assertTrue(d.getDutyDurationInMins(nonHbNdx) == 630);

		assertTrue(d.getNumOfDaysTouched(hbNdx) == 1);
		assertTrue(d.getNumOfDaysTouched(nonHbNdx) == 1);

		assertFalse(d.isEr());

//		System.out.println(d.getRestDurationInMinsHbToHb());
//		System.out.println(d.getRestDurationInMinsHbToNonHb());
//		System.out.println(d.getRestDurationInMinsNonHbToHb());
//		System.out.println(d.getRestDurationInMinsNonHbToNonHb());

		assertTrue(d.getRestDurationInMins(hbNdx) == 840);
		assertTrue(d.getRestDurationInMins(nonHbNdx) == 840);

		assertTrue(d.getNextBriefTime(hbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 19, 30).plusMinutes(840)));
		assertTrue(d.getNextBriefTime(nonHbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 19, 30).plusMinutes(840)));

		assertTrue(d.getAugmented(hbNdx) == 0);
		assertTrue(d.getAugmented(nonHbNdx) == 0);

		assertFalse(d.isEarly(hbNdx));
		assertFalse(d.isEarly(nonHbNdx));

		assertFalse(d.isHard(hbNdx));
		assertFalse(d.isHard(nonHbNdx));

		/*
    	 * Remove last leg from duty.
    	 */

		dutyRuleContext.getAggregatorImpl().removeLast(d);

		assertTrue(d.getBlockTimeInMins() == 210);
		assertTrue(d.getNumOfLegs() == 2);
		assertTrue(d.getBlockTimeInMinsActive() == 210);
		assertTrue(d.getNumOfLegsActive() == 2);
		assertTrue(d.getNumOfLegsIntToDom() == 0);
		assertTrue(d.getNumOfLegsDomToInt() == 0);
		assertTrue(d.getNumOfCriticalLegs() == 0);
		assertTrue(d.getNumOfAgDg() == 0);
		assertTrue(d.getNumOfSpecialFlights() == 0);
		assertTrue(d.getNumOfAnyHomebaseTouch() == 0);
		assertTrue(d.getNumOfDomTouch() == 1);
		assertTrue(d.getNumOfIntTouch() == 0);
		assertTrue(d.getNumOfAcChanges() == 0);
		assertTrue(d.getLongConnDiff() == 0);

		assertTrue(d.getBriefDurationInMins(hbNdx) == 60);
		assertTrue(d.getBriefDurationInMins(nonHbNdx) == 60);
		assertTrue(d.getDebriefDurationInMins(hbNdx) == 30);

		assertTrue(d.getBriefTime(hbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 9, 0)));
		assertTrue(d.getBriefTime(nonHbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 9, 0)));
		assertTrue(d.getDebriefTime(hbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 15, 30)));

		assertTrue(d.getBriefDayBeginning(hbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
		assertTrue(d.getBriefDayBeginning(nonHbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
		assertTrue(d.getDebriefDayEnding(hbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 23, 59, 59)));

		assertTrue(d.getBriefDay(hbNdx).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
		assertTrue(d.getBriefDay(nonHbNdx).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
		assertTrue(d.getDebriefDay(hbNdx).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));

		assertTrue(d.getDutyDurationInMins(hbNdx) == 390);
		assertTrue(d.getDutyDurationInMins(nonHbNdx) == 390);

		assertTrue(d.getNumOfDaysTouched(hbNdx) == 1);
		assertTrue(d.getNumOfDaysTouched(nonHbNdx) == 1);

		assertFalse(d.isEr());

//		System.out.println(d.getRestDurationInMinsHbToHb());
//		System.out.println(d.getRestDurationInMinsHbToNonHb());
//		System.out.println(d.getRestDurationInMinsNonHbToHb());
//		System.out.println(d.getRestDurationInMinsNonHbToNonHb());

		assertTrue(d.getRestDurationInMins(hbNdx) == 780);
		assertTrue(d.getRestDurationInMins(nonHbNdx) == 660);

		assertTrue(d.getNextBriefTime(hbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 15, 30).plusMinutes(780)));
		assertTrue(d.getNextBriefTime(nonHbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 15, 30).plusMinutes(660)));

		assertTrue(d.getAugmented(hbNdx) == 0);
		assertTrue(d.getAugmented(nonHbNdx) == 0);

		assertFalse(d.isEarly(hbNdx));
		assertFalse(d.isEarly(nonHbNdx));

		assertFalse(d.isHard(hbNdx));
		assertFalse(d.isHard(nonHbNdx));

		/*
    	 * Remove all legs from duty.
    	 */

		dutyRuleContext.getAggregatorImpl().removeLast(d);
		dutyRuleContext.getAggregatorImpl().removeLast(d);

		assertTrue(d.getBlockTimeInMins() == 0);
		assertTrue(d.getNumOfLegs() == 0);
		assertTrue(d.getBlockTimeInMinsActive() == 0);
		assertTrue(d.getNumOfLegsActive() == 0);
		assertTrue(d.getNumOfLegsIntToDom() == 0);
		assertTrue(d.getNumOfLegsDomToInt() == 0);
		assertTrue(d.getNumOfCriticalLegs() == 0);
		assertTrue(d.getNumOfAgDg() == 0);
		assertTrue(d.getNumOfSpecialFlights() == 0);
		assertTrue(d.getNumOfAnyHomebaseTouch() == 0);
		assertTrue(d.getNumOfDomTouch() == 0);
		assertTrue(d.getNumOfIntTouch() == 0);
		assertTrue(d.getNumOfAcChanges() == 0);
		assertTrue(d.getLongConnDiff() == 0);

		/*
		 * After reset call in removeLastLeg() method, for empty duties this part is commented out.
		 */
//		assertTrue(d.getBriefDurationInMins(hbNdx) == 90);
//		assertTrue(d.getBriefDurationInMinsNonHb() == 60);
//		assertTrue(d.getDebriefDurationInMins(hbNdx) == 30);
//
//		assertTrue(d.getBriefTime(hbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 8, 30)));
//		assertTrue(d.getBriefTimeNonHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 9, 0)));
//		assertTrue(d.getDebriefTime(hbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 12, 0)));
//
//		assertTrue(d.getBriefDayBeginning(hbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
//		assertTrue(d.getBriefDayBeginningNonHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
//		assertTrue(d.getDebriefDayEnding(hbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 23, 59, 59)));
//
//		assertTrue(d.getBriefDay(hbNdx).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
//		assertTrue(d.getBriefDayNonHb().isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
//		assertTrue(d.getDebriefDay(hbNdx).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
//
//		assertTrue(d.getDutyDurationInMins(hbNdx) == 210);
//		assertTrue(d.getDutyDurationInMinsNonHb() == 180);
//
//		assertTrue(d.getNumOfDaysTouched(hbNdx) == 1);
//		assertTrue(d.getNumOfDaysTouchedNonHb() == 1);
//
//		assertFalse(d.isEr());
//
//		System.out.println(d.getRestDurationInMinsHbToHb());
//		System.out.println(d.getRestDurationInMinsHbToNonHb());
//		System.out.println(d.getRestDurationInMinsNonHbToHb());
//		System.out.println(d.getRestDurationInMinsNonHbToNonHb());
//
//		assertTrue(d.getRestDurationInMinsHbToHb() == 780);
//		assertTrue(d.getRestDurationInMinsHbToNonHb() == 660);
//		assertTrue(d.getRestDurationInMinsNonHbToHb() == 780);
//		assertTrue(d.getRestDurationInMinsNonHbToNonHb() == 660);
//
//		assertTrue(d.getNextBriefTimeHbToHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 12, 0).plusMinutes(780)));
//		assertTrue(d.getNextBriefTimeHbToNonHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 12, 0).plusMinutes(660)));
//		assertTrue(d.getNextBriefTimeNonHbToHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 12, 0).plusMinutes(780)));
//		assertTrue(d.getNextBriefTimeNonHbToNonHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 12, 0).plusMinutes(660)));

		assertTrue(d.getBriefDurationInMins(hbNdx) == 0);
		assertTrue(d.getBriefDurationInMins(nonHbNdx) == 0);
		assertTrue(d.getDebriefDurationInMins(hbNdx) == 0);

		assertTrue(d.getBriefTime(hbNdx) == null);
		assertTrue(d.getBriefTime(nonHbNdx) == null);
		assertTrue(d.getDebriefTime(hbNdx) == null);

		assertTrue(d.getBriefDayBeginning(hbNdx) == null);
		assertTrue(d.getBriefDayBeginning(nonHbNdx) == null);
		assertTrue(d.getDebriefDayEnding(hbNdx) == null);

		assertTrue(d.getBriefDay(hbNdx) == null);
		assertTrue(d.getBriefDay(nonHbNdx) == null);
		assertTrue(d.getDebriefDay(hbNdx) == null);

		assertTrue(d.getDutyDurationInMins(hbNdx) == 0);
		assertTrue(d.getDutyDurationInMins(nonHbNdx) == 0);

		assertTrue(d.getNumOfDaysTouched(hbNdx) == 0);
		assertTrue(d.getNumOfDaysTouched(nonHbNdx) == 0);

		assertFalse(d.isEr());

		assertTrue(d.getRestDurationInMins(hbNdx) == 0);
		assertTrue(d.getRestDurationInMins(nonHbNdx) == 0);

		assertTrue(d.getNextBriefTime(hbNdx) == null);
		assertTrue(d.getNextBriefTime(nonHbNdx) == null);
		assertTrue(d.getAugmented(hbNdx) == 0);
		assertTrue(d.getAugmented(nonHbNdx) == 0);

		assertFalse(d.isEarly(hbNdx));
		assertFalse(d.isEarly(nonHbNdx));

		assertFalse(d.isHard(hbNdx));
		assertFalse(d.isHard(nonHbNdx));

		/*
		 * Regenerate first duty again.
		 */

		dutyRuleContext.getAggregatorImpl().append(d, legAct11HbToDom);
		dutyRuleContext.getAggregatorImpl().append(d, legAct12DomToDom);
		dutyRuleContext.getAggregatorImpl().append(d, legAct13DomToInt);

		/*
		 * Regenerate second duty again.
		 */

		Duty d2 = dutyFactory.generateModel();
		dutyRuleContext.getAggregatorImpl().append(d2, legAct21IntToDom);
		dutyRuleContext.getAggregatorImpl().append(d2, legAct22DomToHb);

		/*
		 * Generate pair for test.
		 */
		PairFactory pairFactory = new PairFactory(HeurosSystemParam.homebases.length);
		PairRuleContext pairRuleContext = new PairRuleContext(HeurosSystemParam.homebases.length);
		PairDutyAggregator pairDutyAggregator = new PairDutyAggregator();

		try {
			pairRuleContext.registerRule(pairDutyAggregator);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    		assertTrue(false);
    	}

		Pair p = pairFactory.generateModel();
		pairRuleContext.getAggregatorImpl().append(p, d);
		pairRuleContext.getAggregatorImpl().append(p, d2);

		assertTrue(p.getHbNdx() == d.getFirstDepAirport().getHbNdx());
		assertTrue(p.getHbNdx() == d2.getLastArrAirport().getHbNdx());
		assertTrue(p.getFirstDuty() == d);
		assertTrue(p.getLastDuty() == d2);
		assertTrue(p.getFirstLeg() == legAct11HbToDom);
		assertTrue(p.getLastLeg() == legAct22DomToHb);
		assertTrue(p.getFirstDepAirport() == d.getFirstDepAirport());
		assertTrue(p.getLastArrAirport() == d2.getLastArrAirport());
		assertTrue(p.getBlockTimeInMins() == 665);
		assertTrue(p.getBlockTimeInMinsActive() == 665);
		assertTrue(p.getBlockTimeInMinsPassive() == 0);
		assertTrue(p.getNumOfLegs() == 5);
		assertTrue(p.getNumOfLegsActive() == 5);
		assertTrue(p.getNumOfLegsPassive() == 0);
		assertTrue(p.getNumOfLegsIntToDom() == 1);
		assertTrue(p.getNumOfLegsDomToInt() == 1);
		assertTrue(p.getBriefDurationInMins(hbNdx) == 120);
		assertTrue(p.getDutyDurationInMins(hbNdx) == 1040);
		assertTrue(p.getDebriefDurationInMins(hbNdx) == 60);
		assertTrue(p.getRestDurationInMins(hbNdx) == d.getRestDurationInMins(hbNdx) + d2.getRestDurationInMins(hbNdx));
		assertTrue(p.getNumOfDaysTouched(hbNdx) == 3);
		assertTrue(p.getNumOfDuties() == 2);
		assertTrue(p.getNumOfInternationalDuties() == 1);
		assertTrue(p.getNumOfEarlyDuties(hbNdx) == 0);
		assertTrue(p.getNumOfHardDuties(hbNdx) == 1);
		assertTrue(p.getNumOfAugmentedDuties(hbNdx) == 0);
		assertTrue(p.getNumOfErDuties() == 0);

		/*
		 * Remove second duty from pair.
		 */
		pairRuleContext.getAggregatorImpl().removeLast(p);

		assertTrue(p.getHbNdx() == d.getFirstDepAirport().getHbNdx());
		assertTrue(p.getFirstDuty() == d);
		assertTrue(p.getLastDuty() == d);
		assertTrue(p.getFirstLeg() == legAct11HbToDom);
		assertTrue(p.getLastLeg() == legAct13DomToInt);
		assertTrue(p.getFirstDepAirport() == d.getFirstDepAirport());
		assertTrue(p.getLastArrAirport() == d.getLastArrAirport());
		assertTrue(p.getBlockTimeInMins() == 390);
		assertTrue(p.getBlockTimeInMinsActive() == 390);
		assertTrue(p.getBlockTimeInMinsPassive() == 0);
		assertTrue(p.getNumOfLegs() == 3);
		assertTrue(p.getNumOfLegsActive() == 3);
		assertTrue(p.getNumOfLegsPassive() == 0);
		assertTrue(p.getNumOfLegsIntToDom() == 0);
		assertTrue(p.getNumOfLegsDomToInt() == 1);
		assertTrue(p.getBriefDurationInMins(hbNdx) == 60);
		assertTrue(p.getDutyDurationInMins(hbNdx) == 630);
		assertTrue(p.getDebriefDurationInMins(hbNdx) == 30);
		assertTrue(p.getRestDurationInMins(hbNdx) == d.getRestDurationInMins(hbNdx));
		assertTrue(p.getNumOfDaysTouched(hbNdx) == 1);
		assertTrue(p.getNumOfDuties() == 1);
		assertTrue(p.getNumOfInternationalDuties() == 1);
		assertTrue(p.getNumOfEarlyDuties(hbNdx) == 0);
		assertTrue(p.getNumOfHardDuties(hbNdx) == 0);
		assertTrue(p.getNumOfAugmentedDuties(hbNdx) == 0);
		assertTrue(p.getNumOfErDuties() == 0);

		/*
		 * Remove all duties from pair.
		 */
		pairRuleContext.getAggregatorImpl().removeLast(p);

		assertTrue(p.getHbNdx() == -1);
		assertTrue(p.getFirstDuty() == null);
		assertTrue(p.getLastDuty() == null);
		assertTrue(p.getFirstLeg() == null);
		assertTrue(p.getLastLeg() == null);
		assertTrue(p.getFirstDepAirport() == null);
		assertTrue(p.getLastArrAirport() == null);
		assertTrue(p.getBlockTimeInMins() == 0);
		assertTrue(p.getBlockTimeInMinsActive() == 0);
		assertTrue(p.getBlockTimeInMinsPassive() == 0);
		assertTrue(p.getNumOfLegs() == 0);
		assertTrue(p.getNumOfLegsActive() == 0);
		assertTrue(p.getNumOfLegsPassive() == 0);
		assertTrue(p.getNumOfLegsIntToDom() == 0);
		assertTrue(p.getNumOfLegsDomToInt() == 0);
		assertTrue(p.getBriefDurationInMins(hbNdx) == 0);
		assertTrue(p.getDutyDurationInMins(hbNdx) == 0);
		assertTrue(p.getDebriefDurationInMins(hbNdx) == 0);
		assertTrue(p.getRestDurationInMins(hbNdx) == 0);
		assertTrue(p.getNumOfDaysTouched(hbNdx) == 0);
		assertTrue(p.getNumOfDuties() == 0);
		assertTrue(p.getNumOfInternationalDuties() == 0);
		assertTrue(p.getNumOfEarlyDuties(hbNdx) == 0);
		assertTrue(p.getNumOfHardDuties(hbNdx) == 0);
		assertTrue(p.getNumOfAugmentedDuties(hbNdx) == 0);
		assertTrue(p.getNumOfErDuties() == 0);

		/*
		 * PairPeriodLength rule test.
		 */
		PairPeriodLength pairPeriodLengthRule = new PairPeriodLength();
		pairRuleContext.getAggregatorImpl().append(p, d);
		pairRuleContext.getAggregatorImpl().append(p, d2);

		try {
			pairRuleContext.registerRule(pairPeriodLengthRule);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    		assertTrue(false);
    	}

		int maxPairingLengthInDays = HeurosSystemParam.maxPairingLengthInDays;
		assertTrue(pairRuleContext.getTotalizerCheckerProxy().isValid(hbNdx, p));
		HeurosSystemParam.maxPairingLengthInDays = 3;
		assertTrue(pairRuleContext.getTotalizerCheckerProxy().isValid(hbNdx, p));
		HeurosSystemParam.maxPairingLengthInDays = 2;
		assertFalse(pairRuleContext.getTotalizerCheckerProxy().isValid(hbNdx, p));
		HeurosSystemParam.maxPairingLengthInDays = maxPairingLengthInDays;

		pairRuleContext.removeRule(pairPeriodLengthRule);

		/*
		 * PairMaxNumberOfPassiveLegs rule test.
		 */
		PairNumOfPassiveLegsLimit pairNumOfPassiveLegsLimit = new PairNumOfPassiveLegsLimit();

		try {
			dutyRuleContext.registerRule(pairNumOfPassiveLegsLimit);
			pairRuleContext.registerRule(pairNumOfPassiveLegsLimit);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    		assertTrue(false);
    	}

		pairRuleContext.getAggregatorImpl().removeLast(p);
		assertTrue(pairRuleContext.getAppendabilityCheckerProxy().isAppendable(hbNdx, p, d2));
		pairRuleContext.getAggregatorImpl().append(p, d2);
		assertTrue(pairRuleContext.getTotalizerCheckerProxy().isValid(hbNdx, p));
		assertTrue(dutyRuleContext.getConnectionCheckerProxy().areConnectable(hbNdx, d, d2));

		legAct12DomToDom.setCover(false);
		legAct13DomToInt.setCover(false);
		legAct21IntToDom.setCover(false);
		legAct22DomToHb.setCover(false);

		/*
		 * TODO Here pairing reCalculate method must cover duty methods as well!
		 */
		dutyRuleContext.getAggregatorImpl().reCalculate(d);
		dutyRuleContext.getAggregatorImpl().reCalculate(d2);
		pairRuleContext.getAggregatorImpl().reCalculate(p);

		assertFalse(pairRuleContext.getTotalizerCheckerProxy().isValid(hbNdx, p));
		assertFalse(dutyRuleContext.getConnectionCheckerProxy().areConnectable(hbNdx, d, d2));
		pairRuleContext.getAggregatorImpl().removeLast(p);
		assertFalse(pairRuleContext.getAppendabilityCheckerProxy().isAppendable(hbNdx, p, d2));

		dutyRuleContext.removeRule(pairNumOfPassiveLegsLimit);
		pairRuleContext.removeRule(pairNumOfPassiveLegsLimit);

		legAct12DomToDom.setCover(true);
		legAct13DomToInt.setCover(true);
		legAct21IntToDom.setCover(true);
		legAct22DomToHb.setCover(true);

		/*
		 * TODO Here pairing reCalculate method must cover duty methods as well!
		 */
		pairRuleContext.getAggregatorImpl().append(p, d2);
		dutyRuleContext.getAggregatorImpl().reCalculate(d);
		dutyRuleContext.getAggregatorImpl().reCalculate(d2);
		pairRuleContext.getAggregatorImpl().reCalculate(p);

		/*
		 * Pair is reset. Old values must be valid again!
		 */
		assertTrue(p.getHbNdx() == d.getFirstDepAirport().getHbNdx());
		assertTrue(p.getHbNdx() == d2.getLastArrAirport().getHbNdx());
		assertTrue(p.getFirstDuty() == d);
		assertTrue(p.getLastDuty() == d2);
		assertTrue(p.getFirstLeg() == legAct11HbToDom);
		assertTrue(p.getLastLeg() == legAct22DomToHb);
		assertTrue(p.getFirstDepAirport() == d.getFirstDepAirport());
		assertTrue(p.getLastArrAirport() == d2.getLastArrAirport());
		assertTrue(p.getBlockTimeInMins() == 665);
		assertTrue(p.getBlockTimeInMinsActive() == 665);
		assertTrue(p.getBlockTimeInMinsPassive() == 0);
		assertTrue(p.getNumOfLegs() == 5);
		assertTrue(p.getNumOfLegsActive() == 5);
		assertTrue(p.getNumOfLegsPassive() == 0);
		assertTrue(p.getNumOfLegsIntToDom() == 1);
		assertTrue(p.getNumOfLegsDomToInt() == 1);
		assertTrue(p.getBriefDurationInMins(hbNdx) == 120);
		assertTrue(p.getDutyDurationInMins(hbNdx) == 1040);
		assertTrue(p.getDebriefDurationInMins(hbNdx) == 60);
		assertTrue(p.getRestDurationInMins(hbNdx) == d.getRestDurationInMins(hbNdx) + d2.getRestDurationInMins(hbNdx));
		assertTrue(p.getNumOfDaysTouched(hbNdx) == 3);
		assertTrue(p.getNumOfDuties() == 2);
		assertTrue(p.getNumOfInternationalDuties() == 1);
		assertTrue(p.getNumOfEarlyDuties(hbNdx) == 0);
		assertTrue(p.getNumOfHardDuties(hbNdx) == 1);
		assertTrue(p.getNumOfAugmentedDuties(hbNdx) == 0);
		assertTrue(p.getNumOfErDuties() == 0);


//		dutyRuleContext.getAggregatorImpl().append(d, legAct31HbToInt);
//		dutyRuleContext.getAggregatorImpl().append(d, legAct32IntToHb);
//
//		dutyRuleContext.getAggregatorImpl().append(d, legAct41HbToHb);


    }

    private static void setLegFields(Leg leg,
    							int flightNo,
	    						Airport depAirport,
	    						Airport arrAirport,
	    						LocalDateTime sobt,
	    						LocalDateTime sibt,
	    						String acType) {
    	leg.setFlightNo(flightNo);
    	leg.setDepAirport(depAirport);
    	leg.setArrAirport(arrAirport);
    	leg.setSobt(sobt);
    	leg.setSibt(sibt);
    	leg.setAcType(acType);
    }
}
