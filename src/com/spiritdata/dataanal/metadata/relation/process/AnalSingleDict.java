package com.spiritdata.dataanal.metadata.relation.process;

import java.util.Map;

import com.spiritdata.dataanal.task.process.TaskProcess;

public class AnalSingleDict implements TaskProcess {

    @Override
    public Map<String, Object> process(Map<String, Object> param) {
        System.out.println("正在AnalSignleDict[分析单项字典指标]中执行！！！");
        return null;
    }
}