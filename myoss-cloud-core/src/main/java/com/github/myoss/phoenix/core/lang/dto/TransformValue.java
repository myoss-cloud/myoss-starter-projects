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

import java.util.List;

/**
 * 转移对象中的值，或者快速设置对象中的值
 *
 * @author Jerry.Chen
 * @since 2018年5月10日 下午2:08:13
 */
public class TransformValue {
    /**
     * 复制 Result 中的错误信息，并设置目标中的success = false
     *
     * @param source 源
     * @param target 目标
     * @param <T> 类型
     * @return target实例对象
     */
    public static <T> Result<T> copyErrorInfo(Result<?> source, Result<T> target) {
        return target.setSuccess(false).setErrorCode(source.getErrorCode()).setErrorMsg(source.getErrorMsg());
    }

    /**
     * 复制 Result 中的所有信息
     *
     * @param source 源
     * @param target 目标
     * @param <T> 类型
     * @return target实例对象
     */
    public static <T> Result<T> copyAllInfo(Result<T> source, Result<T> target) {
        target.setSuccess(source.isSuccess()).setErrorCode(source.getErrorCode()).setErrorMsg(source.getErrorMsg());
        target.setValue(source.getValue()).setExtraInfo(source.getExtraInfo());
        return target;
    }

    /**
     * 设置 Result 中的错误信息，并设置目标中的success = false
     *
     * @param target 目标
     * @param errorCode 错误代码
     * @param errorMsg 错误信息
     * @param value 设置value值
     * @param <T> 类型
     * @return target实例对象
     */
    public static <T> Result<T> setErrorInfo(Result<T> target, String errorCode, String errorMsg, T value) {
        return target.setSuccess(false).setErrorCode(errorCode).setErrorMsg(errorMsg).setValue(value);
    }

    /**
     * 设置 Result 中的错误信息，并设置目标中的success = false
     *
     * @param target 目标
     * @param errorCode 错误代码
     * @param errorMsg 错误信息
     * @param <T> 类型
     * @return target实例对象
     */
    public static <T> Result<T> setErrorInfo(Result<T> target, String errorCode, String errorMsg) {
        return target.setSuccess(false).setErrorCode(errorCode).setErrorMsg(errorMsg);
    }

    /**
     * 复制 Page 中的错误信息，并设置目标中的success = false
     *
     * @param source 源
     * @param target 目标
     * @param <T> 类型
     * @return target实例对象
     */
    public static <T> Page<T> copyErrorInfo(Page<?> source, Page<T> target) {
        return target.setSuccess(false).setErrorCode(source.getErrorCode()).setErrorMsg(source.getErrorMsg());
    }

    /**
     * 复制 Page 中的所有信息
     *
     * @param source 源
     * @param target 目标
     * @param <T> 类型
     * @return target实例对象
     */
    public static <T> Page<T> copyAllInfo(Page<T> source, Page<T> target) {
        target.setSuccess(source.isSuccess()).setErrorCode(source.getErrorCode()).setErrorMsg(source.getErrorMsg());
        target.setValue(source.getValue()).setExtraInfo(source.getExtraInfo());
        target.setPageSize(source.getPageSize())
                .setPageNum(source.getPageNum())
                .setTotalCount(source.getTotalCount())
                .setParam(source.getParam())
                .setSort(source.getSort());
        return target;
    }

    /**
     * 设置 Page 中的错误信息，并设置目标中的success = false
     *
     * @param target 目标
     * @param errorCode 错误代码
     * @param errorMsg 错误信息
     * @param value 设置value值
     * @param <T> 类型
     * @return target实例对象
     */
    public static <T> Page<T> setErrorInfo(Page<T> target, String errorCode, String errorMsg, List<T> value) {
        return target.setSuccess(false).setErrorCode(errorCode).setErrorMsg(errorMsg).setValue(value);
    }

    /**
     * 设置 Page 中的错误信息，并设置目标中的success = false
     *
     * @param target 目标
     * @param errorCode 错误代码
     * @param errorMsg 错误信息
     * @param <T> 类型
     * @return target实例对象
     */
    public static <T> Page<T> setErrorInfo(Page<T> target, String errorCode, String errorMsg) {
        return target.setSuccess(false).setErrorCode(errorCode).setErrorMsg(errorMsg);
    }
}
