package org.heuros.hyperpair;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import org.heuros.data.model.Duty;
import org.heuros.data.model.Leg;
import org.heuros.util.test.HeurosDutyTestUtil;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Airport Introducer test.
 */
public class DutyLegAggregatorHbErTest extends DutyLegAggregatorTest {

	/**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DutyLegAggregatorHbErTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( DutyLegAggregatorHbErTest.class );
    }

    /**
     * Test Duty Leg append/remove.
     */
    public void runTestProcedure()
    {
		/*
		 * HB departed, ER 1 Leg Duty.
		 */
    	final Leg[] lHbDepEr = {dailyLegs[0].getHb1_Hb1ToEr2_1()};	//	LocalDateTime.of(2014, Month.JANUARY, 1, 9, 0), LocalDateTime.of(2014, Month.JANUARY, 1, 20, 0), "320"));	//	IST-ER	660

    	Duty d = HeurosDutyTestUtil.generateDutyInstance(lHbDepEr);

		assertTrue(d.getBlockTimeInMins() == 660);
		assertTrue(d.getNumOfLegs() == 1);
		assertTrue(d.getBlockTimeInMinsActive() == 660);
		assertTrue(d.getNumOfLegsActive() == 1);
		assertTrue(d.getNumOfLegsIntToDom() == 0);
		assertTrue(d.getNumOfLegsDomToInt() == 1);
		assertTrue(d.getNumOfCriticalLegs() == 0);
		assertTrue(d.getNumOfAgDg() == 0);
		assertTrue(d.getNumOfSpecialFlights() == 0);
		assertTrue(d.getNumOfAnyHomebaseTouch() == 0);
		assertTrue(d.getNumOfDomTouch() == 0);
		assertTrue(d.getNumOfIntTouch() == 0);
		assertTrue(d.getNumOfAcChanges() == 0);
		assertTrue(d.getLongConnDiff() == 0);

		assertTrue(d.getBriefDurationInMins(hbNdxIST) == 90);		//	90
		assertTrue(d.getBriefDurationInMins(hbNdxSAW) == 60);
		assertTrue(d.getDebriefDurationInMins(hbNdxIST) == 30);

		assertTrue(d.getBriefTime(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 7, 30)));		//	90
		assertTrue(d.getBriefTime(hbNdxSAW).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 8, 0)));
		assertTrue(d.getDebriefTime(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 20, 30)));

		assertTrue(d.getBriefDayBeginning(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
		assertTrue(d.getBriefDayBeginning(hbNdxSAW).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
		assertTrue(d.getDebriefDayEnding(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 23, 59, 59)));

		assertTrue(d.getBriefDay(hbNdxIST).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
		assertTrue(d.getBriefDay(hbNdxSAW).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
		assertTrue(d.getDebriefDay(hbNdxIST).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));

		assertTrue(d.getDutyDurationInMins(hbNdxIST) == 780);		//	90
		assertTrue(d.getDutyDurationInMins(hbNdxSAW) == 750);

		assertTrue(d.getNumOfDaysTouched(hbNdxIST) == 1);
		assertTrue(d.getNumOfDaysTouched(hbNdxSAW) == 1);

		assertTrue(d.isEr());

		assertTrue(d.getRestDurationInMins(hbNdxIST) == (37 * 60));
		assertTrue(d.getRestDurationInMins(hbNdxSAW) == (37 * 60));

		assertTrue(d.getNextBriefTime(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 20, 30).plusMinutes(37 * 60)));
		assertTrue(d.getNextBriefTime(hbNdxSAW).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 20, 30).plusMinutes(37 * 60)));

		assertTrue(d.getAugmented(hbNdxIST) == 0);
		assertTrue(d.getAugmented(hbNdxSAW) == 0);

		assertFalse(d.isEarly(hbNdxIST));
		assertFalse(d.isEarly(hbNdxSAW));

		assertFalse(d.isHard(hbNdxIST));
		assertFalse(d.isHard(hbNdxSAW));

		/*
		 * Add ER to HB leg.
		 */
		this.dutyRuleContext.getAggregatorProxy().append(d, dailyLegs[0].getHb1_Er2ToHb1_1());	//	LocalDateTime.of(2014, Month.JANUARY, 1, 21, 0), LocalDateTime.of(2014, Month.JANUARY, 2, 9, 0), "320"));	//	ER-IST	720

		assertTrue(d.getBlockTimeInMins() == 1380);
		assertTrue(d.getNumOfLegs() == 2);
		assertTrue(d.getBlockTimeInMinsActive() == 1380);
		assertTrue(d.getNumOfLegsActive() == 2);
		assertTrue(d.getNumOfLegsIntToDom() == 1);
		assertTrue(d.getNumOfLegsDomToInt() == 1);
		assertTrue(d.getNumOfCriticalLegs() == 0);
		assertTrue(d.getNumOfAgDg() == 0);
		assertTrue(d.getNumOfSpecialFlights() == 0);
		assertTrue(d.getNumOfAnyHomebaseTouch() == 0);
		assertTrue(d.getNumOfDomTouch() == 0);
		assertTrue(d.getNumOfIntTouch() == 1);
		assertTrue(d.getNumOfAcChanges() == 0);
		assertTrue(d.getLongConnDiff() == 0);

		assertTrue(d.getBriefDurationInMins(hbNdxIST) == 90);		//	90
		assertTrue(d.getBriefDurationInMins(hbNdxSAW) == 60);
		assertTrue(d.getDebriefDurationInMins(hbNdxIST) == 30);

		assertTrue(d.getBriefTime(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 7, 30)));		//	90
		assertTrue(d.getBriefTime(hbNdxSAW).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 8, 0)));
		assertTrue(d.getDebriefTime(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 2, 9, 30)));

		assertTrue(d.getBriefDayBeginning(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
		assertTrue(d.getBriefDayBeginning(hbNdxSAW).isEqual(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0)));
		assertTrue(d.getDebriefDayEnding(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 2, 23, 59, 59)));

		assertTrue(d.getBriefDay(hbNdxIST).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
		assertTrue(d.getBriefDay(hbNdxSAW).isEqual(LocalDate.of(2014, Month.JANUARY, 1)));
		assertTrue(d.getDebriefDay(hbNdxIST).isEqual(LocalDate.of(2014, Month.JANUARY, 2)));

		assertTrue(d.getDutyDurationInMins(hbNdxIST) == 26 * 60);		//	90
		assertTrue(d.getDutyDurationInMins(hbNdxSAW) == 25 * 60 + 30);

		assertTrue(d.getNumOfDaysTouched(hbNdxIST) == 2);
		assertTrue(d.getNumOfDaysTouched(hbNdxSAW) == 2);

		assertTrue(d.isEr());

		assertTrue(d.getRestDurationInMins(hbNdxIST) == 2820);
		assertTrue(d.getRestDurationInMins(hbNdxSAW) == 2820);

		assertTrue(d.getNextBriefTime(hbNdxIST).isEqual(LocalDateTime.of(2014, Month.JANUARY, 2, 9, 30).plusMinutes(2820)));
		assertTrue(d.getNextBriefTime(hbNdxSAW).isEqual(LocalDateTime.of(2014, Month.JANUARY, 2, 9, 30).plusMinutes(2820)));

		assertTrue(d.getAugmented(hbNdxIST) == 2);
		assertTrue(d.getAugmented(hbNdxSAW) == 2);

		assertFalse(d.isEarly(hbNdxIST));
		assertFalse(d.isEarly(hbNdxSAW));

		assertTrue(d.isHard(hbNdxIST));
		assertTrue(d.isHard(hbNdxSAW));

    }
}
