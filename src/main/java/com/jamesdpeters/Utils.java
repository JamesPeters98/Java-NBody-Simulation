package com.jamesdpeters;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

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
}
