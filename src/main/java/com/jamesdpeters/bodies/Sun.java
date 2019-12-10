//package com.jamesdpeters.bodies;
//
//import javafx.geometry.Point3D;
//import javafx.scene.paint.Color;
//import javafx.scene.paint.PhongMaterial;
//
//public class Sun extends Body {
//
//    private static final double RADIUS = 695700; //KM
//    private static final double MASS = 1988500*Math.pow(10,24); //1988500 × 10^24 kg
//
//    public Sun() {
//        super();
//        PhongMaterial material = new PhongMaterial();
//        material.setDiffuseColor(Color.ORANGE);
//        setMaterial(material);
//    }
//
//    @Override
//    public Point3D getInitialPosition() {
//        return new Point3D(0,0,0);
//    }
//
//    @Override
//    public Point3D getInitialVelocity() {
//        return new Point3D(0,0,0);
//    }
//
//    @Override
//    public String getName() {
//        return "Sun";
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
