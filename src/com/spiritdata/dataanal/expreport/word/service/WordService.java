package com.spiritdata.dataanal.expreport.word.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.Test;
import org.springframework.stereotype.Service;

import com.spiritdata.dataanal.UGA.pojo.User;
import com.spiritdata.dataanal.expreport.word.WordConstants;
import com.spiritdata.dataanal.expreport.word.util.WordUtils;
import com.spiritdata.dataanal.report.model.Report;
import com.spiritdata.dataanal.report.model.ReportHead;
import com.spiritdata.dataanal.report.model.ReportSegment;
import com.spiritdata.dataanal.report.model.SegmentList;
import com.spiritdata.dataanal.report.service.ReportService;
import com.spiritdata.framework.core.model.tree.TreeNode;
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
	
    @Resource
    private ReportService reportSerivce;

    @Resource
    private JsonDService jsonDSerive;

    /**
     * 入口方法：
     * @param userInfo 用户信息
     * @param reportId 报告id，
     * @throws Exception 
     */
    public Map<String,Object> expWord(String reportId, User userInfo) throws Exception{
        
        //1、获得report 的been
        initReportAndJsonD(reportId);
        //2、bulidWord
        Map<String,Object> retMap = bulidWord();
        return retMap;
    }

    @Resource
    private WordService wordService;

    //_report
	private SegmentList<ReportSegment> _REPORT = new SegmentList<ReportSegment>();
	//_DLIST
	private ArrayList<AccessJsonD> _DLIST = new ArrayList<AccessJsonD>();
	//_HEAD
	private ReportHead _HEAD;

    /**
     * 得到jsond和report
     * @param reportId
     * @return 
     * @throws Exception 
     */
    @SuppressWarnings("unchecked")
    private void initReportAndJsonD(String reportId) throws Exception {
        //report
        String reportJson = reportSerivce.getReportJsonById(reportId);
        Map<String,Object> reportMap = (Map<String, Object>)JsonUtils.jsonToObj(reportJson, Map.class);
        map2been(reportMap);
        //jsonD
    }

    /**
     * 
     * @param reportMap
     * @param class1
     * @return 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws ClassNotFoundException 
     * @throws InstantiationException 
     */
    @SuppressWarnings("unchecked")
	private void map2been(Map<String, Object> reportMap) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InstantiationException {
    	//1、 得到数据
        //_DLIST
        List<Map<String,Object>> reportDList = (List<Map<String, Object>>) reportMap.get(WordConstants.REPORT_DLIST);
        //_REPORT
        List<Map<String,Object>> reportReportList = (List<Map<String, Object>>) reportMap.get(WordConstants.REPORT_REPORT);
        //_HEAD
        Map<String,Object> reportHeadMap = (Map<String, Object>) reportMap.get(WordConstants.REPORT_HEAD);
        
        //2、得到转换类 
        //head
        this._HEAD = (ReportHead) fieldBeen(ReportHead.class.getName(),reportHeadMap);
        //dList
        for (Map<String,Object> dListMap :reportDList) {
            AccessJsonD accessJonnD = (AccessJsonD) fieldBeen(AccessJsonD.class.getName(),dListMap);
            this._DLIST.add(accessJonnD);
        }
        //segMent
        for (Map<String,Object> segmentMap:reportReportList) {
        	this._REPORT.add((TreeNode<ReportSegment>)fieldBeen(ReportSegment.class.getName(),segmentMap));
        }
    }

    /**
     * 根据反射，给对象赋值
     * @param string 类
     * @param dataMap map型的数据
     */
	public Object fieldBeen(String className, Map<String,Object> dataMap) throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InstantiationException{
        Object retObj = null;
        //head部分
        if (className.equals("com.spiritdata.dataanal.report.model.ReportHead")) {
        	ReportHead reportHead = (ReportHead) getInstance(ReportHead.class,dataMap);
            retObj = reportHead;
        } else if (className.equals("com.spiritdata.jsonD.model.AccessJsonD")) {
        	//dList部分
            AccessJsonD accessJsonD = (AccessJsonD) getInstance(AccessJsonD.class,dataMap);;
            retObj = accessJsonD;
        } else if (className.equals("com.spiritdata.dataanal.report.model.ReportSegment")) {
        	//segment部分
        	TreeNode<ReportSegment> segmentTree =bulidSegTree(dataMap);
            retObj = segmentTree;
        }
        return retObj;
    }

	/**
	 * 通过反射进行实例，并赋值
	 * @param cls
	 * @param dataMap
	 * @return new Instance
	 */
	private Object getInstance(Class<?> cls,Map<String, Object> dataMap) throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		Iterator<String> it = dataMap.keySet().iterator();
		Object instance =  cls.newInstance();
        Field fields[] = cls.getDeclaredFields();
        while(it.hasNext()){
            String propertyName = it.next();
            Object val = dataMap.get(propertyName);
            //因为属性和report可能开头处差个“_”,
            if(propertyName.indexOf("_")!=-1){
            	propertyName = propertyName.substring(1,propertyName.length());
            }
            for(Field field :fields){
                String fieldName = field.getName();
                field.setAccessible(true);
                if(propertyName.equals(fieldName)){
                    field.set(instance, val);
                    break;
                }
            }
        }
		return instance;
	}

	/**
	 * 递归segment 返回树结点
	 * @return 树结点
	 * @param childSegMap segment数据
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
    @SuppressWarnings("unchecked")
	private TreeNode<ReportSegment> bulidSegTree(Map<String, Object> childSegMap) throws IllegalArgumentException, IllegalAccessException {
    	TreeNode<ReportSegment> treeNode = null;
    	ReportSegment segment = new ReportSegment();
    	List<TreeNode<ReportSegment>> childNodeList = new ArrayList<TreeNode<ReportSegment>>();
    	if (childSegMap!=null&&childSegMap.size()>0) {
    		Iterator<String> it = childSegMap.keySet().iterator();
    		while (it.hasNext()) {
    			String propertyName = it.next();
    			Object val = childSegMap.get(propertyName);
    			if (propertyName.equals(WordConstants.REPORT_SUBSEG)) {
    				for (Map<String,Object> v : (List<Map<String,Object>>)val) {
    					childNodeList.add(bulidSegTree(v));
    					break;
    				}
            	}
    			if(propertyName.indexOf("_")!=-1){
                	propertyName = propertyName.substring(1,propertyName.length());
                }
    			Field parentFields[] = ReportSegment.class.getSuperclass().getDeclaredFields();
	            Field fields[] = ReportSegment.class.getDeclaredFields();
    			for(Field field :fields){
                    String fieldName = field.getName();
                    field.setAccessible(true);
                    if(fieldName.equals(propertyName)){
                        field.set(segment, val);
                        break;
                    }
                }
                for(Field field :parentFields){
                    String fieldName = field.getName();
                    field.setAccessible(true);
                    if(fieldName.equals(propertyName)){
                        field.set(segment, val);
                        break;
                    }
                }
    		}
    	}
    	treeNode = new TreeNode<ReportSegment>(segment);
    	for(TreeNode<ReportSegment> childNode:childNodeList) {
    		treeNode.addChild(childNode);
    	}
		return treeNode;
	}

    /**
     * 创建word
     * @param beenMap 
     * @return 
     * @throws IOException 
     */
    private Map<String, Object> bulidWord() throws IOException {
        //新建一个文档 
        XWPFDocument docx = new XWPFDocument();
        
        //1、标题部分====
        XWPFParagraph titlePara = docx.createParagraph();
        //一个XWPFRun代表具有相同属性的一个区域。
        XWPFRun titleRun = titlePara.createRun();
        String title = this._HEAD.getReportName();
        titleRun.setBold(true); //加粗
        titleRun.setText(title);
        titleRun.setFontSize(22);
        
        //2、正文部分====
        for (TreeNode<ReportSegment> treeNode :this._REPORT) {
        	buildSegmentGroup(treeNode,docx);
        }
        XWPFParagraph contentPara = docx.createParagraph();
        
        //3、写成文档
        OutputStream os = new FileOutputStream("D:\\word\\simpleWrite.docx");
        docx.write(os);
        WordUtils.close(os); 
        return null;
    }

    /**
     * 建立Segment组
     * @param treeNode segment树
     * @param docx 文档对象
     */
    private void buildSegmentGroup(TreeNode<ReportSegment> treeNode,XWPFDocument docx) {
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
}
