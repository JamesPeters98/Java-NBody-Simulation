package com.jamesdpeters.builders;

import com.google.gson.Gson;
import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.json.JPLHorizonsParser;

public class JPLInfo {

    private String name;
    private String filename;
    private double mass, radius, GM;
    private boolean originBody = false;

    public String serialise(Gson gson){
        return gson.toJson(this);
    }

    public JPLInfo fromString(Gson gson, String json){
        return gson.fromJson(json, JPLInfo.class);
    }

    public Body getBody() {
        return getBodyBuilder().create();
    }

    public BodyBuilder getBodyBuilder(){
        return JPLHorizonsParser.parse(filename,name)
                .setMass(mass)
                .setRadius(radius)
                .setGM(GM)
                .setOrigin(originBody);
    }

    public String getName() {
        return name;
    }

    public JPLInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getFilename() {
        return filename;
    }

    public JPLInfo setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public double getMass() {
        return mass;
    }

    public JPLInfo setMass(double mass) {
        this.mass = mass;
        return this;
    }

    public double getRadius() {
        return radius;
    }

    public JPLInfo setRadius(double radius) {
        this.radius = radius;
        return this;
    }

    public JPLInfo setGM(double GM) {
        this.GM = GM;
        return this;
    }
}
