package com.jamesdpeters.universes;

import com.jamesdpeters.MouseControl;
import com.jamesdpeters.bodies.Body;
import com.sun.javafx.perf.PerformanceTracker;
import javafx.animation.AnimationTimer;
import javafx.concurrent.Task;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class Universe {

    List<Body> bodies;
    private Stage stage;
    private double dt;
    private double events = 0;
    private long cpuTime = 0;
    private long lastTime = 0;

    private static final int WIDTH = 1800;
    private static final int HEIGHT = 900;
    private AnchorPane pane;
    private boolean running = true;
    private PerformanceTracker performanceTracker;

    private Label FPS, EPS;
    private double instantEPS;

    public Universe(double dt, Stage stage){
        this.dt = dt;
        this.stage = stage;
        initDisplay();
    }

    private void initDisplay(){
        pane = new AnchorPane();
        Group group = new Group();
        PerspectiveCamera camera = new PerspectiveCamera();
        Scene mainScene = new Scene(pane,WIDTH,HEIGHT,true);

        //Main simulation scene
        SubScene scene = new SubScene(group, WIDTH, HEIGHT, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.BLACK);
        scene.setCamera(camera);
        pane.getChildren().add(scene);

        setupUI();

        performanceTracker = PerformanceTracker.getSceneTracker(mainScene);

        camera.setNearClip(0.01);
        camera.setFarClip(1000000);
        camera.setTranslateX(-WIDTH/2);
        camera.setTranslateY(-HEIGHT/2);
        camera.setTranslateZ(-1500000);

        bodies = createBodies();
        for(Body body : bodies){
            body.addToGroup(group);
            body.setUniverse(this);
            body.setBodies(bodies);
        }

        stage.setTitle(getName());
        stage.setScene(mainScene);
        stage.show();
        timeline();
        new MouseControl(stage,group,mainScene);
    }

    private void timeline(){
        lastTime = System.nanoTime();
        new AnimationTimer(){
            @Override
            public void handle(long now) {
                for(Body body : bodies) body.drawPosition();
                setFPS(performanceTracker.getInstantFPS());
                setEPS(instantEPS);
            }
        }.start();

        Task<Integer> task = new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                while(running){
                    long now = System.nanoTime();
                    //System.out.println("Current Universe Time: "+ TimeUnit.SECONDS.toMinutes((long) time));
                    loop();
                    events++;
                    cpuTime += (now-lastTime);
                    lastTime = now;

                    if(cpuTime > TimeUnit.SECONDS.toNanos(1)){
                        double t = events*dt;
                        System.out.println("EPS: "+events+" = "+t+"x");
                        instantEPS = events;
                        events = 0;
                        cpuTime = 0;
                    }
                }
                return 0;
            }
        };
        Thread backgroundThread = new Thread(task);
        backgroundThread.setDaemon(false);
        backgroundThread.setName("Background Thread");
        backgroundThread.start();
    }

    private void setupUI(){
        FPS = new Label();
        EPS = new Label();
        EPS.setTranslateY(FPS.getTranslateY()+15);

        FPS.setTextFill(Color.web("#FFFFFF"));
        EPS.setTextFill(Color.web("#FFFFFF"));
        pane.getChildren().addAll(FPS,EPS);
    }

    private void setFPS(float fps){
        FPS.setText("FPS: "+Math.round(fps));
    }

    private void setEPS(double eps){
        EPS.setText("EPS: "+Math.round(eps));
    }

    /**
     * Called every tick!
     */
    abstract void loop() throws InterruptedException;

    /**
     * @return a list of all bodies in this Universe.
     */
    abstract List<Body> createBodies();

    abstract String getName();
    public abstract double G();

    public double dt() {
        return dt;
    }

    public void stop(){
        running = false;
    }
}
