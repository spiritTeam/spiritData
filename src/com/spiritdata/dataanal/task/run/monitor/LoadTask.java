package com.spiritdata.dataanal.task.run.monitor;

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
import com.spiritdata.dataanal.task.core.model.TaskInfo;
import com.spiritdata.dataanal.task.core.persistence.pojo.TaskGroupPo;
import com.spiritdata.dataanal.task.core.persistence.pojo.TaskInfoPo;
import com.spiritdata.dataanal.task.core.persistence.pojo.TaskRelPo;
import com.spiritdata.dataanal.task.core.service.TaskManageService;
import com.spiritdata.dataanal.task.run.mem.TaskMemory;
import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.SystemCache;

public class LoadTask extends TimerTask {

    @Override
    public void run() {
        try {
            loadData();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        System.out.println("[开始装载可执行任务到TaskMemory]"+(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSSS", Locale.CHINESE)).format(new Date()));
        //这里需要用到Spring的容器
        ServletContext sc = (ServletContext)SystemCache.getCache(FConstants.SERVLET_CONTEXT).getContent();
        TaskManageService tmService = (TaskManageService)WebApplicationContextUtils.getWebApplicationContext(sc).getBean("taskManageService");

        TaskInfo selfTi, preTi;

        //1-构造任务组结构，主表
        List<TaskGroupPo> tgL = tmService.getCanExcuteTaskGroups();
        Map<String, TaskGroup> tempTGm = new HashMap<String, TaskGroup>();
        for (TaskGroupPo tgp: tgL) {
            tempTGm.put(tgp.getId(), new TaskGroup(tgp));
        }

        //2-处理任务信息结构
        List<TaskInfoPo> tiL = tmService.getCanExcuteTaskInfos();
        Map<String, TaskInfo> tempTIm = new HashMap<String, TaskInfo>();
        for (TaskInfoPo tip: tiL) {
            selfTi = new TaskInfo(tip);
            selfTi.setTaskGroup(tempTGm.get(tip.getTaskGId()));
            selfTi.getTaskGroup().addTask2Graph(selfTi);
            tempTIm.put(tip.getId(), new TaskInfo(tip));
        }

        //3-补充任务中的前置任务
        List<TaskRelPo> trL = tmService.getCanExcuteTaskRels();
        if (trL!=null&&trL.size()>0) {
            for (TaskRelPo trp: trL) {
                selfTi = tempTIm.get(trp.getTaskId());
                preTi = tempTIm.get(trp.getPreTaskId());
                if (selfTi!=null&&preTi!=null) {
                    selfTi.addPreTask(preTi, trp.getUsedPreData()==1);
                }
            }
        }

        //4-加入内存结构
        TaskMemory tm = TaskMemory.getInstance();
        for (String tgId: tempTGm.keySet()) {
            tm.addTaskGroup(tempTGm.get(tgId));
        }
    }
}