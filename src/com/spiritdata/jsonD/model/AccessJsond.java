package com.spiritdata.jsonD.model;

import java.io.Serializable;

import com.spiritdata.jsonD.exceptionC.Jsond0003CException;
import com.spiritdata.jsonD.util.JsonDUtils;

/**
 * 访问某一个JsonD的访问描述对象
 * @author wh
 */
public class AccessJsond implements Serializable {
    private static final long serialVersionUID = -2240704913305758719L;

    private String jsondId; //具体的jsond的id，相当于类的实例的标识，就是jsond文件的id
    private String url; //获取数据的地址
    private String filePath; //数据地址，可能根据一定规则通过此参数计算出url
    private String jsondCode; //jsonDCode，相当于类名称

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public String getJsondId() {
        return jsondId;
    }
    public void setJsondId(String jsondId) {
        this.jsondId = jsondId;
    }
    public String getJsondCode() {
        return jsondCode;
    }
    public void setJsondCode(String jsondCode) {
        if (jsondCode==null||jsondCode.length()==0) throw new IllegalArgumentException("jsonDCode不能为null或空串！");
        if (!JsonDUtils.isLegalCode(jsondCode)) throw new Jsond0003CException("jsonDCode["+jsondCode+"]不合规！", new IllegalArgumentException("jsonDCode不合规，请参看JsonD相关文档！"));
        this.jsondCode = jsondCode;
    }
}