package org.heuros.pair.ga.ga;

import org.heuros.core.ga.ISolutionCost;
import org.heuros.core.ga.chromosome.Chromosome;

public class PairChromosome implements Chromosome<Integer>, Cloneable {

	private int[] genes = null;
	private int[] pos = null;
	private ISolutionCost fitness = null;
	private String info = null;

	@Override
	public void initializeChromosome(int chromosomeLength, Integer setSize) {
		this.genes = new int[chromosomeLength];
		this.pos = new int[chromosomeLength];
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
	public void setFitness(ISolutionCost value) {
		this.fitness = value;
	}

	@Override
	public ISolutionCost getFitness() {
		return this.fitness;
	}

	@Override
    public String getInfo() {
    	return this.info;
    }

	@Override
    public void setInfo(String value) {
    	this.info = value;
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
		this.pos[value] = index;
	}

	public Integer getPosValue(int value) {
		return this.pos[value];
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		PairChromosome newClone = (PairChromosome) super.clone();
		newClone.genes = this.genes.clone();
		newClone.pos = this.pos.clone();
		return newClone;
	}

	public String toString() {
		return String.valueOf(this.fitness);
	}
}
