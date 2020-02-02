package com.jamesdpeters.universes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jamesdpeters.StartUniverse;
import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.builders.UniverseBuilderJPL;
import com.jamesdpeters.integrators.IntegratorFactory;

import java.io.File;
import java.util.List;

public class NormalUniverse extends Universe {

    private UniverseBuilderJPL builder;

    public NormalUniverse() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            String jsonFile = StartUniverse.class.getResource("/Body.json").getFile();
            builder = UniverseBuilderJPL.getInstance().fromFile(gson, new File(jsonFile));
        } catch (Exception e){e.printStackTrace();}
        integrator = IntegratorFactory.getDefaultIntegrator();
        init();
    }

    @Override
    protected void loop() {
        integrator.step(this);
        bodies.forEach(Body::update); // Step each body doesn't update their actual positions.
        bodies.forEach(Body::postUpdate); // Updates all bodies to new position using the step.
    }

    @Override
    public List<Body> createBodies() {
        return builder.createBodies();
    }

    @Override
    public String getName() {
        return builder.getName();
    }

    @Override
    public double dt() {
        return builder.getDt();
    }

    @Override
    public long runningTime() {
        return (long) (365*6); // Run for 500 Simulated Days
    }

    @Override
    public int resolution() {
        return (int) ((1)/(100*dt()));
    }
}
