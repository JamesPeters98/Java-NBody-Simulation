package com.jamesdpeters.eclipse;

import java.time.Duration;
import java.time.LocalDateTime;

public class EclipseInfo {
    public EclipseInfo(){

    }
    public LocalDateTime startDate,endDate; //DateTime for start of eclipse.

    public Duration getDuration(){
        return Duration.between(startDate,endDate);
    }
}