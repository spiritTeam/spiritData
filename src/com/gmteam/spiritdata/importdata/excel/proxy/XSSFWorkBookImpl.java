package com.gmteam.spiritdata.importdata.excel.proxy;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.gmteam.spiritdata.importdata.excel.ExcelConstants;
import com.gmteam.spiritdata.importdata.excel.util.PoiUtils;
import com.gmteam.spiritdata.importdata.excel.util.SheetInfo;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;

/** 
 * @author mht
 * @version  
 * 类说明  适用于2007之后版本的excel(包含2007)
 */
public class XSSFWorkBookImpl implements IPoiUtils {
    /**workbook*/
    private XSSFWorkbook workbook;
    /**文件类型，1代表2007+excel，2代表2007-*/
    private int fileType = ExcelConstants.EXCEL_FILE_TYPE_XSSF;
    private XSSFSheet xSheet;
    private Map<Integer,Integer> delColIndexMap;
    public XSSFWorkBookImpl() {  
    }
    public XSSFWorkBookImpl(File execlFile) throws Exception{
        workbook = new XSSFWorkbook(new FileInputStream(execlFile));
    } 
    public XSSFWorkBookImpl(XSSFSheet xSheet,Map<Integer,Integer> delColIndexMap) {
        this.xSheet=xSheet;
        this.delColIndexMap = delColIndexMap;
    }
    @Override
    public Object getWorkBook() {
        return workbook;
    }
    @Override
    public Object getMDList() throws Exception {
        Map<SheetInfo,MetadataModel> mdMap = PoiUtils.getMdModelMap(workbook,fileType);
        return mdMap;
    }
    @Override
    public Object getData() {
        Object object = PoiUtils.getSheetData(this.xSheet,this.delColIndexMap);
        return null;
    }
}
