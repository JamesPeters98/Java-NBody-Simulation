package com.jamesdpeters;

import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.builder.PlotBuilder;
import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.bodies.ErrorCalculator;
import com.jamesdpeters.eclipse.EclipseCalculator;
import com.jamesdpeters.integrators.IntegratorFactory;
import com.jamesdpeters.json.CSVWriter;
import com.jamesdpeters.json.Graph;
import com.jamesdpeters.universes.SolarSystem;
import com.jamesdpeters.vectors.Vector3D;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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


        SolarSystem universe = new SolarSystem("/BodyExperiments.json");
        universe.setOutput(true);
        universe.setRunningTime(20*365);
        universe.setIntegrator(IntegratorFactory.getYoshidaIntegrator());
        universe.overrideTimeStep(0.01);
        universe.setResolution(1);
        universe.addOnFinishListener(() -> {
            //EclipseCalculator.findEclipses(universe);
            universe.getBodies().stream().filter(body -> body.getName().equals("Earth")).forEach(body -> {
                plotBodyZ(body);
                //plotBodyDeltas(body);
            });
//            EclipseCalculator.findEclipses(universe);
//            universe.getBodies().forEach(body -> CSVWriter.writeBody(body,10));
//            Graph.plotTrajectory(universe,1);
        });
        universe.start();

//        SolarSystem universe = new SolarSystem("/Body.json");
//        universe.setOutput(true);
//        universe.setRunningTime(30*365);
//        universe.setIntegrator(IntegratorFactory.getRK4Integrator());
//        double dt = 1;
//        double res = 96; //Store every 15 days of data.
//        universe.overrideTimeStep(dt);
//        universe.setResolution((int) (res/dt));
//        universe.addOnFinishListener(() -> {
//            //EclipseCalculator.findEclipses(universe);
//            universe.getBodies().stream().filter(body -> body.getName().equals("Moon")).forEach(body -> {
//                Graph.plotBody(body);
//                CSVWriter.writeBodyDelta(body);
//            });
//            //universe.getBodies().forEach(Graph::plotBody);
//            //universe.getBodies().forEach(body -> CSVWriter.writeBody(body,10));
//            //Graph.plotTrajectory(universe,1);
//        });
//        universe.start();

    }

    public static void plotBodyZ(Body body) {
        System.out.println("Plotting 2D for Z-coord" + body.getName());
        Body origin = body.getUniverse().getOriginBody();

        // SIM DATA
        List<Number> time = new ArrayList<>();
        List<Number> z = new ArrayList<>();

        int res = 1;
        final int[] point = {res};
        double maxStep = body.positions.lastKey();
        body.positions.forEach((step, point3D) -> {
            if (point[0] >= res) {
                Vector3D originPoint = origin.positions.get(step);
                point3D = point3D.subtract(originPoint);

                time.add(step*body.getUniverse().dt());
                z.add(point3D.getZ());
                point[0] = 0;
            }
            point[0]++;
        });

        // REAL DATA
        List<Number> timeJPL = new ArrayList<>();
        List<Number> zJPL = new ArrayList<>();

        if(body.getJPLPositions() != null) {
            for(Map.Entry<Double,Vector3D> entry : body.getJPLPositions().entrySet()){
                double t = entry.getKey();
                if(t > maxStep*body.getUniverse().dt()) break;
                Vector3D point3D = entry.getValue();
                timeJPL.add(t);
                zJPL.add(point3D.getZ());
            }
        }

        Plot plt = Graph.getPlot();
        plt.figure(body.getUniverse().getIntegrator().getIntegratorName()+" - "+body.getName());
        plt.subplot(2,1,2);
        Graph.plotData(plt, timeJPL, zJPL, "", "red");
        Graph.plotData(plt, time, z, "", "blue");

        plt.subplot(2,1,1);
        TreeMap<Double,Double> errorMap = ErrorCalculator.calculateZError(body);

        // SIM DATA
        List<Number> times = new ArrayList<>();
        List<Number> deltas = new ArrayList<>();

        errorMap.forEach((t, delta) -> {
            times.add(t);
            deltas.add(delta);
        });

        plotData(plt, times, deltas, "", "black","solid","");

        Graph.openPlot(plt);
    }

//    public static void plotBodyDeltas(Body body) {
//        TreeMap<Double,Double> errorMap = ErrorCalculator.calculateZError(body);
//
//        // SIM DATA
//        List<Number> times = new ArrayList<>();
//        List<Number> deltas = new ArrayList<>();
//
//        errorMap.forEach((time, delta) -> {
//            times.add(time);
//            deltas.add(delta);
//        });
//
//        Plot plt = Graph.getPlot();
//        plt.figure("Deltas: "+body.getUniverse().getIntegrator().getIntegratorName()+" - "+body.getName());
//        plotData(plt, times, deltas, "", "blue","solid","");
//        Graph.openPlot(plt);
//    }

    public static PlotBuilder plotData(Plot plt, List<Number> x, List<Number> y, String label, String color, String linestyle, String fmt){
        PlotBuilder plotBuilder = plt.plot()
                .add(x,y,fmt)
                .label(label)
                .linestyle(linestyle)
                .color(color);
        plt.ylabel("$\\Delta{Z}$ (A.U)");
        plt.ylim(-0.0001,0.0001);
        //plt.legend().loc("upper right");

        return plotBuilder;
    }
}
