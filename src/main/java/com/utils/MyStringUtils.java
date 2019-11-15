package com.utils;

import java.math.BigDecimal;


public class MyStringUtils {

    //判断是否是数字
    public static boolean isNumeric(String str) {
        try {
            String  bigStr = new BigDecimal(str).toString();
        } catch (Exception e) {
            //异常 说明包含非数字。
            return false;
        }
        return true;
    }
    //判断是否是数字
    public static boolean isNumeri(String  str) {
        return str.matches("-?[0-9]+.*[0-9]*");
}
}
