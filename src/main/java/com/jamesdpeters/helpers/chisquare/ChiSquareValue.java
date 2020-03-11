package com.jamesdpeters.helpers.chisquare;

public class ChiSquareValue {
    public double observed, model, error;

    public ChiSquareValue(double model, double observed, double error){
        this.observed = observed;
        this.model = model;
        this.error = error;
    }

    public double getChi2(){
        return Math.pow(observed-model,2)/Math.pow(error,2);
    }
}
