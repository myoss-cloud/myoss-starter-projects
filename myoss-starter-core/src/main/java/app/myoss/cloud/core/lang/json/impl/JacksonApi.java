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

import static app.myoss.cloud.core.utils.JacksonMapper.JSON_OBJECT_TYPE_REFERENCE;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.myoss.cloud.core.exception.BizRuntimeException;
import app.myoss.cloud.core.lang.json.JsonObject;
import app.myoss.cloud.core.lang.json.JsonSpi;
import app.myoss.cloud.core.utils.JacksonMapper;

/**
 * Jackson Api
 *
 * @author Jerry.Chen
 * @since 2020年6月3日 下午3:45:56
 */
public class JacksonApi implements JsonSpi {
    private JacksonMapper jacksonMapper;

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getApi() {
        return (T) jacksonMapper;
    }

    @Override
    public <T> void setApi(T api) {
        jacksonMapper = (JacksonMapper) api;
    }

    @Override
    public String toJson(Object src) {
        return jacksonMapper.toJson(src);
    }

    @Override
    public String toJson(Object src, Type typeOfSrc) {
        ObjectMapper mapper = jacksonMapper.getMapper();
        JavaType javaType = mapper.constructType(typeOfSrc);
        try {
            return mapper.writerFor(javaType).writeValueAsString(src);
        } catch (JsonProcessingException e) {
            throw new BizRuntimeException(e);
        }
    }

    @Override
    public void toJson(Object src, Appendable writer) {
        try {
            jacksonMapper.getMapper().writeValue((Writer) writer, src);
        } catch (IOException e) {
            throw new BizRuntimeException(e);
        }
    }

    @Override
    public void toJson(Object src, Type typeOfSrc, Appendable writer) {
        ObjectMapper mapper = jacksonMapper.getMapper();
        JavaType javaType = mapper.constructType(typeOfSrc);
        try {
            mapper.writerFor(javaType).writeValue((Writer) writer, src);
        } catch (IOException e) {
            throw new BizRuntimeException(e);
        }
    }

    @Override
    public <T> T fromJson(String json, Class<T> classOfT) {
        return jacksonMapper.fromJson(json, classOfT);
    }

    @Override
    public <T> T fromJson(String json, Type typeOfT) {
        ObjectMapper mapper = jacksonMapper.getMapper();
        JavaType javaType = mapper.constructType(typeOfT);
        try {
            return mapper.readerFor(javaType).readValue(json);
        } catch (IOException e) {
            throw new BizRuntimeException(e);
        }
    }

    @Override
    public <T> T fromJson(Reader json, Class<T> classOfT) {
        try {
            return jacksonMapper.getMapper().readValue(json, classOfT);
        } catch (IOException e) {
            throw new BizRuntimeException(e);
        }
    }

    @Override
    public <T> T fromJson(Reader json, Type typeOfT) {
        ObjectMapper mapper = jacksonMapper.getMapper();
        JavaType javaType = mapper.constructType(typeOfT);
        try {
            return mapper.readerFor(javaType).readValue(json);
        } catch (IOException e) {
            throw new BizRuntimeException(e);
        }
    }

    @Override
    public JsonObject fromJson(String json) {
        return jacksonMapper.fromJson(json, JsonObject.class);
    }

    @Override
    public JsonObject fromJson(Reader json) {
        try {
            return jacksonMapper.getMapper().readValue(json, JSON_OBJECT_TYPE_REFERENCE);
        } catch (IOException e) {
            throw new BizRuntimeException(e);
        }
    }

    @Override
    public <T> List<T> fromJsonArray(String json, Class<T> clazz) {
        JavaType javaType = jacksonMapper.constructCollectionType(List.class, clazz);
        return jacksonMapper.fromJson(json, javaType);
    }
}
