package com.jamesdpeters.eclipse;

import java.time.Duration;
import java.time.LocalDateTime;

public class EclipseInfo {

    private Type eclipseType = Type.NA;
    private LocalDateTime midpoint;

    public EclipseInfo(){

    }
    public LocalDateTime startDate,endDate; //DateTime for start of eclipse.

    public Duration getDuration(){
        return Duration.between(startDate,endDate);
    }

    public LocalDateTime midpoint(){
        if(midpoint == null) return startDate.plus(getDuration().dividedBy(2));
        return midpoint;
    }

    public void setEclipseType(Type type){
        eclipseType = type;
    }

    public Type getEclipseType() {
        return eclipseType;
    }

    public void setMidpoint(LocalDateTime midpoint){
        this.midpoint = midpoint;
    }

    public enum Type{
        SOLAR,
        LUNAR,
        NA;
    }
}