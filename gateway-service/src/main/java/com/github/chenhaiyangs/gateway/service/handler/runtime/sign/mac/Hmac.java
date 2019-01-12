package com.github.chenhaiyangs.gateway.service.handler.runtime.sign.mac;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

/**
 * 请求签名工具类
 * @author chenhaiyang
 */
@Slf4j
public class Hmac {
    /**
     * mac key的长度要求为40
     */
    private static final int LENGTH=40;
    /**
     * 验签结果字段
     */
    private static final String SIGN="sign";
    /**
     * 时间戳字段
     */
    private static final String TS="ts";
    /**
     * 验签方式
     */
    private static final String VERTIFY_FUNCTION="HmacSHA1";
    /**
     * 用于生成新的macKey的密钥，这里定死一个默认的密钥
     */
    private static final String MAIN_KEY = "C08C844BC4634DB446C53924290E43FA";

    /**
     * 验证参数类型
     */
    public enum VertifyType{
        /**
         * 验证通过
         */
        PASSED(0),
        /**
         * 长度不足
         */
        LENGTH_NOT_ENOUGH(1),
        /**
         * 缺少时间戳
         */
        TS_LOSSED(2),
        /**
         * 缺少签名结果
         */
        NO_HMAC(3),
        /**
         * 验证不通过
         */
        NOT_PASSED(4),
        /**
         * 传递的javaBean为空
         */
        NO_BEAN(5);

        @Getter
        private int value;

        VertifyType(int value) {
            this.value = value;
        }
    }

    /**
     * 验证报文Hmac
     * @param macKey macKey
     * @param dictParam 验签的目标Map
     * @param hmac 客户端参加签结果
     * @return 返回结果
     */
    public static int verifyHmac(String macKey, Map<String,String> dictParam, String hmac) {
        if(dictParam==null){
            return VertifyType.NO_BEAN.value;
        }
        if (macKey == null || macKey.length() != LENGTH) {
            //makey 长度不足
            return VertifyType.LENGTH_NOT_ENOUGH.value;
        }
        if (!dictParam.containsKey(TS)) {
            //缺少时间戳
            return VertifyType.TS_LOSSED.value;
        }
        if(hmac==null||hmac.length()==0){
            //缺少签名结果
            return VertifyType.NO_HMAC.value;
        }
        String hash = calcHmac(macKey,dictParam);
        if(hash==null){
            return VertifyType.NOT_PASSED.value;
        }
        log.info("计算密钥key：{},计算的mac:{},传入的mac:{} ",macKey,hash,hmac);
        // 4 mac校验不匹配
        log.info("验签结果：{}",hash.equals(hmac.toUpperCase()));
        return hash.equals(hmac.toUpperCase()) ? VertifyType.PASSED.value : VertifyType.NOT_PASSED.value;
    }

    /**
     * 计算hmac校验
     * @param macKey macKey
     * @param dictParam 参数列表
     * @return 返回计算结果
     */
    private static String calcHmac(String macKey, Map<String, String> dictParam) {

        StringBuilder sb = new StringBuilder();
        SortedSet<String> keys = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        keys.addAll(dictParam.keySet());

        for (String key : keys) {
            if(SIGN.equals(key) || dictParam.get(key)==null){
                continue;
            }
            sb.append(key);
            sb.append(dictParam.get(key));
            sb.append("||");
        }
        log.info(macKey);
        log.info(sb.toString());
        SecretKeySpec keySpec = new SecretKeySpec(hexStringToByte(macKey), VERTIFY_FUNCTION);
        Mac mac;
        try {
            mac = Mac.getInstance(VERTIFY_FUNCTION);
            mac.init(keySpec);
            byte[] result = mac.doFinal(sb.toString().getBytes("UTF-8"));
            return bytesToHexString(result);
        } catch (Exception e) {
            log.error("calc Hmac error :{}",e);
        }
        return null;
    }

    /**
     * 生成新的hamcKey
     * @param message 特征
     * @return 返回生成结果
     */
    public static String getNewHmacKey(String message) {
        String str = message + UUID.randomUUID().toString();
        byte[] enk = hex(str);
        byte[] encoded = encryptMode(enk, MAIN_KEY.getBytes());
        String hmacKey = bytesToHexString(encoded);
        hmacKey = hmacKey.toUpperCase().substring(0, 40);
        return hmacKey;
    }


    //下面是私有工具类

    /**
     * 把16进制的字符串转化为字节数组
     * @param hex 16进制的字符串
     * @return 返回byte[] 数组
     */
    private static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    /**
     * 将char 转化为字节数组
     * @param c c
     * @return 返回转化结果
     */
    private static byte toByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 把字节数组转化为16进制的字符串
     * @param bArray 16进制数组
     * @return 返回结果
     */
    private static String bytesToHexString(byte[] bArray) {
        if (bArray == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(bArray.length);
        String sTemp;
        for (byte b:bArray) {
            sTemp = Integer.toHexString(0xFF & b);
            if (sTemp.length() < 2){
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * args在java中调用sun公司提供的3DES加密解密算法时，需要使 用到$JAVA_HOME/jre/lib/目录下如下的4个jar包：
     *     jce.jar
     *     security/US_export_policy.jar
     *     security/local_policy.jar
     *     ext/sunjce_provider.jar
     * @param keybyte keybyte
     * @param src src
     * @return 返回结果
     */
    private static byte[] encryptMode(byte[] keybyte, byte[] src) {
        //定义加密算法,可用 DES,DESede,Blowfish
        String algorithm = "DESede";
        try {
            //生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, algorithm);
            //加密
            Cipher c1 = Cipher.getInstance(algorithm);
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            //在单一方面的加密或解密
            return c1.doFinal(src);
        } catch (Exception e) {
            log.error("encryptMode :{}",e.getMessage(),e);
        }
        return null;
    }

    /**
     * 转16进制
     * @param username  username
     * @return 返回byte[]
     */
    private static byte[] hex(String username) {
        //关键字
        String f = DigestUtils.md5Hex(username);
        byte[] bkeys = f.getBytes();
        return Arrays.copyOf(bkeys,24);
    }
}
