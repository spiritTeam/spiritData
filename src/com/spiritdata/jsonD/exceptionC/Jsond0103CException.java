package com.spiritdata.jsonD.exceptionC;
/**
 * Task转换为JsonD的access对象异常，内部码为0102，基本信息为'Task转换为JsonD的access'
 * 请参看:
 * {@linkplain com.spiritdata.jsonD.util.JsonUtils JsonUtils}
 * @author wh
 */
public class Jsond0103CException extends JsondCException {
    private static final long serialVersionUID = -7562893262042593302L;

    private static String myBaseMsg = "Task转换为JsonD的access";
    private static int myCode = 103;

    /**
     * 构造没有详细消息内容的——'Task转换为JsonD的access'异常
     */
    public Jsond0103CException() {
        super(myCode, myBaseMsg);
    }

    /**
     * 构造有详细消息内容的——'Task转换为JsonD的access'异常
     * @param message 详细消息
     */
    public Jsond0103CException(String msg) {
        super(myCode, myBaseMsg, msg);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息构造新——'Task转换为JsonD的access'异常
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     */
    public Jsond0103CException(Throwable cause) {
        super(myCode, myBaseMsg, cause);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息，以及详细消息构造新——'Task转换为JsonD的access'异常
     * @param message 详细消息
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     */
    public Jsond0103CException(String msg, Throwable cause) {
        super(myCode, myBaseMsg, msg, cause);
    }

    public Jsond0103CException(String msg, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(myCode, myBaseMsg, msg, cause, enableSuppression, writableStackTrace);
    }
}