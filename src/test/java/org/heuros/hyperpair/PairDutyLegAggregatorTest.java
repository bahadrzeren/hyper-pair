package org.heuros.hyperpair;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import org.heuros.data.model.Duty;
import org.heuros.data.model.Leg;
import org.heuros.data.model.Pair;
import org.heuros.hyperpair.rule.PairNumOfPassiveLegsLimit;
import org.heuros.hyperpair.rule.PairPeriodLength;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Airport Introducer test.
 */
public class PairDutyLegAggregatorTest extends AbsTestBase {

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
    public void runTestProcedure()
    {
    	/*
    	 * Generate leg instances for the test.
    	 */
    	Leg legAct11HbToDom = this.generateLegInstance(101, apIST, apAYT, LocalDateTime.of(2014, Month.JANUARY, 1, 10, 0), LocalDateTime.of(2014, Month.JANUARY, 1, 11, 30), "320");	//	90
    	Leg legAct12DomToDom = this.generateLegInstance(102, apAYT, apEZS, LocalDateTime.of(2014, Month.JANUARY, 1, 13, 0), LocalDateTime.of(2014, Month.JANUARY, 1, 15, 0), "320");	//	120
    	Leg legAct13DomToInt = this.generateLegInstance(103, apEZS, apCDG, LocalDateTime.of(2014, Month.JANUARY, 1, 16, 0), LocalDateTime.of(2014, Month.JANUARY, 1, 19, 0), "320");	//	180
    	Leg legAct21IntToDom = this.generateLegInstance(104, apCDG, apADA, LocalDateTime.of(2014, Month.JANUARY, 2, 20, 30), LocalDateTime.of(2014, Month.JANUARY, 2, 22, 15), "320");	//	105
    	Leg legAct22DomToHb = this.generateLegInstance(105, apADA, apIST, LocalDateTime.of(2014, Month.JANUARY, 2, 23, 0), LocalDateTime.of(2014, Month.JANUARY, 3, 1, 50), "320");		//	170
//    	Leg legAct31HbToInt = this.generateLegInstance(106, apIST, apLHR, LocalDateTime.of(2014, Month.JANUARY, 3, 14, 0), LocalDateTime.of(2014, Month.JANUARY, 3, 15, 50), "320");		//	110
//    	Leg legAct32IntToHb = this.generateLegInstance(107, apLHR, apSAW, LocalDateTime.of(2014, Month.JANUARY, 3, 17, 0), LocalDateTime.of(2014, Month.JANUARY, 3, 20, 10), "320");		//	190
//    	Leg legAct41HbToHb = this.generateLegInstance(108, apIST, apSAW, LocalDateTime.of(2014, Month.JANUARY, 4, 20, 0), LocalDateTime.of(2014, Month.JANUARY, 5, 4, 0), "320");			//	480
//    	Leg legPss11HbToDom = this.generateLegInstance(101, apIST, apAYT, LocalDateTime.of(2014, Month.JANUARY, 1, 10, 0), LocalDateTime.of(2014, Month.JANUARY, 1, 11, 30), "737");
//    	Leg legPss12DomToDom = this.generateLegInstance(102, apAYT, apEZS, LocalDateTime.of(2014, Month.JANUARY, 1, 13, 0), LocalDateTime.of(2014, Month.JANUARY, 1, 15, 0), "737");
//    	Leg legPss13DomToInt = this.generateLegInstance(103, apEZS, apCDG, LocalDateTime.of(2014, Month.JANUARY, 1, 16, 0), LocalDateTime.of(2014, Month.JANUARY, 1, 19, 0), "737");
//    	Leg legPss21IntToDom = this.generateLegInstance(104, apCDG, apADA, LocalDateTime.of(2014, Month.JANUARY, 2, 20, 30), LocalDateTime.of(2014, Month.JANUARY, 2, 22, 15), "737");
//    	Leg legPss22DomToHb = this.generateLegInstance(105, apADA, apIST, LocalDateTime.of(2014, Month.JANUARY, 2, 23, 0), LocalDateTime.of(2014, Month.JANUARY, 3, 1, 50), "737");
//    	Leg legPss31HbToInt = this.generateLegInstance(106, apIST, apLHR, LocalDateTime.of(2014, Month.JANUARY, 3, 14, 0), LocalDateTime.of(2014, Month.JANUARY, 3, 15, 50), "737");
//    	Leg legPss32IntToHb = this.generateLegInstance(107, apLHR, apSAW, LocalDateTime.of(2014, Month.JANUARY, 3, 17, 0), LocalDateTime.of(2014, Month.JANUARY, 3, 20, 10), "737");
//    	Leg legPss41HbToHb = this.generateLegInstance(108, apIST, apSAW, LocalDateTime.of(2014, Month.JANUARY, 4, 20, 0), LocalDateTime.of(2014, Month.JANUARY, 5, 4, 0), "737");

		int hbNdxIST = 0;
//		int hbNdxSAW = 1;

		/*
    	 * Aggregate Legs on duty.
    	 */
		Duty d = this.generateDutyInstance(2);
		dutyRuleContext.getAggregatorProxy().appendFw(d, legAct11HbToDom);
		dutyRuleContext.getAggregatorProxy().appendFw(d, legAct12DomToDom);
		dutyRuleContext.getAggregatorProxy().appendFw(d, legAct13DomToInt);

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

		assertTrue(d.getBriefDurationInMins(hbNdxIST) == 90);	//	90
//		assertTrue(d.getBriefDurationInMins(hbNdxSAW) == 60);
		assertTrue(d.getDebriefDurationInMins(hbNdxIST) == 30);

		assertTrue(d.getBriefTime(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 8, 30)));
//		assertTrue(d.getBriefTime(hbNdxSAW).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 9, 0)));
		assertTrue(d.getDebriefTime(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 19, 30)));

		assertTrue(d.getBriefDayBeginning(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
//		assertTrue(d.getBriefDayBeginning(hbNdxSAW).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
		assertTrue(d.getDebriefDayEnding(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 23, 59, 59)));

		assertTrue(d.getBriefDay(hbNdxIST).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
//		assertTrue(d.getBriefDay(hbNdxSAW).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
		assertTrue(d.getDebriefDay(hbNdxIST).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));

		assertTrue(d.getDutyDurationInMins(hbNdxIST) == 660);
//		assertTrue(d.getDutyDurationInMins(hbNdxSAW) == 630);

		assertTrue(d.getNumOfDaysTouched(hbNdxIST) == 1);
//		assertTrue(d.getNumOfDaysTouched(hbNdxSAW) == 1);

		assertFalse(d.isEr());

//		System.out.println(d.getRestDurationInMinsHbToHb());
//		System.out.println(d.getRestDurationInMinsHbToNonHb());
//		System.out.println(d.getRestDurationInMinsNonHbToHb());
//		System.out.println(d.getRestDurationInMinsNonHbToNonHb());

		assertTrue(d.getRestDurationInMins(hbNdxIST) == 840);
//		assertTrue(d.getRestDurationInMins(hbNdxSAW) == 840);

		assertTrue(d.getNextBriefTime(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 19, 30).plusMinutes(840)));
//		assertTrue(d.getNextBriefTime(hbNdxSAW).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 19, 30).plusMinutes(840)));

		assertTrue(d.getAugmented(hbNdxIST) == 0);
//		assertTrue(d.getAugmented(hbNdxSAW) == 0);

		assertFalse(d.isEarly(hbNdxIST));
//		assertFalse(d.isEarly(hbNdxSAW));

		assertFalse(d.isHard(hbNdxIST));
//		assertFalse(d.isHard(hbNdxSAW));

		/*
    	 * Remove last leg from duty.
    	 */

		dutyRuleContext.getAggregatorProxy().removeLast(d);

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

		assertTrue(d.getBriefDurationInMins(hbNdxIST) == 90);
//		assertTrue(d.getBriefDurationInMins(hbNdxSAW) == 60);
		assertTrue(d.getDebriefDurationInMins(hbNdxIST) == 30);

		assertTrue(d.getBriefTime(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 8, 30)));
//		assertTrue(d.getBriefTime(hbNdxSAW).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 9, 0)));
		assertTrue(d.getDebriefTime(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 15, 30)));

		assertTrue(d.getBriefDayBeginning(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
//		assertTrue(d.getBriefDayBeginning(hbNdxSAW).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
		assertTrue(d.getDebriefDayEnding(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 23, 59, 59)));

		assertTrue(d.getBriefDay(hbNdxIST).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
//		assertTrue(d.getBriefDay(hbNdxSAW).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
		assertTrue(d.getDebriefDay(hbNdxIST).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));

		assertTrue(d.getDutyDurationInMins(hbNdxIST) == 420);
//		assertTrue(d.getDutyDurationInMins(hbNdxSAW) == 390);

		assertTrue(d.getNumOfDaysTouched(hbNdxIST) == 1);
//		assertTrue(d.getNumOfDaysTouched(hbNdxSAW) == 1);

		assertFalse(d.isEr());

//		System.out.println(d.getRestDurationInMinsHbToHb());
//		System.out.println(d.getRestDurationInMinsHbToNonHb());
//		System.out.println(d.getRestDurationInMinsNonHbToHb());
//		System.out.println(d.getRestDurationInMinsNonHbToNonHb());

		assertTrue(d.getRestDurationInMins(hbNdxIST) == 660);
//		assertTrue(d.getRestDurationInMins(hbNdxSAW) == 660);

		assertTrue(d.getNextBriefTime(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 15, 30).plusMinutes(660)));
//		assertTrue(d.getNextBriefTime(hbNdxSAW).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 15, 30).plusMinutes(660)));

		assertTrue(d.getAugmented(hbNdxIST) == 0);
//		assertTrue(d.getAugmented(hbNdxSAW) == 0);

		assertFalse(d.isEarly(hbNdxIST));
//		assertFalse(d.isEarly(hbNdxSAW));

		assertFalse(d.isHard(hbNdxIST));
//		assertFalse(d.isHard(hbNdxSAW));

		/*
    	 * Remove all legs from duty.
    	 */

		dutyRuleContext.getAggregatorProxy().removeLast(d);
		dutyRuleContext.getAggregatorProxy().removeLast(d);

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

		assertTrue(d.getBriefDurationInMins(hbNdxIST) == 0);
//		assertTrue(d.getBriefDurationInMins(hbNdxSAW) == 0);
		assertTrue(d.getDebriefDurationInMins(hbNdxIST) == 0);

		assertTrue(d.getBriefTime(hbNdxIST) == null);
//		assertTrue(d.getBriefTime(hbNdxSAW) == null);
		assertTrue(d.getDebriefTime(hbNdxIST) == null);

		assertTrue(d.getBriefDayBeginning(hbNdxIST) == null);
//		assertTrue(d.getBriefDayBeginning(hbNdxSAW) == null);
		assertTrue(d.getDebriefDayEnding(hbNdxIST) == null);

		assertTrue(d.getBriefDay(hbNdxIST) == null);
//		assertTrue(d.getBriefDay(hbNdxSAW) == null);
		assertTrue(d.getDebriefDay(hbNdxIST) == null);

		assertTrue(d.getDutyDurationInMins(hbNdxIST) == 0);
//		assertTrue(d.getDutyDurationInMins(hbNdxSAW) == 0);

		assertTrue(d.getNumOfDaysTouched(hbNdxIST) == 0);
//		assertTrue(d.getNumOfDaysTouched(hbNdxSAW) == 0);

		assertFalse(d.isEr());

		assertTrue(d.getRestDurationInMins(hbNdxIST) == 0);
//		assertTrue(d.getRestDurationInMins(hbNdxSAW) == 0);

		assertTrue(d.getNextBriefTime(hbNdxIST) == null);
//		assertTrue(d.getNextBriefTime(hbNdxSAW) == null);
		assertTrue(d.getAugmented(hbNdxIST) == 0);
//		assertTrue(d.getAugmented(hbNdxSAW) == 0);

		assertFalse(d.isEarly(hbNdxIST));
//		assertFalse(d.isEarly(hbNdxSAW));

		assertFalse(d.isHard(hbNdxIST));
//		assertFalse(d.isHard(hbNdxSAW));

		/*
		 * Regenerate first duty again.
		 */

		dutyRuleContext.getAggregatorProxy().appendFw(d, legAct11HbToDom);
		dutyRuleContext.getAggregatorProxy().appendFw(d, legAct12DomToDom);
		dutyRuleContext.getAggregatorProxy().appendFw(d, legAct13DomToInt);

		/*
		 * Regenerate second duty again.
		 */

		Duty d2 = this.generateDutyInstance(2);
		dutyRuleContext.getAggregatorProxy().appendFw(d2, legAct21IntToDom);
		dutyRuleContext.getAggregatorProxy().appendFw(d2, legAct22DomToHb);

		/*
		 * Generate pair for test.
		 */
		Pair p = this.generatePairInstance(0);
		pairRuleContext.getAggregatorProxy().appendFw(p, d);
		pairRuleContext.getAggregatorProxy().appendFw(p, d2);

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
		assertTrue(p.getBriefDurationInMins() == 150);
		assertTrue(p.getDutyDurationInMins() == 1070);
		assertTrue(p.getDebriefDurationInMins() == 60);
		assertTrue(p.getRestDurationInMins() == d.getRestDurationInMins(hbNdxIST) + d2.getRestDurationInMins(hbNdxIST));
		assertTrue(p.getNumOfDaysTouched() == 3);
		assertTrue(p.getNumOfDuties() == 2);
		assertTrue(p.getNumOfInternationalDuties() == 1);
		assertTrue(p.getNumOfEarlyDuties() == 0);
		assertTrue(p.getNumOfHardDuties() == 1);
		assertTrue(p.getNumOfAugmentedDuties() == 0);
		assertTrue(p.getNumOfErDuties() == 0);

		/*
		 * Remove second duty from pair.
		 */
		pairRuleContext.getAggregatorProxy().removeLast(p);

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
		assertTrue(p.getBriefDurationInMins() == 90);
		assertTrue(p.getDutyDurationInMins() == 660);
		assertTrue(p.getDebriefDurationInMins() == 30);
		assertTrue(p.getRestDurationInMins() == d.getRestDurationInMins(hbNdxIST));
		assertTrue(p.getNumOfDaysTouched() == 1);
		assertTrue(p.getNumOfDuties() == 1);
		assertTrue(p.getNumOfInternationalDuties() == 1);
		assertTrue(p.getNumOfEarlyDuties() == 0);
		assertTrue(p.getNumOfHardDuties() == 0);
		assertTrue(p.getNumOfAugmentedDuties() == 0);
		assertTrue(p.getNumOfErDuties() == 0);

		/*
		 * Remove all duties from pair.
		 */
		pairRuleContext.getAggregatorProxy().removeLast(p);

		assertTrue(p.getHbNdx() == 0);
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
		assertTrue(p.getBriefDurationInMins() == 0);
		assertTrue(p.getDutyDurationInMins() == 0);
		assertTrue(p.getDebriefDurationInMins() == 0);
		assertTrue(p.getRestDurationInMins() == 0);
		assertTrue(p.getNumOfDaysTouched() == 0);
		assertTrue(p.getNumOfDuties() == 0);
		assertTrue(p.getNumOfInternationalDuties() == 0);
		assertTrue(p.getNumOfEarlyDuties() == 0);
		assertTrue(p.getNumOfHardDuties() == 0);
		assertTrue(p.getNumOfAugmentedDuties() == 0);
		assertTrue(p.getNumOfErDuties() == 0);

		/*
		 * PairPeriodLength rule test.
		 */
		PairPeriodLength pairPeriodLengthRule = new PairPeriodLength();
		pairRuleContext.getAggregatorProxy().appendFw(p, d);
		pairRuleContext.getAggregatorProxy().appendFw(p, d2);

		try {
			pairRuleContext.registerRule(pairPeriodLengthRule);
    	} catch (Exception ex) {
    		ex.printStackTrace();
    		assertTrue(false);
    	}

		int maxPairingLengthInDays = HeurosSystemParam.maxPairingLengthInDays;
		assertTrue(pairRuleContext.getFinalCheckerProxy().acceptable(hbNdxIST, p));
		HeurosSystemParam.maxPairingLengthInDays = 3;
		assertTrue(pairRuleContext.getFinalCheckerProxy().acceptable(hbNdxIST, p));
		HeurosSystemParam.maxPairingLengthInDays = 2;
		assertFalse(pairRuleContext.getFinalCheckerProxy().acceptable(hbNdxIST, p));
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

		pairRuleContext.getAggregatorProxy().removeLast(p);
		assertTrue(pairRuleContext.getAppendabilityCheckerProxy().isAppendable(hbNdxIST, p, d2, true));
		pairRuleContext.getAggregatorProxy().appendFw(p, d2);
		assertTrue(pairRuleContext.getFinalCheckerProxy().acceptable(hbNdxIST, p));
		assertTrue(dutyRuleContext.getConnectionCheckerProxy().areConnectable(hbNdxIST, d, d2));

		legAct12DomToDom.setCover(false);
		legAct13DomToInt.setCover(false);
		legAct21IntToDom.setCover(false);
		legAct22DomToHb.setCover(false);

		/*
		 * TODO Here pairing reCalculate method must cover duty methods as well!
		 */
		dutyRuleContext.getAggregatorProxy().reCalculate(d);
		dutyRuleContext.getAggregatorProxy().reCalculate(d2);
		pairRuleContext.getAggregatorProxy().reCalculate(p);

		assertFalse(pairRuleContext.getFinalCheckerProxy().acceptable(hbNdxIST, p));
		assertFalse(dutyRuleContext.getConnectionCheckerProxy().areConnectable(hbNdxIST, d, d2));
		pairRuleContext.getAggregatorProxy().removeLast(p);
		assertFalse(pairRuleContext.getAppendabilityCheckerProxy().isAppendable(hbNdxIST, p, d2, true));

		dutyRuleContext.removeRule(pairNumOfPassiveLegsLimit);
		pairRuleContext.removeRule(pairNumOfPassiveLegsLimit);

		legAct12DomToDom.setCover(true);
		legAct13DomToInt.setCover(true);
		legAct21IntToDom.setCover(true);
		legAct22DomToHb.setCover(true);

		/*
		 * TODO Here pairing reCalculate method must cover duty methods as well!
		 */
		pairRuleContext.getAggregatorProxy().appendFw(p, d2);
		dutyRuleContext.getAggregatorProxy().reCalculate(d);
		dutyRuleContext.getAggregatorProxy().reCalculate(d2);
		pairRuleContext.getAggregatorProxy().reCalculate(p);

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
		assertTrue(p.getBriefDurationInMins() == 150);
		assertTrue(p.getDutyDurationInMins() == 1070);
		assertTrue(p.getDebriefDurationInMins() == 60);
		assertTrue(p.getRestDurationInMins() == d.getRestDurationInMins(hbNdxIST) + d2.getRestDurationInMins(hbNdxIST));
		assertTrue(p.getNumOfDaysTouched() == 3);
		assertTrue(p.getNumOfDuties() == 2);
		assertTrue(p.getNumOfInternationalDuties() == 1);
		assertTrue(p.getNumOfEarlyDuties() == 0);
		assertTrue(p.getNumOfHardDuties() == 1);
		assertTrue(p.getNumOfAugmentedDuties() == 0);
		assertTrue(p.getNumOfErDuties() == 0);


//		dutyRuleContext.getAggregatorProxy().append(d, legAct31HbToInt);
//		dutyRuleContext.getAggregatorProxy().append(d, legAct32IntToHb);
//
//		dutyRuleContext.getAggregatorProxy().append(d, legAct41HbToHb);


    }
}
