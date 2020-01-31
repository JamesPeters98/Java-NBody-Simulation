package com.jamesdpeters.helpers;


//public abstract class DelayTimer extends Task<Integer> {
//
//    long time, delay, prevTime;
//
//    // Hz = rate of timer.
//    public DelayTimer(int Hz){
//        this.time = 0;
//        this.delay = 1000/Hz;
//    }
//
//    @Override
//    protected Integer call() throws InterruptedException {
//        while(true) {
//            torun();
//            Thread.sleep(delay);
//        }
//    }
//
//    public abstract void torun();
//
//    public Thread getThread(){
//        Thread backgroundThread = new Thread(this);
//        backgroundThread.setDaemon(false);
//        backgroundThread.setName("Delay Timer - "+delay);
//        return backgroundThread;
//    }
//}
