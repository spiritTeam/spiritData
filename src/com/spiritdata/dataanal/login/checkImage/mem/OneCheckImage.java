package com.spiritdata.dataanal.login.checkImage.mem;

import java.awt.image.BufferedImage;

public class OneCheckImage {
    protected String checkCode;//验证码
    protected long createTime;//生成验证码的时间
    protected BufferedImage checkImage;//验证码图片

    public String getCheckCode() {
        return this.checkCode;
    }

    public BufferedImage getCheckImage() {
        return this.checkImage;
    }
}