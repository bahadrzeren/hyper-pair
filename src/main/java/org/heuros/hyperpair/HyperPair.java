package org.heuros.hyperpair;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.heuros.conf.HeurosConfFactory;
import org.heuros.core.base.Processor;
import org.heuros.data.model.AirportFactory;
import org.heuros.data.model.Duty;
import org.heuros.data.model.DutyFactory;
import org.heuros.data.model.DutyLegFactory;
import org.heuros.data.model.Leg;
import org.heuros.data.model.LegFactory;
import org.heuros.data.model.PairFactory;
import org.heuros.data.model.PairView;
import org.heuros.data.repo.DutyRepository;
import org.heuros.data.repo.LegRepository;
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
			 * Prepare rule engine.
			 */
			AirportRuleContext airportRuleContext = new AirportRuleContext();
//			airportRuleContext.registerRule();
			LegRuleContext legRuleContext = new LegRuleContext();
//			legRuleContext.registerRule();
			DutyRuleContext dutyRuleContext = new DutyRuleContext();
//			dutyRuleContext.registerRule();
			PairRuleContext pairRuleContext = new PairRuleContext();
//			pairRuleContext.registerRule();

			/*
			 * Prepare factories.
			 */
			AirportFactory airportFactory = new AirportFactory();
			LegFactory legFactory = new LegFactory();
			DutyLegFactory dutyLegFactory = new DutyLegFactory();
			DutyFactory dutyFactory = new DutyFactory();
			PairFactory pairFactory = new PairFactory();

			/*
			 * Add legs to repository.
			 */
			LegRepository legRepo = new LegRepository();
			legs.forEach(legRepo::addToRepo);

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
