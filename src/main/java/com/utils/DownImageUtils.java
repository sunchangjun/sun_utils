package com.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

public class DownImageUtils {

    public static final String TYPE_JPG = "jpg";
    public static final String TYPE_GIF = "gif";
    public static final String TYPE_PNG = "png";
    public static final String TYPE_BMP = "bmp";
    public static final String TYPE_UNKNOWN = null;
    private static final Logger log = LoggerFactory.getLogger(DownImageUtils.class);

    /**
     * 下载工具
     * @param url 文件的地址
     * @param path 预先生成的文件路径前缀
     * @return 返回文件存储的全路径
     * @throws Exception
     */
    public static String getHttpImage(String url, String path) throws Exception {
        InputStream is = null;
        long total = 0;
        try {
            log.info("开始下载文件:"+url);
            long start = System.currentTimeMillis();
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            //对于firefox的浏览器,腾讯输出的图片是jpg格式的
            conn.setRequestProperty("User-agent","Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.8.1.11) Gecko/20071127 Firefox/2.0.0.11 wthx");
            conn.setRequestProperty("Accept","text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
            conn.setRequestProperty("Accept-Language", "zh-cn,zh;q=0.9,en;q=0.7");
            conn.setRequestProperty("Accept-Charset","gb2312,utf-8;q=0.7,*;q=0.7");
            conn.setRequestProperty("Keep-Alive", "300");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setConnectTimeout(10 * 1000);
            conn.setReadTimeout(15 * 1000);
            conn.connect();
            is = conn.getInputStream();
            byte[] b = new byte[1024 * 1024 * 10];
            long size = conn.getContentLengthLong();
            int old_pct = 0;//百分比
            int len = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((len=is.read(b))!=-1) {
                baos.write(b, 0, len);
                total += len;
                int now_pct = (int)((total*100)/size);
                if((now_pct-old_pct)>4){
                    old_pct = now_pct;
                    System.out.print(old_pct+"% ");
                }
            }
            long dur = System.currentTimeMillis()-start;
            System.out.println("done with:"+dur/1000+"秒, 文件大小:"+total/1024+"kb");
            //byte读取完成,开始写入文件
            byte[] bs = baos.toByteArray();
            String type = getPicType(bs);
            if(type != null){
                path += "." + type;
            }
            FileOutputStream fos = new FileOutputStream(path);
            fos.write(bs);
            fos.close();
            log.info("文件保存到:"+path);
            return path;
        }catch(FileNotFoundException fe){//文件不存在的异常,文件在腾讯服务器上不存在,返回404导致
            throw fe;
        }catch(SocketTimeoutException se){//下载到中间的时候,网络出现异常,此时需要将碎文件先删除
            deleteFile(path);
            Thread.sleep(10000);//睡眠10秒
            throw se;
        }catch(Exception e){
            e.printStackTrace();
            deleteFile(path);
            throw e;
        }finally{
            if (is != null){
                try{is.close();
                }catch(Exception e){}
            }
        }
    }
    private static void deleteFile(String filePath){
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }
    /**
     * byte数组转换成16进制字符串
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src){
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
     * 文件的head标志
     * @param head
     * @return 文件类型
     */
    private static String parseHeader(String head) {
        if (head.contains("FFD8FF")) {
            return TYPE_JPG;
        } else if (head.contains("89504E47")) {
            return TYPE_PNG;
        } else if (head.contains("47494638")) {
            return TYPE_GIF;
        } else if (head.contains("424D")) {
            return TYPE_BMP;
        }else{
            return TYPE_UNKNOWN;
        }
    }


    /**
     * 根据文件字节判断图片类型
     * @param bs
     * @return jpg/png/gif/bmp
     */
    public static String getPicType(byte[] bs) {
        byte[] b = new byte[4];
        System.arraycopy(bs, 0, b, 0, 4);
        String head = bytesToHexString(b).toUpperCase();
        return parseHeader(head);
    }



    /**
     * http下载图片(网络)
     * @param pic_url
     * @param saveDir
     * @return
     */
    public static String httpDownImage(String pic_url,String saveDir){
        if(StringUtils.isBlank(pic_url)){
            return null;
        }
        String subString = StringUtils.substringAfterLast(pic_url, "/");
        System.out.println(subString);
        String suffixString = StringUtils.substringAfterLast(subString, ".");
        System.out.println(suffixString);
        String newFile = null;
        if(StringUtils.isBlank(suffixString)){
            newFile=   saveDir+"\\"+subString+".png";
        }else{
            newFile=  saveDir+"\\"+subString;
        }
        try {
            URL url = new URL(pic_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis=new BufferedInputStream(is);
            FileOutputStream fos = new FileOutputStream(new File(newFile));
            byte[] buffer = new byte[1024 * 1024 * 10];
            int len=0;
            while( (len=bis.read(buffer)) != -1 ) {
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


}
