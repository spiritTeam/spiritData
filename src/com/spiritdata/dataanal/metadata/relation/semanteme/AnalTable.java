package com.spiritdata.dataanal.metadata.relation.semanteme;

import java.util.Map;

import com.spiritdata.dataanal.metadata.relation.pojo.MetadataModel;

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
     * @param param 扩展参数，若分析需要其他参数，可通过这个参数传入
     * @return Map<String, Float>，此返回值只能是一行，key为语义编号，float是相似度
     * @throws Exception 
     */
    public Map<String, Float> scanOneTable(String tableName, MetadataModel mm, Map<String, Object> param) throws Exception;
}