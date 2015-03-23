package com.spiritdata.dataanal.task.service;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.spiritdata.dataanal.task.model.TaskGroup;
import com.spiritdata.dataanal.task.persistence.pojo.TaskGroupPo;
import com.spiritdata.dataanal.task.persistence.pojo.TaskInfoPo;
import com.spiritdata.dataanal.task.persistence.pojo.TaskRelPo;
import com.spiritdata.framework.core.dao.mybatis.MybatisDAO;

/**
 * 任务管理服务类，包括持久化部分和应用缓存部分，但不包括任务执行及调度部分。
 * @author wh
 */
public class TaskManageService {
    @Resource(name="defaultDAO")
    private MybatisDAO<TaskGroupPo> taskGroupDao; //任务组
    @Resource(name="defaultDAO")
    private MybatisDAO<TaskInfoPo> taskInfoDao; //任务信息，单个的任务
    @Resource(name="defaultDAO")
    private MybatisDAO<TaskRelPo> taskRelDao; //任务关系，标明前置任务

    @PostConstruct
    public void initParam() {
        taskGroupDao.setNamespace("taskGroup");
        taskInfoDao.setNamespace("taskInfo");
        taskRelDao.setNamespace("taskRel");
    }

    /**
     * 构造缓存中的任务数据。<br/>
     * 这个任务结构是以所有者Id为key的Map，每一个所有者的任务信息为一个List<TaskGroupPo>列表
     * @return 
     */
    public Map<String, List<TaskGroup>> makeCacheTasks() {
        return null;
    }

    /**
     * 调整所有者Id，为登录成功后，切换所有者所准备的方法
     * @param oldOwnerId 旧所有者Id，目前是SessionId
     * @param newOwnerId 新所有者Id，目前是用户的Id
     * @return 调整成功，返回true，否则，返回false
     */
    public boolean changeOwnerId(String oldOwnerId, String newOwnerId) {
        //调整缓存中的数据
        //调整数据库中的数据
        return false;
    }

    /**
     * 保存任务组信息
     * @param tg 任务组信息
     * @return 若保存成功，返回true，否则返回false
     */
    public boolean save(TaskGroup tg) {
        //保存到内存
        String ownerId = (tg.getOwnerId()).trim();
        if (ownerId==null||ownerId.length()==0) { //抛出异常
            
        }
        //保存到数据库
        return false;
    }
}