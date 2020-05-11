package com.jamesdpeters.helpers.polynomialfit;

import org.apache.commons.math3.fitting.AbstractCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;

import java.util.Collection;

public class Curve extends AbstractCurveFitter {


    @Override
    protected LeastSquaresProblem getProblem(Collection<WeightedObservedPoint> points) {
        return null;
    }
}
