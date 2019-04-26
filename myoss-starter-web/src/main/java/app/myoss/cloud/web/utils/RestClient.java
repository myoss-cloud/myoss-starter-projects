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
import java.util.Map;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import app.myoss.cloud.web.constants.WebConstants;
import lombok.extern.slf4j.Slf4j;

/**
 * Rest Client 工具类，使用 {@link RestUtils} 和 {@link RestTemplate} 发送 HTTP 请求，简单易用
 *
 * @author Jerry.Chen
 * @since 2018年12月26日 上午11:02:20
 * @see RestUtils
 * @see RestTemplate
 */
@Slf4j
public class RestClient {
    /**
     * 发送 HTTP 请求的 RestTemplate
     */
    private static RestTemplate REST_TEMPLATE;

    /**
     * 订阅 Spring 容器准备完毕的事件，初始化通用网关配置
     *
     * @param event Spring 容器准备完毕的事件
     */
    @EventListener
    public static void setCoreCommonProperties(ApplicationReadyEvent event) {
        ConfigurableApplicationContext applicationContext = event.getApplicationContext();
        Map<String, RestTemplate> restTemplateMap = applicationContext.getBeansOfType(RestTemplate.class);
        if (restTemplateMap.containsKey(WebConstants.REST_TEMPLATE4_OK_HTTP3_BEAN_NAME)) {
            RestClient.REST_TEMPLATE = restTemplateMap.get(WebConstants.REST_TEMPLATE4_OK_HTTP3_BEAN_NAME);
        } else {
            RestClient.REST_TEMPLATE = restTemplateMap.entrySet().iterator().next().getValue();
        }
    }

    /**
     * 获取发送 HTTP 请求的 RestTemplate
     *
     * @return 发送 HTTP 请求的 RestTemplate
     */
    public static RestTemplate getRestTemplate() {
        return REST_TEMPLATE;
    }

    /**
     * 自定义发送HTTP请求
     *
     * @param httpHeaders 自定义HttpHeaders
     * @param contentType Internet Media Type，互联网媒体类型
     * @param uri 请求的uri
     * @param method 请求方法类型
     * @param requestBody 请求的内容
     * @param responseType 返回的数据类型
     * @param restRequestCallback 回调函数，用于在发送请求之前，做某些处理操作；也可用于在返回结果之前，做某些处理操作
     * @param <T> 数据类型class的泛形
     * @return 请求结果
     */
    public static <T> T exchange(HttpHeaders httpHeaders, MediaType contentType, URI uri, HttpMethod method,
                                 Object requestBody, Class<T> responseType, RestRequestCallback restRequestCallback) {
        return RestUtils.exchange(REST_TEMPLATE, httpHeaders, contentType, uri, method, requestBody, responseType,
                restRequestCallback);
    }

    /**
     * 自定义发送HTTP请求
     *
     * @param httpHeaders 自定义HttpHeaders
     * @param contentType Internet Media Type，互联网媒体类型
     * @param uri 请求的uri
     * @param method 请求方法类型
     * @param requestBody 请求的内容
     * @param responseType 返回的数据类型
     * @param <T> 数据类型class的泛形
     * @return 请求结果
     */
    public static <T> T exchange(HttpHeaders httpHeaders, MediaType contentType, URI uri, HttpMethod method,
                                 Object requestBody, Class<T> responseType) {
        return RestUtils.exchange(REST_TEMPLATE, httpHeaders, contentType, uri, method, requestBody, responseType);
    }

    /**
     * 自定义发送HTTP请求
     *
     * @param contentType Internet Media Type，互联网媒体类型
     * @param uri 请求的uri
     * @param method 请求方法类型
     * @param requestBody 请求的内容
     * @param responseType 返回的数据类型
     * @param <T> 数据类型class的泛形
     * @return 请求结果
     */
    public static <T> T exchange(MediaType contentType, URI uri, HttpMethod method, Object requestBody,
                                 Class<T> responseType) {
        return RestUtils.exchange(REST_TEMPLATE, contentType, uri, method, requestBody, responseType);
    }

    /**
     * 自定义发送HTTP请求
     *
     * @param contentType Internet Media Type，互联网媒体类型
     * @param url 请求的url，url中可以包含占位符{0}, {1}, {n}
     * @param method 请求方法类型
     * @param requestBody 请求的内容
     * @param responseType 返回的数据类型
     * @param <T> 数据类型class的泛形
     * @param uriVariables url中占位符的参数值
     * @return 请求结果
     */
    public static <T> T exchange(MediaType contentType, String url, HttpMethod method, Object requestBody,
                                 Class<T> responseType, Object... uriVariables) {
        return RestUtils.exchange(REST_TEMPLATE, contentType, url, method, requestBody, responseType, uriVariables);
    }

    /**
     * 以POST方法发送HTTP请求，请求的内容为JSON
     *
     * @param url 请求的url，url中可以包含占位符{0}, {1}, {n}
     * @param requestBody 请求的内容
     * @param uriVariables url中占位符的参数值
     * @return 请求结果
     */
    public static String postJson(String url, String requestBody, Object... uriVariables) {
        return RestUtils.postJson(REST_TEMPLATE, url, requestBody, uriVariables);
    }

    /**
     * 以POST方法发送HTTP请求，请求的内容为JSON
     *
     * @param httpHeaders 自定义HttpHeaders
     * @param url 请求的url，url中可以包含占位符{0}, {1}, {n}
     * @param requestBody 请求的内容
     * @param uriVariables url中占位符的参数值
     * @return 请求结果
     */
    public static String postJson(HttpHeaders httpHeaders, String url, String requestBody, Object... uriVariables) {
        return RestUtils.postJson(REST_TEMPLATE, httpHeaders, url, requestBody, uriVariables);
    }

    /**
     * 以POST方法发送HTTP请求，请求的内容为form数据格式
     *
     * @param url 请求的url，url中可以包含占位符{0}, {1}, {n}
     * @param requestBody 请求的内容
     * @param uriVariables url中占位符的参数值
     * @return 请求结果
     */
    public static String postForm(String url, LinkedMultiValueMap<String, String> requestBody, Object... uriVariables) {
        return RestUtils.postForm(REST_TEMPLATE, url, requestBody, uriVariables);
    }

    /**
     * 以POST方法发送HTTP请求，请求的内容为form数据格式
     *
     * @param httpHeaders 自定义HttpHeaders
     * @param url 请求的url，url中可以包含占位符{0}, {1}, {n}
     * @param requestBody 请求的内容
     * @param uriVariables url中占位符的参数值
     * @return 请求结果
     */
    public static String postForm(HttpHeaders httpHeaders, String url, LinkedMultiValueMap<String, String> requestBody,
                                  Object... uriVariables) {
        return RestUtils.postForm(REST_TEMPLATE, httpHeaders, url, requestBody, uriVariables);
    }

    /**
     * 以GET方法发送HTTP请求，并在请求的url中添加动态参数 {@code parameterMap}
     *
     * @param url 请求的url，url中可以包含占位符{0}, {1}, {n}
     * @param parameterMap 请求的参数，将会添加到url中
     * @param responseType 返回的数据类型
     * @param uriVariables url中占位符的参数值
     * @param <T> 泛型
     * @return 请求结果
     */
    public static <T> T getForObject(String url, Map<String, String> parameterMap, Class<T> responseType,
                                     Object... uriVariables) {
        return RestUtils.getForObject(REST_TEMPLATE, url, parameterMap, responseType, uriVariables);
    }

    /**
     * 以GET方法发送HTTP请求，并在请求的url中添加动态参数 {@code parameterMap}
     *
     * @param httpHeaders 自定义HttpHeaders
     * @param url 请求的url，url中可以包含占位符{0}, {1}, {n}
     * @param parameterMap 请求的参数，将会添加到url中
     * @param responseType 返回的数据类型
     * @param uriVariables url中占位符的参数值
     * @param <T> 泛型
     * @return 请求结果
     */
    public static <T> T getForObject(HttpHeaders httpHeaders, String url, Map<String, String> parameterMap,
                                     Class<T> responseType, Object... uriVariables) {
        return RestUtils.getForObject(REST_TEMPLATE, httpHeaders, url, parameterMap, responseType, uriVariables);
    }

    /**
     * 以GET方法发送HTTP请求，并在请求的url中添加动态参数 {@code parameterMap}，返回的数据是字符串类型
     *
     * @param url 请求的url，url中可以包含占位符{0}, {1}, {n}
     * @param parameterMap 请求的参数，将会添加到url中
     * @param uriVariables url中占位符的参数值
     * @return 请求结果
     */
    public static String getForString(String url, Map<String, String> parameterMap, Object... uriVariables) {
        return RestUtils.getForString(REST_TEMPLATE, url, parameterMap, uriVariables);
    }

    /**
     * 以GET方法发送HTTP请求，并在请求的url中添加动态参数 {@code parameterMap}，返回的数据是字符串类型
     *
     * @param httpHeaders 自定义HttpHeaders
     * @param url 请求的url，url中可以包含占位符{0}, {1}, {n}
     * @param parameterMap 请求的参数，将会添加到url中
     * @param uriVariables url中占位符的参数值
     * @return 请求结果
     */
    public static String getForString(HttpHeaders httpHeaders, String url, Map<String, String> parameterMap,
                                      Object... uriVariables) {
        return RestUtils.getForString(REST_TEMPLATE, httpHeaders, url, parameterMap, uriVariables);
    }

    /**
     * 以GET方法发送HTTP请求，返回的数据是字符串类型
     *
     * @param url 请求的url，url中可以包含占位符{0}, {1}, {n}
     * @param uriVariables url中占位符的参数值
     * @return 请求结果
     */
    public static String getForString(String url, Object... uriVariables) {
        return RestUtils.getForString(REST_TEMPLATE, url, uriVariables);
    }

    /**
     * 以GET方法发送HTTP请求，返回的数据是字符串类型
     *
     * @param httpHeaders 自定义HttpHeaders
     * @param url 请求的url，url中可以包含占位符{0}, {1}, {n}
     * @param uriVariables url中占位符的参数值
     * @return 请求结果
     */
    public static String getForString(HttpHeaders httpHeaders, String url, Object... uriVariables) {
        return RestUtils.getForString(REST_TEMPLATE, httpHeaders, url, uriVariables);
    }

    /**
     * 以GET方法发送HTTP请求，并在请求的url中添加动态参数 {@code parameterMap}
     *
     * @param url 请求的url，url中可以包含占位符{0}, {1}, {n}
     * @param parameterMap 请求的参数，将会添加到url中
     * @param responseType 返回的数据类型
     * @param uriVariables url中占位符的参数值
     * @param <T> 泛型
     * @return 请求结果
     */
    public static <T> T getForObject(String url, LinkedMultiValueMap<String, String> parameterMap,
                                     Class<T> responseType, Object... uriVariables) {
        return RestUtils.getForObject(REST_TEMPLATE, url, parameterMap, responseType, uriVariables);
    }

    /**
     * 以GET方法发送HTTP请求，并在请求的url中添加动态参数 {@code parameterMap}
     *
     * @param httpHeaders 自定义HttpHeaders
     * @param url 请求的url，url中可以包含占位符{0}, {1}, {n}
     * @param parameterMap 请求的参数，将会添加到url中
     * @param responseType 返回的数据类型
     * @param uriVariables url中占位符的参数值
     * @param <T> 泛型
     * @return 请求结果
     */
    public static <T> T getForObject(HttpHeaders httpHeaders, String url,
                                     LinkedMultiValueMap<String, String> parameterMap, Class<T> responseType,
                                     Object... uriVariables) {
        return RestUtils.getForObject(REST_TEMPLATE, httpHeaders, url, parameterMap, responseType, uriVariables);
    }

    /**
     * 以GET方法发送HTTP请求，并在请求的url中添加动态参数 {@code parameterMap}，返回的数据是字符串类型
     *
     * @param url 请求的url，url中可以包含占位符{0}, {1}, {n}
     * @param parameterMap 请求的参数，将会添加到url中
     * @param uriVariables url中占位符的参数值
     * @return 请求结果
     */
    public static String getForString(String url, LinkedMultiValueMap<String, String> parameterMap,
                                      Object... uriVariables) {
        return RestUtils.getForString(REST_TEMPLATE, url, parameterMap, uriVariables);
    }

    /**
     * 以GET方法发送HTTP请求，并在请求的url中添加动态参数 {@code parameterMap}，返回的数据是字符串类型
     *
     * @param httpHeaders 自定义HttpHeaders
     * @param url 请求的url，url中可以包含占位符{0}, {1}, {n}
     * @param parameterMap 请求的参数，将会添加到url中
     * @param uriVariables url中占位符的参数值
     * @return 请求结果
     */
    public static String getForString(HttpHeaders httpHeaders, String url,
                                      LinkedMultiValueMap<String, String> parameterMap, Object... uriVariables) {
        return RestUtils.getForString(REST_TEMPLATE, httpHeaders, url, parameterMap, uriVariables);
    }
}
