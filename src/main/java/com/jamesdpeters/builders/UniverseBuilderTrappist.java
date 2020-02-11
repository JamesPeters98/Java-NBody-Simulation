package com.jamesdpeters.builders;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.jamesdpeters.bodies.Body;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class UniverseBuilderTrappist {

    private String name;
    private double dt;
    private List<TrappistInfo> trappistInfo;

    private UniverseBuilderTrappist(){
        trappistInfo = new ArrayList<>();
    }

    public static UniverseBuilderTrappist getInstance(){
        return new UniverseBuilderTrappist();
    }

    public UniverseBuilderTrappist setName(String name) {
        this.name = name;
        return this;
    }

    public UniverseBuilderTrappist setDt(double dt) {
        this.dt = dt;
        return this;
    }

    public UniverseBuilderTrappist addBodyInfo(TrappistInfo trappistInfo){
        this.trappistInfo.add(trappistInfo);
        return this;
    }

    /**
     * GETTERS
     */

    public List<Body> createBodies() {
        List<Body> bodyList = new ArrayList<>();
        trappistInfo.forEach(jplInfo -> {
            bodyList.add(jplInfo.getBody());
        });
        return bodyList;
    }

    public String getName() {
        return name;
    }
    public double getDt() {
        return dt;
    }
    public List<TrappistInfo> getTrappistInfo() { return trappistInfo; }

    /**
     *  SERIALIZER
     */

    public String serialise(Gson gson){
        return gson.toJson(this);
    }

    public UniverseBuilderTrappist fromString(Gson gson, String json){
        return gson.fromJson(json, UniverseBuilderTrappist.class);
    }

    public UniverseBuilderTrappist fromFile(Gson gson, File file) throws FileNotFoundException {
        JsonReader reader = new JsonReader(new FileReader(file));
        return gson.fromJson(reader, UniverseBuilderTrappist.class);
    }
}
