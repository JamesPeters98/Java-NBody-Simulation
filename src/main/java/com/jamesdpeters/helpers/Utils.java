package com.jamesdpeters.helpers;

import com.jamesdpeters.vectors.Vector3D;

import org.jzy3d.maths.Coord3d;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.TreeMap;

public class Utils {

//    public static Label createUILabel(){
//        Label label = new Label();
//        label.setTextFill(Color.web("#FFFFFF"));
//        return label;
//    }
//
//    public static Label createUILabel(GridPane pane, int row){
//        Label label = createUILabel();
//        pane.addRow(row,label);
//        return label;
//    }

    public static Coord3d fromPoint3D(Vector3D vector3D){
        return new Coord3d(vector3D.getX(), vector3D.getY(), vector3D.getZ());
    }

    public static URI getResource(String filename) throws URISyntaxException {
        return Utils.class.getClassLoader().getResource("./"+filename).toURI();
    }

//    public static boolean haveBodiesStoppedRunning(List<Body> bodies){
////        boolean stoppedRunning = true;
////        for(Body body : bodies)
////            if (body.isRunning()) {
////                stoppedRunning = false;
////                break;
////            }
////        return stoppedRunning;
//    }

    public static int getExponentForNumber(double number)
    {
        String numberAsString = String.valueOf(number);
        return numberAsString.substring(numberAsString.indexOf('.') + 1).length() * -1;
    }

    public static void addToTreeMapValue(TreeMap<Double,Double> map, Double key, Double valueToAdd){
        if(map.containsKey(key)){
            double prev = map.get(key);
            map.put(key,prev+valueToAdd);
        } else {
            map.put(key,valueToAdd);
        }
    }

    public static String colorToHex(Color color){
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    public static Color randomColor(){
        Random random = new Random();
        int R = random.nextInt(256);
        int G = random.nextInt(256);
        int B = random.nextInt(256);
        return new Color(R,B,G);
    }

    public static org.jzy3d.colors.Color convert(Color color){
        return new org.jzy3d.colors.Color(color.getRed(),color.getGreen(),color.getBlue());
    }

}
