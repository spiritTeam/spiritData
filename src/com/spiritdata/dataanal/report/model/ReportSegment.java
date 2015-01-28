package com.spiritdata.dataanal.report.model;

import com.spiritdata.framework.core.model.tree.TreeNodeBean;

/**
 * 报告中段落对象，采用树框架实现
 * @author wh
 */
public class ReportSegment extends TreeNodeBean {
    private static final long serialVersionUID = -4825036040320277208L;

    //id 树对象
    //name=nodeName 树对象
    private String title;
    private String content;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
}