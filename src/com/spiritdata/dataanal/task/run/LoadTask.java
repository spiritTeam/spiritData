package com.spiritdata.dataanal.task.run;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimerTask;

import javax.servlet.ServletContext;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.spiritdata.dataanal.task.core.model.TaskGroup;
import com.spiritdata.dataanal.task.core.persistence.pojo.TaskGroupPo;
import com.spiritdata.dataanal.task.core.persistence.pojo.TaskInfoPo;
import com.spiritdata.dataanal.task.core.persistence.pojo.TaskRelPo;
import com.spiritdata.dataanal.task.core.service.TaskManageService;
import com.spiritdata.framework.core.cache.SystemCache;

public class LoadTask extends TimerTask {

    @Override
    public void run() {
        loadData();
    }

    private void loadData() {
        System.out.println("[开始装载可执行任务到TaskMemory]"+(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSSS", Locale.CHINESE)).format(new Date()));
        //这里需要用到Spring的容器
        ServletContext sc = (ServletContext)SystemCache.getCache("SERVLET_CONTEXT").getContent();
        TaskManageService tmService = (TaskManageService)WebApplicationContextUtils.getWebApplicationContext(sc).getBean("taskManageService");

        String temp = "";

        List<TaskGroupPo> tgL = tmService.getCanExcuteTaskGroups();
        List<TaskInfoPo> tiL = tmService.getCanExcuteTaskInfos();
        List<TaskRelPo> trL = tmService.getCanExcuteTaskRels();
        //构造前置任务关系结构：本任务Id->前置任务Id列表
        Map<String, List<String>> preTaskIdRelMap = new HashMap<String, List<String>>();
        if (trL!=null&&trL.size()>0) {
            for (TaskRelPo trp: trL) {
                if (temp!=trp.getTaskId()) {
                    
                } else {
                    
                }
            }
        }
        Map<String, TaskGroup> tempTGm = new HashMap<String, TaskGroup>();
        for (TaskGroupPo tgp: tgL) {
            
        }
    }
}