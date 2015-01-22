package com.spiritdata.dataanal.importdata.excel.service;

import java.io.Serializable;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.spiritdata.dataanal.templet.generate.AbstractGenerateSessionTemplet;

/**
 * 在上传一个Excel文件后，生成templet。<br/>
 * 包括生成Templet+生成Task+启动Task。
 * @author wh
 */

@Service
public class BuildTempletAfterUpload extends AbstractGenerateSessionTemplet implements Serializable {
    private static final long serialVersionUID = 5557763867374849717L;


    @Override
    public Map<String, Object> preTreat(Map<String, Object> param) {
        // TODO Auto-generated method stub
        return null;
    }
}