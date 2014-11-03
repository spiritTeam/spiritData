package com.gmteam.spiritdata.metadata.relation.semanteme.func;

import java.util.Map;

import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;
import com.gmteam.spiritdata.metadata.relation.semanteme.AnalColumn;
import com.gmteam.spiritdata.metadata.relation.semanteme.AnalTable;

public class AnalDict implements AnalColumn, AnalTable {

    @Override
    public Map<String, Float> scanOneTable(String tableName, MetadataModel mm, Map<String, Object> param) throws Exception {
        return null;
    }

    @Override
    public Float scanOneField(String tableName, MetadataModel md, String columnName, Map<String, Object> param) throws Exception {
        return null;
    }
}