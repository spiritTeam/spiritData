package com.spiritdata.dataanal.templet.generate;

import java.util.Map;

/**
 * 生成templet的虚类，所有生成templet的内容都需要以此类为基础。<br/>
 * 包括生成templet和其对应的任务tasks
 * @author wh
 */
public abstract class AbstractGenerateTemplet {

    /**
     * 预处理模板，主要是生成模板所需要的数据，并生成
     * @param param
     * @return
     */
    public abstract Map<String, Object> preTreat(Map<String, Object> param);

    public void GenerateTemplet(Map<String, Object> param) {
        //根据参数进行处理，完成：
        //1-生成任务task，用以生成jonsD
        //2-生成templetD的内容
    }

    /**
     * 
     */
    public void process() {
        
    }
}