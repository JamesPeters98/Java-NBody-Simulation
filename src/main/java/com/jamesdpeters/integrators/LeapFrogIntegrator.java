package com.jamesdpeters.integrators;

import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.helpers.CONSTANTS;
import com.jamesdpeters.universes.Universe;
import com.jamesdpeters.vectors.Vector3D;

public class LeapFrogIntegrator extends Integrator {


//    @Override
//    public void step(Body body) {
////        double dt = body.getUniverse().dt();
////        Vector3D x0 = body.getPosition();
////        Vector3D v0 = body.getVelocity();
////        Vector3D a0 = accel(body,body.getPosition());
////
////        Vector3D x1 = x0.add(v0.multiply(dt)).add(a0.multiply(0.5*dt*dt));
////        Vector3D a1 = accel(body,x1);
////        Vector3D v1 = v0.add(a0.add(a1).multiply(0.5*dt));
////
////        body.setNextPosition(x1);
////        body.setNextVelocity(v1);
//    }

    @Override
    public void step(Universe universe) {

    }

    @Override
    public String getIntegratorName() {
        return "LeapFrog";
    }

}
