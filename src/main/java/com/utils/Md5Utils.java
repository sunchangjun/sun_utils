package com.utils;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 工具类: MD5加密
 * @author zhangsl
 */
public class Md5Utils {

	/**
	 * MD5加密
	 * @param str
	 * @return
	 */
	public static String encode(String origin) {
		StringBuffer sb = new StringBuffer();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(origin.getBytes("utf8"));
			byte[] result = md.digest();
			for (int i = 0; i < result.length; i++) {
				int val = (result[i] & 0xFF) | 0xFFFFFF00;
				sb.append(Integer.toHexString(val).substring(6));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public static void main(String[] args){
		System.out.println(encode("123456"));
	}
	
	/**
	 * 根据传入文件计算文件的md5值
	 * @param file
	 * @return
	 */
    public static String getFileMD5(File file) {  
        if (!file.isFile()) {  
            return null;  
        }  
        MessageDigest digest = null;  
        FileInputStream in = null;  
        byte buffer[] = new byte[1024];  
        int len;  
        try {  
            digest = MessageDigest.getInstance("MD5");  
            in = new FileInputStream(file);  
            while ((len = in.read(buffer, 0, 1024)) != -1) {  
                digest.update(buffer, 0, len);  
            }  
            in.close();  
  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        BigInteger bigInt = new BigInteger(1, digest.digest());  
        return bigInt.toString(16);  
    } 
    
    /**
     * 根据文件输入流计算文件的md5值
     * @param fileStream
     * @return
     */
    public static String getFileMD5ByInputStream(InputStream fileStream){
    	MessageDigest digest = null; 
        byte buffer[] = new byte[1024];  
        int len; 
        try {
            digest = MessageDigest.getInstance("MD5");  
            while ((len = fileStream.read(buffer, 0, 1024)) != -1) {  
                digest.update(buffer, 0, len);  
            }  
            fileStream.close();
		} catch (Exception e) {
			// TODO: handle exception
			 e.printStackTrace(); 
		}
        BigInteger bigInt = new BigInteger(1, digest.digest());  
        return bigInt.toString(16); 
    }
    
    
	
}
