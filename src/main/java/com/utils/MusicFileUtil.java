package com.utils;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

/**
 * 新版文件服务器存储工具,不依赖于环境变量
 * @author DELL
 */
public class MusicFileUtil {
	
	private static String separator = File.separator;

	public enum FileExt {
		mp4,
		ts;
		
		public static FileExt getType(String type) {
	        for (FileExt ext : values()) {
	            if (ext.name().equals(type)){
	                return ext;
	            }
	        }
	        return null;
	    }
	}
	
	/**
	 * 创建要保存文件的绝对路径
	 * 如/root/category/x/t/dsk49430ffjfksfj.png
	 * @param prefix 文件的根路径
	 * @param fileExt 文件的扩展类型
	 * @return
	 */
	public static String createFileAbsPath(String prefix, FileExt fileExt) {
		if(prefix.endsWith("/")|| prefix.endsWith("\\")){
			prefix = prefix.substring(0, prefix.length()-1);
			System.out.println("prefix===>"+prefix);
		}
		checkPrefix(prefix);
		if(fileExt == null){
			return prefix + getRandomPath() + createUuidFilename();
		}else{
			return prefix + getRandomPath() + createUuidFilename() + "." + fileExt.name();
		}
	}
	
	/**
	 * 检查根路径下的子目录
	 * @param prefix
	 */
	private static void checkPrefix(String prefix) {
		File root = new File(prefix);
		if (!root.exists()) {
			root.mkdirs();
		}
		if(new File(prefix+separator+"a").exists() && new File(prefix+separator+"z").exists()){
			//do nothing
		}else{
			createLevelDirs(prefix);
		}
	}
	
	/**
	 * 随机产生分类category下的两个分级目录,如/a/e,/x/x,/t/s
	 * @return
	 */
	private static String getRandomPath() {
		Random random = new Random();
		char c1 = (char) (97 + random.nextInt(26));
		char c2 = (char) (97 + random.nextInt(26));
		StringBuffer sb = new StringBuffer();
		sb.append(separator);
		sb.append(c1);
		sb.append(separator);
		sb.append(c2);
		return sb.toString();
	}
	
	/**
	 * 初始化字母排序的文件夹,从
	 * @param baseDir
	 * @throws IOException
	 */
	private static void createLevelDirs(String baseDir) {
		char c1 = 'a';
		for (int i = 0; i < 26; i++) {
			char c2 = 'a';
			for (int j = 0; j < 26; j++) {
				StringBuffer sb = new StringBuffer();
				sb.append(separator);
				sb.append(c1);
				sb.append(separator);
				sb.append(c2);
				new File(baseDir + sb.toString()).mkdirs();
				int ascii = (int) c2;
				c2 = (char) (ascii + 1);
			}
			int ascii = (int) c1;
			c1 = (char) (ascii + 1);// a to b
		}
	}
	
	/**
	 * 创建36位长的uuid值,用于生成上传文件名,这意味着上传文件会被改名
	 * @return
	 */
	private static String createUuidFilename() {
		String rawUuid = UUID.randomUUID().toString();
		return separator + rawUuid.replaceAll("-", "");
	}
	
	public static void main(String[] args) throws Exception {
		String str = MusicFileUtil.createFileAbsPath("\\\\192.168.3.241\\ts\\mv\\", FileExt.ts);
		System.out.println(str);
	}

}
