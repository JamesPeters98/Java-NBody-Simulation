package com.jamesdpeters.builders;

import com.google.gson.Gson;
import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.vectors.Vector3D;

import java.util.HashMap;

public class BodyBuilder {

    private String name;
    private Vector3D initPos, initVelocity;
    private double mass, radius, GM;
    private HashMap<Long, Vector3D> JPLpositions, JPLvelocities;
    private boolean isOrigin = false;

    public static BodyBuilder getInstance(){
        return new BodyBuilder();
    }

    private BodyBuilder(){}

    public BodyBuilder setName(String name){
        this.name = name;
        return this;
    }

    public BodyBuilder setInitPos(Vector3D initPos) {
        this.initPos = initPos;
        return this;
    }

    public BodyBuilder setInitVelocity(Vector3D initVelocity) {
        this.initVelocity = initVelocity;
        return this;
    }

    public BodyBuilder setMass(double mass) {
        this.mass = mass;
        return this;
    }

    public BodyBuilder setRadius(double radius) {
        this.radius = radius;
        return this;
    }

    public BodyBuilder setPositions(HashMap<Long, Vector3D> positions) {
        this.JPLpositions = positions;
        return this;
    }

    public BodyBuilder setVelocities(HashMap<Long, Vector3D> velocities) {
        this.JPLvelocities = velocities;
        return this;
    }

    public BodyBuilder setOrigin(boolean origin) {
        isOrigin = origin;
        return this;
    }

    public BodyBuilder setGM(double GM) {
        this.GM = GM;
        return this;
    }

    public Body create(){
        return new Body() {
            @Override
            public Vector3D getInitialPosition() {
                return initPos;
            }

            @Override
            public Vector3D getInitialVelocity() {
                return initVelocity;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public double getMass() {
                return mass;
            }

            @Override
            public double getGM() {
                return GM;
            }

            @Override
            public double getBodyRadius() {
                return radius;
            }

            @Override
            public HashMap<Long, Vector3D> getJPLPositions() {
                return JPLpositions;
            }

            @Override
            public HashMap<Long, Vector3D> getJPLVelocities() {
                return JPLvelocities;
            }

            @Override
            public boolean isOrigin() {
                return isOrigin;
            }
        };
    }

    public String serialise(Gson gson){
        return gson.toJson(this);
    }

    public BodyBuilder fromString(Gson gson, String json){
        return gson.fromJson(json, BodyBuilder.class);
    }
}
