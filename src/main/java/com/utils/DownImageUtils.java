package com.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class DownImageUtils {
    /**
     * http下载图片
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
