package com.utils.okhttp;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSONObject;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class OkHttp3Util {

    private static Logger log = LoggerFactory.getLogger(OkHttp3Util.class);
    private static final MediaType MEDIA_TYPE_TEXT = MediaType.parse("text/plain");
    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private final static int CONNECT_TIMEOUT =1000;
    public final static int READ_TIMEOUT=1000;
    public final static int WRITE_TIMEOUT=2000;
    private static final MediaType CONTENT_TYPE = MediaType.parse("application/xml;charset=utf-8");
    // 设置编码
    // private static final String CHARSET_NAME = "UTF-8";

    // 得到OkHttp的客户端
    private static OkHttpClient client = new OkHttpClient();

    // 设置相关的时间参数,ssl证书
    static {
        client = new OkHttpClient().newBuilder().connectTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                // .sslSocketFactory(new XZTLSSocketFactory())
                .build();
    }



    /**
     *
     * 【get请求】
     *
     * @author sunchangjunn 2018年3月7日
     * @param url
     * @return
     */
    public static String  get(String url) {
        Request request = new Request.Builder().url(url.trim()).addHeader("type_", "1").get().build();
        Response response = null;
        String responseUrl="";
        try {
            response = client.newCall(request).execute();
            responseUrl = response.body().string();

        } catch (IOException e) {

        }
        return responseUrl;


    }
    /**
     * 有返回值
     * @param url
     * @param param
     * @return
     * @throws Exception
     */
    public static String httpPostFormReturnString(String url, Map<String, String> param) throws Exception {
        if (param == null || param.size() == 0) {
            throw new Exception("参数非法");
        }


        // 处理接受的参数
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : param.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        RequestBody formBody = builder.build();
        // HTTP头设置
        Request request = new Request.Builder().url(url).header("Accept", "application/json")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", "some secret token").addHeader("type_", "1").post(formBody)
                .build();
        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            String responseUrl = response.body().string();
            return responseUrl;
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }




    public static String post(String url,String content) {
        RequestBody formBody = RequestBody.create(CONTENT_TYPE, content);
        Request request = new Request.Builder().url(url.trim()).addHeader("type_", "1").post(formBody).build();
        Response response = null;
        try {

            response = client.newCall(request).execute();
        } catch (IOException e) {
            log.error("http调用失败}");
        }
        String respnoseBody = null;
        if (response.isSuccessful()) {
            try {
                respnoseBody = response.body().string();
                log.info(JSONObject.toJSONString(respnoseBody));
            } catch (IOException e) {
                System.out.println("http调用失败");
            }
        } else {
            log.error("http调用失败:code={},message={}");
        }
        return respnoseBody;

    }












}
