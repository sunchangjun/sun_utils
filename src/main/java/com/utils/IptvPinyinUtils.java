package com.utils;

import com.github.stuxuhai.jpinyin.ChineseHelper;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IptvPinyinUtils {
	private static StringBuilder stringBuilder;
	/**
     * 转换为有声调的拼音字符串
     * @param pinYinStr 汉字
     * @return 有声调的拼音字符串
     */
    public static String changeToMarkPinYin(String pinYinStr){

        String tempStr = null;

        try {
            tempStr =  PinyinHelper.convertToPinyinString(pinYinStr,  " ", PinyinFormat.WITH_TONE_MARK);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempStr;
    }


    /**
     * 转换为数字声调字符串
     * @param pinYinStr 需转换的汉字
     * @return 转换完成的拼音字符串
     */
    public static String changeToNumberPinYin(String pinYinStr){

        String tempStr = null;

        try {
            tempStr = PinyinHelper.convertToPinyinString(pinYinStr, " ", PinyinFormat.WITH_TONE_NUMBER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tempStr;

    }

    /**
     * 转换为不带音调的拼音字符串
     * @param pinYinStr 需转换的汉字
     * @return 拼音字符串
     */
    public static String changeToTonePinYin(String pinYinStr){
    	if(stringBuilder == null) {
    		stringBuilder = new StringBuilder();
    	} else {
    		stringBuilder.delete(0, stringBuilder.length());
    	}
    	char[] charArray = pinYinStr.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			if(ChineseHelper.isChinese(charArray[i])) {
				String[] chStr = PinyinHelper.convertToPinyinArray(charArray[i], PinyinFormat.WITHOUT_TONE);
				
				if(stringBuilder.length() != 0) {
					stringBuilder.append(" ").append(chStr[0]);
				} else {
					stringBuilder.append(chStr[0]);
				}
				
				if(i + 1 < charArray.length && !ChineseHelper.isChinese(charArray[i + 1])) {
					stringBuilder.append(" ");
				}

			} else {
				stringBuilder.append(charArray[i]);
			}
		} 
		return stringBuilder.toString();
    }
    
    /**
     * 简体转换为繁体
     * @param pinYinStr
     * @return
     */
    public static String changeToTraditional(String pinYinStr){
        String tempStr = null;
        try {
            tempStr = ChineseHelper.convertToTraditionalChinese(pinYinStr);
        } catch (Exception e){
            e.printStackTrace();
        }
        return tempStr;
    }

    /**
     * 繁体转换为简体
     * @param pinYinSt
     * @return
     */
    public static String changeToSimplified(String pinYinSt){
        String tempStr = null;
        try {
            tempStr = ChineseHelper.convertToSimplifiedChinese(pinYinSt);
        } catch (Exception e){
            e.printStackTrace();
        }
        return tempStr;
    }
    
    /**
     * 是否包含除中英文特殊符号以外的字符
     * @param str
     * @return
     */
    public static boolean findNotEngChiString(String str) {
    	Pattern p = Pattern.compile("[^\u4e00-\u9fa5a-zA-z ！!*?？,，\"“”|、：:\\\\//$-_——()]");
        Matcher m = p.matcher(str);
        if (m.find()) {
        	return true;
        } else {
        	return false;
        }
    }
    
    public static String unicode2String(String unicode) {

        StringBuffer string = new StringBuffer();

        String[] hex = unicode.split("\\\\u");

        for (int i = 1; i < hex.length; i++) {

            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);

            // 追加成string
            string.append((char) data);
        }

        return string.toString();
    }
    
    /**
     * 检查汉字是否为多音字
     * @param c 需检查的汉字
     * @return true 多音字，false 不是多音字
     */
    public static boolean checkPinYin(char c){
        boolean check  = false;
        try {
            check = PinyinHelper.hasMultiPinyin(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return check;
    }
    
    /**
     * 转换为每个汉字对应拼音首字母字符串
     * @param src 源字符串
     * @return 拼音首字母字符串
     */
    public static String getShortPinYin(String src){
        try {
            return PinyinHelper.getShortPinyin(src);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 是否字符是中文
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
    	return String.valueOf(c).matches("[\u4e00-\u9fa5]");
    }
    
    private static final Map<String, String> map = new HashMap<String,String>();
    static{
    	map.put("给", "G");
    }
    
    public static void main(String[] args) {
    	System.out.println(IptvPinyinUtils.getShortPinYin("Sula 与 Lampa的寓言"));
    	System.out.println(IptvPinyinUtils.getShortPinYin("给予朋友的信"));
    	System.out.println(IptvPinyinUtils.getShortPinYin("朝阳"));
    	System.out.println(IptvPinyinUtils.checkPinYin('给'));
    	System.out.println(IptvPinyinUtils.getShortPinYin("吓！讲乜野话？"));
	}
    
}
