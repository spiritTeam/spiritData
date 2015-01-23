package com.spiritdata.dataanal.report.generate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.spiritdata.dataanal.exceptionC.Dtal1003CException;
import com.spiritdata.dataanal.report.model.TaskReport;
import com.spiritdata.dataanal.report.model.Reprot;
import com.spiritdata.dataanal.report.model.ReportHead;
import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.util.FileNameUtils;


/**
 * 以Session为基础的
 * @author wh
 */
public abstract class AbstractGenerateSessionReport implements GenerateReport {
    private HttpSession session;//session用来缓存与该会话相关的信息
    public HttpSession getSession() {
        return session;
    }
    public void setSession(HttpSession session) {
        this.session = session;
    }

    @Override
    /**
     * 以Session作为缓存机制，构建模板对象，生成task任务，并启动分析任务。<br/>
     * 应包括如下内容：<br/>
     * 1-通过与处理过程获得需要的数据
     * 2-生成模板-报告
     * 3-生成任务
     * 4-进行持久化存储——模板+任务
     * 5-以Session为容器，构建任务执行的上下文
     * 6-启动任务
     * @param param 完成本过程需要的数据，必须包括预处理需要的数据，应放入preTreadParam参数中
     */
    public void buildANDprocess(Map<String, Object> param) {
        if (param==null||param.size()==0) throw new Dtal1003CException(new IllegalArgumentException("构建模板及任务时，必须设置参数！"));
        if (param.get("preTreadParam")==null) throw new Dtal1003CException(new IllegalArgumentException("构建模板及任务时，Map参数中必须设置key='preTreadParam'的元素！"));
        //1-执行预处理，得到模板及任务
        TaskReport tt = preTreat((Map<String, Object>)param.get("preTreadParam"));
        //2-处理模板，并存储文件及数据库
        Reprot report = tt.getReport();

        //文件处理
        //1-设置文件名称
        String root = (String)(SystemCache.getCache(FConstants.APPOSPATH)).getContent();
        String storeFile = FileNameUtils.concatPath(root, "templetFile"+File.separator+"tpl_"+report.getId()+".json");
        ((ReportHead)report.get_HEAD()).setFileName(storeFile.replace("\\", "/"));
        //2-写文件
        FileOutputStream fileOutputStream = null;
        try {
            File file = new File(storeFile);
            if (!file.exists()) {
                File dirs = new File(FileNameUtils.getFilePath(storeFile));
                if (!dirs.exists()) dirs.mkdirs();
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(report.toJson().getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream!=null) {
                try {fileOutputStream.close();}catch(IOException e) {e.printStackTrace();}
            }
        }
        //3-采用文件框架，存储文件信息到数据库持久化？需要些缓存吗？
        
    }
}