//package com.jamesdpeters.integrators.physics;
//
//import com.jamesdpeters.bodies.Body;
//import com.jamesdpeters.helpers.CONSTANTS;
//import com.jamesdpeters.integrators.abstracts.AbstractIntegrator;
//import com.jamesdpeters.integrators.abstracts.IntegratorType;
//import com.jamesdpeters.vectors.Vector3D;
//
//public class AccelerationIntegrator extends AbstractIntegrator {
//
//    public AccelerationIntegrator(IntegratorType integratorType) {
//        super(integratorType);
//    }
//
//    @Override
//    public Vector3D f(Body body, double dt, Vector3D dx) {
//            Vector3D accel = new Vector3D(0,0,0);
//            for(Body body2 : body.getExclusiveBodies()) {
//                    Vector3D delta = body2.getPosition().subtract(body.getPosition()).add(dx);
//                    double distance = delta.magnitude();
//                    // a(t)
//                    double forceMagnitude = ((body.getGMAU()) * body2.getMass()) / (distance * distance);
//                    accel = accel.add(delta.normalize().multiply(forceMagnitude).multiply(1 / body.getMass()));
//            }
//            return accel;
//    }
//
//    @Override
//    public Vector3D df(Body body, double dt, Vector3D dx) {
//        Vector3D accelDot = new Vector3D(0,0,0);
//        for(Body body2 : body.getExclusiveBodies()) {
//                Vector3D delta = body2.getPosition().subtract(body.getPosition()).add(dx);
//                double distance = delta.magnitude();
//                // a(t)
//                double forceMagnitudeDot = (-2* body2.getMass() * (body.getGMAU()))/(distance*distance*distance);
//                accelDot = accelDot.add(delta.normalize().multiply(forceMagnitudeDot).multiply(1 / body.getMass()));
//        }
//        return accelDot;
//    }
//
//}
