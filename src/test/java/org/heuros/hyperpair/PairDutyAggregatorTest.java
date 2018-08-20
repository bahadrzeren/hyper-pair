package org.heuros.hyperpair;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import org.heuros.core.rule.inf.Rule;
import org.heuros.data.model.Airport;
import org.heuros.data.model.AirportFactory;
import org.heuros.data.model.Duty;
import org.heuros.data.model.DutyFactory;
import org.heuros.data.model.Leg;
import org.heuros.data.model.LegFactory;
import org.heuros.hyperpair.intro.AirportIntroducer;
import org.heuros.hyperpair.intro.DutyLegAggregator;
import org.heuros.hyperpair.intro.LegIntroducer;
import org.heuros.rule.AirportRuleContext;
import org.heuros.rule.DutyRuleContext;
import org.heuros.rule.LegRuleContext;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Airport Introducer test.
 */
public class PairDutyAggregatorTest extends TestCase {

	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public PairDutyAggregatorTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( PairDutyAggregatorTest.class );
    }

    /**
     * Test Pair Duty append/remove.
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
    	LegRuleContext legRuleContext = new LegRuleContext();

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

    	PairDutyAggregatorTest.setLegFields(legAct11HbToDom, 101, apIST, apAYT, LocalDateTime.of(2014, Month.JANUARY, 1, 10, 0), LocalDateTime.of(2014, Month.JANUARY, 1, 11, 30), "320");	//	90
    	PairDutyAggregatorTest.setLegFields(legAct12DomToDom, 102, apAYT, apEZS, LocalDateTime.of(2014, Month.JANUARY, 1, 13, 0), LocalDateTime.of(2014, Month.JANUARY, 1, 15, 0), "320");	//	120
    	PairDutyAggregatorTest.setLegFields(legAct13DomToInt, 103, apEZS, apCDG, LocalDateTime.of(2014, Month.JANUARY, 1, 16, 0), LocalDateTime.of(2014, Month.JANUARY, 1, 19, 0), "320");	//	180
    	PairDutyAggregatorTest.setLegFields(legAct21IntToDom, 104, apCDG, apADA, LocalDateTime.of(2014, Month.JANUARY, 2, 20, 30), LocalDateTime.of(2014, Month.JANUARY, 2, 22, 15), "320");	//	105
    	PairDutyAggregatorTest.setLegFields(legAct22DomToHb, 105, apADA, apIST, LocalDateTime.of(2014, Month.JANUARY, 2, 23, 0), LocalDateTime.of(2014, Month.JANUARY, 3, 1, 50), "320");		//	170
    	PairDutyAggregatorTest.setLegFields(legAct31HbToInt, 106, apIST, apLHR, LocalDateTime.of(2014, Month.JANUARY, 3, 14, 0), LocalDateTime.of(2014, Month.JANUARY, 3, 15, 50), "320");		//	110
    	PairDutyAggregatorTest.setLegFields(legAct32IntToHb, 107, apLHR, apSAW, LocalDateTime.of(2014, Month.JANUARY, 3, 17, 0), LocalDateTime.of(2014, Month.JANUARY, 3, 20, 10), "320");		//	190
    	PairDutyAggregatorTest.setLegFields(legAct41HbToHb, 108, apIST, apSAW, LocalDateTime.of(2014, Month.JANUARY, 4, 20, 0), LocalDateTime.of(2014, Month.JANUARY, 5, 4, 0), "320");			//	480

    	PairDutyAggregatorTest.setLegFields(legPss11HbToDom, 101, apIST, apAYT, LocalDateTime.of(2014, Month.JANUARY, 1, 10, 0), LocalDateTime.of(2014, Month.JANUARY, 1, 11, 30), "737");
    	PairDutyAggregatorTest.setLegFields(legPss12DomToDom, 102, apAYT, apEZS, LocalDateTime.of(2014, Month.JANUARY, 1, 13, 0), LocalDateTime.of(2014, Month.JANUARY, 1, 15, 0), "737");
    	PairDutyAggregatorTest.setLegFields(legPss13DomToInt, 103, apEZS, apCDG, LocalDateTime.of(2014, Month.JANUARY, 1, 16, 0), LocalDateTime.of(2014, Month.JANUARY, 1, 19, 0), "737");
    	PairDutyAggregatorTest.setLegFields(legPss21IntToDom, 104, apCDG, apADA, LocalDateTime.of(2014, Month.JANUARY, 2, 20, 30), LocalDateTime.of(2014, Month.JANUARY, 2, 22, 15), "737");
    	PairDutyAggregatorTest.setLegFields(legPss22DomToHb, 105, apADA, apIST, LocalDateTime.of(2014, Month.JANUARY, 2, 23, 0), LocalDateTime.of(2014, Month.JANUARY, 3, 1, 50), "737");
    	PairDutyAggregatorTest.setLegFields(legPss31HbToInt, 106, apIST, apLHR, LocalDateTime.of(2014, Month.JANUARY, 3, 14, 0), LocalDateTime.of(2014, Month.JANUARY, 3, 15, 50), "737");
    	PairDutyAggregatorTest.setLegFields(legPss32IntToHb, 107, apLHR, apSAW, LocalDateTime.of(2014, Month.JANUARY, 3, 17, 0), LocalDateTime.of(2014, Month.JANUARY, 3, 20, 10), "737");
    	PairDutyAggregatorTest.setLegFields(legPss41HbToHb, 108, apIST, apSAW, LocalDateTime.of(2014, Month.JANUARY, 4, 20, 0), LocalDateTime.of(2014, Month.JANUARY, 5, 4, 0), "737");

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
		DutyFactory dutyFactory = new DutyFactory();
		DutyRuleContext dutyRuleContext = new DutyRuleContext();
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
		dutyLegAggregator.append(d, legAct11HbToDom);
		dutyLegAggregator.append(d, legAct12DomToDom);
		dutyLegAggregator.append(d, legAct13DomToInt);

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

		assertTrue(d.getBriefDurationInMinsHb() == 60);	//	90
		assertTrue(d.getBriefDurationInMinsNonHb() == 60);
		assertTrue(d.getDebriefDurationInMins() == 30);

		assertTrue(d.getBriefTimeHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 9, 0)));
		assertTrue(d.getBriefTimeNonHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 9, 0)));
		assertTrue(d.getDebriefTime().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 19, 30)));

		assertTrue(d.getBriefDayBeginningHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
		assertTrue(d.getBriefDayBeginningNonHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
		assertTrue(d.getDebriefDayEnding().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 23, 59, 59)));

		assertTrue(d.getBriefDayHb().isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
		assertTrue(d.getBriefDayNonHb().isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
		assertTrue(d.getDebriefDay().isEqual(LocalDate.of(2014, Month.JANUARY, 1)));

		assertTrue(d.getDutyDurationInMinsHb() == 630);
		assertTrue(d.getDutyDurationInMinsNonHb() == 630);

		assertTrue(d.getNumOfDaysTouchedHb() == 1);
		assertTrue(d.getNumOfDaysTouchedNonHb() == 1);

		assertFalse(d.isEr());

		assertTrue(d.getRestDurationInMinsHbToHb() == 840);
		assertTrue(d.getRestDurationInMinsHbToNonHb() == 840);
		assertTrue(d.getRestDurationInMinsNonHbToHb() == 840);
		assertTrue(d.getRestDurationInMinsNonHbToNonHb() == 840);

		assertTrue(d.getNextBriefTimeHbToHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 19, 30).plusMinutes(840)));
		assertTrue(d.getNextBriefTimeHbToNonHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 19, 30).plusMinutes(840)));
		assertTrue(d.getNextBriefTimeNonHbToHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 19, 30).plusMinutes(840)));
		assertTrue(d.getNextBriefTimeNonHbToNonHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 19, 30).plusMinutes(840)));

		assertTrue(d.getAugmentedHb() == 0);
		assertTrue(d.getAugmentedNonHb() == 0);

		assertFalse(d.isEarlyHb());
		assertFalse(d.isEarlyNonHb());

		assertFalse(d.isHardHb());
		assertFalse(d.isHardNonHb());

		/*
    	 * Remove last leg from duty.
    	 */

		dutyLegAggregator.removeLast(d);

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

		assertTrue(d.getBriefDurationInMinsHb() == 60);
		assertTrue(d.getBriefDurationInMinsNonHb() == 60);
		assertTrue(d.getDebriefDurationInMins() == 30);

		assertTrue(d.getBriefTimeHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 9, 0)));
		assertTrue(d.getBriefTimeNonHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 9, 0)));
		assertTrue(d.getDebriefTime().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 15, 30)));

		assertTrue(d.getBriefDayBeginningHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
		assertTrue(d.getBriefDayBeginningNonHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
		assertTrue(d.getDebriefDayEnding().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 23, 59, 59)));

		assertTrue(d.getBriefDayHb().isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
		assertTrue(d.getBriefDayNonHb().isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
		assertTrue(d.getDebriefDay().isEqual(LocalDate.of(2014, Month.JANUARY, 1)));

		assertTrue(d.getDutyDurationInMinsHb() == 390);
		assertTrue(d.getDutyDurationInMinsNonHb() == 390);

		assertTrue(d.getNumOfDaysTouchedHb() == 1);
		assertTrue(d.getNumOfDaysTouchedNonHb() == 1);

		assertFalse(d.isEr());

		System.out.println(d.getRestDurationInMinsHbToHb());
		System.out.println(d.getRestDurationInMinsHbToNonHb());
		System.out.println(d.getRestDurationInMinsNonHbToHb());
		System.out.println(d.getRestDurationInMinsNonHbToNonHb());

		assertTrue(d.getRestDurationInMinsHbToHb() == 780);
		assertTrue(d.getRestDurationInMinsHbToNonHb() == 660);
		assertTrue(d.getRestDurationInMinsNonHbToHb() == 780);
		assertTrue(d.getRestDurationInMinsNonHbToNonHb() == 660);

		assertTrue(d.getNextBriefTimeHbToHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 15, 30).plusMinutes(780)));
		assertTrue(d.getNextBriefTimeHbToNonHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 15, 30).plusMinutes(660)));
		assertTrue(d.getNextBriefTimeNonHbToHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 15, 30).plusMinutes(780)));
		assertTrue(d.getNextBriefTimeNonHbToNonHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 15, 30).plusMinutes(660)));

		assertTrue(d.getAugmentedHb() == 0);
		assertTrue(d.getAugmentedNonHb() == 0);

		assertFalse(d.isEarlyHb());
		assertFalse(d.isEarlyNonHb());

		assertFalse(d.isHardHb());
		assertFalse(d.isHardNonHb());

		/*
    	 * Remove all legs from duty.
    	 */

		dutyLegAggregator.removeLast(d);
		dutyLegAggregator.removeLast(d);

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
		 * After reset call for empty duties this part is commented out.
		 */
//		assertTrue(d.getBriefDurationInMinsHb() == 90);
//		assertTrue(d.getBriefDurationInMinsNonHb() == 60);
//		assertTrue(d.getDebriefDurationInMins() == 30);
//
//		assertTrue(d.getBriefTimeHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 8, 30)));
//		assertTrue(d.getBriefTimeNonHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 9, 0)));
//		assertTrue(d.getDebriefTime().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 12, 0)));
//
//		assertTrue(d.getBriefDayBeginningHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
//		assertTrue(d.getBriefDayBeginningNonHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
//		assertTrue(d.getDebriefDayEnding().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 23, 59, 59)));
//
//		assertTrue(d.getBriefDayHb().isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
//		assertTrue(d.getBriefDayNonHb().isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
//		assertTrue(d.getDebriefDay().isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
//
//		assertTrue(d.getDutyDurationInMinsHb() == 210);
//		assertTrue(d.getDutyDurationInMinsNonHb() == 180);
//
//		assertTrue(d.getNumOfDaysTouchedHb() == 1);
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

		assertTrue(d.getBriefDurationInMinsHb() == 0);
		assertTrue(d.getBriefDurationInMinsNonHb() == 0);
		assertTrue(d.getDebriefDurationInMins() == 0);

		assertTrue(d.getBriefTimeHb() == null);
		assertTrue(d.getBriefTimeNonHb() == null);
		assertTrue(d.getDebriefTime() == null);

		assertTrue(d.getBriefDayBeginningHb() == null);
		assertTrue(d.getBriefDayBeginningNonHb() == null);
		assertTrue(d.getDebriefDayEnding() == null);

		assertTrue(d.getBriefDayHb() == null);
		assertTrue(d.getBriefDayNonHb() == null);
		assertTrue(d.getDebriefDay() == null);

		assertTrue(d.getDutyDurationInMinsHb() == 0);
		assertTrue(d.getDutyDurationInMinsNonHb() == 0);

		assertTrue(d.getNumOfDaysTouchedHb() == 0);
		assertTrue(d.getNumOfDaysTouchedNonHb() == 0);

		assertFalse(d.isEr());

		assertTrue(d.getRestDurationInMinsHbToHb() == 0);
		assertTrue(d.getRestDurationInMinsHbToNonHb() == 0);
		assertTrue(d.getRestDurationInMinsNonHbToHb() == 0);
		assertTrue(d.getRestDurationInMinsNonHbToNonHb() == 0);

		assertTrue(d.getNextBriefTimeHbToHb() == null);
		assertTrue(d.getNextBriefTimeHbToNonHb() == null);
		assertTrue(d.getNextBriefTimeNonHbToHb() == null);
		assertTrue(d.getNextBriefTimeNonHbToNonHb() == null);
		assertTrue(d.getAugmentedHb() == 0);
		assertTrue(d.getAugmentedNonHb() == 0);

		assertFalse(d.isEarlyHb());
		assertFalse(d.isEarlyNonHb());

		assertFalse(d.isHardHb());
		assertFalse(d.isHardNonHb());

//		dutyLegAggregator.append(d, legAct21IntToDom);
//		dutyLegAggregator.append(d, legAct22DomToHb);
//
//		dutyLegAggregator.append(d, legAct31HbToInt);
//		dutyLegAggregator.append(d, legAct32IntToHb);
//
//		dutyLegAggregator.append(d, legAct41HbToHb);

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
