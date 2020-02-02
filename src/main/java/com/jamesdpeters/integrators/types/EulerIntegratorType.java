//package com.jamesdpeters.integrators.types;
//
//import com.jamesdpeters.bodies.Body;
//import com.jamesdpeters.integrators.abstracts.AbstractIntegrator;
//import com.jamesdpeters.integrators.abstracts.IntegratorType;
//import com.jamesdpeters.vectors.Vector3D;
//
//public class EulerIntegratorType extends IntegratorType {
//
//    @Override
//    public String getIntegratorName() {
//        return "Euler";
//    }
//
//    @Override
//    public Vector3D step(AbstractIntegrator i, Body b, double dt) {
//        // f(t+dt) = f(t) + df(t)*dt
//        return i.f(b, 0, Vector3D.ZERO).add(i.df(b,0, Vector3D.ZERO).multiply(dt));
//    }
//}
