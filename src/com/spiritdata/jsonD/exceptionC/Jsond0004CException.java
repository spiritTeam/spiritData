package com.spiritdata.jsonD.exceptionC;

/**
 * JsonD编码不合规，内部码为0002，基本信息为'JsonD信息不规范'
 * @author wh
 */
public class Jsond0004CException extends JsondCException {
    private static final long serialVersionUID = -1721935395468980737L;

    private static String myBaseMsg = "JsonD编码不合规";
    private static int myCode = 3;

    /**
     * 构造没有详细消息内容的——'JsonD编码不合规'异常
     */
    public Jsond0004CException() {
        super(myCode, myBaseMsg);
    }

    /**
     * 构造有详细消息内容的——'JsonD编码不合规'异常
     * @param message 详细消息
     */
    public Jsond0004CException(String msg) {
        super(myCode, myBaseMsg, msg);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息构造新——'JsonD编码不合规'异常
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     */
    public Jsond0004CException(Throwable cause) {
        super(myCode, myBaseMsg, cause);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息，以及详细消息构造新——'JsonD编码不合规'异常
     * @param message 详细消息
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     */
    public Jsond0004CException(String msg, Throwable cause) {
        super(myCode, myBaseMsg, msg, cause);
    }

    public Jsond0004CException(String msg, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(myCode, myBaseMsg, msg, cause, enableSuppression, writableStackTrace);
    }
}