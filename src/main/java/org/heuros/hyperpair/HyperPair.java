package org.heuros.hyperpair;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.heuros.conf.HeurosConfFactory;
import org.heuros.context.PairOptimizationContext;
import org.heuros.core.ga.GeneticIterationListener;
import org.heuros.core.ga.chromosome.Chromosome;
import org.heuros.core.ga.crossover.UniformCrossover;
import org.heuros.core.ga.mutation.IntegerGeneMutator;
import org.heuros.core.ga.selection.BinaryTournamentSelector;
import org.heuros.core.rule.intf.Rule;
import org.heuros.data.PairingPricingNetwork;
import org.heuros.data.model.Duty;
import org.heuros.data.model.Leg;
import org.heuros.data.processor.BiDirDutyPairingChecker;
import org.heuros.data.processor.BiDirLegPairingChecker;
import org.heuros.data.processor.DutyGenerator;
import org.heuros.data.repo.AirportRepository;
import org.heuros.data.repo.DutyRepository;
import org.heuros.data.repo.LegRepository;
import org.heuros.exception.RuleAnnotationIsMissing;
import org.heuros.exception.RuleRegistrationMatchingException;
import org.heuros.hyperpair.ga.PairChromosomeFactory;
import org.heuros.hyperpair.ga.PairChromosomeDecoder;
import org.heuros.hyperpair.ga.PairOptimizer;
import org.heuros.hyperpair.heuristic.PairingGenerator;
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

	private static List<Rule> rules = new ArrayList<Rule>();

	static {
		DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss", Locale.ENGLISH).withZone(ZoneOffset.UTC);
		System.setProperty("logfile.date.time", datetimeFormatter.format(LocalDateTime.now()));

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

	private static Logger logger = Logger.getLogger(HyperPair.class);

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
												.setNumOfBases(HeurosSystemParam.homebases.length)
												.extractData();

			HeurosDatasetParam.dataPeriodStartInc = legs.get(0).getSobt().withDayOfMonth(1).toLocalDate().plusMonths(1).atStartOfDay();
			logger.info("Data period start: " + HeurosDatasetParam.dataPeriodStartInc);
			HeurosDatasetParam.dataPeriodEndExc = legs.get(legs.size() - 1).getSibt();
			logger.info("Data period end: " + HeurosDatasetParam.dataPeriodEndExc);

			HeurosDatasetParam.optPeriodStartInc = legs.get(0).getSobt().withDayOfMonth(1).toLocalDate().plusMonths(1).atStartOfDay();
			logger.info("Opt period start: " + HeurosDatasetParam.optPeriodStartInc);
			HeurosDatasetParam.optPeriodEndExc = legs.get(legs.size() - 1).getSibt().withDayOfMonth(1).toLocalDate().atStartOfDay();
			if (HeurosDatasetParam.optPeriodEndExc.isEqual(HeurosDatasetParam.optPeriodStartInc))
					HeurosDatasetParam.optPeriodEndExc = HeurosDatasetParam.dataPeriodEndExc.minusDays(2 + 2 * HeurosSystemParam.maxPairingLengthInDays).toLocalDate().atStartOfDay();
			logger.info("Opt period end: " + HeurosDatasetParam.optPeriodEndExc);
			HeurosDatasetParam.legCoverPeriodEndExc = HeurosDatasetParam.optPeriodEndExc.plusDays(HeurosSystemParam.maxPairingLengthInDays);
			logger.info("Leg cover period end: " + HeurosDatasetParam.legCoverPeriodEndExc);
			HeurosDatasetParam.dutyProcessPeriodEndExc = HeurosDatasetParam.legCoverPeriodEndExc.plusDays(HeurosSystemParam.maxPairingLengthInDays - 1);
			logger.info("Duty process period end: " + HeurosDatasetParam.dutyProcessPeriodEndExc);

			/*
			 * Generate context.
			 */
			PairOptimizationContext pairOptimizationContext = new PairOptimizationContext().setAirportRuleContext(new AirportRuleContext())
																							.setAirportRepository(new AirportRepository())
																							.setLegRuleContext(new LegRuleContext(HeurosSystemParam.homebases.length))
																							.setLegRepository(new LegRepository())
																							.setDutyRuleContext(new DutyRuleContext(HeurosSystemParam.homebases.length))
																							.setDutyRepository(new DutyRepository())
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
			DutyGenerator dutyGenerator = new DutyGenerator().setDutyRuleContext(pairOptimizationContext.getDutyRuleContext())
																.setLegConnectionIndex(pairOptimizationContext.getConnectionLegsIndex())
																.setLegRepository(pairOptimizationContext.getLegRepository())
																.setNumOfBases(HeurosSystemParam.homebases.length);
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
			pairOptimizationContext.registerDuties(duties, HeurosSystemParam.homebases.length);

			ExecutorService executorService = Executors.newFixedThreadPool(HeurosSystemParam.homebases.length * 2);

			List<Future<Boolean>> pairGenCalls = new ArrayList<Future<Boolean>>(HeurosSystemParam.homebases.length);

			for (int hbNdx = 0; hbNdx < HeurosSystemParam.homebases.length; hbNdx++) {

//			UniDirDutyPairingChecker pairChecker = new UniDirDutyPairingChecker(hbNdx).setMaxPairingLengthInHours(HeurosSystemParam.maxPairingLengthInDays * 24)
//																	.setMaxIdleTimeInAPairInHours(HeurosSystemParam.maxIdleTimeInAPairInHours)
//																	.setDutyRepository(pairOptimizationContext.getDutyRepository())
//																	.setDutyRuleContext(pairOptimizationContext.getDutyRuleContext())
//																	.setPairRuleContext(pairOptimizationContext.getPairRuleContext())
//																	.setDutyIndexByDepAirportNdxBrieftime(pairOptimizationContext.getDutyIndexByDepAirportNdxBrieftime());

				BiDirDutyPairingChecker dutyPairChecker = new BiDirDutyPairingChecker(hbNdx, HeurosDatasetParam.dutyProcessPeriodEndExc)
																	.setMaxPairingLengthInHours(HeurosSystemParam.maxPairingLengthInDays * 24)
																	.setMaxIdleTimeInAPairInHours(HeurosSystemParam.maxIdleTimeInAPairInHours)
																	.setDutyRepository(pairOptimizationContext.getDutyRepository())
																	.setDutyRuleContext(pairOptimizationContext.getDutyRuleContext())
																	.setPairRuleContext(pairOptimizationContext.getPairRuleContext())
																	.setDutyIndexByDepAirportNdxBrieftime(pairOptimizationContext.getDutyIndexByDepAirportNdxBrieftime())
																	.setDutyIndexByArrAirportNdxNextBrieftime(pairOptimizationContext.getDutyIndexByArrAirportNdxNextBrieftime());
				pairGenCalls.add(executorService.submit(dutyPairChecker));

				BiDirLegPairingChecker legPairChecker = new BiDirLegPairingChecker(hbNdx, HeurosDatasetParam.legCoverPeriodEndExc)
																	.setMaxPairingLengthInHours(HeurosSystemParam.maxPairingLengthInDays * 24)
																	.setMaxIdleTimeInAPairInHours(HeurosSystemParam.maxIdleTimeInAPairInHours)
																	.setLegRepository(pairOptimizationContext.getLegRepository())
																	.setDutyRepository(pairOptimizationContext.getDutyRepository())
																	.setDutyRuleContext(pairOptimizationContext.getDutyRuleContext())
																	.setPairRuleContext(pairOptimizationContext.getPairRuleContext())
																	.setDutyIndexByLegNdx(pairOptimizationContext.getDutyIndexByLegNdx())
																	.setDutyIndexByDepAirportNdxBrieftime(pairOptimizationContext.getDutyIndexByDepAirportNdxBrieftime())
																	.setDutyIndexByArrAirportNdxNextBrieftime(pairOptimizationContext.getDutyIndexByArrAirportNdxNextBrieftime());
				pairGenCalls.add(executorService.submit(legPairChecker));
			}

			for (int i = 0; i < pairGenCalls.size(); i++) {
				try {
					if (pairGenCalls.get(i).get())
						logger.info(i + "th pairGen is completed its task!");
				} catch(Exception ex) {
					ex.printStackTrace();
					logger.error(ex);
				}
			}

			executorService.shutdown();

			PairingPricingNetwork pricingNetwork = new PairingPricingNetwork(HeurosDatasetParam.dutyProcessPeriodEndExc, 
																				HeurosSystemParam.maxIdleTimeInAPairInHours, 
																				HeurosSystemParam.maxPairingLengthInDays)
																				.setLegRepository(pairOptimizationContext.getLegRepository())
																				.setDutyRepository(pairOptimizationContext.getDutyRepository())
																				.setDutyRuleContext(pairOptimizationContext.getDutyRuleContext())
																				.setDutyIndexByDepAirportNdxBrieftime(pairOptimizationContext.getDutyIndexByDepAirportNdxBrieftime());
			pricingNetwork.buildNetwork();

			PairingGenerator pairingGenerator = new PairingGenerator(HeurosSystemParam.maxIdleTimeInAPairInHours, 
																		HeurosSystemParam.maxPairingLengthInDays)
																			.setDutyRuleContext(pairOptimizationContext.getDutyRuleContext())
																			.setPairRuleContext(pairOptimizationContext.getPairRuleContext())
																			.setDutyIndexByLegNdx(pairOptimizationContext.getDutyIndexByLegNdx())
																			.setPairingPricingNetwork(pricingNetwork);

			PairChromosomeDecoder pairChromosomeDecoder = new PairChromosomeDecoder().setLegRepository(pairOptimizationContext.getLegRepository())
																						.setDutyRepository(pairOptimizationContext.getDutyRepository())
//																						.setDutyRuleContext(pairOptimizationContext.getDutyRuleContext())
//																						.setPairRuleContext(pairOptimizationContext.getPairRuleContext())
																						.setDutyIndexByLegNdx(pairOptimizationContext.getDutyIndexByLegNdx())
//																						.setHbDepArrDutyIndexByLegNdx(pairOptimizationContext.getHbDepArrDutyIndexByLegNdx())
//																						.setHbDepDutyIndexByLegNdx(pairOptimizationContext.getHbDepDutyIndexByLegNdx())
//																						.setNonHbDutyIndexByLegNdx(pairOptimizationContext.getNonHbDutyIndexByLegNdx())
//																						.setHbArrDutyIndexByLegNdx(pairOptimizationContext.getHbArrDutyIndexByLegNdx())
//																						.setDutyIndexByDepAirportNdxBrieftime(pairOptimizationContext.getDutyIndexByDepAirportNdxBrieftime())
//																						.setDutyIndexByArrAirportNdxNextBrieftime(pairOptimizationContext.getDutyIndexByArrAirportNdxNextBrieftime());
																						.setPairingGenerator(pairingGenerator);
			pairChromosomeDecoder.orderLegs();

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
																				.setGeneticIterationListener(new GeneticIterationListener<Integer>() {
																					@Override
																					public void onProgress(int iteration, double elapsedTime, Chromosome<Integer> best) {
																						HyperPair.logger.info("Progress!!! at " + iteration + ".th iteration, elapsedTime: " +
																												elapsedTime + ", best: " + String.valueOf(best.getInfo()));
																					}
																					@Override
																					public void onIterate(int iteration, double elapsedTime, Chromosome<Integer> best) {
																						HyperPair.logger.info(iteration + ".th itearation, elapsedTime: " +
																												elapsedTime + ", best: " + String.valueOf(best.getInfo()));
																						pairChromosomeDecoder.orderLegs();
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
