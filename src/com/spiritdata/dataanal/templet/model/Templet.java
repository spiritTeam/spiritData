package com.spiritdata.dataanal.templet.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.spiritdata.dataanal.exceptionC.Dtal1002CException;
import com.spiritdata.framework.core.model.tree.TreeNode;
import com.spiritdata.jsonD.ConvertJson;
import com.spiritdata.jsonD.util.JsonUtils;

/**
 * 模板类，此模板类只包含模板本身的信息，不包括处理模板相关的信息
 * <br/>包括：_HEAD,_DATA,_TEMPET
 * @author wh
 */
public class Templet implements Serializable, ConvertJson {
    private static final long serialVersionUID = 518670183146944686L;

    private Object _HEAD;//头信息，可以是String templetHead 对象
    private List<OneJsond> dataList;//jsond数据访问列表
    private Object _TEMPLET;//模板主题信息，可以是String templetHead 对象

    public Object get_HEAD() {
        return _HEAD;
    }
    public void set_HEAD(Object _HEAD) {
        this._HEAD = _HEAD;
    }
    public Object get_TEMPLET() {
        return _TEMPLET;
    }
    public void set_TEMPLET(Object _TEMPLET) {
        this._TEMPLET = _TEMPLET;
    }

    /**
     * 向templet中加入一个jsond的访问信息
     * @param one
     */
    public void addOneJsond(OneJsond one) {
        if (dataList==null) dataList=new ArrayList<OneJsond>();
        one.setTdid(dataList.size());
        dataList.add(one);
    }

    /**
     * 根据jsondId获取访问信息在本模板中的id
     * @param jsondId
     * @return
     */
    public int getDid(String jsondId) {
        int ret = -1;
        if (dataList==null||dataList.size()==0) {
            for (int i=0; i<dataList.size(); i++) {
                OneJsond oj = dataList.get(i);
                if (oj.equals(jsondId)) return oj.getTdid();
            }
        }
        return ret;
    }

    /**
     * 把模板对象转换为json字符串
     * @return json字符串
     */
    public String toJson() {
        if (_HEAD==null) throw new Dtal1002CException("templetD不规范：头信息(_HEAD)必须设置！");
        if (_TEMPLET==null) throw new Dtal1002CException("templetD不规范：模板主题信息(_TEMPLET)必须设置！");

        String jsonS = "{";
        //转换头
        if (_HEAD instanceof TempletHead) {
            jsonS += ((TempletHead)_HEAD).toJson();
        } else {
            jsonS += "\"_HEAD\":"+JsonUtils.objToJson(_HEAD);
        }
        //转换dataList;模板可以没有任何dataList
        if (dataList!=null&&dataList.size()>0) {
            jsonS += ",\"_DLIST\":[";
            for (int i=0; i<dataList.size(); i++) {
                if (i!=0) jsonS += ",";
                jsonS += dataList.get(i).toJson();
            }
            jsonS += "],";
        }
        //转换体templet
        if (_TEMPLET instanceof String) {
            jsonS += "\"_TEMPLET\":"+_TEMPLET;
        } else if (_TEMPLET instanceof SegmentList) {
            jsonS += "\"_TEMPLET\":"+((SegmentList<TreeNode<TempletSegment>>)_TEMPLET).toJson();
        } else {
            jsonS += "\"_TEMPLET\":"+JsonUtils.objToJson(_TEMPLET);
        }
        return jsonS+"}";
    }
}