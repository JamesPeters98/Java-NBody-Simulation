//package com.jamesdpeters.integrators.physics;
//
//import com.jamesdpeters.bodies.Body;
//import com.jamesdpeters.integrators.abstracts.AbstractIntegrator;
//import com.jamesdpeters.integrators.abstracts.IntegratorType;
//import com.jamesdpeters.vectors.Vector3D;
//
//public class PositionIntegrator extends AbstractIntegrator {
//
//    VelocityIntegrator velocityIntegrator;
//
//    public PositionIntegrator(IntegratorType integratorType) {
//        super(integratorType);
//        velocityIntegrator = new VelocityIntegrator(integratorType);
//    }
//
//    @Override
//    public Vector3D f(Body body, double dt, Vector3D dx) {
//        return body.getPosition();
//    }
//
//    @Override
//    public Vector3D df(Body body, double dt, Vector3D dx) {
//        return body.getVelocity().add(velocityIntegrator.df(body, dt, dx).multiply(dt));
//    }
//}
