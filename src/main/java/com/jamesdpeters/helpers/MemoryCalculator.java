package com.jamesdpeters.helpers;

import com.jamesdpeters.universes.Universe;

public class MemoryCalculator {

    public static void calculateEstimatedMemory(Universe universe){
        int bodies = universe.getBodies().size();
        int treeMapBytes = 40;
        int vector3DBytes = 40; //40 Bytes per vector3D
        long posSize = (long) (universe.runningTime()/universe.dt())/universe.resolution();

        System.out.println("RESOLUTION: "+universe.resolution());
        long memSize = (bodies*((3*vector3DBytes+treeMapBytes)*posSize));
        double memSizeGB = memSize/(Math.pow(10,9)); //Bytes
        System.out.println("ESTIMATED MEMORY SIZE: "+memSizeGB+" GB");

        try {
            checkMemory(memSize);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void checkMemory(long memSize) throws Exception {
        if((memSize >= getTotalMemory()) && (getTotalMemory() != -1)){
            throw new Exception("NOT ENOUGH MEMORY TO RUN THIS SIMULATION!");
        }
    }

    public static long getTotalMemory(){
        System.gc();
        /* This will return Long.MAX_VALUE if there is no preset limit */
        long maxMemory = Runtime.getRuntime().maxMemory();
        /* Maximum amount of memory the JVM will attempt to use */
        return (maxMemory == Long.MAX_VALUE ? -1 : maxMemory);
    }

    public static void printMemoryUsed(){
        long mem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("MEMORY USED: "+mem+" Bytes");
    }
}
