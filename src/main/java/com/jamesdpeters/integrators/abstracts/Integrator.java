package com.jamesdpeters.integrators.abstracts;

import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.universes.Universe;
import com.jamesdpeters.vectors.Vector3D;

public abstract class Integrator {

    //Integrator can decide to evolve each
    public abstract void step(Universe universe);
    public abstract String getIntegratorName();

    //Calculates Acceleration of given body using the temp pos parameter, e.g index 1 = x1
    public Vector3D accel(Body body, int positionIndex){
        Vector3D accel = Vector3D.ZERO;
        for(Body body2 : body.getExclusiveBodies()) {
            Vector3D delta = body.getTempPos(positionIndex).subtract(body2.getTempPos(positionIndex));
            double distance = delta.magnitude();
            // a(t)
            double accelMagnitude = -(body2.getGMAU()) / (distance * distance * distance);
            accel = accel.add(delta.multiply(accelMagnitude));
        }
        return accel;
    }

//    public Vector3D accelDerivative(Body body){
//        Vector3D accelDot = new Vector3D(0,0,0);
//        for(Body body2 : body.getExclusiveBodies()) {
//            Vector3D delta = body.getTempPos().subtract(body2.getTempPos());
//            double distance = delta.magnitude();
//            // a(t)
//            double accelMagnitudeDot = (-body2.getGMAU())/(distance*distance*distance);
//            accelDot = accelDot.add(delta.multiply(accelMagnitudeDot));
//        }
//        return accelDot;
//    }
}
