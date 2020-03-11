package com.jamesdpeters.bodies;

import com.jamesdpeters.helpers.Constants;
import com.jamesdpeters.helpers.Utils;
import com.jamesdpeters.universes.Universe;
import com.jamesdpeters.vectors.Vector3D;


import java.awt.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Body implements Callable<Boolean>, Comparable<Body> {

    // BODIES PROPERTIES
    private transient Vector3D velocity, nextVelocity, tempVelocity; //Velocity in Metres per second (m/s)
    private transient Vector3D position, nextPos, tempPos; //Position in Kilometers (Km)
    public transient TreeMap<Integer, Vector3D>  positions;   // Store history of positions. Key - time.
    private transient List<Body> bodies;    //List of bodies to interact with.
    private transient List<Body> exclusiveBodies; //List of bodies without this body.
    private transient Universe universe;    //Universe this Body belongs too.
    private transient HashMap<Integer, Vector3D> tempAccelMap, tempVeloMap, tempPosMap;

    private int loop = -1;
    private double GMinAU;
    private Color color = Utils.randomColor();

    public Body(){
        super();
        position = getInitialPosition();
        velocity = getInitialVelocity();
        positions = new TreeMap<>();
        tempAccelMap = new HashMap<>();
        tempVeloMap = new HashMap<>();
        tempPosMap = new HashMap<>();
        GMinAU = getGM()* Constants.CONVERSIONS.GM_to_AU;
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

    public void update(){
        if(loop >= universe.resolution() || loop == -1) {
            positions.put(universe.getUniverseStep(), position);
            loop = 0;
        }
        loop++;
    }

    /**
     * Call this after all bodies have been updated.
     */
    public void postUpdate(){
       velocity = nextVelocity;
       position = nextPos;
    }

    public void setBodies(List<Body> bodies){
        this.bodies = bodies;
        this.exclusiveBodies = new ArrayList<>(bodies);
        exclusiveBodies.remove(this);
    }

    public void setUniverse(Universe universe){
        this.universe = universe;
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
    public abstract TreeMap<Double, Vector3D> getJPLPositions();
    public abstract TreeMap<Double, Vector3D> getJPLVelocities();
    public abstract boolean isOrigin();
    public abstract LocalDateTime getStartDate();

    public Color getColor(){
        return color;
    }

    public Body setColor(Color color) {
        this.color = color;
        return this;
    }

    public double getBodyRadiusAU(){
        return getBodyRadius()/ Constants.KILOMETERS.AU;
    }
    public double getGMAU() { return GMinAU;}

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

    @Override
    public int compareTo(Body o) {
        return getName().compareTo(o.getName());
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

    public List<Body> getExclusiveBodies(){
        return exclusiveBodies;
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

    // Set velocity to be updated after the integrator has made all steps. (after update() method is called)
    public void setNextPosition(Vector3D position){
        this.nextPos = position;
    }
    public void setNextVelocity(Vector3D velo){
        this.nextVelocity = velo;
    }

    public void setTempAccel(int index, Vector3D accel){
        tempAccelMap.put(index,accel);
    }
    public Vector3D getTempAccel(int index){
        return tempAccelMap.get(index);
    }

    public void setTempVelocity(int index, Vector3D velocity){
        tempVeloMap.put(index,velocity);
    }
    public Vector3D getTempVelocity(int index){
        return tempVeloMap.get(index);
    }

    public void setTempPos(int index, Vector3D position){
        tempPosMap.put(index,position);
    }
    public Vector3D getTempPos(int index){
        return tempPosMap.get(index);
    }
}
