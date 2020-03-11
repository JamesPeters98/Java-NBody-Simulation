package com.jamesdpeters.eclipse;

import com.jamesdpeters.bodies.Body;

import java.util.TreeMap;

public class TransitInfo {

    public TreeMap<Double,Double> totalTransits;
    public TreeMap<Body, TreeMap<Double,Double>> transits;

    public TransitInfo(TreeMap<Double,Double> totalTransits, TreeMap<Body, TreeMap<Double,Double>> transits){
        this.totalTransits = totalTransits;
        this.transits = transits;
    }
}
