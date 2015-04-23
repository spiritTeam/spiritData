package com.spiritdata.dataanal.task.run;

/**
 * task运行时配置参数
 * @author wh
 */
public class TaskContextConfig {
    //任务队列的最大长度
    private int MAX_QUEUE_LENGTH = (1<<10)-1; //2^10=1024

    //加载数据的间隔时间
    private int LOAD_INTERVAL = 1*1000*60*5; //默认是5分钟
    //    private int LOAD_INTERVAL = 1*1000*1; //测试为1秒

    public int getMAX_QUEUE_LENGTH() {
        return MAX_QUEUE_LENGTH;
    }

    public void setMAX_QUEUE_LENGTH(int mAX_QUEUE_LENGTH) {
        MAX_QUEUE_LENGTH = mAX_QUEUE_LENGTH;
    }

    public int getLOAD_INTERVAL() {
        return LOAD_INTERVAL;
    }

    public void setLOAD_INTERVAL(int lOAD_INTERVAL) {
        LOAD_INTERVAL = lOAD_INTERVAL;
    }
}