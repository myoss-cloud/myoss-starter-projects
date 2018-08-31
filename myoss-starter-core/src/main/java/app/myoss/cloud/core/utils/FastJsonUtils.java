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

package app.myoss.cloud.core.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;

import app.myoss.cloud.core.spring.boot.config.FastJsonAutoConfiguration;

/**
 * Fast JSON 扩展API
 *
 * @author Jerry.Chen
 * @since 2018年3月26日 下午10:01:14
 */
public class FastJsonUtils {
    /**
     * Fast JSON的全局配置
     */
    public static final FastJsonConfig GLOBAL_FAST_JSON_CONFIG = FastJsonAutoConfiguration.fastJsonConfig();

    /**
     * 将 JSON 字符串转换为 JSONObject 对象，如果 JSON 字符串为空字符串或者 NULL，则创建一个 JSONObject 对象
     *
     * @param jsonString JSON字符串（可以是空字符串或者 NULL）
     * @return JSONObject对象
     */
    public static JSONObject parseObject(String jsonString) {
        JSONObject result = JSON.parseObject(jsonString);
        return (result != null ? result : new JSONObject());
    }

    /**
     * 将 JSON 字符串转换为 JSONArray 对象，如果 JSON 字符串为空字符串或者 NULL，则创建一个 JSONArray 对象
     *
     * @param jsonString JSON字符串（可以是空字符串或者 NULL）
     * @return JSONArray对象
     */
    public static JSONArray parseArray(String jsonString) {
        JSONArray result = JSON.parseArray(jsonString);
        return (result != null ? result : new JSONArray());
    }

    /**
     * 输出JSON字符串，日期会格式化，其它格式化参考：
     * {@link FastJsonAutoConfiguration#fastJsonConfig()}
     *
     * @param object 待转换待对象
     * @return JSON字符串
     */
    public static String toJSONString(Object object) {
        return JSON.toJSONString(object, GLOBAL_FAST_JSON_CONFIG.getSerializeConfig(),
                SerializerFeature.WriteDateUseDateFormat);
    }

    /**
     * 从 JSONObject 对象中获取key，如果 key 为空字符串或者 NULL，则创建一个 JSONObject 对象
     *
     * @param source JSONObject原始对象
     * @param key 要从原始对象中获取的key
     * @return JSONObject对象
     */
    public static JSONObject getJSONObject(JSONObject source, String key) {
        JSONObject result = source.getJSONObject(key);
        return (result != null ? result : new JSONObject());
    }
}
