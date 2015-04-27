package com.spiritdata.dataanal.task.run;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.spiritdata.framework.core.web.InitSysConfigListener;

/**
 * 在web环境下运行的壳shell
 * @author wh
 */
public class WebRunningListener implements ServletContextListener {
    private Logger logger = Logger.getLogger(InitSysConfigListener.class);

    @Override
    //初始化
    public void contextInitialized(ServletContextEvent arg0) {
        try {
            TaskRunning.Beginning(null);
        } catch(Exception e) {
            logger.error("初始化任务执行环境异常：",e);
        }
    }

    @Override
    //销毁
    public void contextDestroyed(ServletContextEvent sce) {
    }
}