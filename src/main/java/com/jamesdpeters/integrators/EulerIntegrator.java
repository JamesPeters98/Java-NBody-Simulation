package com.jamesdpeters.integrators;

import com.jamesdpeters.bodies.Body;
import javafx.geometry.Point3D;

public class EulerIntegrator extends Integrator {

    public EulerIntegrator(Point3D initialVelocity, Point3D initialDisplacement, double mass, double dt) {
        super(initialVelocity, initialDisplacement, mass, dt);
    }

    @Override
    public void step(Body body) {
        // Euler step x(t+dt) = x(t) + v(t)*dt
        Point3D displacement = velocity.multiply(dt).multiply(0.001); //d = vt (Converts from Meters to KM!)
        position = position.add(displacement);

        // Euler step velocity for next step v(t+dt) = v(t) + a(t)
        velocity = velocity.add(body.acceleration(dt,Point3D.ZERO).multiply(dt)); // ACCEL PROB NOT WORKING
    }
}
