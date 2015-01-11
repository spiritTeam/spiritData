package com.spiritdata.dataanal.metadata.relation.semanteme;

import java.util.Map;

import com.spiritdata.dataanal.exceptionC.DtalCException;
import com.spiritdata.dataanal.metadata.relation.pojo.MetadataModel;

/**
 * 以元数据为目标的语义分析方法
 * 所有针对元数据分析方法都可继承此接口，
 * 目前想到的有：表主键，表关联
 * @author wh
 */
public interface AnalMetadata {
    /**
     * 扫描元数据，分析语义
     * @param md 元数据
     * @param param 扩展参数，若分析需要其他参数，可通过这个参数传入
     * @return Map<String, Object> 一个Map对象，这样能返回更丰富的信息
     * @throws DtalCException 
     */
    public Map<String, Object> scanMetadata(MetadataModel mm, Map<String, Object> param) throws DtalCException;
}