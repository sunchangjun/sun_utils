package com.utils;

import com.SunUtilsApplication;
import com.maxmind.geoip2.DatabaseReader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * @author ：suncj
 * @date ：2019/9/26 11:37
 */
public class HttpUtils {
    private static Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    private static  DatabaseReader reader=null;
    private  static void before(){
        if(null == reader){
            try {
                File fileDir = ResourceUtils.getFile("classpath:GeoLite2");
                String databaseFilePath=fileDir.getPath()+File.separator+"GeoLite2-City.mmdb";
                File database = new File(databaseFilePath);
                reader=new DatabaseReader.Builder(database).build();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    /**
     * 获取最为匹配的本机ip地址
     * @return
     * @throws Exception
     */
    public static String getLocalIpAddress() throws Exception {
        InetAddress candidateAddress = null;
        Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
        while(ifaces.hasMoreElements()){// 遍历所有的网络接口
            NetworkInterface iface = ifaces.nextElement();
            Enumeration<InetAddress> inetAddrs = iface.getInetAddresses();
            while(inetAddrs.hasMoreElements()){// 在所有的接口下再遍历IP
                InetAddress inetAddr = inetAddrs.nextElement();
                if (!inetAddr.isLoopbackAddress()) {// 排除loopback类型地址
                    if (inetAddr.isSiteLocalAddress()) {// 如果是site-local地址，就是它了
                        return inetAddr.getHostAddress();
                    } else if (candidateAddress == null) {// site-local类型的地址未被发现，先记录候选地址
                        candidateAddress = inetAddr;
                    }
                }
            }
        }
        if (candidateAddress != null) {
            return candidateAddress.getHostAddress();
        }
        // 如果没有发现 non-loopback地址.只能用最次选的方案
        InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
        return jdkSuppliedAddress.getHostAddress();
    }

    /**
     * 返回客户端真实的ip地址
     * @param req
     * @return
     */
    public static String getIpAddress(HttpServletRequest req){
        String real_ip = req.getHeader("x-forwarded-for");
        real_ip = (real_ip == null)?req.getRemoteAddr():real_ip;
        if(real_ip.equals("0:0:0:0:0:0:0:1")){
            real_ip = "127.0.0.1";
        }
        return real_ip;
    }


    /**
     * 获取客户端IP地址
     *
     * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
     * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
     */
    public static String getIP(HttpServletRequest request) {
        String ip = null;
        try {
            ip = request.getHeader("x-forwarded-for");
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
        } catch (Exception e) {
            logger.error("IPUtils ERROR ", e);
        }
        return ip;
    }

    /**
     *
     * @description: 获得国家
     * @param reader
     * @param ip
     * @return
     * @throws Exception
     */
    public static String getCountry(DatabaseReader reader, String ip) throws Exception {
        return reader.city(InetAddress.getByName(ip)).getCountry().getNames().get("zh-CN");
    }

    /**
     *
     * @description: 获得省份
     * @param reader
     * @param ip
     * @return
     * @throws Exception
     */
    public static String getProvince(DatabaseReader reader, String ip) throws Exception {
        return reader.city(InetAddress.getByName(ip)).getMostSpecificSubdivision().getNames().get("zh-CN");
    }

    /**
     *
     * @description: 获得城市
     * @param reader
     * @param ip
     * @return
     * @throws Exception
     */
    public static String getCity(DatabaseReader reader, String ip) throws Exception {
        return reader.city(InetAddress.getByName(ip)).getCity().getNames().get("zh-CN");
    }

    /**
     *
     * @description: 获得经度
     * @param reader
     * @param ip
     * @return
     * @throws Exception
     */
    public static Double getLongitude(DatabaseReader reader, String ip) throws Exception {
        return reader.city(InetAddress.getByName(ip)).getLocation().getLongitude();
    }

    /**
     *
     * @description: 获得纬度
     * @param reader
     * @param ip
     * @return
     * @throws Exception
     */
    public static Double getLatitude(DatabaseReader reader, String ip) throws Exception {
        return reader.city(InetAddress.getByName(ip)).getLocation().getLatitude();
    }

    /**
     *
     * @description: 获得纬度
     * @param ip
     * @return
     * @throws Exception
     */
    public static Double getLatitude(String ip) throws Exception {
        before();
        return reader.city(InetAddress.getByName(ip)).getLocation().getLatitude();
    }



    public static void main(String[] args) {
        String path = "F:\\代码模板\\java_template\\sun_utils\\src\\main\\resources\\GeoLite2\\GeoLite2-City.mmdb";
        // 创建 GeoLite2 数据库
        File database = new File(path);
        // 读取数据库内容
        try {
            DatabaseReader reader = new DatabaseReader.Builder(database).build();
            System.out.println(HttpUtils.getProvince(reader,"47.98.153.144"));
            System.out.println(HttpUtils.getCity(reader,"47.98.153.144"));
            System.out.println(HttpUtils.getLongitude(reader,"47.98.153.144"));
            System.out.println(HttpUtils.getLatitude(reader,"47.98.153.144"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
