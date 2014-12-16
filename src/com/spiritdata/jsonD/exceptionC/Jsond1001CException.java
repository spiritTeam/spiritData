package com.spiritdata.jsonD.exceptionC;

import com.spiritdata.framework.exceptionC.Plat0000CException;

/**
 * JsonD资源获取异常，内部码为1001，基本信息为'JsonD资源获取'
 * 请参看:
 * {@linkplain com.spiritdata.jsonD.util.JsonUtils JsonUtils}
 * @author wh
 */
public class Jsond1001CException extends JsondCException {
    private static final long serialVersionUID = 1197542690339017535L;

    private static String myBaseMsg = "JsonD资源获取";
    private static int myCode = 1001;

    /**
     * 构造没有详细消息内容的——'JsonD资源获取'异常
     * @throws Plat0000CException 若设置的分类码或内部码不符合规范
     */
    public Jsond1001CException() throws Plat0000CException {
        super(myCode, myBaseMsg);
    }

    /**
     * 构造有详细消息内容的——'JsonD资源获取'异常
     * @param message 详细消息
     * @throws Plat0000CException 若设置的分类码或内部码不符合规范
     */
    public Jsond1001CException(String msg) throws Plat0000CException {
        super(myCode, myBaseMsg, msg);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息构造新——'JsonD资源获取'异常
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     * @throws Plat0000CException 若设置的分类码或内部码不符合规范
     */
    public Jsond1001CException(Throwable cause) throws Plat0000CException {
        super(myCode, myBaseMsg, cause);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息，以及详细消息构造新——'JsonD资源获取'异常
     * @param message 详细消息
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     * @throws Plat0000CException 若设置的分类码或内部码不符合规范
     */
    public Jsond1001CException(String msg, Throwable cause) throws Plat0000CException {
        super(myCode, myBaseMsg, msg, cause);
    }

    public Jsond1001CException(String msg, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) throws Plat0000CException {
        super(myCode, myBaseMsg, msg, cause, enableSuppression, writableStackTrace);
    }
}