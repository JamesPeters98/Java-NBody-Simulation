package com.jamesdpeters;

import com.jamesdpeters.eclipse.EclipseCalculator;
import com.jamesdpeters.integrators.IntegratorFactory;
import com.jamesdpeters.json.CSVWriter;
import com.jamesdpeters.json.Graph;
import com.jamesdpeters.universes.SolarSystem;

public class StartUniverse {

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


//        SolarSystem universe = new SolarSystem("/BodyEclipse.json");
//        universe.setOutput(true);
//        universe.setRunningTime(0.25*365);
//        universe.setIntegrator(IntegratorFactory.getRK4Integrator());
//        universe.overrideTimeStep(0.0001);
//        universe.addOnFinishListener(() -> {
//            EclipseCalculator.findEclipses(universe);
//            //universe.getBodies().forEach(body -> CSVWriter.writeBody(body,10));
//            //Graph.plotTrajectory(universe,1);
//        });
//        universe.start();

        SolarSystem universe = new SolarSystem("/Body.json");
        universe.setOutput(true);
        universe.setRunningTime(30*365);
        universe.setIntegrator(IntegratorFactory.getRK4Integrator());
        double dt = 1;
        double res = 96; //Store every 15 days of data.
        universe.overrideTimeStep(dt);
        universe.setResolution((int) (res/dt));
        universe.addOnFinishListener(() -> {
            //EclipseCalculator.findEclipses(universe);
            universe.getBodies().stream().filter(body -> body.getName().equals("Moon")).forEach(body -> {
                Graph.plotBody(body);
                CSVWriter.writeBodyDelta(body);
            });
            //universe.getBodies().forEach(Graph::plotBody);
            //universe.getBodies().forEach(body -> CSVWriter.writeBody(body,10));
            //Graph.plotTrajectory(universe,1);
        });
        universe.start();

    }
}
