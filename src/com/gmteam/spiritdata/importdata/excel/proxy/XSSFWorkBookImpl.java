package com.gmteam.spiritdata.importdata.excel.proxy;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.gmteam.spiritdata.importdata.excel.util.PoiUtils;
import com.gmteam.spiritdata.importdata.excel.util.SheetInfo;
import com.gmteam.spiritdata.importdata.excel.util.pmters.MdPmters;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataColumn;
import com.gmteam.spiritdata.metadata.relation.pojo.MetadataModel;

/** 
 * @author mht
 * @version  
 * 类说明  适用于2007之后版本的excel(包含2007)
 */
public class XSSFWorkBookImpl implements IPoiUtils {
    /**workbook*/
    private XSSFWorkbook workbook; 
    public XSSFWorkBookImpl() {  
    }
    public XSSFWorkBookImpl(File execlFile) throws Exception{
        workbook = new XSSFWorkbook(new FileInputStream(execlFile));
    } 
    @Override
    public Object getWorkBook() {
        return workbook;
    }
    @Override
    public Object getMDList() throws Exception {
        int sheetSize = workbook.getNumberOfSheets();
        XSSFSheet sheet;
        for(int i=0;i<sheetSize;i++ ){
            int sheetIndex = i;
            sheet = workbook.getSheetAt(i);
            int rows = sheet.getLastRowNum()+1;
            /**
             * 保存sheetInfo
             */
            SheetInfo sheetInfo = new SheetInfo();
            sheetInfo.setSheetIndex(sheetIndex);
            sheetInfo.setSheetName(sheet.getSheetName());
            /**
             * 根据条数分析MateData
             */
            MdPmters mdPmters = new MdPmters();
            mdPmters.setFileType(1);
            mdPmters.setRows(rows);
            mdPmters.setSheet(sheet);
            mdPmters.setSheetInfo(sheetInfo);
            Map<SheetInfo,MetadataModel> mdModelMap =  PoiUtils.getMdModelMap(mdPmters);
        }
        return null;
    }

}
