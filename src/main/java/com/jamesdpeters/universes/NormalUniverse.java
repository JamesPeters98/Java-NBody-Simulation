package com.jamesdpeters.universes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jamesdpeters.Main;
import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.builders.UniverseBuilder;
import com.jamesdpeters.builders.UniverseBuilderJPL;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class NormalUniverse extends Universe {

    Collection<Body> callables;
    List<Future> futures;
    UniverseBuilderJPL builder;

    public NormalUniverse(Stage stage) {
        super(stage);
        futures = new ArrayList<>();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            String jsonFile = Main.class.getResource("/Body.json").getFile();
            builder = UniverseBuilderJPL.getInstance().fromFile(gson, new File(jsonFile));
        } catch (Exception e){e.printStackTrace();}

        init();
    }

    @Override
    protected void loop() {
        bodies.forEach(Body::update); // Step each body doesn't update their actual positions.
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
        return TimeUnit.DAYS.toSeconds(365*5); // Run for 500 Simulated Days
    }
}
