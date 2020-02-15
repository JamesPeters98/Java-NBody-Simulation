package com.jamesdpeters.json;

import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.eclipse.EclipseInfo;
import com.jamesdpeters.helpers.Constants;
import com.jamesdpeters.universes.Universe;
import com.jamesdpeters.vectors.Vector3D;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CSVWriter {

    // Resolution = Number of points to discard between each CSV record.
    public static void writeBody(Body body, int resolution) throws IOException {
        Path path = Paths.get("outputs/"+body.getUniverse().getName()+"/"+body.getUniverse().getIntegrator().getIntegratorName()+"/"+body.getName()+"/"+body.getName()+"_data_"+body.getUniverse().dt()+".csv");
        Files.createDirectories(path.getParent());
        FileWriter file = new FileWriter(String.valueOf(path));     // this creates the file with the given name
        PrintWriter outputFile = new PrintWriter(file); // this sends the output to file1

        // Write the file as a comma seperated file (.csv) so it can be read it into EXCEL
        // first some general information about the histogram
//        outputFile.println("dt, " + body.getUniverse().dt());
//        outputFile.println("universe, " + body.getUniverse().getName());
//        outputFile.println("GM, " + body.getGM());
//        outputFile.println("Body Name, " + body.getName());
//        outputFile.println("Initial Pos X, " + body.getInitialPosition().getX());
//        outputFile.println("Initial Pos Y, " + body.getInitialPosition().getY());
//        outputFile.println("Initial Pos Z, " + body.getInitialPosition().getZ());
//
//        outputFile.println(",");
        outputFile.println("time (days), x, y, z, magnitude (A.U)");
        double dt = body.getUniverse().dt();

        // now make a loop to write the contents of each bin to disk, one number at a time
        // together with the x-coordinate of the centre of each bin.
        AtomicInteger points = new AtomicInteger(resolution);
        body.positions.forEach((time, point3D) -> {
            points.incrementAndGet();
            if(points.get() >= resolution) {
                // comma separated values
                Vector3D origin = body.getUniverse().getOriginBody().positions.get(time);
                point3D = point3D.subtract(origin);
                outputFile.println(time*dt + "," + point3D.getX() + "," + point3D.getY() + "," + point3D.getZ()+","+point3D.magnitude());
                points.set(0);
            }
        });
//        outputFile.println("***************************");
//        if(body.getJPLPositions() != null) {
//            body.getJPLPositions().forEach((day, point3D) -> {
//                outputFile.println(Constants.SECONDS.DAY * day + "," + point3D.getX() + "," + point3D.getY() + "," + point3D.getZ() + "," + point3D.magnitude());
//            });
//        }

        outputFile.close(); // close the output file
        System.out.println("Written CSV Data for: "+body.getName());
    }

    public static void writeEnergyShift(Universe universe) throws IOException {
        Path path = Paths.get("outputs/"+universe.getName()+"/Energyshift/"+universe.dt()+"-dt/"+universe.getIntegrator().getIntegratorName()+"-"+universe.getName()+".csv");
        Files.createDirectories(path.getParent());
        FileWriter file = new FileWriter(String.valueOf(path));     // this creates the file with the given name
        PrintWriter outputFile = new PrintWriter(file); // this sends the output to file1

        double initEnergy = universe.energyShift.get(0.0);

        // Write the file as a comma seperated file (.csv) so it can be read it into EXCEL
        // first some general information about the histogram
        outputFile.println("dt, " + universe.dt());
        outputFile.println("universe, " + universe.getName());
        outputFile.println("initial Energy (J), "+ initEnergy);

        outputFile.println(",");
        outputFile.println("time, energy change (J)");

        universe.energyShift.forEach((time, energy) -> {
            outputFile.println(time+","+(initEnergy-energy));
        });

        outputFile.close(); // close the output file
        System.out.println("Written CSV Data for: Energy Shift");
    }

    public static void writeEclipseData(String folderPath, TreeMap<Double,Double> coneRadius, TreeMap<Double,Double> edgeOfMoonDist,TreeMap<Double,Double> lambda,TreeMap<Double,Double> area) throws IOException {
        Path path = Paths.get("outputs/eclipse/"+folderPath+"/data.csv");
        Files.createDirectories(path.getParent());
        FileWriter file = new FileWriter(String.valueOf(path));     // this creates the file with the given name
        PrintWriter outputFile = new PrintWriter(file); // this sends the output to file1

        outputFile.println("time, Cone Radius, Edge Of Moon Distance, Lambda, Area");
        for(Double time: coneRadius.keySet()){
            outputFile.println(time+","+coneRadius.get(time)+","+edgeOfMoonDist.get(time)+","+lambda.get(time)+","+area.get(time));
        }
        outputFile.close(); // close the output file
        System.out.println("Saved CSV Data for: Eclipse - "+folderPath);
    }

    public static void writeEclipseInfo(String folderPath, HashMap<Integer, EclipseInfo> eclipseInfo, Universe universe) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MMM-dd");
        Path path = Paths.get("outputs/eclipse/"+folderPath +"/eclipseInfo-" +universe.getOriginBody().getStartDate().format(formatter) +"-dt-"+universe.dt() +"-"+universe.getIntegrator().getIntegratorName()+".csv");
        Files.createDirectories(path.getParent());
        FileWriter file = new FileWriter(String.valueOf(path));     // this creates the file with the given name
        PrintWriter outputFile = new PrintWriter(file); // this sends the output to file1

        outputFile.println("Eclipse, Start Date, End Date, Duration (Seconds)");
        for(Map.Entry<Integer,EclipseInfo> entry : eclipseInfo.entrySet()){
            outputFile.println(entry.getKey()+","+entry.getValue().startDate+","+entry.getValue().endDate+","+entry.getValue().getDuration().toSeconds());
        }
        outputFile.close(); // close the output file
        System.out.println("Saved CSV Data for: Eclipse Info - "+folderPath);
    }
}
