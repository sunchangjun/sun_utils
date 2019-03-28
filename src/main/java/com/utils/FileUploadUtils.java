package com.utils;

import org.apache.commons.lang3.StringUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;
import java.util.UUID;

/**
 * 文件上传存储及路径工具类
 * @author shilong.zhang
 */
public class FileUploadUtils {

	private static FileUploadProps props = FileUploadProps.getInstance();
	private static String separator = "\\";//File.separator;

	/**
	 * 返回文件系统根目录绝对路径
	 * @return
	 */
	public static String getAbsoluteRoot() {
		String root = props.getProperties(FileUploadProps.fileServerRoot);
//		return "\\\\192.168.3.100\\qqmusic";
		return root;
	}

	/**
	 * 返回临时目录绝对路径
	 * @return
	 * @throws IOException
	 */
	public static String getAbsoluteTempDir() {
		return getAbsoluteRoot() + separator + "temp";
	}

	/**
	 * 创建要保存文件的绝对路径,如/root/category/x/t/dsk49430ffjfksfj
	 * @param category
	 * @param filename
	 * @return
	 */
	public static String createFileAbsPath(String category, String filename) {
		return getAbsoluteRoot() + getAndInitCategoryDir(category) + randomLevelFolder()
				+ separator + createUuidFilename() + getFileExtention(filename);
	}
	
	public static String createLyricAbsPath(String category,long song_id,String ext) {
		return getAbsoluteRoot() + getAndInitCategoryDir(category) + randomLevelFolder()
				+ separator + song_id+"."+ ext;
	}

	/**
	 * 返回类型的绝对路径,如果不存在则先创建,并且创建下面的分目录
	 * @param category
	 * @return
	 * @throws IOException
	 */
	private static String getAndInitCategoryDir(String category) {
		String cateAbsolute = getAbsoluteRoot() + separator + category;
		File categoryFile = new File(cateAbsolute);
		if (!categoryFile.exists()) {
			categoryFile.mkdirs();
			genLevelFolder(cateAbsolute);
		}
		return separator + category;
	}

	/**
	 * 随机产生分类category下的两个分级目录,如/a/e,/x/x,/t/s
	 * @return
	 */
	private static String randomLevelFolder() {
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
	 * 创建36位长的uuid值,用于生成上传文件名,这意味着上传文件会被改名
	 * @return
	 */
	public static String createUuidFilename() {
		String rawUuid = UUID.randomUUID().toString();
		return rawUuid.replaceAll("-", "");
	}

	/**
	 * 初始化字母排序的文件夹,从
	 * @param baseDir
	 * @throws IOException
	 */
	public static void genLevelFolder(String baseDir) {
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
	 * 绝对路径转换成http的url
	 * @param fileAbsPath
	 * @return
	 * @throws IOException
	 */
	public static String absPathToUrl(String fileAbsPath) {
		String httpPrefix = props.getProperties(FileUploadProps.fileServerPrefix);
		String path = fileAbsPath.substring(getAbsoluteRoot().length());
		if(separator.equals("\\")){//for windows
			path = path.replaceAll("\\\\", "/");
		}
		return httpPrefix + path;
	}
	
	/**
	 * 获取排行榜根目录绝对路径
	 * @return
	 */
	public static String getTopListDirPath() {
		return getAbsoluteRoot() + separator + "topList";
	}
	
	/**
	 * 获取单个排行榜文件夹绝对路径
	 * @param topName 单个排行榜名字
	 * @return
	 */
	public static String getTopDirPath(String topName) {
		return getTopListDirPath() + separator + topName;
	}
	
	/**
	 * 拷贝排行榜歌曲或者歌词到新路径中
	 * @param oldPath
	 * @param topName
	 * @param fileName
	 * @return
	 */
	public static String copyTopListFile(String oldPath, String topName, String fileName) {
		String fomatFileName = formatFileName(fileName);
		String newPath = getTopDirPath(topName) + separator + fomatFileName; 
		File oldFile = new File(oldPath);
		File newFile = new File(newPath); 
		try {
			if(!newFile.exists()) {
				System.out.println("拷贝文件到      " + newPath);
				Files.copy(oldFile.toPath(), newFile.toPath());
			}
			return newPath;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 创建文件夹
	 * @return
	 */
	public static String mkDir(String dirPath) {
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		return dir.getAbsolutePath();
	}
	
	/**
	 * 删除文件夹及文件里的所有文件
	 * @param dir 文件夹绝对路径
	 * @return
	 */
	 public static boolean deleteDir(String path) {
		File dir = new File(path);
		if(dir.exists()) {
			if (dir.isDirectory()) {
				String[] files = dir.list();
				//递归删除目录中的子目录下
				if(files != null && files.length > 0) {
					for (int i=0; i < files.length; i++) {
						boolean success = deleteDir(path + separator + files[i]);
						if (!success) {
							return false;
						}
					}
				}
			}
			// 目录此时为空，可以删除
			return dir.delete();
		}
		return false;
    }

	/**
	 * 相对路径转换成http的url
	 * @param fileRelativePath
	 * @return
	 * @throws IOException
	 */
	public static String relativePathToUrl(String fileRelativePath) {
		if(StringUtils.isBlank(fileRelativePath))
			return fileRelativePath;
		String httpPrefix = props.getProperties(FileUploadProps.fileServerPrefix);
		return httpPrefix + fileRelativePath;
	}
	
	public static String absPathToRelative(String fileAbsPath) {
		String root = props.getProperties(FileUploadProps.fileServerRoot);
		return fileAbsPath.substring(root.length());
	}
	
	
	/**
	 * 截取文件的扩展名,大写转换为小写
	 * @param filename
	 * @return 返回如 .apk,.png,.jpeg
	 */
	public static String getFileExtention(String filename) {
		if(filename == null)
			return "";
		int pos = filename.lastIndexOf(".");
		return (pos == -1) ? "" : "." + filename.substring(pos + 1, filename.length()).toLowerCase();
	}
	
	public static String getFileExtentionType(String filename) {
		if(filename == null)
			return "";
		int pos = filename.lastIndexOf(".");
		return (pos == -1) ? "" : filename.substring(pos + 1, filename.length()).toLowerCase();
	}
	
	public static String getFileServerPrefix(){
		return props.getProperties(FileUploadProps.fileServerPrefix);
	}
	
	/**
	 * 从路径中获取文件名，包括文件类型
	 * @param filename
	 * @return 返回如  hero.m4a
	 */
	public static String getFileNameFromPath(String path) {
		if(path == null)
			return "";
		int pos = path.lastIndexOf("\\");
		return (pos == -1) ? "" : path.substring(pos + 1, path.length()).toLowerCase();
	}
	
	/**
	 * 替换文件名中的特殊符号
	 * @param fileName
	 * @return
	 */
	public static String formatFileName(String fileName) {
		String newFileName = fileName.replaceAll("\\\\"," ")
				.replace("/"," ").replace("|", " ")
				.replace("\"", "“").replace("*", " ")
				.replace("?", "？").replace(":", "：");
		return newFileName;
	}
	
	/**
	 * 获取文件名称
	 * @param url
	 * @return
	 */
	public static String getFileNameFromUrl(String url) {
		// http://y.gtimg.cn/music/photo_new/T001R120x120M000000nmQ1v0JGExN.jpg
		// http://isure.stream.qqmusic.qq.com/C2000028kjnJ3SZW7O.m4a?guid=12347284&vkey=FD8012B125BCF69781527D528CD8E1C3AD1E78049EA8926F258A4BF18A7E2582A36939CD0275901F0BDA78D65773760B8361471A6463F118&uin=&fromtag=50
		int qpos = url.lastIndexOf("?");
		url = (qpos == -1) ? url : url.substring(0, qpos);
		int pos = url.lastIndexOf("/");
		return url.substring(pos + 1);
	}
	
	public static void main(String[] args) throws IOException {
//		System.out.println(createFileAbsPath("1","1"));
		// System.out.println(absPathToUrl("d:/file/sns/i/f/5a093ccc0fe54bf0b16d3f09976b0373"));
		// FileServiceImpl imp = new FileServiceImpl();
		// System.out.println(imp.moveTemporaryFile("5a093ccc0fe54bf0b16d3f09976b0373",
		// "sns"));
//		System.out.println(getFileExtention("/tuotuoyi/tuotuoyi2/chatfiles/5ec2de40-a628-11e5-8e64-2fe6976c7b66"));
	}

}
