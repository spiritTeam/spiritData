package com.gmteam.spiritdata.matedata.pojo;
/** 
 * @author mht
 * @version  
 * 类说明 table与StatisticsInfo关联类
 */
public class TableStatisticsInfoOrg {
    private String id;
    private String tableId;
    private Integer rows;
    private String statisticsInfoId;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTableId() {
        return tableId;
    }
    public void setTableId(String tableId) {
        this.tableId = tableId;
    }
    public Integer getRows() {
        return rows;
    }
    public void setRows(Integer rows) {
        this.rows = rows;
    }
    public String getStatisticsInfoId() {
        return statisticsInfoId;
    }
    public void setStatisticsInfoId(String statisticsInfoId) {
        this.statisticsInfoId = statisticsInfoId;
    }
    
}
