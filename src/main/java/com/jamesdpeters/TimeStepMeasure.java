package com.jamesdpeters;

import com.github.sh0nk.matplotlib4j.Plot;
import com.jamesdpeters.bodies.BodyErrorWorker;
import com.jamesdpeters.integrators.IntegratorFactory;
import com.jamesdpeters.integrators.abstracts.Integrator;
import com.jamesdpeters.json.Graph;
import com.jamesdpeters.universes.SolarSystem;
import com.jamesdpeters.universes.Universe;

import java.util.*;
import java.util.List;

public class TimeStepMeasure {

    private static HashMap<String, TreeMap<Double,Double>> bodyErrors;
    private static List<Universe> universes;
    private static String[] colors = {"red","blue","green","orange","pink","black","yellow"};

    public static void main(String[] args) throws InterruptedException {

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

        bodyErrors = new HashMap<>();
        universes = new ArrayList<>();

        Integrator integrator = IntegratorFactory.getRK4Integrator();
        double[] timesteps = {1,0.5,0.25,0.1,0.05,0.025};
        double runningTime = 365*10;

        for(double timestep : timesteps){
            SolarSystem universe = getUniverse(timestep,integrator);
            universe.setRunningTime(runningTime);
            universe.start();
            universes.add(universe);
        }

        while(true){
            boolean finished = true;
            for(Universe u : universes) {
                if(!u.hasFinished()){
                    //System.out.println("Hasn't finished!");
                    finished = false;
                    break;
                }
            }
            if(finished) break;
            Thread.sleep(1000);
        }
        System.out.println("FINISHED!");
        System.out.println(bodyErrors);
        plot();

    }

    private static SolarSystem getUniverse(double timestep, Integrator integrator){
        SolarSystem universe = new SolarSystem();
        universe.setOutput(false);
        universe.overrideTimeStep(timestep);
        universe.setIntegrator(integrator);
        universe.addOnFinishListener(() -> universe.getBodies().forEach(body -> {
            System.out.println("Running listener!");
            double error = BodyErrorWorker.calculateError(body,2);
            TreeMap<Double, Double> timestepErrors = bodyErrors.computeIfAbsent(body.getName(), k -> new TreeMap<>());
            timestepErrors.put(timestep,error);
        }));
        return universe;
    }

    private static void plot(){

        int pos = 0;
        for(Map.Entry<String, TreeMap<Double, Double>> entry : bodyErrors.entrySet()){
            String body = entry.getKey();
            TreeMap<Double,Double> errors = entry.getValue();

            Plot plt = Graph.getPlot();
            plt.figure("Timestep Errors - "+body);
            List<Number> timesteps = new ArrayList<>();
            List<Number> errorVals = new ArrayList<>();
            for(Map.Entry<Double,Double> values : errors.entrySet()){
                timesteps.add(values.getKey());
                errorVals.add(values.getValue());
            }

            plt.plot()
                    .add(timesteps,errorVals)
                    .label(body)
                    .linestyle("solid")
                    .color(colors[pos]);
            plt.xlabel("Timestep (Days)");
            plt.ylabel("Average Error (AU)");
            plt.legend().loc("upper right");
            Graph.openPlot(plt);

            pos++;
        }
    }
}
