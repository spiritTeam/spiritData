package com.spiritdata.dataanal.task.run;

import java.util.Date;
import java.util.TimerTask;

public class LoadTask extends TimerTask {
    private volatile boolean readComplete = true; 

    @Override
    public void run() {
        System.out.println("================"+(new Date()).toString());
        //读取数据库中的信息，并构造TaskGroup;
        if (readComplete) loadData();
    }

    private void loadData() {
        readComplete = false;
        try {
            
        } catch(Exception e) {
            
        } finally {
            readComplete = true;         
        }
    }
}