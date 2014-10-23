package com.gmteam.spiritdata.importdata.excel.proxy;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.gmteam.spiritdata.importdata.excel.ExcelConstants;
import com.gmteam.spiritdata.importdata.excel.pojo.SheetInfo;
import com.gmteam.spiritdata.importdata.excel.util.PoiUtils;
import com.gmteam.spiritdata.metadata.relation.pojo.TableMapOrg;

/** 
 * @author mht
 * @version  
 * 类说明  适用于2007之后版本的excel(包含2007)
 */
public class XSSFWorkBookImpl implements IWorkBookProxy {
    private Map<SheetInfo,Object> mdMap;
    private TableMapOrg[] tabMapOrgAry;
    /**workbook*/
    private XSSFWorkbook workbook;
    /**文件类型，1代表2007+excel，2代表2007-*/
    private int fileType = ExcelConstants.EXCEL_FILE_TYPE_XSSF;
    private SheetInfo sheetInfo;
    private Map<Integer,Integer> delColIndexMap;
    public XSSFWorkBookImpl(SheetInfo sheetInfo, Map<Integer, Integer> delColIndexMap, TableMapOrg[] tabMapOrgAry) {
        this.sheetInfo=sheetInfo;
        this.delColIndexMap = delColIndexMap;
        this.tabMapOrgAry = tabMapOrgAry;
    }
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
    @Resource(name="dataSource")
    private  BasicDataSource ds;
    @Override
    public Map<String,Object> getData() {
//        Map<String,Object> saveLogMap = null;
//        MetadataModel md = this.mdMap.get(this.sheetInfo);
//        try {
//            Connection conn = ds.getConnection();
//            PoiUtils.saveInDB(conn,(XSSFSheet)this.sheetInfo.getSheet(),this.delColIndexMap,this.tabMapOrgAry,md);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
        return null;
    }
}
