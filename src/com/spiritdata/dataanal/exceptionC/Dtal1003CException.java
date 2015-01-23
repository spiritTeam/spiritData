package com.spiritdata.dataanal.exceptionC;

/**
 * 报告生成时，内部码为1003，基本信息为'报告生成时'
 * @author wh
 */
public class Dtal1003CException extends DtalCException {
    private static final long serialVersionUID = 6270463547210877699L;

    private static String myBaseMsg = "报告生成时";
    private static int myCode = 1003;

    /**
     * 构造没有详细消息内容的——'报告生成时'异常
     */
    public Dtal1003CException() {
        super(myCode, myBaseMsg);
    }

    /**
     * 构造有详细消息内容的——'报告生成时'异常
     * @param message 详细消息
     */
    public Dtal1003CException(String msg) {
        super(myCode, myBaseMsg, msg);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息构造新——'报告生成时'异常
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     */
    public Dtal1003CException(Throwable cause) {
        super(myCode, myBaseMsg, cause);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息，以及详细消息构造新——'报告生成时'异常
     * @param message 详细消息
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     */
    public Dtal1003CException(String msg, Throwable cause) {
        super(myCode, myBaseMsg, msg, cause);
    }

    public Dtal1003CException(String msg, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(myCode, myBaseMsg, msg, cause, enableSuppression, writableStackTrace);
    }
}