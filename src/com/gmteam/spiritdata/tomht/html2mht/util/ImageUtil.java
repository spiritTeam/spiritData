package com.gmteam.spiritdata.tomht.html2mht.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
/**
 * 处理图片，
 * @author mht
 */
public class ImageUtil {
    public static String GetImageStr() {
        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        String imgFile = "d:\\1234.jpg";
        //待处理的图片
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try {
            in = new FileInputStream(imgFile);        
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        //返回Base64编码过的字节数组字符串
        return encoder.encode(data);
    }
    public static byte[] base64ToBytes(String imgStr){
    	BASE64Decoder decoder = new BASE64Decoder();
    	byte[] b=null;
    	try {
    		b = decoder.decodeBuffer(imgStr);
    		for(int i=0;i<b.length;++i) {
    		    if(b[i]<0) {//调整异常数据
    		        b[i]+=256;
    		    }
    		}
    		return b;
    	} catch (IOException e) {
    	    return null;
    	}
     
    }
    /**
     * 返回保存图片的路径
     * @param imgStr
     * @param path
     * @return
     * @throws IOException 
     */
    public  String GenerateImage(String imgStr,String path) throws IOException {
        //对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) //图像数据为空
            return null;
        BASE64Decoder decoder = new BASE64Decoder();
        OutputStream out =  null;
        try {
            //Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for(int i=0;i<b.length;++i) {
                if(b[i]<0) {
                    //生成jpeg图片
                    b[i]+=256;
                }
            }
            //String imageNme = new Date()+".jpg";
            CommonUtils cu = new CommonUtils();
            String fileName = cu.getUUID();
            String imgFilePath = path+BriefConstants.HTML_PATH+fileName+".jpg";//新生成的图片
            File f = new File(imgFilePath);
            out = new FileOutputStream(f);
            out.write(b);
            out.flush();
            out.close();
            return "/sa/"+BriefConstants.HTML_PATH+fileName+".jpg";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }finally{
            out.close();
        }
    }
}
