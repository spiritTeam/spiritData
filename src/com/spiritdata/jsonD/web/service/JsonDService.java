package com.spiritdata.jsonD.web.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;

import com.spiritdata.dataanal.task.run.mem.TaskMemoryService;
import com.spiritdata.filemanage.category.ANAL.service.AnalResultFileService;
import com.spiritdata.filemanage.core.persistence.pojo.FileIndexPo;
import com.spiritdata.filemanage.core.service.FileManageService;
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
    private FileManageService fmService;

    /**
     * 根据jsonD实例的Id(就是对应的文件的Id)，得到jsonD串
     * @param jsonDId jsonD实例的Id
     * @return jsonD串
     */
    public Map<String, Object> getJsonDById(String jsonDId) {
        if (StringUtils.isNullOrEmptyOrSpace(jsonDId)) throw new JsonD1001CException("所给jsonDId参数为空，无法获取数据！");
        Map<String, Object> retM = new HashMap<String, Object>();
        //先从内存中取
        //TODO 这段代码使得jsonD不独立了，很别扭
        TaskMemoryService tms = TaskMemoryService.getInstance();
        retM = tms.getTaskStatus(jsonDId);
        String _status = retM.get("status")+"";
        if (_status.equals("3")||_status.equals("-1")) { //执行成功或不在内存，则从文件中读取
            //再从数据库和文件系统中取
            FileIndexPo fip = fmService.getFileIndexPoById(jsonDId);
            if (fip==null) throw new JsonD1001CException("没有找到Id为["+jsonDId+"]的的JsonD数据！");
            String fileUri = FileNameUtils.concatPath(fip.getPath(), fip.getFileName());
            return this.getJsonDByUri(fileUri);
        } else {
            retM.put("jsonType", 2);//非成功或失败的其他状态
        }
        if (retM.size()==0) retM=null;
        return retM;
    }

    /**
     * 根据Uri，得到jsonD串。注意若Uri不带协议头，则指的是相对于服务目录根的相对地址
     * @param uri sond的Uri
     * @return jsonD串
     */
    @SuppressWarnings("unchecked")
	public Map<String, Object> getJsonDByUri(String uri) {
        Map<String, Object> retM = new HashMap<String, Object>();
        String _jsonStr = "";

        if (uri.indexOf("\\\\:")!=-1||uri.indexOf("//:")!=-1) {//走协议方式
            
        } else {//走服务器目录方式
            if (uri.charAt(0)=='\\'||uri.charAt(0)=='/') {
                uri = FileNameUtils.concatPath(((CacheEle<String>)SystemCache.getCache(FConstants.APPOSPATH)).getContent(), uri);
            }
            File f = FileUtils.getFile(uri);
            if (f.isFile()) {//读取文件
                try {
                    _jsonStr = FileUtils.readFileToString(f, "UTF-8");
                    _jsonStr = JsonUtils.getCompactJsonStr(_jsonStr);
                    retM.put("jsonType", 1);
                    retM.put("data",_jsonStr);
                } catch(IOException ioe) {
                    throw new JsonD1001CException("读取文件["+uri+"]失败！");
                }
            } else {
                throw new JsonD1001CException("Uri["+uri+"]所指向的地址不可用！");
            }
        }
        if (retM.size()==0) return null;
        return retM;
    }
}