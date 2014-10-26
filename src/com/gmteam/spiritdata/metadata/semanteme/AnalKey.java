package com.gmteam.spiritdata.metadata.semanteme;

import java.util.Map;

import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;

/**
 * 主键分析器
 * @author wangxia
 */
public class AnalKey {
    /**
     * 分析主键。
     * 分析方法，查看指标表，看是否有主键可能性，若类型为String或Integer则可能性更大，float可能性%50，Data可能性*10*。
     * 若没有单列主键，则查双列，三列，直到查到为止。
     * 分析结构以json的形式存储在文件中，便于以后查找。
     * @param tableName 表名称
     * @param md 元数据信息
     * @return 是一个Map<String, Float>，其中String是列名，float是主键可能性
     */
    public Map<String, Integer> analKey(String tableName, MetadataModel md) {
        return null;
    }
}