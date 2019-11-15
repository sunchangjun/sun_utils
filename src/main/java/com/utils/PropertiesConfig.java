package com.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 读取配置文件,解决上传图片session失效问题
 * @author ：suncj
 * @date ：2019/7/26 15:39
 */


public class PropertiesConfig {

    public static String getValue(String key){
        Properties prop = new Properties();
        String application_profile=getApplicationValue("spring.profiles.active");
        String applicationFilePath="/application-"+application_profile+".properties";
        InputStream in = new PropertiesConfig().getClass().getResourceAsStream(applicationFilePath);
        try {
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop.getProperty(key);
    }
    private static String getApplicationValue(String key){
        Properties prop = new Properties();
        InputStream in = new PropertiesConfig().getClass().getResourceAsStream("/application.properties");
        try {
            prop.load(in);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop.getProperty(key);
    }


}
