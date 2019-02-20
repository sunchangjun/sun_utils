package com.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class LyricUtils {

    public  static boolean  readAndCheckLyric(String  lyricFilePath,String message) {
        try {
            File lyricFile=new File(lyricFilePath);
            if (lyricFile.isFile()  &&  lyricFile.exists()){
                String encoding = "utf-8";
                InputStreamReader read = new InputStreamReader(new FileInputStream(lyricFile), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                StringBuffer stringBuffer = new  StringBuffer();
                String lineStr = null; // 每次读取一行字符串
                while ((lineStr = bufferedReader.readLine()) != null) {
                    stringBuffer.append(lineStr);
                    if (!lineStr.startsWith("[")){
                        System.out.println("歌词格式不对");
                        return false;
                    }
                    System.out.println(lineStr);
                }
                if (!StringUtils.contains(stringBuffer.toString(),"的号码是")){
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
