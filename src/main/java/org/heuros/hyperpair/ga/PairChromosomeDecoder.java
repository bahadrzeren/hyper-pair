package org.heuros.hyperpair.ga;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.heuros.core.data.ndx.OneDimIndexInt;
import org.heuros.core.data.ndx.TwoDimIndexIntXLocalDateTime;
import org.heuros.core.ga.chromosome.Chromosome;
import org.heuros.core.ga.decoder.Decoder;
import org.heuros.data.model.Duty;
import org.heuros.data.model.DutyView;
import org.heuros.data.model.Leg;
import org.heuros.data.model.Pair;
import org.heuros.data.repo.DutyRepository;
import org.heuros.data.repo.LegRepository;
import org.heuros.rule.DutyRuleContext;
import org.heuros.rule.PairRuleContext;

public class PairChromosomeDecoder implements Decoder<Integer, Pair> {

	private static Logger logger = Logger.getLogger(PairChromosomeDecoder.class);

	private LegRepository legRepository = null;
	private DutyRepository dutyRepository = null;
	private DutyRuleContext dutyRuleContext = null;
	private PairRuleContext pairRuleContext = null;

	private OneDimIndexInt<DutyView> hbDepDutyIndexByLegNdx = null;
	private OneDimIndexInt<DutyView> hbDepHbArrDutyIndexByLegNdx = null;
	private TwoDimIndexIntXLocalDateTime<DutyView> dutyIndexByDepAirportNdxBrieftime = null;
	private TwoDimIndexIntXLocalDateTime<DutyView> hbArrDutyIndexByDepAirportNdxBrieftime = null;

	private List<Leg> reOrderedLegs = null;

	public PairChromosomeDecoder setLegRepository(LegRepository legRepository) {
		this.legRepository = legRepository;
		this.reOrderedLegs = this.legRepository.getModels().parallelStream().sorted(new Comparator<Leg>() {
			@Override
			public int compare(Leg a, Leg b) {
				if (a.getSobt().toLocalDate().isBefore(b.getSobt().toLocalDate()))
					return -1;
				else
					if (a.getSobt().toLocalDate().isAfter(b.getSobt().toLocalDate()))
						return 1;
					else
						/*
						 * TODO HB impl will be changed.
						 */
						if (a.getNumOfDutiesIncludesHbDep() < b.getNumOfDutiesIncludesHbDep())
							return -1;
						else
							if (a.getNumOfDutiesIncludesHbDep() > b.getNumOfDutiesIncludesHbDep())
								return 1;
				return 0;
			}
		}).collect(Collectors.toList());
		PairChromosomeDecoder.logger.info("Leg list is reordered according to their number inclusions by HB departed duties.");
		return this;
	}

	public PairChromosomeDecoder setDutyRepository(DutyRepository dutyRepository) {
		this.dutyRepository = dutyRepository;
		return this;
	}

	public PairChromosomeDecoder setDutyRuleContext(DutyRuleContext dutyRuleContext) {
		this.dutyRuleContext = dutyRuleContext;
		return this;
	}

	public PairChromosomeDecoder setPairRuleContext(PairRuleContext pairRuleContext) {
		this.pairRuleContext = pairRuleContext;
		return this;
	}

	public PairChromosomeDecoder setHbDepDutyIndexByLegNdx(OneDimIndexInt<DutyView> hbDepDutyIndexByLegNdx) {
		this.hbDepDutyIndexByLegNdx = hbDepDutyIndexByLegNdx;
		return this;
	}

	public PairChromosomeDecoder setHbDepHbArrDutyIndexByLegNdx(OneDimIndexInt<DutyView> hbDepHbArrDutyIndexByLegNdx) {
		this.hbDepHbArrDutyIndexByLegNdx = hbDepHbArrDutyIndexByLegNdx;
		return this;
	}

	public PairChromosomeDecoder setDutyIndexByDepAirportNdxBrieftime(
			TwoDimIndexIntXLocalDateTime<DutyView> dutyIndexByDepAirportNdxBrieftime) {
		this.dutyIndexByDepAirportNdxBrieftime = dutyIndexByDepAirportNdxBrieftime;
		return this;
	}

	public PairChromosomeDecoder setHbArrDutyIndexByDepAirportNdxBrieftime(
			TwoDimIndexIntXLocalDateTime<DutyView> hbArrDutyIndexByDepAirportNdxBrieftime) {
		this.hbArrDutyIndexByDepAirportNdxBrieftime = hbArrDutyIndexByDepAirportNdxBrieftime;
		return this;
	}

	private int getNextLegNdxToCover(int prevReOrderedLegNdx) {
		for (int i = prevReOrderedLegNdx + 1; i < this.reOrderedLegs.size(); i++) {
			if (this.reOrderedLegs.get(i).isCover()
					&& (this.reOrderedLegs.get(i).getNumOfDutiesIncludesHbDep() > 0))
				return i;
		}
		return Integer.MAX_VALUE;
	}

	/*
	 * TODO HB impl will be changed.
	 */
	private Duty getHbDepDuty(Leg legToCover, int heuristicNo) {
		DutyView[] hbDepDuties = this.hbDepDutyIndexByLegNdx.getArray(legToCover.getNdx());
		System.out.println();
		return null;
	}

	@Override
	public List<Pair> decode(Chromosome<Integer> chromosome) {

		int geneNdx = 0;

		double fitness = Double.MAX_VALUE;
		int reOrderedLegNdx = 0;
		int[] numOfLegCoverings = new int[this.legRepository.getModels().size()];
		int[] numOfCoveringsInDuties = new int[this.dutyRepository.getModels().size()];

		while (true) {
			reOrderedLegNdx = this.getNextLegNdxToCover(reOrderedLegNdx);
			if (reOrderedLegNdx == Integer.MAX_VALUE)
				break;

			Leg legToCover = this.reOrderedLegs.get(reOrderedLegNdx);
			int heuristicNo = chromosome.getGeneValue(geneNdx);
			/*
			 * TODO HB impl will be changed.
			 */
			Duty hbDepDuty = this.getHbDepDuty(legToCover, heuristicNo);
		}

		chromosome.setFitness(fitness);
		return null;
	}

}
