package com.jamesdpeters.builders.trappist;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.builders.BodyBuilder;
import com.jamesdpeters.builders.UniverseBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UniverseBuilderTrappist extends UniverseBuilder {

    private List<TrappistBody> trappistBodies;
    private LocalDateTime startDate;

    private UniverseBuilderTrappist(){
        super();
        trappistBodies = new ArrayList<>();
    }

    public static UniverseBuilderTrappist getInstance(){
        return new UniverseBuilderTrappist();
    }


    public UniverseBuilderTrappist addBodyInfo(TrappistBody trappistBody){
        this.trappistBodies.add(trappistBody);
        return this;
    }

    public List<TrappistBody> getTrappistBodies() {
        return trappistBodies;
    }

    /**
     * GETTERS
     */

    public List<Body> createBodies() {
        System.out.println("Start Date: "+startDate);
        List<Body> bodyList = new ArrayList<>();
        trappistBodies.forEach(trappistBody -> {
            BodyBuilder builder = trappistBody.getBodyBuilder().setStartDate(startDate);
            bodyList.add(builder.create());
        });
        return bodyList;
    }

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


    public UniverseBuilderTrappist setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
        return this;
    }
}
