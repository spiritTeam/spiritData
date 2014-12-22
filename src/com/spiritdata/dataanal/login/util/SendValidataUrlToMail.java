package com.spiritdata.dataanal.login.util;

import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.spiritdata.dataanal.login.LoginConstants;

public class SendValidataUrlToMail {
    private  String host = LoginConstants.HOST_MAIL_SMTP;
    private String mailName = LoginConstants.HOST_MAIL_NAME.substring(0,LoginConstants.HOST_MAIL_NAME.lastIndexOf("@"));
    private String password = LoginConstants.HOST_MAIL_PASSWORD;
    private  boolean debug = LoginConstants.HOST_MAIL_DEBUG;
    public void send(String targetMail, String msgTitle, String msgText) {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.auth", Boolean.valueOf(true));
        if (debug) {
            props.put("mail.debug", Boolean.valueOf(debug));
        }
        MyAuth auth = new MyAuth(mailName, password);
        Session session = Session.getInstance(props, auth);
        session.setDebug(debug);
        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(LoginConstants.HOST_MAIL_NAME));
            InternetAddress[] address = { new InternetAddress(targetMail) };
            msg.setRecipients(Message.RecipientType.TO, address);
            msg.setSubject(msgTitle);
            msg.setSentDate(new Date());
            msg.setText(msgText);
            Transport.send(msg);
        } catch (MessagingException mex) {
            System.out.println("\n--Exception handling in msgsendsample.java");
            mex.printStackTrace();
            System.out.println();
            Exception ex = mex;
            do {
                if ((ex instanceof SendFailedException)) {
                SendFailedException sfex = (SendFailedException)ex;
                Address[] invalid = sfex.getInvalidAddresses();
                if (invalid != null) {
                    System.out.println("    ** Invalid Addresses");
                    for (int i = 0; i < invalid.length; i++)
                    System.out.println("         " + invalid[i]);
                }
                Address[] validUnsent = sfex.getValidUnsentAddresses();
                if (validUnsent != null) {
                    System.out.println("    ** ValidUnsent Addresses");
                    for (int i = 0; i < validUnsent.length; i++)
                    System.out.println("         " + validUnsent[i]);
                }
                Address[] validSent = sfex.getValidSentAddresses();
                if (validSent != null) {
                    System.out.println("    ** ValidSent Addresses");
                    for (int i = 0; i < validSent.length; i++)
                    System.out.println("         " + validSent[i]);
                }
              }
              System.out.println();
              if ((ex instanceof MessagingException))
                  ex = ((MessagingException)ex).getNextException();
              else
                  ex = null; 
            }
            while (ex != null);
        }
    }
}  