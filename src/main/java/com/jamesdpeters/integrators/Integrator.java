package com.jamesdpeters.integrators;

import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.integrators.abstracts.IntegratorType;
import com.jamesdpeters.integrators.physics.PositionIntegrator;
import com.jamesdpeters.integrators.physics.VelocityIntegrator;
import javafx.geometry.Point3D;

public class Integrator {

    private PositionIntegrator positionIntegrator;
    private VelocityIntegrator velocityIntegrator;
    private IntegratorType integratorType;

    public Integrator(IntegratorType integratorType){
        this.integratorType = integratorType;
        positionIntegrator = new PositionIntegrator(integratorType);
        velocityIntegrator = new VelocityIntegrator(integratorType);
    }

    public void step(Body body){
        body.setPosition(positionIntegrator.step(body,body.getUniverse().dt()).multiply(0.001));
        body.setVelocity(velocityIntegrator.step(body,body.getUniverse().dt()));
    }

    public IntegratorType getIntegratorType() {
        return integratorType;
    }
}
