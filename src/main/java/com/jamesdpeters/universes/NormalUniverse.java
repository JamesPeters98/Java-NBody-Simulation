package com.jamesdpeters.universes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jamesdpeters.StartUniverse;
import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.bodies.BodyErrorWorker;
import com.jamesdpeters.builders.UniverseBuilderJPL;
import com.jamesdpeters.integrators.IntegratorFactory;
import com.jamesdpeters.json.Graph;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NormalUniverse extends Universe {

    private UniverseBuilderJPL builder;
    private double dt = 0;
    private List<Runnable> onFinish;

    public NormalUniverse() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            String jsonFile = StartUniverse.class.getResource("/Body.json").getFile();
            builder = UniverseBuilderJPL.getInstance().fromFile(gson, new File(jsonFile));
        } catch (Exception e){e.printStackTrace();}
        integrator = IntegratorFactory.getDefaultIntegrator();
        onFinish = new ArrayList<>();
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
//        universe.getBodies().forEach(BodyErrorWorker::calculateError);
//        universe.getBodies().forEach(Graph::plotBody);
        onFinish.forEach(Runnable::run);
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
        return (long) (365*30); // Run for 500 Simulated Days
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

    public void addOnFinishListener(Runnable runnable){
        onFinish.add(runnable);
    }
}
