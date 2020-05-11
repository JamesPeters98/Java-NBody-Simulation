package com.jamesdpeters;

import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.builder.PlotBuilder;
import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.eclipse.CompareInfo;
import com.jamesdpeters.eclipse.EclipseCalculator;
import com.jamesdpeters.eclipse.EclipseInfo;
import com.jamesdpeters.integrators.IntegratorFactory;
import com.jamesdpeters.integrators.abstracts.Integrator;
import com.jamesdpeters.json.CSVWriter;
import com.jamesdpeters.json.Graph;
import com.jamesdpeters.json.KaggleCSVParser;
import com.jamesdpeters.universes.SolarSystem;
import com.jamesdpeters.vectors.Vector3D;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StartEclipseMeasurement {

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

        HashMap<Long, EclipseInfo> solarMap = KaggleCSVParser.parse("/eclipsedata/solar.csv");
        HashMap<Long, EclipseInfo> lunarMap = KaggleCSVParser.parse("/eclipsedata/lunar.csv");

        SolarSystem universe = new SolarSystem("/BodyExperiments.json");
        universe.setOutput(true);
        universe.setRunningTime(40*365);
        universe.setIntegrator(IntegratorFactory.getYoshidaIntegrator());
        universe.overrideTimeStep(0.0025);
        universe.setResolution(1);
        universe.addOnFinishListener(() -> {
            HashMap<Integer,EclipseInfo> simData = EclipseCalculator.findEclipses(universe);
            compareData(simData,lunarMap, EclipseInfo.Type.LUNAR);
            compareData(simData,solarMap, EclipseInfo.Type.SOLAR);
            //universe.getBodies().stream().filter(body -> body.getName().equals("Earth")).forEach(StartEclipseMeasurement::plotBodyZ);
        });
        universe.start();
    }

    static void compareData(HashMap<Integer,EclipseInfo> simData, HashMap<Long,EclipseInfo> nasaData, EclipseInfo.Type type){
        List<EclipseInfo> sim = simData.values().stream().filter(eclipseInfo -> eclipseInfo.getEclipseType().equals(type)).collect(Collectors.toList());
        LocalDateTime[] minMaxLunar = minMax(sim);
        List<EclipseInfo> nasa = nasaData.values().stream().filter(eclipseInfo -> (eclipseInfo.midpoint().isAfter(minMaxLunar[0]) && eclipseInfo.midpoint().isBefore(minMaxLunar[1]))).collect(Collectors.toList());

        List<CompareInfo> compare = new ArrayList<>();
        for(int i=0; i < nasa.size(); i++){
            EclipseInfo nasaEclipse = nasa.get(i);
            Optional<EclipseInfo> simEclipse = sim.stream().filter(eclipseInfo -> isCloseDate(nasaEclipse,eclipseInfo)).findFirst();
            LocalDateTime simMidpoint = null;
            if(simEclipse.isPresent()) simMidpoint = simEclipse.get().midpoint();
            CompareInfo compareInfo = new CompareInfo(simMidpoint,nasaEclipse.midpoint());
            compare.add(compareInfo);
        }
        CSVWriter.writeEclipseMidpoint(compare,type);
        plot(compare,type.toString());
    }

    static LocalDateTime[] minMax(List<EclipseInfo> simData){
        LocalDateTime[] minMax = new LocalDateTime[2];
        Optional<EclipseInfo> min = simData.stream().min(Comparator.comparing(EclipseInfo::midpoint));
        Optional<EclipseInfo> max = simData.stream().max(Comparator.comparing(EclipseInfo::midpoint));
        if(min.isPresent() && max.isPresent()){
            minMax[0] = min.get().midpoint().minusHours(1);
            minMax[1] = max.get().midpoint().plusHours(1);
        }
        return minMax;
    }

    static boolean isCloseDate(EclipseInfo truePoint, EclipseInfo sim){
        LocalDate nasa = truePoint.midpoint().toLocalDate();
        LocalDate simTime = sim.midpoint().toLocalDate();
        if (nasa.equals(simTime)) return true;
        if(nasa.minusDays(1).equals(simTime)) return true;
        if(nasa.plusDays(1).equals(simTime)) return true;
        return false;
    }


    public static void plot(List<CompareInfo> compareInfos, String title) {
        System.out.println("Plotting Deltas - "+title);

        // SIM DATA
        List<Number> time = new ArrayList<>();
        List<Number> deltas = new ArrayList<>();

        LocalDateTime first = compareInfos.get(0).getNASA();

        compareInfos.forEach(((info) -> {
            if(info.hasSimulatedValue()) {
                time.add(first.until(info.getNASA(), ChronoUnit.SECONDS) / 86400.0);
                deltas.add(info.getDeltaSeconds());
            }
        }));


        Plot plt = Graph.getPlot();
        plt.figure("Eclipse Deltas - "+title);
        plotData(plt, time, deltas, "", "red", "","r*");
        Graph.openPlot(plt);
    }

    public static PlotBuilder plotData(Plot plt, List<Number> x, List<Number> y, String label, String color, String linestyle, String fmt){
        PlotBuilder plotBuilder = plt.plot()
                .add(x,y,fmt)
                .label(label)
                .linestyle(linestyle)
                .linewidth("1")
                .color(color);
        plt.xlabel("Day");
        plt.ylabel("$\\Delta{T}$ (s)");
        //plt.legend().loc("upper right");

        return plotBuilder;
    }
}
