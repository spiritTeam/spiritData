package com.gmteam.matedata.excel;
/** 
 * @author 
 * @version  
 * 类说明 
 */
public class MetaColumnInfo {
    private Integer titleIndex;
    private String titleName;
    private String titleType;
    private String pk;
    public String getPk() {
        return pk;
    }
    public void setPk(String pk) {
        this.pk = pk;
    }
    public Integer getTitleIndex() {
        return titleIndex;
    }
    public void setTitleIndex(Integer titleIndex) {
        this.titleIndex = titleIndex;
    }
    public String getTitleName() {
        return titleName;
    }
    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }
    public String getTitleType() {
        return titleType;
    }
    public void setTitleType(String titleType) {
        this.titleType = titleType;
    }
    
}
