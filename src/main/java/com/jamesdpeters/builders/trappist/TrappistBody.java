package com.jamesdpeters.builders.trappist;

import com.google.gson.Gson;
import com.jamesdpeters.bodies.Body;
import com.jamesdpeters.builders.BodyBuilder;
import com.jamesdpeters.helpers.Constants;
import com.jamesdpeters.vectors.Vector3D;

public class TrappistBody {

    private String name;
    private double sunMassRatio, earthMassRatio, inclination, orbitalPeriod, semiMajorAxis, startPhase, sunRadiusRatio, earthRadiusRatio;
    private boolean originBody = false;

    public String serialise(Gson gson){
        return gson.toJson(this);
    }

    public TrappistBody fromString(Gson gson, String json){
        return gson.fromJson(json, TrappistBody.class);
    }

    public Body getBody() {
        return getBodyBuilder().create();
    }

    public BodyBuilder getBodyBuilder(){
        System.out.println(getName()+" Radius KM: "+calculateRadius());
        return BodyBuilder.getInstance()
                .setName(name)
                .setMass(calculateMass())
                .setRadius(calculateRadius())
                .setGM(calculateGM())
                .setInitPos(calculateInitialPosition())
                .setInitVelocity(calculateInitialVelocity())
                .setOrigin(originBody);
    }


    private Vector3D calculateInitialVelocity(){
        if(originBody) return Vector3D.ZERO;
        double velocity = Math.PI*2*semiMajorAxis/orbitalPeriod; // M/s
        Vector3D velo = new Vector3D(0,velocity,0); //Start with velocity at x=0 in y direction. z=0
        velo = velo.rotateAroundZ(startPhase).rotateAroundY(Math.toRadians(90-inclination));
        //System.out.println("Initial Velocity "+getName()+": "+velo);
        return velo;
    }

    private Vector3D calculateInitialPosition(){
        if(originBody) return Vector3D.ZERO;
        //Assume always starts at semi-major axis.
        Vector3D pos = new Vector3D(semiMajorAxis,0,0);
        return pos.rotateAroundZ(startPhase).rotateAroundY(Math.toRadians(90-inclination));
    }


    private double calculateGM(){
        if(earthMassRatio != 0) return Constants.EARTH.getEarthGM(earthMassRatio);
        else if(sunMassRatio != 0) return Constants.SUN.getSunGM(sunMassRatio);
        else try {
                throw new Exception("Cannot have zero mass objects! "+getName());
            } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
            return 0;
        }
    }

    private double calculateMass(){
        if(earthMassRatio != 0) return Constants.EARTH.getEarthMass(earthMassRatio);
        else if(sunMassRatio != 0) return Constants.SUN.getSunMass(sunMassRatio);
        else try {
                throw new Exception("Cannot have zero mass objects! "+getName());
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
                return 0;
            }
    }

    private double calculateRadius(){
        if(earthRadiusRatio != 0) return Constants.EARTH.getEarthRadius(earthRadiusRatio);
        else if(sunRadiusRatio != 0) return Constants.SUN.getSunRadius(sunRadiusRatio);
        else return 0;
    }


    /**
     * Getters and Setters
     */


    public String getName() {
        return name;
    }

    public TrappistBody setName(String name) {
        this.name = name;
        return this;
    }


    public double getEarthMassRatio() {
        return earthMassRatio;
    }

    public TrappistBody setEarthMassRatio(double earthMassRatio) {
        this.earthMassRatio = earthMassRatio;
        return this;
    }

    public double getInclination() {
        return inclination;
    }

    public TrappistBody setInclination(double inclination) {
        this.inclination = inclination;
        return this;
    }

    public double getOrbitalPeriod() {
        return orbitalPeriod;
    }

    public TrappistBody setOrbitalPeriod(double orbitalPeriod) {
        this.orbitalPeriod = orbitalPeriod;
        return this;
    }

    public double getSemiMajorAxis() {
        return semiMajorAxis;
    }

    public TrappistBody setSemiMajorAxis(double semiMajorAxis) {
        this.semiMajorAxis = semiMajorAxis;
        return this;
    }

    public double getStartPhase() {
        return startPhase;
    }

    public TrappistBody setStartPhase(double startPhase) {
        this.startPhase = startPhase;
        return this;
    }

    public boolean isOriginBody() {
        return originBody;
    }

    public TrappistBody setOriginBody(boolean originBody) {
        this.originBody = originBody;
        return this;
    }

    public double getSunMassRatio() {
        return sunMassRatio;
    }

    public TrappistBody setSunMassRatio(double sunMassRatio) {
        this.sunMassRatio = sunMassRatio;
        return this;
    }

    public double getSunRadiusRatio() {
        return sunRadiusRatio;
    }

    public TrappistBody setSunRadiusRatio(double sunRadiusRatio) {
        this.sunRadiusRatio = sunRadiusRatio;
        return this;
    }

    public double getEarthRadiusRatio() {
        return earthRadiusRatio;
    }

    public TrappistBody setEarthRadiusRatio(double earthRadiusRatio) {
        this.earthRadiusRatio = earthRadiusRatio;
        return this;
    }
}
