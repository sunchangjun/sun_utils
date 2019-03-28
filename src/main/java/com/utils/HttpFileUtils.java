package com.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.file.Files;

public class HttpFileUtils {
    private static final Logger log = LoggerFactory.getLogger(HttpFileUtils.class);

    public static String downloadMvFilePartlyNew(String mvPrefix, String url, long size){
        if (size == 0 || StringUtils.isBlank(url)){
            return null;
        }
        log.info("开始下载MV,URL:"+url);
        String t = "d:/part-download/";
        File tf = new File(t);
        if(!tf.exists()){
            tf.mkdirs();
        }
        MultiThreadDownLoad load = new MultiThreadDownLoad(url, 4, t);
        String localfile = load.downloadPart();
        File src = new File(localfile);
        String fileName = FileUploadUtils.getFileNameFromUrl(url);
        String fileExt = FileUploadUtils.getFileExtentionType(fileName);
        String path = MusicFileUtil.createFileAbsPath(mvPrefix, MusicFileUtil.FileExt.getType(fileExt));
        File dst = new File(path);
        try {
            Files.copy(src.toPath(), dst.toPath());
            log.info("文件保存到:"+path);
            boolean result = false;
            int count=1;
            while(!result){//删除文件
                result = src.delete();
                if(!result){
                    log.info("第"+count+"次删除文件:"+localfile+" 结果:"+result);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
                if(count == 10) {
                    log.info("删除10次未成功,进入死循环,放弃删除:"+path);
                    result=true;
                }
                count++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return path;
    }

    public static String downloadMvImageNew(String prefix, String url){
        if (StringUtils.isBlank(url)){
            return null;
        }
        String fileName = FileUploadUtils.getFileNameFromUrl(url);
        String fileExt = FileUploadUtils.getFileExtentionType(fileName);
        String path = MusicFileUtil.createFileAbsPath(prefix, MusicFileUtil.FileExt.getType(fileExt));
        for(int i=0; i<3; i++){
            try{
                path = UrlConnectionHelper.getHttpImage(url, path);
            }catch(FileNotFoundException fe){//文件不存在的异常,不用下载了,返回null
                path = null;
            }catch(SocketTimeoutException te){//超时异常发生,往往是网络出了故障,需要重下载
                if(i<2){
                    log.info("文件:"+url+"下载超时,开始重试下载");
                    continue;
                }else if(i==2){//马上就要退出了,已经使用了3次下载机会
                    path = null;
                }
            }catch(Exception e){//未知异常,这种按照不下载处理
                log.warn("发现未知异常请代码处理!");
                e.printStackTrace();
                path = null;
            }
            break;
        }
        return path;
    }
}
