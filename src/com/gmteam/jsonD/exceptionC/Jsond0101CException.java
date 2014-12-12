package com.gmteam.jsonD.exceptionC;

import com.gmteam.framework.exceptionC.Plat0000CException;

/**
 * JsonD中对象向Json串转换异常，内部码为0101，基本信息为'json转换'
 * 请参看:
 * {@linkplain com.gmteam.jsonD.util.JsonUtils JsonUtils}
 * @author wh
 */
public class Jsond0101CException extends JsondCException {
    private static final long serialVersionUID = 1197542690339017535L;
    private static String myBaseMsg = "json转换";
    private static int myCode = 101;

    /**
     * 构造没有详细消息内容的——'json转换'异常
     * @throws Plat0000CException 若设置的分类码或内部码不符合规范
     */
    public Jsond0101CException() throws Plat0000CException {
        super(myCode, myBaseMsg);
    }

    /**
     * 构造有详细消息内容的——'json转换'异常
     * @param message 详细消息
     * @throws Plat0000CException 若设置的分类码或内部码不符合规范
     */
    public Jsond0101CException(String msg) throws Plat0000CException {
        super(myCode, myBaseMsg, msg);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息构造新——'json转换'异常
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     * @throws Plat0000CException 若设置的分类码或内部码不符合规范
     */
    public Jsond0101CException(Throwable cause) throws Plat0000CException {
        super(myCode, myBaseMsg, cause);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息，以及详细消息构造新——'json转换'异常
     * @param message 详细消息
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     * @throws Plat0000CException 若设置的分类码或内部码不符合规范
     */
    public Jsond0101CException(String msg, Throwable cause) throws Plat0000CException {
        super(myCode, myBaseMsg, msg, cause);
    }

    public Jsond0101CException(String msg, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) throws Plat0000CException {
        super(myCode, myBaseMsg, msg, cause, enableSuppression, writableStackTrace);
    }
}