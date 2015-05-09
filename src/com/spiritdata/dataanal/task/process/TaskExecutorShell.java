package com.spiritdata.dataanal.task.process;

import java.sql.Timestamp;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.spiritdata.dataanal.task.TaskUtils;
import com.spiritdata.dataanal.task.core.enumeration.StatusType;
import com.spiritdata.dataanal.task.core.model.TaskInfo;
import com.spiritdata.dataanal.task.core.service.TaskManageService;
import com.spiritdata.dataanal.task.run.mem.TaskMemoryService;
import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.SystemCache;

public class TaskExecutorShell implements Runnable {
    //需要执行的任务信息
    private TaskInfo ti;

    /**
     * 构造函数，此对象的构造必须要有一个任务信息
     * @param ti
     */
    public TaskExecutorShell(TaskInfo ti) {
        super();
        this.ti = ti;
    }

    /**
     * 执行任务信息中所指定的任务
     */
    @Override
    public void run() {
        if (this.ti!=null) {
            //1-设置为执行状态
            this.ti.setProcessing();
            this.ti.setBeginTime(new Timestamp(System.currentTimeMillis()));
            //2-执行
            boolean success=false; //是否执行成功
            boolean notSaveResult2File=false; //是否把结果存储为文件，默认情况下，会把结果存入文件
            Map<String, Object> resultMap = null; //执行的结果
            try {
                //2-classLoader
                TaskProcess tp = TaskUtils.loadClass(this.ti);
                resultMap = tp.process(ti.getParam());
                //根据结果，设置处理参数
                if (resultMap.get("sysResultData")!=null) {
                    Map<String, String> sysResultData = (Map<String, String>)resultMap.get("sysResultData");
                    if (sysResultData.get("resultType").trim().equals("1")) success=true;
                    if (sysResultData.get("notSaveResult2File").trim().equals("1")) notSaveResult2File=true;
                }
            } catch(Exception e) {
                //执行失败不做任何处理
                e.printStackTrace();
            }
            //3-执行结束处理
            this.ti.setEndTime(new Timestamp(System.currentTimeMillis()));
            if (!success) ti.setFailed();
            else {
                ti.setSuccessed();
                if (!notSaveResult2File&&resultMap!=null) {//文件存储为文件
                    Map<String, Object> userResultData = (Map<String, Object>)resultMap.get("userResultData");
                }
            }
            //写入数据库
            //这里需要用到Spring的容器
            ServletContext sc = (ServletContext)SystemCache.getCache(FConstants.SERVLET_CONTEXT).getContent();
            TaskManageService tmService = (TaskManageService)WebApplicationContextUtils.getWebApplicationContext(sc).getBean("taskManageService");
            tmService.completeTaskInfo(ti, (success?StatusType.SUCCESS:StatusType.FAILD));
            //把失败的任务再放入内存继续执行
            if (ti.getStatus()==StatusType.FAILD) (TaskMemoryService.getInstance()).addFaildTaskInfo(ti);
        }
    }
}