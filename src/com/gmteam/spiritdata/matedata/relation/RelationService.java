package com.gmteam.spiritdata.matedata.relation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.dbcp.BasicDataSource;

import com.gmteam.spiritdata.importdata.excel.ExcelContentAttributes;
import com.gmteam.spiritdata.importdata.excel.service.pojoservice.DataUploadLogService;
import com.gmteam.spiritdata.importdata.excel.service.pojoservice.LogTableOrgService;
import com.gmteam.spiritdata.importdata.excel.service.pojoservice.TableInfoService;
import com.gmteam.spiritdata.importdata.excel.storepojo.ColumnInfo;
import com.gmteam.spiritdata.importdata.excel.storepojo.LogTableOrg;
import com.gmteam.spiritdata.importdata.excel.storepojo.TableInfo;
import com.gmteam.spiritdata.importdata.excel.util.CommonUtils;

/** 
 * @author mht
 * @version  
 * 类说明 
 * db和excel都会用的service方法，
 * 如新建数据表，
 */
public class RelationService {
    /**
     * 获得dataSource
     */
    @Resource(name="dataSource")
    private BasicDataSource ds;
    @Resource
    private DataUploadLogService  dataUploadLogService;
    CommonUtils cu = new CommonUtils();
    /**
     * 动态创建数据表，
     * @throws Exception 
     */
    public String createDataTable(ExcelContentAttributes contentAttributes) throws Exception {
        List<ColumnInfo> metaColumnInfoList = contentAttributes.getColumnInfo();
        int columnSize = metaColumnInfoList.size();
        String tableName = "MD_TAB_"+cu.getUUID2TableSeq(contentAttributes.getLogId())+contentAttributes.getSheetIndex();
        StringBuffer sbSQl = new StringBuffer("create table "+tableName+"( id varchar2(200) primary key,");
        for(int i=0;i<columnSize;i++){
            String columnName = "column"+metaColumnInfoList.get(i).getColumnIndex();
            String dataType = metaColumnInfoList.get(i).getColumnType();
            if(dataType.equals("String")){
                dataType = "varchar(200)";
            }else if(dataType.equals("Double")){
                dataType = "binary_double";
            }else if(dataType.equals("Date")){
                dataType = "date";
            }
            if(i<columnSize-1){
                sbSQl.append(columnName+" "+dataType+",");
            }else{
                sbSQl.append(columnName+" "+dataType+")");
            }
        }
        Map<String , Object> map=new HashMap<String , Object>();
        map.put("sql",sbSQl.toString()); 
        dataUploadLogService.createDataTableInfo(map);
        //向新建data表中插入数据
        return tableName;
    }
    /**
     * 保存Title到tableInfo
     */
    @Resource
    private TableInfoService tableInfoService;
    public String saveTableInfo(ExcelContentAttributes excelContentAttributes) throws SQLException {
        TableInfo ti = new TableInfo();
        String tableId = cu.getUUID();
        ti.setId(tableId);
        ti.setTableName(excelContentAttributes.getTableName());
        tableInfoService.insertTableInfo(ti);
        return tableId;
    }
    /**
     *日志与数据表关系，可以根据上传日志找到
     *对应的数据表
     */
    @Resource
    private LogTableOrgService logTableOrgService;
    public void saveLogTableOrgInfo(ExcelContentAttributes excelContentAttributes) {
        LogTableOrg lto = new LogTableOrg();
        lto.setId(cu.getUUID());
        lto.setLogId(excelContentAttributes.getLogId());
        lto.setTableId(excelContentAttributes.getTableId());
        logTableOrgService.insertLogTableOrg(lto);
    }
    /**
     * 保存列信息，如列类型，列
     * @param contentAttributes
     */
    public void saveColumnInfo(ExcelContentAttributes contentAttributes) {
        List<ColumnInfo> metaColumnInfoList = contentAttributes.getColumnInfo(); 
        int columnSize = metaColumnInfoList.size();
        Connection saveColumnInfoConn = null;
        PreparedStatement ps = null;
        try {
            String sql = "insert into MD_COLUMN_INFO(id,tableId,titleIndex,titleName,titleType,pk) values(?,?,?,?,?,?)";
            String pkName = contentAttributes.getPkName();
            saveColumnInfoConn = ds.getConnection();
            ps = saveColumnInfoConn.prepareStatement(sql);
            for(int i=0;i<columnSize;i++){
                ps.setObject(1, cu.getUUID());
                ps.setObject(2, contentAttributes.getTableId());
                ps.setObject(3, metaColumnInfoList.get(i).getColumnIndex()); 
                ps.setObject(4, metaColumnInfoList.get(i).getColumnName());
                ps.setObject(5, metaColumnInfoList.get(i).getColumnType());
                if(pkName.equals(metaColumnInfoList.get(i).getColumnName())){
                    ps.setObject(6, "Y");
                }else{
                    ps.setObject(6, "N");
                }
                ps.addBatch();
            }
            ps.executeBatch();
            cu.closeConn(saveColumnInfoConn, ps, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            cu.closeConn(saveColumnInfoConn, ps, null);
        }
    }
}
