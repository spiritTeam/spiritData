package com.spiritdata.dataanal.metadata.relation.process;

import java.util.HashMap;
import java.util.Map;

import com.spiritdata.dataanal.SDConstants;
import com.spiritdata.dataanal.task.process.TaskProcess;

public class GetMDInfos implements TaskProcess {

    /**
     * 获得一组元数据信息
     */
    @Override
    public Map<String, Object> process(Map<String, Object> param) {
        Map<String, Object> ret = new HashMap<String, Object>();
        Map<String, Object> sysRd = new HashMap<String, Object>();
        sysRd.put("JsonDCode", SDConstants.JDC_MD_INFO);
        ret.put("sysResultData", sysRd);
        //获得元数据-包括语义信息
        //获得元数据的指标(统计)信息
        return ret;
    }
}