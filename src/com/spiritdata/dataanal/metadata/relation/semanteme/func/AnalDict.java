package com.spiritdata.dataanal.metadata.relation.semanteme.func;

import java.util.Map;

import com.spiritdata.dataanal.metadata.relation.pojo.MetadataModel;
import com.spiritdata.dataanal.metadata.relation.semanteme.AnalColumn;
import com.spiritdata.dataanal.metadata.relation.semanteme.AnalTable;

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