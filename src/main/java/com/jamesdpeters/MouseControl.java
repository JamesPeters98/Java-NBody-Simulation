package com.jamesdpeters;

import com.jamesdpeters.bodies.Body;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.math3.util.MathUtils;

import java.util.List;
import java.util.Optional;

public class MouseControl {

    public MouseControl(Stage stage, Group group, Scene scene, List<Body> bodies, Camera camera){
        initMouseControl(stage,group,scene,bodies,camera);
    }

    private Translate translate;
    private double r, theta, phi;
    private Camera camera;
    private Rotate rotateTheta, rotatePhi;

    private void initMouseControl(Stage stage, Group group, Scene scene, List<Body> bodies, Camera camera) {
        r = 100000000;
        theta = Math.PI;
        phi = 0;
        this.camera = camera;

        rotateTheta = new Rotate(theta,Rotate.X_AXIS);
        rotatePhi = new Rotate(phi,Rotate.Z_AXIS);
        camera.getTransforms().addAll(rotatePhi,rotateTheta);

        stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            double angleRate = Math.PI/360;
            if(event.getCode() == KeyCode.A){
                theta = MathUtils.reduce(theta+angleRate,Math.PI*2,0);
            }
            if(event.getCode() == KeyCode.D){
                theta = MathUtils.reduce(theta-angleRate,Math.PI*2,0);
            }
            if(event.getCode() == KeyCode.W){
                phi = MathUtils.reduce(phi+angleRate, Math.PI*2, 0);
            }
            if(event.getCode() == KeyCode.S){
                phi = MathUtils.reduce(phi-angleRate, Math.PI*2, 0);
            }
            rotateTheta.angleProperty().set(theta);
            rotatePhi.angleProperty().set(phi);

            adjustCamera();

            System.out.println("Theta: "+theta);
            System.out.println("Phi: "+phi);
            System.out.println("Camera: x:"+camera.getTranslateX()+" y:"+camera.getTranslateY()+" z:"+camera.getTranslateZ());
        });
//        Translate pivot = new Translate(0,0,0);
//        Rotate xRotate = new Rotate(0, Rotate.X_AXIS);
//        Rotate yRotate = new Rotate(0, Rotate.Y_AXIS);
//
//        camera.getTransforms().addAll(pivot,xRotate,yRotate);
//        //bodies.forEach(body -> body.points.forEach(point3D -> ));
//
//
//        xRotate.angleProperty().bind(angleX);
//        yRotate.angleProperty().bind(angleY);
//
//        scene.setOnMousePressed(event -> {
//            anchorX = event.getSceneX();
//            anchorY = event.getSceneY();
//            anchorAngleX = angleX.get();
//            anchorAngleY = angleY.get();
//        });
//
//        scene.setOnMouseDragged(event -> {
//            angleX.set(anchorAngleX - (anchorY - event.getSceneY()));
//            angleY.set(anchorAngleY + anchorX - event.getSceneX());
//        });

//        scene.setOnMousePressed(event -> {
//            oldX = event.getScreenX();
//    });
////
////
////
//        pivot = new Translate(0,0,0);
//
//        Optional<Body> body = bodies.stream().filter(body1 -> body1.getName().equals("Sun")).findFirst();
//        if(body.isPresent()){
//            System.out.println("Found sun!");
//            Body b = body.get();
//            pivot = new Translate(b.getTranslateX(),b.getTranslateY(),b.getTranslateZ());
//        }
//
//        Rotate yRotate = new Rotate(0, Rotate.X_AXIS);
//        camera.getTransforms().addAll (
//                pivot,
//                yRotate
//        );

//        stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
//            if(event.getCode() == KeyCode.A){
//                rotation = new Rotate(-1, 0,0,0, Rotate.Z_AXIS);
//            }
//            if(event.getCode() == KeyCode.D){
//                rotation = new Rotate(1, 0,0,0, Rotate.Z_AXIS);
//            }
//            if(event.getCode() == KeyCode.W){
//                rotation = new Rotate(-1, pivot.getX(), pivot.getY(), pivot.getZ(), Rotate.Y_AXIS);
//            }
//            if(event.getCode() == KeyCode.S){
//                rotation = new Rotate(1, pivot.getX(), pivot.getY(), pivot.getZ(), Rotate.Y_AXIS);
//            }
//            camera.getTransforms().add(rotation);
//            System.out.println("Camera: x"+camera.getTranslateX()+" y:"+camera.getTranslateY()+" z:"+camera.getTranslateZ());
//        });
//
//        stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
//            if(event.getCode() == KeyCode.A){
//                rotation = new Rotate(-1, 0,0,0, Rotate.Z_AXIS);
//            }
//            if(event.getCode() == KeyCode.D){
//                rotation = new Rotate(1, 0,0,0, Rotate.Z_AXIS);
//            }
//            if(event.getCode() == KeyCode.W){
//                rotation = new Rotate(-1, pivot.getX(), pivot.getY(), pivot.getZ(), Rotate.Y_AXIS);
//            }
//            if(event.getCode() == KeyCode.S){
//                rotation = new Rotate(1, pivot.getX(), pivot.getY(), pivot.getZ(), Rotate.Y_AXIS);
//            }
//            camera.getTransforms().add(rotation);
//            System.out.println("Camera: x"+camera.getTranslateX()+" y:"+camera.getTranslateY()+" z:"+camera.getTranslateZ());
//        });

//        stage.addEventHandler(MouseEvent.ANY, event -> {
//            int newX = (int) event.getScreenX();
//
//            if ( oldX < newX ) { // if mouse moved to right
//                rotation = new Rotate( 10.0,
//                        // camera rotates around its location
//                        camera.getTranslateX(), camera.getTranslateY(), camera.getTranslateZ(),
//                        Rotate.Z_AXIS );
//
//
//            } else if ( oldX > newX ) { // if mouse moved to left
//                rotation = new Rotate( -10.0,
//                        // camera rotates around its location
//                        camera.getTranslateX(), camera.getTranslateY(), camera.getTranslateZ(),
//                        Rotate.Z_AXIS );
//
//            }
//            camera.getTransforms().addAll(pivot, rotation );
//            oldX = newX;
//        });



        // animate the camera position.
//        Timeline timeline = new Timeline(
//                new KeyFrame(
//                        Duration.seconds(0),
//                        new KeyValue(yRotate.angleProperty(), 0)
//                ),
//                new KeyFrame(
//                        Duration.seconds(15),
//                        new KeyValue(yRotate.angleProperty(), 90)
//                ),
//                new KeyFrame(
//                        Duration.seconds(15),
//                        new KeyValue(yRotate.angleProperty(), 0)
//                )
//        );
//        timeline.setCycleCount(Timeline.INDEFINITE);
//        timeline.play();

        //Attach a scroll listener
        stage.addEventHandler(ScrollEvent.SCROLL, event -> {
            if(event.getDeltaY() != 0) {
                //Get how much scroll was done in Y axis.
                double delta = event.getDeltaY();
                double direction = delta / Math.abs(delta);
                //Add it to the Z-axis location.
                double currentZoom = camera.translateZProperty().get();
                camera.translateZProperty().set(currentZoom + (direction * 0.1 * currentZoom));
            }
        });
    }

    public void adjustCamera(){
        camera.translateXProperty().set(r*Math.sin(theta)*Math.cos(phi));
        camera.translateYProperty().set(r*Math.sin(theta)*Math.sin(phi));
        camera.translateZProperty().set(r*Math.cos(theta));
    }
}
