package com.gmteam.spiritdata.importdata.excel.proxy;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.gmteam.spiritdata.importdata.excel.ExcelConstants;
import com.gmteam.spiritdata.importdata.excel.pojo.SheetInfo;
import com.gmteam.spiritdata.importdata.excel.util.PoiUtils;

/** 
 * @author mht
 * @version  
 * 类说明  适用于2007之后版本的excel(包含2007)
 */
public class XSSFWorkBookImpl implements IWorkBookProxy {
    private Map<SheetInfo,Object> mdMap;
    /**workbook*/
    private XSSFWorkbook workbook;
    /**文件类型，1代表2007+excel，2代表2007-*/
    private int fileType = ExcelConstants.EXCEL_FILE_TYPE_XSSF;
    public XSSFWorkBookImpl(File execlFile) throws Exception{
        workbook = new XSSFWorkbook(new FileInputStream(execlFile));
    } 
    @Override
    public Object getWorkBook() {
        return workbook;
    }
    @Override
    public  Map<SheetInfo,Object> getMDMap() throws Exception {
        mdMap = PoiUtils.getMdModelMap(workbook,fileType);
        return mdMap;
    }
}
