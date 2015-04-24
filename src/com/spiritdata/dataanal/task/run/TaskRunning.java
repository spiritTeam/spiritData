package com.spiritdata.dataanal.task.run;

import java.util.Date;
import java.util.Timer;

/**
 * <p>任务执行的入口点，今后可以把它作为main函数处理，在新的jvm中启动，以提高效率。
 * <pre>
 * 当下此入口点功能为：
 * 1-启动对TaskQueue的监控线程——定时轮询从数据库中读取任务信息并更新TaskQueue对象
 * 2-启动执行Task的线程池
 * 3-启动从TaskQueue中读取任务并放入线程池执行的线程
 * </pre>
 * @author wh
 */
public class TaskRunning {
    private TaskContextConfig tcc;

    private Timer loadTaskTimer = new Timer("loadTaskTimer", true);

    public TaskRunning(TaskContextConfig tcc) {
        this.tcc = (tcc==null?new TaskContextConfig():tcc);
        System.out.println("加载最大任务数:"+this.tcc.getMAX_QUEUE_LENGTH());
        System.out.println("间隔毫秒数:"+this.tcc.getLOAD_INTERVAL());
    }

    //开启任务加载的轮询过程
    public void startLoad() {
        LoadTask tl = new LoadTask();
        loadTaskTimer.schedule(tl, new Date(), tcc.getLOAD_INTERVAL());
    }

    /*
     * 若在另一个JVM中执行任务，这部分功能可以放在main中
     */
    public static void Beginning(TaskContextConfig tcc) {
        //延迟加载，以便让Spring处理好自己的容器
        System.out.println("===============================");
        System.out.println("任务处理过程启动");
        System.out.println("===============================");

        TaskRunning tr = new TaskRunning(tcc);
        tr.startLoad();
    }
}