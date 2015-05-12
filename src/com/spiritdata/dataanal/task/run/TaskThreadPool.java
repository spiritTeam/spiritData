package com.spiritdata.dataanal.task.run;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.spiritdata.dataanal.task.process.TaskExecutorShell;

/**
 * 任务执行的线程池，采用java自带的线程池
 * @author wh
 */
public class TaskThreadPool {
    private static ExecutorService pool;

    /**
     * 向线程池加入可执行任务，并执行
     * @param ti 任务信息
     */
    public static void executeTask(TaskExecutorShell tes) {
        TaskThreadPool.pool.execute(tes);
    }

    /**
     * 初始化线程池
     * @param initSize 初始化的线程数
     * @param maxSize 线程池最大线程数
     */
    public static void init(int initSize, int maxSize) {
        TaskThreadPool.pool = Executors.newFixedThreadPool(maxSize);
        //加一个关闭jvm时可调用的方法，关闭此线程池
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    System.out.println("JVM退出时关闭线程池！");
                    TaskThreadPool.shutdown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 关闭线程池
     */
    public static void shutdown() {
        TaskThreadPool.pool.shutdown();
    }
}