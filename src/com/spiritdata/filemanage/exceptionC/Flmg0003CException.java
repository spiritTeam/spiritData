package com.spiritdata.filemanage.exceptionC;
/**
 * 文件管理中文件读写(IO)异常，内部码为0003，基本信息为'文件读写(IO)'
 * @author wh
 */
public class Flmg0003CException extends FlmgCException {
    private static final long serialVersionUID = 1893627716220129223L;

    private static String myBaseMsg = "文件读写(IO)";
    private static int myCode = 3;

    /**
     * 构造没有详细消息内容的——'文件读写(IO)'异常
     */
    public Flmg0003CException() {
        super(myCode, myBaseMsg);
    }

    /**
     * 构造有详细消息内容的——'文件读写(IO)'异常
     * @param message 详细消息
     */
    public Flmg0003CException(String msg) {
        super(myCode, myBaseMsg, msg);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息构造新——'文件读写(IO)'异常
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     */
    public Flmg0003CException(Throwable cause) {
        super(myCode, myBaseMsg, cause);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息，以及详细消息构造新——'文件读写(IO)'异常
     * @param message 详细消息
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     */
    public Flmg0003CException(String msg, Throwable cause) {
        super(myCode, myBaseMsg, msg, cause);
    }

    public Flmg0003CException(String msg, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(myCode, myBaseMsg, msg, cause, enableSuppression, writableStackTrace);
    }
}