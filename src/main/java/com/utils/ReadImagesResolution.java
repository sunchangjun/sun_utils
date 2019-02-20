package com.utils;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * 图片处理之读取图片分辨率
 */
public class ReadImagesResolution {

    public void test() {
        File file = new File("D:\\music\\images\\20190214def (1).png");
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(file);
            int width = bi.getWidth();
            int height = bi.getHeight();
            System.out.println("宽:"+width+"   高:"+height);

        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
