package com.spiritdata.dataanal.exceptionC;

/**
 * DataAnal数据分析中字典项分析，内部码为0203，基本信息为'字典项分析'异常
 * @author wh
 */
public class Dtal0203CException extends DtalCException {
    private static final long serialVersionUID = 4069386424502916992L;

    private static String myBaseMsg = "字典项分析";
    private static int myCode = 203;

    /**
     * 构造没有详细消息内容的——'字典项分析'异常
     */
    public Dtal0203CException() {
        super(myCode, myBaseMsg);
    }

    /**
     * 构造有详细消息内容的——'字典项分析'异常
     * @param message 详细消息
     */
    public Dtal0203CException(String msg) {
        super(myCode, myBaseMsg, msg);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息构造新——'字典项分析'异常
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     */
    public Dtal0203CException(Throwable cause) {
        super(myCode, myBaseMsg, cause);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息，以及详细消息构造新——'字典项分析'异常
     * @param message 详细消息
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     */
    public Dtal0203CException(String msg, Throwable cause) {
        super(myCode, myBaseMsg, msg, cause);
    }

    public Dtal0203CException(String msg, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(myCode, myBaseMsg, msg, cause, enableSuppression, writableStackTrace);
    }
}