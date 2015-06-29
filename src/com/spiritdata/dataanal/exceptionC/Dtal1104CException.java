package com.spiritdata.dataanal.exceptionC;

/**
 * DataAnal用户信息校验，内部码为1104，基本信息为'用户信息校验'
 * 请参看:
 * {@linkplain com.spiritdata.dataanal.metadata.relation.pojo.MetadataModel MetadataModel} 包
 * @author wh,mht
 */
public class Dtal1104CException extends DtalCException {
    private static final long serialVersionUID = 7076807147636335529L;

    private static String myBaseMsg = "用户信息校验";
    private static int myCode = 1104;

    /**
     * 构造没有详细消息内容的——'用户信息校验'异常
     */
    public Dtal1104CException() {
        super(myCode, myBaseMsg);
    }

    /**
     * 构造有详细消息内容的——'用户信息校验'异常
     * @param message 详细消息
     */
    public Dtal1104CException(String msg) {
        super(myCode, myBaseMsg, msg);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息构造新——'用户信息校验'异常
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     */
    public Dtal1104CException(Throwable cause) {
        super(myCode, myBaseMsg, cause);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息，以及详细消息构造新——'用户信息校验'异常
     * @param message 详细消息
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     */
    public Dtal1104CException(String msg, Throwable cause) {
        super(myCode, myBaseMsg, msg, cause);
    }

    public Dtal1104CException(String msg, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(myCode, myBaseMsg, msg, cause, enableSuppression, writableStackTrace);
    }
}