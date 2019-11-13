package com.jamesdpeters.bodies;

import com.jamesdpeters.Utils;
import com.jamesdpeters.universes.Universe;
import com.sun.javafx.scene.CameraHelper;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.SceneHelper;
import com.sun.javafx.scene.SceneUtils;
import com.sun.prism.paint.Paint;
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
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public abstract class Body extends Sphere implements Callable<Boolean> {

    // 3D UI OBJECTS
    private transient List<Cylinder> paths;
    public transient List<Point3D> points;
    public transient Polyline line;
    private transient Group group;
    private transient static final int trailAmount = 25000; // Number of events to show in trail.
    private transient static PhongMaterial lineMaterial;
    private transient Label bodyLabel;
    private transient Pane pane;
    private transient Camera camera;

    // BODIES PROPERTIES
    private transient Point3D velocity; //Velocity in Metres per second (m/s)
    private transient Point3D position; //Position in Kilometers (Km)

    private transient List<Body> bodies; //List of bodies to interact with.
    private transient Universe universe; //Universe this Body belongs too.

    public Body(){
        super();
        setRadius(getBodyRadius());
        this.paths = new ArrayList<>();
        this.points = new ArrayList<>();
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
        clearPaths();

        bodyLabel = Utils.createUILabel();
        bodyLabel.setText(getName());
    }

    public void setVelocity(Point3D velocity){
        this.velocity = velocity;
    }

    public void drawPosition(){
        camera = SceneHelper.getEffectiveCamera(getScene());
        setTranslateX(position.getX());
        setTranslateY(position.getY());
        setTranslateZ(position.getZ());
        addTrail();

        Point3D pos2D = localToScene(Point3D.ZERO,true);
        bodyLabel.setTranslateX(pos2D.getX());
        bodyLabel.setTranslateY(pos2D.getY());
    }

    public Point3D forceFromBody(Body body, Universe universe){
        Point3D delta = body.getPos().subtract(position);
        double distance = delta.magnitude()*1000;
        double forceMagnitude = (universe.G() * getMass() * body.getMass())/(distance*distance);
        return delta.normalize().multiply(forceMagnitude);
    }

    public void moveWithForce(Point3D force, double dt){
        Point3D acceleration = force.multiply(1/getMass()); //F = ma
        velocity = velocity.add(acceleration.multiply(dt)); //V = V0 + at
        Point3D displacement = velocity.multiply(dt).multiply(0.001); //d = vt (Converts from Meters to KM!)
        position = position.add(displacement);
    }

    public Point3D getPos(){
        return position;
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
        //System.out.println("----------------");
        //System.out.println("UPDATING "+getName());
        Point3D force = new Point3D(0,0,0);
        for(Body body : bodies){
            if(body != this){
                Point3D f = forceFromBody(body,universe);
                //System.out.println("Force from "+body.getName()+" = "+f);
                force = force.add(f);
                //moveWithForceFromBody(body,universe);
            }
        }
        //System.out.println("TOTAL FORCE: "+force.magnitude());
        moveWithForce(force,universe.dt());
    }

    public void setBodies(List<Body> bodies){
        this.bodies = bodies;
    }
    public void setUniverse(Universe universe){
        this.universe = universe;
    }

    /**
     * IMPLEMENTATIONS
     */
    public abstract Point3D getInitialPosition();
    public abstract Point3D getInitialVelocity();
    public abstract String getName();
    public abstract double getMass();
    public abstract double getBodyRadius();

    /**
     * TRAIL CODE - SHOULD PROBABLY MOVE THIS.
     */

    private void addTrail(){
        Point3D point = localToScene(Point3D.ZERO);
        points.add(point);
        if(points.size() > trailAmount){
            points.remove(0);
        }
        updateTrail();

//        Point3D oldPoint = new Point3D(getTranslateX(),getTranslateY(),getTranslateZ());
////        Cylinder line = createConnection(oldPoint,newPoint);
////        addPath(line);
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

        Cylinder line = new Cylinder(getRadius(), height);
        lineMaterial = new PhongMaterial();
        lineMaterial.setDiffuseColor(new Color(1,1,1,0.5));
        lineMaterial.diffuseMapProperty();
        line.setMaterial(lineMaterial);

        line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);

        return line;
    }

    private void addPath(Cylinder cylinder){
//        if(paths.size() > trailAmount){
//            group.getChildren().remove(paths.remove(0));
//        }
        //paths.add(cylinder);
        //group.getChildren().addAll(cylinder);
    }

    private void updateTrail(){
        line.getPoints().clear();

        for(Point3D point3D : points){
            SubScene subScene = NodeHelper.getSubScene(this);
            Point3D point = SceneUtils.subSceneToScene(subScene,point3D);
            Point2D pos2D = CameraHelper.project(camera,point);
            line.getPoints().addAll(pos2D.getX(),pos2D.getY());
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
}
