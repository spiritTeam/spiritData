package com.spiritdata.jsonD.web.service;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.util.FileNameUtils;
import com.spiritdata.jsonD.util.JsonUtils;
import com.spiritdata.jsonD.exceptionC.Jsond1001CException;

/**
 * JsonD的web服务，主要是获取Jsond资源
 * @author wh
 */

@Service
public class JsondService {
    /**
     * 根据Jsond实例的Id，得到Jsond串
     * @param jsondId Jsond实例的Id
     * @return Jsond串
     */
    public String getJsondById(String jsondId) {
        if (jsondId==null||jsondId.length()==0) throw new Jsond1001CException("所给jsondId参数为空，无法获取数据！");
        //先从内存中取
        String ret = "";
        //再从数据库和文件系统中取
        if (ret==null||ret.trim().length()==0) {
            
        }
        //根据id获取内容，现在先不处理
        return this.getJsondByUri("demo\\templetDemo\\templet1.json");
    }

    /**
     * 根据Uri，得到Jsond串。注意若Uri不带协议头，则指的是相对于服务目录根的相对地址
     * @param uri sond的Uri
     * @return Jsond串
     */
    public String getJsondByUri(String uri) {
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
                    throw new Jsond1001CException("读取文件["+uri+"]失败！");
                }
            } else {
                throw new Jsond1001CException("Uri["+uri+"]所指向的地址不可用！");
            }
        }
        return ret;
    }
}