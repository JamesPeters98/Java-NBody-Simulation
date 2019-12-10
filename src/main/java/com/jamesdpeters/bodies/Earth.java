//package com.jamesdpeters.bodies;
//
//import javafx.geometry.Point3D;
//import javafx.scene.image.Image;
//import javafx.scene.paint.PhongMaterial;
//
//public class Earth extends Body {
//
//    private static final double RADIUS = 6371; //KM
//    private static final double MASS = 5.972*Math.pow(10,24); //5.972 × 10^24 kg
//
//    private static final double MAP_WIDTH  = 8192 / 2d;
//    private static final double MAP_HEIGHT = 4092 / 2d;
//
//    //Initial Position - KM
//    private static final double X = 1.39*Math.pow(10,8);
//    private static final double Y = 5.36*Math.pow(10,7);
//    private static final double Z = -2.40*Math.pow(10,3);
//
//    //Initial Velocity - KM/S
//    private static final double VX = -1.12*Math.pow(10,1);
//    private static final double VY = 2.77*Math.pow(10,1);
//    private static final double VZ = -1.66*Math.pow(10,-3);
//
//    private static final String DIFFUSE_MAP =
//            "earth/earth_gebco8_texture_8192x4096.jpg";
//    private static final String NORMAL_MAP =
//            "earth/earth_normalmap_flat_8192x4096.jpg";
//    private static final String SPECULAR_MAP =
//            "earth/earth_specularmap_flat_8192x4096.jpg";
//
//    public Earth() {
//        PhongMaterial earthMaterial = new PhongMaterial();
//
//        earthMaterial.setDiffuseMap(
//                new Image(
//                        this.getClass().getClassLoader().getResourceAsStream(DIFFUSE_MAP),
//                        MAP_WIDTH,
//                        MAP_HEIGHT,
//                        true,
//                        true
//                )
//        );
//        earthMaterial.setBumpMap(
//                new Image(
//                        this.getClass().getClassLoader().getResourceAsStream(NORMAL_MAP),
//                        MAP_WIDTH,
//                        MAP_HEIGHT,
//                        true,
//                        true
//                )
//        );
//        earthMaterial.setSpecularMap(
//                new Image(
//                        this.getClass().getClassLoader().getResourceAsStream(SPECULAR_MAP),
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
//        return "Earth";
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
