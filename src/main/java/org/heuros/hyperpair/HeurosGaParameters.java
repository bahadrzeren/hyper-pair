package org.heuros.hyperpair;

public class HeurosGaParameters {

    public static long maxElapsedTimeInNanoSecs = 14 * 60 * 60 * 1000000000l;	//	hr * min * sec * ns.

    public static int maxNumOfIterations = 1000;
    public static int maxNumOfIterationsWOProgress = 50;
    public static int populationSize = 10;
    public static int minNumOfChildren = 2;
    public static int numOfEliteChromosomes = 2;
    public static float mutationRate = 0.01f;
    public static boolean allowDublicateChromosomes = false;
}
