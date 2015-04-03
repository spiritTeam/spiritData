package com.spiritdata.jsonD.model;

import java.io.Serializable;

import com.spiritdata.framework.util.StringUtils;
import com.spiritdata.jsonD.exceptionC.JsonD0003CException;
import com.spiritdata.jsonD.util.JsonDUtils;

/**
 * 访问某一个JsonD的访问描述对象
 * @author wh
 */
public class AccessJsonD implements Serializable {
    private static final long serialVersionUID = -2240704913305758719L;

    private String jsonDId; //具体的jsonD的id，相当于类的实例的标识，就是jsonD文件的id
    private String url; //获取数据的地址
    private String filePath; //数据地址，可能根据一定规则通过此参数计算出url
    private String jsonDCode; //jsonDCode，相当于类名称

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
    public String getJsonDId() {
        return jsonDId;
    }
    public void setJsonDId(String jsonDId) {
        this.jsonDId = jsonDId;
    }
    public String getJsonDCode() {
        return jsonDCode;
    }
    public void setJsonDCode(String jsonDCode) {
        if (StringUtils.isNullOrEmptyOrSpace(jsonDCode)) throw new IllegalArgumentException("jsonDCode不能为null或空串！");
        if (!JsonDUtils.isLegalCode(jsonDCode)) throw new JsonD0003CException("jsonDCode["+jsonDCode+"]不合规！", new IllegalArgumentException("jsonDCode不合规，请参看JsonD相关文档！"));
        this.jsonDCode = jsonDCode;
    }
}