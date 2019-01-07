package org.heuros.pair.ga.ga;

import org.heuros.core.ga.chromosome.Chromosome;
import org.heuros.core.ga.crossover.Crossover;

/**
 * Uniform crossover implementation class.
 * 
 * @author bahadrzeren
 * 
 * @param <T> Type of the class which is used to represent one single gene.
 */
public class CycleCrossover<T> implements Crossover<T> {

	@SuppressWarnings("unchecked")
	@Override
    public int crossover(Chromosome<T> population[],
                            int startingChildIndex,
                            Chromosome<T> mother,
                            Chromosome<T> father,
                            double worstFitness) throws CloneNotSupportedException {
        int res = startingChildIndex;

        PairChromosome motherC = (PairChromosome) mother;
        PairChromosome fatherC = (PairChromosome) father;
        PairChromosome childM = (PairChromosome) mother.clone();
        PairChromosome childF = (PairChromosome) father.clone();

        boolean[] cgfM = new boolean[motherC.getChromosomeLength()];
        boolean[] cgfF = new boolean[fatherC.getChromosomeLength()];

        int ndxM = 0;
        int ndxF = 0;
        int valueM = 0;
        int valueF = 0;
        while (!cgfM[ndxM]) {
            cgfM[ndxM] = true;
            cgfF[ndxF] = true;
            valueM = fatherC.getGeneValue(ndxM);
            valueF = motherC.getGeneValue(ndxF);
            ndxM = motherC.getPosValue(valueM);
            ndxF = fatherC.getPosValue(valueF);
        }

        for (int i = 0; i < motherC.getChromosomeLength(); i++) {
            if (!cgfM[i]) {
            	childM.setGeneValue(i, fatherC.getGeneValue(i));
            	childF.setGeneValue(i, motherC.getGeneValue(i));
cgfM[i] = true;
cgfF[i] = true;
            }
        }

for (boolean b : cgfM) {
	if (!b)
		System.out.println();
}

for (boolean b : cgfF) {
	if (!b)
		System.out.println();
}

cgfM = new boolean[motherC.getChromosomeLength()];
cgfF = new boolean[fatherC.getChromosomeLength()];

for (int i = 0; i < motherC.getChromosomeLength(); i++) {
	cgfM[childM.getGeneValue(i)] = true;
	cgfF[childF.getGeneValue(i)] = true;
}

for (boolean b : cgfM) {
	if (!b)
		System.out.println();
}

for (boolean b : cgfF) {
	if (!b)
		System.out.println();
}

		population[res] = (Chromosome<T>) childM;
        res++;
        population[res] = (Chromosome<T>) childF;
        res++;
        return res;
    }
}
