package com.jamesdpeters.universes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jamesdpeters.StartUniverse;
import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.builders.UniverseBuilderJPL;
import com.jamesdpeters.integrators.IntegratorFactory;

import java.io.File;
import java.util.List;

public class TrappistUniverse extends Universe {

    private UniverseBuilderJPL builder;
    private double dt = 0;

    public TrappistUniverse() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            String jsonFile = StartUniverse.class.getResource("/Trappist.json").getFile();
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
    protected void onFinish() {

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
        if(dt == 0){
            return builder.getDt();
        }
        return dt;
    }

    @Override
    public long runningTime() {
        return (long) (365*25); // Run for 500 Simulated Days
    }

    @Override
    public int resolution() {
        //dt needs to be a factor of (1/24)
        return Math.max((int) ((1/24)/dt()),1);
    }

    public void overrideTimeStep(double dt){
        System.out.println("Overriding time step! "+dt);
        this.dt = dt;
    }
}
