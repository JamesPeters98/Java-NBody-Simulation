package com.jamesdpeters.universes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jamesdpeters.Main;
import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.bodies.Earth;
import com.jamesdpeters.bodies.Mars;
import com.jamesdpeters.bodies.Sun;
import com.jamesdpeters.builders.UniverseBuilder;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NormalUniverse extends Universe {

    Collection<Body> callables;
    List<Future> futures;
    UniverseBuilder builder;

    public NormalUniverse(Stage stage) {
        super(stage);
        futures = new ArrayList<>();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            String jsonFile = Main.class.getResource("/Body.json").getFile();
            builder = UniverseBuilder.getInstance().fromFile(gson, new File(jsonFile));
        } catch (Exception e){e.printStackTrace();}

        init();
    }

    @Override
    protected void loop() {
        bodies.forEach(Body::update);
//        try {
//            if (service != null) service.invokeAll(bodies);
//        } catch (Exception e){ e.printStackTrace();}
//        System.out.println("Adding to service!");
//        for(Body body : bodies){
//            System.out.println(body.getName());
//        }
//        try {
//            for (Body body : bodies) {
//                if(body != null) futures
//                        .add(
//                                service.
//                                        submit(
//                                                body));
//            }
//        } catch (Exception e){ e.printStackTrace();}
//        System.out.println("Added Sims!");
//        boolean finished = false;
//        while(!finished){
//            for(Future future : futures){
//                if(!future.isDone()) continue;
//            }
//            finished = true;
//        }
    }

    @Override
    public List<Body> createBodies() {
//        List<Body> bodies = new ArrayList<>();
//        bodies.add(new Sun());
//        bodies.add(new Earth());
//        bodies.add(new Mars());
//        return bodies;
        return builder.createBodies();
    }

    @Override
    public String getName() {
        return builder.getName();
    }

    @Override
    public double G() {
        return builder.getG();
    }

    @Override
    public double dt() {
        return builder.getDt();
    }
}
