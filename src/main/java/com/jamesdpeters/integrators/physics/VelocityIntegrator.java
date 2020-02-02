//package com.jamesdpeters.integrators.physics;
//
//import com.jamesdpeters.bodies.Body;
//import com.jamesdpeters.integrators.abstracts.AbstractIntegrator;
//import com.jamesdpeters.integrators.abstracts.IntegratorType;
//import com.jamesdpeters.vectors.Vector3D;
//
//public class VelocityIntegrator extends AbstractIntegrator {
//
//    AccelerationIntegrator accelerationIntegrator;
//
//    public VelocityIntegrator(IntegratorType integratorType) {
//        super(integratorType);
//        accelerationIntegrator = new AccelerationIntegrator(integratorType);
//    }
//
//    @Override
//    public Vector3D f(Body body, double dt, Vector3D dx) {
//        return body.getVelocity();
//    }
//
//    @Override
//    public Vector3D df(Body body, double dt, Vector3D dx) {
//        return accelerationIntegrator.f(body,dt,dx);
//    }
//}
