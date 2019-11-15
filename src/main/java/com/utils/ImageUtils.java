/**
 *
 */
package com.utils;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

/**
 * Alex Tang
 * 2018年7月17日
 * desc:
 */
public class ImageUtils {
    private static final Logger logger = LoggerFactory.getLogger(ImageUtils.class);


    public static final String TYPE_JPG = "jpg";
    public static final String TYPE_GIF = "gif";
    public static final String TYPE_PNG = "png";
    public static final String TYPE_BMP = "bmp";
    public static final String TYPE_UNKNOWN = "unknown";

    public static class ImagesMessage {
        private int width;
        private int height;

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    /**
     * byte数组转换成16进制字符串
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    /**
     * 根据文件流判断图片类型
     *
     * @param fis
     * @return jpg/png/gif/bmp
     */
    public static String getPicType(FileInputStream fis) {
        //读取文件的前几个字节来判断图片格式
        byte[] b = new byte[4];
        try {
            fis.read(b, 0, b.length);
            String type = bytesToHexString(b).toUpperCase();
            if (type.contains("FFD8FF")) {
                return TYPE_JPG;
            } else if (type.contains("89504E47")) {
                return TYPE_PNG;
            } else if (type.contains("47494638")) {
                return TYPE_GIF;
            } else if (type.contains("424D")) {
                return TYPE_BMP;
            } else {
                return TYPE_UNKNOWN;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    /*读取图片分辨率*/
    public static ImagesMessage identifyImageResolution(String filePath) {
        if (StringUtils.isBlank(filePath)) {
            return null;
        }
        ImagesMessage im = new ImagesMessage();
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return null;
            }
            BufferedImage bi = ImageIO.read(file);
            int width = bi.getWidth();
            int height = bi.getHeight();
            im.setHeight(height);
            im.setWidth(width);
            System.out.println("宽:" + width + "   高:" + height);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return im;
    }
    /*检测图片分辨率*/
    public static boolean checkImagesGteResolution(String filePath, int resolution) {
        try {
            ImagesMessage imagesMessage = identifyImageResolution(filePath);
            if (null != imagesMessage) {
                int width = imagesMessage.getWidth();
                int height = imagesMessage.getHeight();
                if (width >= resolution && height >= resolution) {
                    System.out.println("检测图片>=" + resolution + ",结果可用");
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    /*下载图片*/
    public static String httpDownImage(String pic_url, String saveDir) {
        if (StringUtils.isBlank(pic_url)) {
            return null;
        }
        String subString = StringUtils.substringAfterLast(pic_url, "/");
        String suffixString = StringUtils.substringAfterLast(subString, ".");
        String newFile = null;
        if (StringUtils.isBlank(suffixString)) {
            newFile = saveDir + "\\" + subString + ".png";
        } else {
            newFile = saveDir + "\\" + subString;
        }
        try {
            URL url = new URL(pic_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            FileOutputStream fos = new FileOutputStream(new File(newFile));
            byte[] buffer = new byte[1024 * 1024 * 10];
            int len = 0;
            while ((len = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
                fos.flush();
            }
            is.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newFile;
    }

    /*判断远端图片是否存在*/
    public static boolean getRource(String source) {
        try {
            URL url = new URL(source);
            URLConnection uc = url.openConnection();
            InputStream in = uc.getInputStream();
            if (source.equalsIgnoreCase(uc.getURL().toString()))
                in.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void replaceImageColor(String file, Color srcColor, Color targetColor) throws IOException {
        URL http;
        if (file.trim().startsWith("https")) {
            http = new URL(file);
            HttpsURLConnection conn = (HttpsURLConnection) http.openConnection();
            conn.setRequestMethod("GET");
        } else if (file.trim().startsWith("http")) {
            http = new URL(file);
            HttpURLConnection conn = (HttpURLConnection) http.openConnection();
            conn.setRequestMethod("GET");
        } else {
            http = new File(file).toURI().toURL();
        }
        BufferedImage bi = ImageIO.read(http.openStream());

        for (int i = 0; i < bi.getWidth(); i++) {
            for (int j = 0; j < bi.getHeight(); j++) {
                System.out.println(bi.getRGB(i, j));
                if (srcColor.getRGB() == bi.getRGB(i, j)) {
                    System.out.println(i + "," + j + "  from:" + srcColor.getRGB() + "to" + targetColor.getRGB());
                    bi.setRGB(i, j, targetColor.getRGB());
                }
            }
        }
        Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName("png");
        ImageWriter writer = it.next();
        File f = new File("c://test02.png");
        ImageOutputStream ios = ImageIO.createImageOutputStream(f);
        writer.setOutput(ios);
        writer.write(bi);
        bi.flush();
        ios.flush();
        ios.close();
    }

    //通过修改dpi压缩文件大小
    public static boolean handleDpichangeSize( String inPath,String outPath) {
        try {
            File file= new File(inPath);
            if (!file.isFile() || !file.exists()){
                return false;
            }
            BufferedImage image = ImageIO.read(file);
            File outFile= new File(outPath);
            JPEGImageEncoder jpegEncoder = JPEGCodec.createJPEGEncoder(new FileOutputStream(outFile));
            JPEGEncodeParam jpegEncodeParam = jpegEncoder.getDefaultJPEGEncodeParam(image);
            jpegEncodeParam.setDensityUnit(JPEGEncodeParam.DENSITY_UNIT_DOTS_INCH);
            jpegEncoder.setJPEGEncodeParam(jpegEncodeParam);
            jpegEncodeParam.setQuality(0.7f, false);
            jpegEncoder.encode(image, jpegEncodeParam);
            image.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
