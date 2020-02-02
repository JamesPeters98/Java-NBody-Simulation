package com.jamesdpeters.integrators;

public class IntegratorFactory {

    public static Integrator getDefaultIntegrator(){
        return getRK4Integrator();
    }

//    public static Integrator getLeapFrogIntegrator(){
//        return new LeapFrogIntegrator();
//    }

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
