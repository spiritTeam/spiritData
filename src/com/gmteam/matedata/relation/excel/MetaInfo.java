package com.gmteam.matedata.relation.excel;

import java.util.List;
import org.springframework.stereotype.Component;

/** 
 * @author mht
 * @version  
 * 类说明 ：一个集合了所有信息的类
 * table信息,column信息,logId,
 */
@Component
public class MetaInfo {
    private String tableName;
    private String tableId;
    private List<MetaColumnInfo> mateColumnInfo;
    
    public String getTableName() {
        return tableName;
    }
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    public String getTableId() {
        return tableId;
    }
    public void setTableId(String tableId) {
        this.tableId = tableId;
    }
    public List<MetaColumnInfo> getMateColumnInfo() {
        return mateColumnInfo;
    }
    public void setMateColumnInfo(List<MetaColumnInfo> mateColumnInfo) {
        this.mateColumnInfo = mateColumnInfo;
    }
    
}
