/*
 * Copyright 2018-2020 https://github.com/myoss
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

package app.myoss.cloud.core.lang.json;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.util.ClassUtils;

import app.myoss.cloud.core.lang.json.impl.FastJsonApi;
import app.myoss.cloud.core.lang.json.impl.GsonApi;
import app.myoss.cloud.core.lang.json.impl.JacksonApi;
import app.myoss.cloud.core.utils.JacksonMapper;

/**
 * JSON 工具类，封装了常用的方法，可以使用 SPI 的方式自由切换 JSON 实现类
 *
 * @author Jerry.Chen
 * @since 2020年6月3日 下午3:49:01
 */
public final class JsonApi {
    public static final boolean  JACKSON_2_PRESENT;
    public static final boolean  GSON_PRESENT;
    public static final boolean  FASTJSON_PRESENT;
    private static final JsonSpi JSON_SPI;

    static {
        ClassLoader classLoader = JsonApi.class.getClassLoader();
        JACKSON_2_PRESENT = ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", classLoader)
                && ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", classLoader);
        GSON_PRESENT = ClassUtils.isPresent("com.google.gson.Gson", classLoader);
        FASTJSON_PRESENT = ClassUtils.isPresent("com.alibaba.fastjson.JSON", classLoader);
        JSON_SPI = getJsonSpi();
    }

    public static void checkJsonDependency() {
        if (GSON_PRESENT || FASTJSON_PRESENT || JACKSON_2_PRESENT) {
            return;
        }
        throw new UnsupportedOperationException("please add json dependency: gson or fastjson or jackson ");
    }

    /**
     * 获取 Json SPI，请在
     * <code>resources/META-INF/services/app.myoss.cloud.core.lang.json.JsonSpi</code>
     * 设置具体的实现类
     *
     * @return JsonSpi 实例对象
     */
    public static JsonSpi getJsonSpi() {
        if (JSON_SPI != null) {
            return JSON_SPI;
        }
        ServiceLoader<JsonSpi> serializers = ServiceLoader.load(JsonSpi.class);
        List<JsonSpi> apis = StreamSupport.stream(serializers.spliterator(), false).collect(Collectors.toList());
        for (JsonSpi api : apis) {
            if ((api instanceof JacksonApi) && JACKSON_2_PRESENT) {
                checkJsonDependency();
                JacksonApi jacksonApi = (JacksonApi) api;
                jacksonApi.setApi(JacksonMapper.nonNullMapper());
                return api;
            }
            if ((api instanceof GsonApi) && GSON_PRESENT) {
                checkJsonDependency();
                GsonApi gsonApi = (GsonApi) api;
                gsonApi.setApi(new com.google.gson.GsonBuilder().disableHtmlEscaping().create());
                return api;
            }
            if ((api instanceof FastJsonApi) && FASTJSON_PRESENT) {
                checkJsonDependency();
                return api;
            }
            return api;
        }
        throw new UnsupportedOperationException(
                "please add JsonApi in the file: META-INF/services/app.myoss.cloud.core.lang.json.JsonApi");
    }

    /**
     * 将对象序列化为JSON字符串
     *
     * @param src 对象
     * @return JSON字符串
     */
    public static String toJson(Object src) {
        return JSON_SPI.toJson(src);
    }

    /**
     * 将对象序列化为JSON字符串
     *
     * @param src 对象
     * @param typeOfSrc 范型转换器
     * @return JSON字符串
     */
    public static String toJson(Object src, Type typeOfSrc) {
        return JSON_SPI.toJson(src, typeOfSrc);
    }

    /**
     * 将对象序列化为JSON字符串
     *
     * @param src 对象
     * @param writer 将JSON字符串写入到 {@link Appendable} 中
     */
    public static void toJson(Object src, Appendable writer) {
        JSON_SPI.toJson(src, writer);
    }

    /**
     * 将对象序列化为JSON字符串
     *
     * @param src 对象
     * @param typeOfSrc 范型转换器
     * @param writer 将JSON字符串写入到 {@link Appendable} 中
     */
    public static void toJson(Object src, Type typeOfSrc, Appendable writer) {
        JSON_SPI.toJson(src, typeOfSrc, writer);
    }

    /**
     * 反序列化JSON字符串为POJO对象
     *
     * @param json JSON字符串
     * @param classOfT class类型
     * @param <T> 泛型
     * @return POJO对象
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        return JSON_SPI.fromJson(json, classOfT);
    }

    /**
     * 反序列化JSON字符串为POJO对象
     *
     * @param json JSON字符串
     * @param typeOfT 范型转换器
     * @param <T> 泛型
     * @return POJO对象
     */
    public static <T> T fromJson(String json, Type typeOfT) {
        return JSON_SPI.fromJson(json, typeOfT);
    }

    /**
     * 反序列化JSON字符串为POJO对象
     *
     * @param json JSON字符串
     * @param classOfT class类型
     * @param <T> 泛型
     * @return POJO对象
     */
    public static <T> T fromJson(Reader json, Class<T> classOfT) {
        return JSON_SPI.fromJson(json, classOfT);
    }

    /**
     * 反序列化JSON字符串为POJO对象
     *
     * @param json JSON字符串
     * @param typeOfT 范型转换器
     * @param <T> 泛型
     * @return POJO对象
     */
    public <T> T fromJson(Reader json, Type typeOfT) {
        return JSON_SPI.fromJson(json, typeOfT);
    }

    /**
     * 反序列化JSON字符串为JsonObject对象
     *
     * @param json JSON字符串
     * @return JsonObject对象
     */
    public static JsonObject fromJson(String json) {
        return JSON_SPI.fromJson(json);
    }

    /**
     * 反序列化JSON字符串为JsonObject对象
     *
     * @param json JSON字符串
     * @return JsonObject对象
     */
    public static JsonObject fromJson(Reader json) {
        return JSON_SPI.fromJson(json);
    }

    /**
     * 反序列化JSON字符串为JsonArray对象
     *
     * @param json JSON字符串
     * @return JsonArray对象
     */
    public static JsonArray fromJsonArray(String json) {
        return JSON_SPI.fromJson(json, JsonArray.class);
    }

    /**
     * 反序列化JSON字符串为JsonObject对象
     *
     * @param json JSON字符串
     * @return JsonObject对象
     */
    public static <T> List<T> fromJsonArray(String json, Class<T> clazz) {
        return JSON_SPI.fromJsonArray(json, clazz);
    }
}
