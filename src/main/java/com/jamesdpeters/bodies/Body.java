package com.jamesdpeters.bodies;

import com.jamesdpeters.helpers.CONSTANTS;
import com.jamesdpeters.integrators.Integrator;
import com.jamesdpeters.integrators.IntegratorFactory;
import com.jamesdpeters.universes.Universe;
import com.jamesdpeters.vectors.Vector3D;


import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Body implements Callable<Boolean> {

    // BODIES PROPERTIES
    private transient Vector3D velocity, nextVelocity; //Velocity in Metres per second (m/s)
    private transient Vector3D position, nextPos; //Position in Kilometers (Km)
    public transient TreeMap<Double, Vector3D>  positions;   // Store history of positions. Key - time.
    private transient List<Body> bodies;    //List of bodies to interact with.
    private transient Universe universe;    //Universe this Body belongs too.

    // TRAIL PROPERTIES
    //private transient double trails; // Time since last point added to trail.
    //private transient int trailLimit = 500; // Number of events to show in trail.
    private transient double trailInterval = 5000; // interval in events between trail points.
    private int loop = -1;

    // INTEGRATOR
    public Integrator integrator;

    private Thread thread;
    private Body body;
    private final AtomicBoolean setToRun = new AtomicBoolean(false);

    public Body(){
        super();
        body = this;
        position = getInitialPosition();
        velocity = getInitialVelocity();
        positions = new TreeMap<>();
//        thread = new Thread(updateTask());
//        thread.start();
    }

    public double getEnergy(){
        return kinteticEnergy()+potentialEnergy();
    }

    private double kinteticEnergy(){
        return 0.5*getMass()*getVelocity().magnitude();
    }

    private double potentialEnergy(){
        return bodies.stream().filter(body -> body != this).mapToDouble(body -> -getGM() * (body.getMass() / distTo(body).magnitude())).sum();
    }

    public Vector3D distTo(Body body){
        return body.position.subtract(position);
    }

    public double calculateTimePeriod(){
        return 2*Math.PI/Math.sqrt(getGM()/(Math.pow(getBodyRadius(),3)));
    }

    /**
     * Updates this body using all the surrounding bodies!
     */
//    public void updateThreaded(){
//        setToRun.set(true);
//        synchronized (setToRun) {
//            setToRun.notify();
//        }
//    }

    public void update(){
        step();
    }

//    public boolean isRunning(){
//        return setToRun.get();
//    }


    private void step(){
        integrator.step(body);
        if(loop >= universe.resolution() || loop == -1) {
            positions.put(universe.getUniverseTime(), position);
            loop = 0;
        }
        loop++;
        //setToRun.set(false);
    }

//    private Runnable updateTask(){
//        return () -> {
//            while(!thread.isInterrupted()) {
//                if (!saved) {
//                    synchronized (setToRun) {
//                        if (setToRun.get()) {
//                            step();
//                        } else {
//                            try {
//                                //System.out.println("WAITING FOR THREAD");
//                                setToRun.wait();
//                                //System.out.println("WAITED!");
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                } else {
//                    break;
//                }
//            }
//        };
//    }

    /**
     * Call this after all bodies have been updated.
     */
    public void postUpdate(){
       velocity = nextVelocity;
       position = nextPos;
    }

    public void setBodies(List<Body> bodies){
        this.bodies = bodies;
    }
    public void setUniverse(Universe universe){
        this.universe = universe;
        trailInterval = (trailInterval/universe.dt());
        integrator = IntegratorFactory.getDefaultIntegrator();
    }


    /**
     * IMPLEMENTATIONS
     */
    public abstract Vector3D getInitialPosition();
    public abstract Vector3D getInitialVelocity();
    public abstract String getName();
    public abstract double getMass();
    public abstract double getGM();
    public abstract double getBodyRadius();
    public abstract HashMap<Long, Vector3D> getJPLPositions();
    public abstract HashMap<Long, Vector3D> getJPLVelocities();
    public abstract boolean isOrigin();

    public double getBodyRadiusAU(){
        return getBodyRadius()/CONSTANTS.KILOMETERS.AU;
    }

    @Override
    public String toString() {
        return "["+getName()+"] " +
                "\n Pos:"+getInitialPosition()+
                "\n Velocity:"+velocity;
    }

    @Override
    public Boolean call() {
        update();
        return true;
    }


    public Vector3D getVelocity() {
        return velocity;
    }

    public Vector3D getPosition() {
        return position;
    }

    public Universe getUniverse() {
        return universe;
    }

    public List<Body> getBodies() {
        return bodies;
    }

    public Body setPosition(Vector3D position) {
        this.position = position;
        return this;
    }

    public void setVelocity(Vector3D velocity){
        this.velocity = velocity;
    }

    public void addPosition(Vector3D pos){
        position.add(pos);
    }

    public void addVelocity(Vector3D velocity){
        this.velocity.add(velocity);
    }

    // Set velocity to be updated after the integrator has made all steps.
    public void setNextPosition(Vector3D position){
        this.nextPos = position;
    }

    public void setNextVelocity(Vector3D velo){
        this.nextVelocity = velo;
    }

}
