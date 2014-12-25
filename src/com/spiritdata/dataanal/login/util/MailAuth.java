package com.spiritdata.dataanal.login.util;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
/**
 * 身份验证类，
 * 用于验证发送邮件邮箱的用户名和密码
 * @author mht
 */
public class MailAuth extends Authenticator{
    String username = "";
    String password = "";

    public MailAuth(String userString, String pasString) {
      this.username = userString;
      this.password = pasString;
    }
  
    public PasswordAuthentication getPasswordAuthentication(){
      return new PasswordAuthentication(this.username, this.password);
    }
}
