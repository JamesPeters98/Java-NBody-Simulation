package com.jamesdpeters.universes;

import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.bodies.Earth;
import com.jamesdpeters.bodies.Moon;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NormalUniverse extends Universe {

    ExecutorService service;
    Collection<Body> callables;
    List<Future> futures;

    public NormalUniverse(double dt, Stage stage) {
        super(dt, stage);
        service = Executors.newSingleThreadExecutor();
        callables = new ArrayList<>();
    }

    @Override
    void loop() {
        bodies.forEach(Body::update);
    }

    @Override
    List<Body> createBodies() {
        List<Body> bodies = new ArrayList<>();
        bodies.add(new Earth(0,0,0));
        bodies.add(new Moon(384400,0,0));
        return bodies;
    }

    @Override
    String getName() {
        return "Standard Universe";
    }

    @Override
    public double G() {
        return 6.67e-11;
    }
}
