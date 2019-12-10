//package com.jamesdpeters.bodies;
//
//import javafx.geometry.Point3D;
//import javafx.scene.image.Image;
//import javafx.scene.paint.PhongMaterial;
//
//public class Moon extends Body {
//
//    private static final double RADIUS = 1737; //KM
//    private static final double MASS = 7.34767309*Math.pow(10,22); //5.972 × 10^24 kg
//    private static final double MAP_WIDTH  = 8192 / 2d;
//    private static final double MAP_HEIGHT = 4092 / 2d;
//
//    private static final String DIFFUSE_MAP =
//            "";
//    private static final String NORMAL_MAP =
//            "";
//    private static final String SPECULAR_MAP =
//            "";
//
//    public Moon() {
//        super();
//
//        setVelocity(new Point3D(0,1022,0));
//
//        PhongMaterial earthMaterial = new PhongMaterial();
//
//        earthMaterial.setDiffuseMap(
//                new Image(
//                        this.getClass().getResourceAsStream(DIFFUSE_MAP),
//                        MAP_WIDTH,
//                        MAP_HEIGHT,
//                        true,
//                        true
//                )
//        );
//        earthMaterial.setBumpMap(
//                new Image(
//                        this.getClass().getResourceAsStream(NORMAL_MAP),
//                        MAP_WIDTH,
//                        MAP_HEIGHT,
//                        true,
//                        true
//                )
//        );
//        earthMaterial.setSpecularMap(
//                new Image(
//                        this.getClass().getResourceAsStream(SPECULAR_MAP),
//                        MAP_WIDTH,
//                        MAP_HEIGHT,
//                        true,
//                        true
//                )
//        );
//
//        setMaterial(earthMaterial);
//    }
//
//    @Override
//    public Point3D getInitialPosition() {
//        return null;
//    }
//
//    @Override
//    public Point3D getInitialVelocity() {
//        return null;
//    }
//
//    @Override
//    public String getName() {
//        return null;
//    }
//
//    @Override
//    public double getMass() {
//        return 0;
//    }
//
//    @Override
//    public double getBodyRadius() {
//        return 0;
//    }
//}
