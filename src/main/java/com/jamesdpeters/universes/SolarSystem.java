package com.jamesdpeters.universes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jamesdpeters.StartUniverse;
import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.builders.UniverseBuilder;
import com.jamesdpeters.builders.jpl.UniverseBuilderJPL;
import com.jamesdpeters.eclipse.EclipseCalculator;
import com.jamesdpeters.integrators.IntegratorFactory;
import com.jamesdpeters.json.CSVWriter;
import com.jamesdpeters.json.Graph;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SolarSystem extends Universe {

    private UniverseBuilder builder;
    private double dt = 0;
    private List<Runnable> onFinish;

    public SolarSystem() {
        System.out.println("JSON File name: "+getJsonFilePath());
        builder = getBuilder();
        integrator = IntegratorFactory.getDefaultIntegrator();
        onFinish = new ArrayList<>();
        init();
    }

    protected UniverseBuilder getBuilder(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            String jsonFile = StartUniverse.class.getResource(getJsonFilePath()).getFile();
            return UniverseBuilderJPL.getInstance().fromFile(gson, new File(jsonFile));
        } catch (Exception e){e.printStackTrace();}
        return null;
    }

    protected String getJsonFilePath(){
        return "/Body.json";
    }

    @Override
    protected void loop() {
        integrator.step(this);
        bodies.forEach(Body::update); // Step each body doesn't update their actual positions.
        bodies.forEach(Body::postUpdate); // Updates all bodies to new position using the step.
    }

    @Override
    protected void onFinish() throws IOException {
//        universe.getBodies().forEach(BodyErrorWorker::calculateError);
        getBodies().forEach(Graph::plotBody);
        for (Body body : getBodies()) {
            CSVWriter.writeBody(body,5);
        }
        Graph.plotTrajectory(this,1);
        onFinish.forEach(Runnable::run);
        EclipseCalculator.findEclipses(this);
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
    public double runningTime() {
        return (long) (365*15); // Run for 500 Simulated Days
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
