package com.jamesdpeters;

import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.builder.ScaleBuilder;
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
    private static HashMap<Integrator,TreeMap<Double,Double>> integratorComputationalTime;
    private static String bodyToCalculate = "";

    static double[] timesteps;
    static Integrator[] integrators;
    static int cols, rows;

    static double runningTime;

    @SuppressWarnings("ConstantConditions")
    public static void main(String[] args) throws InterruptedException {

        universes = new ArrayList<>();
        intergratorTimestepErrors = new HashMap<>();
        integratorAccumulatedError = new HashMap<>();
        integratorComputationalTime = new HashMap<>();
//        timesteps = new double[]{0.025,0.05,0.1,0.15,0.2,0.25,0.3};
        timesteps = new double[]{0.0025,0.005,0.01,0.05,0.06,0.64,0.075};
        integrators = new Integrator[]{IntegratorFactory.getRK4Integrator(),IntegratorFactory.getYoshidaIntegrator()};
        rows = 2; cols = 2;
        double res = 192; //Store every 96 days of data (96 because it's divisible by lots of decimals e.g 0.1,0.2,0.3,0.4,0.5)
        runningTime = res*25
        ; //Stores 100 deltas.
        bodyToCalculate = "Moon";

        boolean runInParallel = false;

        for(Integrator integrator : integrators) {
            intergratorTimestepErrors.put(integrator, new TreeMap<>());
            integratorAccumulatedError.put(integrator, new TreeMap<>());
            integratorComputationalTime.put(integrator, new TreeMap<>());
            for (double timestep : timesteps) {
                SolarSystem universe = getUniverse(timestep, integrator, runningTime, res);
                universe.start();
                if(!runInParallel){
                    while(!universe.hasFinished()){
                        Thread.sleep(1000);
                    }
                    TreeMap<Double,Double> computationTime = integratorComputationalTime.get(integrator);
                    computationTime.put(timestep,universe.performanceTracker.timeTaken());
                    integratorComputationalTime.put(integrator,computationTime);
                }
                universes.add(universe);
            }
        }

        if(runInParallel) {
            while (true) {
                boolean finished = true;
                for (Universe u : universes) {
                    if (!u.hasFinished()) {
                        finished = false;
                        break;
                    }
                }
                if (finished) break;
                Thread.sleep(1000);
            }
        }
        System.out.println("FINISHED!");
        plotComputationalTime();
        plotComputationalTimeVsError();
        //plot();
        //plotAccumulated();
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
                if((integrator.getIntegratorName().equals("Yoshida") || integrator.getIntegratorName().equals("LeapFrog")) && dt == 0.005) Graph.plotBody(body);
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
        final double[] minMaxY = {1,0.0};
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
                    double val = values.getValue();
                    if(val > 0) {
                        step.add(values.getKey());
                        errorVals.add(Math.log10(val));
                    }
                }
                plt.plot()
                        .add(step, errorVals)
                        .label("$\\Delta{T} = $" + timestep)
                        .linestyle("solid")
                        .linewidth("1")
                        .color("C" + pos[0]);
                plt.xlabel("Time (Days)");
                plt.ylabel("$Log_{10}$ (Error (AU))");
                //plt.yscale(ScaleBuilder.Scale.symlog);
                plt.legend().loc("best");
                System.out.println("Y limit: "+minMaxY[1]);
                plt.ylim(Math.log10(minMaxY[0]),0);

                pos[0]++;
            });
            index++;
        }


        Graph.openPlot(plt);


    }

    private static void plotAccumulated(){
        Plot plt = Graph.getPlot();
        plt.figure("Accumulated Errors "+bodyToCalculate+" "+Arrays.toString(timesteps)+" RunningTime: "+runningTime+" Days");
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
            //plt.yscale(ScaleBuilder.Scale.log);
            plt.legend().loc("best");

            pos[0]++;
        }

        Graph.openPlot(plt);
    }

    private static void plotComputationalTime(){
        Plot plt = Graph.getPlot();
        plt.figure("Computational Time: "+runningTime+" Days");
        plt.subplot(1, 1, 1);
        final int[] pos = {0};

        for(Map.Entry<Integrator,TreeMap<Double,Double>> entry : integratorComputationalTime.entrySet()) {
            TreeMap<Double,Double> computationalTime = entry.getValue();
            List<Number> step = new ArrayList<>();
            List<Number> timeVals = new ArrayList<>();
            computationalTime.forEach((timestep, time) -> {
                step.add(timestep);
                timeVals.add(time);
            });
            plt.plot()
                    .add(step, timeVals)
                    .label(entry.getKey().getIntegratorName())
                    .linestyle("solid")
                    .color("C" + pos[0]);
            plt.xlabel("Time-Step (Days)");
            plt.ylabel("Computational Time (s)");
            plt.legend().loc("best");

            pos[0]++;
        }

        Graph.openPlot(plt);
    }

    private static void plotComputationalTimeVsError(){
        Plot plt = Graph.getPlot();
        plt.figure("Computational Time Vs Error: "+runningTime+" Days");
        plt.subplot(1, 1, 1);
        final int[] pos = {0};

        for(Map.Entry<Integrator,TreeMap<Double,Double>> entry : integratorComputationalTime.entrySet()) {
            TreeMap<Double,Double> computationalTime = entry.getValue();
            TreeMap<Double,Double> accumulatedError = integratorAccumulatedError.get(entry.getKey());
            List<Number> accumulatedErrors = new ArrayList<>();
            List<Number> timeVals = new ArrayList<>();
            computationalTime.forEach((timestep, time) -> {
                accumulatedErrors.add(accumulatedError.get(timestep));
                timeVals.add(time);
            });
            plt.plot()
                    .add(timeVals, accumulatedErrors)
                    .label(entry.getKey().getIntegratorName())
                    .linestyle("solid")
                    .color("C" + pos[0]);
            plt.xlabel("Computational Time (s)");
            plt.ylabel("Accumulated Error (AU)");
            plt.legend().loc("best");

            pos[0]++;
        }

        Graph.openPlot(plt);
    }
}
