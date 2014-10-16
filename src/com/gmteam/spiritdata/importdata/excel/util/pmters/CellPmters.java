package com.gmteam.spiritdata.importdata.excel.util.pmters;
/** 
 * @author mht
 * @version  
 * 类说明 存放一个单元格的信息
 * 把excel描绘成一个坐标轴，
 * 左上为原点，x为对应行对应顺序，
 * y为列对应顺序
 */
public class CellPmters {
    /**横坐标*/
    private int x;
    /**纵坐标*/
    private int y;
    /**类型*/
    private String dataType;
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    public String getDataType() {
        return dataType;
    }
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
    
}
