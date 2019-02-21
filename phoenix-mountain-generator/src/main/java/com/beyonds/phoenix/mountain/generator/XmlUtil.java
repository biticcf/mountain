package com.beyonds.phoenix.mountain.generator;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * 
 * @Author: Daniel.Cao
 * @Date:   2019年1月13日
 * @Time:   上午10:39:30
 *
 */
class XmlUtil {

    private static final XmlMapper xmlMapper = new XmlMapper();

    private XmlUtil() {
        //		com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

    }

    /**
     * +输出全部属性 如果xml中存在，对象中没有，则自动忽略该属性 失败返回null
     * 
     * @param xmlContent
     * @param clazz
     * @return
     */
    public static <T> T toNormalObject(String xmlContent, Class<T> clazz) {
        return xmlToObject(xmlContent, clazz);
    }

    /**
     * +输出全部属性 如果xml中存在，对象中没有，则自动忽略该属性 失败返回null
     * 
     * @param inputStream
     * @param clazz
     * @return
     */
    public static <T> T toNormalObject(byte[] bytes, Class<T> clazz) {
        return xmlToObject(bytes, clazz);
    }

    /**
     * +输出全部属性 失败返回""
     * 
     * @param object
     * @return
     */
    public synchronized static byte[] toNormalXml(Object object) {
        return objectToXml(Include.ALWAYS, object);
    }

    private static <T> T xmlToObject(byte[] bytes, Class<T> clazz) {
        try {
            return xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(bytes, clazz);
        } catch (Exception e) {
        }
        return null;
    }

    public static <T> T xmlToObject(String xmlContent, Class<T> clazz) {
        try {
            return xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(xmlContent, clazz);
        } catch (Exception e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }

    public static <T> byte[] objectToXml(Include include, T object) {
        try {
            return xmlMapper.setSerializationInclusion(include).writerWithDefaultPrettyPrinter().writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unused")
    private static <T> byte[] objectToXml(T object) {
        try {
            return xmlMapper.setSerializationInclusion(Include.ALWAYS).writerWithDefaultPrettyPrinter().writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}
