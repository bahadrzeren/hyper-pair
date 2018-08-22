package org.heuros.hyperpair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.heuros.conf.HeurosConfFactory;
import org.heuros.core.base.Processor;
import org.heuros.core.rule.intf.Rule;
import org.heuros.data.model.AirportFactory;
import org.heuros.data.model.Duty;
import org.heuros.data.model.DutyFactory;
import org.heuros.data.model.Leg;
import org.heuros.data.model.LegFactory;
import org.heuros.data.model.PairFactory;
import org.heuros.data.model.PairView;
import org.heuros.data.repo.DutyRepository;
import org.heuros.data.repo.LegRepository;
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

	public static void main(String[] args) throws IOException {
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

			/*
			 * Prepare factories.
			 */
			AirportFactory airportFactory = new AirportFactory();
			LegFactory legFactory = new LegFactory();
			DutyFactory dutyFactory = new DutyFactory();
			PairFactory pairFactory = new PairFactory();

			/*
			 * Prepare rule contexts.
			 */
			AirportRuleContext airportRuleContext = new AirportRuleContext();
			LegRuleContext legRuleContext = new LegRuleContext();
			DutyRuleContext dutyRuleContext = new DutyRuleContext();
			PairRuleContext pairRuleContext = new PairRuleContext();

			/*
			 * Register rules.
			 */
//			rules.for-> airportRuleContext.registerRule(r));

			/*
			 * Add legs to repository.
			 */
			LegRepository legRepo = new LegRepository();
			legs.forEach((l) -> { legRepo.addToRepo(l);});

			/*
			 * Generate duties.
			 */
			DutyRepository dutyRepo = new DutyRepository();


			/*
			 * Map Leg list to LegWrapper list
			 */


			/*
			 * Prepare and run the optimizer.
			 */
			Processor<Duty, PairView> optimizer = new HyperPairOptimizer();
			List<PairView> pairs = optimizer.proceed(dutyRepo);

			/*
			 * Report solution.
			 */
			
		}
    }
}
