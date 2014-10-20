package com.gmteam.spiritdata.importdata.excel.proxy;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

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
        Map<SheetInfo,MetadataModel> mdMap = PoiUtils.getMdModelMap(workbook,fileType);
//        List<Map<SheetInfo,MetadataModel>> mdModelMapList = new ArrayList<Map<SheetInfo,MetadataModel>>();
//        int sheetSize = ((XSSFWorkbook) workbook).getNumberOfSheets();
//        XSSFSheet sheet;
//        for(int i=0;i<sheetSize;i++ ){
//            int sheetIndex = i;
//            sheet = ((XSSFWorkbook) workbook).getSheetAt(sheetIndex);
//            int rows = sheet.getLastRowNum()+1;
//            if(rows+1>=2){
//                /**
//                 * 根据条数分析MateData
//                 */
//                MdPmters mdPmters = new MdPmters();
//                mdPmters.setFileType(1);
//                mdPmters.setSheet(sheet);
//                Map<SheetInfo,MetadataModel> mdModelMap =  PoiUtils.getMdModelMap(sheet,sheetIndex,fileType);
//                mdModelMapList.add(mdModelMap);
//            }
//        }
        return mdMap;
    }
}
