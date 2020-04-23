package com.jamesdpeters.universes;

import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.helpers.MemoryCalculator;
import com.jamesdpeters.helpers.SimulationPerformanceTracker;
import com.jamesdpeters.integrators.abstracts.Integrator;
import com.jamesdpeters.json.CSVWriter;
import com.jamesdpeters.eclipse.EclipseCalculator;
import com.jamesdpeters.json.Graph;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

public abstract class Universe {

    List<Body> bodies;
    private Body originBody;
    private double events = 0;
    private long cpuTime = 0;
    private long lastTime = 0;
    private int universeStep = 0;
    public transient TreeMap<Double, Double> energyShift;
    private boolean running = true;
    private boolean output = true;
    Integrator integrator;
    private Universe universe;
    private SimulationPerformanceTracker performanceTracker;

    public Universe(){
        energyShift = new TreeMap<>();
    }

    /**
     *  MUST BE CALLED BEFORE SIMULATION STARTS
     */
    public void init(){
        universe = this;
        bodies = createBodies();

        for(Body body : bodies){
            if(body.isOrigin()) originBody = body;
            body.setUniverse(this);
            body.setBodies(bodies);
        }
        //MemoryCalculator.calculateEstimatedMemory(this);
        MomentumCorrector.correct(this);
        energyShift.put(0.0,getTotalEnergy());

        System.out.println("********************************************");
        performanceTracker = new SimulationPerformanceTracker(this);
    }

    public void start(){
        lastTime = System.nanoTime();
        Runnable task = () -> {
            int loops  = 0;
            performanceTracker.startTracker();
            while(running){
                long now = System.nanoTime();
                try {
                    loop();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                events++;
                cpuTime += (now-lastTime);
                lastTime = now;
                universeStep++;


                if(cpuTime > TimeUnit.SECONDS.toNanos(1)){
                    System.out.println("Universe Time: "+(getUniverseTime())+" Days "+(100*getUniverseTime()/runningTime())+"%");
                    events = 0;
                    cpuTime = 0;
                }

                // Every 100 timesteps
                if(loops >= 100){
                    loops = 0;
                    energyShift.put(getUniverseTime(),getTotalEnergy());
                }
                loops++;

                if(getUniverseTime() >= runningTime()){
                    performanceTracker.finishTracker();
                    System.out.println("------------------------");
                    System.out.println("--Finished Simulation!--");
                    System.out.println("------------------------");
                    MemoryCalculator.printMemoryUsed();
                    performanceTracker.printStats();

                    try {
                        onFinish();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    running = false;

                    if(output) {
//                        EclipseCalculator.findEclipses(this);
//                        Graph.plotTrajectory(universe, 1000);

//                        for (Body body : bodies) {
////                            Graph.plotBody(body);
//                            try {
//                                CSVWriter.writeBody(body, 1);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        try {
//                            CSVWriter.writeEnergyShift(universe);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                    }
                }
            }

        };

        Thread backgroundThread = new Thread(task);
        backgroundThread.setDaemon(false);
        backgroundThread.setName("Background Thread");
        backgroundThread.start();
    }

    private double getTotalEnergy(){
        return bodies.stream().mapToDouble(Body::getEnergy).sum();
    }

    /**
     * Called every tick!
     */
    protected abstract void loop() throws InterruptedException;
    protected abstract void onFinish() throws IOException;

    public void stop(){
        running = false;
    }

    public boolean hasFinished(){
        return !running;
    }

    public double getUniverseTime() {
        return universeStep*dt();
    }

    public int getUniverseStep() {
        return universeStep;
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

    public void setIntegrator(Integrator integrator){
        this.integrator = integrator;
    }

    public void setOutput(boolean output){
        this.output = output;
    }


    /**
     *  ABSTRACT METHODS
     **/
    public abstract List<Body> createBodies();
    public abstract String getName();
    public abstract double dt();
    public abstract double runningTime();
    public abstract int resolution(); //Number of steps between each point saved.
}
