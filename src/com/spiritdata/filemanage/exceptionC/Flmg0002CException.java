package com.spiritdata.filemanage.exceptionC;
/**
 * 文件管理中数据类型不合法，内部码为0002，基本信息为'数据类型不合法'
 * @author wh
 */
public class Flmg0002CException extends FlmgCException {
    private static final long serialVersionUID = -7866589640578169284L;

    private static String myBaseMsg = "数据类型不合法";
    private static int myCode = 1;

    /**
     * 构造没有详细消息内容的——'数据类型不合法'异常
     */
    public Flmg0002CException() {
        super(myCode, myBaseMsg);
    }

    /**
     * 构造有详细消息内容的——'数据类型不合法'异常
     * @param message 详细消息
     */
    public Flmg0002CException(String msg) {
        super(myCode, myBaseMsg, msg);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息构造新——'数据类型不合法'异常
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     */
    public Flmg0002CException(Throwable cause) {
        super(myCode, myBaseMsg, cause);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息，以及详细消息构造新——'数据类型不合法'异常
     * @param message 详细消息
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     */
    public Flmg0002CException(String msg, Throwable cause) {
        super(myCode, myBaseMsg, msg, cause);
    }

    public Flmg0002CException(String msg, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(myCode, myBaseMsg, msg, cause, enableSuppression, writableStackTrace);
    }
}