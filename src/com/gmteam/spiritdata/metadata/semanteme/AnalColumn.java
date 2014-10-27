package com.gmteam.spiritdata.metadata.semanteme;

import java.util.Map;

import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;

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
     * @return Map<Integer, Float>，此返回值只能是一行，key为列语义编号，float是相似度
     */
    public Map<Integer, Float> scanOneField(String tableName, MetadataModel md, String columnName);
}