package com.jamesdpeters.interfaces;

import javafx.geometry.Point3D;

public abstract class Integrator {

    Point3D velocity;
    Point3D position;
    double mass;
    double dt;

    public Integrator(Point3D initialVelocity, Point3D initialPosition, double mass, double dt){
        this.velocity = initialVelocity;
        this.position = initialPosition;
        this.mass = mass;
        this.dt = dt;
    }

    Point3D f(double dt, Point3D force){
        Point3D acceleration = force.multiply(1/mass); //F = ma
        return velocity.add(acceleration.multiply(dt)); //V = V0 + at
    }

    abstract void step(Point3D force);

}
