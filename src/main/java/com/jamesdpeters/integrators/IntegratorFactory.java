package com.jamesdpeters.integrators;

import com.jamesdpeters.integrators.types.EulerIntegratorType;
import com.jamesdpeters.integrators.types.RK4IntegratorType;
import com.jamesdpeters.integrators.types.SecondOrderIntegratorType;

public class IntegratorFactory {

    public static Integrator getDefaultIntegrator(){
        return getRK4Integrator();
    }

    public static Integrator getEulerIntegrator(){
        return new Integrator(new EulerIntegratorType());
    }

    public static Integrator getSecondOrderIntegrator(){
        return new Integrator(new SecondOrderIntegratorType());
    }

    public static Integrator getRK4Integrator(){
        return new Integrator(new RK4IntegratorType());
    }

}
