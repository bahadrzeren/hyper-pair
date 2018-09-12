package org.heuros.hyperpair.intro;

import java.time.temporal.ChronoUnit;

import org.heuros.core.rule.intf.Aggregator;
import org.heuros.core.rule.intf.RuleImplementation;
import org.heuros.data.model.DutyView;
import org.heuros.data.model.Pair;
import org.heuros.hyperpair.HeurosSystemParam;

@RuleImplementation(ruleName = "Pair duty aggregator", 
					violationMessage = "Pair duty aggregator failed", 
					description = "Duty aggregator for pairs.")
public class PairDutyAggregator implements Aggregator<Pair, DutyView> {

	@Override
	public void append(Pair p, DutyView d) {
		p.append(d);
		this.softAppend(p, d);
	}

	@Override
	public void softAppend(Pair p, DutyView d) {
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

		p.incBlockTimeInMins(incAmount * d.getBlockTimeInMins());
		p.incBlockTimeInMinsActive(incAmount * d.getBlockTimeInMinsActive());
		p.incBlockTimeInMinsPassive(incAmount * d.getBlockTimeInMinsPassive());
		p.incNumOfLegs(incAmount * d.getNumOfLegs());
		p.incNumOfLegsActive(incAmount * d.getNumOfLegsActive());
		p.incNumOfLegsPassive(incAmount * d.getNumOfLegsPassive());
		p.incNumOfLegsIntToDom(incAmount * d.getNumOfLegsIntToDom());
		p.incNumOfLegsDomToInt(incAmount * d.getNumOfLegsDomToInt());

		for (int i = 0; i < HeurosSystemParam.homebases.length; i++)
			p.setNumOfDaysTouched(i, (int) ChronoUnit.DAYS.between(p.getFirstDuty().getBriefDay(i), p.getLastDuty().getDebriefDay(i)) + 1);

		for (int i = 0; i < HeurosSystemParam.homebases.length; i++) {
			if ((p.getNumOfDuties() == 1) && (incAmount > 0)) {
				p.incBriefDurationInMins(i, incAmount * d.getBriefDurationInMins(i));
				p.incDutyDurationInMins(i, incAmount * d.getDutyDurationInMins(i));
				p.incRestDurationInMins(i, incAmount * d.getRestDurationInMins(i));
				if (d.isEarly(i))
					p.incNumOfEarlyDuties(i, incAmount);
				if (d.isHard(i))
					p.incNumOfHardDuties(i, incAmount);
				if (d.getAugmented(i) > 0)
					p.incNumOfAugmentedDuties(i, incAmount);
			} else {
				p.incBriefDurationInMins(i, incAmount * d.getBriefDurationInMins(i));
				p.incDutyDurationInMins(i, incAmount * d.getDutyDurationInMins(i));
				p.incRestDurationInMins(i, incAmount * d.getRestDurationInMins(i));
				if (d.isEarly(i))
					p.incNumOfEarlyDuties(i, incAmount);
				if (d.isHard(i))
					p.incNumOfHardDuties(i, incAmount);
				if (d.getAugmented(i) > 0)
					p.incNumOfAugmentedDuties(i, incAmount);
			}
			p.incDebriefDurationInMins(i, incAmount * d.getDebriefDurationInMins(i));
		}

		if (d.isInternational())
			p.incNumOfInternationalDuties(incAmount);
		if (d.isEr())
			p.incNumOfErDuties(incAmount);
	}

	@Override
	public void reset(Pair p) {
		p.setNumOfDuties(0);
		p.setBlockTimeInMins(0);
		p.setBlockTimeInMinsActive(0);
		p.setBlockTimeInMinsPassive(0);
		p.setNumOfLegs(0);
		p.setNumOfLegsActive(0);
		p.setNumOfLegsPassive(0);
		p.setNumOfLegsIntToDom(0);
		p.setNumOfLegsDomToInt(0);
		for (int i = 0; i < HeurosSystemParam.homebases.length; i++) {
			p.setBriefDurationInMins(i, 0);
			p.setDutyDurationInMins(i, 0);
			p.setRestDurationInMins(i, 0);
			p.setNumOfDaysTouched(i, 0);
			p.setNumOfEarlyDuties(i, 0);
			p.setNumOfHardDuties(i, 0);
			p.setNumOfAugmentedDuties(i, 0);
			p.setDebriefDurationInMins(i, 0);
		}
		p.setNumOfInternationalDuties(0);
		p.setNumOfErDuties(0);
	}

	@Override
	public boolean reCalculate(Pair p) {
		if (p.isComplete()) {
			this.reset(p);
			p.getDuties().forEach((d) -> this.softAppend(p, d));
			return true;
		}
		return false;
	}
}
