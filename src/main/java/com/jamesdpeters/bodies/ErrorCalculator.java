package com.jamesdpeters.bodies;

import com.jamesdpeters.vectors.Vector3D;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ErrorCalculator {

    //Returns a map of Key:Step and Value:Delta
    public static TreeMap<Double,Double> calculate(Body body){
        double dt = body.getUniverse().dt();
        Body origin = body.getUniverse().getOriginBody();
        TreeMap<Double,Double> deltas = new TreeMap<>();

        if(body.getJPLPositions() != null && body.getJPLPositions().size() > 0) {
            for (Map.Entry<Double, Vector3D> entry : body.getJPLPositions().entrySet()) {
                double time = entry.getKey();
                if(time <= 0) continue;
                double stepDouble = (time/dt);
                int step = (int) Math.round(stepDouble);
                if(step != stepDouble) continue;

                Vector3D truePos = entry.getValue();
                if (body.positions.containsKey(step)) {
                    Vector3D simPos = body.positions.get(step);
                    Vector3D originPoint = origin.positions.get(step);
                    simPos = simPos.subtract(originPoint);
                    double error = truePos.subtract(simPos).magnitude();
                    deltas.put(time,error);
                }
            }
        }

        return deltas;
    }

    public static TreeMap<Double,Double> calculateZError(Body body){
        double dt = body.getUniverse().dt();
        Body origin = body.getUniverse().getOriginBody();
        TreeMap<Double,Double> deltas = new TreeMap<>();

        if(body.getJPLPositions() != null && body.getJPLPositions().size() > 0) {
            for (Map.Entry<Double, Vector3D> entry : body.getJPLPositions().entrySet()) {
                double time = entry.getKey();
                if(time <= 0) continue;
                double stepDouble = (time/dt);
                int step = (int) Math.round(stepDouble);
                if(step != stepDouble) continue;

                Vector3D truePos = entry.getValue();
                if (body.positions.containsKey(step)) {
                    Vector3D simPos = body.positions.get(step);
                    Vector3D originPoint = origin.positions.get(step);
                    simPos = simPos.subtract(originPoint);
                    double error = truePos.getZ()-simPos.getZ();
                    deltas.put(time,error);
                }
            }
        }

        return deltas;
    }
}
