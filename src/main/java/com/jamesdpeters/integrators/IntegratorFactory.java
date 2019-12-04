package com.jamesdpeters.integrators;

import javafx.geometry.Point3D;

public class IntegratorFactory {

    public static Integrator getIntegrator(Point3D initialVelocity, Point3D initialDisplacement, double mass, double dt){
//        return new RK4Integrator(initialVelocity, initialDisplacement, mass, dt);
        return new EulerIntegrator(initialVelocity, initialDisplacement, mass, dt);
    }

}
