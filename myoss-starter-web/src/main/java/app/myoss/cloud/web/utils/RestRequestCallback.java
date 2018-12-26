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

package app.myoss.cloud.web.utils;

import java.net.URI;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

/**
 * Rest Request 回调函数，用于在发送请求之前，做某些处理操作；也可用于在返回结果之前，做某些处理操作
 *
 * @author Jerry.Chen
 * @since 2018年12月26日 下午2:04:32
 */
public interface RestRequestCallback {
    /**
     * 用于在发送请求之前，做某些处理操作
     *
     * @param restTemplate RestTemplate
     * @param uri 请求的uri
     * @param httpHeaders 自定义HttpHeaders
     * @param method 请求方法类型
     * @param requestBody 请求的内容
     * @param responseType 返回的数据类型
     * @param <T> 数据类型class的泛形
     */
    default <T> void beforeRequest(RestTemplate restTemplate, URI uri, HttpHeaders httpHeaders, HttpMethod method,
                                   Object requestBody, Class<T> responseType) {
        // do nothing
    }

    /**
     * 用于在返回结果之前，做某些处理操作
     *
     * @param restTemplate RestTemplate
     * @param uri 请求的uri
     * @param method 请求方法类型
     * @param httpEntity 请求的内容
     * @param responseType 返回的数据类型
     * @param responseBody 请求结果
     * @param <T> 数据类型class的泛形
     * @return 请求结果（可以重写返回结果）
     */
    default <T> T afterRequest(RestTemplate restTemplate, URI uri, HttpMethod method, HttpEntity<?> httpEntity,
                               Class<T> responseType, T responseBody) {
        // do nothing
        return responseBody;
    }

    /**
     * 用于在发生异常的时候，做某些处理操作
     *
     * @param restTemplate RestTemplate
     * @param uri 请求的uri
     * @param method 请求方法类型
     * @param httpEntity 请求的内容
     * @param responseType 返回的数据类型
     * @param ex 异常信息
     * @param <T> 数据类型class的泛形
     */
    default <T> void onThrowException(RestTemplate restTemplate, URI uri, HttpMethod method, HttpEntity<?> httpEntity,
                                      Class<T> responseType, Exception ex) {
        // do nothing
    }
}
