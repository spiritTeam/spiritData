package com.spiritdata.dataanal.expreport.word.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;

import com.spiritdata.dataanal.UGA.pojo.User;
import com.spiritdata.dataanal.report.model.Report;
import com.spiritdata.dataanal.report.service.ReportService;
import com.spiritdata.jsonD.model.JsonD;
import com.spiritdata.jsonD.util.JsonUtils;
import com.spiritdata.jsonD.web.service.JsonDService;

/**
 * 导出word report服务
 * @author mht
 */
@Service
public class WordService {
    /**
     * 用于获取report
     */
    @Resource
    private ReportService reportSerivce;
    /**
     * 用于获取jsond
     */

    @Resource
    private JsonDService jsonDSerive;

    /**
     * report
     */
    private Report report;

    /**
     * jsonDList
     */
    private List<JsonD> jsonDList = new ArrayList<JsonD>();

    /**
     * 入口方法：
     * @param userInfo 用户信息
     * @param reportId 报告id，
     * @throws Exception 
     */
    public Map<String,Object> expWord(String reportId, User userInfo,List<String> jsonDIdList) throws Exception{
        
        //1、获得report
        initReportAndJsonD(reportId);
        //2、bulidWord
        Map<String,Object> retMap = bulidWord();
        return retMap;
    }

    @Resource
    private WordService wordService;
    /**
     * 得到jsond和report
     * @param reportId
     * @param jsonDIdList
     * @throws Exception 
     */
	@SuppressWarnings("unchecked")
	private void initReportAndJsonD(String reportId) throws Exception {
        //report
        String reportJson = reportSerivce.getReportJsonById(reportId);
        Map<String,Object> reportMap = (Map<String, Object>)JsonUtils.jsonToObj(reportJson, Map.class);
        map2been(reportMap);
        //jsonD
        if(this.report!=null){
        }
    }

	/**
	 * 
	 * @param reportMap
	 * @param class1
	 */
    private void map2been(Map<String, Object> reportMap) {
		Iterator<String> it = reportMap.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			Object value = reportMap.get(key);
			if (value instanceof String) {
				System.out.println("key="+key+":val="+value);
			} else {
				System.out.println("key="+key);
			}
		}
	}

	/**
     * 创建word
     * @return 
     * @throws IOException 
     */
    private Map<String, Object> bulidWord() throws IOException {
//    	//新建一个文档 
//	    XWPFDocument docx = new XWPFDocument();
//	    
//	    //1、标题部分====
//	    XWPFParagraph titlePara = docx.createParagraph();
//	    //一个XWPFRun代表具有相同属性的一个区域。
//	    XWPFRun titleRun = titlePara.createRun();
//	    String title = this.report.get_HEAD().getReportName();
//	    titleRun.setBold(true); //加粗
//	    titleRun.setText(title);
//	    titleRun.setFontSize(22);
//	    
//	    //2、正文部分====
//	    // report array
//	    _REPORT_REPORT [] _reportAry = this.report.get_REPORT();
//	    for (_REPORT_REPORT _report :_reportAry) {
//	    	//向下递归？遍历？还未想好
//	    	_report.getTitle();
//	    	buildSegmentGroup(_report,docx);
//	    }
//	    OutputStream os = new FileOutputStream("D:\\word\\simpleWrite.docx");
//	    docx.write(os);
//	    close(os); 
        return null;
    }

    /**
     * 关闭输出流
     * @param os
     */
    public static void close(OutputStream os) {
        if (os != null) {
           try {
               os.close();
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
    }

    /**
     * 建立Segment组
     * @param _report report
     * @param docx 文档主体
     */
//	private void buildSegmentGroup(_REPORT_REPORT _report, XWPFDocument docx) {
//		String title = _report.getTitle();
//		XWPFParagraph reportSegP = docx.createParagraph();
//		XWPFRun titleRun = reportSegP.createRun();
//	}
}
