package com.spiritdata.dataanal.visitmanage.run;

import java.util.List;
import java.util.Map;

import com.spiritdata.dataanal.common.model.Owner;
import com.spiritdata.dataanal.visitmanage.run.mem.VisitMemoryService;

public class VisitLogRunning extends Thread {
    /**
     * 若在另一个JVM中执行任务，这部分功能可以放在main中
     */
    public static void Beginning() {
        VisitLogRunning vlr = new VisitLogRunning();
        vlr.setName("visitServiceMain");
        vlr.start();
    }

    /**
     * 启动任务服务的处理主进程
     */
    @Override
    public void run() {
        try {
            System.out.println("1-初始化访问队列");
            VisitMemoryService vms = VisitMemoryService.getInstance();
            vms.init();
        } catch(Exception e) {
        }
    }

}