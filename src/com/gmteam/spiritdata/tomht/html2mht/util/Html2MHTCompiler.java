package com.gmteam.spiritdata.tomht.html2mht.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.MimetypesFileTypeMap;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.lexer.Page;
import org.htmlparser.util.DefaultParserFeedback;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

/** 
 * @author 
 * @version  
 */
public class Html2MHTCompiler {
    private URL strWeb = null; /**网页地址*/
    private String strText = null; /**网页文本内容*/
    private String strFileName = null; /**本地文件名*/
    private String strEncoding = null; /**网页编码*/
    //mht格式附加信息
    private String from = "dongle2001@126.com";
    private String to;
    private String subject = "mht compile";
    private String cc;
    private String bcc;
    private String smtp = "localhost";
    public static void main(String[] args) {
        //http://www.mtime.com/my/tropicofcancer/blog/843555/
        //"http://localhost:8080/bf/apps/briefView/viewB.jsp"
        String strUrl = "http://www.mtime.com/my/tropicofcancer/blog/843555/";
        String strEncoding = "utf-8";
        //String strText = JQuery.getHtmlText(strUrl, strEncoding, null);
        String strText = downFileTxt(strUrl, strEncoding);
        if (strText == null)
            return;
        Html2MHTCompiler h2t = new Html2MHTCompiler(strUrl, strEncoding, "11.mht");
        h2t.compile();
        //Html2MHTCompiler.mht2html("test.mht", "a.html");
    }
    /**
     * 方法说明：初始化
     * 输入参数：strText 网页文本内容; strUrl 网页地址; strEncoding 网页编码; strFileName 本地文件名
     * 返回类型：
     */
    public Html2MHTCompiler(String strUrl, String strEncoding, String strFileName) {
        try {
            strWeb = new URL(strUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return;
        }
       //this.strText = strText;
        this.strText = downFileTxt(strUrl, strEncoding);
        this.strEncoding = strEncoding;
        this.strFileName = strFileName;
    }
    public void setStrText(String strText) {
        this.strText = strText;
    }
    public void setStrWeb(URL strWeb) {
        this.strWeb = strWeb;
    }
    public void setStrFileName(String strFileName) {
        this.strFileName = strFileName;
    }
    public void setStrEncoding(String strEncoding) {
        this.strEncoding = strEncoding;
    }
    public Html2MHTCompiler() {
        super();
    }
    /**
     * 方法说明：执行下载操作
     * 输入参数：
     * 返回类型：
     */
    public boolean compile() {
        if (strWeb == null || strText == null || strFileName == null || strEncoding == null)
            return false;
        Map<String,String> urlMap = new HashMap<String,String>();
        NodeList nodes = new NodeList();
        try {
            Parser parser = createParser(strText);
            parser.setEncoding(strEncoding);
            nodes = parser.parse(null);
        } catch (ParserException e) {
            e.printStackTrace();
        }
        extractAllScriptNodes(nodes);
        List<List<String>> urlScriptList = extractAllScriptNodes(nodes, urlMap);
        List<List<String>> urlImageList = extractAllImageNodes(nodes, urlMap);
        for (Iterator<Entry<String, String>> iter = urlMap.entrySet().iterator(); iter.hasNext();) {
            Map.Entry<String,String> entry = (Map.Entry<String,String>) iter.next();
            String key = (String)entry.getKey();
            String val = (String)entry.getValue();
            //strText = JHtmlClear.replace(strText, val, key);
            strText = strText.replaceAll(val, key);
        }
        try {
            createMhtArchive(strText, urlScriptList, urlImageList);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    /**
     *方法说明：建立HTML parser
     *输入参数：inputHTML 网页文本内容
     *返回类型：HTML parser
    */
    private Parser createParser(String inputHTML) {
        Lexer mLexer = new Lexer(new Page(inputHTML));
        return new Parser(mLexer, new DefaultParserFeedback(DefaultParserFeedback.QUIET));
    }
    /**
     *方法说明：抽取基础URL地址
     *输入参数：nodes 网页标签集合
     *返回类型：
    */
    private void extractAllScriptNodes(NodeList nodes) {
        NodeList filtered = nodes.extractAllNodesThatMatch(new TagNameFilter("BASE"), true);
        if (filtered != null && filtered.size() > 0) {
            Tag tag = (Tag) filtered.elementAt(0);
            String href = tag.getAttribute("href");
            if (href != null && href.length() > 0) {
                try {
                    strWeb = new URL(href);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     *方法说明：抽取网页包含的css,js链接
     *输入参数：nodes 网页标签集合; urlMap 已存在的url集合
     *返回类型：css,js链接的集合
    */
    private List<List<String>> extractAllScriptNodes(NodeList nodes, Map<String,String> urlMap) {
        List<List<String>> urlList = new ArrayList<List<String>>();
        NodeList filtered = nodes.extractAllNodesThatMatch(new TagNameFilter("script"), true);
        for (int i = 0; i < filtered.size(); i++) {
            Tag tag = (Tag) filtered.elementAt(i);
            String src = tag.getAttribute("src");
            // Handle external css file's url
            if (src != null && src.length() > 0) {
                String innerURL = src;
                String absoluteURL = makeAbsoluteURL(strWeb, innerURL);
                if (absoluteURL != null && !urlMap.containsKey(absoluteURL)) {
                    urlMap.put(absoluteURL, innerURL);
                    List<String> urlInfo = new ArrayList<String>();
                    urlInfo.add(innerURL);
                    urlInfo.add(absoluteURL);
                    urlList.add(urlInfo);
                }
                tag.setAttribute("src", absoluteURL);
            }
        }
        filtered = nodes.extractAllNodesThatMatch(new TagNameFilter("link"), true);
        for (int i = 0; i < filtered.size(); i++) {
            Tag tag = (Tag) filtered.elementAt(i);
            String type = (tag.getAttribute("type"));
            String rel = (tag.getAttribute("rel"));
            String href = tag.getAttribute("href");
            boolean isCssFile = false;
            if (rel != null) {
                isCssFile = rel.indexOf("stylesheet") != -1;
            } else if (type != null) {
                isCssFile |= type.indexOf("text/css") != -1;
            }
            if (isCssFile && href != null && href.length() > 0) {
                String innerURL = href;
                String absoluteURL = makeAbsoluteURL(strWeb, innerURL);
                if (absoluteURL != null && !urlMap.containsKey(absoluteURL)) {
                    urlMap.put(absoluteURL, innerURL);
                    List<String> urlInfo = new ArrayList<String>();
                    urlInfo.add(innerURL);
                    urlInfo.add(absoluteURL);
                    urlList.add(urlInfo);
                } 
                tag.setAttribute("href", absoluteURL);
            }
        }
        return urlList;
    }
    /**
     * 方法说明：抽取网页包含的图像链接
     * 输入参数：nodes 网页标签集合; urlMap 已存在的url集合
     * 返回类型：图像链接集合
    */
    private List<List<String>> extractAllImageNodes(NodeList nodes, Map<String, String> urlMap) {
        List<List<String>> urlList = new ArrayList<List<String>>();
        NodeList filtered = nodes.extractAllNodesThatMatch(new TagNameFilter("IMG"), true);
        for (int i = 0; i < filtered.size(); i++) {
            Tag tag = (Tag) filtered.elementAt(i);
            String src = tag.getAttribute("src");
            // Handle external css file's url
            if (src != null && src.length() > 0) {
                String innerURL = src;
                String absoluteURL = makeAbsoluteURL(strWeb, innerURL);
                if (absoluteURL != null && !urlMap.containsKey(absoluteURL)) {
                    urlMap.put(absoluteURL, innerURL);
                    List<String> urlInfo = new ArrayList<String>();
                    urlInfo.add(innerURL);
                    urlInfo.add(absoluteURL);
                    urlList.add(urlInfo);
                }
                tag.setAttribute("src", absoluteURL);
            }
        }
        return urlList;
    }
    /**
     * 方法说明：相对路径转绝对路径
     * 输入参数：strWeb 网页地址; innerURL 相对路径链接
     * 返回类型：绝对路径链接
     */
    public static String makeAbsoluteURL(URL strWeb, String innerURL) {
        //去除后缀
        //int pos = innerURL.indexOf("?");
        //if (pos != -1) {
        // innerURL = innerURL.substring(0, pos);
        //}
        if (innerURL != null&& innerURL.toLowerCase().indexOf("http") == 0) {
            //System.out.println(innerURL);
            return innerURL;
        }
        URL linkUri = null;
        try {
            linkUri = new URL(strWeb, innerURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        String absURL = linkUri.toString();
        //absURL = JHtmlClear.replace(absURL, "../", "");
        //absURL = JHtmlClear.replace(absURL, "./", "");
        //absURL = absURL.replaceAll("../", "");
        //absURL = absURL.replaceAll("./", "");
        //System.out.println(absURL);
         return absURL;
    }
    /**
     * 方法说明：创建mht文件
     * 输入参数：content 网页文本内容; urlScriptList 脚本链接集合; urlImageList 图片链接集合
     * 返回类型：
     */
    
    private void createMhtArchive(String content, List<List<String>> urlScriptList, List<List<String>> urlImageList) throws Exception {
        //Instantiate a Multipart object
        MimeMultipart mp = new MimeMultipart("related");
        Properties props = new Properties();
        props.put("mail.smtp.host", smtp);
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage msg = new MimeMessage(session);
        // set mailer
        msg.setHeader("X-Mailer", "Code Manager .SWT");
        // set from
        if (from != null) {
            msg.setFrom(new InternetAddress(from));
        }
        // set subject
        if (subject != null) {
            msg.setSubject(subject);
        }
        // to
        if (to != null) {
            InternetAddress[] toAddresses = getInetAddresses(to);
            msg.setRecipients(Message.RecipientType.TO, toAddresses);
        }
        // cc
        if (cc != null) {
            InternetAddress[] ccAddresses = getInetAddresses(cc);
            msg.setRecipients(Message.RecipientType.CC, ccAddresses);
        }
        // bcc
        if (bcc != null) {
            InternetAddress[] bccAddresses = getInetAddresses(bcc);
            msg.setRecipients(Message.RecipientType.BCC, bccAddresses);
        }
        //设置网页正文
        MimeBodyPart bp = new MimeBodyPart();
        bp.setText(content, strEncoding);
        bp.addHeader("Content-Type", "text/html;charset=" + strEncoding);
        bp.addHeader("Content-Location", strWeb.toString());
        mp.addBodyPart(bp);
        int urlCount = urlScriptList.size();
        for (int i = 0; i < urlCount; i++) {
            bp = new MimeBodyPart();
            List<String> urlInfo = (List<String>) urlScriptList.get(i);
            // String url = urlInfo.get(0).toString();
            String absoluteURL = urlInfo.get(1).toString();
            bp.addHeader("Content-Location",javax.mail.internet.MimeUtility.encodeWord(java.net.URLDecoder.decode(absoluteURL, strEncoding)));
            DataSource source = new AttachmentDataSource(absoluteURL, "text");
            bp.setDataHandler(new DataHandler(source));
            mp.addBodyPart(bp);
        }
        urlCount = urlImageList.size();
        for (int i = 0; i < urlCount; i++) {
            bp = new MimeBodyPart();
            List<String> urlInfo = (List<String>) urlImageList.get(i);
            // String url = urlInfo.get(0).toString();
            String absoluteURL = urlInfo.get(1).toString();
            bp.addHeader("Content-Location",javax.mail.internet.MimeUtility.encodeWord(java.net.URLDecoder.decode(absoluteURL, strEncoding)));
            DataSource source = new AttachmentDataSource(absoluteURL, "image");
            bp.setDataHandler(new DataHandler(source));
            mp.addBodyPart(bp);
        }
        msg.setContent(mp);
        // write the mime multi part message to a file
        File dir = new File (strFileName) ;
        if(!dir.exists())
        dir.createNewFile() ;
        //
        msg.writeTo(new FileOutputStream(strFileName));
    }
    /**
     * 方法说明：得到资源文件的name
     * 输入参数：strName 资源文件链接, ID 资源文件的序号
     * 返回类型：资源文件的本地临时文件名
     */
    public static String getName(String strName, int ID) {
        char separator = '/';
        if( strName.lastIndexOf(separator) >= 0)
            return format(strName.substring(strName.lastIndexOf(separator) + 1));
        return "temp" + ID;
    }
    /**
     * 方法说明：格式化文件名
     * 输入参数：strName 文件名
     * 返回类型：经过处理的符合命名规则的文件名
     */
    private static String format(String strName) {
        if (strName == null)
        return null;
        strName = strName.replaceAll(" ", " ");
        String strText = "\\/:*?\"<>|^___FCKpd___0quot";
        for (int i = 0; i < strName.length(); ++i) {
            String ch = String.valueOf(strName.charAt(i));
            if (strText.indexOf(ch) != -1) {
                strName = strName.replace(strName.charAt(i), '-');
            }
        }
        return strName;
    }
    /**
     * 方法说明：得到jsp文本
     * 输入参数：String jspUrl文件路径; strEncoding 内容编码
     * 返回类型：html文本
     */
    public static String downFileTxt(String jspUrl, String strEncoding) {
        InputStream textStream = null;
        BufferedInputStream buff = null;
        BufferedReader br = null;
        Reader r = null;
        try {
            URL url = new URL(jspUrl);
            textStream = url.openStream();
            buff = new BufferedInputStream(textStream);
            r = new InputStreamReader(buff, strEncoding);
            br = new BufferedReader(r);
            StringBuffer strHtml = new StringBuffer("");
            String strLine = null;
            while ((strLine = br.readLine()) != null) {
                strHtml.append(strLine + "\r\n");
            }
            br.close();
            r.close();
            textStream.close();
            return strHtml.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try{
                if (br != null)
                br.close();
                if (buff != null)
                buff.close();
                if (textStream != null)
                textStream.close();
            }catch(Exception e){
                System.out.println("解析jsp失败");
            }
        }
        return null;
    }
    /**
     * 方法说明：得到资源文件字节流
     * 输入参数：String jspUrl文件路径; strEncoding 内容编码
     * 返回类型：html文本
     */
    private static byte[] downFileByte(String jspUrl) {
        InputStream textStream = null;
        BufferedInputStream buff = null;
        //BufferedReader br = null;
        //Reader r = null;
        //byte[] buf = new byte[1024];
        byte[] buf = null;
        //int len = 0;
        try {
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            URL url = new URL(jspUrl);
            textStream = url.openStream();
            buff = new BufferedInputStream(textStream);
            int ch = 0 ;  
            while ((ch = buff.read()) != - 1 ) {  
                byteArray.write(ch);  
            }
            buf = byteArray.toByteArray();
            buff.close();
            textStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try{
                //if (br != null)
                // br.close();
                if (buff != null)
                buff.close();
                if (textStream != null)
                textStream.close();
            }catch(Exception e){
                System.out.println("解析文件失败");
            }
        }
        return buf;
    }
    private InternetAddress[] getInetAddresses(String emails) throws Exception {
        List<String> list = new ArrayList<String>();
        StringTokenizer tok = new StringTokenizer(emails, ",");
        while (tok.hasMoreTokens()) {
            list.add(tok.nextToken());
        }
        int count = list.size();
        InternetAddress[] addresses = new InternetAddress[count];
        for (int i = 0; i < count; i++) {
            addresses[i] = new InternetAddress(list.get(i).toString());
        }
        return addresses;
    }
    class AttachmentDataSource implements DataSource {
        private MimetypesFileTypeMap map = new MimetypesFileTypeMap();
        private String strUrl;
        private String strType;
        private byte[] dataSize = null;
        /**
         * This is some content type maps.
         */
        private Map<String,String> normalMap = new HashMap<String,String>();
        {
            // Initiate normal mime type map
            // Images
            normalMap.put("image", "image/jpeg");
            normalMap.put("text", "text/plain");
        }
        public AttachmentDataSource(String strUrl, String strType) {
            this.strType = strType;
            this.strUrl = strUrl;
            strUrl = strUrl.trim();
            strUrl = strUrl.replaceAll(" ", "%20");
            //dataSize = JQuery.downBinaryFile(strUrl, null);
            if("text".equals(strType))
                dataSize = downFileTxt(strUrl, strEncoding).getBytes();
            else if("image".equals(strType))
                dataSize = downFileByte(strUrl);
        }
        /**
         * Returns the content type.
         */
        public String getContentType() {
            return getMimeType(getName());
        }
        public String getName() {
            char separator = File.separatorChar;
            if( strUrl.lastIndexOf(separator) >= 0 )
                return strUrl.substring(strUrl.lastIndexOf(separator) + 1);
                return strUrl;
        }
        private String getMimeType(String fileName) {
            String type = (String)normalMap.get(strType);
            if (type == null) {
                try {
                    type = map.getContentType(fileName);
                } catch (Exception e) {
                }
                //System.out.println(type);
                // Fix the null exception
                if (type == null) {
                    type = "application/octet-stream";
                }
            }
            return type;
        }
        public InputStream getInputStream() throws IOException {
        if (dataSize == null)
            dataSize = new byte[0];
            return new ByteArrayInputStream(dataSize);
        }
        public OutputStream getOutputStream() throws IOException {
            return new java.io.ByteArrayOutputStream();
        }
    }
}