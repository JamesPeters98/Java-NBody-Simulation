package com.jamesdpeters.integrators.types;

import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.integrators.abstracts.Integrator;
import com.jamesdpeters.universes.Universe;
import com.jamesdpeters.vectors.Vector3D;

public class LeapFrogIntegrator extends Integrator {

    @Override
    public void step(Universe universe) {
        double dt = universe.dt();

        for(Body body : universe.getBodies()) {
            Vector3D x0 = body.getPosition();
            body.setTempPos(0,x0);
        }

        for(Body body : universe.getBodies()) {
            Vector3D x0 = body.getPosition();
            Vector3D v0 = body.getVelocity();
            Vector3D a0 = accel(body,0);
            Vector3D x1 = x0.add(v0.multiply(dt)).add(a0.multiply(0.5 * dt * dt));
            body.setTempPos(1,x1);
            body.setNextPosition(x1);
            body.setTempAccel(0,a0);
        }

        for(Body body : universe.getBodies()){
            Vector3D a1 = accel(body,1);
            Vector3D a0 = body.getTempAccel(0);
            Vector3D v0 = body.getVelocity();
            Vector3D v1 = v0.add(a0.add(a1).multiply(0.5 * dt));
            body.setNextVelocity(v1);
        }
    }

    @Override
    public String getIntegratorName() {
        return "LeapFrog";
    }

}
