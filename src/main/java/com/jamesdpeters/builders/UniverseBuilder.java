package com.jamesdpeters.builders;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.jamesdpeters.bodies.Body;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public abstract class UniverseBuilder {

    protected String name;
    protected double dt;

    /**
     * SETTERS & GETTERS
     */
    public void setName(String name) {
        this.name = name;
    }
    public void setDt(double dt) {
        this.dt = dt;
    }

    public abstract List<Body> createBodies();
    public String getName() {
        return name;
    }
    public double getDt() {
        return dt;
    }

    /**
     *  SERIALIZER
     */
    public String serialise(Gson gson){
        return gson.toJson(this);
    }

}
