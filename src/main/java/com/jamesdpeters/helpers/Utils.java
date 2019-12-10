package com.jamesdpeters.helpers;

import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.universes.Universe;
import javafx.geometry.Point3D;
import javafx.scene.control.Label;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import org.jzy3d.maths.Coord3d;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Utils {

    public static Label createUILabel(){
        Label label = new Label();
        label.setTextFill(Color.web("#FFFFFF"));
        return label;
    }

    public static Label createUILabel(GridPane pane, int row){
        Label label = createUILabel();
        pane.addRow(row,label);
        return label;
    }

    public static Coord3d fromPoint3D(Point3D point3D){
        return new Coord3d(point3D.getX(),point3D.getY(),point3D.getZ());
    }

    public static URI getResource(String filename) throws URISyntaxException {
        return Utils.class.getClassLoader().getResource("./"+filename).toURI();
    }


}
