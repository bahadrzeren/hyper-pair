package org.heuros.pair;

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

/**
 * The main class that is used to start process.
 * 
 * @author bahadrzeren
 */
public abstract class AbsPairingOptimizer {

	private static List<Rule> rules = new ArrayList<Rule>();

	static {
		DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss", Locale.ENGLISH).withZone(ZoneOffset.UTC);
		System.setProperty("logfile.date.time", datetimeFormatter.format(LocalDateTime.now()));

		AbsPairingOptimizer.rules.add(new AirportIntroducer());
		AbsPairingOptimizer.rules.add(new DutyLegAggregator());
		AbsPairingOptimizer.rules.add(new LegIntroducer());
		AbsPairingOptimizer.rules.add(new PairDutyAggregator());
		AbsPairingOptimizer.rules.add(new CriticalStations1());
		AbsPairingOptimizer.rules.add(new CriticalStations2());
		AbsPairingOptimizer.rules.add(new DutyACChange());
		AbsPairingOptimizer.rules.add(new DutyAgDgFlights());
		AbsPairingOptimizer.rules.add(new DutyAugmentationCheck());
		AbsPairingOptimizer.rules.add(new DutyEndIfStatIsTouched());
		AbsPairingOptimizer.rules.add(new DutyFlightConnectionTime());
		AbsPairingOptimizer.rules.add(new DutyForceToBeFirstLayoverDuty());
		AbsPairingOptimizer.rules.add(new DutyIntFlights());
		AbsPairingOptimizer.rules.add(new DutyLayover());
		AbsPairingOptimizer.rules.add(new DutyLength());
		AbsPairingOptimizer.rules.add(new DutyMaxBlockTimeForIntLayovers());
		AbsPairingOptimizer.rules.add(new DutyNumOfLegsLimit());
		AbsPairingOptimizer.rules.add(new DutyOneDutyStats());
		AbsPairingOptimizer.rules.add(new DutySpecEuroStats());
		AbsPairingOptimizer.rules.add(new DutySpecialFlightNums1());
		AbsPairingOptimizer.rules.add(new DutySpecialFlightNums2());
		AbsPairingOptimizer.rules.add(new PairDutyDay());
		AbsPairingOptimizer.rules.add(new PairDutyRestCheck());
		AbsPairingOptimizer.rules.add(new PairEarlyDuty());
		AbsPairingOptimizer.rules.add(new PairGrouping());
		AbsPairingOptimizer.rules.add(new PairHardDuty());
		AbsPairingOptimizer.rules.add(new PairIntDuties());
		AbsPairingOptimizer.rules.add(new PairLayoverCheck());
		AbsPairingOptimizer.rules.add(new PairNumOfPassiveLegsLimit());
		AbsPairingOptimizer.rules.add(new PairPeriodLength());
	}

	private static Logger logger = Logger.getLogger(AbsPairingOptimizer.class);

	public abstract void doOptimize() throws InterruptedException, ExecutionException, CloneNotSupportedException;

	protected PairOptimizationContext pairOptimizationContext = null;
	protected DutyLegOvernightConnNetwork pricingNetwork = null;

	public void runTheOptimizer(String[] args) throws RuleAnnotationIsMissing, RuleRegistrationMatchingException, InterruptedException, ExecutionException, CloneNotSupportedException {
    	/*
    	 * Load configuration file.
    	 */
		String inputDataFile = null;
		if ((args != null) && (args.length > 1)) {

			inputDataFile = args[0];

			HeurosSystemParam.effectiveDutyBlockHourLimit = Integer.parseInt(args[1]);	//	4 * 60;

			HeurosSystemParam.numOfLegsToBeChoosen = Integer.parseInt(args[2]);	//	3;
			HeurosSystemParam.pairEnumerationDept = Integer.parseInt(args[3]);	//	2;
			HeurosSystemParam.pairEnumIdleDayBuffer = Integer.parseInt(args[4]);	//	1;

			HeurosSystemParam.weightDutyInclusionScore = Double.parseDouble(args[5]);	//	0.0;
			HeurosSystemParam.weightDutyInclusionScoreWoDh = Double.parseDouble(args[6]);	//	0.0;
			HeurosSystemParam.weightDutyEffectiveInclusionScore = Double.parseDouble(args[7]);	//	0.0;
			HeurosSystemParam.weightDutyEffectiveInclusionScoreWoDh = Double.parseDouble(args[8]);	//	0.0;

			HeurosSystemParam.weightPairInclusionScore = Double.parseDouble(args[9]);	//	0.0;
			HeurosSystemParam.weightPairInclusionScoreWoDh = Double.parseDouble(args[10]);	//	0.95;
			HeurosSystemParam.weightPairEffectiveInclusionScore = Double.parseDouble(args[11]);	//	0.0;
			HeurosSystemParam.weightPairEffectiveInclusionScoreWoDh = Double.parseDouble(args[12]);	//	0.0;

			HeurosSystemParam.weightHeurModDh = Double.parseDouble(args[13]);	//	0.05;
			HeurosSystemParam.weightHeurModEf = Double.parseDouble(args[14]);	//	0.0;

			HeurosSystemParam.hmResetWeightAfterBestSol = Double.parseDouble(args[15]);	//	0.33;
			HeurosSystemParam.hmResetWeightAfterImprSol = Double.parseDouble(args[16]);	//	0.66;
		} else {
			String confFileName = null;
			if ((args != null) && (args.length > 0))
				confFileName = args[0];
			HeurosConf conf = new HeurosConfFactory<HeurosConf>()
									.createConfObject(confFileName, HeurosConf.class);
			inputDataFile = conf.getLegs();
		}

		if (inputDataFile != null) {

			/*
			 * Load LEG data from CSV file.
			 */
			List<Leg> legs = new LegsLoader().setLegsFileName(inputDataFile)
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
			pairOptimizationContext = new PairOptimizationContext(HeurosSystemParam.homebases.length);

			/*
			 * Register rules.
			 */
			pairOptimizationContext.registerRules(AbsPairingOptimizer.rules);

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

			pricingNetwork = new DutyLegOvernightConnNetwork(HeurosDatasetParam.dutyProcessPeriodEndExc, 
																HeurosSystemParam.maxNetDutySearchDeptInHours, 
																pairOptimizationContext);
			pricingNetwork.buildNetwork();

			BiDirPairChecker pairChecker = new BiDirPairChecker(pairOptimizationContext, pricingNetwork);
			Future<Boolean> pairCheckCall = executorService.submit(pairChecker);
			if (pairCheckCall.get())
				logger.info("pairCheck job is completed!");

			executorService.shutdown();

			this.doOptimize();
		}
    }
}