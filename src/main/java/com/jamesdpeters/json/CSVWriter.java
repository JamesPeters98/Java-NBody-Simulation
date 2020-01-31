package com.jamesdpeters.json;

import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.universes.Universe;
import com.jamesdpeters.vectors.Vector3D;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class CSVWriter {

    // Resolution = Number of points to discard between each CSV record.
    public static void writeBody(Body body, int resolution) throws IOException {
        Path path = Paths.get("outputs/"+body.integrator.getIntegratorType().getIntegratorName()+"/"+body.getName()+"/"+body.getName()+"_data_"+body.getUniverse().dt()+".csv");
        Files.createDirectories(path.getParent());
        FileWriter file = new FileWriter(String.valueOf(path));     // this creates the file with the given name
        PrintWriter outputFile = new PrintWriter(file); // this sends the output to file1

        // Write the file as a comma seperated file (.csv) so it can be read it into EXCEL
        // first some general information about the histogram
        outputFile.println("dt, " + body.getUniverse().dt());
        outputFile.println("universe, " + body.getUniverse().getName());
        outputFile.println("GM, " + body.getGM());
        outputFile.println("Body Name, " + body.getName());
        outputFile.println("Initial Pos X, " + body.getInitialPosition().getX());
        outputFile.println("Initial Pos Y, " + body.getInitialPosition().getY());
        outputFile.println("Initial Pos Z, " + body.getInitialPosition().getZ());

        outputFile.println(",");
        outputFile.println("time, x, y, z, magnitude");

        // now make a loop to write the contents of each bin to disk, one number at a time
        // together with the x-coordinate of the centre of each bin.
        AtomicInteger points = new AtomicInteger(resolution);
        body.positions.forEach((time, point3D) -> {
            points.getAndIncrement();
            if(points.get() >= resolution) {
                // comma separated values
                Vector3D origin = body.getUniverse().getOriginBody().positions.get(time);
                point3D = point3D.subtract(origin);
                outputFile.println(time + "," + point3D.getX() + "," + point3D.getY() + "," + point3D.getZ()+","+point3D.magnitude());
                points.set(0);
            }
        });
        outputFile.println("***************************");
        body.getJPLPositions().forEach((day, point3D) -> {
            outputFile.println(TimeUnit.DAYS.toSeconds(day) + "," + point3D.getX() + "," + point3D.getY() + "," + point3D.getZ()+","+point3D.magnitude());
        });

        outputFile.close(); // close the output file
        System.out.println("Written CSV Data for: "+body.getName());
    }

    public static void writeEnergyShift(Universe universe) throws IOException {
        Path path = Paths.get("outputs/"+universe.getName()+"-"+universe.dt()+".csv");
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

    public static void writeEclipseData(TreeMap<Double,Double> coneRadius, TreeMap<Double,Double> edgeOfMoonDist,TreeMap<Double,Double> lambda) throws IOException {
        Path path = Paths.get("outputs/eclipse/"+"data.csv");
        Files.createDirectories(path.getParent());
        FileWriter file = new FileWriter(String.valueOf(path));     // this creates the file with the given name
        PrintWriter outputFile = new PrintWriter(file); // this sends the output to file1

        outputFile.println("time, Cone Radius, Edge Of Moon Distance, Lambda");
        for(Double time: coneRadius.keySet()){
            outputFile.println(time+","+coneRadius.get(time)+","+edgeOfMoonDist.get(time)+","+lambda.get(time));
        }
        outputFile.close(); // close the output file
        System.out.println("Written CSV Data for: Eclipse");
    }
}
