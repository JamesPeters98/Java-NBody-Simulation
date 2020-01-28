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

        /* Total number of processors or cores available to the JVM */
        System.out.println("Available processors (cores): " +
                Runtime.getRuntime().availableProcessors());

        /* Total amount of free memory available to the JVM */
        System.out.println("Free memory (bytes): " +
                Runtime.getRuntime().freeMemory());

        /* This will return Long.MAX_VALUE if there is no preset limit */
        long maxMemory = Runtime.getRuntime().maxMemory();
        /* Maximum amount of memory the JVM will attempt to use */
        System.out.println("Maximum memory (bytes): " +
                (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory));

        /* Total memory currently in use by the JVM */
        System.out.println("Total memory (bytes): " +
                Runtime.getRuntime().totalMemory());
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
