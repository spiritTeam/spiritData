package com.gmteam.spiritdata.importdata.excel.util.pmters;
/** 
 * @author 
 * @version mht  
 * 类说明  包含最多的类型，空的个数，以及比重
 */
public class DTParam {
    //错误类型的
    private int errorNum;
    //type最多的类型
    private String dataType;
    //比重
    private double proportion;
    //空的个数
    private int nullNum;
    
    public int getErrorNum() {
        return errorNum;
    }
    public void setErrorNum(int errorNum) {
        this.errorNum = errorNum;
    }
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
    public double getProportion() {
        return proportion;
    }
    public void setProportion(double proportion) {
        this.proportion = proportion;
    }
}
