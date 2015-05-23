package com.spiritdata.dataanal.task.run.monitor;

import java.util.TimerTask;
import com.spiritdata.dataanal.task.run.mem.TaskMemoryService;

/**
 * 装载任务信息到内存的线程
 * @author wh
 */
public class LoadAndCleanTask extends TimerTask {
    @Override
    public void run() {
        try {
            TaskMemoryService tms = TaskMemoryService.getInstance();
            tms.loadData();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}