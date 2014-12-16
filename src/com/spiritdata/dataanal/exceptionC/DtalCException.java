package com.spiritdata.dataanal.exceptionC;

import com.spiritdata.framework.CodeException;
import com.spiritdata.framework.exceptionC.Plat0000CException;

/**
 * DataAnal数据分析“带码异常”，其分类码为"DTAL"
 * @author wh
 */
public abstract class DtalCException extends CodeException {
    private static final long serialVersionUID = -4358416181016248285L;

    private static String category = "DTAL"; //分类码

    //扩充父类的构造函数
    /**
     * 构造没有详细消息内容的——DataAnal数据分析“带码异常”
     * @param c 内部码
     * @param bMsg 基础信息
     * @throws PlatCException 若设置的分类码或内部码不符合规范
     */
    protected DtalCException(int c, String bMsg) throws Plat0000CException {
        super(category, c, bMsg);
    }

    /**
     * 构造有详细消息内容的——DataAnal数据分析“带码异常”
     * @param c 内部码
     * @param bMsg 基础信息
     * @param message 详细消息
     * @throws PlatCException 若设置的分类码或内部码不符合规范
     */
    protected DtalCException(int c, String bMsg, String message) throws Plat0000CException {
        super(category, c, bMsg, message);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息构造新——DataAnal数据分析“带码异常”
     * @param c 内部码
     * @param bMsg 基础信息
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     * @throws PlatCException 若设置的分类码或内部码不符合规范
     */
    protected DtalCException(int c, String bMsg, Throwable cause) throws Plat0000CException {
        super(category, c, bMsg, cause);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息，以及详细消息构造新——DataAnal数据分析“带码异常”
     * @param c 内部码
     * @param bMsg 基础信息
     * @param message 详细消息
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     * @throws Plat0000CException 若设置的分类码或内部码不符合规范
     */
    protected DtalCException(int c, String bMsg, String message, Throwable cause) throws Plat0000CException {
        super(category, c, bMsg, message, cause);
    }

    protected DtalCException(int c, String bMsg, String message,Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) throws Plat0000CException {
        super(category, c, bMsg, message, cause, enableSuppression, writableStackTrace);
    }
}