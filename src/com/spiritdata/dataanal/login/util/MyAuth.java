package com.spiritdata.dataanal.login.util;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class MyAuth extends Authenticator
{
  String username = "";
  String password = "";

  public MyAuth(String userString, String pasString) { this.username = userString;
    this.password = pasString;
  }

  public PasswordAuthentication getPasswordAuthentication()
  {
    return new PasswordAuthentication(this.username, this.password);
  }
}
