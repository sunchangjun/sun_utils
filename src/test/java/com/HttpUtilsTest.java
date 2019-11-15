package com;

import com.maxmind.geoip2.DatabaseReader;
import com.utils.HttpUtils;
import org.junit.Test;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author ：suncj
 * @date ：2019/9/26 11:58
 */
public class HttpUtilsTest {
    @Test
    public void test(){
//        try {
//            File path = new File(ResourceUtils.getURL("classpath:").getPath());
//            System.out.println(path.getPath());
//            File file = ResourceUtils.getFile("classpath:GeoLite2");
//            System.out.println(file.getPath());
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        // 创建 GeoLite2 数据库
//        File database = new File(path);
        // 读取数据库内容
        try {
//            DatabaseReader reader = new DatabaseReader.Builder(database).build();
//            System.out.println(HttpUtils.getProvince(reader,"47.98.153.144"));
//            System.out.println(HttpUtils.getCity(reader,"47.98.153.144"));
//            System.out.println(HttpUtils.getLongitude(reader,"47.98.153.144"));
            System.out.println(HttpUtils.getLatitude("47.98.153.144"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
