//package com.jamesdpeters.integrators.types;
//
//import com.jamesdpeters.bodies.Body;
//import com.jamesdpeters.integrators.abstracts.AbstractIntegrator;
//import com.jamesdpeters.integrators.abstracts.IntegratorType;
//import com.jamesdpeters.vectors.Vector3D;
//
//public class SecondOrderIntegratorType extends IntegratorType {
//
//    @Override
//    public Vector3D step(AbstractIntegrator i, Body body, double dt) {
//        // k1 = dt*df(t)
//        Vector3D k1 = i.df(body,0, Vector3D.ZERO).multiply(dt);
//        // f(t+dt) = f(t) + dt*df(t+dt/2, f(t) + k1/2)
//        return i.f(body,0, Vector3D.ZERO).add(i.df(body, dt/2, k1.multiply(1/2)).multiply(dt));
//    }
//
//    @Override
//    public String getIntegratorName() {
//        return "2ndOrder";
//    }
//
//}
