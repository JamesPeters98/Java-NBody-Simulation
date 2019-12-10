package com.jamesdpeters.integrators.abstracts;

import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.integrators.Integrator;
import javafx.geometry.Point3D;

import java.util.List;

public abstract class AbstractIntegrator {

    double time; // Keep track of time.
    IntegratorType integratorType;

    public AbstractIntegrator(IntegratorType integratorType){
        this.integratorType = integratorType;
    }

    //Function Integrator is implementing.
    public abstract Point3D f(Body body, double dt, Point3D dx);

    //Gradient of function Integrator is implementing.
    public abstract Point3D df(Body body, double dt, Point3D dx);

    //Performs a step dependant on the integrator being used e.g Euler, 2nd Order, 4th etc.
    public Point3D step(Body body, double dt){
        return integratorType.step(this,body,dt);
    }
}