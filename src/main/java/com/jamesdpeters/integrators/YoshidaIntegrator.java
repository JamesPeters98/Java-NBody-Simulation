package com.jamesdpeters.integrators;

import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.integrators.abstracts.Integrator;
import com.jamesdpeters.universes.Universe;
import com.jamesdpeters.vectors.Vector3D;

public class YoshidaIntegrator extends Integrator {

    public YoshidaIntegrator() {
        System.out.println("c1:"+c1);
        System.out.println("c2:"+c2);
    }

    private static final double w0 = -Math.cbrt(2)/(2-Math.cbrt(2));
    private static final double w1 = 1/(2-Math.cbrt(2));
    private static final double c1 = w1/2;
    private static final double c2 = (w0+w1)/2;
    private static final double d1 = w1;
    private static final double d2 = w0;

    @Override
    public void step(Universe universe) {
        double dt = universe.dt();

        for(Body body : universe.getBodies()){
            Vector3D x0 = body.getPosition();
            Vector3D v0 = body.getVelocity();
            Vector3D x1 = x0.add(v0.multiply(c1*dt));
            body.setTempPos(x1);
            body.setTempVelocity(v0);
        }

        for(Body body : universe.getBodies()){
            Vector3D a1 = accel(body);
            Vector3D v1 = body.getTempVelocity().add(a1.multiply(d1*dt));
            Vector3D x2 = body.getTempPos().add(v1.multiply(c2*dt));
            body.setTempPos(x2);
            body.setTempVelocity(v1);
        }

        for(Body body : universe.getBodies()){
            Vector3D a2 = accel(body);
            Vector3D v2 = body.getTempVelocity().add(a2.multiply(d2*dt));
            Vector3D x3 = body.getTempPos().add(v2.multiply(c2*dt));
            body.setTempPos(x3);
            body.setTempVelocity(v2);
        }

        for(Body body : universe.getBodies()){
            Vector3D a3 = accel(body);
            Vector3D v3 = body.getTempVelocity().add(a3.multiply(d1*dt));
            Vector3D x4 = body.getTempPos().add(v3.multiply(c1*dt));
            body.setNextPosition(x4);
            body.setNextVelocity(v3);
        }
    }

    @Override
    public String getIntegratorName() {
        return "Yoshida";
    }


}
