package org.heuros.hyperpair;

import java.io.IOException;

import org.heuros.conf.HeurosConfFactory;
import org.heuros.core.base.Loader;
import org.heuros.core.model.Leg;
import org.heuros.loader.legs.LegsLoader;

/**
 * The main class that is used to start process.
 * 
 * @author bahadrzeren
 */
public class HyperPair {

	public static void main(String[] args) throws IOException {
    	/**
    	 * Load configuration file.
    	 */
		String confFileName = null;
		if ((args != null)
				&& (args.length > 0))
			confFileName = args[0];

		HeurosConf conf = new HeurosConfFactory<HeurosConf>()
								.createConfObject(confFileName, HeurosConf.class);

		if (conf != null) {

			Loader<Leg> loader = new LegsLoader(conf.getLegs());

//			Reporter<Leg> reporter = new LegCsvReporter(conf.getOutput());
//
//			new DataTransformDirector<Leg>().registerLoader(loader)
//											.registerReporter(reporter)
//											.proceed();
		}
    }
}
