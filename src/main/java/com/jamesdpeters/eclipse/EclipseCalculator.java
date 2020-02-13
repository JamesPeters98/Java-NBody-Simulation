package com.jamesdpeters.eclipse;

import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.eclipse.EclipseInfo;
import com.jamesdpeters.json.CSVWriter;
import com.jamesdpeters.json.Graph;
import com.jamesdpeters.universes.Universe;
import com.jamesdpeters.vectors.Vector3D;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

public class EclipseCalculator {

    //This is just for Earth, Sun and Moon (Luna). Needs to be adapted for more general scenario.
    public static void findEclipses(Universe universe){
        Body sun = null, earth = null, moon = null;
        for(Body body : universe.getBodies()){
            if(body.isOrigin()) sun = body;
            if(body.getName().equals("Earth")) earth = body;
            if(body.getName().equals("Moon")) moon = body;
        }
        if(sun != null && earth != null && moon != null) {
            TreeMap<Double,Double> ConeRadius = new TreeMap<>();
            TreeMap<Double,Double> EdgeOfMoonDist = new TreeMap<>();
            TreeMap<Double,Double> Lambda = new TreeMap<>();
            TreeMap<Double,Double> Area = new TreeMap<>();
            TreeMap<Double,Double> JPLArea = new TreeMap<>();

            for(Integer step: sun.positions.keySet()) {
                //Simulated data
                Vector3D MoonPos = moon.positions.get(step);
                Vector3D SunPos = sun.positions.get(step);
                Vector3D EarthPos = earth.positions.get(step);
                calc(step*universe.dt(), EarthPos, SunPos, MoonPos, sun, earth, moon, ConeRadius, EdgeOfMoonDist, Lambda, Area);
            }

            for(Double time: sun.getJPLPositions().keySet()){
                Vector3D MoonPos = moon.getJPLPositions().get(time);
                Vector3D SunPos = sun.getJPLPositions().get(time);
                Vector3D EarthPos = earth.getJPLPositions().get(time);
                calc(time, EarthPos, SunPos, MoonPos, sun, earth, moon, null, null, null, JPLArea);
            }

            HashMap<Integer,EclipseInfo> eclipseInfoHashMap = calculateEclipseFeatures(sun.getStartDate(),Area);
            Graph.plotEclipse("Eclipse Plot",Area,JPLArea);
            try {
                CSVWriter.writeEclipseData("SolarSystem",ConeRadius,EdgeOfMoonDist,Lambda,Area);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                CSVWriter.writeEclipseInfo("SolarSystem",eclipseInfoHashMap,universe);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void calc(double time, Vector3D EarthPos, Vector3D SunPos, Vector3D MoonPos, Body sun, Body earth, Body moon, TreeMap<Double,Double> ConeRadius, TreeMap<Double,Double> EdgeOfMoonDist,TreeMap<Double,Double> Lambda,TreeMap<Double,Double> Area){
        Vector3D SunEarthVector = directionVector(EarthPos,SunPos);

        //Point perpendicular to line
        //Lambda < 0 means Moon is behind Earth from Sun's perspective.
        double lambda = (MoonPos.subtract(EarthPos).dotProduct(SunEarthVector)) / (SunEarthVector.dotProduct(SunEarthVector));

        Vector3D P = SunEarthVector.multiply(lambda).add(EarthPos);
        double SEdist = dist(EarthPos,SunPos).magnitude();
        double PEdist = dist(EarthPos, P).magnitude();

        // Radius of circle at point of cone perpendicular to Moon.
        double r1 = (PEdist / SEdist) * (sun.getBodyRadiusAU() - earth.getBodyRadiusAU()) + earth.getBodyRadiusAU();
        double MoonToP = dist(MoonPos, P).magnitude();
        double EdgeOfMoon = MoonToP - moon.getBodyRadiusAU();

        //Calculate Area of eclipse
        double r2 = moon.getBodyRadiusAU();
        double d = MoonToP;
        double A = 0;

        if(d >= r1 + r2) { // No intersection = 0 area;
            A = 0;
        } else if(d <= r1 - r2){ // If moon is inside cone area it's just the moons total area.
            A = Math.PI*r2*r2;
        } else { // Otherwise it's the intersection area between the two.
            double d1 = (r1 * r1 - r2 * r2 + d * d) / (2 * d);
            double d2 = d - d1;
            A = (r1 * r1) * Math.acos(d1 / r1) - d1 * Math.sqrt(r1 * r1 - d1 * d1)
                    + (r2 * r2) * Math.acos(d2 / r2) - d2 * Math.sqrt(r2 * r2 - d2 * d2);
        }

        double areaRatio = 1 - A/(Math.PI*r1*r1);

        if(ConeRadius != null) ConeRadius.put(time, r1);
        if(EdgeOfMoonDist != null) EdgeOfMoonDist.put(time, EdgeOfMoon);
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
