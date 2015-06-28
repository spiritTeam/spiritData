package com.spiritdata.dataanal.common.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.spiritdata.dataanal.task.run.TaskContextConfig;
import com.spiritdata.dataanal.task.run.TaskRunning;
import com.spiritdata.dataanal.visitmanage.run.VisitLogRunning;
import com.spiritdata.framework.core.web.InitSysConfigListener;

public class WebRunningListener implements ServletContextListener {
    private Logger logger = Logger.getLogger(InitSysConfigListener.class);

    @Override
    //初始化
    public void contextInitialized(ServletContextEvent arg0) {
        try {
//            //任务框架启动
//            TaskContextConfig tcc = new TaskContextConfig();
//            TaskRunning.Beginning(tcc);
//            //访问日志框架启动
//            VisitLogRunning.Beginning();
        } catch(Exception e) {
            logger.error("初始化任务执行环境异常：",e);
        }
    }

    @Override
    //销毁
    public void contextDestroyed(ServletContextEvent sce) {
    }
}