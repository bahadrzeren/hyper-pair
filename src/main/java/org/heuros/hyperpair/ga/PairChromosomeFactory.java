package org.heuros.hyperpair.ga;

import java.util.Random;

import org.heuros.core.ga.chromosome.ChromosomeFactory;

public class PairChromosomeFactory implements ChromosomeFactory<Integer> {

	private static Random random = new Random();

	private int chromosomeLength = 0;
	private int maxGeneValue = 0;

	@Override
	public PairChromosomeFactory setChromosomeLength(int value) {
		this.chromosomeLength = value;
		return this;
	}

	public PairChromosomeFactory setMaxGeneValue(int value) {
		this.maxGeneValue = value;
		return this;
	}

	@Override
	public int getChromosomeLength() {
		return this.chromosomeLength;
	}

	@Override
	public PairChromosome createChromosome() {
		PairChromosome newChromosome = new PairChromosome();
		newChromosome.initializeChromosome(this.chromosomeLength);
		for (int i = 0; i < this.chromosomeLength; i++)
			newChromosome.setGeneValue(i, random.nextInt(this.maxGeneValue));
		return newChromosome;
	}

}
