package com.jamesdpeters;

import com.jamesdpeters.universes.SolarSystem;
import com.jamesdpeters.universes.TrappistSystem;

public class StartTrappist {

    public static void main(String[] args) {

        System.out.println("********************************************");

        /* Total number of processors or cores available to the JVM */
        System.out.println("Available processors (cores): " + Runtime.getRuntime().availableProcessors());

        /* Total amount of free memory available to the JVM */
        System.out.println("Free memory (bytes): " + Runtime.getRuntime().freeMemory());

        /* This will return Long.MAX_VALUE if there is no preset limit */
        long maxMemory = Runtime.getRuntime().maxMemory();
        /* Maximum amount of memory the JVM will attempt to use */
        System.out.println("Maximum memory (bytes): " + (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory));

        /* Total memory currently in use by the JVM */
        System.out.println("Total memory (bytes): " + Runtime.getRuntime().totalMemory());

        System.out.println("********************************************");


        TrappistSystem universe = new TrappistSystem();
        universe.setOutput(true);
        universe.overrideTimeStep(0.00001);
        universe.start();

    }
}
