package org.heuros.pair.ga;

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
import org.heuros.core.ga.mutation.SwapGeneMutator;
import org.heuros.core.ga.selection.BinaryTournamentSelector;
import org.heuros.core.rule.intf.Rule;
import org.heuros.data.DutyLegOvernightConnNetwork;
import org.heuros.data.model.Duty;
import org.heuros.data.model.Leg;
import org.heuros.data.processor.BiDirDutyPairingChecker;
import org.heuros.data.processor.BiDirLegPairingChecker;
import org.heuros.data.processor.DutyGenerator;
import org.heuros.exception.RuleAnnotationIsMissing;
import org.heuros.exception.RuleRegistrationMatchingException;
import org.heuros.loader.legs.LegsLoader;
import org.heuros.pair.conf.HeurosConf;
import org.heuros.pair.conf.HeurosDatasetParam;
import org.heuros.pair.conf.HeurosGaParameters;
import org.heuros.pair.conf.HeurosSystemParam;
import org.heuros.pair.ga.ga.CycleCrossover;
import org.heuros.pair.ga.ga.PairChromosomeDecoder;
import org.heuros.pair.ga.ga.PairChromosomeFactory;
import org.heuros.pair.ga.ga.PairOptimizer;
import org.heuros.pair.intro.AirportIntroducer;
import org.heuros.pair.intro.DutyLegAggregator;
import org.heuros.pair.intro.LegIntroducer;
import org.heuros.pair.intro.PairDutyAggregator;
import org.heuros.pair.rule.CriticalStations1;
import org.heuros.pair.rule.CriticalStations2;
import org.heuros.pair.rule.DutyACChange;
import org.heuros.pair.rule.DutyAgDgFlights;
import org.heuros.pair.rule.DutyAugmentationCheck;
import org.heuros.pair.rule.DutyEndIfStatIsTouched;
import org.heuros.pair.rule.DutyFlightConnectionTime;
import org.heuros.pair.rule.DutyForceToBeFirstLayoverDuty;
import org.heuros.pair.rule.DutyIntFlights;
import org.heuros.pair.rule.DutyLayover;
import org.heuros.pair.rule.DutyLength;
import org.heuros.pair.rule.DutyMaxBlockTimeForIntLayovers;
import org.heuros.pair.rule.DutyNumOfLegsLimit;
import org.heuros.pair.rule.DutyOneDutyStats;
import org.heuros.pair.rule.DutySpecEuroStats;
import org.heuros.pair.rule.DutySpecialFlightNums1;
import org.heuros.pair.rule.DutySpecialFlightNums2;
import org.heuros.pair.rule.PairDutyDay;
import org.heuros.pair.rule.PairDutyRestCheck;
import org.heuros.pair.rule.PairEarlyDuty;
import org.heuros.pair.rule.PairGrouping;
import org.heuros.pair.rule.PairHardDuty;
import org.heuros.pair.rule.PairIntDuties;
import org.heuros.pair.rule.PairLayoverCheck;
import org.heuros.pair.rule.PairNumOfPassiveLegsLimit;
import org.heuros.pair.rule.PairPeriodLength;
import org.heuros.pair.sp.PairingGenerator;

/**
 * The main class that is used to start process.
 * 
 * @author bahadrzeren
 */
public class GaPair {

	private static List<Rule> rules = new ArrayList<Rule>();

	static {
		DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss", Locale.ENGLISH).withZone(ZoneOffset.UTC);
		System.setProperty("logfile.date.time", datetimeFormatter.format(LocalDateTime.now()));

		GaPair.rules.add(new AirportIntroducer());
		GaPair.rules.add(new DutyLegAggregator());
		GaPair.rules.add(new LegIntroducer());
		GaPair.rules.add(new PairDutyAggregator());
		GaPair.rules.add(new CriticalStations1());
		GaPair.rules.add(new CriticalStations2());
		GaPair.rules.add(new DutyACChange());
		GaPair.rules.add(new DutyAgDgFlights());
		GaPair.rules.add(new DutyAugmentationCheck());
		GaPair.rules.add(new DutyEndIfStatIsTouched());
		GaPair.rules.add(new DutyFlightConnectionTime());
		GaPair.rules.add(new DutyForceToBeFirstLayoverDuty());
		GaPair.rules.add(new DutyIntFlights());
		GaPair.rules.add(new DutyLayover());
		GaPair.rules.add(new DutyLength());
		GaPair.rules.add(new DutyMaxBlockTimeForIntLayovers());
		GaPair.rules.add(new DutyNumOfLegsLimit());
		GaPair.rules.add(new DutyOneDutyStats());
		GaPair.rules.add(new DutySpecEuroStats());
		GaPair.rules.add(new DutySpecialFlightNums1());
		GaPair.rules.add(new DutySpecialFlightNums2());
		GaPair.rules.add(new PairDutyDay());
		GaPair.rules.add(new PairDutyRestCheck());
		GaPair.rules.add(new PairEarlyDuty());
		GaPair.rules.add(new PairGrouping());
		GaPair.rules.add(new PairHardDuty());
		GaPair.rules.add(new PairIntDuties());
		GaPair.rules.add(new PairLayoverCheck());
		GaPair.rules.add(new PairNumOfPassiveLegsLimit());
		GaPair.rules.add(new PairPeriodLength());
	}

	private static Logger logger = Logger.getLogger(GaPair.class);

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
			PairOptimizationContext pairOptimizationContext = new PairOptimizationContext(HeurosSystemParam.homebases.length);

			/*
			 * Register rules.
			 */
			pairOptimizationContext.registerRules(GaPair.rules);

			/*
			 * Add airports and legs to repositories and generate necessary indexes.
			 */
			pairOptimizationContext.registerAirportsAndLegs(legs, HeurosDatasetParam.dataPeriodStartInc, HeurosSystemParam.maxLegConnectionTimeInMins);

			/*
			 * Generate duties.
			 */
			DutyGenerator dutyGenerator = new DutyGenerator(pairOptimizationContext.getLegRepository(),
															pairOptimizationContext.getConnectionLegsIndex(),
															HeurosSystemParam.homebases.length,
															pairOptimizationContext.getDutyRuleContext());
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

			List<Future<Boolean>> pairInitCalls = new ArrayList<Future<Boolean>>(HeurosSystemParam.homebases.length);

			for (int hbNdx = 0; hbNdx < HeurosSystemParam.homebases.length; hbNdx++) {

//			UniDirDutyPairingChecker pairChecker = new UniDirDutyPairingChecker(hbNdx).setMaxPairingLengthInHours(HeurosSystemParam.maxPairingLengthInDays * 24)
//																	.setMaxIdleTimeInAPairInHours(HeurosSystemParam.maxIdleTimeInAPairInHours)
//																	.setDutyRepository(pairOptimizationContext.getDutyRepository())
//																	.setDutyRuleContext(pairOptimizationContext.getDutyRuleContext())
//																	.setPairRuleContext(pairOptimizationContext.getPairRuleContext())
//																	.setDutyIndexByDepAirportNdxBrieftime(pairOptimizationContext.getDutyIndexByDepAirportNdxBrieftime());

				BiDirDutyPairingChecker dutyPairChecker = new BiDirDutyPairingChecker(hbNdx,
																						HeurosDatasetParam.dutyProcessPeriodEndExc,
																						HeurosSystemParam.effectiveDutyBlockHourLimit,
																						HeurosSystemParam.maxPreDutySearchDeptInHours,
																						HeurosSystemParam.maxPairingLengthInDays * 24,
																						pairOptimizationContext);
				pairInitCalls.add(executorService.submit(dutyPairChecker));

				BiDirLegPairingChecker legPairChecker = new BiDirLegPairingChecker(hbNdx,
																					HeurosDatasetParam.legCoverPeriodEndExc,
																					HeurosSystemParam.maxPreDutySearchDeptInHours,
																					HeurosSystemParam.maxPairingLengthInDays * 24,
																					pairOptimizationContext);
				pairInitCalls.add(executorService.submit(legPairChecker));
			}

			for (int i = 0; i < pairInitCalls.size(); i++) {
				try {
					if (pairInitCalls.get(i).get())
						logger.info(i + "th pairGen is completed its task!");
				} catch(Exception ex) {
					ex.printStackTrace();
					logger.error(ex);
				}
			}

			executorService.shutdown();

			DutyLegOvernightConnNetwork pricingNetwork = new DutyLegOvernightConnNetwork(HeurosDatasetParam.dutyProcessPeriodEndExc, 
																							HeurosSystemParam.maxNetDutySearchDeptInHours, 
																							HeurosSystemParam.maxPairingLengthInDays,
																							pairOptimizationContext);
			pricingNetwork.buildNetwork();

			PairingGenerator pairingGenerator = new PairingGenerator(pairOptimizationContext.getPairRuleContext(),
																		pairOptimizationContext.getDutyIndexByLegNdx(),
																		pricingNetwork,
																		pairOptimizationContext.getDutyRepository());

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

//			List<Pair> solution = 
					pairOptimizer.proceed();
System.out.println();


			/*
			 * Report solution.
			 */
			
		}
    }
}
