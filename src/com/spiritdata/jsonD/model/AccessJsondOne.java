package com.spiritdata.jsonD.model;

import com.spiritdata.framework.core.model.BaseObject;
import com.spiritdata.jsonD.exceptionC.Jsond0003CException;
import com.spiritdata.jsonD.util.JsonDUtils;

/**
 * 访问某一个JsonD的访问描述对象
 * @author wh
 */
public class AccessJsondOne extends BaseObject {
    private static final long serialVersionUID = -2240704913305758719L;

    private String id; //数据Id
    private String url; //获取数据的地址
    private String filePath; //数据地址，可能根据一定规则通过此参数计算出url
    private String jsonDId; //也可以指定jsonD数据的id
    private String jsonDCode; //jsonDCode
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
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
        if (jsonDCode==null||jsonDCode.length()==0) throw new IllegalArgumentException("jsonDCode不能为null或空串！");
        if (!JsonDUtils.isLegalCode(jsonDCode)) throw new Jsond0003CException("jsonDCode["+jsonDCode+"]不合规！", new IllegalArgumentException("jsonDCode不合规，请参看JsonD相关文档！"));
        this.jsonDCode = jsonDCode;
    }
}