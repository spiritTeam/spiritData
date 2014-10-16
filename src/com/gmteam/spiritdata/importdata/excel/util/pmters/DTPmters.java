package com.gmteam.spiritdata.importdata.excel.util.pmters;
/** 
 * @author 
 * @version mht  
 * 类说明  包含最多的类型，空的个数，以及比重
 */
public class DTPmters {
    /**type最多的类型*/
    private String dataType;
    /**比重*/
    private int proportion;
    /**空的个数*/
    private int nullNum;
    public String getDataType() {
        return dataType;
    }
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    public int getNullNum() {
        return nullNum;
    }
    public void setNullNum(int nullNum) {
        this.nullNum = nullNum;
    }
    public int getProportion() {
        return proportion;
    }
    public void setProportion(int proportion) {
        this.proportion = proportion;
    }
    
}
