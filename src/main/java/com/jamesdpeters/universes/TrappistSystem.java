package com.jamesdpeters.universes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jamesdpeters.StartUniverse;
import com.jamesdpeters.builders.UniverseBuilder;
import com.jamesdpeters.builders.trappist.UniverseBuilderTrappist;
import com.jamesdpeters.eclipse.TransitCalculator;
import com.jamesdpeters.eclipse.TransitInfo;
import com.jamesdpeters.helpers.chisquare.ChiSquaredFitter;
import com.jamesdpeters.helpers.polynomialfit.PolynomialFit;
import com.jamesdpeters.vectors.Vector3D;

import java.io.File;
import java.io.IOException;

public class TrappistSystem extends SolarSystem {

    private Vector3D directionForTransits = new Vector3D(1,0,0);

    public TrappistSystem() {
        System.out.println("TRAPPIST");
        setRunningTime(10);
    }

    @Override
    protected UniverseBuilder getBuilder(String jsonFileName) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            String jsonFile = StartUniverse.class.getResource(getJsonFilePath()).getFile();
            return UniverseBuilderTrappist.getInstance().fromFile(gson, new File(jsonFile));
        } catch (Exception e){e.printStackTrace();}
        return null;
    }

    @Override
    protected String getJsonFilePath() {
        return "/TrappistPolyFit.json";
    }

    @Override
    protected void onFinish() {
        //Graph.plotTrajectory(this, 10);

        try {
            TransitInfo info = TransitCalculator.plotTotalTransits(this,directionForTransits,false);
            ChiSquaredFitter fitter = new ChiSquaredFitter();
            fitter.load("/trappist_data/nature_data.csv",7650.915787);
            fitter.outputData("Trappist",info.totalTransits);
            fitter.plot(info);

            PolynomialFit fit = new PolynomialFit();
            fit.fit(info.totalTransits,0,0.0142,0); //1C
//            //fit.fit(info.totalTransits,0.961,0.982, 0.96754); //1B
//            fit.fit(info.totalTransits,3.023,3.053, 3.02346); //1D
//            //fit.fit(info.totalTransits,3.362,3.395, 3.36347); //1E
//            //fit.fit(info.totalTransits,11.29,11.33, 11.31); //1F
//            //fit.fit(info.totalTransits,14.49,14.53, 14.51); //1G
//            fit.fit(info.totalTransits,11.653,11.7, 11.68); //1H
            fit.fitObservations("/trappist_data/nature_data.csv",7650.915787,0.00001,300);


            double redChi2 = fitter.chiSquare(info.totalTransits);
            System.out.println("Fitted Reduced Chi^2 Value : "+redChi2);
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
    public int resolution() {
        //dt needs to be a factor of (1/24)
        return 10;
    }
}
