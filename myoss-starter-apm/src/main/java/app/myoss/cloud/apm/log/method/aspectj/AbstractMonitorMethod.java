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

package app.myoss.cloud.apm.log.method.aspectj;

import java.io.Writer;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;

import app.myoss.cloud.core.lang.json.JsonApi;
import app.myoss.cloud.core.utils.JacksonMapper;

/**
 * 记录方法入参和返回值的基类
 *
 * @author Jerry.Chen
 * @since 2018年3月31日 下午10:56:32
 */
public abstract class AbstractMonitorMethod {
    @Autowired
    protected MonitorMethodProperties properties;
    private JacksonMapper             jacksonMapper;
    private Object                    gson;

    /**
     * 转换那些无法被JSON序列化的对象，比如：ServletRequest/ServletResponse
     *
     * @param arguments 待转换的方法参数数组对象
     * @return 转换后的方法参数数组对象
     */
    protected Object[] convertArgs(Object[] arguments) {
        for (int i = 0; i < arguments.length; i++) {
            Object value = arguments[i];
            if (value == null) {
                continue;
            }
            convertArgs(arguments, i, value);
        }
        return arguments;
    }

    /**
     * 转换那些无法被JSON序列化的对象，比如：ServletRequest/ServletResponse
     *
     * @param arguments 待转换的方法参数数组对象
     * @param i 索引
     * @param value 转换后的值
     */
    protected void convertArgs(Object[] arguments, int i, Object value) {
        if (value instanceof Writer) {
            arguments[i] = value.getClass().getName();
        } else {
            for (Class exclude : properties.getExcludeClass()) {
                if (exclude.isInstance(value)) {
                    arguments[i] = value.getClass().getName();
                    break;
                }
            }
        }
    }

    /**
     * 将对象转换为 JSON 字符串
     *
     * @param input 对象
     * @return JSON 字符串
     */
    protected String toJSONString(Object input) {
        if (JsonApi.JACKSON_2_PRESENT) {
            if (jacksonMapper == null) {
                synchronized (this) {
                    if (jacksonMapper == null) {
                        jacksonMapper = new JacksonMapper();
                        jacksonMapper.getMapper().setDateFormat(new SimpleDateFormat(properties.getDateFormat()));
                    }
                }
            }
            return jacksonMapper.toJson(input);
        } else if (JsonApi.GSON_PRESENT) {
            if (gson == null) {
                synchronized (this) {
                    if (gson == null) {
                        gson = new com.google.gson.GsonBuilder().serializeNulls()
                                .setDateFormat(properties.getDateFormat())
                                .create();
                    }
                }
            }
            return ((com.google.gson.Gson) gson).toJson(input);
        } else if (JsonApi.FASTJSON_PRESENT) {
            return com.alibaba.fastjson.JSONObject.toJSONStringWithDateFormat(input, properties.getDateFormat());
        }
        throw new UnsupportedOperationException("please add json dependency: gson or fastjson or jackson ");
    }
}
