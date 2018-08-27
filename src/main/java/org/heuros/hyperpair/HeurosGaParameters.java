package org.heuros.hyperpair;

public class HeurosGaParameters {

    public static long maxElapsedTimeInNanoSecs = 6000000000000l;

    public static int maxNumOfIterations = 1000;
    public static int maxNumOfIterationsWOProgress = 200;
    public static int populationSize = 50;
    public static int minNumOfChildren = 10;
    public static int numOfEliteChromosomes = 4;
    public static float mutationRate = 0.02f;
    public static boolean allowDublicateChromosomes = false;
}
