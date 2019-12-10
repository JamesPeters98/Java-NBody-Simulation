package com.jamesdpeters.universes;

import com.jamesdpeters.bodies.Body;
import javafx.geometry.Point3D;

public class MomentumCorrector {

    public static void correct(Universe universe){
        Point3D momentum = Point3D.ZERO;
        double totalMass = 0;

        for(Body body : universe.bodies){
            momentum = momentum.add(body.getVelocity().multiply(body.getMass()));
            totalMass += body.getMass();
        }

        Point3D velocity = momentum.multiply(1/totalMass);

        for(Body body : universe.bodies){
            body.setVelocity(body.getVelocity().subtract(velocity));
        }

        System.out.println("TOTAL MOMENTUM = "+momentum.magnitude());
        System.out.println("TOTAL MASS = "+totalMass);
        System.out.println("VELOCITY CORRECT = "+velocity.magnitude());
        System.out.println("VELOCITY CORRECT = "+velocity);

    }
}
