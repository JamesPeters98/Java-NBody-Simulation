package com.jamesdpeters.integrators.abstracts;

import com.jamesdpeters.bodies.Body;
import javafx.geometry.Point3D;

public abstract class IntegratorType {

    public abstract String getIntegratorName();

    public abstract Point3D step(AbstractIntegrator integrator, Body body, double dt);
}
