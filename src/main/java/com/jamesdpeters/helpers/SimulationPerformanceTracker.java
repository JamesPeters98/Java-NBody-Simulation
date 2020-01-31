package com.jamesdpeters.helpers;

import com.jamesdpeters.universes.Universe;

import java.util.concurrent.TimeUnit;

public class SimulationPerformanceTracker {

    private long startTime, endTime;
    private Universe universe;

    public SimulationPerformanceTracker(Universe universe){
        this.universe = universe;
    }

    public void startTracker(){
        startTime = System.currentTimeMillis();
    }

    public void finishTracker(){
        endTime = System.currentTimeMillis();
    }

    private long timeTaken(){
        return (endTime-startTime)/1000;
    }

    public void printStats(){
        long posSize = (long) (universe.getBodies().size()*(universe.runningTime()/universe.dt()));
        long EPS = posSize/timeTaken();
        System.out.println("********************************");
        System.out.println("Stats for "+universe.getName());
        System.out.println("Step Time: "+universe.dt()+" (Days)");
        System.out.println("Time Taken: "+timeTaken()+" s");
        System.out.println("Events Per Second: "+EPS);
        System.out.println("********************************");
    }


}
