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

package app.myoss.cloud.core.lang.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import app.myoss.cloud.core.lang.json.JsonApi;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 封装方法返回参数
 *
 * @author Jerry.Chen
 * @since 2018年5月9日 下午2:55:11
 * @param <T> 泛型
 */
@AllArgsConstructor
@Accessors(chain = true)
@Data
@EqualsAndHashCode
@io.swagger.annotations.ApiModel("封装方法返回参数")
public class Result<T> implements Serializable {
    private static final long   serialVersionUID = -8699167988071027834L;
    @io.swagger.annotations.ApiModelProperty(value = "结果值", notes = "泛型")
    private T                   value;
    @io.swagger.annotations.ApiModelProperty("是否成功")
    private boolean             success          = true;
    @io.swagger.annotations.ApiModelProperty("错误代码")
    private String              errorCode;
    @io.swagger.annotations.ApiModelProperty("错误信息")
    private String              errorMsg;
    @io.swagger.annotations.ApiModelProperty("扩展信息")
    private Map<String, Object> extraInfo;

    /**
     * 创建 Result 实例
     */
    public Result() {
        super();
    }

    /**
     * 创建 Result 实例
     *
     * @param value 结果值
     */
    public Result(T value) {
        this();
        this.value = value;
    }

    /**
     * 创建 Result 实例
     * <p>
     * 初始化错误信息，会设置<code>success = false</code>
     *
     * @param errorMsg 错误信息
     * @param errorCode 错误代码
     */
    public Result(String errorCode, String errorMsg) {
        this(false, errorCode, errorMsg);
    }

    /**
     * 创建 Result 实例
     *
     * @param success 是否成功
     * @param errorMsg 错误信息
     * @param errorCode 错误代码
     */
    public Result(boolean success, String errorCode, String errorMsg) {
        this(null, success, errorCode, errorMsg);
    }

    /**
     * 创建 Result 实例
     *
     * @param value 结果值
     * @param success 是否成功
     * @param errorMsg 错误信息
     * @param errorCode 错误代码
     */
    public Result(T value, boolean success, String errorCode, String errorMsg) {
        this.success = success;
        this.value = value;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    /**
     * 增加扩展信息
     *
     * @param key 扩展信息中的key
     * @param value 扩展信息中的value
     * @return 当前Result实例对象
     */
    public Result<T> addExtraInfo(String key, Object value) {
        if (this.extraInfo == null) {
            this.extraInfo = new HashMap<>();
        }
        this.extraInfo.put(key, value);
        return this;
    }

    /**
     * 获取扩展信息
     *
     * @param key 扩展信息中的key
     * @return 扩展信息中的value
     */
    public Object getExtraInfo(String key) {
        return (this.extraInfo != null ? this.extraInfo.get(key) : null);
    }

    @Override
    public String toString() {
        return JsonApi.toJson(this);
    }
}
