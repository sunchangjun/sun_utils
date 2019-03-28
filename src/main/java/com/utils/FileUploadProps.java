package com.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 文件服务器工具类,封装了文件服务器的配置
 * @author shilong.zhang
 * @create_date 2015年9月2日
 */
public class FileUploadProps {

	private static FileUploadProps instance = null;
	private static Properties prb = null;
	private static final String env = System.getProperty("spring.profiles.active");
	public static final String file = "fileserver-"+env+".properties";
	
	public static final String fileServerRoot = "fileserver.rootdir";
	public static final String fileServerPrefix = "fileserver.prefix";
	
	static ClassLoader cl = FileUploadProps.class.getClassLoader();
	static {
		prb = new Properties();
		try {
			InputStream resourceAsStream = cl.getResourceAsStream(file);
			prb.load(resourceAsStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized FileUploadProps getInstance() {
		if (instance == null) {
			instance = new FileUploadProps();
		}
		return instance;
	}
	
	private FileUploadProps() {
		//检查root目录及下面的temp目录是否存在,否则预先初始化
		File serverRoot = new File(prb.get(fileServerRoot) + "/temp");
		if(!serverRoot.exists()){
			serverRoot.mkdirs();
		}
	}

	/**
	 * 返回配置文件属性值
	 * @param key
	 * @return
	 */
	public String getProperties(String key) {
		return prb.getProperty(key);
	}
	
}
