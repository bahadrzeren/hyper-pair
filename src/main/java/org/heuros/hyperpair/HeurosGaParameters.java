package org.heuros.hyperpair;

public class HeurosGaParameters<T, M> {

    public static long maxElapsedTimeInNanoSecs = 60000000000l;

    public static int maxNumOfIterations = 1000;
    public static int maxNumOfIterationsWOProgress = 200;
    public static int populationSize = 100;
    public static int minNumOfChildren = 20;
    public static int numOfEliteChromosomes = 4;
    public static float mutationRate = 0.02f;
    public static float greedRate = 0.0f;
    public static boolean allowDublicateChromosomes = false;
}
