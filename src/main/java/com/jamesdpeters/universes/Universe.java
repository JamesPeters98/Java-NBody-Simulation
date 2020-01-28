package com.jamesdpeters.universes;

import com.jamesdpeters.helpers.DelayTimer;
import com.jamesdpeters.helpers.MouseControl;
import com.jamesdpeters.helpers.Utils;
import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.json.CSVWriter;
import com.jamesdpeters.json.Graph;
import com.sun.javafx.perf.PerformanceTracker;
import javafx.animation.AnimationTimer;
import javafx.concurrent.Task;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class Universe {

    List<Body> bodies;
    Body originBody;
    private Stage stage;
    private double events = 0;
    private long cpuTime = 0;
    private long lastTime = 0;
    private double universeTime = 0;
    public transient TreeMap<Double, Double> energyShift;

    private static final int WIDTH = 1800;
    private static final int HEIGHT = 900;
    private AnchorPane pane;
    private boolean running = true;
    private PerformanceTracker performanceTracker;

    private Label FPS, EPS, UniverseTime, USPS;
    private double instantEPS;

    ExecutorService service;
    Universe universe;

    public Universe(Stage stage){
        this.stage = stage;
        energyShift = new TreeMap<>();
    }

    /**
     *  MUST BE CALLED BEFORE SIMULATION STARTS
     */
    public void init(){
        universe = this;
        pane = new AnchorPane();
        Group group = new Group();
        Camera camera = new PerspectiveCamera(false);
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
        camera.setTranslateZ(-1000000000);

        bodies = createBodies();
        for(Body body : bodies){
            if(body.isOrigin()) originBody = body;
            body.addToGroup(group);
            body.setUniverse(this);
            body.setBodies(bodies);
            body.addLabelToPane(pane);
        }
        MomentumCorrector.correct(this);
        energyShift.put(0.0,getTotalEnergy());

        this.service = Executors.newFixedThreadPool(bodies.size());

        stage.setTitle(getName());
        stage.setScene(mainScene);
        //stage.show();
        timeline();
        new MouseControl(stage,group,mainScene,bodies,camera);
    }

    private void timeline(){
        lastTime = System.nanoTime();
        new AnimationTimer(){
            @Override
            public void handle(long now) {
                for(Body body : bodies) body.drawPosition();
                setFPS(performanceTracker.getInstantFPS());
                setEPS(instantEPS);
                setUniverseTime((long) universeTime);
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
                    universeTime += dt();

                    if(cpuTime > TimeUnit.SECONDS.toNanos(1)){
                        double t = events*dt();
                        System.out.println("Universe Time: "+TimeUnit.SECONDS.toDays((long) universeTime)+" Days");
                        //System.out.println("Universe Total Energy: "+getTotalEnergy()+" (J)");
                        instantEPS = events;
                        events = 0;
                        cpuTime = 0;
                    }

                    // Every 1000 timesteps
                    if(universeTime % (5000*dt()) == 0){
                        energyShift.put(universeTime,getTotalEnergy());
                    }

                    if(universeTime >= runningTime()){
                        running = false;
                        System.out.println("------------------------");
                        System.out.println("--Finished Simulation!--");
                        System.out.println("------------------------");
                        Graph.plotTrajectory(universe,500);
                        for(Body body : bodies){
                            Graph.plotBody(body);
                            try {
                                CSVWriter.writeBody(body, 500);
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        try {
                            CSVWriter.writeEnergyShift(universe);
                        }  catch (IOException e){
                            e.printStackTrace();
                        }
                        return 0;
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
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_LEFT);

        FPS = Utils.createUILabel(grid,0);
        EPS = Utils.createUILabel(grid, 1);
        USPS = Utils.createUILabel(grid, 2);
        UniverseTime = Utils.createUILabel(grid,3);

        pane.getChildren().addAll(grid);
    }

    private void setFPS(float fps){
        FPS.setText("FPS: "+Math.round(fps));
    }
    private void setEPS(double eps){
        EPS.setText("EPS: "+Math.round(eps));
        USPS.setText("USPS: "+Math.round(eps*dt()));
    }

    private void setUniverseTime(long seconds){
        UniverseTime.setText("Universe Time: "+TimeUnit.SECONDS.toDays(seconds)+" Days");
    }

    private double getTotalEnergy(){
        return bodies.stream().mapToDouble(Body::getEnergy).sum();
    }

    /**
     * Called every tick!
     */
    protected abstract void loop() throws InterruptedException;

    /**
     * @return a list of all bodies in this Universe.
     */
    public abstract List<Body> createBodies();
    public abstract String getName();
    //public abstract double G();
    public abstract double dt();
    public abstract long runningTime();

    public void stop(){
        running = false;
    }


    public double getUniverseTime() {
        return universeTime;
    }

    public List<Body> getBodies() {
        return bodies;
    }

    public Body getOriginBody() {
        return originBody;
    }
}
