package com.gmteam.jsonD.exceptionC;

import com.gmteam.framework.exceptionC.Plat0000CException;

/**
 * JsonD中原子数据类型不规范，内部码为0001，基本信息为'原子类型(AtomData)不规范'
 * 请参看:
 * {@linkplain com.gmteam.jsonD.model.AtomData AtomData}
 * @author wh
 */
public class Jsond0001CException extends JsondCException {
    private static final long serialVersionUID = -7866589640578169284L;
    private static String myBaseMsg = "原子类型(AtomData)不规范";
    private static int myCode = 1;

    /**
     * 构造没有详细消息内容的——'原子类型(AtomData)不规范'异常
     * @throws Plat0000CException 若设置的分类码或内部码不符合规范
     */
    public Jsond0001CException() throws Plat0000CException {
        super(myCode, myBaseMsg);
    }

    /**
     * 构造有详细消息内容的——'原子类型(AtomData)不规范'异常
     * @param message 详细消息
     * @throws Plat0000CException 若设置的分类码或内部码不符合规范
     */
    public Jsond0001CException(String msg) throws Plat0000CException {
        super(myCode, myBaseMsg, msg);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息构造新——'原子类型(AtomData)不规范'异常
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     * @throws Plat0000CException 若设置的分类码或内部码不符合规范
     */
    public Jsond0001CException(Throwable cause) throws Plat0000CException {
        super(myCode, myBaseMsg, cause);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息，以及详细消息构造新——'原子类型(AtomData)不规范'异常
     * @param message 详细消息
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     * @throws Plat0000CException 若设置的分类码或内部码不符合规范
     */
    public Jsond0001CException(String msg, Throwable cause) throws Plat0000CException {
        super(myCode, myBaseMsg, msg, cause);
    }

    public Jsond0001CException(String msg, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) throws Plat0000CException {
        super(myCode, myBaseMsg, msg, cause, enableSuppression, writableStackTrace);
    }
}