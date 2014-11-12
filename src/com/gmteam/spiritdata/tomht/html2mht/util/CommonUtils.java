package com.gmteam.spiritdata.tomht.html2mht.util;

import java.util.UUID;
/** 
 * @author mht
 * @version  
 * 类说明 :常用工具类，获得uuid,截取uuid
 */
public class CommonUtils {
    /**
     * 获得uuid
     * @return
     */
    public String getUUID(){
        UUID uuid = UUID.randomUUID();
        return uuid+"";
    }
    /**
     * 根据给定的uuid截取一部分作为序列
     * 作为tableName返回
     * @param uuid
     * @return
     */
    public String getUUID2TableSeq(String uuid){
        uuid = uuid.substring(uuid.lastIndexOf("-")+1, uuid.length());
        return uuid;
    }
    /**
     * 输入一个路径，把他变成'/a/b/..'的方式或者"\\a\\b..."
     * 的方式
     * @param path
     * @return
     */
    public String getJavaPath(String path){
        while(path.indexOf("\\")!=-1){
            path = path.replace("\\", "/");
        }
        return path;
    }
}
