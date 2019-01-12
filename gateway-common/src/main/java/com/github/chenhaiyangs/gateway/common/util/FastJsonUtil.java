package com.github.chenhaiyangs.gateway.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * fastJSON工具类
 * @author chenhaiyang
 */
public class FastJsonUtil {
    /**
     * 将请求报文转化成map。要求：转成成的map中的子json字符串，顺序必须和原始报文一致。
     * 这样签名算法才能正确的解析
     * @param postBody postBody
     * @return map
     */
    public static Map<String,String> parseToJSONObject(String postBody) {
        return JSON.parseObject(postBody,new TypeReference<LinkedHashMap<String,String>>(){},Feature.OrderedField);
    }

    /**
     * JSON字符串转换为Map<String,String>类型的map
     * @param jsonStr json字符串
     * @return 返回map
     */
    public static Map<String,String> toStringMap(String jsonStr){
        return JSON.parseObject(jsonStr,new TypeReference<LinkedHashMap<String,String>>(){});
    }

    /**
     * JSON字符串转换为Map<String,Boolean>类型的map
     * @param jsonStr json字符串
     * @return 返回map
     */
    public static Map<String,Boolean> toBooleanValueMap(String jsonStr){
        return JSON.parseObject(jsonStr,new TypeReference<LinkedHashMap<String,Boolean>>(){});
    }

    /**
     * JSON字符串转换为Map<String,Integer>类型的map
     * @param jsonStr json字符串
     * @return 返回map
     */
    public static Map<String,Integer> toIntegerValueMap(String jsonStr){
        return JSON.parseObject(jsonStr,new TypeReference<LinkedHashMap<String,Integer>>(){});
    }

    /**
     * JSON字符串转换为Map<Inetegr,Integer>类型的map
     * @param jsonStr json字符串
     * @return 返回map
     */
    public static Map<Integer,Integer> toIntegerKeyValueMap(String jsonStr){
        return JSON.parseObject(jsonStr,new TypeReference<LinkedHashMap<Integer,Integer>>(){});
    }

    /**
     * 判断是否是json
     * @param body body
     * @return 结果
     */
    public static boolean isJson(String body) {
        try{
            JSON.parseObject(body,new TypeReference<LinkedHashMap<String,Object>>(){});
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 转为json字符串
     * @param bean  bean
     * @return string
     */
    public static String toJSONString(Object bean) {
        return JSONObject.toJSONString(bean, SerializerFeature.WriteMapNullValue);
    }

    /**
     * 将json字符串转化为json对象
     * @param jsonStr jsonStr
     * @param clazz clazz
     * @param <T> t
     * @return t
     */
    public static <T> T toJSONObject(String jsonStr,Class<T> clazz) {
        JSONObject jsonObject=JSONObject.parseObject(jsonStr);
        return JSONObject.toJavaObject(jsonObject,clazz);
    }

    /**
     * 将json字符串转化为StringObject类型的map
     * @param jsonStr json字符串
     * @return map
     */
    public static Map<String,Object> parseToObjectMap(String jsonStr) {
        return JSON.parseObject(jsonStr,new TypeReference<LinkedHashMap<String,Object>>(){});
    }
}
