package org.heuros.pair.hyper.ga;

import org.apache.commons.lang3.ArrayUtils;
import org.heuros.core.ga.chromosome.Chromosome;

public class HyperChromosome implements Chromosome<Integer>, Cloneable {

	private int[] genes = null;
	private double fitness = Double.MAX_VALUE;
	private String info = null;

	private int[] histogram = null;

	@Override
	public void initializeChromosome(int chromosomeLength, Integer setSize) {
		this.genes = new int[chromosomeLength];
		this.histogram = new int[setSize];
		for (int i = 0; i < this.genes.length; i++)
			this.genes[i] = -1;
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
		if (this.genes[index] >= 0)
			this.histogram[this.genes[index]]--;
		this.histogram[value]++;
		this.genes[index] = value;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		HyperChromosome newClone = (HyperChromosome) super.clone();
		newClone.genes = this.genes.clone();
		newClone.histogram = this.histogram.clone();
		return newClone;
	}

	public String toString() {
		return ArrayUtils.toString(this.histogram);
	}
}
