package com.spiritdata.dataanal.task.run.monitor;

import com.spiritdata.dataanal.task.core.model.TaskInfo;
import com.spiritdata.dataanal.task.process.TaskExecutorShell;
import com.spiritdata.dataanal.task.run.TaskThreadPool;
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
     * 从任务内存读取信息，并放入线程池
     */
    public void run() {
        while (true) {
            try {
                sleep(this.interval);
                if (tms.getExecuterListSize()>0) {
                    //读取可执行的任务
                    TaskInfo ti = tms.getNextCanProcessTaskInfo();
                    if (ti!=null) {
                        TaskExecutorShell executor = new TaskExecutorShell(tms.getNextCanProcessTaskInfo());
                        TaskThreadPool.executeTask(executor);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}