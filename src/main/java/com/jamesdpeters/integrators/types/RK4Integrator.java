package com.jamesdpeters.integrators.types;

import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.integrators.abstracts.Integrator;
import com.jamesdpeters.universes.Universe;
import com.jamesdpeters.vectors.Vector3D;

public class RK4Integrator extends Integrator {

    @Override
    public void step(Universe universe) {
        double dt = universe.dt();

        for(Body body : universe.getBodies()){
            body.setTempPos(1,body.getPosition());          //x1
        }

        for(Body body : universe.getBodies()){
            Vector3D a1 = accel(body,1);
            Vector3D v1 = body.getVelocity();

            Vector3D x2 = body.getPosition().add(v1.multiply(dt/2.0));
            Vector3D v2 = body.getVelocity().add(a1.multiply(dt/2.0));

            body.setTempPos(2,x2);
            body.setTempVelocity(2,v2);
            body.setTempAccel(1,a1);
        }

        for(Body body : universe.getBodies()){
            Vector3D a2 = accel(body,2);
            Vector3D v2 = body.getTempVelocity(2);

            Vector3D x3 = body.getPosition().add(v2.multiply(dt/2.0));
            Vector3D v3 = body.getVelocity().add(a2.multiply(dt/2.0));

            body.setTempPos(3,x3);
            body.setTempVelocity(3,v3);
            body.setTempAccel(2,a2);
        }

        for(Body body : universe.getBodies()){
            Vector3D a3 = accel(body,3);
            Vector3D v3 = body.getTempVelocity(3);

            Vector3D x4 = body.getPosition().add(v3.multiply(dt));
            Vector3D v4 = body.getVelocity().add(a3.multiply(dt));

            body.setTempPos(4,x4);
            body.setTempVelocity(4,v4);
            body.setTempAccel(3,a3);
        }

        for(Body body : universe.getBodies()){
            Vector3D a4 = accel(body,4);
            Vector3D dx = sumRK4(body.getVelocity(),body.getTempVelocity(2),body.getTempVelocity(3),body.getTempVelocity(4)).multiply(dt);
            Vector3D dv = sumRK4(body.getTempAccel(1),body.getTempAccel(2),body.getTempAccel(3),a4).multiply(dt);
            body.setNextPosition(body.getPosition().add(dx));
            body.setNextVelocity(body.getVelocity().add(dv));
        }
    }

    @Override
    public String getIntegratorName() {
        return "RK4";
    }

    private Vector3D sumRK4(Vector3D k1, Vector3D k2, Vector3D k3, Vector3D k4){
        Vector3D sumdx = Vector3D.ZERO;
        sumdx = sumdx.add(k1);
        sumdx = sumdx.add(k2.multiply(2.0));
        sumdx = sumdx.add(k3.multiply(2.0));
        sumdx = sumdx.add(k4);
        return sumdx.multiply((double) 1 / (double) 6);
    }
}
