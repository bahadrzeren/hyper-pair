package org.heuros.hyperpair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.heuros.conf.HeurosConfFactory;
import org.heuros.context.PairOptimizationContext;
import org.heuros.core.ga.GeneticIterationListener;
import org.heuros.core.ga.crossover.UniformCrossover;
import org.heuros.core.ga.mutation.IntegerGeneMutator;
import org.heuros.core.ga.selection.BinaryTournamentSelector;
import org.heuros.core.rule.intf.Rule;
import org.heuros.data.model.AirportFactory;
import org.heuros.data.model.Duty;
import org.heuros.data.model.DutyFactory;
import org.heuros.data.model.Leg;
import org.heuros.data.model.LegFactory;
import org.heuros.data.model.PairFactory;
import org.heuros.data.processor.DutyGenerator;
import org.heuros.data.repo.AirportRepository;
import org.heuros.data.repo.DutyRepository;
import org.heuros.data.repo.LegRepository;
import org.heuros.exception.RuleAnnotationIsMissing;
import org.heuros.exception.RuleRegistrationMatchingException;
import org.heuros.hyperpair.ga.PairChromosomeFactory;
import org.heuros.hyperpair.ga.PairChromosomeDecoder;
import org.heuros.hyperpair.ga.PairOptimizer;
import org.heuros.hyperpair.intro.AirportIntroducer;
import org.heuros.hyperpair.intro.DutyLegAggregator;
import org.heuros.hyperpair.intro.LegIntroducer;
import org.heuros.hyperpair.intro.PairDutyAggregator;
import org.heuros.hyperpair.rule.CriticalStations1;
import org.heuros.hyperpair.rule.CriticalStations2;
import org.heuros.hyperpair.rule.DutyACChange;
import org.heuros.hyperpair.rule.DutyAgDgFlights;
import org.heuros.hyperpair.rule.DutyAugmentationCheck;
import org.heuros.hyperpair.rule.DutyEndIfStatIsTouched;
import org.heuros.hyperpair.rule.DutyFlightConnectionTime;
import org.heuros.hyperpair.rule.DutyForceToBeFirstLayoverDuty;
import org.heuros.hyperpair.rule.DutyIntFlights;
import org.heuros.hyperpair.rule.DutyLayover;
import org.heuros.hyperpair.rule.DutyLength;
import org.heuros.hyperpair.rule.DutyMaxBlockTimeForIntLayovers;
import org.heuros.hyperpair.rule.DutyNumOfLegsLimit;
import org.heuros.hyperpair.rule.DutyOneDutyStats;
import org.heuros.hyperpair.rule.DutySpecEuroStats;
import org.heuros.hyperpair.rule.DutySpecialFlightNums1;
import org.heuros.hyperpair.rule.DutySpecialFlightNums2;
import org.heuros.hyperpair.rule.PairDutyDay;
import org.heuros.hyperpair.rule.PairDutyRestCheck;
import org.heuros.hyperpair.rule.PairEarlyDuty;
import org.heuros.hyperpair.rule.PairGrouping;
import org.heuros.hyperpair.rule.PairHardDuty;
import org.heuros.hyperpair.rule.PairIntDuties;
import org.heuros.hyperpair.rule.PairLayoverCheck;
import org.heuros.hyperpair.rule.PairNumOfPassiveLegsLimit;
import org.heuros.hyperpair.rule.PairPeriodLength;
import org.heuros.loader.legs.LegsLoader;
import org.heuros.rule.AirportRuleContext;
import org.heuros.rule.DutyRuleContext;
import org.heuros.rule.LegRuleContext;
import org.heuros.rule.PairRuleContext;

/**
 * The main class that is used to start process.
 * 
 * @author bahadrzeren
 */
public class HyperPair {

	private static Logger logger = Logger.getLogger(HyperPair.class);

	private static List<Rule> rules = new ArrayList<Rule>();

	static {
		HyperPair.rules.add(new AirportIntroducer());
		HyperPair.rules.add(new DutyLegAggregator());
		HyperPair.rules.add(new LegIntroducer());
		HyperPair.rules.add(new PairDutyAggregator());
		HyperPair.rules.add(new CriticalStations1());
		HyperPair.rules.add(new CriticalStations2());
		HyperPair.rules.add(new DutyACChange());
		HyperPair.rules.add(new DutyAgDgFlights());
		HyperPair.rules.add(new DutyAugmentationCheck());
		HyperPair.rules.add(new DutyEndIfStatIsTouched());
		HyperPair.rules.add(new DutyFlightConnectionTime());
		HyperPair.rules.add(new DutyForceToBeFirstLayoverDuty());
		HyperPair.rules.add(new DutyIntFlights());
		HyperPair.rules.add(new DutyLayover());
		HyperPair.rules.add(new DutyLength());
		HyperPair.rules.add(new DutyMaxBlockTimeForIntLayovers());
		HyperPair.rules.add(new DutyNumOfLegsLimit());
		HyperPair.rules.add(new DutyOneDutyStats());
		HyperPair.rules.add(new DutySpecEuroStats());
		HyperPair.rules.add(new DutySpecialFlightNums1());
		HyperPair.rules.add(new DutySpecialFlightNums2());
		HyperPair.rules.add(new PairDutyDay());
		HyperPair.rules.add(new PairDutyRestCheck());
		HyperPair.rules.add(new PairEarlyDuty());
		HyperPair.rules.add(new PairGrouping());
		HyperPair.rules.add(new PairHardDuty());
		HyperPair.rules.add(new PairIntDuties());
		HyperPair.rules.add(new PairLayoverCheck());
		HyperPair.rules.add(new PairNumOfPassiveLegsLimit());
		HyperPair.rules.add(new PairPeriodLength());
	}

	public static void main(String[] args) throws IOException, RuleAnnotationIsMissing, RuleRegistrationMatchingException {
    	/*
    	 * Load configuration file.
    	 */
		String confFileName = null;
		if ((args != null)
				&& (args.length > 0))
			confFileName = args[0];

		HeurosConf conf = new HeurosConfFactory<HeurosConf>()
								.createConfObject(confFileName, HeurosConf.class);

		if (conf != null) {

			/*
			 * Load LEG data from CSV file.
			 */
			List<Leg> legs = new LegsLoader().setLegsFileName(conf.getLegs())
												.setModelFactory(new LegFactory())
												.extractData();

			HeurosDatasetParam.dataPeriodStartInc = legs.get(0).getSobt().withDayOfMonth(1).toLocalDate().plusMonths(1).atStartOfDay();
			logger.info("Data period start: " + HeurosDatasetParam.dataPeriodStartInc);
			HeurosDatasetParam.dataPeriodEndExc = legs.get(legs.size() - 1).getSibt();
			logger.info("Data period end: " + HeurosDatasetParam.dataPeriodEndExc);

			HeurosDatasetParam.optPeriodStartInc = legs.get(0).getSobt().withDayOfMonth(1).toLocalDate().plusMonths(1).atStartOfDay();
			logger.info("Opt period start: " + HeurosDatasetParam.dataPeriodStartInc);
			HeurosDatasetParam.optPeriodEndExc = legs.get(legs.size() - 1).getSibt().withDayOfMonth(1).toLocalDate().atStartOfDay();
			logger.info("Opt period end: " + HeurosDatasetParam.dataPeriodEndExc);

			/*
			 * Generate context.
			 */
			PairOptimizationContext pairOptimizationContext = new PairOptimizationContext().setAirportFactory(new AirportFactory())
																							.setAirportRuleContext(new AirportRuleContext())
																							.setAirportRepository(new AirportRepository())
																							.setLegFactory(new LegFactory())
																							.setLegRuleContext(new LegRuleContext(HeurosSystemParam.homebases.length))
																							.setLegRepository(new LegRepository())
																							.setDutyFactory(new DutyFactory(HeurosSystemParam.homebases.length))
																							.setDutyRuleContext(new DutyRuleContext(HeurosSystemParam.homebases.length))
																							.setDutyRepository(new DutyRepository())
																							.setPairFactory(new PairFactory())
																							.setPairRuleContext(new PairRuleContext(HeurosSystemParam.homebases.length));

			/*
			 * Register rules.
			 */
			pairOptimizationContext.registerRules(HyperPair.rules);

			/*
			 * Add airports and legs to repositories and generate necessary indexes.
			 */
			pairOptimizationContext.registerAirportsAndLegs(legs, HeurosDatasetParam.dataPeriodStartInc, HeurosSystemParam.maxLegConnectionTimeInMins);

			/*
			 * Generate duties.
			 */
			DutyGenerator dutyGenerator = new DutyGenerator().setDutyFactory(pairOptimizationContext.getDutyFactory())
																.setDutyRuleContext(pairOptimizationContext.getDutyRuleContext())
																.setLegConnectionIndex(pairOptimizationContext.getConnectionLegsIndex())
																.setLegRepository(pairOptimizationContext.getLegRepository());
			List<Duty> duties = dutyGenerator.proceed();

//			/*
//			 * Revalidate duties!
//			 */
//			duties.forEach((d) -> {
//				if (!RuleUtil.lazyDutyValidator.validateDuty(d,
//																pairOptimizationContext.getLegRuleContext(),
//																pairOptimizationContext.getDutyRuleContext()))
//					logger.error("Duty " + d + " is not valid!");
//			});

			/*
			 * Add duties to dutyRepository and generate necessary indexes.
			 */
			pairOptimizationContext.registerDuties(duties);

			PairChromosomeDecoder pairChromosomeDecoder = new PairChromosomeDecoder().setPairFactory(pairOptimizationContext.getPairFactory())
																						.setLegRepository(pairOptimizationContext.getLegRepository())
																						.setDutyRepository(pairOptimizationContext.getDutyRepository())
																						.setPairRuleContext(pairOptimizationContext.getPairRuleContext())
																						.setDutyIndexByLegNdx(pairOptimizationContext.getDutyIndexByLegNdx())
																						.setHbDepDutyIndexByLegNdx(pairOptimizationContext.getHbDepDutyIndexByLegNdx())
																						.setHbDepHbArrDutyIndexByLegNdx(pairOptimizationContext.getHbDepHbArrDutyIndexByLegNdx())
																						.setDutyIndexByDepAirportNdxBrieftime(pairOptimizationContext.getDutyIndexByDepAirportNdxBrieftime())
																						.setHbArrDutyIndexByDepAirportNdxBrieftime(pairOptimizationContext.getHbArrDutyIndexByDepAirportNdxBrieftime());

			PairOptimizer pairOptimizer = (PairOptimizer) new PairOptimizer().setAllowDublicateChromosomes(HeurosGaParameters.allowDublicateChromosomes)
																				.setMaxElapsedTimeInNanoSecs(HeurosGaParameters.maxElapsedTimeInNanoSecs)
																				.setMaxNumOfIterations(HeurosGaParameters.maxNumOfIterations)
																				.setMaxNumOfIterationsWOProgress(HeurosGaParameters.maxNumOfIterationsWOProgress)
																				.setMinNumOfChildren(HeurosGaParameters.minNumOfChildren)
																				.setMutationRate(HeurosGaParameters.mutationRate)
																				.setNumOfEliteChromosomes(HeurosGaParameters.numOfEliteChromosomes)
																				.setPopulationSize(HeurosGaParameters.populationSize)
																				.setSelector(new BinaryTournamentSelector<Integer>())
																				.setCrossoverOperator(new UniformCrossover<Integer>())
																				.setGeneticIterationListener(new GeneticIterationListener() {
																					@Override
																					public void onProgress(int iteration, double elapsedTime, String info) {
																						HyperPair.logger.info(iteration + ".th itearation, elapsedTime: " +
																												elapsedTime + ", best: " + info);
																					}
																					@Override
																					public void onException(Exception ex) {
																						ex.printStackTrace();
																						HyperPair.logger.error(ex);
																					}
																				})
																				.setDecoder(pairChromosomeDecoder)
																				.setMutator(new IntegerGeneMutator().setMaxGeneValueExc(pairChromosomeDecoder.getNumOfHeuristics()))
																				/*
																				 * TODO Chromosome length = 6000 must be parametric.
																				 */
																				.setChromosomeFactory(new PairChromosomeFactory().setChromosomeLength(10000)
																																	.setMaxGeneValue(pairChromosomeDecoder.getNumOfHeuristics()));

//			List<Pair> solution = 
					pairOptimizer.proceed();
System.out.println();


			/*
			 * Prepare and run the optimizer.
			 */
//			Processor<Duty, PairView> optimizer = new HyperPairOptimizer();
//			List<PairView> pairs = optimizer.proceed(dutyRepo);

			/*
			 * Report solution.
			 */
			
		}
    }
}
