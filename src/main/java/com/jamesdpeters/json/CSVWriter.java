package com.jamesdpeters.json;

import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.bodies.ErrorCalculator;
import com.jamesdpeters.eclipse.EclipseInfo;
import com.jamesdpeters.helpers.Constants;
import com.jamesdpeters.helpers.chisquare.ChiSquareValue;
import com.jamesdpeters.universes.Universe;
import com.jamesdpeters.vectors.Vector3D;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CSVWriter {

    private static PrintWriter getOutputFile(String filenamePath){
        try {
            Path path = Paths.get("outputs/"+filenamePath);
            Files.createDirectories(path.getParent());
            FileWriter file = new FileWriter(String.valueOf(path));     // this creates the file with the given name
            return new PrintWriter(file); // this sends the output to file
        } catch (IOException e) {
            System.err.println("File couldn't be accessed it may be being used by another process!");
            System.err.println("Close the file and press Enter to try again!");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try { reader.readLine(); } catch (IOException ex) { ex.printStackTrace(); }
            return getOutputFile(filenamePath);
        }
    }

    // Resolution = Number of points to discard between each CSV record.
    public static void writeBody(Body body, int resolution) {
        PrintWriter outputFile = getOutputFile(body.getUniverse().getName()+"/"+body.getUniverse().getIntegrator().getIntegratorName()+"/"+body.getName()+"/"+body.getName()+"_data_"+body.getUniverse().dt()+".csv"); // this sends the output to file1
        if(outputFile != null) {
            // Write the file as a comma seperated file (.csv) so it can be read it into EXCEL
            outputFile.println("Time (Days), X, Y, X, Magnitude (A.U)");
            double dt = body.getUniverse().dt();

            // now make a loop to write the contents of each step to disk, one number at a time
            AtomicInteger points = new AtomicInteger(resolution);
            body.positions.forEach((time, point3D) -> {
                points.incrementAndGet();
                if (points.get() >= resolution) {
                    // comma separated values
                    Vector3D origin = body.getUniverse().getOriginBody().positions.get(time);
                    point3D = point3D.subtract(origin);
                    outputFile.println(time * dt + "," + point3D.getX() + "," + point3D.getY() + "," + point3D.getZ() + "," + point3D.magnitude());
                    points.set(0);
                }
            });
            outputFile.close(); // close the output file
            System.out.println("Written CSV Data for: " + body.getName());
        }
    }

    public static void writeEnergyShift(Universe universe) {
        PrintWriter outputFile = getOutputFile(universe.getName()+"/Energyshift/"+universe.dt()+"-dt/"+universe.getIntegrator().getIntegratorName()+"-"+universe.getName()+".csv");
        double initEnergy = universe.energyShift.get(0.0);
        if(outputFile != null) {
            // Write the file as a comma seperated file (.csv) so it can be read it into EXCEL
            outputFile.println("dt, " + universe.dt());
            outputFile.println("universe, " + universe.getName());
            outputFile.println("initial Energy (J), " + initEnergy);

            outputFile.println(",");
            outputFile.println("time, energy change (J)");

            universe.energyShift.forEach((time, energy) -> {
                outputFile.println(time + "," + (initEnergy - energy));
            });

            outputFile.close(); // close the output file
            System.out.println("Written CSV Data for: Energy Shift");
        }
    }

    public static void writeEclipseData(String folderPath, TreeMap<Double,Double> coneRadius, TreeMap<Double,Double> edgeOfMoonDist,TreeMap<Double,Double> lambda,TreeMap<Double,Double> area) {
        PrintWriter outputFile = getOutputFile("eclipse/"+folderPath+"/data.csv"); // this sends the output to file1
        if (outputFile != null) {
            outputFile.println("time, Cone Radius, Edge Of Moon Distance, Lambda, Area");
            for (Double time : coneRadius.keySet()) {
                outputFile.println(time + "," + coneRadius.get(time) + "," + edgeOfMoonDist.get(time) + "," + lambda.get(time) + "," + area.get(time));
            }
            outputFile.close(); // close the output file
            System.out.println("Saved CSV Data for: Eclipse - " + folderPath);
        }
    }

    public static void writeTransitData(String folderPath, TreeMap<Body, TreeMap<Double,Double>> transits, TreeMap<Double,Double> totalTransits) {
        PrintWriter outputFile = getOutputFile("eclipse/"+folderPath+"/totalTransits.csv");
        if(outputFile != null) {
            outputFile.print("time");
            transits.keySet().forEach(body -> outputFile.print(", " + body.getName()));
            outputFile.println(", Total");

            Set<Double> times = transits.get(transits.firstKey()).keySet();
            times.forEach(time -> {
                outputFile.print(time);
                transits.forEach((body, map) -> {
                    outputFile.print(", " + map.get(time));
                });
                outputFile.println(", " + totalTransits.get(time));
            });
            outputFile.close(); // close the output file
            System.out.println("Saved CSV Data for: Total Transits - " + folderPath);
        }
    }

    public static void writeChi2Data(String folderPath, TreeMap<Double,ChiSquareValue> values) {
        PrintWriter outputFile = getOutputFile("eclipse/"+folderPath+"/chi2fit.csv");
        if (outputFile != null) {
            outputFile.println("Time, Model Luminosity, Observed Luminosity, Error, Chi^2");
            values.forEach((time, chiSquareValue) -> {
                outputFile.println(time + ", " + chiSquareValue.model + ", " + chiSquareValue.observed + ", " + chiSquareValue.error + ", " + chiSquareValue.getChi2());
            });
            outputFile.close(); // close the output file
            System.out.println("Saved CSV Data for: Chi^2 fits - " + folderPath);
        }
    }

    public static void writeEclipseInfo(String folderPath, HashMap<Integer, EclipseInfo> eclipseInfo, Universe universe) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MMM-dd");
        PrintWriter outputFile = getOutputFile("eclipse/"+folderPath +"/eclipseInfo-" +universe.getOriginBody().getStartDate().format(formatter) +"-dt-"+universe.dt() +"-"+universe.getIntegrator().getIntegratorName()+".csv");
        if (outputFile != null) {
            outputFile.println("Eclipse, Start Date, Start Time, End Date, End Time, Duration (Seconds), Peak Time");
            for (Map.Entry<Integer, EclipseInfo> entry : eclipseInfo.entrySet()) {
                outputFile.println(entry.getKey() + "," + entry.getValue().startDate.toLocalDate() + "," + entry.getValue().startDate.toLocalTime() + "," + entry.getValue().endDate.toLocalDate() + "," + entry.getValue().endDate.toLocalTime() + "," + entry.getValue().getDuration().toSeconds()+ "," +entry.getValue().midpoint().toLocalTime());
            }
            outputFile.close(); // close the output file
            System.out.println("Saved CSV Data for: Eclipse Info - " + folderPath);
        }
    }


    //Extras
    // Resolution = Number of points to discard between each CSV record.
    public static TreeMap<Double,Double> writeBodyDelta(Body body) {
        PrintWriter outputFile = getOutputFile(body.getUniverse().getName()+"/"+body.getUniverse().getIntegrator().getIntegratorName()+"/"+body.getName()+"/"+body.getName()+"_deltas_"+body.getUniverse().dt()+".csv"); // this sends the output to file1
        TreeMap<Double,Double> deltas = ErrorCalculator.calculate(body);
        if(outputFile != null) {
            // Write the file as a comma seperated file (.csv) so it can be read it into EXCEL
            outputFile.println("Time (Days), Delta (A.U)");
            deltas.forEach((day, error) -> {
                outputFile.println(day+ "," + error);
            });
            outputFile.close(); // close the output file
            System.out.println("Written CSV Data for: " + body.getName()+" Deltas");
        }
        return deltas;
    }
}
