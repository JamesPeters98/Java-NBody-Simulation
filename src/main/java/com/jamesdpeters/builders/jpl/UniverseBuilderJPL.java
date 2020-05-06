package com.jamesdpeters.builders.jpl;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.builders.UniverseBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class UniverseBuilderJPL extends UniverseBuilder {

    private List<JPLInfo> JPLInfo;

    private UniverseBuilderJPL(){
        JPLInfo = new ArrayList<>();
    }
    public static UniverseBuilderJPL getInstance(){
        return new UniverseBuilderJPL();
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

    /**
     *  SERIALIZER
     */
    public UniverseBuilderJPL fromString(Gson gson, String json){
        return gson.fromJson(json, UniverseBuilderJPL.class);
    }

    public UniverseBuilderJPL fromFile(Gson gson, File file) {
        try {
            JsonReader reader = new JsonReader(new FileReader(file));
            return gson.fromJson(reader, UniverseBuilderJPL.class);
        } catch (FileNotFoundException e){
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}
