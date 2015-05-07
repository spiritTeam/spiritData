package com.spiritdata.dataanal.task.run.monitor;

/**
 * 分配任务到线程池
 * @author wh
 */
public class DispatchTask extends Thread {
    private int interval = 100; //0.1秒

    public DispatchTask(int interval) {
        super();
        this.interval = interval;
        this.setName("dispatchTask");
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}