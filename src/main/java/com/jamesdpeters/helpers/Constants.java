package com.jamesdpeters.helpers;

public class Constants {

    public static class METRES {
        // ASTRONOMICAL CONSTANT IN METRES.
        final static public double AU = 149597870700.0;
    }

    public static class KILOMETERS {
        final static double KM = 1000;

        // ASTRONOMICAL CONSTANT IN KILOMETRES.
        final static public double AU = Constants.METRES.AU/KM;
    }

    public static class CONVERSIONS {
        final static double KM_cubed = 1000000000;

        // Convert GM into Astronomical Units.
        final static public double GM_to_AU = (KM_cubed)/(Math.pow(METRES.AU,3)/Math.pow(SECONDS.DAY,2));
    }

    public static class SECONDS {
        public final static double DAY = 86400;
    }

    public static class EARTH {
        private final static double MASS = 5.97219;
        public static double getEarthMass(double ratio){
            return Math.pow(MASS*ratio,24);
        }

        private final static double GM = 398600.435436;
        public static double getEarthGM(double ratio){
            return ratio*GM;
        }

        private final static double RADIUS = 6371.01;
        public static double getEarthRadius(double ratio){
            return ratio*RADIUS;
        }
    }

    public static class SUN {
        private final static double MASS = 1988500;
        public static double getSunMass(double ratio){
            return Math.pow(MASS*ratio,24);
        }

        private final static double GM = 132712440041.93938;
        public static double getSunGM(double ratio){
            return ratio*GM;
        }

        private final static double RADIUS = 695700;
        public static double getSunRadius(double ratio){
            return ratio*RADIUS;
        }
    }

}
