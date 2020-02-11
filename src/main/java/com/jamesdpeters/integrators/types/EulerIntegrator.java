package com.jamesdpeters.integrators.types;

import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.integrators.abstracts.Integrator;
import com.jamesdpeters.universes.Universe;
import com.jamesdpeters.vectors.Vector3D;

public class EulerIntegrator extends Integrator {

    @Override
    public void step(Universe universe) {
        double dt = universe.dt();

        for(Body body : universe.getBodies()){
            body.setTempPos(1,body.getPosition());                 //x1
        }

        for(Body body : universe.getBodies()){
            Vector3D v = body.getVelocity().add(accel(body,1).multiply(dt));
            Vector3D x = body.getTempPos(1).add(v.multiply(dt));
            body.setNextVelocity(v);
            body.setNextPosition(x);
        }
    }

    @Override
    public String getIntegratorName() {
        return "Euler";
    }

}
