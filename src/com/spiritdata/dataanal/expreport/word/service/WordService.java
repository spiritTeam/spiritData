package com.spiritdata.dataanal.expreport.word.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STStyleType;
import org.springframework.stereotype.Service;

import com.spiritdata.dataanal.UGA.pojo.User;
import com.spiritdata.dataanal.expreport.word.WordConstants;
import com.spiritdata.dataanal.expreport.word.util.WordUtils;
import com.spiritdata.dataanal.expreport.word.web.WordController;
import com.spiritdata.dataanal.report.model.OneJsonD;
import com.spiritdata.dataanal.report.model.Report;
import com.spiritdata.dataanal.report.model.ReportHead;
import com.spiritdata.dataanal.report.model.ReportSegment;
import com.spiritdata.dataanal.report.model.SegmentList;
import com.spiritdata.dataanal.report.service.ReportService;
import com.spiritdata.framework.FConstants;
import com.spiritdata.framework.core.cache.CacheEle;
import com.spiritdata.framework.core.cache.SystemCache;
import com.spiritdata.framework.core.model.tree.TreeNode;
import com.spiritdata.framework.util.FileNameUtils;
import com.spiritdata.jsonD.model.AccessJsonD;
import com.spiritdata.jsonD.model.JsonD;
import com.spiritdata.jsonD.model.JsonDHead;
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
    private Report report = new Report();
    
    //jsonD
    private JsonD jsonD = new JsonD();

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
        reportMap2been(reportMap);
        //jsonD
//        for (OneJsonD jsonDInfo : this.report.get_DLIST()) {
//            // TODO 由于jsonD格式不确定，解析未完成
//            String jsonDUri = jsonDInfo.getUrl();
//            String jsonDJson = reportSerivce.getReportJsonByUri(jsonDUri);
//            Map<String,Object> jsonDMap = (Map<String, Object>)JsonUtils.jsonToObj(jsonDJson, Map.class);
//            jsonDMap2Bean(jsonDMap);
//        }
    }

    /**
     * jsonDMap to jsonDBeen
     * @param jsonDMap
     */
    @SuppressWarnings({ "unchecked", "unused" })
    private void jsonDMap2Bean(Map<String, Object> jsonDMap) throws Exception {
        Map<String,Object> _DMap = (Map<String, Object>) jsonDMap.get("_DATA");
        JsonDHead  jsonDHead = (JsonDHead) fieldBeen(JsonDHead.class.getName(), jsonDMap);
        // TODO 未实现，感觉应该跟report的一样吧。
    }

    /**
     * reportMap to reportBeen
     * @param reportMap
     */
    @SuppressWarnings("unchecked")
    private void reportMap2been(Map<String, Object> reportMap) throws Exception {
        //1、 得到数据
        //_DLIST
        List<Map<String,Object>> reportDList = (List<Map<String, Object>>) reportMap.get(WordConstants.REPORT_DLIST);
        //_REPORT
        List<Map<String,Object>> reportReportList = (List<Map<String, Object>>) reportMap.get(WordConstants.REPORT_REPORT);
        //_HEAD
        Map<String,Object> reportHeadMap = (Map<String, Object>) reportMap.get(WordConstants.REPORT_HEAD);
        
        //2、得到转换类 
        //head
        ReportHead rh = (ReportHead) fieldBeen(ReportHead.class.getName(),reportHeadMap);
        this.report.set_HEAD(rh);
        //dList
        for (Map<String,Object> dListMap :reportDList) {
            AccessJsonD accessJonnD = (AccessJsonD) fieldBeen(AccessJsonD.class.getName(),dListMap);
            this.report.addOneJsonD(accessJonnD);
        }
        //segMent
        SegmentList<ReportSegment> _REPORT = new SegmentList<ReportSegment>();
        for (Map<String,Object> segmentMap:reportReportList) {
            _REPORT.add((TreeNode<ReportSegment>)fieldBeen(ReportSegment.class.getName(),segmentMap));
        }
        this.report.set_REPORT(_REPORT);
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
            AccessJsonD accessJsonD = (AccessJsonD) getInstance(AccessJsonD.class,dataMap);
            retObj = accessJsonD;
        } else if (className.equals("com.spiritdata.dataanal.report.model.ReportSegment")) {
            //segment部分
            TreeNode<ReportSegment> segmentTree =bulidSegTree(dataMap);
            retObj = segmentTree;
        } else if (className.equals("com.spiritdata.jsonD.model.JsonDHead")) {
            JsonDHead reportHead = (JsonDHead) getInstance(JsonDHead.class,dataMap);
            retObj = reportHead;
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
            if (propertyName.indexOf("_")!=-1) {
                propertyName = propertyName.substring(1,propertyName.length());
            }
            for (Field field :fields) {
                String fieldName = field.getName();
                field.setAccessible(true);
                if (propertyName.equals(fieldName)) {
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
                    }
                }
                if (propertyName.indexOf("_")!=-1) {
                    propertyName = propertyName.substring(1,propertyName.length());
                }
                Field parentFields[] = ReportSegment.class.getSuperclass().getDeclaredFields();
                Field fields[] = ReportSegment.class.getDeclaredFields();
                for (Field field :fields) {
                    String fieldName = field.getName();
                    field.setAccessible(true);
                    if (fieldName.equals(propertyName)) {
                        field.set(segment, val);
                        break;
                    }
                }
                for (Field field :parentFields) {
                    String fieldName = field.getName();
                    field.setAccessible(true);
                    if (fieldName.equals("nodeName")) {
                        String nodeName = (String) childSegMap.get("title");
                        if (nodeName.equals("")||nodeName==null) {
                            nodeName = (String) childSegMap.get("name");
                        }
                        field.set(segment, nodeName);
                        break;
                    } else {
                        if (fieldName.equals(propertyName)) {
                            field.set(segment, val);
                            break;
                        }
                    }
                }
            }
        }
        treeNode = new TreeNode<ReportSegment>(segment);
        for (TreeNode<ReportSegment> childNode:childNodeList) {
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
    @SuppressWarnings("unchecked")
    private Map<String, Object> bulidWord() throws IOException {
        //新建一个文档 
        XWPFDocument docx = new XWPFDocument();
        
        //大纲级别
        setOutLineLvl(docx);
        //最初大纲级别
        int outLineLvl = WordConstants.REPORT_OUT_LINE_MIN_LEVEL;
        
        //1、标题部分====
        XWPFParagraph titlePara = docx.createParagraph();
        //一个XWPFRun代表具有相同属性的一个区域。
        XWPFRun titleRun = titlePara.createRun();
        Object _HEAD = this.report.get_HEAD();
        titleRun.setBold(true); //加粗
        titleRun.setText(((ReportHead)_HEAD).getReportName());
        titleRun.setFontSize(22);
        
        //2、正文部分====
        Object _REPORT = this.report.get_REPORT();
        
        for (TreeNode<ReportSegment> treeNode :(SegmentList<ReportSegment>)_REPORT) {
            buildSegmentGroup(treeNode,docx,outLineLvl);
        }
        
        //3、写成文档
        String reportName = "XXX.docx";
        String uri = WordConstants.REPORT_SAVE_PATH+reportName;
        uri = FileNameUtils.concatPath(((CacheEle<String>)SystemCache.getCache(FConstants.APPOSPATH)).getContent(), uri);
        OutputStream os = new FileOutputStream(uri);
        docx.write(os);
        this.close(os);
        Map<String,Object> retMap = new HashMap<String,Object>();
        retMap.put("success", true);
        retMap.put("reportName", reportName);
        return retMap;
    }

    /**
     * 建立Segment组
     * @param treeNode segment树
     * @param docx 文档对象
     * @param outLineLvl 
     */
    private void buildSegmentGroup(TreeNode<ReportSegment> treeNode,XWPFDocument docx, int outLineLvl) {
        //段落
        XWPFParagraph paragraph = docx.createParagraph();
        //设置段落大纲等级 id为前缀+outLineLvl(0~8)
        paragraph.setStyle(WordConstants.REPORT_OUT_LINE_LEVEL_PREFIX+outLineLvl);
        //小标题
        XWPFRun titleRun = paragraph.createRun();
        
        ReportSegment rs = treeNode.getTnEntity();
        String pTitle = rs.getTitle();
        titleRun.setText(pTitle);
        titleRun.setBold(true);
        titleRun.setFontSize(WordConstants.REPORT_TITLE_FONT_SIZZE);
        if (pTitle==null||pTitle.equals("")) pTitle = rs.getNodeName();
        String content = rs.getContent();
        if (content!=null&&!content.equals("")) {
            XWPFParagraph conttentPG = docx.createParagraph();
            XWPFRun contentRun = conttentPG.createRun();
            contentRun.setText(content);
            contentRun.setFontSize(WordConstants.REPORT_CONTENT_FONT_SIZZE);
        }
        List<TreeNode<ReportSegment>> childList  = treeNode.getChildren();
        if (childList!=null&&childList.size()>0) {
        	outLineLvl = outLineLvl+1;
            for (int i=0;i<childList.size();i++) {
                buildSegmentGroup(childList.get(i), docx,outLineLvl);
            }
        }
        //TODO 待完成 文档结构部分
    }

    /**
     * word大纲级别 
     * @param docx
     */
    private void setOutLineLvl(XWPFDocument docx){
        XWPFStyles xStyles = docx.createStyles();
        for (int i=0;i<=WordConstants.REPORT_OUT_LINE_MAX_LEVEL;i++) {
        	// TODO 这个方法中有很多东西未探究明白，应该可以用于扩展其他样式
            //id为oTL0~oTL8
            String styleId = "oTL"+i;
            CTStyle ctStyle = CTStyle.Factory.newInstance();
            ctStyle.setStyleId(styleId);

            CTString styleName = CTString.Factory.newInstance();
            styleName.setVal(styleId);
            ctStyle.setName(styleName);

            CTDecimalNumber indentNumber = CTDecimalNumber.Factory.newInstance();
            indentNumber.setVal(BigInteger.valueOf(i));

            // lower number > style is more prominent in the formats bar
            ctStyle.setUiPriority(indentNumber);

            CTOnOff onoffnull = CTOnOff.Factory.newInstance();
            ctStyle.setUnhideWhenUsed(onoffnull);

            // style shows up in the formats bar
            ctStyle.setQFormat(onoffnull);

            // style defines a heading of the given level
            CTPPr ppr = CTPPr.Factory.newInstance();
            ppr.setOutlineLvl(indentNumber);
            ctStyle.setPPr(ppr);

            XWPFStyle xStyle = new XWPFStyle(ctStyle);
            xStyle.setType(STStyleType.PARAGRAPH);
            xStyles.addStyle(xStyle);
        }
    }
    /**
     * 关闭输出流
     * @param os
     */
    private void close(OutputStream os) {
        if (os != null) {
           try {
               os.close();
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
    }
}
