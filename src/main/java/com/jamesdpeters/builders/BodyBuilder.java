package com.jamesdpeters.builders;

import com.google.gson.Gson;
import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.vectors.Vector3D;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.TreeMap;

public class BodyBuilder {

    private String name;
    private Vector3D initPos, initVelocity;
    private double mass, radius, GM;
    private TreeMap<Double, Vector3D> JPLpositions, JPLvelocities;
    private boolean isOrigin = false;
    private LocalDateTime startDate;

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

    public BodyBuilder setPositions(TreeMap<Double, Vector3D> positions) {
        this.JPLpositions = positions;
        return this;
    }

    public BodyBuilder setVelocities(TreeMap<Double, Vector3D> velocities) {
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

    public BodyBuilder setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
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
            public TreeMap<Double, Vector3D> getJPLPositions() {
                return JPLpositions;
            }

            @Override
            public TreeMap<Double, Vector3D> getJPLVelocities() {
                return JPLvelocities;
            }

            @Override
            public boolean isOrigin() {
                return isOrigin;
            }

            @Override
            public LocalDateTime getStartDate() {
                return startDate;
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
