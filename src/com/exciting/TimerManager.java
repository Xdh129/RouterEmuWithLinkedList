package com.exciting;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

class TimerManager{
    static class Task extends TimerTask {
        @Override
        public void run() {
        }
    }
    private List<Timer> TimerList=new ArrayList<>();
    Timer createTimer(){
        Timer timer=new Timer();
        TimerList.add(timer);
        return timer;
    }

    void stopAllTimer(){
        for(Timer timer:TimerList){
            timer.cancel();
        }
    }

}