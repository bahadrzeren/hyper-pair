package org.heuros.hyperpair.intro;

import java.time.temporal.ChronoUnit;

import org.heuros.core.rule.inf.AbstractRule;
import org.heuros.core.rule.inf.Aggregator;
import org.heuros.core.rule.inf.RuleImplementation;
import org.heuros.data.model.DutyView;
import org.heuros.data.model.Pair;

@RuleImplementation(ruleName = "Pair duty aggregator", 
					violationMessage = "Pair duty aggregator failed", 
					description = "Duty aggregator for pairs.")
public class PairDutyAggregator extends AbstractRule implements Aggregator<Pair, DutyView> {

	@Override
	public void append(Pair p, DutyView d) {
		p.append(d);
		this.appendInternal(p, d);
	}

	private void appendInternal(Pair p, DutyView d) {
		incTotalizers(p, d, 1);
	}

	@Override
	public DutyView removeLast(Pair p) {
		DutyView d = p.removeLast();
		if (d == null)
			return null;

		this.removeLast(p, d);

		return d;
	}

	private void removeLast(Pair p, DutyView d) {
		incTotalizers(p, d, -1);
	}

	public void incTotalizers(Pair p, DutyView d, int incAmount) {
		p.incNumOfDuties(incAmount);

		if (p.getNumOfDuties() == 0) {
			this.reset(p);
			return;
		}

		if (p.getNumOfDuties() == 1)
			p.setHomeBase(p.getFirstLeg().getDepAirport());

		p.incBlockTimeInMins(incAmount * d.getBlockTimeInMins());
		p.incBlockTimeInMinsActive(incAmount * d.getBlockTimeInMinsActive());
		p.incBlockTimeInMinsPassive(incAmount * d.getBlockTimeInMinsPassive());
		p.incNumOfLegs(incAmount * d.getNumOfLegs());
		p.incNumOfLegsActive(incAmount * d.getNumOfLegsActive());
		p.incNumOfLegsPassive(incAmount * d.getNumOfLegsPassive());
		p.incNumOfLegsIntToDom(incAmount * d.getNumOfLegsIntToDom());
		p.incNumOfLegsDomToInt(incAmount * d.getNumOfLegsDomToInt());

		p.setNumOfDaysTouched((int) ChronoUnit.DAYS.between(p.getFirstDuty().getBriefDayHb(), p.getLastDuty().getDebriefDay()) + 1);

		if ((p.getNumOfDuties() == 1) && (incAmount > 0)) {
			p.incBriefDurationInMins(incAmount * d.getBriefDurationInMinsHb());
			p.incDutyDurationInMins(incAmount * d.getDutyDurationInMinsHb());
			if (p.getFirstDepAirport() == d.getLastArrAirport())
				p.incRestDurationInMins(incAmount * d.getRestDurationInMinsHbToHb());
			else
				p.incRestDurationInMins(incAmount * d.getRestDurationInMinsHbToNonHb());
			if (d.isEarlyHb())
				p.incNumOfEarlyDuties(incAmount);
			if (d.isHardHb())
				p.incNumOfHardDuties(incAmount);
			if (d.getAugmentedHb() > 0)
				p.incNumOfAugmentedDuties(incAmount);
		} else {
			p.incBriefDurationInMins(incAmount * d.getBriefDurationInMinsNonHb());
			p.incDutyDurationInMins(incAmount * d.getDutyDurationInMinsNonHb());
			if (p.getFirstDepAirport() == d.getLastArrAirport())
				p.incRestDurationInMins(incAmount * d.getRestDurationInMinsNonHbToHb());
			else
				p.incRestDurationInMins(incAmount * d.getRestDurationInMinsNonHbToNonHb());
			if (d.isEarlyNonHb())
				p.incNumOfEarlyDuties(incAmount);
			if (d.isHardNonHb())
				p.incNumOfHardDuties(incAmount);
			if (d.getAugmentedNonHb() > 0)
				p.incNumOfAugmentedDuties(incAmount);
		}

		p.incDebriefDurationInMins(incAmount * d.getDebriefDurationInMins());

		if (d.isInternational())
			p.incNumOfInternationalDuties(incAmount);
		if (d.isEr())
			p.incNumOfErDuties(incAmount);
	}

	@Override
	public void reCalculate(Pair p) {
		this.reset(p);
		p.getDuties().forEach((d) -> this.appendInternal(p, d));
	}

	@Override
	public void reset(Pair p) {
		p.setNumOfDuties(0);
		p.setHomeBase(null);
		p.setBlockTimeInMins(0);
		p.setBlockTimeInMinsActive(0);
		p.setBlockTimeInMinsPassive(0);
		p.setNumOfLegs(0);
		p.setNumOfLegsActive(0);
		p.setNumOfLegsPassive(0);
		p.setNumOfLegsIntToDom(0);
		p.setNumOfLegsDomToInt(0);
		p.setBriefDurationInMins(0);
		p.setDutyDurationInMins(0);
		p.setRestDurationInMins(0);
		p.setNumOfDaysTouched(0);
		p.setNumOfEarlyDuties(0);
		p.setNumOfHardDuties(0);
		p.setNumOfAugmentedDuties(0);
		p.setDebriefDurationInMins(0);
		p.setNumOfInternationalDuties(0);
		p.setNumOfErDuties(0);
	}

}
