package com.spiritdata.dataanal.test;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Runtime获取文件创建时间示例
 */
public class FileCreatedDemo {
    public static void main(String[] args) {
    	getCreateTime("C:\\Users\\Administrator\\Desktop\\文档\\LOGO-050112.jpg");
    	getModifiedTime("C:\\Users\\Administrator\\Desktop\\文档\\LOGO-050112.jpg");
    }
    
    public static void getCreateTime(String filePath) {
        String strTime = null;
        try {
        	Process p = Runtime.getRuntime().exec("cmd /C dir " + filePath + "/tc");
        	InputStream is = p.getInputStream();
        	BufferedReader br = new BufferedReader(new InputStreamReader(is));
        	String line;
        	while ((line = br.readLine()) != null) {
        		if (line.endsWith(".txt")) {
        			strTime = line.substring(0, 17);
        			break;
        		}
        	}
        } catch (IOException e) {
        	e.printStackTrace();
        }
        System.out.println("创建时间    " + strTime);
    }
    public static void getModifiedTime(String filePath) {
	    long time = new File(filePath).lastModified();
	    String ctime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(time));
	    System.out.println("修改时间[1] " + ctime);
    }
}
