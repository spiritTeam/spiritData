package com.spiritdata.dataanal.metadata.relation.semanteme;

import java.util.Map;

import com.spiritdata.dataanal.exceptionC.DtalCException;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataModel;

/**
 * 以字段为目标的语义分析方法
 * 所有字段语义分析方法都可继承此接口
 * @author wh
 */
public interface AnalColumn {
    /**
     * 扫描表，并分析某一列的语义
     * @param tableName 表名
     * @param md 元数据
     * @param columnName 列名
     * @param param 扩展参数，若分析需要其他参数，可通过这个参数传入
     * @return Map<String, Object> 一个Map对象，这样能返回更丰富的信息
     * @throws DtalCException 
     */
    public Float scanOneField(String tableName, MetadataModel md, String columnName, Map<String, Object> param) throws DtalCException;
}