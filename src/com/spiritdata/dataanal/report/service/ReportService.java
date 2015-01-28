package com.spiritdata.dataanal.report.service;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.util.FileNameUtils;
import com.spiritdata.jsonD.util.JsonUtils;
import com.spiritdata.dataanal.exceptionC.Dtal1001CException;

/**
 * 报告服务，主要是获得报告信息
 * @author wh
 */
public class ReportService {
    /**
     * 根据报告Id，得到报告的Json串
     * @param templetId 报告Id
     * @return 报告json串
     */
    public String getTempletJsonById(String templetId) {
        if (templetId==null||templetId.length()==0) throw new Dtal1001CException("所给templetId参数为空，无法获取数据！");
        //根据id获取内容，现在先不处理
        return this.getTempletJsonByUri("demo\\templetDemo\\templet1.json");
    }

    /**
     * 根据Uri，得到报告的Json串。注意若Uri不带协议头，则指的是相对于服务目录根的相对地址
     * @param uri 报告的Uri
     * @return 报告json串
     */
    public String getTempletJsonByUri(String uri) {
        String ret = null;
        if (uri.indexOf("\\\\:")!=-1||uri.indexOf("//:")!=-1) {//走协议方式
            
        } else {//走服务器目录方式
            uri = FileNameUtils.concatPath(((CacheEle<String>)SystemCache.getCache(FConstants.APPOSPATH)).getContent(), uri);
            File f = FileUtils.getFile(uri);
            if (f.isFile()) {//读取文件
                try {
                    ret = FileUtils.readFileToString(f, "UTF-8");
                    ret = JsonUtils.getCompactJsonStr(ret);
                } catch(IOException ioe) {
                    throw new Dtal1001CException("读取文件["+uri+"]失败！");
                }
            } else {
                throw new Dtal1001CException("Uri["+uri+"]所指向的地址不可用！");
            }
        }
        return ret;
    }
}