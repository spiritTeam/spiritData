package com.spiritdata.jsonD.exceptionC;
/**
 * Json串转换为对象异常，内部码为0102，基本信息为'json串转换为对象'
 * 请参看:
 * {@linkplain com.spiritdata.jsonD.util.JsonUtils JsonUtils}
 * @author wh
 */
public class JsonD0102CException extends JsonDCException {
    private static final long serialVersionUID = -2726723859541109643L;

    private static String myBaseMsg = "json串转换为对象";
    private static int myCode = 102;

    /**
     * 构造没有详细消息内容的——'json串转换为对象'异常
     */
    public JsonD0102CException() {
        super(myCode, myBaseMsg);
    }

    /**
     * 构造有详细消息内容的——'json串转换为对象'异常
     * @param message 详细消息
     */
    public JsonD0102CException(String msg) {
        super(myCode, myBaseMsg, msg);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息构造新——'json串转换为对象'异常
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     */
    public JsonD0102CException(Throwable cause) {
        super(myCode, myBaseMsg, cause);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息，以及详细消息构造新——'json串转换为对象'异常
     * @param message 详细消息
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     */
    public JsonD0102CException(String msg, Throwable cause) {
        super(myCode, myBaseMsg, msg, cause);
    }

    public JsonD0102CException(String msg, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(myCode, myBaseMsg, msg, cause, enableSuppression, writableStackTrace);
    }
}