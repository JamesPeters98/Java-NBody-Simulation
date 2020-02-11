package com.jamesdpeters.bodies;

import com.github.sh0nk.matplotlib4j.Plot;
import com.jamesdpeters.json.Graph;
import com.jamesdpeters.vectors.Vector3D;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BodyErrorWorker {

    public static double calculateError(Body body){
        double dt = body.getUniverse().dt();
        Body origin = body.getUniverse().getOriginBody();
        int steps = 0;
        List<Number> times = new ArrayList<>();
        List<Number> errors = new ArrayList<>();

        if(body.getJPLPositions() != null && body.getJPLPositions().size() > 0) {
            for (Map.Entry<Double, Vector3D> entry : body.getJPLPositions().entrySet()) {
                double time = entry.getKey();
                int step = (int) (time/dt);
                Vector3D truePos = entry.getValue();
                    if (body.positions.containsKey(step)) {
                        steps++;
                        Vector3D simPos = body.positions.get(step);
                        Vector3D originPoint = origin.positions.get(step);
                        simPos = simPos.subtract(originPoint);
                        double error = truePos.subtract(simPos).magnitude();
                        //System.out.println(time+": True: "+truePos+" Sim: "+simPos);
                        times.add(time);
                        errors.add(error);
                    }
            }


//            System.out.println(body.getName()+" Error value: "+errorVal);
//            Plot plt = Graph.getPlot();
//            plt.figure("Error Plot - "+body.getName());
//            Graph.plotData(plt,times,errors,"Errors for "+body.getName(),"red");
//            Graph.openPlot(plt);
        }
        double errorSum = errors.stream().mapToDouble(Number::doubleValue).sum();
        double errorVal = errorSum/steps;
        return errorVal;
    }
}
