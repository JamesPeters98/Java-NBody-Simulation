package com.jamesdpeters.vectors;

import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.json.CSVWriter;
import com.jamesdpeters.json.Graph;
import com.jamesdpeters.universes.Universe;
import org.apache.commons.math3.geometry.spherical.twod.Edge;
import org.jzy3d.colors.Color;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class EclipseCalculator {

    public EclipseCalculator(){

    }

    public static Vector3D calculatePoint(Universe universe){
        Body sun = null, earth = null, moon = null;
        Vector3D P = Vector3D.ZERO;
        for(Body body : universe.getBodies()){
            if(body.isOrigin()) sun = body;
            if(body.getName().equals("Earth")) earth = body;
            if(body.getName().equals("Moon")) moon = body;
        }
        if(sun != null && earth != null && moon != null) {
            //double time = sun.positions.higherKey(249.0);
            TreeMap<Double,Double> ConeRadius = new TreeMap<>();
            TreeMap<Double,Double> EdgeOfMoonDist = new TreeMap<>();
            TreeMap<Double,Double> Lambda = new TreeMap<>();
            for(Double time: sun.positions.keySet()) {
                Vector3D SunEarthVector = directionVector(earth, sun, time);
                Vector3D SunPos = sun.positions.get(time);
                Vector3D MoonPos = moon.positions.get(time);
                Vector3D EarthPos = earth.positions.get(time);

                //Point perpendicular to line
                //Lambda < 0 means Moon is behind Earth from Sun's perspective.
                double lambda = (MoonPos.subtract(EarthPos).dotProduct(SunEarthVector)) / (SunEarthVector.dotProduct(SunEarthVector));

                    P = SunEarthVector.multiply(lambda).add(EarthPos);
                    double SEdist = dist(earth, sun, time).magnitude();
                    double PEdist = dist(earth, P, time).magnitude();

                    // Radius of circle at point of cone perpendicular to Moon.
                    double RE = (PEdist / SEdist) * (sun.getBodyRadiusAU() - earth.getBodyRadiusAU()) + earth.getBodyRadiusAU();
                    double MoonToP = dist(moon, P, time).magnitude();
                    double EdgeOfMoon = MoonToP - moon.getBodyRadiusAU();

                    ConeRadius.put(time, RE);
                    EdgeOfMoonDist.put(time, EdgeOfMoon);
                    Lambda.put(time, lambda);
            }
            try {
                CSVWriter.writeEclipseData(ConeRadius,EdgeOfMoonDist,Lambda);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            System.out.println("RE: "+RE);
//            System.out.println("Edge of Moon: "+EdgeOfMoon);
//
//            System.out.println("SUN POS: "+SunPos);
//            System.out.println("Earth POS: "+EarthPos);
//            System.out.println("Moon POS: "+MoonPos);
//            System.out.println("PERPENDICULAR POINT: "+P);
//
//            List<Vector3D> vectors = new ArrayList<>();
//            vectors.add(MoonPos);
//            //vectors.add(SunPos);
//            vectors.add(EarthPos);
//            vectors.add(P);
//
//            Color[] colors = new Color[]{Color.GRAY,Color.GREEN,Color.RED};
//
//            Graph.plotStationaryPoints(vectors,colors);
        }

        return P;
    }

    private static Vector3D directionVector(Body from, Body to, double time){
        return dist(from, to, time).normalize();
    }

    private static Vector3D dist(Body from, Body to, double time){
        Vector3D fromPos = from.positions.get(time);
        Vector3D toPos = to.positions.get(time);
        return toPos.subtract(fromPos);
    }

    private static Vector3D dist(Body from, Vector3D to, double time){
        Vector3D fromPos = from.positions.get(time);
        return to.subtract(fromPos);
    }

}
