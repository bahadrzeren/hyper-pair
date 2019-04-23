package org.heuros.pair.heuro;

import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.heuros.exception.RuleAnnotationIsMissing;
import org.heuros.exception.RuleRegistrationMatchingException;
import org.heuros.pair.AbsPairingOptimizer;

/**
 * The main class that is used to start process.
 * 
 * @author bahadrzeren
 */
public class HeuroPair extends AbsPairingOptimizer {

	private static Logger logger = Logger.getLogger(HeuroPair.class);

	public static void main(String[] args) throws RuleAnnotationIsMissing, RuleRegistrationMatchingException, InterruptedException, ExecutionException, CloneNotSupportedException {
		HeuroPair optimizer = new HeuroPair();
		optimizer.runTheOptimizer(args);
	}

	@Override
	public void doOptimize() throws InterruptedException, ExecutionException, CloneNotSupportedException {
		HeuroOptimizer pairOptimizer = new HeuroOptimizer(pairOptimizationContext, pricingNetwork);
		pairOptimizer.doMinimize();
		logger.info("Optimization run is completed!");
    }
}
