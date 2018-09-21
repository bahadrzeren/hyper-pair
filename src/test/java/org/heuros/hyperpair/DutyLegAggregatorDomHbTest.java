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
public class DutyLegAggregatorDomHbTest extends AbsTestBase {

	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DutyLegAggregatorDomHbTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( DutyLegAggregatorDomHbTest.class );
    }

    /**
     * Test Duty Leg append/remove.
     */
    public void runTestProcedure()
    {
		/*
		 * DOM departed, HB arrival 1 Leg Duty.
		 */
    	Leg l1 = this.generateLegInstance(101, apAYT, apIST,
    										LocalDateTime.of(2014, Month.JANUARY, 1, 12, 30), 
    										LocalDateTime.of(2014, Month.JANUARY, 1, 14, 0), "320");	//	90

    	Duty d = this.generateDutyInstance(2, l1);

		assertTrue(d.getBlockTimeInMins() == 90);
		assertTrue(d.getNumOfLegs() == 1);
		assertTrue(d.getBlockTimeInMinsActive() == 90);
		assertTrue(d.getNumOfLegsActive() == 1);
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

		assertTrue(d.getBriefDurationInMins(hbNdxIST) == 60);
		assertTrue(d.getBriefDurationInMins(hbNdxSAW) == 60);
		assertTrue(d.getDebriefDurationInMins(hbNdxIST) == 30);

		assertTrue(d.getBriefTime(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 11, 30)));
		assertTrue(d.getBriefTime(hbNdxSAW).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 11, 30)));
		assertTrue(d.getDebriefTime(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 14, 30)));

		assertTrue(d.getBriefDayBeginning(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
		assertTrue(d.getBriefDayBeginning(hbNdxSAW).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
		assertTrue(d.getDebriefDayEnding(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 23, 59, 59)));

		assertTrue(d.getBriefDay(hbNdxIST).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
		assertTrue(d.getBriefDay(hbNdxSAW).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
		assertTrue(d.getDebriefDay(hbNdxIST).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));

		assertTrue(d.getDutyDurationInMins(hbNdxIST) == 180);
		assertTrue(d.getDutyDurationInMins(hbNdxSAW) == 180);

		assertTrue(d.getNumOfDaysTouched(hbNdxIST) == 1);
		assertTrue(d.getNumOfDaysTouched(hbNdxSAW) == 1);

		assertFalse(d.isEr());

		assertTrue(d.getRestDurationInMins(hbNdxIST) == 780);
		assertTrue(d.getRestDurationInMins(hbNdxSAW) == 660);

		assertTrue(d.getNextBriefTime(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 14, 30).plusMinutes(780)));
		assertTrue(d.getNextBriefTime(hbNdxSAW).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 14, 30).plusMinutes(660)));

		assertTrue(d.getAugmented(hbNdxIST) == 0);
		assertTrue(d.getAugmented(hbNdxSAW) == 0);

		assertFalse(d.isEarly(hbNdxIST));
		assertFalse(d.isEarly(hbNdxSAW));

		assertFalse(d.isHard(hbNdxIST));
		assertFalse(d.isHard(hbNdxSAW));

		/*
		 * Add HB to INT Leg.
		 */
		Leg l2 = this.generateLegInstance(1710, apIST, apFRA, 
											LocalDateTime.of(2014, Month.JANUARY, 1, 11, 0), 
											LocalDateTime.of(2014, Month.JANUARY, 1, 14, 0), "320");	//	180
		this.dutyRuleContext.getAggregatorProxy().appendFw(d, l2);

		assertTrue(d.getBlockTimeInMins() == 270);
		assertTrue(d.getNumOfLegs() == 2);
		assertTrue(d.getBlockTimeInMinsActive() == 270);
		assertTrue(d.getNumOfLegsActive() == 2);
		assertTrue(d.getNumOfLegsIntToDom() == 0);
		assertTrue(d.getNumOfLegsDomToInt() == 1);
		assertTrue(d.getNumOfCriticalLegs() == 0);
		assertTrue(d.getNumOfAgDg() == 0);
		assertTrue(d.getNumOfSpecialFlights() == 1);
		assertTrue(d.getNumOfAnyHomebaseTouch() == 1);
		assertTrue(d.getNumOfDomTouch() == 1);
		assertTrue(d.getNumOfIntTouch() == 0);
		assertTrue(d.getNumOfAcChanges() == 0);
		assertTrue(d.getLongConnDiff() == 0);

		assertTrue(d.getBriefDurationInMins(hbNdxIST) == 60);
		assertTrue(d.getBriefDurationInMins(hbNdxSAW) == 60);
		assertTrue(d.getDebriefDurationInMins(hbNdxIST) == 30);

		assertTrue(d.getBriefTime(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 11, 30)));
		assertTrue(d.getBriefTime(hbNdxSAW).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 11, 30)));
		assertTrue(d.getDebriefTime(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 14, 30)));

		assertTrue(d.getBriefDayBeginning(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
		assertTrue(d.getBriefDayBeginning(hbNdxSAW).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
		assertTrue(d.getDebriefDayEnding(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 23, 59, 59)));

		assertTrue(d.getBriefDay(hbNdxIST).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
		assertTrue(d.getBriefDay(hbNdxSAW).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
		assertTrue(d.getDebriefDay(hbNdxIST).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));

		assertTrue(d.getDutyDurationInMins(hbNdxIST) == 180);
		assertTrue(d.getDutyDurationInMins(hbNdxSAW) == 180);

		assertTrue(d.getNumOfDaysTouched(hbNdxIST) == 1);
		assertTrue(d.getNumOfDaysTouched(hbNdxSAW) == 1);

		assertFalse(d.isEr());

		assertTrue(d.getRestDurationInMins(hbNdxIST) == 660);
		assertTrue(d.getRestDurationInMins(hbNdxSAW) == 660);

		assertTrue(d.getNextBriefTime(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 14, 30).plusMinutes(660)));
		assertTrue(d.getNextBriefTime(hbNdxSAW).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 14, 30).plusMinutes(660)));

		assertTrue(d.getAugmented(hbNdxIST) == 0);
		assertTrue(d.getAugmented(hbNdxSAW) == 0);

		assertFalse(d.isEarly(hbNdxIST));
		assertFalse(d.isEarly(hbNdxSAW));

		assertFalse(d.isHard(hbNdxIST));
		assertFalse(d.isHard(hbNdxSAW));

    }
}
