package com.jamesdpeters.integrators;

import com.jamesdpeters.bodies.Body;
import javafx.geometry.Point3D;

import java.util.List;

public abstract class Integrator {

    Point3D velocity;
    Point3D position;
    double mass;
    double dt;
    double time; // Keep track of time.

    public Integrator(Point3D initialVelocity, Point3D initialPosition, double mass, double dt){
        this.velocity = initialVelocity;
        this.position = initialPosition;
        this.mass = mass;
        this.dt = dt;
    }

    public abstract void step(Body body);

    public Point3D getVelocity() {
        return velocity;
    }

    public Point3D getPosition() {
        return position;
    }
}
