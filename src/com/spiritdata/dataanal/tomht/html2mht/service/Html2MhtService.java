package com.spiritdata.dataanal.tomht.html2mht.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.spiritdata.dataanal.tomht.html2mht.util.BriefConstants;
import com.spiritdata.dataanal.tomht.html2mht.util.CommonUtils;
import com.spiritdata.dataanal.tomht.html2mht.util.Html2MHTCompiler;
import com.spiritdata.dataanal.tomht.html2mht.util.ImageUtil;

/** 
 * @author 
 * @version  
 * 类说明 Html2Mhtservie类，
 * 提供Html2Mht的方法
 */
@Component
public class Html2MhtService {
    CommonUtils cu = new CommonUtils();
    /**WebContent路径*/
    String eclipseWebPath = "/WebContent/";
    /**
     * 创建图片，返回图片路径
     * @param imgStr
     * @param rootDirectory
     * @return
     * @throws IOException 
     */
    public String createImage(String imgStr,String realPath) throws IOException{
        ImageUtil iu = new ImageUtil();
        String imagePath = iu.GenerateImage(imgStr,cu.getJavaPath(realPath));
        return imagePath;
    }
    /**
     * 需要拼接字符串的网页全文
     * @param url
     * @param imgMap
     * @return 保存文件名
     */
    public String createMht(String url, Map<String, String> imgInfoMap, String osPath) throws Exception {
        String docStr = Html2MHTCompiler.downFileTxt(url, "utf-8");
        //存文件，替换串
        String imgFileName = null;
        Iterator<String> iterator=imgInfoMap.keySet().iterator();
        while(iterator.hasNext()) {
          //生成Img文件
          String domId=String.valueOf(iterator.next());
          String imgStr=imgInfoMap.get(domId);
          System.out.println("----");
          imgFileName = createImage(imgStr, cu.getJavaPath(osPath));
          //替换dom
          docStr = replaceIdByImg(docStr, domId, imgFileName,cu.getJavaPath(osPath));
        }
        Html2MHTCompiler h2t = new Html2MHTCompiler();
        h2t.setStrText(docStr);
        h2t.setStrEncoding("utf-8");
        String mhtFilePath=cu.getJavaPath(cu.getJavaPath(osPath))+BriefConstants.MHT_PATH+"aaaa.mht";
        h2t.setStrFileName(mhtFilePath);
        h2t.setStrWeb(new URL(url));
        h2t.compile();
        return "";
    }
    /**
     * 根据domId，拼接字符串
     * @param jspContentStr 页面内容
     * @param domId divId
     * @param imgFileName 
     * @param path
     * @return
     */
    private String replaceIdByImg(String jspContentStr, String domId, String imgFileName, String path) {
        String divContent = jspContentStr.substring(jspContentStr.indexOf("<div id=\""+domId+"\""));
        String oldDivStr = divContent.substring(0,divContent.indexOf("</div>"));
        ///bf/WebContent/html2mht/htmlFolder/aa.jpg
        String newDivStr = oldDivStr+"<img src=\""+imgFileName+"\">";
        jspContentStr = jspContentStr.replace(oldDivStr, newDivStr);
        return jspContentStr;
    }
    /**
     * 不需要拼接字符串的网页全文
     * @param url ：请求路径
     * @param osPath：实际路径
     * @return
     * @throws MalformedURLException
     */
    public String createMht(String url, String osPath) throws MalformedURLException {
        String docStr = Html2MHTCompiler.downFileTxt(url, "utf-8");
        Html2MHTCompiler h2t = new Html2MHTCompiler();
        h2t.setStrText(docStr);
        h2t.setStrEncoding("utf-8");
        String mhtFilePath=cu.getJavaPath(cu.getJavaPath(osPath))+BriefConstants.MHT_PATH+"aaaa.mht";
        h2t.setStrFileName(mhtFilePath);
        h2t.setStrWeb(new URL(url));
        h2t.compile();
        return "";
    }
    /**
     * 对图片信息进行处理，组成一个key=domId，value=imgStr 格式的map
     * @param requestMap
     * @return
     */
    public Map<String, String> getImgInfo(Map<String, String[]> requestMap) {
        if(requestMap.size()>1){
            Map<String,String> imgInfoMap = new HashMap<String, String>();
            Iterator<String> iterator=requestMap.keySet().iterator();
            while(iterator.hasNext()) {
                String domId=String.valueOf(iterator.next());
                if(!domId.equals("thisUrl")){
                    String imgArray[] = requestMap.get(domId);
                    for(String s:imgArray){
                        imgInfoMap.put(domId, s);
                    }
                }
            }
            return imgInfoMap;
        }else{
            return null;
        }
    }
}
