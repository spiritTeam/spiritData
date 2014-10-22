package com.gmteam.spiritdata.importdata.excel.proxy;

import java.io.File;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;

import com.gmteam.spiritdata.importdata.excel.ExcelConstants;
import com.gmteam.spiritdata.importdata.excel.pojo.SheetInfo;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;
import com.gmteam.spiritdata.metadata.relation.pojo.TableMapOrg;
/** 
 * @author mht
 * @version  
 * 类说明 代理类，根据fileType类型，
 * 得到相应的workbook，
 */
public class WorkBookProxy implements IPoiUtils{
    private Object excelWorkBook;
    private IPoiUtils iPoiUtils;
    private Map<SheetInfo,MetadataModel> mdMap;
    public WorkBookProxy(File execlFile,int fileType) throws Exception {
        if (fileType==ExcelConstants.EXCEL_FILE_TYPE_XSSF)
            this.iPoiUtils = new XSSFWorkBookImpl(execlFile);
        else if (fileType==ExcelConstants.EXCEL_FILE_TYPE_HSSF)
            this.iPoiUtils = new HSSFWorkBookImpl(execlFile);
        else throw new Exception("不是excel文件");
    }
    public WorkBookProxy(SheetInfo sheetInfo,Map<Integer,Integer> delColIndexMap, TableMapOrg[] tabMapOrg) throws Exception {
        this.iPoiUtils = new XSSFWorkBookImpl(sheetInfo,delColIndexMap,tabMapOrg);
    }
    public WorkBookProxy(HSSFSheet hSheet,Map<Integer,Integer> delColIndexMap,TableMapOrg[] tabMapOrg) throws Exception {
        this.iPoiUtils = new HSSFWorkBookImpl(hSheet,delColIndexMap);
    }
    @Override
    public Object getWorkBook() throws Exception {
        excelWorkBook = iPoiUtils.getWorkBook();
        return excelWorkBook;
    }
    @Override
    public Map<SheetInfo,MetadataModel> getMDMap() throws Exception {
        this.mdMap = iPoiUtils.getMDMap();
        return this.mdMap;
    }
    @Override
    public Object getData() {
        return iPoiUtils.getData();
    }
}
