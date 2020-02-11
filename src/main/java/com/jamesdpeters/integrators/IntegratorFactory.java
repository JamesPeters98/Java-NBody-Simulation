package com.jamesdpeters.integrators;

import com.jamesdpeters.integrators.abstracts.Integrator;
import com.jamesdpeters.integrators.types.EulerIntegrator;
import com.jamesdpeters.integrators.types.LeapFrogIntegrator;
import com.jamesdpeters.integrators.types.RK4Integrator;
import com.jamesdpeters.integrators.types.YoshidaIntegrator;

public class IntegratorFactory {

    public static Integrator getDefaultIntegrator(){
        return getRK4Integrator();
    }

    public static Integrator getLeapFrogIntegrator(){
        return new LeapFrogIntegrator();
    }

    public static Integrator getYoshidaIntegrator(){
        return new YoshidaIntegrator();
    }

    public static Integrator getEulerIntegrator(){
        return new EulerIntegrator();
    }
//
//    public static Integrator getSecondOrderIntegrator(){
//        return new StandardIntegrator(new SecondOrderIntegratorType());
//    }

    public static Integrator getRK4Integrator(){
        return new RK4Integrator();
    }

}
