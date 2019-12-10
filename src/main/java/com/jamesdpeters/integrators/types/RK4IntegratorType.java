package com.jamesdpeters.integrators.types;

import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.integrators.abstracts.AbstractIntegrator;
import com.jamesdpeters.integrators.abstracts.IntegratorType;
import javafx.geometry.Point3D;

public class RK4IntegratorType extends IntegratorType {

    @Override
    public Point3D step(AbstractIntegrator i, Body body, double dt) {
        Point3D k1 = i.df(body,0,Point3D.ZERO).multiply(dt);
        Point3D k2 = i.df(body,dt*0.5,k1.multiply(0.5)).multiply(dt);
        Point3D k3 = i.df(body,dt*0.5,k2.multiply(0.5)).multiply(dt);
        Point3D k4 = i.df(body,dt,k3).multiply(dt);

        // f(t+dt) = f(t) + dt*df(t+dt/2, f(t) + k1/2)
        Point3D f = i.f(body,0,Point3D.ZERO);
        Point3D sumdx = Point3D.ZERO;
        sumdx = sumdx.add(k1);
        sumdx = sumdx.add(k2.multiply(2));
        sumdx = sumdx.add(k3.multiply(2));
        sumdx = sumdx.add(k4);
        sumdx = sumdx.multiply((double) 1 / (double) 6);

        return f.add(sumdx);
    }

    @Override
    public String getIntegratorName() {
        return "RK4";
    }

}
