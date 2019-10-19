package com.jamesdpeters.builders;

import com.google.gson.Gson;
import com.jamesdpeters.bodies.Body;
import javafx.geometry.Point3D;

public class BodyBuilder {

    private String name;
    private Point3D initPos, initVelocity;
    private double mass, radius;

    public static BodyBuilder getInstance(){
        return new BodyBuilder();
    }

    private BodyBuilder(){}

    public BodyBuilder setName(String name){
        this.name = name;
        return this;
    }

    public BodyBuilder setInitPos(Point3D initPos) {
        this.initPos = initPos;
        return this;
    }

    public BodyBuilder setInitVelocity(Point3D initVelocity) {
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

    public Body create(){
        return new Body() {
            @Override
            public Point3D getInitialPosition() {
                return initPos;
            }

            @Override
            public Point3D getInitialVelocity() {
                return initVelocity.multiply(1000);
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
            public double getBodyRadius() {
                return radius;
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
