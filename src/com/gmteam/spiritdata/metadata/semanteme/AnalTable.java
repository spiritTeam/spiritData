package com.gmteam.spiritdata.metadata.semanteme;

import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;

/**
 * 以表为目标的语义分析方法
 * 所有表语义分析方法都可继承此接口，
 * 目前想到的有：表主键，表关联
 * @author wh
 */
public interface AnalTable {
    /**
     * 扫描表，分析表语义
     * @param tableName 表名
     * @param md 元数据
     * @return Map<Integer, Float>，此返回值只能是一行，key为语义编号，float是相似度
     */
    public Object scanOneTable(String tableName, MetadataModel md);
}