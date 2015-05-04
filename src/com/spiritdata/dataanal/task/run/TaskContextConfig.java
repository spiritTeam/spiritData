package com.spiritdata.dataanal.task.run;

/**
 * task运行时配置参数
 * @author wh
 */
public class TaskContextConfig {
    //任务内存对象中最大任务组个数
    private int MEMORY_MAXSIZE_TASKGROUP = (1<<10)-1; //2^10=1023

    //任务内存对象中最大任务个数
    private int MEMORY_MAXSIZE_TASKINFO = (1<<13)-1; //2^13=8095

    //任务分发到线程池过程的间隔时间
    private int DISPATCH_INTERVAL = 1*1000*1; //默认为1秒

    //加载数据的间隔时间
    //private int LOAD_INTERVAL = 1*1000*60*5; //默认是5分钟
    private int LOAD_INTERVAL = 1*1000*1; //测试为1秒

    public int getMEMORY_MAXSIZE_TASKGROUP() {
        return MEMORY_MAXSIZE_TASKGROUP;
    }

    public void setMEMORY_MAXSIZE_TASKGROUP(int mEMORY_MAXSIZE_TASKGROUP) {
        MEMORY_MAXSIZE_TASKGROUP = mEMORY_MAXSIZE_TASKGROUP;
    }

    public int getMEMORY_MAXSIZE_TASKINFO() {
        return MEMORY_MAXSIZE_TASKINFO;
    }

    public void setMEMORY_MAXSIZE_TASKINFO(int mEMORY_MAXSIZE_TASKINFO) {
        MEMORY_MAXSIZE_TASKINFO = mEMORY_MAXSIZE_TASKINFO;
    }

    public int getLOAD_INTERVAL() {
        return LOAD_INTERVAL;
    }

    public void setLOAD_INTERVAL(int lOAD_INTERVAL) {
        LOAD_INTERVAL = lOAD_INTERVAL;
    }

    public int getDISPATCH_INTERVAL() {
        return DISPATCH_INTERVAL;
    }

    public void setDISPATCH_INTERVAL(int dISPATCH_INTERVAL) {
        DISPATCH_INTERVAL = dISPATCH_INTERVAL;
    }
}