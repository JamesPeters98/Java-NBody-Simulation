package com.jamesdpeters.eclipse;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class CompareInfo{

    private LocalDateTime simulated, NASA;

    public CompareInfo(LocalDateTime simulated, LocalDateTime NASA){
        this.NASA = NASA;
        this.simulated = simulated;
    }

    public LocalDateTime getNASA() {
        return NASA;
    }

    public LocalDateTime getSimulated() {
        return simulated;
    }

    public double getDeltaSeconds(){
        return NASA.until(simulated, ChronoUnit.SECONDS);
    }

    public boolean hasSimulatedValue(){
        return simulated != null;
    }
}
