package org.heuros.hyperpair.ga;

import java.util.Random;

import org.heuros.core.ga.chromosome.ChromosomeFactory;

public class PairChromosomeFactory implements ChromosomeFactory<Integer> {

	private static Random random = new Random();

	private int chromosomeLength = 0;
	private int setSize = 0;

	@Override
	public PairChromosomeFactory setChromosomeLength(int value) {
		this.chromosomeLength = value;
		return this;
	}

	public PairChromosomeFactory setSetSize(int value) {
		this.setSize = value;
		return this;
	}

	@Override
	public int getChromosomeLength() {
		return this.chromosomeLength;
	}

	@Override
	public PairChromosome createChromosome() {
		PairChromosome newChromosome = new PairChromosome();
		newChromosome.initializeChromosome(this.chromosomeLength, this.setSize);
		for (int i = 0; i < this.chromosomeLength; i++)
			newChromosome.setGeneValue(i, random.nextInt(this.setSize));
		return newChromosome;
	}

}
