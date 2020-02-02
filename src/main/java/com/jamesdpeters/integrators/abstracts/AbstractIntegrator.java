//package com.jamesdpeters.integrators.abstracts;
//
//import com.jamesdpeters.bodies.Body;
//import com.jamesdpeters.vectors.Vector3D;
//
//public abstract class AbstractIntegrator {
//
//    double time; // Keep track of time.
//
//    public AbstractIntegrator(IntegratorType integratorType){
//        this.integratorType = integratorType;
//    }
//
//    //Function Integrator is implementing.
//    public abstract Vector3D f(Body body, double dt, Vector3D dx);
//
//    //Gradient of function Integrator is implementing.
//    public abstract Vector3D df(Body body, double dt, Vector3D dx);
//
//    //Performs a step dependant on the integrator being used e.g Euler, 2nd Order, 4th etc.
//    public Vector3D step(Body body, double dt){
//        return integratorType.step(this,body,dt);
//    }
//}
