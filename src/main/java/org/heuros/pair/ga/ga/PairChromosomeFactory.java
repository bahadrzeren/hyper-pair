package org.heuros.pair.ga.ga;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import org.heuros.context.PairOptimizationContext;
import org.heuros.core.ga.chromosome.ChromosomeFactory;
import org.heuros.data.model.Leg;
import org.heuros.pair.conf.HeurosAlgParameters;

public class PairChromosomeFactory implements ChromosomeFactory<Integer>, Comparator<Leg> {

	private static Random random = new Random();
	private int chromosomeLength = 0;

	private ArrayList<Leg> legs = null;

	public PairChromosomeFactory(PairOptimizationContext pairOptimizationContext) {
		this.legs = (ArrayList<Leg>) pairOptimizationContext.getLegRepository().getModels();
	}

	@Override
	public PairChromosomeFactory setChromosomeLength(int value) {
		this.chromosomeLength = value;
		return this;
	}

	@Override
	public int getChromosomeLength() {
		return this.chromosomeLength;
	}

	@Override
	public int compare(Leg o1, Leg o2) {
		if (o1.getNumOfIncludingPairsWoDh() < o2.getNumOfIncludingPairsWoDh())
			return -1;
		else
			if (o1.getNumOfIncludingPairsWoDh() > o2.getNumOfIncludingPairsWoDh())
				return 1;
			else
				return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public PairChromosome createChromosome() {
		ArrayList<Leg> randLegs = (ArrayList<Leg>) this.legs.clone();

		randLegs.sort(this);

		PairChromosome newChromosome = new PairChromosome();
		newChromosome.initializeChromosome(this.chromosomeLength, this.chromosomeLength);
		int i = 0;
		while (randLegs.size() > 0) {
			int randNdx = random.nextInt(HeurosAlgParameters.minNumOfChildren);
			if (randNdx >= randLegs.size())
				randNdx = random.nextInt(randLegs.size());
			newChromosome.setGeneValue(i, randLegs.get(randNdx).getNdx());
			randLegs.remove(randNdx);
			i++;
		}
		return newChromosome;
	}
}
