package org.heuros.hyperpair;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import org.heuros.data.model.Duty;
import org.heuros.data.model.Leg;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Airport Introducer test.
 */
public class DutyLegAggregatorIntDomTest extends AbsTestBase {

	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DutyLegAggregatorIntDomTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( DutyLegAggregatorIntDomTest.class );
    }

    /**
     * Test Duty Leg append/remove.
     */
    public void runTestProcedure()
    {
		/*
		 * INT departed, DOM arrival 1 Leg Duty.
		 */
    	Leg l1 = this.generateLegInstance(101, apCDG, apVAN,
    										LocalDateTime.of(2014, Month.JANUARY, 1, 14, 40), 
    										LocalDateTime.of(2014, Month.JANUARY, 1, 19, 0), "320");	//	260

    	Duty d = this.generateDutyInstance(2, l1);

		assertTrue(d.getBlockTimeInMins() == 260);
		assertTrue(d.getNumOfLegs() == 1);
		assertTrue(d.getBlockTimeInMinsActive() == 260);
		assertTrue(d.getNumOfLegsActive() == 1);
		assertTrue(d.getNumOfLegsIntToDom() == 1);
		assertTrue(d.getNumOfLegsDomToInt() == 0);
		assertTrue(d.getNumOfCriticalLegs() == 1);
		assertTrue(d.getNumOfAgDg() == 0);
		assertTrue(d.getNumOfSpecialFlights() == 0);
		assertTrue(d.getNumOfAnyHomebaseTouch() == 0);
		assertTrue(d.getNumOfDomTouch() == 0);
		assertTrue(d.getNumOfIntTouch() == 0);
		assertTrue(d.getNumOfAcChanges() == 0);
		assertTrue(d.getLongConnDiff() == 0);

		assertTrue(d.getBriefDurationInMins(hbNdxIST) == 60);
//		assertTrue(d.getBriefDurationInMins(hbNdxSAW) == 60);
		assertTrue(d.getDebriefDurationInMins(hbNdxIST) == 30);

		assertTrue(d.getBriefTime(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 13, 40)));
//		assertTrue(d.getBriefTime(hbNdxSAW).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 13, 40)));
		assertTrue(d.getDebriefTime(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 19, 30)));

		assertTrue(d.getBriefDayBeginning(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
//		assertTrue(d.getBriefDayBeginning(hbNdxSAW).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
		assertTrue(d.getDebriefDayEnding(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 23, 59, 59)));

		assertTrue(d.getBriefDay(hbNdxIST).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
//		assertTrue(d.getBriefDay(hbNdxSAW).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
		assertTrue(d.getDebriefDay(hbNdxIST).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));

		assertTrue(d.getDutyDurationInMins(hbNdxIST) == 350);
//		assertTrue(d.getDutyDurationInMins(hbNdxSAW) == 350);

		assertTrue(d.getNumOfDaysTouched(hbNdxIST) == 1);
//		assertTrue(d.getNumOfDaysTouched(hbNdxSAW) == 1);

		assertFalse(d.isEr());

		assertTrue(d.getRestDurationInMins(hbNdxIST) == 660);
//		assertTrue(d.getRestDurationInMins(hbNdxSAW) == 660);

		assertTrue(d.getNextBriefTime(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 19, 30).plusMinutes(660)));
//		assertTrue(d.getNextBriefTime(hbNdxSAW).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 19, 30).plusMinutes(660)));

		assertTrue(d.getAugmented(hbNdxIST) == 0);
//		assertTrue(d.getAugmented(hbNdxSAW) == 0);

		assertFalse(d.isEarly(hbNdxIST));
//		assertFalse(d.isEarly(hbNdxSAW));

		assertFalse(d.isHard(hbNdxIST));
//		assertFalse(d.isHard(hbNdxSAW));

		/*
		 * INT departed, DOM arrival 1 Leg Duty.
		 */
		Leg l2 = this.generateLegInstance(101, apAYT, apIST, 
											LocalDateTime.of(2014, Month.JANUARY, 1, 20, 30), 
											LocalDateTime.of(2014, Month.JANUARY, 1, 22, 0), "320");	//	90
		this.dutyRuleContext.getAggregatorProxy().appendFw(d, l2);

		assertTrue(d.getBlockTimeInMins() == 350);
		assertTrue(d.getNumOfLegs() == 2);
		assertTrue(d.getBlockTimeInMinsActive() == 350);
		assertTrue(d.getNumOfLegsActive() == 2);
		assertTrue(d.getNumOfLegsIntToDom() == 1);
		assertTrue(d.getNumOfLegsDomToInt() == 0);
		assertTrue(d.getNumOfCriticalLegs() == 1);
		assertTrue(d.getNumOfAgDg() == 0);
		assertTrue(d.getNumOfSpecialFlights() == 0);
		assertTrue(d.getNumOfAnyHomebaseTouch() == 0);
		assertTrue(d.getNumOfDomTouch() == 1);
		assertTrue(d.getNumOfIntTouch() == 0);
		assertTrue(d.getNumOfAcChanges() == 0);
		assertTrue(d.getLongConnDiff() == 0);

		assertTrue(d.getBriefDurationInMins(hbNdxIST) == 60);
//		assertTrue(d.getBriefDurationInMins(hbNdxSAW) == 60);
		assertTrue(d.getDebriefDurationInMins(hbNdxIST) == 30);

		assertTrue(d.getBriefTime(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 13, 40)));
//		assertTrue(d.getBriefTime(hbNdxSAW).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 13, 40)));
		assertTrue(d.getDebriefTime(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 22, 30)));

		assertTrue(d.getBriefDayBeginning(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
//		assertTrue(d.getBriefDayBeginning(hbNdxSAW).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
		assertTrue(d.getDebriefDayEnding(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 23, 59, 59)));

		assertTrue(d.getBriefDay(hbNdxIST).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
//		assertTrue(d.getBriefDay(hbNdxSAW).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
		assertTrue(d.getDebriefDay(hbNdxIST).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));

		assertTrue(d.getDutyDurationInMins(hbNdxIST) == 530);
//		assertTrue(d.getDutyDurationInMins(hbNdxSAW) == 530);

		assertTrue(d.getNumOfDaysTouched(hbNdxIST) == 1);
//		assertTrue(d.getNumOfDaysTouched(hbNdxSAW) == 1);

		assertFalse(d.isEr());

		assertTrue(d.getRestDurationInMins(hbNdxIST) == 780);
//		assertTrue(d.getRestDurationInMins(hbNdxSAW) == 760);

		assertTrue(d.getNextBriefTime(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 22, 30).plusMinutes(780)));
//		assertTrue(d.getNextBriefTime(hbNdxSAW).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 22, 30).plusMinutes(760)));

		assertTrue(d.getAugmented(hbNdxIST) == 0);
//		assertTrue(d.getAugmented(hbNdxSAW) == 0);

		assertFalse(d.isEarly(hbNdxIST));
//		assertFalse(d.isEarly(hbNdxSAW));

		assertFalse(d.isHard(hbNdxIST));
//		assertFalse(d.isHard(hbNdxSAW));
    }
}
