package com.spiritdata.dataanal.task.run;

/**
 * task运行时配置参数
 * @author wh
 */
//目前不支持线程数的调整，即线程初始大小和最大大小设置没有意义，目前的线程池都是按照最大值进行初始化的
public class TaskContextConfig {
    //==主存储限制
    //任务内存对象中最大任务组个数
    private int MEMORY_MAXSIZE_TASKGROUP = (1<<8)-1; //2^10=255
    //任务内存对象中最大任务个数
    private int MEMORY_MAXSIZE_TASKINFO = (1<<10)-1; //2^13=1023

    //==时间间隔设置
    //加载数据的间隔时间
    //private int LOAD_INTERVAL = 1*1000*10; //默认是10秒
    //private int LOAD_INTERVAL = 1*1000*1; //测试为1秒
    private int LOADCLEAN_INTERVAL = 1*1000*10; //10秒，便于调试

    //任务分发到线程池过程的间隔时间
    private int DISPATCH_INTERVAL = 1*1000*1; //默认为1秒
    //已处理的对象多长时间后才能被删除(这种方法是权宜办法，在极端情况下还是会造成任务的多次执行)
    private int CLEANDEALEDOBJ_AFTERTIME = 1*1000*10; //默认为1秒 删除了，没用了

    //每次清除任务组或任务信息的个数
    private int MEMORY_CLEANSIZE_TASK = (1<<4)-1; //2^4-1=15
    //任务处理线程的初始化大小
    private int PROCESS_INITSIZE = 10; //默认为10个线程
    //任务处理线程的最大数
    private int PROCESS_MAXSIZE = 10; //默认为30个线程
    //一个任务最多执行次数，超过这个次数若执行仍然失败，则认为任务失效
    private int EXECUTECOUNT_LIMIT = 3; //默认为3次

    public int getMEMORY_MAXSIZE_TASKGROUP() {
        return MEMORY_MAXSIZE_TASKGROUP;
    }
    public void setMEMORY_MAXSIZE_TASKGROUP(int MEMORY_MAXSIZE_TASKGROUP) {
        this.MEMORY_MAXSIZE_TASKGROUP = MEMORY_MAXSIZE_TASKGROUP;
    }

    public int getMEMORY_MAXSIZE_TASKINFO() {
        return MEMORY_MAXSIZE_TASKINFO;
    }
    public void setMEMORY_MAXSIZE_TASKINFO(int MEMORY_MAXSIZE_TASKINFO) {
        this.MEMORY_MAXSIZE_TASKINFO = MEMORY_MAXSIZE_TASKINFO;
    }

    public int getLOADCLEAN_INTERVAL() {
        return LOADCLEAN_INTERVAL;
    }
    public void setLOADCLEAN_INTERVAL(int LOADCLEAN_INTERVAL) {
        this.LOADCLEAN_INTERVAL = LOADCLEAN_INTERVAL;
    }

    public int getDISPATCH_INTERVAL() {
        return DISPATCH_INTERVAL;
    }
    public void setDISPATCH_INTERVAL(int DISPATCH_INTERVAL) {
        this.DISPATCH_INTERVAL = DISPATCH_INTERVAL;
    }

    public int getCLEANDEALEDOBJ_AFTERTIME() {
        return this.CLEANDEALEDOBJ_AFTERTIME;
    }
    public void setCLEANDEALEDOBJ_INTERVAL(int CLEANDEALEDOBJ_AFTERTIME) {
        this.CLEANDEALEDOBJ_AFTERTIME = CLEANDEALEDOBJ_AFTERTIME;
    }
//    public int getCLEAN_INTERVAL() {
//        return CLEAN_INTERVAL;
//    }
//    public void setCLEAN_INTERVAL(int CLEAN_INTERVAL) {
//        this.CLEAN_INTERVAL = CLEAN_INTERVAL;
//    }

    public int getMEMORY_CLEANSIZE_TASK() {
        return MEMORY_CLEANSIZE_TASK;
    }
    public void setMEMORY_CLEANSIZE_TASK(int MEMORY_CLEANSIZE_TASK) {
        this.MEMORY_CLEANSIZE_TASK = MEMORY_CLEANSIZE_TASK;
    }

    public int getPROCESS_INITSIZE() {
        return PROCESS_INITSIZE;
    }
    public void setPROCESS_INITSIZE(int PROCESS_INITSIZE) {
        this.PROCESS_INITSIZE = PROCESS_INITSIZE;
    }

    public int getPROCESS_MAXSIZE() {
        return PROCESS_MAXSIZE;
    }
    public void setPROCESS_MAXSIZE(int PROCESS_MAXSIZE) {
        this.PROCESS_MAXSIZE = PROCESS_MAXSIZE;
    }

    public int getEXECUTECOUNT_LIMIT() {
        return EXECUTECOUNT_LIMIT;
    }
    public void setEXECUTECOUNT_LIMIT(int EXECUTECOUNT_LIMIT) {
        this.EXECUTECOUNT_LIMIT = EXECUTECOUNT_LIMIT;
    }
}