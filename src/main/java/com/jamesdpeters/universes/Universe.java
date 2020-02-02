package com.jamesdpeters.universes;

import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.helpers.MemoryCalculator;
import com.jamesdpeters.helpers.SimulationPerformanceTracker;
import com.jamesdpeters.integrators.Integrator;
import com.jamesdpeters.json.CSVWriter;
import com.jamesdpeters.json.Graph;
import com.jamesdpeters.vectors.EclipseCalculator;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class Universe {

    List<Body> bodies;
    Body originBody;
    private double events = 0;
    private long cpuTime = 0;
    private long lastTime = 0;
    private double universeTime = 0;
    public transient TreeMap<Double, Double> energyShift;

    private static final int WIDTH = 1800;
    private static final int HEIGHT = 900;
    //private AnchorPane pane;
    private boolean running = true;
    //private PerformanceTracker performanceTracker;

    //private Label FPS, EPS, UniverseTime, USPS;
    private double instantEPS;

    Integrator integrator;
    ExecutorService service;
    Universe universe;
    SimulationPerformanceTracker performanceTracker;

    public Universe(){
        energyShift = new TreeMap<>();
    }

    /**
     *  MUST BE CALLED BEFORE SIMULATION STARTS
     */
    public void init(){
        universe = this;
//        pane = new AnchorPane();
//        Group group = new Group();
//        Camera camera = new PerspectiveCamera(false);
//        Scene mainScene = new Scene(pane,WIDTH,HEIGHT,true);

//        //Main simulation scene
//        SubScene scene = new SubScene(group, WIDTH, HEIGHT, true, SceneAntialiasing.BALANCED);
//        scene.setFill(Color.BLACK);
//        scene.setCamera(camera);
//        pane.getChildren().add(scene);

        //setupUI();

//        performanceTracker = PerformanceTracker.getSceneTracker(mainScene);
//
//        camera.setNearClip(0.01);
//        camera.setFarClip(1000000);
//        camera.setTranslateX(-WIDTH/2);
//        camera.setTranslateY(-HEIGHT/2);
//        camera.setTranslateZ(-1000000000);

        bodies = createBodies();
        for(Body body : bodies){
            if(body.isOrigin()) originBody = body;
            //body.addToGroup(group);
            body.setUniverse(this);
            body.setBodies(bodies);
            //body.addLabelToPane(pane);
        }
        MemoryCalculator.calculateEstimatedMemory(this);
        MomentumCorrector.correct(this);
        energyShift.put(0.0,getTotalEnergy());

        this.service = Executors.newFixedThreadPool(bodies.size());
        System.out.println("********************************************");
        performanceTracker = new SimulationPerformanceTracker(this);
        timeline();
    }

    private void timeline(){
        lastTime = System.nanoTime();


        Runnable task = () -> {
            int loops  = 0;
            //long interval = (long) ((double) runningTime()/dt())/50;
            performanceTracker.startTracker();
            while(running){
                long now = System.nanoTime();
                //System.out.println("Current Universe Time: "+ TimeUnit.SECONDS.toMinutes((long) time));
                try {
                    loop();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                events++;
                cpuTime += (now-lastTime);
                lastTime = now;
                universeTime += dt();

                if(cpuTime > TimeUnit.SECONDS.toNanos(1)){
                    double t = events*dt();
                    System.out.println("Universe Time: "+(universeTime)+" Days "+(100*universeTime/runningTime())+"%");
                    //MemoryCalculator.printMemoryUsed();
                    //System.out.println("Universe Total Energy: "+getTotalEnergy()+" (J)");
                    instantEPS = events;
                    events = 0;
                    cpuTime = 0;
                }

                // Every 1000 timesteps
                if(loops >= 1000){
                    loops = 0;
                    energyShift.put(universeTime,getTotalEnergy());
                }
                loops++;

                if(universeTime >= runningTime()){
                    running = false;
                    performanceTracker.finishTracker();
                    System.out.println("------------------------");
                    System.out.println("--Finished Simulation!--");
                    System.out.println("------------------------");
                    MemoryCalculator.printMemoryUsed();
                    //performanceTracker.printStats();

                    //Eclipse
                    EclipseCalculator.findEclipses(this);

//                    Graph.plotTrajectory(universe,1);
                    for(Body body : bodies){
                        Graph.plotBody(body);
//                        try {
//                            CSVWriter.writeBody(body, 1);
//                        } catch (Exception e){
//                            e.printStackTrace();
//                        }
                    }
                    try {
                        CSVWriter.writeEnergyShift(universe);
                    }  catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread backgroundThread = new Thread(task);
        backgroundThread.setDaemon(false);
        backgroundThread.setName("Background Thread");
        backgroundThread.start();
    }

//    private void setupUI(){
//        GridPane grid = new GridPane();
//        grid.setAlignment(Pos.TOP_LEFT);
//
//        FPS = Utils.createUILabel(grid,0);
//        EPS = Utils.createUILabel(grid, 1);
//        USPS = Utils.createUILabel(grid, 2);
//        UniverseTime = Utils.createUILabel(grid,3);
//
//        pane.getChildren().addAll(grid);
//    }

//    private void setFPS(float fps){
//        FPS.setText("FPS: "+Math.round(fps));
//    }
//    private void setEPS(double eps){
//        EPS.setText("EPS: "+Math.round(eps));
//        USPS.setText("USPS: "+Math.round(eps*dt()));
//    }
//
//    private void setUniverseTime(long seconds){
//        UniverseTime.setText("Universe Time: "+TimeUnit.SECONDS.toDays(seconds)+" Days");
//    }

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
    public abstract int resolution(); //Number of steps between each point saved.

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

    public Integrator getIntegrator() {
        return integrator;
    }
}
