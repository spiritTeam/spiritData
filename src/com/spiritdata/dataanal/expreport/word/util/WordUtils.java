package com.spiritdata.dataanal.expreport.word.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * word utils
 * @author mht
 */
public abstract class WordUtils{

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
