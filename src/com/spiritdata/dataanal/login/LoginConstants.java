package com.spiritdata.dataanal.login;
/**
 * 激活邮箱常量
 * @author admi
 */
public class LoginConstants {
    /*
     * MAIL_SMTP_HOST，只有发件人才用，目标邮箱没有，例如qq邮箱就是smtp.qq.com，
     * 网易163邮箱就是smtp.163.com,由于是发件箱邮箱的smtp,如果mailName变成163，
     * 则smtp就变为smtp.163.com
     */
    public final static String HOST_MAIL_SMTP="smtp.qq.com";
    //发件箱用户名
    public final static String HOST_MAIL_NAME="3546509521@qq.com";
    //邮箱密码
    public final static String HOST_MAIL_PASSWORD="MHT7415963LJJ";
    //是否打印dug信息
    public final static boolean HOST_MAIL_DEBUG=false;
}
