package com.spiritdata.dataanal.report.model;

import java.util.ArrayList;
import java.util.List;

import com.spiritdata.framework.core.model.tree.TreeNode;
import com.spiritdata.jsonD.Convert2Json;

/**
 * 报告段结构列表，报告中的_REPORT目前就是这个结构。
 * 此结构是一个森林
 * @author wh
 * @param <E> 符合TreeNode<ReportSegment>的对象
 */
public class SegmentList<E> extends ArrayList<TreeNode<ReportSegment>> implements Convert2Json {
    private static final long serialVersionUID = 1123232352332618215L;

    @Override
    /**
     * 转换为Json格式，注意去掉parent
     */
    public String toJson() {
        if (this.size()==0) return "[]";
        String ret = "[";
        String tempStr;
        for (int i=0; i<this.size(); i++) {
            TreeNode<ReportSegment> oneSeg = this.get(i);
            ret +="\"id\":\""+oneSeg.getId()+"\"";
            ret +=",\"name\":\""+oneSeg.getNodeName()+"\"";
            tempStr = oneSeg.getTnEntity().getTitle();
            if (tempStr!=null&&tempStr.trim().length()>0) ret +=",\"title\":\""+tempStr+"\"";
            tempStr = oneSeg.getTnEntity().getContent();
            if (tempStr!=null&&tempStr.trim().length()>0) ret +=",\"content\":\""+tempStr+"\"";
            //处理子树
            if (oneSeg.getChildren()!=null&&oneSeg.getChildren().size()>0) {
                ret +=",\"subSeg\":"+convertList2Json(oneSeg.getChildren());
            }
        }
        return ret+"]";
    }

    private String convertList2Json(List<TreeNode<ReportSegment>> children) {
        if (children.size()==0) return "[]";
        String ret = "[";
        String tempStr;
        for (int i=0; i<children.size(); i++) {
            TreeNode<ReportSegment> oneSeg = this.get(i);
            ret +="\"id\":\""+oneSeg.getId()+"\"";
            ret +=",\"name\":\""+oneSeg.getNodeName()+"\"";
            tempStr = oneSeg.getTnEntity().getTitle();
            if (tempStr!=null&&tempStr.trim().length()>0) ret +=",\"title\":\""+tempStr+"\"";
            tempStr = oneSeg.getTnEntity().getContent();
            if (tempStr!=null&&tempStr.trim().length()>0) ret +=",\"content\":\""+tempStr+"\"";
            //处理子树
            if (oneSeg.getChildren()!=null&&oneSeg.getChildren().size()>0) {
                ret +=",\"subSeg\":"+convertList2Json(oneSeg.getChildren());
            }
        }
        return ret+"]";
    }
}