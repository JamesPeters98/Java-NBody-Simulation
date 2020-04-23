package com.jamesdpeters.eclipse;

public class LimbDarkening {

    public static double intensityRatio(double starRadius, double distanceFromCenter, double planet){
        double d = distanceFromCenter;
        if(d>(starRadius+planet)){
            return 1;
        }

        System.out.println("Star Radius: "+starRadius+" Planet Radius: "+planet+" D: "+distanceFromCenter);

        double a = (2.0/5.0);
        double b = (3.0/5.0);

//        if((d+planet) > starRadius){
//            d = ((distanceFromCenter*distanceFromCenter)-(planet*planet)+(starRadius*starRadius))/(2*distanceFromCenter);
//            System.out.println("Using center of segment.");
//        }
        double ratio = (a + b*(starRadius/Math.sqrt(starRadius*starRadius+d*d)));
        System.out.println("Ratio: "+ratio);

        return ratio;
    }

}
