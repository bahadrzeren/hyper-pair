package org.heuros.hyperpair.ga;

import org.heuros.core.ga.chromosome.Chromosome;

public class PairChromosome implements Chromosome<Integer> {

	private int[] genes = null;
	private double fitness = Double.MAX_VALUE;

	@Override
	public void initializeChromosome(int chromosomeLength) {
		this.genes = new int[chromosomeLength];
	}

	@Override
	public boolean isEqual(Chromosome<Integer> cand) {
		if (this.fitness != cand.getFitness())
			return false;
		else
			for (int i = 0; i < this.genes.length; i++)
				if (this.genes[i] != cand.getGeneValue(i))
					return false;
		return true;
	}

	@Override
	public void setFitness(double value) {
		this.fitness = value;
	}

	@Override
	public double getFitness() {
		return this.fitness;
	}

	@Override
	public int getChromosomeLength() {
		return this.genes.length;
	}

	@Override
	public Integer getGeneValue(int index) {
		return this.genes[index];
	}

	@Override
	public void setGeneValue(int index, Integer value) {
		this.genes[index] = value;
	}

	public Object clone() throws CloneNotSupportedException {
		PairChromosome newClone = (PairChromosome) this.clone();
		newClone.genes = this.genes.clone();
		return newClone;
	}
}
