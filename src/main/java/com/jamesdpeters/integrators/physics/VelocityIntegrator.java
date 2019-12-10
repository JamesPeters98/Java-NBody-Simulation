package com.jamesdpeters.integrators.physics;

import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.integrators.abstracts.AbstractIntegrator;
import com.jamesdpeters.integrators.abstracts.IntegratorType;
import javafx.geometry.Point3D;

public class VelocityIntegrator extends AbstractIntegrator {

    AccelerationIntegrator accelerationIntegrator;

    public VelocityIntegrator(IntegratorType integratorType) {
        super(integratorType);
        accelerationIntegrator = new AccelerationIntegrator(integratorType);
    }

    @Override
    public Point3D f(Body body, double dt, Point3D dx) {
        return body.getVelocity();
    }

    @Override
    public Point3D df(Body body, double dt, Point3D dx) {
        return accelerationIntegrator.f(body,dt,dx);
    }
}
