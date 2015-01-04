package com.spiritdata.dataanal.login.exceptionC;

public class Login0001CException extends LoginCException {

	private static final long serialVersionUID = 6534987921168375249L;
	//异常原因
	// #TODO
	private static String myBaseMsg = "元数据类型不规范";
    private static int myCode = 1;

    /**
     * 构造没有详细消息内容的——'元数据类型不规范'异常
     */
    public Login0001CException() {
        super(myCode, myBaseMsg);
    }

    /**
     * 构造有详细消息内容的——'元数据类型不规范'异常
     * @param message 详细消息
     */
    public Login0001CException(String msg) {
        super(myCode, myBaseMsg, msg);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息构造新——'元数据类型不规范'异常
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     */
    public Login0001CException(Throwable cause) {
        super(myCode, myBaseMsg, cause);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息，以及详细消息构造新——'元数据类型不规范'异常
     * @param message 详细消息
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     */
    public Login0001CException(String msg, Throwable cause) {
        super(myCode, myBaseMsg, msg, cause);
    }

    public Login0001CException(String msg, Throwable cause, boolean enableSuppression,boolean writableStackTrace) {
        super(myCode, myBaseMsg, msg, cause, enableSuppression, writableStackTrace);
    }

}
