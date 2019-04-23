package org.heuros.pair.ga;

import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.heuros.core.ga.GeneticIterationListener;
import org.heuros.core.ga.chromosome.Chromosome;
import org.heuros.core.ga.mutation.SwapGeneMutator;
import org.heuros.core.ga.selection.BinaryTournamentSelector;
import org.heuros.exception.RuleAnnotationIsMissing;
import org.heuros.exception.RuleRegistrationMatchingException;
import org.heuros.pair.AbsPairingOptimizer;
import org.heuros.pair.conf.HeurosGaParameters;
import org.heuros.pair.ga.ga.CycleCrossover;
import org.heuros.pair.ga.ga.PairChromosomeDecoder;
import org.heuros.pair.ga.ga.PairChromosomeFactory;
import org.heuros.pair.ga.ga.PairOptimizer;
import org.heuros.pair.sp.PairingGenerator;

/**
 * The main class that is used to start process.
 * 
 * @author bahadrzeren
 */
public class GaPair extends AbsPairingOptimizer {

	private static Logger logger = Logger.getLogger(GaPair.class);

	public static void main(String[] args) throws RuleAnnotationIsMissing, RuleRegistrationMatchingException, InterruptedException, ExecutionException, CloneNotSupportedException {
		GaPair optimizer = new GaPair();
		optimizer.runTheOptimizer(args);
	}

	@Override
	public void doOptimize() throws InterruptedException, ExecutionException, CloneNotSupportedException {

		PairingGenerator pairingGenerator = new PairingGenerator(pairOptimizationContext, pricingNetwork);

		PairChromosomeDecoder pairChromosomeDecoder = new PairChromosomeDecoder().setLegRepository(pairOptimizationContext.getLegRepository())
																					.setDutyRepository(pairOptimizationContext.getDutyRepository())
																					.setDutyIndexByLegNdx(pairOptimizationContext.getDutyIndexByLegNdx())
																					.setPairingGenerator(pairingGenerator);

		PairOptimizer pairOptimizer = (PairOptimizer) new PairOptimizer().setAllowDublicateChromosomes(HeurosGaParameters.allowDublicateChromosomes)
																			.setMaxElapsedTimeInNanoSecs(HeurosGaParameters.maxElapsedTimeInNanoSecs)
																			.setMaxNumOfIterations(HeurosGaParameters.maxNumOfIterations)
																			.setMaxNumOfIterationsWOProgress(HeurosGaParameters.maxNumOfIterationsWOProgress)
																			.setMinNumOfChildren(HeurosGaParameters.minNumOfChildren)
																			.setMutationRate(HeurosGaParameters.mutationRate)
																			.setNumOfEliteChromosomes(HeurosGaParameters.numOfEliteChromosomes)
																			.setPopulationSize(HeurosGaParameters.populationSize)
																			.setSelector(new BinaryTournamentSelector<Integer>())
																			.setCrossoverOperator(new CycleCrossover<Integer>())
																			.setRunParallel(true)
																			.setGeneticIterationListener(new GeneticIterationListener<Integer>() {
																				@Override
																				public void onProgress(int iteration, double elapsedTime, Chromosome<Integer> best) {
																					GaPair.logger.info("Progress!!! at " + iteration + ".th iteration, elapsedTime: " +
																											elapsedTime + ", best: " + String.valueOf(best.getInfo()));
																				}
																				@Override
																				public void onIterate(int iteration, double elapsedTime, Chromosome<Integer> best) {
																					GaPair.logger.info(iteration + ".th itearation, elapsedTime: " +
																											elapsedTime + ", best: " + String.valueOf(best.getInfo()));
																				}
																				@Override
																				public void onException(Exception ex) {
																					ex.printStackTrace();
																					GaPair.logger.error(ex);
																				}
																			})
																			.setDecoder(pairChromosomeDecoder)
																			.setMutator(new SwapGeneMutator())
																			/*
																			 * TODO Chromosome length = 10000 must be parametric.
																			 */
																			.setChromosomeFactory(new PairChromosomeFactory().
																										setChromosomeLength(pairOptimizationContext.getLegRepository().getModels().size()));
		pairOptimizer.proceed();
		logger.info("Optimization run is completed!");
    }
}
