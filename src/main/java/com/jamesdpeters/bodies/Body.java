package com.jamesdpeters.bodies;

import com.jamesdpeters.builders.JPLInfo;
import com.jamesdpeters.helpers.DelayTimer;
import com.jamesdpeters.helpers.LimitedCopyOnWriteArrayList;
import com.jamesdpeters.helpers.Utils;
import com.jamesdpeters.integrators.Integrator;
import com.jamesdpeters.integrators.IntegratorFactory;
import com.jamesdpeters.json.CSVWriter;
import com.jamesdpeters.json.Graph;
import com.jamesdpeters.universes.Universe;
import com.sun.javafx.scene.CameraHelper;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.SceneHelper;
import com.sun.javafx.scene.SceneUtils;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Sphere;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public abstract class Body extends Sphere implements Callable<Boolean> {

    // 3D UI OBJECTS
    private transient List<Cylinder> paths;
    public transient List<Point3D> points;
    public transient Polyline line;
    private transient Group group;
    private transient static PhongMaterial lineMaterial;
    private transient Label bodyLabel;
    private transient Pane pane;
    private transient Camera camera;


    // BODIES PROPERTIES
    private transient Point3D velocity; //Velocity in Metres per second (m/s)
    private transient Point3D position; //Position in Kilometers (Km)

    public transient LinkedHashMap<Double,Point3D>  positions;   // Store history of positions. Key - time.

    private transient List<Body> bodies;    //List of bodies to interact with.
    private transient Universe universe;    //Universe this Body belongs too.

    // TRAIL PROPERTIES
    private transient double trails; // Time since last point added to trail.
    private transient int trailLimit = 500; // Number of events to show in trail.
    private transient double trailInterval = 5000; // interval in events between trail points.
    private transient boolean saved = false;


    // INTEGRATOR
    public Integrator integrator;

    public Body(){
        super();
        setRadius(getBodyRadius());
        this.paths = new ArrayList<>();
        this.points = new LimitedCopyOnWriteArrayList<>(trailLimit);
        this.line = new Polyline();

        lineMaterial = new PhongMaterial();
        lineMaterial.setDiffuseColor(new Color(1,1,1,0.5));
        lineMaterial.diffuseMapProperty();
        line.setStroke(Color.WHITE);
        line.setStrokeWidth(1);
        //line.setFill(Color.WHITE);
        //line.minWidth(100000);

        position = getInitialPosition();
        velocity = getInitialVelocity();

        positions = new LinkedHashMap<>();
        clearPaths();

        bodyLabel = Utils.createUILabel();
        bodyLabel.setText(getName());

        //Timer to poll body position and add point to trail!
        DelayTimer delayTimer = new DelayTimer(500) {
            @Override
            public void torun() {
                addTrail();
            }
        };
        delayTimer.getThread().start();
    }


    public void drawPosition(){
        camera = SceneHelper.getEffectiveCamera(getScene());
        setTranslateX(position.getX());
        setTranslateY(position.getY());
        setTranslateZ(position.getZ());

        updateTrail();

        try {
            Point3D pos2D = localToScene(Point3D.ZERO, true);
            bodyLabel.setTranslateX(pos2D.getX());
            bodyLabel.setTranslateY(pos2D.getY());
        } catch (InternalError e){
            // Internal error? JavaFX 12 bug likely...
        }
    }

    public void addToGroup(Group group){
        this.group = group;
        group.getChildren().addAll(this);
    }

    public void addLabelToPane(Pane pane){
        this.pane = pane;
        pane.getChildren().addAll(bodyLabel);
        pane.getChildren().addAll(line);
    }

    /**
     * Updates this body using all the surrounding bodies!
     */
    public void update(){
        if(!saved){
            integrator.step(this);
            positions.put(universe.getUniverseTime(),position);
//                if(universe.getUniverseTime() > TimeUnit.DAYS.toSeconds(1000)){
//                    try {
//                        CSVWriter.writeBody(this, 1000);
//                        Graph.plotTrajectory(this);
//                        saved = true;
//                        positions = new LinkedHashMap<>();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
            }
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
    public abstract Point3D getInitialPosition();
    public abstract Point3D getInitialVelocity();
    public abstract String getName();
    public abstract double getMass();
    public abstract double getBodyRadius();
    public abstract HashMap<Long,Point3D> getJPLPositions();
    public abstract HashMap<Long,Point3D> getJPLVelocities();
    public abstract boolean isOrigin();

    /**
     * TRAIL CODE - SHOULD PROBABLY MOVE THIS.
     */

    private void addTrail(){
            Point3D point = localToScene(Point3D.ZERO);
            points.add(point);
    }

    private void updateTrail(){
        line.getPoints().clear();
        SubScene subScene = NodeHelper.getSubScene(this);

        for(Point3D point3D : points){
                Point3D point = SceneUtils.subSceneToScene(subScene, point3D);
                Point2D pos2D = CameraHelper.project(camera, point);
                line.getPoints().addAll(pos2D.getX(), pos2D.getY());
            }
    }

    private void clearPaths(){
        if(group != null) {
            group.getChildren().removeAll(paths);
            paths.clear();
        }
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


    public Point3D getVelocity() {
        return velocity;
    }

    public Point3D getPosition() {
        return position;
    }

    public Universe getUniverse() {
        return universe;
    }

    public List<Body> getBodies() {
        return bodies;
    }

    public Body setPosition(Point3D position) {
        this.position = position;
        return this;
    }

    public void setVelocity(Point3D velocity){
        this.velocity = velocity;
    }

    public void addPosition(Point3D pos){
        position.add(pos);
    }

    public void addVelocity(Point3D velocity){
        this.velocity.add(velocity);
    }

}
