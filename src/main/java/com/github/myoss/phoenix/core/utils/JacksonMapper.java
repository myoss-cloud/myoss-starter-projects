/*
 * Copyright 2018-2018 https://github.com/myoss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.myoss.phoenix.core.utils;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import lombok.Getter;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.github.myoss.phoenix.core.exception.BizRuntimeException;

/**
 * 简单封装Jackson，实现{@code JSON String<->Java Object} 的Mapper. 封装不同的输出风格,
 * 使用不同的builder函数创建实例.
 *
 * @author Jerry.Chen 2018年5月4日 下午6:43:13
 */
public class JacksonMapper {
    public static TypeReference<SortedMap<String, String>> SORTED_MAP_S2S_TYPE_REFERENCE = new TypeReference<SortedMap<String, String>>() {
                                                                                         };
    @Getter
    private ObjectMapper                                   mapper;

    public JacksonMapper() {
        this(null);
    }

    public JacksonMapper(Include include) {
        mapper = new ObjectMapper();
        // 设置输出时包含属性的风格
        if (include != null) {
            mapper.setSerializationInclusion(include);
        }
        // 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    /**
     * 创建只输出非Null且非Empty(如List.isEmpty)的属性到Json字符串的Mapper,建议在外部接口中使用.
     *
     * @return JacksonMapper
     */
    public static JacksonMapper nonEmptyMapper() {
        return new JacksonMapper(Include.NON_EMPTY);
    }

    /**
     * 创建只输出初始值被改变的属性到Json字符串的Mapper, 最节约的存储方式，建议在内部接口中使用。
     *
     * @return JacksonMapper
     */
    public static JacksonMapper nonDefaultMapper() {
        return new JacksonMapper(Include.NON_DEFAULT);
    }

    /**
     * Object可以是POJO，也可以是Collection或数组。 如果对象为Null, 返回"null". 如果集合为空集合, 返回"[]".
     *
     * @param object Java对象
     * @return json字符串
     */
    public String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            throw new BizRuntimeException("write to json string error:" + object, e);
        }
    }

    /**
     * 反序列化POJO或简单Collection如 {@code List<String>}. 如果JSON字符串为Null或"null"字符串,
     * 返回Null. 如果JSON字符串为"[]", 返回空集合. 如需反序列化复杂Collection如 {@code List<MyBean>},
     * 请使用fromJson(String, JavaType)
     *
     * @param jsonString json字符串
     * @param clazz class
     * @param <T> 泛型
     * @return POJO对象
     * @see #fromJson(String, JavaType)
     */
    public <T> T fromJson(String jsonString, Class<T> clazz) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }
        try {
            return mapper.readValue(jsonString, clazz);
        } catch (IOException e) {
            throw new BizRuntimeException("parse json string error:" + jsonString, e);
        }
    }

    /**
     * 反序列化复杂Collection如 {@code List<Bean>},
     * 先使用createCollectionType()或constructMapType()构造类型, 然后调用本函数.
     *
     * @param jsonString json字符串
     * @param javaType JavaType
     * @param <T> 泛型
     * @return POJO对象
     */
    public <T> T fromJson(String jsonString, JavaType javaType) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }
        try {
            return mapper.readValue(jsonString, javaType);
        } catch (IOException e) {
            throw new BizRuntimeException("parse json string error:" + jsonString, e);
        }
    }

    /**
     * JSON转换成Java对象
     *
     * @param json JSON字符串
     * @return Java对象
     */
    @SuppressWarnings("unchecked")
    public HashMap<String, Object> json2Map(String json) {
        return fromJson(json, HashMap.class);
    }

    /**
     * 把object转出clazz对象，比如POJO和Map互换
     *
     * @param object 原对象
     * @param clazz 目标类型
     * @param <T> 泛型
     * @return Java对象
     */
    public <T> T convert(Object object, Class<T> clazz) {
        if (object == null) {
            return null;
        }
        return mapper.convertValue(object, clazz);
    }

    /**
     * 把object转出clazz对象，比如POJO和Map互换
     *
     * @param object 原对象
     * @param toValueType 目标类型
     * @param <T> 泛型
     * @return Java对象
     */
    public <T> T convert(Object object, JavaType toValueType) {
        if (object == null) {
            return null;
        }
        return mapper.convertValue(object, toValueType);
    }

    /**
     * 把object转出clazz对象，比如POJO和Map互换
     *
     * @param object 原对象
     * @param toValueTypeRef 目标类型
     * @param <T> 泛型
     * @return Java对象
     */
    public <T> T convert(Object object, TypeReference<?> toValueTypeRef) {
        if (object == null) {
            return null;
        }
        return mapper.convertValue(object, toValueTypeRef);
    }

    /**
     * 构造Collection类型.
     *
     * @param collectionClass 集合类型class
     * @param elementClass 集合中元素类型class
     * @return JavaType
     */
    public JavaType constructCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
        return mapper.getTypeFactory().constructCollectionType(collectionClass, elementClass);
    }

    /**
     * 构造Map类型.
     *
     * @param mapClass Map类型class
     * @param keyClass Key类型class
     * @param valueClass Value类型class
     * @return JavaType
     */
    public JavaType constructMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
        return mapper.getTypeFactory().constructMapType(mapClass, keyClass, valueClass);
    }

    /**
     * 当JSON里只含有Bean的部分属性时，更新一個已存在Bean，只覆盖部分的属性.
     *
     * @param jsonString json字符串
     * @param object Java对象
     */
    public void update(String jsonString, Object object) {
        try {
            mapper.readerForUpdating(object).readValue(jsonString);
        } catch (IOException e) {
            throw new BizRuntimeException("update json string:" + jsonString + " to object:" + object + " error.", e);
        }
    }

    /**
     * 输出JSONP格式数据.
     *
     * @param functionName JavaScript function name
     * @param object Java对象
     * @return json字符串
     */
    public String toJsonP(String functionName, Object object) {
        return toJson(new JSONPObject(functionName, object));
    }

    /**
     * 设定是否使用Enum的toString函数来读写Enum, 为False时使用Enum的name()函數來读写Enum, 默认为False.
     * 注意本函數一定要在Mapper创建后, 所有的读写动作之前调用.
     */
    public void enableEnumUseToString() {
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
    }
}
