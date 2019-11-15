
package com.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @author sunchangjunn 2018年11月19日下午4:38:24
 */
public class HttpURLConnectionUtil {
	public static String sendPostRequest(String url, String param) {
		HttpURLConnection httpURLConnection = null;
		OutputStream out = null; // 写
		InputStream in = null; // 读
		int responseCode = 0; // 远程主机响应的HTTP状态码
		String result = "";
		try {
			URL sendUrl = new URL(url);
			httpURLConnection = (HttpURLConnection) sendUrl.openConnection();
			// post方式请求
			httpURLConnection.setRequestMethod("POST");
			// 设置头部信息
			httpURLConnection.setRequestProperty("headerdata", "ceshiyongde");
			// 一定要设置 Content-Type 要不然服务端接收不到参数
			httpURLConnection.setRequestProperty("Content-Type", "application/Json; charset=UTF-8");
			// 指示应用程序要将数据写入URL连接,其值默认为false（是否传参）
			httpURLConnection.setDoOutput(true);
			// httpURLConnection.setDoInput(true);

			httpURLConnection.setUseCaches(false);
			httpURLConnection.setConnectTimeout(30000); // 30秒连接超时
			httpURLConnection.setReadTimeout(30000); // 30秒读取超时
			// 获取输出流
			out = httpURLConnection.getOutputStream();
			// 输出流里写入POST参数
			out.write(param.getBytes());
			out.flush();
			out.close();
			responseCode = httpURLConnection.getResponseCode();
			BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
			result = br.readLine();
		} catch (Exception e) {
			e.printStackTrace();

		}
		return result;
	}

	public static void readContentFromPost() throws IOException {
		// Post请求的url，与get不同的是不需要带参数
		URL postUrl = new URL("http://www.xxxxxxx.com");
		// 打开连接
		HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
		// 设置是否向connection输出，因为这个是post请求，参数要放在
		// http正文内，因此需要设为true
		connection.setDoOutput(true);
		// Read from the connection. Default is true.
		connection.setDoInput(true);
		// 默认是 GET方式
		connection.setRequestMethod("POST");
		// Post 请求不能使用缓存
		connection.setUseCaches(false);
		// 设置本次连接是否自动重定向
		connection.setInstanceFollowRedirects(true);
		// 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
		// 意思是正文是urlencoded编码过的form参数
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		// 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
		// 要注意的是connection.getOutputStream会隐含的进行connect。
		connection.connect();
		DataOutputStream out = new DataOutputStream(connection.getOutputStream());
		// 正文，正文内容其实跟get的URL中 '? '后的参数字符串一致
		String content = "字段名=" + URLEncoder.encode("字符串值", "编码");
		// DataOutputStream.writeBytes将字符串中的16位的unicode字符以8位的字符形式写到流里面
		out.writeBytes(content);
		// 流用完记得关
		out.flush();
		out.close();
		// 获取响应
		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		String line;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
		}
		reader.close();
		// 该干的都干完了,记得把连接断了
		connection.disconnect();
	}
	
	
	public static String sendGetRequest(String path){
        BufferedReader in = null;        
        StringBuilder result = new StringBuilder(); 
        try {
            //GET请求直接在链接后面拼上请求参数
  
            URL url = new URL(path);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            //Get请求不需要DoOutPut
            conn.setDoOutput(false);
            conn.setDoInput(true);
            //设置连接超时时间和读取超时时间
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //连接服务器  
            conn.connect();  
            // 取得输入流，并使用Reader读取  
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //关闭输入流
        finally{
            try{
                if(in!=null){
                    in.close();
                }
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        }
        return result.toString();
    }


}