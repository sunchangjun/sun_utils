package com.file.images;

import com.utils.DownImageUtils;
import org.junit.Test;

public class ImagesUtilsTest {
    @Test
    public void test(){
      boolean bool=  DownImageUtils.getRource("http://y.gtimg.cn/music/photo_new/T002R500x500M0000020I7sO0ayXhN.jpg");
      System.out.println(bool);
    }

}
