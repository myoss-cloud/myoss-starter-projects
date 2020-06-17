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

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import app.myoss.cloud.core.lang.json.JsonObject;
import app.myoss.cloud.core.lang.json.JsonSpi;

/**
 * Gson Api
 *
 * @author Jerry.Chen
 * @since 2020年6月3日 下午3:17:59
 */
public class GsonApi implements JsonSpi {
    private Gson gson;

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getApi() {
        return (T) gson;
    }

    @Override
    public <T> void setApi(T api) {
        gson = (Gson) api;
    }

    @Override
    public String toJson(Object src) {
        return gson.toJson(src);
    }

    @Override
    public String toJson(Object src, Type typeOfSrc) {
        return gson.toJson(src, typeOfSrc);
    }

    @Override
    public void toJson(Object src, Appendable writer) {
        gson.toJson(src, writer);
    }

    @Override
    public void toJson(Object src, Type typeOfSrc, Appendable writer) {
        gson.toJson(src, typeOfSrc, writer);
    }

    @Override
    public <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    @Override
    public <T> T fromJson(String json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }

    @Override
    public <T> T fromJson(Reader json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    @Override
    public <T> T fromJson(Reader json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }

    @Override
    public JsonObject fromJson(String json) {
        return gson.fromJson(json, new TypeToken<LinkedHashMap<String, Object>>() {
        }.getType());
    }

    @Override
    public JsonObject fromJson(Reader json) {
        return gson.fromJson(json, new TypeToken<LinkedHashMap<String, Object>>() {
        }.getType());
    }

    @Override
    public <T> List<T> fromJsonArray(String json, Class<T> clazz) {
        // todo check
        return gson.fromJson(json, new TypeToken<List<Class<T>>>() {
        }.getType());
    }
}
