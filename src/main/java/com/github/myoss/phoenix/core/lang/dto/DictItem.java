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

package com.github.myoss.phoenix.core.lang.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import lombok.Data;

/**
 * 封装字典选项值
 *
 * @author Jerry.Chen
 * @since 2018年5月9日 下午4:33:50
 */
@Data
public class DictItem<T extends Serializable> implements Serializable {
    private static final long   serialVersionUID = -4979428360062313584L;
    /**
     * 值
     */
    private T                   value;
    /**
     * 代码
     */
    private String              code;
    /**
     * 名称
     */
    private String              name;
    /**
     * 描述信息
     */
    private String              desc;
    /**
     * 扩展信息
     */
    private Map<String, Object> extraInfo;

    /**
     * 增加扩展信息
     *
     * @param key 扩展信息中的key
     * @param value 扩展信息中的value
     */
    public void addExtraInfo(String key, Object value) {
        if (this.extraInfo == null) {
            this.extraInfo = new HashMap<>();
        }
        this.extraInfo.put(key, value);
    }

    /**
     * 获取扩展信息
     *
     * @param key 扩展信息中的key
     * @return 扩展信息中的value
     */
    public Object getExtraInfo(String key) {
        return this.extraInfo == null ? null : this.extraInfo.get(key);
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
