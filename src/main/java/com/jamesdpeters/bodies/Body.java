package com.jamesdpeters.bodies;

import com.jamesdpeters.universes.Universe;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

public class Body extends Sphere implements Callable<Object> {

    private String name;
    private List<Cylinder> paths;
    private Group group;
    private static final int trailAmount = 1000; // Number of events to show in trail.
    private static PhongMaterial lineMaterial;

    private double mass; //Mass in Kilograms (Kg)
    private Point3D velocity; //Velocity in Metres per second (m/s)
    private volatile Point3D position; //Position in Kilometers (Km)

    private List<Body> bodies; //List of bodies to interact with.
    private Universe universe; //Universe this Body belongs too.

    public Body(String name, double radius, double mass, double x, double y, double z){
        super(radius);
        this.name = name;
        this.paths = new ArrayList<>();
        this.mass = mass;
        this.velocity = new Point3D(0,0,0); //Initial velocity

        lineMaterial = new PhongMaterial();
        lineMaterial.setDiffuseColor(new Color(1,1,1,0.5));
        lineMaterial.diffuseMapProperty();

        position = new Point3D(x,y,z);
        clearPaths();
    }

    public void setVelocity(Point3D velocity){
        this.velocity = velocity;
    }

    public void drawPosition(){
        addTrail(position);
        setTranslateX(position.getX());
        setTranslateY(position.getY());
        setTranslateZ(position.getZ());
    }

    public Point3D forceFromBody(Body body, Universe universe){
        Point3D delta = body.getPos().subtract(position);
        double distance = delta.magnitude()*1000;
        double forceMagnitude = (universe.G() * mass * body.mass)/(distance*distance);
        return delta.normalize().multiply(forceMagnitude);
    }

    public void moveWithForce(Point3D force, double dt){
        Point3D acceleration = force.multiply(1/mass); //F = ma
        velocity = velocity.add(acceleration.multiply(dt)); //V = V0 + at
        Point3D displacement = velocity.multiply(dt).multiply(0.001); //d = vt (Converts from Meters to KM!)
        position = position.add(displacement);
        //System.out.println(getName()+" new pos: "+position+" km");
    }

    public void moveWithForceFromBody(Body body, Universe universe){
        Point3D force = forceFromBody(body,universe);
        moveWithForce(force,universe.dt());
    }

    public Point3D getPos(){
        return position;
    }


    public void addToGroup(Group group){
        this.group = group;
        group.getChildren().addAll(this);
    }

    /**
     * Updates this body using all the surrounding bodies!
     */
    public void update(){
        for(Body body : bodies){
            if(body != this){
                moveWithForceFromBody(body,universe);
            }
        }
    }

    public void setBodies(List<Body> bodies){
        this.bodies = bodies;
    }

    public void setUniverse(Universe universe){
        this.universe = universe;
    }


    /**
     * TRAIL CODE - SHOULD PROBABLY MOVE THIS.
     */

    //Call before moving the object! These coordinates should be the new coordinates.
    private void addTrail(Point3D newPoint){
        Point3D oldPoint = new Point3D(getTranslateX(),getTranslateY(),getTranslateZ());
        Cylinder line = createConnection(oldPoint,newPoint);
        addPath(line);
    }

    public Cylinder createConnection(Point3D origin, Point3D target) {
        Point3D yAxis = new Point3D(0, 1, 0);
        Point3D diff = target.subtract(origin);
        double height = diff.magnitude();

        Point3D mid = target.midpoint(origin);
        Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());

        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

        Cylinder line = new Cylinder(getRadius()/6, height);
        lineMaterial = new PhongMaterial();
        lineMaterial.setDiffuseColor(new Color(1,1,1,0.5));
        lineMaterial.diffuseMapProperty();
        line.setMaterial(lineMaterial);

        line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);

        return line;
    }

    private void addPath(Cylinder cylinder){
        if(paths.size() > trailAmount){
            group.getChildren().remove(paths.remove(0));
        }
        paths.add(cylinder);
        group.getChildren().addAll(cylinder);
    }

    private void clearPaths(){
        if(group != null) {
            group.getChildren().removeAll(paths);
            paths.clear();
        }
    }

    @Override
    public String toString() {
        return "["+name+"] - ("+getTranslateX()+","+getTranslateY()+","+getTranslateZ()+")";
    }

    public String getName() {
        return name;
    }

    @Override
    public Object call() {
        update();
        return true;
    }
}
