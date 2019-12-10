package com.jamesdpeters.integrators.physics;

import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.integrators.abstracts.AbstractIntegrator;
import com.jamesdpeters.integrators.abstracts.IntegratorType;
import javafx.geometry.Point3D;

public class PositionIntegrator extends AbstractIntegrator {

    VelocityIntegrator velocityIntegrator;

    public PositionIntegrator(IntegratorType integratorType) {
        super(integratorType);
        velocityIntegrator = new VelocityIntegrator(integratorType);
    }

    @Override
    public Point3D f(Body body, double dt, Point3D dx) {
        return body.getPosition().multiply(1000);
    }

    @Override
    public Point3D df(Body body, double dt, Point3D dx) {
        return body.getVelocity().add(velocityIntegrator.df(body, dt, dx).multiply(dt));
    }
}
