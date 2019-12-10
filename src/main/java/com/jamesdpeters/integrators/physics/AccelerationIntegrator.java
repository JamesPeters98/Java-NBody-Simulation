package com.jamesdpeters.integrators.physics;

import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.integrators.abstracts.AbstractIntegrator;
import com.jamesdpeters.integrators.abstracts.IntegratorType;
import javafx.geometry.Point3D;

public class AccelerationIntegrator extends AbstractIntegrator {

    public AccelerationIntegrator(IntegratorType integratorType) {
        super(integratorType);
    }

    @Override
    public Point3D f(Body body, double dt, Point3D dx) {
            Point3D accel = new Point3D(0,0,0);
            for(Body body2 : body.getBodies()) {
                if (body2 != body) {
                    Point3D delta = body2.getPosition().subtract(body.getPosition()).add(dx);
                    double distance = delta.magnitude() * 1000;
                    // a(t)
                    double forceMagnitude = (body2.getUniverse().G() * body.getMass() * body2.getMass()) / (distance * distance);
                    accel = accel.add(delta.normalize().multiply(forceMagnitude).multiply(1 / body.getMass()));
                }
            }
            return accel;
    }

    @Override
    public Point3D df(Body body, double dt, Point3D dx) {
        Point3D accelDot = new Point3D(0,0,0);
        for(Body body2 : body.getBodies()) {
            if (body2 != body) {
                Point3D delta = body2.getPosition().subtract(body.getPosition()).add(dx);
                double distance = delta.magnitude() * 1000;
                // a(t)
                double forceMagnitudeDot = (-2*body.getUniverse().G() * body2.getMass() * body.getMass())/(distance*distance*distance);
                accelDot = accelDot.add(delta.normalize().multiply(forceMagnitudeDot).multiply(1 / body.getMass()));
            }
        }
        return accelDot;
    }

}
