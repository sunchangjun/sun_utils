package com.utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.*;

/**
 * Alex Tang 2018年3月29日 desc:HttpUrlConnectionHelper
 */
public class UrlConnectionHelper {

	private static final int CONNECTION_TIMEOUT = 5000; // 建立连接超时时间 5s
	private static final int READ_TIMEOUT = 5000; // 数据传输超时时间 5s
	private static ExecutorService executor = Executors.newFixedThreadPool(1);
	private static final String newLine = "\r\n";// 换行符
	private static final String boundaryPrefix = "--"; // 分割线
	private static String BOUNDARY = "========7d4a6d158c9"; // 分隔符
	private static final Logger log = LoggerFactory.getLogger(UrlConnectionHelper.class);
	
	public static String requestGet(String strUrl) throws Exception {
		String result = null;
		URL url = new URL(strUrl);// 新建一个URL对象
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();// 打开一个HttpURLConnection连接
		urlConn.setConnectTimeout(CONNECTION_TIMEOUT);// 设置连接主机超时时间
		urlConn.setReadTimeout(READ_TIMEOUT);// 设置从主机读取数据超时
		urlConn.setUseCaches(true);// 设置是否使用缓存 默认是true
		urlConn.setRequestMethod("GET");// 设置为Post请求
		setConn(urlConn);// urlConn设置请求头信息
		urlConn.connect();// 开始连接
		if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {// 判断请求是否成功
			result = streamToString(urlConn.getInputStream());// 获取返回的数据
		} else {
			throw new Exception("HttpCode:" + urlConn.getResponseCode());
		}
		urlConn.disconnect();// 关闭连接
		return result;
	}

	public static String requestPost(String strUrl, String bodys)
			throws Exception {
		String result = null;
		// 1. 获取访问地址URL
		URL url = new URL(strUrl);
		// 2. 创建HttpURLConnection对象
		HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
		/* 3. 设置请求参数等 */
		// 请求方式
		urlConn.setRequestMethod("POST");
		// 设置连接主机超时时间
		urlConn.setConnectTimeout(CONNECTION_TIMEOUT);
		// 设置从主机读取数据超时
		urlConn.setReadTimeout(READ_TIMEOUT);
		// urlConn设置请求头信息
		setConn(urlConn);
		// 设置是否输出
		urlConn.setDoOutput(true);
		// 设置是否读入
		urlConn.setDoInput(true);
		// 设置是否使用缓存
		urlConn.setUseCaches(false);
		// 设置此 HttpURLConnection 实例是否应该自动执行 HTTP 重定向
		urlConn.setInstanceFollowRedirects(true);
		// 设置使用标准编码格式编码参数的名-值对
		// urlConn.setRequestProperty("Content-Type","application/json");
		// 连接
		urlConn.connect();
		/* 4. 处理输入输出 */
		// 写入参数到请求中
		OutputStream out = urlConn.getOutputStream();
		out.write(bodys.getBytes());
		out.flush();
		out.close();
		// 判断请求是否成功
		if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			// 获取返回的数据
			result = streamToString(urlConn.getInputStream());
		} else {
			throw new Exception("HttpCode:" + urlConn.getResponseCode());
		}
		// 5. 断开连接
		urlConn.disconnect();
		return result;
	}

	/**
	 * 文件上传
	 * 
	 * @param strUrl
	 * @param file
	 */
	public static String uploadFile(String strUrl, List<File> files)
			throws Exception {
		// 服务器的域名
		URL url = new URL(strUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// 设置为POST情
		conn.setRequestMethod("POST");
		// 发送POST请求必须设置如下两行
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		// 设置请求头参数
		conn.setRequestProperty("connection", "Keep-Alive");
		conn.setRequestProperty("Charsert", "UTF-8");
		conn.setRequestProperty("Content-Type",
				"multipart/form-data; boundary=" + BOUNDARY);
		OutputStream out = new DataOutputStream(conn.getOutputStream());
		// 设置文件
		for (File file : files) {
			setMultipartFiles(file, out);
		}

		// 定义最后数据分隔线，即--加上BOUNDARY再加上--。
		byte[] end_data = (newLine + boundaryPrefix + BOUNDARY + boundaryPrefix + newLine)
				.getBytes();
		// 写上结尾标识
		out.write(end_data);
		out.flush();
		out.close();

		// 定义BufferedReader输入流来读取URL的响应
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String result = "";
		String line = null;
		while ((line = reader.readLine()) != null) {
			if (line != null) {
				result += line;
			}
		}
		return result;
	}

	/**
	 * 设置upload文件
	 * 
	 * @param file
	 * @param out
	 * @throws Exception
	 */
	private static void setMultipartFiles(File file, OutputStream out)
			throws Exception {
		// 上传文件
		StringBuilder sb = new StringBuilder();
		sb.append(boundaryPrefix);
		sb.append(BOUNDARY);
		sb.append(newLine);
		// 文件参数,photo参数名可以随意修改
		sb.append("Content-Disposition: form-data;name=\"photo\";filename=\""
				+ file.getName() + "\"" + newLine);
		sb.append("Content-Type:application/octet-stream");
		// 参数头设置完以后需要两个换行，然后才是参数内容
		sb.append(newLine);
		sb.append(newLine);

		// 将参数头的数据写入到输出流中
		out.write(sb.toString().getBytes());

		// 数据输入流,用于读取文件数据
		DataInputStream in = new DataInputStream(new FileInputStream(file));
		byte[] bufferOut = new byte[1024];
		int bytes = 0;
		// 每次读1KB数据,并且将文件数据写入到输出流中
		while ((bytes = in.read(bufferOut)) != -1) {
			out.write(bufferOut, 0, bytes);
		}
		// 最后添加换行
		out.write(newLine.getBytes());
		in.close();
	}

	/**
	 * 未下载完成的如果抛出异常则会删除临时文件
	 * @param address
	 * @param saveFile
	 * @return
	 * @throws Exception
	 */
	public static int httpDownload(String address, String saveFile)
			throws Exception {
		long start = System.currentTimeMillis();
		FileOutputStream fs = new FileOutputStream(saveFile);
		InputStream is = null;
		int total = 0;
		try {
			URL url = new URL(address);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			setConn(conn);
			conn.setConnectTimeout(10 * 1000);
			conn.setReadTimeout(15 * 1000);
			conn.connect();
			is = conn.getInputStream();
			byte[] buffer = new byte[1024 * 1024 * 10];
			DownloadCallable callable = new DownloadCallable(is, buffer);
			while (true) {
				Future<Integer> future = executor.submit(callable);
				int byteread = future.get(10, TimeUnit.SECONDS);// 10秒收不到数据,则判断为卡住了,退出
				if (byteread != -1) {
					fs.write(buffer, 0, byteread);
					fs.flush();
					total += byteread;
				} else {
					break;
				}
			}
			is.close();
			fs.close();
		} catch (Exception e) {
			if (is != null)
				is.close();
			fs.close();
			File file = new File(saveFile);
			if (file.exists()) {// 如果有未下载完成的文件就删除他
				file.delete();
			}
			throw e;
			//throw new Exception("文件下载失败");
		}
		long end = System.currentTimeMillis();
		// System.out.println("开始"+start+"  结束："+end);
		int actual = (int) (end - start);// 实际秒数
		// System.out.println("实际毫秒数:"+actual);
		// System.out.println(total);
		int expect = (total * 1000) / (500 * 1024);// 期望毫数
		// System.out.println("期望时间："+expect);
		return (expect - actual) >= 0 ? (expect - actual) : 0;
	}

	/**
	 * 带百分比输出的下载
	 * @param address
	 * @param filePath
	 * @param size
	 * @return
	 * @throws Exception
	 */
	public static long downloadPercentOld(String address, String filePath, long size) throws Exception {
		long start = System.currentTimeMillis();
		FileOutputStream fs = new FileOutputStream(filePath);
		InputStream is = null;
		long total = 0;
		try {
			URL url = new URL(address);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			setConn(conn);
			conn.setConnectTimeout(30 * 1000);
			conn.setReadTimeout(60 * 1000);
			conn.connect();
			is = conn.getInputStream();
			byte[] buffer = new byte[1024 * 1024 * 10];
			DownloadCallable callable = new DownloadCallable(is, buffer);
			if(size==0){
				size = conn.getContentLengthLong();
			}
			int old_pct = 0;//百分比
			while (true) {
				Future<Integer> future = executor.submit(callable);
				int byteread = future.get(10, TimeUnit.SECONDS);// 10秒收不到数据,则判断为卡住了,退出
				if (byteread != -1) {
					fs.write(buffer, 0, byteread);
					fs.flush();
					total += byteread;
					int now_pct = (int)((total*100)/size);
					if((now_pct-old_pct)>4){
						old_pct = now_pct;
						System.out.print(old_pct+"% ");
					}
				} else {
					break;
				}
			}
			System.out.println("done!");
			is.close();
			fs.close();
		}catch(FileNotFoundException e){//文件不存在的异常
			if (is != null)
				is.close();
			fs.close();
			File file = new File(filePath);
			if (file.exists()) {// 如果有未下载完成的文件就删除他
				file.delete();
			}
			throw e;
		}catch (Exception e) {
			e.printStackTrace();
			if (is != null)
				is.close();
			fs.close();
			File file = new File(filePath);
			if (file.exists()) {// 如果有未下载完成的文件就删除他
				file.delete();
			}
			throw e;
		}
		long end = System.currentTimeMillis();
		return end-start;
	}
	
	/**
	 * 带百分比输出的下载
	 * @param address
	 * @param filePath
	 * @param size
	 * @throws Exception
	 */
	public static void downloadPercent(String address, String filePath, long size) throws Exception {
		FileOutputStream fs = new FileOutputStream(filePath);
		InputStream is = null;
		long total = 0;
		try {
			URL url = new URL(address);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			setConn(conn);
			conn.setConnectTimeout(10 * 1000);
			conn.setReadTimeout(15 * 1000);
			conn.connect();
			is = conn.getInputStream();
			byte[] buffer = new byte[1024 * 1024 * 10];
			if(size==0){
				size = conn.getContentLengthLong();
			}
			int old_pct = 0;//百分比
			int byteread = 0;
			while ((byteread=is.read(buffer))!=-1) {
				fs.write(buffer, 0, byteread);
				fs.flush();
				total += byteread;
				int now_pct = (int)((total*100)/size);
				if((now_pct-old_pct)>4){
					old_pct = now_pct;
					System.out.print(old_pct+"% ");
				}
			}
			System.out.println("done!");
		}catch(FileNotFoundException fe){//文件不存在的异常
			deleteFile(filePath);
			throw fe;
		}catch(SocketTimeoutException se){
			deleteFile(filePath);
			Thread.sleep(10000);//网络异常,睡眠10秒
			throw se;
		}catch(Exception e){
			e.printStackTrace();
			deleteFile(filePath);
			throw e;
		}finally{
			if (is != null){
				try{
					is.close();
				}catch(Exception e){}
			}
			try{
				fs.close();
			}catch(Exception e){}
		}
	}
	
	private static void deleteFile(String filePath){
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
	}
	
	private static class DownloadCallable implements Callable<Integer> {
		private InputStream is;
		private byte[] buffer;
		public DownloadCallable(InputStream is, byte[] buffer) {
			this.is = is;
			this.buffer = buffer;
		}
		@Override
		public Integer call() throws Exception {
			return is.read(buffer);
		}
	}

	/*
	 * 将输入流转换成字符串
	 * @param is 从网络获取的输入流
	 * @return
	 */
	public static String streamToString(InputStream is) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			baos.close();
			is.close();
			byte[] byteArray = baos.toByteArray();
			return new String(byteArray);
		} catch (Exception e) {
			return null;
		}
	}
	
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
			String type = DownImageUtils.getPicType(bs);
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
	
	public static String getHttpFile(String url, String path) throws Exception {
		InputStream is = null;
		FileOutputStream fos = new FileOutputStream(path);
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
			while ((len=is.read(b))!=-1) {
				fos.write(b, 0, len);
				fos.flush();
				total += len;
				int now_pct = (int)((total*100)/size);
				if((now_pct-old_pct)>4){
					old_pct = now_pct;
					System.out.print(old_pct+"% ");
				}
			}
			long dur = System.currentTimeMillis()-start;
			System.out.println("done with:"+dur/1000+"秒, 文件大小:"+total/1024+"kb");
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
			try{
				fos.close();
			}catch(Exception e){}
		}
	}

	/**
	 * 设置头文件
	 */
	private static void setConn(HttpURLConnection conn) {
		conn.setRequestProperty("User-agent","Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.8.1.11) Gecko/20071127 Firefox/2.0.0.11 wthx");
		conn.setRequestProperty("Accept","text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
		conn.setRequestProperty("Accept-Language", "zh-cn,zh;q=0.9,en;q=0.7");
		conn.setRequestProperty("Accept-Charset","gb2312,utf-8;q=0.7,*;q=0.7");
		conn.setRequestProperty("Keep-Alive", "300");
		conn.setRequestProperty("Connection", "keep-alive");
	}

	public static void main(String args[]) throws Exception {
		String url = "http://118.112.10.148/vcloud1049.tc.qq.com/1049_M0106016004Hdx5a1KmwaI1001600134.f40.mp4?vkey=D8867C16C27D3EAFA7898A340A84A06C48C50BC0072A2A0D5325ECF54D96F693C26C813550A28FB24B34514592E379C6B6CBBF13BF8F1AAE1F54C5CDF13796EE52B0BFC9ECBBBDA58E8BC6A51A2263072B48720AB8D0145D&stdfrom=0";
		UrlConnectionHelper.getHttpFile(url, "D:/fff.png");
	}
}
