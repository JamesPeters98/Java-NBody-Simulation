package com.jamesdpeters.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jamesdpeters.StartUniverse;
import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.builders.UniverseBuilder;
import com.jamesdpeters.builders.trappist.TrappistBody;
import com.jamesdpeters.builders.trappist.UniverseBuilderTrappist;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;

public class JsonConverterTrappist {

    public JsonConverterTrappist() throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

//        try {
//            String jsonFile = getClass().getResource("Body.json").getFile();
//            UniverseBuilder builder = UniverseBuilder.getInstance().fromFile(new Gson(), new File(jsonFile));
//            System.out.println(builder.getName());
//        } catch (Exception e){e.printStackTrace();}

//        BodyBuilder builder = BodyBuilder.getInstance()
//                .setName("Test")
//                .setInitPos(new Point3D(0,0,0))
//                .setInitVelocity(new Point3D(-1.12*Math.pow(10,1),2.77*Math.pow(10,1),-1.66*Math.pow(10,-3)))
//                .setMass(1.32*Math.pow(10,24))
//                .setRadius(6000);
//
//        String Universe = UniverseBuilder.getInstance()
//                .setName("Universe")
//                .setDt(0.1)
//                .setG(6.67e-11)
//                .addBody(builder)
//                .addBody(builder)
//                .serialise(gson);

        TrappistBody info = new TrappistBody();
        info.setName("Test Body");
        info.setEarthMassRatio(0.9);
        info.setInclination(87);
        info.setOrbitalPeriod(1.5);
        info.setSemiMajorAxis(1);
        info.setStartPhase(0);
        info.setEarthRadiusRatio(1);
        info.setColor("#FF0000");
        info.setMeanAnomaly(36.0);
        info.setPeriapsisArgument(235.23);

        UniverseBuilderTrappist builder = UniverseBuilderTrappist.getInstance();
        builder.setDt(0.1);
        builder.setName("Universe");
        builder.addBodyInfo(info);
        builder.addBodyInfo(info);
        builder.setStartDate(LocalDateTime.of(2020,10,12,0,0));

        String Universe = builder.serialise(gson);
        System.out.println(Universe);

        String file = StartUniverse.class.getResource("").getFile();    // Resource Folder
        File f = new File(file+"/TrappistDummy.json");
        System.out.println(f.getPath());
        FileUtils.writeStringToFile(f,Universe, Charset.defaultCharset());

        UniverseBuilderTrappist universeBuilder = UniverseBuilderTrappist.getInstance().fromFile(gson,f);
        System.out.println(universeBuilder.serialise(gson));
//        for(TrappistBody body : universeBuilder.getTrappistBodies()){
//            System.out.println(body.getBodyBuilder().serialise(gson));
//        }

    }

    public static void main(String[] args) throws IOException {
        new JsonConverterTrappist();
    }
}
