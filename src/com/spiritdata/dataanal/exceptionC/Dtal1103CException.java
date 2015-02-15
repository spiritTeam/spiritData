package com.spiritdata.dataanal.exceptionC;

/**
 * DataAnal邮件发送失败，内部码为1103，基本信息为'邮件发送失败'
 * 请参看:
 * {@linkplain com.spiritdata.dataanal.metadata.relation.pojo.MetadataModel MetadataModel} 包
 * @author wh,mht
 */
public class Dtal1103CException extends DtalCException {
    private static final long serialVersionUID = 7076807147636335529L;

    private static String myBaseMsg = "邮件发送失败";
    private static int myCode = 1103;

    /**
     * 构造没有详细消息内容的——'邮件发送失败'异常
     */
    public Dtal1103CException() {
        super(myCode, myBaseMsg);
    }

    /**
     * 构造有详细消息内容的——'邮件发送失败'异常
     * @param message 详细消息
     */
    public Dtal1103CException(String msg) {
        super(myCode, myBaseMsg, msg);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息构造新——'邮件发送失败'异常
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     */
    public Dtal1103CException(Throwable cause) {
        super(myCode, myBaseMsg, cause);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息，以及详细消息构造新——'邮件发送失败'异常
     * @param message 详细消息
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     */
    public Dtal1103CException(String msg, Throwable cause) {
        super(myCode, myBaseMsg, msg, cause);
    }

    public Dtal1103CException(String msg, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(myCode, myBaseMsg, msg, cause, enableSuppression, writableStackTrace);
    }
}