package com.jamesdpeters.builders;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.jamesdpeters.bodies.Body;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class UniverseBuilder {

    private String name;
    private double dt, G;
    private List<BodyBuilder> bodies;

    private UniverseBuilder(){
        bodies = new ArrayList<>();
    }

    public static UniverseBuilder getInstance(){
        return new UniverseBuilder();
    }

    public UniverseBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public UniverseBuilder setDt(double dt) {
        this.dt = dt;
        return this;
    }

    public UniverseBuilder setG(double g) {
        G = g;
        return this;
    }

    public UniverseBuilder addBody(BodyBuilder bodyBuilder){
        bodies.add(bodyBuilder);
        return this;
    }

    /**
     * GETTERS
     */

    public List<Body> createBodies() {
        List<Body> bodyList = new ArrayList<>();
        bodies.forEach(bodyBuilder -> bodyList.add(bodyBuilder.create()));
        return bodyList;
    }

    public String getName() {
        return name;
    }
    public double getDt() {
        return dt;
    }
    public double getG() {
        return G;
    }

    /**
     *  SERIALIZER
     */

    public String serialise(Gson gson){
        return gson.toJson(this);
    }

    public UniverseBuilder fromString(Gson gson, String json){
        return gson.fromJson(json, UniverseBuilder.class);
    }

    public UniverseBuilder fromFile(Gson gson, File file) throws FileNotFoundException {
        JsonReader reader = new JsonReader(new FileReader(file));
        return gson.fromJson(reader, UniverseBuilder.class);
    }
}
