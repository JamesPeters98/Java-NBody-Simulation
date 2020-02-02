package com.jamesdpeters.integrators;

import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.universes.Universe;
import com.jamesdpeters.vectors.Vector3D;

public abstract class Integrator {

    //Integrator can decide to evolve each
    public abstract void step(Universe universe);
    public abstract String getIntegratorName();

    //Calculates Acceleration of given body using the temp pos parameter.
    public Vector3D accel(Body body){
        Vector3D accel = Vector3D.ZERO;
        for(Body body2 : body.getExclusiveBodies()) {
            Vector3D delta = body2.getTempPos().subtract(body.getTempPos());
            double distance = delta.magnitude();
            // a(t)
            double accelMagnitude = (body2.getGMAU()) / (distance * distance);
            accel = accel.add(delta.normalize().multiply(accelMagnitude));
        }
        return accel;
    }

    public Vector3D accelDerivative(Body body){
        Vector3D accelDot = new Vector3D(0,0,0);
        for(Body body2 : body.getExclusiveBodies()) {
            Vector3D delta = body2.getTempPos().subtract(body.getTempPos());
            double distance = delta.magnitude();
            // a(t)
            double accelMagnitudeDot = (-body2.getGMAU())/(distance*distance*distance);
            accelDot = accelDot.add(delta.normalize().multiply(accelMagnitudeDot));
        }
        return accelDot;
    }
}
