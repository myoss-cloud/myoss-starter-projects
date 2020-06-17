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

/**
 * Json Api 工具类
 *
 * @author Jerry.Chen
 * @since 2020年6月3日 下午3:08:17
 */
public interface JsonSpi {
    /**
     * 获取JSON实现类
     *
     * @param <T> 泛型
     * @return JSON实现类
     */
    <T> T getApi();

    /**
     * 设置JSON实现类
     *
     * @param api JSON实现类
     * @param <T> 泛型
     */
    <T> void setApi(T api);

    /**
     * 将对象序列化为JSON字符串
     *
     * @param src 对象
     * @return JSON字符串
     */
    String toJson(Object src);

    /**
     * 将对象序列化为JSON字符串
     *
     * @param src 对象
     * @param typeOfSrc 范型转换器
     * @return JSON字符串
     */
    String toJson(Object src, Type typeOfSrc);

    /**
     * 将对象序列化为JSON字符串
     *
     * @param src 对象
     * @param writer 将JSON字符串写入到 {@link Appendable} 中
     */
    void toJson(Object src, Appendable writer);

    /**
     * 将对象序列化为JSON字符串
     *
     * @param src 对象
     * @param typeOfSrc 范型转换器
     * @param writer 将JSON字符串写入到 {@link Appendable} 中
     */
    void toJson(Object src, Type typeOfSrc, Appendable writer);

    /**
     * 反序列化JSON字符串为POJO对象
     *
     * @param json JSON字符串
     * @param classOfT class类型
     * @param <T> 泛型
     * @return POJO对象
     */
    <T> T fromJson(String json, Class<T> classOfT);

    /**
     * 反序列化JSON字符串为POJO对象
     *
     * @param json JSON字符串
     * @param typeOfT 范型转换器
     * @param <T> 泛型
     * @return POJO对象
     */
    <T> T fromJson(String json, Type typeOfT);

    /**
     * 反序列化JSON字符串为POJO对象
     *
     * @param json JSON字符串
     * @param classOfT class类型
     * @param <T> 泛型
     * @return POJO对象
     */
    <T> T fromJson(Reader json, Class<T> classOfT);

    /**
     * 反序列化JSON字符串为POJO对象
     *
     * @param json JSON字符串
     * @param typeOfT 范型转换器
     * @param <T> 泛型
     * @return POJO对象
     */
    <T> T fromJson(Reader json, Type typeOfT);

    /**
     * 反序列化JSON字符串为JsonObject对象
     *
     * @param json JSON字符串
     * @return JsonObject对象
     */
    JsonObject fromJson(String json);

    /**
     * 反序列化JSON字符串为JsonObject对象
     *
     * @param json JSON字符串
     * @return JsonObject对象
     */
    JsonObject fromJson(Reader json);

    <T> List<T> fromJsonArray(String json, Class<T> clazz);
}
