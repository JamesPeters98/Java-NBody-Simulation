package com.jamesdpeters.helpers.polynomialfit;

import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.builder.PlotBuilder;
import com.jamesdpeters.eclipse.CompareInfo;
import com.jamesdpeters.helpers.chisquare.ChiSquaredFitter;
import com.jamesdpeters.json.Graph;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class PolynomialFit {

    double[] coeffs;
    double minXRange, maxXRange, previousGuess;

    public PolynomialFit(){

    }

    public void fit(TreeMap<Double,Double> modelData, double minXRange, double maxXRange, double previousGuess){
        int degree = 15;
        this.maxXRange = maxXRange;
        this.minXRange = minXRange;
        this.previousGuess = previousGuess;
        PolynomialCurveFitter fitter = PolynomialCurveFitter.create(degree);
        fitter.withStartPoint(new double[]{0.98,0,10});

        WeightedObservedPoints points = new WeightedObservedPoints();
        modelData.entrySet().stream().filter(doubleDoubleEntry -> (doubleDoubleEntry.getKey() >= minXRange && doubleDoubleEntry.getKey() <= maxXRange)).forEach(entry -> {
            points.add(entry.getKey()-previousGuess,entry.getValue());
        });

        List<WeightedObservedPoint> pointsList = points.toList();
        coeffs = fitter.fit(pointsList);

        plot(pointsList);
    }

    private double getPolynomialValue(double x){
        double val = 0;
        for(int i=0; i<coeffs.length; i++){
            val += coeffs[i]*Math.pow(x,i);
            //System.out.println("coeff: "+coeffs[i]+" x^"+(i));
        }
        return val;
    }

    public void plot(List<WeightedObservedPoint> modelData) {
        List<Number> modelX = new ArrayList<>();
        List<Number> modelY = new ArrayList<>();

        modelData.forEach((point) -> {
            modelX.add(point.getX()+previousGuess);
            modelY.add(point.getY());
        });

        // SIM DATA
        List<Number> X = new ArrayList<>();
        List<Number> Y = new ArrayList<>();

        for(double x=minXRange; x<=maxXRange; x+=0.001){
            X.add(x);
            Y.add(getPolynomialValue(x-previousGuess));
        }

        Plot plt = Graph.getPlot();
        plt.figure("Fitted Polynomial");
        plotData(plt, modelX, modelY, "Simulation", "blue", "","b*","Day","Luminosity Ratio ($L/L_0$)");
        plotData(plt, X, Y, "Polynomial Fit", "red", "solid","","Day","Luminosity Ratio ($L/L_0$)");
        plt.legend().loc("best");
        Graph.openPlot(plt);
    }

    public void fitObservations(String experimentalDataPath, double TDBoffset, double spacing, int N){
        ChiSquaredFitter fitter = new ChiSquaredFitter();
        fitter.load(experimentalDataPath, TDBoffset);
        HashMap<Double,Double> chiSquares = new HashMap<>(); //Offset vs Previous Guess.
        for(double offset=-N*spacing; offset<=N*spacing; offset+=spacing){
            double chi2 = fitter.chiSquare(getModelData(offset,spacing));
            chiSquares.put(offset,chi2);
        }
        Optional<Map.Entry<Double,Double>> minChi2 = chiSquares.entrySet().stream().min(Comparator.comparing(Map.Entry::getValue));
        minChi2.ifPresent(entry -> System.out.println("New Guess: "+(entry.getKey()+previousGuess+TDBoffset)));
        plot(chiSquares);
    }

    private TreeMap<Double,Double> getModelData(double offset, double spacing){
        TreeMap<Double,Double> modelData = new TreeMap<>();
        for(double x=minXRange; x<=maxXRange; x+=spacing){
            modelData.put(x+offset,getPolynomialValue(x-previousGuess));
        }
        return modelData;
    }

    public static void plot(HashMap<Double,Double> chi2) {
        // SIM DATA
        List<Number> X = new ArrayList<>();
        List<Number> Y = new ArrayList<>();

        chi2.forEach((x,chi) -> {
            X.add(x);
            Y.add(chi);
        });

        Plot plt = Graph.getPlot();
        plt.figure("Chi^2 Fit");
        plotData(plt, X, Y, "", "red", "","r|", "Offset", "$\\chi^2$");
        Graph.openPlot(plt);
    }

//    public static void plot() {
//        // SIM DATA
//
//
//        Plot plt = Graph.getPlot();
//        plt.figure("Model Data");
//        plotData(plt, X, Y, "", "red", "solid","");
//        Graph.openPlot(plt);
//    }

    public static PlotBuilder plotData(Plot plt, List<Number> x, List<Number> y, String label, String color, String linestyle, String fmt, String xLabel, String yLabel){
        PlotBuilder plotBuilder = plt.plot()
                .add(x,y,fmt)
                .label(label)
                .linewidth("1")
                .color(color);
        if(linestyle!=null) plotBuilder.linestyle(linestyle);
        plt.xlabel(xLabel);
        plt.ylabel(yLabel);
        //plt.legend().loc("upper right");

        return plotBuilder;
    }
}
