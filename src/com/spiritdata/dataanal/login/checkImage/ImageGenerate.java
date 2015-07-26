package com.spiritdata.dataanal.login.checkImage;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * 生成验证码图片，这是一个静态方法集
 * @author wh
 */
public class ImageGenerate {
    private static int width = 93;//图片宽
    private static int height = 33;//图片高
    private static int lineSize = 38;//干扰线数量

    /**
     * 根据checkCode生成验证码图片
     * @param checkCode 验证码
     * @return 验证码图片的对象对象
     */
    public static BufferedImage generate(String checkCode) {
        Random random = new Random();
        BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_BGR);
        Graphics g = image.getGraphics();//产生Image对象的Graphics对象,改对象可以在图像上进行各种绘制操作
        g.fillRect(0, 0, width, height);
        g.setFont(new Font("Times New Roman",Font.ROMAN_BASELINE,18));
        g.setColor(new Color((110+random.nextInt(7)),(110+random.nextInt(9)),(110+random.nextInt(5))));
        //绘制干扰线
        for(int i=0;i<=lineSize;i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(13);
            int yl = random.nextInt(15);
            g.drawLine(x, y, x+xl, y+yl);
        }
        for (int i=0;i<checkCode.length();i++) {
            g.setFont(new Font("Fixedsys",Font.CENTER_BASELINE,18));
            g.setColor(new Color(random.nextInt(101),random.nextInt(111),random.nextInt(121)));
            g.translate((random.nextInt(1)==1?1:-1)*random.nextInt(2), (random.nextInt(1)==1?1:-1)*random.nextInt(2));
            g.drawString(""+checkCode.charAt(i), 18*i+20, 25);
        }
        g.dispose();
        return image;
    }
}