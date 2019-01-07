package org.heuros.pair.ga.ga;

import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;
import org.heuros.core.ga.chromosome.ChromosomeFactory;

public class PairChromosomeFactory implements ChromosomeFactory<Integer> {

	private static Random random = new Random();

	private int chromosomeLength = 0;

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
	public PairChromosome createChromosome() {
		int[] randArry = new int[this.chromosomeLength];
		for (int i = 0; i < this.chromosomeLength; i++)
			randArry[i] = i;

		PairChromosome newChromosome = new PairChromosome();
		newChromosome.initializeChromosome(this.chromosomeLength, this.chromosomeLength);
		int i = 0;
		while (randArry.length > 0) {
			int randNdx = random.nextInt(randArry.length);
			newChromosome.setGeneValue(i, randArry[randNdx]);
			randArry = ArrayUtils.remove(randArry, randNdx);
			i++;
		}
		return newChromosome;
	}

}
