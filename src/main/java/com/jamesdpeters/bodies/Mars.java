//package com.jamesdpeters.bodies;
//
//import javafx.geometry.Point3D;
//import javafx.scene.image.Image;
//import javafx.scene.paint.Color;
//import javafx.scene.paint.PhongMaterial;
//
//public class Mars extends Body {
//
//    private static final double RADIUS = 3389.92; //KM
//    private static final double MASS = 6.4171*Math.pow(10,23); //5.972 × 10^24 kg
//
//    private static final double MAP_WIDTH  = 8192 / 2d;
//    private static final double MAP_HEIGHT = 4092 / 2d;
//
//    //Initial Position - KM
//    private static final double X = -2.471842535143256*Math.pow(10,8);
//    private static final double Y = 8.154525045308748*Math.pow(10,6);
//    private static final double Z = 6.235730264151237*Math.pow(10,6);
//
//    //Initial Velocity - KM/S
//    private static final double VX = 1.038441986927667*Math.pow(10,-1);
//    private static final double VY = -2.214476145739698*Math.pow(10,1);
//    private static final double VZ = -4.665844760734998*Math.pow(10,-1);
//
//    private static final String DIFFUSE_MAP =
//            "earth/earth_gebco8_texture_8192x4096.jpg";
//    private static final String NORMAL_MAP =
//            "earth/earth_normalmap_flat_8192x4096.jpg";
//    private static final String SPECULAR_MAP =
//            "earth/earth_specularmap_flat_8192x4096.jpg";
//
//    public Mars() {
//        PhongMaterial marsMat = new PhongMaterial();
//        marsMat.setDiffuseColor(Color.DARKRED);
//        setMaterial(marsMat);
//    }
//
//    @Override
//    public Point3D getInitialPosition() {
//        return new Point3D(X,Y,Z);
//    }
//
//    @Override
//    public Point3D getInitialVelocity() {
//        return new Point3D(VX,VY,VZ).multiply(1000); //CONVERT TO M/S!
//    }
//
//    @Override
//    public String getName() {
//        return "Mars";
//    }
//
//    @Override
//    public double getMass() {
//        return MASS;
//    }
//
//    @Override
//    public double getBodyRadius() {
//        return RADIUS;
//    }
//}
