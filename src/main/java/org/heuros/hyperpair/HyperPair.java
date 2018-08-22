package org.heuros.hyperpair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.heuros.conf.HeurosConfFactory;
import org.heuros.context.AirportContext;
import org.heuros.context.DutyContext;
import org.heuros.context.LegContext;
import org.heuros.context.PairContext;
import org.heuros.core.rule.intf.Rule;
import org.heuros.data.model.AirportFactory;
import org.heuros.data.model.DutyFactory;
import org.heuros.data.model.Leg;
import org.heuros.data.model.LegFactory;
import org.heuros.data.model.PairFactory;
import org.heuros.data.repo.AirportRepository;
import org.heuros.data.repo.DutyRepository;
import org.heuros.data.repo.LegRepository;
import org.heuros.exception.RuleAnnotationIsMissing;
import org.heuros.exception.RuleRegistrationMatchingException;
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

//			System.out.println(legs.get(0).getSobt());
//			System.out.println(legs.get(0).getSobt().truncatedTo(ChronoUnit.SECONDS));
//			System.out.println(legs.get(0).getSobt().truncatedTo(ChronoUnit.MINUTES));
//			System.out.println(legs.get(0).getSobt().truncatedTo(ChronoUnit.HOURS));
//			System.out.println(legs.get(0).getSobt().truncatedTo(ChronoUnit.DAYS));

			HeurosDatasetParam.dataPeriodStartInc = legs.get(0).getSobt().withDayOfMonth(1).toLocalDate().plusMonths(1).atStartOfDay();
			logger.info("Data period start: " + HeurosDatasetParam.dataPeriodStartInc);
			HeurosDatasetParam.dataPeriodEndExc = legs.get(legs.size() - 1).getSibt();
			logger.info("Data period end: " + HeurosDatasetParam.dataPeriodEndExc);

			HeurosDatasetParam.optPeriodStartInc = legs.get(0).getSobt().withDayOfMonth(1).toLocalDate().plusMonths(1).atStartOfDay();
			logger.info("Opt period start: " + HeurosDatasetParam.dataPeriodStartInc);
			HeurosDatasetParam.optPeriodEndExc = legs.get(legs.size() - 1).getSibt().withDayOfMonth(1).toLocalDate().atStartOfDay();
			logger.info("Opt period end: " + HeurosDatasetParam.dataPeriodEndExc);

			/*
			 * Generate contexts.
			 */
			AirportContext airportContext = (AirportContext) new AirportContext().setModelFactory(new AirportFactory())
																					.setRuleContext(new AirportRuleContext())
																					.setDataRepository(new AirportRepository());

			LegContext legContext = (LegContext) new LegContext().setModelFactory(new LegFactory())
																	.setRuleContext(new LegRuleContext())
																	.setDataRepository(new LegRepository());

			DutyContext dutyContext = (DutyContext) new DutyContext().setModelFactory(new DutyFactory())
																		.setRuleContext(new DutyRuleContext())
																		.setDataRepository(new DutyRepository());

			PairContext pairContext = (PairContext) new PairContext().setModelFactory(new PairFactory())
																		.setRuleContext(new PairRuleContext());

			/*
			 * Register rules.
			 */
			for (Rule r : HyperPair.rules) {
				try {
					int numOfRegistrations = 0;
					numOfRegistrations += airportContext.getRuleContext().registerRule(r);
					numOfRegistrations += legContext.getRuleContext().registerRule(r);
					numOfRegistrations += dutyContext.getRuleContext().registerRule(r);
					numOfRegistrations += pairContext.getRuleContext().registerRule(r);
					if (numOfRegistrations != r.getClass().getGenericInterfaces().length)
						throw new RuleRegistrationMatchingException("Rule imlementations and number of registrations do not match!");
				} catch (RuleRegistrationMatchingException ex) {
					logger.error(ex);
					throw ex;
				} catch (RuleAnnotationIsMissing ex) {
					logger.error("RuleImplementation annotation is missing for " + r.getClass().getName() + ".");
					logger.error(ex);
					throw ex;
				}
			}

			/*
			 * Add airports and legs to repositories.
			 */
			legs.forEach((l) -> {
				l.setDepAirport(airportContext.getAirport(l.getDep()));
				l.setArrAirport(airportContext.getAirport(l.getArr()));
				if (!legContext.registerLeg(l)) {
					if (l.getSobt().isAfter(HeurosDatasetParam.dataPeriodStartInc))
						logger.warn("Leg " + l + " is not registered!");
				}
			});

			/*
			 * Generate duties.
			 */
			

			/*
			 * Map Leg list to LegWrapper list
			 */


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
