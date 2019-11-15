package com.string;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class checkStringTest {

//    @Test
    public void test(){
        String string="https://www.jianshu.com/p/c4dce1a85a0e";
       System.out.println(string.startsWith("http")); ;

    }
    @Test
    public  void   test1(){
        String string="https://www.jianshu.com/p/c4dce1a85a0e";
       boolean boo= StringUtils.containsAny(string,"x","123","4","","");
        System.out.println(boo);
    }


}
