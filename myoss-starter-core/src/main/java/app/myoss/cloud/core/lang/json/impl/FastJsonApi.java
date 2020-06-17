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

package app.myoss.cloud.core.lang.json.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;

import app.myoss.cloud.core.exception.BizRuntimeException;
import app.myoss.cloud.core.lang.json.JsonObject;
import app.myoss.cloud.core.lang.json.JsonSpi;

/**
 * FastJson Api
 *
 * @author Jerry.Chen
 * @since 2020年6月3日 下午3:35:32
 */
public class FastJsonApi implements JsonSpi {

    @Override
    public <T> T getApi() {
        return null;
    }

    @Override
    public <T> void setApi(T api) {
    }

    @Override
    public String toJson(Object src) {
        return JSON.toJSONString(src);
    }

    @Override
    public String toJson(Object src, Type typeOfSrc) {
        SerializeConfig.getGlobalInstance().put(src, typeOfSrc);
        return JSON.toJSONString(src);
    }

    @Override
    public void toJson(Object src, Appendable writer) {
        JSON.writeJSONString((Writer) writer, src);
    }

    @Override
    public void toJson(Object src, Type typeOfSrc, Appendable writer) {
        SerializeConfig.getGlobalInstance().put(src, typeOfSrc);
        JSON.writeJSONString((Writer) writer, src);
    }

    @Override
    public <T> T fromJson(String json, Class<T> classOfT) {
        return JSON.parseObject(json, classOfT);
    }

    @Override
    public <T> T fromJson(String json, Type typeOfT) {
        return JSON.parseObject(json, typeOfT);
    }

    private StringBuilder toString(Reader json) {
        StringBuilder sb = new StringBuilder(2048);
        char[] buf = new char[2048];
        try {
            int nRead;
            while ((nRead = json.read(buf)) != -1) {
                sb.append(buf, 0, nRead);
            }
        } catch (IOException e) {
            throw new BizRuntimeException(e);
        }
        return sb;
    }

    @Override
    public <T> T fromJson(Reader json, Class<T> classOfT) {
        return JSON.parseObject(toString(json).toString(), classOfT);
    }

    @Override
    public <T> T fromJson(Reader json, Type typeOfT) {
        return JSON.parseObject(toString(json).toString(), typeOfT);
    }

    @Override
    public JsonObject fromJson(String json) {
        return JSON.parseObject(json, JsonObject.class);
    }

    @Override
    public JsonObject fromJson(Reader json) {
        return JSON.parseObject(toString(json).toString(), JsonObject.class);
    }

    @Override
    public <T> List<T> fromJsonArray(String json, Class<T> clazz) {
        return JSON.parseArray(json, clazz);
    }
}
