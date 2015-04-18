package com.spiritdata.jsonD.web.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;

import com.spiritdata.filemanage.category.ANAL.service.AanlResultFileService;
import com.spiritdata.filemanage.core.persistence.pojo.FileIndexPo;
import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.util.FileNameUtils;
import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.jsonD.util.JsonUtils;
import com.spiritdata.jsonD.exceptionC.JsonD1001CException;

/**
 * JsonD的web服务，主要是获取jsonD资源
 * @author wh
 */

public class JsonDService {
    @Resource
    private AanlResultFileService arfService;

    /**
     * 根据jsonD实例的Id，得到jsonD串
     * @param jsonDId jsonD实例的Id
     * @return jsonD串
     */
    public String getJsonDById(String jsonDId) {
        if (StringUtils.isNullOrEmptyOrSpace(jsonDId)) throw new JsonD1001CException("所给jsonDId参数为空，无法获取数据！");
        //先从内存中取
        String ret = "";
        //再从数据库和文件系统中取
        if (StringUtils.isNullOrEmptyOrSpace(ret)) {
            Map<String, Object> m = new HashMap<String, Object>();
            m.put("id", jsonDId);
            List<FileIndexPo> afl = arfService.getAnalFiles(m);
            if (afl==null||afl.size()==0) throw new JsonD1001CException("没有查到id="+jsonDId+"的JsonD数据！");
            FileIndexPo fip = afl.get(0);
            String fileUri = FileNameUtils.concatPath(fip.getPath(), fip.getFileName());
            return this.getJsonDByUri(fileUri);
        }
        //根据id获取内容，现在先不处理原文件名templet1.json
        return this.getJsonDByUri("demo\\templetDemo\\afterImport(IMPFID-c56873f1ff954637be9609ee1bc67a40_RID-63f78f9a12b748e5ae442e8e647baa0f).json");
    }

    /**
     * 根据Uri，得到jsonD串。注意若Uri不带协议头，则指的是相对于服务目录根的相对地址
     * @param uri sond的Uri
     * @return jsonD串
     */
    @SuppressWarnings("unchecked")
	public String getJsonDByUri(String uri) {
        String ret = null;
        if (uri.indexOf("\\\\:")!=-1||uri.indexOf("//:")!=-1) {//走协议方式
            
        } else {//走服务器目录方式
        	//mht 对uri的修改
        	if (uri.indexOf("datafile=/")!=-1) {
        		uri = uri.substring(uri.indexOf("datafile=/")+10, uri.length());
        	}
            if (uri.indexOf(":")==-1) uri = FileNameUtils.concatPath(((CacheEle<String>)SystemCache.getCache(FConstants.APPOSPATH)).getContent(), uri);
            File f = FileUtils.getFile(uri);
            if (f.isFile()) {//读取文件
                try {
                    ret = FileUtils.readFileToString(f, "UTF-8");
                    ret = JsonUtils.getCompactJsonStr(ret);
                } catch(IOException ioe) {
                    throw new JsonD1001CException("读取文件["+uri+"]失败！");
                }
            } else {
                throw new JsonD1001CException("Uri["+uri+"]所指向的地址不可用！");
            }
        }
        return ret;
    }
}