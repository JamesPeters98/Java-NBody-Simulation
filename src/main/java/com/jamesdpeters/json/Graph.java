package com.jamesdpeters.json;

import com.github.sh0nk.matplotlib4j.*;
import com.github.sh0nk.matplotlib4j.builder.PlotBuilder;
import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.eclipse.TransitInfo;
import com.jamesdpeters.helpers.MultiScatter;
import com.jamesdpeters.helpers.Utils;
import com.jamesdpeters.helpers.chisquare.Value;
import com.jamesdpeters.universes.Universe;
import com.jamesdpeters.vectors.Vector3D;
import org.jzy3d.chart.Chart;
import org.jzy3d.chart.ChartLauncher;
import org.jzy3d.chart.Settings;
import org.jzy3d.chart.factories.AWTChartComponentFactory;
import org.jzy3d.colors.Color;
import org.jzy3d.maths.Coord3d;
import org.jzy3d.maths.Rectangle;
import org.jzy3d.plot3d.primitives.Scatter;
import org.jzy3d.plot3d.rendering.canvas.Quality;

import java.io.IOException;
import java.util.*;

public class Graph {

    public static void plotTrajectory(Body body) {
                Scatter scatter = getScatter(body);
                Chart chart = AWTChartComponentFactory.chart(Quality.Fastest, "newt");
                chart.getScene().add(scatter);

                Settings.getInstance().setHardwareAccelerated(true);
                System.out.println("------------------------------------");
                ChartLauncher.instructions();
                ChartLauncher.openChart(chart, new Rectangle(200, 200, 1280, 720), body.getName());
    }

    public static void plotTrajectory(Universe universe, int resolution){
        MultiScatter scatter = getMultiScatter(universe,resolution);

        Chart chart = AWTChartComponentFactory.chart(Quality.Nicest, "newt");
        chart.getScene().add(scatter);

        Settings.getInstance().setHardwareAccelerated(true);
        System.out.println("------------------------------------");
        ChartLauncher.instructions();
        ChartLauncher.openChart(chart, new Rectangle(200, 200, 1280, 720), universe.getName());
    }

    public static void plotStationaryPoints(List<Vector3D> points, Color[] colors){
        Coord3d[] coords = new Coord3d[points.size()];
        for(int i=0; i<points.size(); i++){
            coords[i] = Utils.fromPoint3D(points.get(i));
        }
        Scatter scatter = new Scatter(coords);
        scatter.setWidth(10f);
        scatter.setColors(colors);

        Chart chart = AWTChartComponentFactory.chart(Quality.Nicest, "newt");
        chart.getScene().add(scatter);

        Settings.getInstance().setHardwareAccelerated(true);
        ChartLauncher.openChart(chart, new Rectangle(200, 200, 1280, 720), "Points Plot");
    }

    private static Scatter getScatter(Body body){
        Coord3d[] coords = new Coord3d[body.positions.size()];
        int i=0;
        for(Vector3D vector3D : body.positions.values()) {
            coords[i] = Utils.fromPoint3D(vector3D);
            i++;
        }
        return new Scatter(coords);
    }

    private static MultiScatter getMultiScatter(Universe universe, int resolution){
        List<List<Coord3d>> coordList = new ArrayList<>();
        Color[] colors = new Color[universe.getBodies().size()];
        int k=0;
        for(Body body : universe.getBodies()){
            List<Coord3d> coords = new ArrayList<>();
            int point=resolution;

            for(Vector3D vector3D : body.positions.values()) {
                point++;
                if(point >= resolution) { // Ignore points between the resolution being used.
                    coords.add(Utils.fromPoint3D(vector3D));
                    point = 0;
                }
            }

            coordList.add(coords);
            colors[k] = Utils.convert(body.getColor());
            k++;
            System.out.println("Points: "+coords.size());
        }

        return new MultiScatter(coordList,colors,1.0f);
    }

    public static void plotBody(Body body) {
        System.out.println("Plotting 2D for " + body.getName());
        Body origin = body.getUniverse().getOriginBody();

        // SIM DATA
        List<Number> time = new ArrayList<>();
        List<Number> x = new ArrayList<>();
        List<Number> y = new ArrayList<>();
        List<Number> z = new ArrayList<>();
        List<Number> mag = new ArrayList<>();

        int res = 1;
        final int[] point = {res};
        body.positions.forEach((step, point3D) -> {
            if (point[0] >= res) {
                Vector3D originPoint = origin.positions.get(step);
                point3D = point3D.subtract(originPoint);

                time.add(step*body.getUniverse().dt());
                x.add(point3D.getX());
                y.add(point3D.getY());
                z.add(point3D.getZ());
                mag.add(point3D.magnitude());
                point[0] = 0;
            }
            point[0]++;
        });

        // REAL DATA
        List<Number> timeJPL = new ArrayList<>();
        List<Number> xJPL = new ArrayList<>();
        List<Number> yJPL = new ArrayList<>();
        List<Number> zJPL = new ArrayList<>();
        List<Number> magJPL = new ArrayList<>();

        if(body.getJPLPositions() != null) {
            body.getJPLPositions().forEach((t, point3D) -> {
                timeJPL.add(t);
                xJPL.add(point3D.getX());
                yJPL.add(point3D.getY());
                zJPL.add(point3D.getZ());
                magJPL.add(point3D.magnitude());
            });
        }

        Plot plt = getPlot();
        plt.figure(body.getName());
        plt.subplot(2, 2, 1);
        plotData(plt, time, x, "X Simulated", "blue");
        plotData(plt, timeJPL, xJPL, "X JPL", "red");

        plt.subplot(2, 2, 2);
        plotData(plt, time, y, "Y Simulated", "blue");
        plotData(plt, timeJPL, yJPL, "Y JPL", "red");

        plt.subplot(2, 2, 3);
        plotData(plt, time, z, "Z Simulated", "blue");
        plotData(plt, timeJPL, zJPL, "Z JPL", "red");

        plt.subplot(2, 2, 4);
        plotData(plt, time, mag, "R Simulated", "blue");
        plotData(plt, timeJPL, magJPL, "R JPL", "red");

        //plt.title(body.getName());
        openPlot(plt);
    }

    private static void plotEclipse(Plot plt, TreeMap<Double,Double> area, String color, String datasetName, String linestyle, String fmt){
        List<Number> time = new ArrayList<>();
        List<Number> areas = new ArrayList<>();

        area.forEach((t, a) -> {
            time.add(t);
            areas.add(a);
        });

        plotData(plt,time,areas,datasetName,color,linestyle,fmt);
    }

    private static void plotEclipseValue(Plot plt, TreeMap<Double,Value> area, String color, String datasetName){
        List<Number> time = new ArrayList<Number>();
        List<Number> areas = new ArrayList<>();

        area.forEach((t, a) -> {
            time.add(t);
            areas.add(a.value);
        });

        PlotBuilder plotBuilder = plt.plot()
                .add(time,areas,".")
                .label(datasetName)
                .color(color);
        plt.xlabel("Day");
        plt.ylabel("Position (AU)");
        plt.legend().loc("upper right");
    }

    public static void plotEclipse(String title, TreeMap<Double,Double> area, TreeMap<Double, Double> JPLArea){
        Plot plt = getPlot();
        plt.figure(title);
        plt.subplot(1,1,1);
        plotEclipse(plt,area, "blue","Simulated Data","solid","");
        if(JPLArea != null) plotEclipse(plt,JPLArea, "red", "JPL Data","","r*");
        plt.ylabel("Luminosity Ratio (L/L0)");
        openPlot(plt);
    }

    public static void plotEclipse(String title, Map<Body,TreeMap<Double, Double>> areas, TreeMap<Double, Double> totalArea){
        Plot plt = getPlot();
        plt.figure(title);
        plt.subplot(1,1,1);
        plotEclipse(plt,totalArea, "black","Total Luminosity","solid","");
        areas.forEach((planet, map) -> {
            plotEclipse(plt,map, Utils.colorToHex(planet.getColor()),planet.getName(),"solid","");
        });
        plt.ylabel("Luminosity Ratio (L/L0)");
        openPlot(plt);
    }

    public static void plotEclipseWithObservations(String title, TransitInfo transitInfo, TreeMap<Double, Value> experimentalData){
        Plot plt = getPlot();
        plt.figure(title);
        plt.subplot(1,1,1);
        plotEclipse(plt,transitInfo.totalTransits, "black","Total Luminosity","solid","");
        transitInfo.transits.forEach((planet, map) -> {
            plotEclipse(plt,map, Utils.colorToHex(planet.getColor()),planet.getName(),"solid","");
        });
        plotEclipseValue(plt,experimentalData,"r","Experimental Data");
        plt.ylabel("Luminosity Ratio (L/L0)");
        openPlot(plt);
    }

    public static void openPlot(Plot plt){
        Runnable run = () -> {
            try {
                plt.show();
            } catch (IOException | PythonExecutionException e) {
                e.printStackTrace();
            }
        };

        Thread thread = new Thread(run);
        thread.start();
    }

    public static Plot getPlot(){
        PythonConfig config = PythonConfig.pythonBinPathConfig("C:\\Users\\James\\AppData\\Local\\Programs\\Python\\Python37\\python.exe");
        return Plot.create(config);
    }

    public static PlotBuilder plotData(Plot plt, List<Number> x, List<Number> y, String label, String color){
        return plotData(plt, x, y, label, color,"solid","");
    }

    public static PlotBuilder plotData(Plot plt, List<Number> x, List<Number> y, String label, String color, String linestyle, String fmt){
        PlotBuilder plotBuilder = plt.plot()
                .add(x,y,fmt)
                .label(label)
                .linestyle(linestyle)
                .color(color);
        plt.xlabel("Day");
        plt.ylabel("Position (AU)");
        plt.legend().loc("upper right");

        return plotBuilder;
    }

    public static void main(String[] args) {
        Plot plt = getPlot();
        plt.figure("Title");

        List<Number>
                x = Arrays.asList(1,2,3,4,5),
                y = Arrays.asList(2,4,6,8,10);

        String fmt = "ro";

        plt.plot()
                .add(x,y,fmt)
                .label("label")
                .linestyle("solid");
        plt.xlabel("X");
        plt.ylabel("Y");
        plt.legend().loc("upper right");

        openPlot(plt);
    }
    }
