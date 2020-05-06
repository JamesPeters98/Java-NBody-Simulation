package com.jamesdpeters;

import com.github.sh0nk.matplotlib4j.Plot;
import com.jamesdpeters.bodies.BodyErrorWorker;
import com.jamesdpeters.integrators.IntegratorFactory;
import com.jamesdpeters.integrators.abstracts.Integrator;
import com.jamesdpeters.json.CSVWriter;
import com.jamesdpeters.json.Graph;
import com.jamesdpeters.universes.SolarSystem;
import com.jamesdpeters.universes.Universe;
import com.sun.source.tree.Tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class StartSolarSystemError {

    private static List<Universe> universes;
    private static HashMap<Integrator,TreeMap<Double,TreeMap<Double,Double>>> intergratorTimestepErrors;
    private static HashMap<Integrator,TreeMap<Double,Double>> integratorAccumulatedError;
    private static String bodyToCalculate = "";

    static double[] timesteps;
    static Integrator[] integrators;
    static int cols, rows;

    public static void main(String[] args) throws InterruptedException {

        universes = new ArrayList<>();
        intergratorTimestepErrors = new HashMap<>();
        integratorAccumulatedError = new HashMap<>();
        timesteps = new double[]{0.6,0.5,0.4,0.3,0.2};
        integrators = new Integrator[]{IntegratorFactory.getRK4Integrator(),IntegratorFactory.getLeapFrogIntegrator(),IntegratorFactory.getYoshidaIntegrator()};
        rows = 1; cols = 3;
        double res = 192; //Store every 96 days of data (96 because it's divisible by lots of decimals e.g 0.1,0.2,0.3,0.4,0.5)
        double runningTime = res*250; //Stores 100 deltas.
        bodyToCalculate = "Moon";

        for(Integrator integrator : integrators) {
            intergratorTimestepErrors.put(integrator, new TreeMap<>());
            integratorAccumulatedError.put(integrator, new TreeMap<>());
            for (double timestep : timesteps) {
                SolarSystem universe = getUniverse(timestep, integrator, runningTime, res);
                universe.start();
                universes.add(universe);
            }
        }

        while(true){
            boolean finished = true;
            for(Universe u : universes) {
                if(!u.hasFinished()){
                    finished = false;
                    break;
                }
            }
            if(finished) break;
            Thread.sleep(1000);
        }
        System.out.println("FINISHED!");
        plot();
        plotAccumulated();
    }

    private static SolarSystem getUniverse(double dt, Integrator integrator, double runningTime, double res){
        SolarSystem universe = new SolarSystem("/Body96Timestep.json");
        universe.setOutput(true);
        universe.setRunningTime(runningTime);
        universe.setIntegrator(integrator);
        universe.overrideTimeStep(dt);
        universe.setResolution((int) (res/dt));
        universe.addOnFinishListener(() -> {
            universe.getBodies().stream().filter(body -> body.getName().equals(bodyToCalculate)).forEach(body -> {
                if(integrator.getIntegratorName().equals("RK4")) Graph.plotBody(body);
                TreeMap<Double,TreeMap<Double,Double>> timestepErrors = intergratorTimestepErrors.get(integrator);
                TreeMap<Double,Double> accumulatedError = integratorAccumulatedError.get(integrator);

                TreeMap<Double,Double> deltas = CSVWriter.writeBodyDelta(body);
                timestepErrors.put(dt,deltas);
                accumulatedError.put(dt,deltas.values().stream().reduce(0.0, Double::sum));

                integratorAccumulatedError.put(integrator,accumulatedError);
                intergratorTimestepErrors.put(integrator,timestepErrors);
            });

        });
        return universe;
    }

    private static void plot(){
        Plot plt = Graph.getPlot();
        plt.figure("Timestep Errors - "+bodyToCalculate+" - "+Arrays.toString(timesteps));

        int index = 1;
        final double[] minMaxY = {0.0,0.0};
        for(Map.Entry<Integrator,TreeMap<Double,TreeMap<Double,Double>>> entry : intergratorTimestepErrors.entrySet()) {
            entry.getValue().values().forEach(map -> {
                Optional<Map.Entry<Double,Double>> max = map.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue));
                if(max.isPresent() && max.get().getValue() > minMaxY[1]) minMaxY[1] = max.get().getValue();
                Optional<Map.Entry<Double,Double>> min = map.entrySet().stream().min(Comparator.comparing(Map.Entry::getValue));
                if(min.isPresent() && min.get().getValue() < minMaxY[0]) minMaxY[0] = min.get().getValue();
            });
        }
        for(Map.Entry<Integrator,TreeMap<Double,TreeMap<Double,Double>>> entry : intergratorTimestepErrors.entrySet()) {
            plt.subplot(rows, cols, index);
            plt.title(entry.getKey().getIntegratorName());
            final int[] pos = {0};
            TreeMap<Double,TreeMap<Double,Double>> timestepErrors = entry.getValue();
            timestepErrors.forEach((timestep, errors) -> {
                System.out.println(entry.getKey().getIntegratorName()+" time-step: "+timestep);
                List<Number> step = new ArrayList<>();
                List<Number> errorVals = new ArrayList<>();
                for (Map.Entry<Double, Double> values : errors.entrySet()) {
                    step.add(values.getKey());
                    errorVals.add(values.getValue());
                }
                plt.plot()
                        .add(step, errorVals)
                        .label("$\\Delta{T} = $" + timestep)
                        .linestyle("solid")
                        .linewidth("1")
                        .color("C" + pos[0]);
                plt.xlabel("Time (Days)");
                plt.ylabel("Error (AU)");
                plt.legend().loc("upper left");
                plt.ylim(minMaxY[0],minMaxY[1]);

                pos[0]++;
            });
            index++;
        }


        Graph.openPlot(plt);


    }

    private static void plotAccumulated(){
        Plot plt = Graph.getPlot();
        plt.figure("Accumulated Errors "+bodyToCalculate+" "+Arrays.toString(timesteps));
        plt.subplot(1, 1, 1);
        final int[] pos = {0};

        for(Map.Entry<Integrator,TreeMap<Double,Double>> entry : integratorAccumulatedError.entrySet()) {
            TreeMap<Double,Double> accumulatedError = entry.getValue();
            List<Number> step = new ArrayList<>();
            List<Number> errorVals = new ArrayList<>();
            accumulatedError.forEach((timestep, error) -> {
                step.add(timestep);
                errorVals.add(error);
            });
            plt.plot()
                    .add(step, errorVals)
                    .label(entry.getKey().getIntegratorName())
                    .linestyle("solid")
                    .color("C" + pos[0]);
            plt.xlabel("Time-Step (Days)");
            plt.ylabel("Accumulated Error (AU)");
            plt.legend().loc("upper left");

            pos[0]++;
        }

        Graph.openPlot(plt);
    }
}
