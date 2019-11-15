package com.utils;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Hashtable;

/**
 * 二维码工具类
 * 
 * @author alistair.chow
 * @date 2018-05-15
 */
public class QRCodeUtils {
    private static final String CHARSET = "UTF-8";
    private static final String FORMAT_NAME = "JPG";
    // 二维码尺寸
    private static final int QRCODE_SIZE = 200;
    // LOGO宽度
    private static final int WIDTH = 60;
    // LOGO高度
    private static final int HEIGHT = 60;

    /**
     * 生成带LOGO的二维码
     * @param content       二维码内容
     * @param logoImgPath   LOGO图片路径
     * @param destPath      二维码输出路径
     * @param needCompress  是否压缩LOGO
     * @throws IOException
     * @throws WriterException
     */
    public static void encoderQRCode(String content, String logoImgPath, String destPath, boolean needCompress)
            throws IOException, WriterException {
        BufferedImage image = createImage(content, logoImgPath, needCompress);

        ImageIO.write(image, FORMAT_NAME, new File(destPath));
    }

    /**
     * 生成不带LOGO的二维码
     * @param content
     * @param destPath
     */
    public static void encoderQRCode(String content, String destPath)
            throws IOException, WriterException {
        encoderQRCode(content, null, destPath, false);
    }

    /**
     * 生成带LOGO的二维码，并输出到指定输出流
     * @param content
     * @param logoImgPath
     * @param output
     * @param needCompress
     * @throws IOException
     * @throws WriterException
     */
    public static void encoderQRCode(String content, String logoImgPath, OutputStream output, boolean needCompress)
            throws IOException, WriterException {
        BufferedImage image = createImage(content, logoImgPath, needCompress);
        ImageIO.write(image, FORMAT_NAME, output);
    }

    /**
     * 生成不带LOGO的二维码，并输出到指定输出流
     * @param content
     * @param output
     * @throws IOException
     * @throws WriterException
     */
    public static void encodeQRCode(String content, OutputStream output)
            throws IOException, WriterException {
        encoderQRCode(content, null, output, false);
    }

    /**
     * 生成带LOGO的二维码BASE64字符串
     * @param content
     * @return
     * @throws IOException
     */
    public static String encoderQRCodeStr(String content, String logoImgPath, boolean needCompress)
            throws IOException, WriterException {
        BufferedImage image = createImage(content, logoImgPath, needCompress);
        return qrCode2Base64(image);
    }

    /**
     * 生成不带LOGO的二维码BASE64字符串
     * @param content
     * @return
     * @throws IOException
     * @throws WriterException
     */
    public static String encoderQRCodeStr(String content) throws IOException, WriterException {
        return encoderQRCodeStr(content, null, false);
    }

    /**
     * 创建二维码图片
     * @param content           二维码内容
     * @param logoImgPath       LOGO图片路径
     * @param needCompress      是否压缩LOGO
     * @return
     * @throws WriterException
     */
    private static BufferedImage createImage(String content, String logoImgPath, boolean needCompress)
            throws WriterException, IOException {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        if(logoImgPath == null || logoImgPath.equals("")){
            return image;
        }

        insertImage(image, logoImgPath, needCompress);
        return image;
    }

    /**
     * 解析二维码图片
     * @param image
     * @return
     * @throws NotFoundException
     */
    public static String decoderQRCode(BufferedImage image)
            throws NotFoundException {
        if(image == null){
            return null;
        }
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result;
        Hashtable<DecodeHintType, Object> hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
        result = new MultiFormatReader().decode(bitmap, hints);
        String resultStr = result.getText();
        return resultStr;
    }

    /**
     * 解析二维码图片
     * @param file
     * @return
     * @throws IOException
     * @throws NotFoundException
     */
    public static String decoderQRCode(File file)
            throws IOException, NotFoundException {
        BufferedImage image;
        image = ImageIO.read(file);
        return decoderQRCode(image);
    }

    /**
     * 解析二维码图片
     * @param path
     * @return
     * @throws IOException
     * @throws NotFoundException
     */
    public static String decoderQRCode(String path) throws IOException, NotFoundException {
        return decoderQRCode(new File(path));
    }

    /**
     * 解析二维码字符串
     * @param qrCodeStr BASE64字符串
     * @return
     */
    public static String decoderQRCodeStr(String qrCodeStr)
            throws IOException, NotFoundException {
        BufferedImage bufImg = null;
        byte[] bytes = base64Stream(qrCodeStr);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        bufImg = ImageIO.read(inputStream);
        return decoderQRCode(bufImg);
    }

    /**
     * 添加LOGO
     * @param source            二维码图片
     * @param logoImgPath       LOGO路径
     * @param needCompress      是否压缩
     * @throws IOException
     */
    private static void insertImage(BufferedImage source, String logoImgPath, boolean needCompress)
            throws IOException {
        File file = new File(logoImgPath);
        if(!file.exists()){
            return;
        }

        Image src = ImageIO.read(new File(logoImgPath));
        int width = ((BufferedImage) src).getWidth();
        int height = ((BufferedImage) src).getHeight();
        if(needCompress){
            if(width > WIDTH){
                width = WIDTH;
            }

            if(height > HEIGHT){
                height = HEIGHT;
            }

            Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics graphics = tag.getGraphics();
            graphics.drawImage(image, 0, 0, null);
            graphics.dispose();
            src = image;
        }

        // 插入LOGO
        Graphics2D graphics2D = source.createGraphics();
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        graphics2D.drawImage(src, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, height, 6, 6);
        graphics2D.setStroke(new BasicStroke(3f));
        graphics2D.draw(shape);
        graphics2D.dispose();
    }

    /**
     * 二维码转BASE64
     * @param bufImg
     * @return
     * @throws IOException
     */
    private static String qrCode2Base64(BufferedImage bufImg) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufImg, FORMAT_NAME, outputStream);
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(outputStream.toByteArray());
    }

    private static byte[] base64Stream(String imgStr) throws IOException {
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] bytes = null;
        // Base64解码
        bytes = decoder.decodeBuffer(imgStr);
        for (int i = 0; i < bytes.length; ++i) {
            if (bytes[i] < 0) {// 调整异常数据
                bytes[i] += 256;
            }
        }
        return bytes;
    }
}
