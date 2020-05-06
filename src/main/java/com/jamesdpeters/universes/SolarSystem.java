package com.jamesdpeters.universes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jamesdpeters.StartUniverse;
import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.builders.UniverseBuilder;
import com.jamesdpeters.builders.jpl.UniverseBuilderJPL;
import com.jamesdpeters.integrators.IntegratorFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SolarSystem extends Universe {

    private UniverseBuilder builder;
    private double dt = 0;
    private List<Runnable> onFinish;
    private double runningTime = (365*15);
    private int resolution = 1;

    public SolarSystem() {
        this(null);
    }

    public SolarSystem(String jsonFileName){
        String file = (jsonFileName != null) ? jsonFileName : getJsonFilePath();
        System.out.println("JSON File name: "+file);
        builder = getBuilder(file);
        integrator = IntegratorFactory.getDefaultIntegrator();
        onFinish = new ArrayList<>();
        init();
    }

    protected UniverseBuilder getBuilder(String jsonFilePath){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonFile = StartUniverse.class.getResource(jsonFilePath).getFile();
        return UniverseBuilderJPL.getInstance().fromFile(gson, new File(jsonFile));
    }

    protected String getJsonFilePath(){
        return "/Body.json";
    }

    @Override
    protected void loop() {
        integrator.step(this);
        bodies.forEach(Body::update);
    }

    @Override
    protected void onFinish() throws IOException {
//        universe.getBodies().forEach(BodyErrorWorker::calculateError);
//        getBodies().forEach(Graph::plotBody);
//        for (Body body : getBodies()) {
//            CSVWriter.writeBody(body,5);
//        }
//        Graph.plotTrajectory(this,1);
        onFinish.forEach(Runnable::run);
        //EclipseCalculator.findEclipses(this);
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
        return runningTime; // Run for 500 Simulated Days
    }

    @Override
    public int resolution() {
        //dt needs to be a factor of (1/24)
        //return Math.max((int) ((1/24)/dt()),1);
        return resolution;
    }

    public void overrideTimeStep(double dt){
        System.out.println("Overriding time step! "+dt);
        this.dt = dt;
    }

    public void addOnFinishListener(Runnable runnable){
        onFinish.add(runnable);
    }

    public void setRunningTime(double runningTime){
        this.runningTime = runningTime;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }
}
