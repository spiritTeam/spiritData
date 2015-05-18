package com.spiritdata.filemanage.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import com.spiritdata.filemanage.exceptionC.Flmg0003CException;
import com.spiritdata.framework.util.FileNameUtils;
import com.spiritdata.framework.util.StringUtils;

/**
 * 文件处理类
 * @author wh
 */
public abstract class FileOperUtils {
    /**
     * 写字符串内容content到文件
     * @param content 字符串内容
     * @param fullFileName 文件全名，包括路径
     */
    public static void write2File(String content, String fullFileName) {
        if (StringUtils.isNullOrEmptyOrSpace(fullFileName)) throw new Flmg0003CException(new IllegalArgumentException("文件名参数[fullFileName]为空或空串，无法写入文件"));
        //写文件
        FileOutputStream fileOutputStream = null;
        BufferedWriter writer = null;
        try {
            File file = new File(fullFileName);
            if (!file.exists()) {
                File dirs = new File(FileNameUtils.getFilePath(fullFileName));
                if (!dirs.exists()) {
                    if (dirs.mkdirs()) file.createNewFile();
                    else throw new Flmg0003CException("创建目录["+FileNameUtils.getFilePath(fullFileName)+"]失败，无法写入文件");
                }
            }
            //存储为UTF-8
            fileOutputStream = new FileOutputStream(file);
            writer = new BufferedWriter(new OutputStreamWriter(fileOutputStream, "utf-8"));
            //字符串转换
            byte[] _content = content.getBytes();
            String result = new String(_content,"utf-8");
            //写入
            writer = new BufferedWriter(new OutputStreamWriter(fileOutputStream, "utf-8"));
            writer.append(result);
        } catch (FileNotFoundException e) {
            throw new Flmg0003CException(e);
        } catch (IOException e) {
            throw new Flmg0003CException(e);
        } finally {
            if (fileOutputStream!=null) {
                try {fileOutputStream.close();}catch(IOException e) {e.printStackTrace();}
            }
            if (writer!=null) {
                try {writer.close();}catch(IOException e) {e.printStackTrace();}
            }
        }
    }
}