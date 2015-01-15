package com.spiritdata.jsonD.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 与jsonD相关的公用方法
 * @author wh
 */
public abstract class JsonDUtils {

    /**
     * Code是否合法，前提是this.Code不为空
     */
    public static boolean isLegalCode(String code) {
        Pattern pattern = Pattern.compile("^[A-Z]+(\\.[A-Z]+)*::\\d+");
        Matcher matcher = pattern.matcher(code);
        return matcher.find();
    }
}