package com.spiritdata.dataanal.expreport.word.model.report;

/**
 * report _REPORT
 * @author mht
 */

public class _REPORT_REPORT {
    private String id;
    private String name;
    private String title;
    private _REPORT_SUBSEG [] subSeg;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public _REPORT_SUBSEG[] getSubSeg() {
        return subSeg;
    }
    public void setSubSeg(_REPORT_SUBSEG[] subSeg) {
        this.subSeg = subSeg;
    }
    
}
