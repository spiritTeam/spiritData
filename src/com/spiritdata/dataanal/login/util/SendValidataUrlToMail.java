package com.spiritdata.dataanal.login.util;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.spiritdata.dataanal.login.LoginConstants;
/**
 * 发送简易验证邮件
 * @author mht
 */
public class SendValidataUrlToMail {
    private  String host = LoginConstants.HOST_MAIL_SMTP;
    private String mailName = LoginConstants.HOST_MAIL_NAME.substring(0,LoginConstants.HOST_MAIL_NAME.lastIndexOf("@"));
    private String password = LoginConstants.HOST_MAIL_PASSWORD;
    private  boolean debug = LoginConstants.HOST_MAIL_DEBUG;
    public void send(String targetMail, String msgTitle, String msgText) throws MessagingException  {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.auth", Boolean.valueOf(true));
        if (debug) props.put("mail.debug", Boolean.valueOf(debug));
        System.out.println("开始发送邮件===================targetMail="+targetMail+",mailName="+mailName+", password="+password+",msgText="+msgText);
        MailAuth auth = new MailAuth(mailName, password);
        Session session = Session.getInstance(props, auth);
        session.setDebug(debug);
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(LoginConstants.HOST_MAIL_NAME));
        InternetAddress[] address = { new InternetAddress(targetMail) };
        msg.setRecipients(Message.RecipientType.TO, address);
        msg.setSubject(msgTitle);
        msg.setSentDate(new Date());
        msg.setText(msgText);
        Transport.send(msg);
        System.out.println("结束发送邮件===================targetMail="+targetMail+",mailName="+mailName+", password="+password+",msgText="+msgText);
    }
}  