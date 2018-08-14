package org.heuros.hyperpair;

import java.io.IOException;
import java.util.List;

import org.heuros.conf.HeurosConfFactory;
import org.heuros.core.base.Processor;
import org.heuros.core.data.base.Wrapper;
import org.heuros.data.model.DutyExtensionFactory;
import org.heuros.data.model.DutyWrapperFactory;
import org.heuros.data.model.LegExtension;
import org.heuros.data.model.LegExtensionFactory;
import org.heuros.data.model.LegModel;
import org.heuros.data.model.LegWrapper;
import org.heuros.data.model.LegWrapperFactory;
import org.heuros.data.model.PairExtension;
import org.heuros.data.model.PairExtensionFactory;
import org.heuros.data.model.PairModel;
import org.heuros.data.model.PairWrapper;
import org.heuros.data.model.PairWrapperFactory;
import org.heuros.data.repo.LegRepository;
import org.heuros.loader.legs.LegsLoader;
import org.heuros.processor.leg.LegProcessor;
import org.heuros.rule.DutyRuleContext;
import org.heuros.rule.LegRuleContext;
import org.heuros.rule.PairRuleContext;

/**
 * The main class that is used to start process.
 * 
 * @author bahadrzeren
 */
public class HyperPair {

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
			List<LegModel> legs = new LegsLoader().setLegsFileName(conf.getLegs())
														.extractData();

			/*
			 * Prepare rule engine and factories.
			 */

			LegRuleContext legRuleContext = new LegRuleContext();
//			legRuleContext.registerRule();
			DutyRuleContext dutyRuleContext = new DutyRuleContext();
//			dutyRuleContext.registerRule();
			PairRuleContext pairRuleContext = new PairRuleContext();
//			pairRuleContext.registerRule();

			LegExtensionFactory legExtensionFactory = new LegExtensionFactory();
			DutyExtensionFactory dutyExtensionFactory = new DutyExtensionFactory();
			PairExtensionFactory pairExtensionFactory = new PairExtensionFactory();

			LegWrapperFactory legWrapperFactory = new LegWrapperFactory(legRuleContext, legExtensionFactory);
			DutyWrapperFactory dutyWrapperFactory = new DutyWrapperFactory(dutyRuleContext, dutyExtensionFactory);
			PairWrapperFactory pairWrapperFactory = new PairWrapperFactory(pairRuleContext, pairExtensionFactory);

			/*
			 * Map Leg list to LegWrapper list
			 */
			List<LegWrapper> legWrappers = new LegProcessor().setRuleContext(legRuleContext)
																					.setExtensionFactory(legExtensionFactory)
																					.setWrapperFactory(legWrapperFactory)
																					.proceed(legs);

			LegRepository legRepo = new LegRepository(legWrapperFactory);
			legWrappers.forEach(legRepo::addWrapperToRepo);

			Processor<LegWrapper, PairWrapper, PairModel, PairExtension> optimizer = new HyperPairOptimizer();
			List<PairWrapper> pairs = optimizer.proceed(legWrappers);

			/*
			 * Report solution.
			 */
//			Reporter<PairWrapper> reporter = new LegCsvReporter(conf.getOutput());
			
		}
    }
}
