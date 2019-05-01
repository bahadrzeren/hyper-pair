package org.heuros.pair.conf;

public class HeurosAlgParameters {

    public static long maxElapsedTimeInNanoSecs = 4 * 60 * 60 * 1000000000l;	//	hr * min * sec * ns.

    public static int maxNumOfIterations = 200;
    public static int maxNumOfIterationsWOProgress = 1000;
    public static int populationSize = 10;
    public static int minNumOfChildren = 2;
    public static int numOfEliteChromosomes = 2;
    public static float mutationRate = 0.02f;
    public static boolean allowDublicateChromosomes = false;
}
