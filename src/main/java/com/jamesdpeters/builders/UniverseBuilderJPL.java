package com.jamesdpeters.builders;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.jamesdpeters.bodies.Body;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class UniverseBuilderJPL {

    private String name;
    private double dt, G;
    private List<JPLInfo> JPLInfo;

    private UniverseBuilderJPL(){
        JPLInfo = new ArrayList<>();
    }

    public static UniverseBuilderJPL getInstance(){
        return new UniverseBuilderJPL();
    }

    public UniverseBuilderJPL setName(String name) {
        this.name = name;
        return this;
    }

    public UniverseBuilderJPL setDt(double dt) {
        this.dt = dt;
        return this;
    }

    public UniverseBuilderJPL setG(double g) {
        G = g;
        return this;
    }

    public UniverseBuilderJPL addBodyInfo(JPLInfo jplInfo){
        JPLInfo.add(jplInfo);
        return this;
    }

    /**
     * GETTERS
     */

    public List<Body> createBodies() {
        List<Body> bodyList = new ArrayList<>();
        JPLInfo.forEach(jplInfo -> {
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
    public double getG() {
        return G;
    }
    public List<JPLInfo> getJPLInfo() { return JPLInfo; }

    /**
     *  SERIALIZER
     */

    public String serialise(Gson gson){
        return gson.toJson(this);
    }

    public UniverseBuilderJPL fromString(Gson gson, String json){
        return gson.fromJson(json, UniverseBuilderJPL.class);
    }

    public UniverseBuilderJPL fromFile(Gson gson, File file) throws FileNotFoundException {
        JsonReader reader = new JsonReader(new FileReader(file));
        return gson.fromJson(reader, UniverseBuilderJPL.class);
    }
}
