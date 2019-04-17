package org.heuros.pair.heuro;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.heuros.conf.HeurosConfFactory;
import org.heuros.context.PairOptimizationContext;
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
import org.heuros.pair.conf.HeurosSystemParam;
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
public class HeuroPair {

	private static List<Rule> rules = new ArrayList<Rule>();

	static {
		DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss", Locale.ENGLISH).withZone(ZoneOffset.UTC);
		System.setProperty("logfile.date.time", datetimeFormatter.format(LocalDateTime.now()));

		HeuroPair.rules.add(new AirportIntroducer());
		HeuroPair.rules.add(new DutyLegAggregator());
		HeuroPair.rules.add(new LegIntroducer());
		HeuroPair.rules.add(new PairDutyAggregator());
		HeuroPair.rules.add(new CriticalStations1());
		HeuroPair.rules.add(new CriticalStations2());
		HeuroPair.rules.add(new DutyACChange());
		HeuroPair.rules.add(new DutyAgDgFlights());
		HeuroPair.rules.add(new DutyAugmentationCheck());
		HeuroPair.rules.add(new DutyEndIfStatIsTouched());
		HeuroPair.rules.add(new DutyFlightConnectionTime());
		HeuroPair.rules.add(new DutyForceToBeFirstLayoverDuty());
		HeuroPair.rules.add(new DutyIntFlights());
		HeuroPair.rules.add(new DutyLayover());
		HeuroPair.rules.add(new DutyLength());
		HeuroPair.rules.add(new DutyMaxBlockTimeForIntLayovers());
		HeuroPair.rules.add(new DutyNumOfLegsLimit());
		HeuroPair.rules.add(new DutyOneDutyStats());
		HeuroPair.rules.add(new DutySpecEuroStats());
		HeuroPair.rules.add(new DutySpecialFlightNums1());
		HeuroPair.rules.add(new DutySpecialFlightNums2());
		HeuroPair.rules.add(new PairDutyDay());
		HeuroPair.rules.add(new PairDutyRestCheck());
		HeuroPair.rules.add(new PairEarlyDuty());
		HeuroPair.rules.add(new PairGrouping());
		HeuroPair.rules.add(new PairHardDuty());
		HeuroPair.rules.add(new PairIntDuties());
		HeuroPair.rules.add(new PairLayoverCheck());
		HeuroPair.rules.add(new PairNumOfPassiveLegsLimit());
		HeuroPair.rules.add(new PairPeriodLength());
	}

	private static Logger logger = Logger.getLogger(HeuroPair.class);

	public static void main(String[] args) throws IOException, RuleAnnotationIsMissing, RuleRegistrationMatchingException, InterruptedException, ExecutionException, CloneNotSupportedException {
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
			pairOptimizationContext.registerRules(HeuroPair.rules);

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
//			List<Future<Boolean>> pairInitCalls = new ArrayList<Future<Boolean>>(HeurosSystemParam.homebases.length);

			for (int hbNdx = 0; hbNdx < HeurosSystemParam.homebases.length; hbNdx++) {

				BiDirDutyPairingChecker dutyPairChecker = new BiDirDutyPairingChecker(hbNdx,
																						HeurosDatasetParam.dutyProcessPeriodEndExc,
																						HeurosSystemParam.effectiveDutyBlockHourLimit,
																						HeurosSystemParam.maxPreDutySearchDeptInHours,
																						HeurosSystemParam.maxPairingLengthInDays * 24,
																						pairOptimizationContext);

//				pairInitCalls.add(executorService.submit(dutyPairChecker));
				Future<Boolean> dutyPairCheckCall = executorService.submit(dutyPairChecker);
				if (dutyPairCheckCall.get())
					logger.info("dutyPairCheck job is completed!");

				BiDirLegPairingChecker legPairChecker = new BiDirLegPairingChecker(hbNdx,
																					HeurosDatasetParam.legCoverPeriodEndExc,
																					HeurosSystemParam.maxPreDutySearchDeptInHours,
																					HeurosSystemParam.maxPairingLengthInDays * 24,
																					pairOptimizationContext);

//				pairInitCalls.add(executorService.submit(legPairChecker));
				Future<Boolean> legPairCheckCall = executorService.submit(legPairChecker);
				if (legPairCheckCall.get())
					logger.info("legPairCheck job is completed!");

			}

//			for (int i = 0; i < pairInitCalls.size(); i++) {
//				try {
//					if (pairInitCalls.get(i).get())
//						logger.info(i + "th pairGen is completed its task!");
//				} catch(Exception ex) {
//					ex.printStackTrace();
//					logger.error(ex);
//				}
//			}

			DutyLegOvernightConnNetwork pricingNetwork = new DutyLegOvernightConnNetwork(HeurosDatasetParam.dutyProcessPeriodEndExc, 
																							HeurosSystemParam.maxNetDutySearchDeptInHours, 
																							pairOptimizationContext);
			pricingNetwork.buildNetwork();

			BiDirPairChecker pairChecker = new BiDirPairChecker(pairOptimizationContext, pricingNetwork);
			Future<Boolean> pairCheckCall = executorService.submit(pairChecker);
			if (pairCheckCall.get())
				logger.info("pairCheck job is completed!");

			executorService.shutdown();

			PairingGenerator pairingGenerator = new PairingGenerator(pairOptimizationContext, pricingNetwork);

			HeuroOptimizer pairOptimizer = new HeuroOptimizer(pairOptimizationContext,
																pricingNetwork,
																pairingGenerator);

//			List<Pair> solution = 
					pairOptimizer.doMinimize();
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
