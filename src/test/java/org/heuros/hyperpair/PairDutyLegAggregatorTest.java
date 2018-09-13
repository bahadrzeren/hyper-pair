package org.heuros.hyperpair;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import org.heuros.data.model.Airport;
import org.heuros.data.model.Duty;
import org.heuros.data.model.Leg;
import org.heuros.hyperpair.intro.AirportIntroducer;
import org.heuros.hyperpair.intro.DutyLegAggregator;
import org.heuros.hyperpair.intro.LegIntroducer;
import org.heuros.util.test.DailyLegsTest;
import org.heuros.util.test.HeurosAirportTestUtil;
import org.heuros.util.test.HeurosDutyTestUtil;
import org.heuros.util.test.HeurosLegTestUtil;

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
    	 * Generate airport instances.
    	 */
    	assertTrue(HeurosAirportTestUtil.initializeAirportContext(new AirportIntroducer()));

    	Airport apIST = HeurosAirportTestUtil.generateAirportInstance("IST");
    	Airport apSAW = HeurosAirportTestUtil.generateAirportInstance("SAW");
    	Airport apAYT = HeurosAirportTestUtil.generateAirportInstance("AYT");
    	Airport apEZS = HeurosAirportTestUtil.generateAirportInstance("EZS");
//    	Airport apESB = HeurosAirportTestUtil.generateAirportInstance("ESB");
//    	Airport apADA = HeurosAirportTestUtil.generateAirportInstance("ADA");
    	Airport apCDG = HeurosAirportTestUtil.generateAirportInstance("CDG");
    	Airport apBRE = HeurosAirportTestUtil.generateAirportInstance("BRE");
//    	Airport apHAM = HeurosAirportTestUtil.generateAirportInstance("HAM");
    	Airport apJED = HeurosAirportTestUtil.generateAirportInstance("JED");
    	Airport apJFK = HeurosAirportTestUtil.generateAirportInstance("JFK");
    	Airport apLHR = HeurosAirportTestUtil.generateAirportInstance("LHR");

    	assertTrue(apIST.isAnyHb());
    	assertTrue(apSAW.isAnyHb());
    	assertTrue(apAYT.isAnyNonHb());
    	assertTrue(apEZS.isAnyNonHb());
    	assertTrue(apCDG.isAnyNonHb());
    	assertTrue(apBRE.isAnyNonHb());
    	assertTrue(apJED.isAnyNonHb());
    	assertTrue(apJFK.isAnyNonHb());
    	assertTrue(apLHR.isAnyNonHb());

    	assertTrue(apLHR.isSpecialEuroStation());

    	/*
    	 * Generate leg instances for the test.
    	 */

    	assertTrue(HeurosLegTestUtil.initializeLegContext(new LegIntroducer(), HeurosSystemParam.homebases.length));
    	DailyLegsTest[] dailyLegs = HeurosLegTestUtil.getTestLegs(5, apIST, apSAW, apAYT, apEZS, apCDG, apBRE, apJED, apJFK);

		/*
		 * Generate Duty context.
		 */
    	assertTrue(HeurosDutyTestUtil.initializeDutyContext(new DutyLegAggregator(), HeurosSystemParam.homebases.length));

    	final Leg[] legs = {dailyLegs[0].getHb1_Hb1ToDom1_1(),
    						dailyLegs[0].getHb1_Dom1ToHb1_1()};

    	Duty d = HeurosDutyTestUtil.generateDutyInstance(legs);

		assertTrue(d.getBlockTimeInMins() == 180);
		assertTrue(d.getNumOfLegs() == 2);
		assertTrue(d.getBlockTimeInMinsActive() == 180);
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
		int hbNdx = 0;
		int nonHbNdx = 1;
		assertTrue(d.getBriefDurationInMins(hbNdx) == 90);		//	90
		assertTrue(d.getBriefDurationInMins(nonHbNdx) == 60);
		assertTrue(d.getDebriefDurationInMins(hbNdx) == 30);

		assertTrue(d.getBriefTime(hbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 8, 30)));		//	90
		assertTrue(d.getBriefTime(nonHbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 9, 0)));
		assertTrue(d.getDebriefTime(hbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 14, 30)));

		assertTrue(d.getBriefDayBeginning(hbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
		assertTrue(d.getBriefDayBeginning(nonHbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
		assertTrue(d.getDebriefDayEnding(hbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 23, 59, 59)));

		assertTrue(d.getBriefDay(hbNdx).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
		assertTrue(d.getBriefDay(nonHbNdx).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
		assertTrue(d.getDebriefDay(hbNdx).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));

		assertTrue(d.getDutyDurationInMins(hbNdx) == 360);		//	90
		assertTrue(d.getDutyDurationInMins(nonHbNdx) == 330);

		assertTrue(d.getNumOfDaysTouched(hbNdx) == 1);
		assertTrue(d.getNumOfDaysTouched(nonHbNdx) == 1);

		assertFalse(d.isEr());

//		System.out.println(d.getRestDurationInMinsHbToHb());
//		System.out.println(d.getRestDurationInMinsHbToNonHb());
//		System.out.println(d.getRestDurationInMinsNonHbToHb());
//		System.out.println(d.getRestDurationInMinsNonHbToNonHb());

		assertTrue(d.getRestDurationInMins(hbNdx) == 780);
		assertTrue(d.getRestDurationInMins(nonHbNdx) == 660);

		assertTrue(d.getNextBriefTime(hbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 14, 30).plusMinutes(780)));
		assertTrue(d.getNextBriefTime(nonHbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 14, 30).plusMinutes(660)));

		assertTrue(d.getAugmented(hbNdx) == 0);
		assertTrue(d.getAugmented(nonHbNdx) == 0);

		assertFalse(d.isEarly(hbNdx));
		assertFalse(d.isEarly(nonHbNdx));

		assertFalse(d.isHard(hbNdx));
		assertFalse(d.isHard(nonHbNdx));

//		/*
//    	 * Remove last leg from duty.
//    	 */
//
//		dutyRuleContext.getAggregatorImpl().removeLast(d);
//
//		assertTrue(d.getBlockTimeInMins() == 210);
//		assertTrue(d.getNumOfLegs() == 2);
//		assertTrue(d.getBlockTimeInMinsActive() == 210);
//		assertTrue(d.getNumOfLegsActive() == 2);
//		assertTrue(d.getNumOfLegsIntToDom() == 0);
//		assertTrue(d.getNumOfLegsDomToInt() == 0);
//		assertTrue(d.getNumOfCriticalLegs() == 0);
//		assertTrue(d.getNumOfAgDg() == 0);
//		assertTrue(d.getNumOfSpecialFlights() == 0);
//		assertTrue(d.getNumOfAnyHomebaseTouch() == 0);
//		assertTrue(d.getNumOfDomTouch() == 1);
//		assertTrue(d.getNumOfIntTouch() == 0);
//		assertTrue(d.getNumOfAcChanges() == 0);
//		assertTrue(d.getLongConnDiff() == 0);
//
//		assertTrue(d.getBriefDurationInMins(hbNdx) == 60);
//		assertTrue(d.getBriefDurationInMins(nonHbNdx) == 60);
//		assertTrue(d.getDebriefDurationInMins(hbNdx) == 30);
//
//		assertTrue(d.getBriefTime(hbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 9, 0)));
//		assertTrue(d.getBriefTime(nonHbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 9, 0)));
//		assertTrue(d.getDebriefTime(hbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 15, 30)));
//
//		assertTrue(d.getBriefDayBeginning(hbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
//		assertTrue(d.getBriefDayBeginning(nonHbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
//		assertTrue(d.getDebriefDayEnding(hbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 23, 59, 59)));
//
//		assertTrue(d.getBriefDay(hbNdx).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
//		assertTrue(d.getBriefDay(nonHbNdx).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
//		assertTrue(d.getDebriefDay(hbNdx).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
//
//		assertTrue(d.getDutyDurationInMins(hbNdx) == 390);
//		assertTrue(d.getDutyDurationInMins(nonHbNdx) == 390);
//
//		assertTrue(d.getNumOfDaysTouched(hbNdx) == 1);
//		assertTrue(d.getNumOfDaysTouched(nonHbNdx) == 1);
//
//		assertFalse(d.isEr());
//
////		System.out.println(d.getRestDurationInMinsHbToHb());
////		System.out.println(d.getRestDurationInMinsHbToNonHb());
////		System.out.println(d.getRestDurationInMinsNonHbToHb());
////		System.out.println(d.getRestDurationInMinsNonHbToNonHb());
//
//		assertTrue(d.getRestDurationInMins(hbNdx) == 780);
//		assertTrue(d.getRestDurationInMins(nonHbNdx) == 660);
//
//		assertTrue(d.getNextBriefTime(hbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 15, 30).plusMinutes(780)));
//		assertTrue(d.getNextBriefTime(nonHbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 15, 30).plusMinutes(660)));
//
//		assertTrue(d.getAugmented(hbNdx) == 0);
//		assertTrue(d.getAugmented(nonHbNdx) == 0);
//
//		assertFalse(d.isEarly(hbNdx));
//		assertFalse(d.isEarly(nonHbNdx));
//
//		assertFalse(d.isHard(hbNdx));
//		assertFalse(d.isHard(nonHbNdx));
//
//		/*
//    	 * Remove all legs from duty.
//    	 */
//
//		dutyRuleContext.getAggregatorImpl().removeLast(d);
//		dutyRuleContext.getAggregatorImpl().removeLast(d);
//
//		assertTrue(d.getBlockTimeInMins() == 0);
//		assertTrue(d.getNumOfLegs() == 0);
//		assertTrue(d.getBlockTimeInMinsActive() == 0);
//		assertTrue(d.getNumOfLegsActive() == 0);
//		assertTrue(d.getNumOfLegsIntToDom() == 0);
//		assertTrue(d.getNumOfLegsDomToInt() == 0);
//		assertTrue(d.getNumOfCriticalLegs() == 0);
//		assertTrue(d.getNumOfAgDg() == 0);
//		assertTrue(d.getNumOfSpecialFlights() == 0);
//		assertTrue(d.getNumOfAnyHomebaseTouch() == 0);
//		assertTrue(d.getNumOfDomTouch() == 0);
//		assertTrue(d.getNumOfIntTouch() == 0);
//		assertTrue(d.getNumOfAcChanges() == 0);
//		assertTrue(d.getLongConnDiff() == 0);
//
//		/*
//		 * After reset call in removeLastLeg() method, for empty duties this part is commented out.
//		 */
////		assertTrue(d.getBriefDurationInMins(hbNdx) == 90);
////		assertTrue(d.getBriefDurationInMinsNonHb() == 60);
////		assertTrue(d.getDebriefDurationInMins(hbNdx) == 30);
////
////		assertTrue(d.getBriefTime(hbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 8, 30)));
////		assertTrue(d.getBriefTimeNonHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 9, 0)));
////		assertTrue(d.getDebriefTime(hbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 12, 0)));
////
////		assertTrue(d.getBriefDayBeginning(hbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
////		assertTrue(d.getBriefDayBeginningNonHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
////		assertTrue(d.getDebriefDayEnding(hbNdx).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 23, 59, 59)));
////
////		assertTrue(d.getBriefDay(hbNdx).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
////		assertTrue(d.getBriefDayNonHb().isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
////		assertTrue(d.getDebriefDay(hbNdx).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
////
////		assertTrue(d.getDutyDurationInMins(hbNdx) == 210);
////		assertTrue(d.getDutyDurationInMinsNonHb() == 180);
////
////		assertTrue(d.getNumOfDaysTouched(hbNdx) == 1);
////		assertTrue(d.getNumOfDaysTouchedNonHb() == 1);
////
////		assertFalse(d.isEr());
////
////		System.out.println(d.getRestDurationInMinsHbToHb());
////		System.out.println(d.getRestDurationInMinsHbToNonHb());
////		System.out.println(d.getRestDurationInMinsNonHbToHb());
////		System.out.println(d.getRestDurationInMinsNonHbToNonHb());
////
////		assertTrue(d.getRestDurationInMinsHbToHb() == 780);
////		assertTrue(d.getRestDurationInMinsHbToNonHb() == 660);
////		assertTrue(d.getRestDurationInMinsNonHbToHb() == 780);
////		assertTrue(d.getRestDurationInMinsNonHbToNonHb() == 660);
////
////		assertTrue(d.getNextBriefTimeHbToHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 12, 0).plusMinutes(780)));
////		assertTrue(d.getNextBriefTimeHbToNonHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 12, 0).plusMinutes(660)));
////		assertTrue(d.getNextBriefTimeNonHbToHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 12, 0).plusMinutes(780)));
////		assertTrue(d.getNextBriefTimeNonHbToNonHb().isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 12, 0).plusMinutes(660)));
//
//		assertTrue(d.getBriefDurationInMins(hbNdx) == 0);
//		assertTrue(d.getBriefDurationInMins(nonHbNdx) == 0);
//		assertTrue(d.getDebriefDurationInMins(hbNdx) == 0);
//
//		assertTrue(d.getBriefTime(hbNdx) == null);
//		assertTrue(d.getBriefTime(nonHbNdx) == null);
//		assertTrue(d.getDebriefTime(hbNdx) == null);
//
//		assertTrue(d.getBriefDayBeginning(hbNdx) == null);
//		assertTrue(d.getBriefDayBeginning(nonHbNdx) == null);
//		assertTrue(d.getDebriefDayEnding(hbNdx) == null);
//
//		assertTrue(d.getBriefDay(hbNdx) == null);
//		assertTrue(d.getBriefDay(nonHbNdx) == null);
//		assertTrue(d.getDebriefDay(hbNdx) == null);
//
//		assertTrue(d.getDutyDurationInMins(hbNdx) == 0);
//		assertTrue(d.getDutyDurationInMins(nonHbNdx) == 0);
//
//		assertTrue(d.getNumOfDaysTouched(hbNdx) == 0);
//		assertTrue(d.getNumOfDaysTouched(nonHbNdx) == 0);
//
//		assertFalse(d.isEr());
//
//		assertTrue(d.getRestDurationInMins(hbNdx) == 0);
//		assertTrue(d.getRestDurationInMins(nonHbNdx) == 0);
//
//		assertTrue(d.getNextBriefTime(hbNdx) == null);
//		assertTrue(d.getNextBriefTime(nonHbNdx) == null);
//		assertTrue(d.getAugmented(hbNdx) == 0);
//		assertTrue(d.getAugmented(nonHbNdx) == 0);
//
//		assertFalse(d.isEarly(hbNdx));
//		assertFalse(d.isEarly(nonHbNdx));
//
//		assertFalse(d.isHard(hbNdx));
//		assertFalse(d.isHard(nonHbNdx));
//
//		/*
//		 * Regenerate first duty again.
//		 */
//
//		dutyRuleContext.getAggregatorImpl().append(d, legAct11HbToDom);
//		dutyRuleContext.getAggregatorImpl().append(d, legAct12DomToDom);
//		dutyRuleContext.getAggregatorImpl().append(d, legAct13DomToInt);
//
//		/*
//		 * Regenerate second duty again.
//		 */
//
//		Duty d2 = dutyFactory.generateModel();
//		dutyRuleContext.getAggregatorImpl().append(d2, legAct21IntToDom);
//		dutyRuleContext.getAggregatorImpl().append(d2, legAct22DomToHb);
//
//		/*
//		 * Generate pair for test.
//		 */
//		PairFactory pairFactory = new PairFactory(HeurosSystemParam.homebases.length);
//		PairRuleContext pairRuleContext = new PairRuleContext(HeurosSystemParam.homebases.length);
//		PairDutyAggregator pairDutyAggregator = new PairDutyAggregator();
//
//		try {
//			pairRuleContext.registerRule(pairDutyAggregator);
//    	} catch (Exception ex) {
//    		ex.printStackTrace();
//    		assertTrue(false);
//    	}
//
//		Pair p = pairFactory.generateModel();
//		pairRuleContext.getAggregatorImpl().append(p, d);
//		pairRuleContext.getAggregatorImpl().append(p, d2);
//
//		assertTrue(p.getHbNdx() == d.getFirstDepAirport().getHbNdx());
//		assertTrue(p.getHbNdx() == d2.getLastArrAirport().getHbNdx());
//		assertTrue(p.getFirstDuty() == d);
//		assertTrue(p.getLastDuty() == d2);
//		assertTrue(p.getFirstLeg() == legAct11HbToDom);
//		assertTrue(p.getLastLeg() == legAct22DomToHb);
//		assertTrue(p.getFirstDepAirport() == d.getFirstDepAirport());
//		assertTrue(p.getLastArrAirport() == d2.getLastArrAirport());
//		assertTrue(p.getBlockTimeInMins() == 665);
//		assertTrue(p.getBlockTimeInMinsActive() == 665);
//		assertTrue(p.getBlockTimeInMinsPassive() == 0);
//		assertTrue(p.getNumOfLegs() == 5);
//		assertTrue(p.getNumOfLegsActive() == 5);
//		assertTrue(p.getNumOfLegsPassive() == 0);
//		assertTrue(p.getNumOfLegsIntToDom() == 1);
//		assertTrue(p.getNumOfLegsDomToInt() == 1);
//		assertTrue(p.getBriefDurationInMins(hbNdx) == 120);
//		assertTrue(p.getDutyDurationInMins(hbNdx) == 1040);
//		assertTrue(p.getDebriefDurationInMins(hbNdx) == 60);
//		assertTrue(p.getRestDurationInMins(hbNdx) == d.getRestDurationInMins(hbNdx) + d2.getRestDurationInMins(hbNdx));
//		assertTrue(p.getNumOfDaysTouched(hbNdx) == 3);
//		assertTrue(p.getNumOfDuties() == 2);
//		assertTrue(p.getNumOfInternationalDuties() == 1);
//		assertTrue(p.getNumOfEarlyDuties(hbNdx) == 0);
//		assertTrue(p.getNumOfHardDuties(hbNdx) == 1);
//		assertTrue(p.getNumOfAugmentedDuties(hbNdx) == 0);
//		assertTrue(p.getNumOfErDuties() == 0);
//
//		/*
//		 * Remove second duty from pair.
//		 */
//		pairRuleContext.getAggregatorImpl().removeLast(p);
//
//		assertTrue(p.getHbNdx() == d.getFirstDepAirport().getHbNdx());
//		assertTrue(p.getFirstDuty() == d);
//		assertTrue(p.getLastDuty() == d);
//		assertTrue(p.getFirstLeg() == legAct11HbToDom);
//		assertTrue(p.getLastLeg() == legAct13DomToInt);
//		assertTrue(p.getFirstDepAirport() == d.getFirstDepAirport());
//		assertTrue(p.getLastArrAirport() == d.getLastArrAirport());
//		assertTrue(p.getBlockTimeInMins() == 390);
//		assertTrue(p.getBlockTimeInMinsActive() == 390);
//		assertTrue(p.getBlockTimeInMinsPassive() == 0);
//		assertTrue(p.getNumOfLegs() == 3);
//		assertTrue(p.getNumOfLegsActive() == 3);
//		assertTrue(p.getNumOfLegsPassive() == 0);
//		assertTrue(p.getNumOfLegsIntToDom() == 0);
//		assertTrue(p.getNumOfLegsDomToInt() == 1);
//		assertTrue(p.getBriefDurationInMins(hbNdx) == 60);
//		assertTrue(p.getDutyDurationInMins(hbNdx) == 630);
//		assertTrue(p.getDebriefDurationInMins(hbNdx) == 30);
//		assertTrue(p.getRestDurationInMins(hbNdx) == d.getRestDurationInMins(hbNdx));
//		assertTrue(p.getNumOfDaysTouched(hbNdx) == 1);
//		assertTrue(p.getNumOfDuties() == 1);
//		assertTrue(p.getNumOfInternationalDuties() == 1);
//		assertTrue(p.getNumOfEarlyDuties(hbNdx) == 0);
//		assertTrue(p.getNumOfHardDuties(hbNdx) == 0);
//		assertTrue(p.getNumOfAugmentedDuties(hbNdx) == 0);
//		assertTrue(p.getNumOfErDuties() == 0);
//
//		/*
//		 * Remove all duties from pair.
//		 */
//		pairRuleContext.getAggregatorImpl().removeLast(p);
//
//		assertTrue(p.getHbNdx() == -1);
//		assertTrue(p.getFirstDuty() == null);
//		assertTrue(p.getLastDuty() == null);
//		assertTrue(p.getFirstLeg() == null);
//		assertTrue(p.getLastLeg() == null);
//		assertTrue(p.getFirstDepAirport() == null);
//		assertTrue(p.getLastArrAirport() == null);
//		assertTrue(p.getBlockTimeInMins() == 0);
//		assertTrue(p.getBlockTimeInMinsActive() == 0);
//		assertTrue(p.getBlockTimeInMinsPassive() == 0);
//		assertTrue(p.getNumOfLegs() == 0);
//		assertTrue(p.getNumOfLegsActive() == 0);
//		assertTrue(p.getNumOfLegsPassive() == 0);
//		assertTrue(p.getNumOfLegsIntToDom() == 0);
//		assertTrue(p.getNumOfLegsDomToInt() == 0);
//		assertTrue(p.getBriefDurationInMins(hbNdx) == 0);
//		assertTrue(p.getDutyDurationInMins(hbNdx) == 0);
//		assertTrue(p.getDebriefDurationInMins(hbNdx) == 0);
//		assertTrue(p.getRestDurationInMins(hbNdx) == 0);
//		assertTrue(p.getNumOfDaysTouched(hbNdx) == 0);
//		assertTrue(p.getNumOfDuties() == 0);
//		assertTrue(p.getNumOfInternationalDuties() == 0);
//		assertTrue(p.getNumOfEarlyDuties(hbNdx) == 0);
//		assertTrue(p.getNumOfHardDuties(hbNdx) == 0);
//		assertTrue(p.getNumOfAugmentedDuties(hbNdx) == 0);
//		assertTrue(p.getNumOfErDuties() == 0);
//
//		/*
//		 * PairPeriodLength rule test.
//		 */
//		PairPeriodLength pairPeriodLengthRule = new PairPeriodLength();
//		pairRuleContext.getAggregatorImpl().append(p, d);
//		pairRuleContext.getAggregatorImpl().append(p, d2);
//
//		try {
//			pairRuleContext.registerRule(pairPeriodLengthRule);
//    	} catch (Exception ex) {
//    		ex.printStackTrace();
//    		assertTrue(false);
//    	}
//
//		int maxPairingLengthInDays = HeurosSystemParam.maxPairingLengthInDays;
//		assertTrue(pairRuleContext.getTotalizerCheckerProxy().isValid(hbNdx, p));
//		HeurosSystemParam.maxPairingLengthInDays = 3;
//		assertTrue(pairRuleContext.getTotalizerCheckerProxy().isValid(hbNdx, p));
//		HeurosSystemParam.maxPairingLengthInDays = 2;
//		assertFalse(pairRuleContext.getTotalizerCheckerProxy().isValid(hbNdx, p));
//		HeurosSystemParam.maxPairingLengthInDays = maxPairingLengthInDays;
//
//		pairRuleContext.removeRule(pairPeriodLengthRule);
//
//		/*
//		 * PairMaxNumberOfPassiveLegs rule test.
//		 */
//		PairNumOfPassiveLegsLimit pairNumOfPassiveLegsLimit = new PairNumOfPassiveLegsLimit();
//
//		try {
//			dutyRuleContext.registerRule(pairNumOfPassiveLegsLimit);
//			pairRuleContext.registerRule(pairNumOfPassiveLegsLimit);
//    	} catch (Exception ex) {
//    		ex.printStackTrace();
//    		assertTrue(false);
//    	}
//
//		pairRuleContext.getAggregatorImpl().removeLast(p);
//		assertTrue(pairRuleContext.getAppendabilityCheckerProxy().isAppendable(hbNdx, p, d2));
//		pairRuleContext.getAggregatorImpl().append(p, d2);
//		assertTrue(pairRuleContext.getTotalizerCheckerProxy().isValid(hbNdx, p));
//		assertTrue(dutyRuleContext.getConnectionCheckerProxy().areConnectable(hbNdx, d, d2));
//
//		legAct12DomToDom.setCover(false);
//		legAct13DomToInt.setCover(false);
//		legAct21IntToDom.setCover(false);
//		legAct22DomToHb.setCover(false);
//
//		/*
//		 * TODO Here pairing reCalculate method must cover duty methods as well!
//		 */
//		dutyRuleContext.getAggregatorImpl().reCalculate(d);
//		dutyRuleContext.getAggregatorImpl().reCalculate(d2);
//		pairRuleContext.getAggregatorImpl().reCalculate(p);
//
//		assertFalse(pairRuleContext.getTotalizerCheckerProxy().isValid(hbNdx, p));
//		assertFalse(dutyRuleContext.getConnectionCheckerProxy().areConnectable(hbNdx, d, d2));
//		pairRuleContext.getAggregatorImpl().removeLast(p);
//		assertFalse(pairRuleContext.getAppendabilityCheckerProxy().isAppendable(hbNdx, p, d2));
//
//		dutyRuleContext.removeRule(pairNumOfPassiveLegsLimit);
//		pairRuleContext.removeRule(pairNumOfPassiveLegsLimit);
//
//		legAct12DomToDom.setCover(true);
//		legAct13DomToInt.setCover(true);
//		legAct21IntToDom.setCover(true);
//		legAct22DomToHb.setCover(true);
//
//		/*
//		 * TODO Here pairing reCalculate method must cover duty methods as well!
//		 */
//		pairRuleContext.getAggregatorImpl().append(p, d2);
//		dutyRuleContext.getAggregatorImpl().reCalculate(d);
//		dutyRuleContext.getAggregatorImpl().reCalculate(d2);
//		pairRuleContext.getAggregatorImpl().reCalculate(p);
//
//		/*
//		 * Pair is reset. Old values must be valid again!
//		 */
//		assertTrue(p.getHbNdx() == d.getFirstDepAirport().getHbNdx());
//		assertTrue(p.getHbNdx() == d2.getLastArrAirport().getHbNdx());
//		assertTrue(p.getFirstDuty() == d);
//		assertTrue(p.getLastDuty() == d2);
//		assertTrue(p.getFirstLeg() == legAct11HbToDom);
//		assertTrue(p.getLastLeg() == legAct22DomToHb);
//		assertTrue(p.getFirstDepAirport() == d.getFirstDepAirport());
//		assertTrue(p.getLastArrAirport() == d2.getLastArrAirport());
//		assertTrue(p.getBlockTimeInMins() == 665);
//		assertTrue(p.getBlockTimeInMinsActive() == 665);
//		assertTrue(p.getBlockTimeInMinsPassive() == 0);
//		assertTrue(p.getNumOfLegs() == 5);
//		assertTrue(p.getNumOfLegsActive() == 5);
//		assertTrue(p.getNumOfLegsPassive() == 0);
//		assertTrue(p.getNumOfLegsIntToDom() == 1);
//		assertTrue(p.getNumOfLegsDomToInt() == 1);
//		assertTrue(p.getBriefDurationInMins(hbNdx) == 120);
//		assertTrue(p.getDutyDurationInMins(hbNdx) == 1040);
//		assertTrue(p.getDebriefDurationInMins(hbNdx) == 60);
//		assertTrue(p.getRestDurationInMins(hbNdx) == d.getRestDurationInMins(hbNdx) + d2.getRestDurationInMins(hbNdx));
//		assertTrue(p.getNumOfDaysTouched(hbNdx) == 3);
//		assertTrue(p.getNumOfDuties() == 2);
//		assertTrue(p.getNumOfInternationalDuties() == 1);
//		assertTrue(p.getNumOfEarlyDuties(hbNdx) == 0);
//		assertTrue(p.getNumOfHardDuties(hbNdx) == 1);
//		assertTrue(p.getNumOfAugmentedDuties(hbNdx) == 0);
//		assertTrue(p.getNumOfErDuties() == 0);
//
//
////		dutyRuleContext.getAggregatorImpl().append(d, legAct31HbToInt);
////		dutyRuleContext.getAggregatorImpl().append(d, legAct32IntToHb);
////
////		dutyRuleContext.getAggregatorImpl().append(d, legAct41HbToHb);


    }
}
