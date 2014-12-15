package com.gmteam.spiritdata.exceptionC;

import com.gmteam.framework.exceptionC.Plat0000CException;

/**
 * 模板获取异常，内部码为0101，基本信息为'模板获取'
 * 请参看:
 * {@linkplain com.gmteam.spiritdata.templet.service.TempletService TempletService}
 * @author wh
 */
public class Dtal0101CException extends DtalCException {
    private static final long serialVersionUID = -6750918808279334815L;

    private static String myBaseMsg = "模板获取";
    private static int myCode = 101;

    /**
     * 构造没有详细消息内容的——'模板获取'异常
     * @throws Plat0000CException 若设置的分类码或内部码不符合规范
     */
    public Dtal0101CException() throws Plat0000CException {
        super(myCode, myBaseMsg);
    }

    /**
     * 构造有详细消息内容的——'模板获取'异常
     * @param message 详细消息
     * @throws Plat0000CException 若设置的分类码或内部码不符合规范
     */
    public Dtal0101CException(String msg) throws Plat0000CException {
        super(myCode, myBaseMsg, msg);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息构造新——'模板获取'异常
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     * @throws Plat0000CException 若设置的分类码或内部码不符合规范
     */
    public Dtal0101CException(Throwable cause) throws Plat0000CException {
        super(myCode, myBaseMsg, cause);
    }

    /**
     * 根据指定的原因和(cause==null?null:cause.toString())的详细消息，以及详细消息构造新——'模板获取'异常
     * @param message 详细消息
     * @param cause 异常原因，以后通过Throwable.getCause()方法获取它。允许使用null值，指出原因不存在或者是未知的异常
     * @throws Plat0000CException 若设置的分类码或内部码不符合规范
     */
    public Dtal0101CException(String msg, Throwable cause) throws Plat0000CException {
        super(myCode, myBaseMsg, msg, cause);
    }

    public Dtal0101CException(String msg, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) throws Plat0000CException {
        super(myCode, myBaseMsg, msg, cause, enableSuppression, writableStackTrace);
    }
}