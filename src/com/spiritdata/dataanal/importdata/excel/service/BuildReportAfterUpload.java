package com.spiritdata.dataanal.importdata.excel.service;

import java.io.Serializable;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Service;

import com.spiritdata.dataanal.report.generate.AbstractGenerateSessionReport;
import com.spiritdata.dataanal.report.model.TaskReport;

/**
 * 在上传一个Excel文件后，生成templet。<br/>
 * 包括生成Templet+生成Task+启动Task。
 * @author wh
 */

@Service
public class BuildReportAfterUpload extends AbstractGenerateSessionReport implements Serializable {
    private static final long serialVersionUID = 5557763867374849717L;

    /**
     * 无参构造函数，用此方式创建对象，必须设置Session
     */
    public BuildReportAfterUpload() {
        super();
    }

    /**
     * 构造实例，并设置Sesion
     * @param session 所设置的对象
     */
    public BuildReportAfterUpload(HttpSession session) {
        super();
        super.setSession(session);
    }

    @Override
    public TaskReport preTreat(Map<String, Object> param) {
        // TODO Auto-generated method stub
        return null;
    }
}