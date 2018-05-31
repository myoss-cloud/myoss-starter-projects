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

package com.github.myoss.phoenix.core.log.method.aspectj;

import java.io.Writer;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;

/**
 * 记录方法入参和返回值的基类
 *
 * @author Jerry.Chen
 * @since 2018年3月31日 下午10:56:32
 */
public abstract class AbstractMonitorMethod {
    @Autowired
    protected MonitorMethodProperties properties;

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

    protected String toJSONString(Object input) {
        return JSONObject.toJSONStringWithDateFormat(input, properties.getDateFormat());
    }
}
