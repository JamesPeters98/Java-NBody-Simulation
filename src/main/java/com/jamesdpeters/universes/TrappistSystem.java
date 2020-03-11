package com.jamesdpeters.universes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jamesdpeters.StartUniverse;
import com.jamesdpeters.builders.UniverseBuilder;
import com.jamesdpeters.builders.trappist.UniverseBuilderTrappist;
import com.jamesdpeters.eclipse.TransitCalculator;
import com.jamesdpeters.eclipse.TransitInfo;
import com.jamesdpeters.helpers.chisquare.ChiSquaredFitter;
import com.jamesdpeters.json.Graph;
import com.jamesdpeters.vectors.Vector3D;

import java.io.File;
import java.io.IOException;
import java.util.TreeMap;

public class TrappistSystem extends SolarSystem {

    private Vector3D directionForTransits = new Vector3D(1,0,0);

    public TrappistSystem() {
        System.out.println("TRAPPIST");
    }

    @Override
    protected UniverseBuilder getBuilder() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            String jsonFile = StartUniverse.class.getResource(getJsonFilePath()).getFile();
            return UniverseBuilderTrappist.getInstance().fromFile(gson, new File(jsonFile));
        } catch (Exception e){e.printStackTrace();}
        return null;
    }

    @Override
    protected String getJsonFilePath() {
        return "/Trappist.json";
    }

    @Override
    protected void onFinish() {
        Graph.plotTrajectory(this, 10);

        try {
            TransitInfo info = TransitCalculator.plotTotalTransits(this,directionForTransits);
            ChiSquaredFitter fitter = new ChiSquaredFitter();
            fitter.load("/trappist_data/nature_data.csv",7650.915787);
            fitter.outputData("Trappist",info.totalTransits);
            fitter.plot(info);
//            double redChi2 = fitter.chiSquare(model);
//            System.out.println("Fitted Reduced Chi^2 Value : "+redChi2);
        } catch (IOException e) { e.printStackTrace(); }

//        for (Body body : bodies) {
//            Graph.plotBody(body);
//            try {
//                CSVWriter.writeBody(body, 1);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public double runningTime() {
        return 30; // Run for 500 Simulated Days
    }

    @Override
    public int resolution() {
        //dt needs to be a factor of (1/24)
        return Math.max((int) ((1/24)/dt()),1);
    }
}
