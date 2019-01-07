package org.heuros.pair.hyper.ga;

import java.util.Random;

import org.heuros.core.ga.chromosome.ChromosomeFactory;

public class HyperChromoFactory implements ChromosomeFactory<Integer> {

	private static Random random = new Random();

	private int chromosomeLength = 0;
	private int setSize = 0;

	@Override
	public HyperChromoFactory setChromosomeLength(int value) {
		this.chromosomeLength = value;
		return this;
	}

	public HyperChromoFactory setSetSize(int value) {
		this.setSize = value;
		return this;
	}

	@Override
	public int getChromosomeLength() {
		return this.chromosomeLength;
	}

	@Override
	public HyperChromosome createChromosome() {
		HyperChromosome newChromosome = new HyperChromosome();
		newChromosome.initializeChromosome(this.chromosomeLength, this.setSize);
		for (int i = 0; i < this.chromosomeLength; i++)
			newChromosome.setGeneValue(i, random.nextInt(this.setSize));
		return newChromosome;
	}

}
