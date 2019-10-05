package com.jamesdpeters.bodies;

import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;

public class Earth extends Body {

    private static final double RADIUS = 6371; //KM
    private static final double MASS = 5.972*Math.pow(10,24); //5.972 × 10^24 kg

    private static final double MAP_WIDTH  = 8192 / 2d;
    private static final double MAP_HEIGHT = 4092 / 2d;

    private static final String DIFFUSE_MAP =
            "earth/earth_gebco8_texture_8192x4096.jpg";
    private static final String NORMAL_MAP =
            "earth/earth_normalmap_flat_8192x4096.jpg";
    private static final String SPECULAR_MAP =
            "earth/earth_specularmap_flat_8192x4096.jpg";

    public Earth(String name, double radius, double x, double y, double z) {
        super(name, radius, MASS, x, y, z);
        PhongMaterial earthMaterial = new PhongMaterial();

        earthMaterial.setDiffuseMap(
                new Image(
                        this.getClass().getClassLoader().getResourceAsStream(DIFFUSE_MAP),
                        MAP_WIDTH,
                        MAP_HEIGHT,
                        true,
                        true
                )
        );
        earthMaterial.setBumpMap(
                new Image(
                        this.getClass().getClassLoader().getResourceAsStream(NORMAL_MAP),
                        MAP_WIDTH,
                        MAP_HEIGHT,
                        true,
                        true
                )
        );
        earthMaterial.setSpecularMap(
                new Image(
                        this.getClass().getClassLoader().getResourceAsStream(SPECULAR_MAP),
                        MAP_WIDTH,
                        MAP_HEIGHT,
                        true,
                        true
                )
        );

        setMaterial(earthMaterial);
    }

    public Earth(double x, double y, double z){
        this("Earth", RADIUS, x,y,z);
    }
}
