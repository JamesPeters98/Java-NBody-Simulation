package com.jamesdpeters.universes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jamesdpeters.StartUniverse;
import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.builders.UniverseBuilderJPL;
import com.jamesdpeters.helpers.Utils;
import com.jamesdpeters.vectors.EclipseCalculator;
import com.jamesdpeters.vectors.Vector3D;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

public class NormalUniverse extends Universe {

    Collection<Body> callables;
    List<Future> futures;
    UniverseBuilderJPL builder;

    public NormalUniverse() {
        futures = new ArrayList<>();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            String jsonFile = StartUniverse.class.getResource("/Body.json").getFile();
            builder = UniverseBuilderJPL.getInstance().fromFile(gson, new File(jsonFile));
        } catch (Exception e){e.printStackTrace();}

        init();
    }

    @Override
    protected void loop() {
        bodies.forEach(Body::update); // Step each body doesn't update their actual positions.
        //while(!Utils.haveBodiesStoppedRunning(bodies));
        bodies.forEach(Body::postUpdate); // Updates all bodies to new position using the step.
    }

    @Override
    public List<Body> createBodies() {
//        List<Body> bodies = new ArrayList<>();
//        bodies.add(new Sun());
//        bodies.add(new Earth());
//        bodies.add(new Mars());
//        return bodies;
        List<Body> bodies = builder.createBodies();
        //bodies.forEach(body -> System.out.println(body.getJPLPositions().toString()));
        return bodies;
    }

    @Override
    public String getName() {
        return builder.getName();
    }

//    @Override
//    public double G() {
//        return builder.getG();
//    }

    @Override
    public double dt() {
        return builder.getDt();
    }

    @Override
    public long runningTime() {
        return 260; // Run for 500 Simulated Days
    }

    @Override
    public int resolution() {
        return (int) ((1)/(100*dt()));
    }
}
