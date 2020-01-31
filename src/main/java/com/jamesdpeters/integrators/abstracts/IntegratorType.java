package com.jamesdpeters.integrators.abstracts;

import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.vectors.Vector3D;

public abstract class IntegratorType {

    public abstract String getIntegratorName();

    public abstract Vector3D step(AbstractIntegrator integrator, Body body, double dt);
}
