package com.spiritdata.dataanal.task.run.monitor;

import com.spiritdata.dataanal.task.core.model.TaskInfo;
import com.spiritdata.dataanal.task.run.mem.TaskMemoryService;

/**
 * 分配任务到线程池
 * @author wh
 */
public class DispatchTask extends Thread {
    private int interval = 100; //0.1秒
    private TaskMemoryService tms = null;

    public DispatchTask(int interval) {
        super();
        this.interval = interval;
        this.setName("dispatchTask");
        tms = TaskMemoryService.getInstance();
    }

    @Override
    /**
     * 从任务内存读取信息，并
     */
    public void run() {
        while (true) {
            try {
                sleep(this.interval);
                System.out.println("abc");
                //读取可执行的任务
                TaskInfo ti = tms.getNextCanProcessTaskInfo();
                
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}