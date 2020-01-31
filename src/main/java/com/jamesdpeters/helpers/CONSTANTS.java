package com.jamesdpeters.helpers;

public class CONSTANTS {

    public static class METRES {
        // ASTRONOMICAL CONSTANT IN METRES.
        final static public double AU = 149597870700.0;
    }

    public static class KILOMETERS {
        final static double KM = 1000;

        // ASTRONOMICAL CONSTANT IN KILOMETRES.
        final static public double AU = CONSTANTS.METRES.AU/KM;
    }

    public static class CONVERSIONS {
        final static double KM_cubed = 1000000000;

        // Convert GM into Astronomical Units.
        final static public double GM_to_AU = (KM_cubed)/(Math.pow(METRES.AU,3)/Math.pow(SECONDS.DAY,2));
    }

    public static class SECONDS {
        final static double DAY = 86400;
    }

}
