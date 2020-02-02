package com.jamesdpeters.integrators;

import com.jamesdpeters.integrators.abstracts.Integrator;

public class IntegratorFactory {

    public static Integrator getDefaultIntegrator(){
        return getYoshidaIntegrator();
    }

    public static Integrator getLeapFrogIntegrator(){
        return new LeapFrogIntegrator();
    }

    public static Integrator getYoshidaIntegrator(){
        return new YoshidaIntegrator();
    }

//    public static Integrator getEulerIntegrator(){
//        return new StandardIntegrator(new EulerIntegratorType());
//    }
//
//    public static Integrator getSecondOrderIntegrator(){
//        return new StandardIntegrator(new SecondOrderIntegratorType());
//    }

    public static Integrator getRK4Integrator(){
        return new RK4Integrator();
    }

}
