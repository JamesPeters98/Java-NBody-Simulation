package com.jamesdpeters.builders;

import com.google.gson.Gson;
import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.helpers.Constants;
import com.jamesdpeters.json.JPLHorizonsParser;

public class TrappistInfo {

    private String name;
    private double earthMassRatio, radius, inclination, orbitalPeriod;
    private boolean originBody = false;

    public String serialise(Gson gson){
        return gson.toJson(this);
    }

    public TrappistInfo fromString(Gson gson, String json){
        return gson.fromJson(json, TrappistInfo.class);
    }

    public Body getBody() {
        return getBodyBuilder().create();
    }

    public BodyBuilder getBodyBuilder(){
        return BodyBuilder.getInstance()
                .setMass(Constants.EARTH.getEarthMass(earthMassRatio))
                .setRadius(radius)
                .setGM(Constants.EARTH.getEarthGM(earthMassRatio))
                .setOrigin(originBody);
    }

    public String getName() {
        return name;
    }

    public TrappistInfo setName(String name) {
        this.name = name;
        return this;
    }

    public double getRadius() {
        return radius;
    }

    public TrappistInfo setRadius(double radius) {
        this.radius = radius;
        return this;
    }

}
