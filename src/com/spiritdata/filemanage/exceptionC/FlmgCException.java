package com.spiritdata.filemanage.exceptionC;

import com.spiritdata.framework.CodeException;

/**
 * Filemanage“带码异常”，其分类码为"FLMG"
 * @author wh
 */
public abstract class FlmgCException extends CodeException {
    private static final long serialVersionUID = -2801278037489953741L;
    private static String category = "FLMG"; //分类码

    //扩充父类的构造函数
    /**
     * 构造没有详细消息内容的——FLMG“带码异常”
     * @param c 内部码
     * @param bMsg 基础信息
     */
    protected FlmgCException(int c, String bMsg) {
        super(category, c, bMsg);
    }

    /**
     * 构造有详细消息内容的——FLMG“带码异常”
     * @param c 内部码
     * @param bMsg 基础信息
     * @param message 详细消息
     */
    protected FlmgCException(int c, String bMsg, String message) {
        super(category, c, bMsg, message);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息构造新——FLMG“带码异常”
     * @param c 内部码
     * @param bMsg 基础信息
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     */
    protected FlmgCException(int c, String bMsg, Throwable cause) {
        super(category, c, bMsg, cause);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息，以及详细消息构造新——FLMG“带码异常”
     * @param c 内部码
     * @param bMsg 基础信息
     * @param message 详细消息
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     */
    protected FlmgCException(int c, String bMsg, String message, Throwable cause) {
        super(category, c, bMsg, message, cause);
    }

    protected FlmgCException(int c, String bMsg, String message,Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(category, c, bMsg, message, cause, enableSuppression, writableStackTrace);
    }
}