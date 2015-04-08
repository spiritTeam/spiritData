package com.spiritdata.dataanal.expreport.word.service;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.springframework.stereotype.Service;

import com.spiritdata.dataanal.UGA.pojo.User;
import com.spiritdata.dataanal.expreport.word.WordConstants;
import com.spiritdata.dataanal.report.model.Report;
import com.spiritdata.dataanal.report.model.ReportHead;
import com.spiritdata.dataanal.report.service.ReportService;
import com.spiritdata.jsonD.model.AccessJsonD;
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
        System.out.println("1");
        if(this.report!=null){
        }
    }

	/**
	 * 
	 * @param reportMap
	 * @param class1
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws ClassNotFoundException 
	 * @throws InstantiationException 
	 */
    @SuppressWarnings("unchecked")
	private void map2been(Map<String, Object> reportMap) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InstantiationException {
    	//第一层  dlist，report，head
    	//_DLIST
    	List<Map<String,Object>> reportDList = (List<Map<String, Object>>) reportMap.get(WordConstants.REPORT_DLIST);
    	//_REPORT
    	List<Map<String,Object>> reportReportList = (List<Map<String, Object>>) reportMap.get(WordConstants.REPORT_REPORT);
    	//_HEAD
		Map<String,Object> reportHeadMap = (Map<String, Object>) reportMap.get(WordConstants.REPORT_HEAD);
		//第二层 
		//head
		ReportHead reportHead = (ReportHead) fieldBeen(ReportHead.class,reportHeadMap);
		//dList
		List<AccessJsonD> accessJsonDList = new ArrayList<AccessJsonD>();
		for (Map<String,Object> dListMap :reportDList) {
			AccessJsonD accessJonnD = (AccessJsonD) fieldBeen(ReportHead.class,dListMap);
			accessJsonDList.add(accessJonnD);
		}
		//segMent
	}
    /**
     * @param propertyName 
     * @param val 
     * @param rh2 
     * @throws ClassNotFoundException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException 
     */
	public Object fieldBeen(Class<?> cls, Map<String,Object> dataMap) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InstantiationException{
		Iterator<String> headIt = dataMap.keySet().iterator();
    	Object retObj = null;
		Field fields[] = cls.getDeclaredFields();
    	for(Field field :fields){
    		String fieldName = field.getName();
    		field.setAccessible(true);
    		if (cls.getName().equals("com.spiritdata.dataanal.report.model.ReportHead")) {
    			ReportHead reportHead = (ReportHead) cls.newInstance();
    			while(headIt.hasNext()){
    				String propertyName = headIt.next();
    				Object val = dataMap.get(propertyName);
    				if(fieldName.equals(propertyName)){
            			field.set(reportHead, val);
            		}
    			}
    			retObj = reportHead;
        	} else if(cls.getName().equals("com.spiritdata.jsonD.model.AccessJsonD")) {
        		AccessJsonD accessJsonD = (AccessJsonD) cls.newInstance();
        		while(headIt.hasNext()){
    				String propertyName = headIt.next();
    				Object val = dataMap.get(propertyName);
    				if(fieldName.equals(propertyName)){
            			field.set(accessJsonD, val);
            			if(fieldName.equals(propertyName)){
                			field.set(accessJsonD, val);
                		}
            		}
    			}
        		retObj = accessJsonD;
        	}
    	}
		return retObj;
	}
	public AccessJsonD fieldBeen(AccessJsonD accessJonnD, String propertyName, Object val) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException{
    	Class<?> c=Class.forName(accessJonnD.getClass().getName());
    	Field fields[]=c.getDeclaredFields();
    	for(Field field :fields){
    		String fieldName = field.getName();
    		field.setAccessible(true);
    		if(fieldName.equals(propertyName)){
    			field.set(accessJonnD, val);
    		}
    	}
		return accessJonnD;
	}
	@Test
	public void fieldBeen() throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InstantiationException{
    	Class<?> c=Class.forName(ReportHead.class.getName());
    	System.out.println(c.getName());
    	if(ReportHead.class.getName().equals("com.spiritdata.dataanal.report.model.ReportHead")){
    		ReportHead c1 =  ReportHead.class.newInstance();
        	Field fields[]=c.getDeclaredFields();
        	for(Field field :fields){
        		String fieldName = field.getName();
        		field.setAccessible(true);
        		if(fieldName.equals("id")){
        			field.set(c1, "123");
        		}
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
