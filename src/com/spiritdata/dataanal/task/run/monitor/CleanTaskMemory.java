package com.spiritdata.dataanal.task.run.monitor;

import java.util.TimerTask;

import com.spiritdata.dataanal.task.run.mem.TaskMemoryService;

public class CleanTaskMemory extends TimerTask {
    @Override
    public void run() {
        try {
            TaskMemoryService tms = TaskMemoryService.getInstance();
            tms.cleanTaskGroup();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}