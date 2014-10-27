package com.gmteam.spiritdata.metadata.semanteme;

import java.util.Map;

import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;

/**
 * 分析字段语义的方法
 * 所有字段语义分析方法都可继承此接口
 * @author wh
 */
public interface AnalSemanteme {
    /**
     * 根据表名，元数据信息，列名称分析元数据
     * @param tableName 表名
     * @param md 元数据
     * @param columnName 列名
     * @return Map<Integer, Float>，此返回值只能是一行，key为语义编号，float是相似度
     */
    public Map<Integer, Float> scanOneField(String tableName, MetadataModel md, String columnName);
}