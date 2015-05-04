package com.spiritdata.dataanal.task.run.monitor;

/**
 * 分配任务到线程池
 * @author wh
 */
public class DispatchTask extends Thread {

    public DispatchTask() {
        super();
        this.setName("dispatchTask");
    }

    @Override
    /**
     * 从任务内存读取信息，并
     */
    public void run() {
        while (true) {
            try {
                sleep(1000);
                System.out.println("abc");
                //读取可执行的任务
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}