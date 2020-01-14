package com.utils;

/**
 * @author ：suncj
 * @date ：2019/11/27 19:01
 */


        import java.io.IOException;
        import java.io.UnsupportedEncodingException;
        import java.net.URLEncoder;

        import javax.crypto.Cipher;
        import javax.crypto.SecretKey;
        import javax.crypto.spec.SecretKeySpec;

        import sun.misc.BASE64Decoder;
        import sun.misc.BASE64Encoder;

public class SddxDES3Util {

    //定义加密算法，有DES、DESede(即3DES)、Blowfish
    private static final String Algorithm = "DESede";
    private static final String PASSWORD_CRYPT_KEY = "123457000000000000000000";

    public static void main(String[] args) throws IOException {

        String m = "12321312321321aaaaabbbbb";
        System.out.println(m);

        // 1. 加密
        // 1.1 加密字符串
        byte[] e = encryptMode(m.getBytes());

        // 1.2 BASE64Encoder得到加密后的字符串
        BASE64Encoder enc = new BASE64Encoder();
        String eS = enc.encode(e);
        System.out.println(eS);

        // 1.3. URLEncoder
        String em = URLEncoder.encode(eS, "gbk");
        System.out.println(em);


        // 2. 解密
        // 2.1 BASE64Decoder加密后的字符串
        BASE64Decoder dec=new BASE64Decoder();
        byte[] d = dec.decodeBuffer(eS);

        // 2.2 解密字符串
        String ds = new String(decryptMode(d));
        System.out.println(ds);


    }


    /**
     * 加密方法
     * @param src 源数据的字节数组
     * @return
     */
    public static byte[] encryptMode(byte[] src) {
        try {
            SecretKey deskey = new SecretKeySpec(build3DesKey(PASSWORD_CRYPT_KEY), Algorithm);    //生成密钥
            Cipher c1 = Cipher.getInstance(Algorithm);    //实例化负责加密/解密的Cipher工具类
            c1.init(Cipher.ENCRYPT_MODE, deskey);    //初始化为加密模式
            return c1.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (java.lang.Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }


    /**
     * 解密函数
     * @param src 密文的字节数组
     * @return
     */
    public static byte[] decryptMode(byte[] src) {
        try {
            SecretKey deskey = new SecretKeySpec(build3DesKey(PASSWORD_CRYPT_KEY), Algorithm);
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.DECRYPT_MODE, deskey);    //初始化为解密模式
            return c1.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (java.lang.Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }


    /*
     * 根据字符串生成密钥字节数组
     * @param keyStr 密钥字符串
     * @return
     * @throws UnsupportedEncodingException
     */
    public static byte[] build3DesKey(String keyStr) throws UnsupportedEncodingException{
        byte[] key = new byte[24];    //声明一个24位的字节数组，默认里面都是0
        byte[] temp = keyStr.getBytes("UTF-8");    //将字符串转成字节数组

        /*
         * 执行数组拷贝
         * System.arraycopy(源数组，从源数组哪里开始拷贝，目标数组，拷贝多少位)
         */
        if(key.length > temp.length){
            //如果temp不够24位，则拷贝temp数组整个长度的内容到key数组中
            System.arraycopy(temp, 0, key, 0, temp.length);
        }else{
            //如果temp大于24位，则拷贝temp数组24个长度的内容到key数组中
            System.arraycopy(temp, 0, key, 0, key.length);
        }
        return key;
    }
}


