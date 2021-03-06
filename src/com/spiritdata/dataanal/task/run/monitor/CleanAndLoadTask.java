package com.spiritdata.dataanal.task.run.monitor;

import java.util.TimerTask;
import com.spiritdata.dataanal.task.run.mem.TaskMemoryService;

/**
 * 装载任务信息到内存的线程
 * @author wh
 */
public class CleanAndLoadTask extends TimerTask {
    @Override
    public void run() {
        try {
            TaskMemoryService tms = TaskMemoryService.getInstance();
            tms.cleanANDloadData();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}