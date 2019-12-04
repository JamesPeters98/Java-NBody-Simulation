package com.jamesdpeters.integrators;

import com.jamesdpeters.bodies.Body;
import javafx.geometry.Point3D;

public class RK4Integrator extends Integrator {


    public RK4Integrator(Point3D initialVelocity, Point3D initialDisplacement, double mass, double dt) {
        super(initialVelocity, initialDisplacement, mass, dt);
    }

    @Override
    public void step(Body body) {
//        Point3D dx1 = velocity(0,force).multiply(dt);
//        Point3D dx2 = velocity(0.5*dt,force).multiply(dt);
//        Point3D dx3 = velocity(0.5*dt,force).multiply(dt);
//        Point3D dx4 = velocity(dt,force).multiply(dt);
//
//        velocity = dx1;
//
//        Point3D sumDx = dx1.add(dx2.multiply(2)).add(dx3.multiply(2)).add(dx4);
//        Point3D displacement = sumDx.multiply(1/6).multiply(0.001); //d = vt (Converts from Meters to KM!)
//
//        position = position.add(displacement);
    }
}
