package org.heuros.pair.intro;

import java.time.temporal.ChronoUnit;

import org.heuros.core.rule.intf.Aggregator;
import org.heuros.core.rule.intf.RuleImplementation;
import org.heuros.data.model.DutyView;
import org.heuros.data.model.Pair;

@RuleImplementation(ruleName = "Pair duty aggregator", 
					violationMessage = "Pair duty aggregator failed", 
					description = "Duty aggregator for pairs.")
public class PairDutyAggregator implements Aggregator<Pair, DutyView> {

	@Override
	public void appendFw(Pair p, DutyView d) {
		p.appendFw(d);
		this.softAppendFw(p, d);
	}

	@Override
	public void appendBw(Pair p, DutyView d) {
		p.appendBw(d);
		this.softAppendBw(p, d);
	}

	@Override
	public void softAppendFw(Pair p, DutyView d) {
		incTotalizers(p, d, 1);
	}

	@Override
	public void softAppendBw(Pair p, DutyView d) {
		incTotalizers(p, d, 1);
	}

	@Override
	public DutyView removeLast(Pair p) {
		DutyView d = p.removeLast();
		if (d == null)
			return null;

		this.remove(p, d);

		return d;
	}

	@Override
	public DutyView removeFirst(Pair p) {
		DutyView d = p.removeFirst();
		if (d == null)
			return null;

		this.remove(p, d);

		return d;
	}

	private void remove(Pair p, DutyView d) {
		incTotalizers(p, d, -1);
	}

	@Override
	public void removeAll(Pair p) {
		while (this.removeFirst(p) != null) {
		}
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

//		for (int i = 0; i < HeurosSystemParam.homebases.length; i++)
			p.setNumOfDaysTouched((int) ChronoUnit.DAYS.between(p.getFirstDuty().getBriefDay(p.getHbNdx()), 
																p.getLastDuty().getDebriefDay(p.getHbNdx())) + 1);

//		for (int i = 0; i < HeurosSystemParam.homebases.length; i++) {
			if ((p.getNumOfDuties() == 1) && (incAmount > 0)) {
				p.incBriefDurationInMins(incAmount * d.getBriefDurationInMins(p.getHbNdx()));
				p.incDutyDurationInMins(incAmount * d.getDutyDurationInMins(p.getHbNdx()));
				p.incRestDurationInMins(incAmount * d.getRestDurationInMins(p.getHbNdx()));
				if (d.isEarly(p.getHbNdx()))
					p.incNumOfEarlyDuties(incAmount);
				if (d.isHard(p.getHbNdx()))
					p.incNumOfHardDuties(incAmount);
				if (d.getAugmented(p.getHbNdx()) > 0)
					p.incNumOfAugmentedDuties(incAmount);
			} else {
				p.incBriefDurationInMins(incAmount * d.getBriefDurationInMins(p.getHbNdx()));
				p.incDutyDurationInMins(incAmount * d.getDutyDurationInMins(p.getHbNdx()));
				p.incRestDurationInMins(incAmount * d.getRestDurationInMins(p.getHbNdx()));
				if (d.isEarly(p.getHbNdx()))
					p.incNumOfEarlyDuties(incAmount);
				if (d.isHard(p.getHbNdx()))
					p.incNumOfHardDuties(incAmount);
				if (d.getAugmented(p.getHbNdx()) > 0)
					p.incNumOfAugmentedDuties(incAmount);
			}
			p.incDebriefDurationInMins(incAmount * d.getDebriefDurationInMins(p.getHbNdx()));
//		}

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
//		for (int i = 0; i < HeurosSystemParam.homebases.length; i++) {
			p.setBriefDurationInMins(0);
			p.setDutyDurationInMins(0);
			p.setRestDurationInMins(0);
			p.setNumOfDaysTouched(0);
			p.setNumOfEarlyDuties(0);
			p.setNumOfHardDuties(0);
			p.setNumOfAugmentedDuties(0);
			p.setDebriefDurationInMins(0);
//		}
		p.setNumOfInternationalDuties(0);
		p.setNumOfErDuties(0);
	}

	@Override
	public boolean reCalculate(Pair p) {
		if (p.isComplete()) {
			this.reset(p);
			p.getDuties().forEach((d) -> this.softAppendFw(p, d));
			return true;
		}
		return false;
	}
}
