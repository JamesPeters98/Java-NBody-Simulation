package com.jamesdpeters.eclipse;

import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.helpers.Utils;
import com.jamesdpeters.json.CSVWriter;
import com.jamesdpeters.json.Graph;
import com.jamesdpeters.universes.Universe;
import com.jamesdpeters.vectors.Vector3D;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class TransitCalculator {

    public static void plotTotalTransits(Universe universe, Vector3D direction){
        TreeMap<Double,Double> totalTransit = new TreeMap<>();
        for(Body body : universe.getOriginBody().getExclusiveBodies()){
            TreeMap<Double,Double> transit = findTransits(universe,body,direction,true);
            for(Map.Entry<Double,Double> entry : transit.entrySet()){
                double value = entry.getValue()-1;
                Utils.addToTreeMapValue(totalTransit,entry.getKey(),value);
            }
        }

        for(Map.Entry<Double,Double> entry : totalTransit.entrySet()){
            Utils.addToTreeMapValue(totalTransit,entry.getKey(),1.0);
        }
        Graph.plotEclipse("Total Transits",totalTransit,null);
    }

    //This will calculate the Transits for the given body in the given direction.
    public static TreeMap<Double, Double> findTransits(Universe universe, Body planet, Vector3D direction, boolean plot){
        Body star = universe.getOriginBody();

        TreeMap<Double,Double> ConeRadius = new TreeMap<>();
        TreeMap<Double,Double> EdgeOfMoonDist = new TreeMap<>();
        TreeMap<Double,Double> Lambda = new TreeMap<>();
        TreeMap<Double,Double> Area = new TreeMap<>();

        for(Integer step: star.positions.keySet()) {
            //Simulated data
            Vector3D planetPos = planet.positions.get(step);
            Vector3D starPos = star.positions.get(step);
            calc(step*universe.dt(),direction,starPos,planetPos,star,planet, ConeRadius, EdgeOfMoonDist, Lambda, Area);
        }


        HashMap<Integer,EclipseInfo> eclipseInfoHashMap = calculateEclipseFeatures(star.getStartDate(),Area);
        if(plot) Graph.plotEclipse("Transits for "+planet.getName(),Area,null);
            try {
                CSVWriter.writeEclipseData("Trappist/"+planet.getName(),ConeRadius,EdgeOfMoonDist,Lambda,Area);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                CSVWriter.writeEclipseInfo("Trappist/"+planet.getName(),eclipseInfoHashMap,universe);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return Area;
    }

    private static void calc(double time, Vector3D direction, Vector3D starPos, Vector3D planetPos, Body star, Body planet, TreeMap<Double,Double> ConeRadius, TreeMap<Double,Double> EdgeOfMoonDist,TreeMap<Double,Double> Lambda,TreeMap<Double,Double> Area){

        //Point perpendicular to line
        //Lambda < 0 means Moon is behind Earth from Sun's perspective.
        double lambda = (planetPos.subtract(starPos).dotProduct(direction)) / (direction.dotProduct(direction));


        Vector3D P = direction.multiply(lambda).add(starPos);

        // Radius of circle at point of cone perpendicular to Moon.
        double r1 = star.getBodyRadiusAU();
        double planetToP = dist(planetPos, P).magnitude();
        double EdgeOfPlanet = planetToP - planet.getBodyRadiusAU();

        double areaRatio = 1;

        if(lambda > 0) {
            //Calculate Area of eclipse
            double r2 = planet.getBodyRadiusAU();
            double d = planetToP;
            double A = 0;

            if (d >= r1 + r2) { // No intersection = 0 area;
                A = 0;
            } else if (d <= r1 - r2) { // If moon is inside cone area it's just the moons total area.
                A = Math.PI * r2 * r2;
            } else { // Otherwise it's the intersection area between the two.
                double d1 = (r1 * r1 - r2 * r2 + d * d) / (2 * d);
                double d2 = d - d1;
                A = (r1 * r1) * Math.acos(d1 / r1) - d1 * Math.sqrt(r1 * r1 - d1 * d1)
                        + (r2 * r2) * Math.acos(d2 / r2) - d2 * Math.sqrt(r2 * r2 - d2 * d2);
            }
            areaRatio = 1 - A / (Math.PI * r1 * r1);
        }

        if(ConeRadius != null) ConeRadius.put(time, r1);
        //if(EdgeOfMoonDist != null) EdgeOfMoonDist.put(time, EdgeOfMoon);
        if(Lambda != null) Lambda.put(time, lambda);
        if(Area != null) Area.put(time, areaRatio);
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

    private static Vector3D dist(Vector3D from, Vector3D to){
        return to.subtract(from);
    }

    private static Vector3D directionVector(Vector3D from, Vector3D to){
        return dist(from,to).normalize();
    }


    private static HashMap<Integer, EclipseInfo> calculateEclipseFeatures(LocalDateTime startDate, TreeMap<Double,Double> area){
        double prevL = 1;
        HashMap<Integer,EclipseInfo> eclipseList = new HashMap<>();
        int eclipses = 0;
        for(Map.Entry<Double,Double> entry : area.entrySet()){
            double time = entry.getKey();
            double L = entry.getValue();
            long offset = (long) (time*86400);

            if(prevL == 1 && L < 1){
                //Start of eclipse.
                EclipseInfo info = new EclipseInfo();
                info.startDate = startDate.plusSeconds(offset);
                eclipseList.put(eclipses,info);
            }
            if(prevL < 1 && L == 1){
                //End of eclipse.
                EclipseInfo info = eclipseList.get(eclipses);
                info.endDate = startDate.plusSeconds(offset);
                eclipses++;
            }
            prevL = L;
        }
        return eclipseList;
    }



}
