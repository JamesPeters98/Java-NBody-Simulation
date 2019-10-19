package com.jamesdpeters;

import com.jamesdpeters.universes.NormalUniverse;
import com.jamesdpeters.universes.Universe;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    Universe universe;

    public void start(Stage primaryStage) throws Exception {
        primaryStage.setOnCloseRequest(onCloseListener());
        universe = new NormalUniverse(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        universe.stop();
    }

    private EventHandler<WindowEvent> onCloseListener(){
        return windowEvent -> {
            universe.stop();
            Platform.exit();
        };
    }


}
