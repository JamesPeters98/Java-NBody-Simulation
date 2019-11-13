package com.jamesdpeters.interfaces;

import javafx.geometry.Point3D;

public class EulerIntegrator extends Integrator {


    public EulerIntegrator(Point3D initialVelocity, Point3D initialDisplacement, double mass, double dt) {
        super(initialVelocity, initialDisplacement, mass, dt);
    }

    @Override
    void step(Point3D force) {
        Point3D displacement = f(dt,force).multiply(dt).multiply(0.001); //d = vt (Converts from Meters to KM!)
    }
}
