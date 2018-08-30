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
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 封装分页查询参数
 *
 * @author Jerry.Chen
 * @since 2018年5月9日 下午3:50:31
 * @param <T> 泛型
 */
@Accessors(chain = true)
@Data
@EqualsAndHashCode
public class Page<T> implements Serializable {
    /**
     * 默认的每页条数为：20
     */
    public static final int     DEFAULT_PAGE_SIZE = 20;
    /**
     * 默认的起始页面为：1（在MySQL中是从0开始，使用的时候需要减一）
     */
    public static final int     DEFAULT_PAGE_NUM  = 1;

    private static final long   serialVersionUID  = -3939417600928116634L;
    /**
     * 每页条数
     */
    private int                 pageSize          = DEFAULT_PAGE_SIZE;
    /**
     * 第几页，在MySQL中是从0开始，使用的时候需要减一
     */
    private int                 pageNum           = DEFAULT_PAGE_NUM;
    /**
     * 记录总数
     */
    private int                 totalCount        = 0;
    /**
     * 分页查询条件参数
     */
    private T                   param;
    /**
     * 分页排序字段
     */
    private Sort                sort;
    /**
     * 结果列表数据
     */
    private List<T>             value;
    private boolean             success           = true;
    private String              errorMsg;
    private String              errorCode;
    private Map<String, Object> extraInfo;

    /**
     * 创建 Page 实例
     */
    public Page() {
        super();
    }

    /**
     * 创建 Page 实例，初始化分页查询条件参数
     *
     * @param param 分页查询条件参数
     */
    public Page(T param) {
        this();
        this.param = param;
    }

    /**
     * 创建 Page 实例，初始化结果值
     *
     * @param value 结果值
     */
    public Page(List<T> value) {
        this();
        this.value = value;
    }

    /**
     * 创建 Page 实例
     * <p>
     * 初始化错误信息，会设置<code>success = false</code>
     *
     * @param errorCode 错误代码
     * @param errorMsg 错误信息
     */
    public Page(String errorCode, String errorMsg) {
        this(false, errorCode, errorMsg);
    }

    /**
     * 创建 Page 实例
     *
     * @param success 是否成功
     * @param errorCode 错误代码
     * @param errorMsg 错误信息
     */
    public Page(boolean success, String errorCode, String errorMsg) {
        this(success, null, errorCode, errorMsg);
    }

    /**
     * 创建 Page 实例
     *
     * @param success 是否成功
     * @param value 结果值
     * @param errorCode 错误代码
     * @param errorMsg 错误信息
     */
    public Page(boolean success, List<T> value, String errorCode, String errorMsg) {
        this();
        this.success = success;
        this.value = value;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    /**
     * 获取每页条数
     *
     * @return 每页条数
     */
    public int getPageSize() {
        return (pageSize < 1 ? Page.DEFAULT_PAGE_SIZE : pageSize);
    }

    /**
     * 获取当前是第几页
     *
     * @return 第几页，这里是从第<code>1</code>页开始（在MySQL中是从0开始，使用的时候需要减一）
     */
    public int getPageNum() {
        return (pageNum < 1 ? DEFAULT_PAGE_NUM : pageNum);
    }

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
     * 查找扩展信息中的key
     *
     * @param key 扩展信息中的key
     * @return 扩展信息中的value
     */
    public Object getExtraInfo(String key) {
        return (this.extraInfo != null ? this.extraInfo.get(key) : null);
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
