package com.spiritdata.dataanal.task.run;

import java.util.Date;
import java.util.Timer;

import com.spiritdata.dataanal.task.run.mem.TaskMemory;
import com.spiritdata.dataanal.task.run.monitor.DispatchTask;
import com.spiritdata.dataanal.task.run.monitor.LoadTask;

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
//守护线程，与主进程同存亡，用户线程，自己要完成
public class TaskRunning extends Thread {
    private TaskContextConfig tcc;

    public TaskRunning(TaskContextConfig tcc) {
        this.tcc = (tcc==null?new TaskContextConfig():tcc);
        System.out.println("任务内存最大任务组数:"+this.tcc.getMEMORY_MAXSIZE_TASKGROUP());
        System.out.println("任务内存最大任务数:"+this.tcc.getMEMORY_MAXSIZE_TASKINFO());
        System.out.println("任务信息装载间隔毫秒数:"+this.tcc.getLOAD_INTERVAL());
        System.out.println("任务分发监控的间隔时间:"+this.tcc.getDISPATCH_INTERVAL());
    }

    //开启任务加载的轮询过程
    public void startLoad() {
        Timer loadTaskTimer = new Timer("loadTaskTimer", true);
        LoadTask lt = new LoadTask();
        loadTaskTimer.schedule(lt, new Date(), tcc.getLOAD_INTERVAL());
    }

    //开启任务分发过程
    public void startDispatch() {
        DispatchTask dt = new DispatchTask();
        dt.setDaemon(true);
        dt.start();
    }
    /*
     * 若在另一个JVM中执行任务，这部分功能可以放在main中
     */
    public static void Beginning(TaskContextConfig tcc) {
        TaskRunning tr = new TaskRunning(tcc);
        tr.setName("taskServiceMain");
        tr.start();
    }

    @Override
    /**
     * 启动任务服务的处理主进程
     */
    public void run() {
        try {
            sleep(20000);//多少毫秒后启动任务处理，先让系统的其他启动任务完成，这里设置死为10秒钟
            //延迟加载，以便让Spring处理好自己的容器
            System.out.println("-------------------------------");
            System.out.println("===============================");
            System.out.println("任务处理服务启动");
            System.out.println("===============================");
            System.out.println("1-任务服务参数加载");
            System.out.println("2-初始化任务内存");
            TaskMemory tm = TaskMemory.getInstance();
            tm.initParam(tcc);
            System.out.println("3-加载任务信息到内存");
            startLoad();
            System.out.println("4-启动任务分发线程");
            startDispatch();
            System.out.println("-------------------------------");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}