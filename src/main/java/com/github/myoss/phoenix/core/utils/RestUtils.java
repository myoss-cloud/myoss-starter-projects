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

package com.github.myoss.phoenix.core.utils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.myoss.phoenix.core.constants.PhoenixConstants;
import com.github.myoss.phoenix.core.exception.BizRuntimeException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Rest API工具类
 *
 * @author Jerry.Chen
 * @since 2018年3月31日 下午7:24:50
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RestUtils {
    /**
     * 自定义发送HTTP请求
     *
     * @param restTemplate RestTemplate
     * @param httpHeaders 自定义HttpHeaders
     * @param contentType Internet Media Type，互联网媒体类型
     * @param uri 请求的uri，uri中可以包含占位符{0}, {1}, {n}
     * @param method 请求方法类型
     * @param requestBody 请求的内容
     * @param responseType 返回的数据类型
     * @param <T> 数据类型class的泛形
     * @return 请求结果
     */
    public static <T> T exchange(RestTemplate restTemplate, HttpHeaders httpHeaders, MediaType contentType, URI uri,
                                 HttpMethod method, Object requestBody, Class<T> responseType) {
        if (contentType != null) {
            httpHeaders.setContentType(contentType);
        }
        HttpEntity<?> httpEntity = new HttpEntity<>(requestBody, httpHeaders);
        log.info("requestUrl: {}, requestMethod: {}, requestBody: {}", uri, method, requestBody);
        long start = System.nanoTime();
        try {
            ResponseEntity<T> responseEntity = restTemplate.exchange(uri, method, httpEntity, responseType);
            T responseBody = responseEntity.getBody();
            long costTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            log.info("costTime: {}, responseBody: {}", costTime, responseBody);
            return responseBody;
        } catch (Exception e) {
            long costTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            if (e instanceof RestClientResponseException) {
                RestClientResponseException exception = (RestClientResponseException) e;
                // 打印出发送http请求的错误信息，帮助追踪错误源
                String exceptionBody = exception.getResponseBodyAsString();
                if (log.isInfoEnabled()) {
                    log.warn("requestUrl: {}, requestMethod: {}, costTime: {}, exceptionBody: {}", uri, method,
                            costTime, exceptionBody);
                } else {
                    log.warn("requestUrl: {}, requestMethod: {}, costTime: {}, requestBody: {}, exceptionBody: {}", uri,
                            method, costTime, requestBody, exceptionBody);
                }
            } else {
                if (log.isInfoEnabled()) {
                    log.warn("requestUrl: {}, requestMethod: {}, costTime: {}", uri, method, costTime);
                } else {
                    log.warn("requestUrl: {}, requestMethod: {}, costTime: {}, requestBody: {}", uri, method, costTime,
                            requestBody);
                }
            }
            throw e;
        }
    }

    /**
     * 自定义发送HTTP请求
     *
     * @param restTemplate RestTemplate
     * @param contentType Internet Media Type，互联网媒体类型
     * @param uri 请求的uri，uri中可以包含占位符{0}, {1}, {n}
     * @param method 请求方法类型
     * @param requestBody 请求的内容
     * @param responseType 返回的数据类型
     * @param <T> 数据类型class的泛形
     * @return 请求结果
     */
    public static <T> T exchange(RestTemplate restTemplate, MediaType contentType, URI uri, HttpMethod method,
                                 Object requestBody, Class<T> responseType) {
        return exchange(restTemplate, new HttpHeaders(), contentType, uri, method, requestBody, responseType);
    }

    /**
     * 自定义发送HTTP请求
     *
     * @param restTemplate RestTemplate
     * @param contentType Internet Media Type，互联网媒体类型
     * @param url 请求的url，url中可以包含占位符{0}, {1}, {n}
     * @param method 请求方法类型
     * @param requestBody 请求的内容
     * @param responseType 返回的数据类型
     * @param <T> 数据类型class的泛形
     * @param uriVariables url中占位符的参数值
     * @return 请求结果
     */
    public static <T> T exchange(RestTemplate restTemplate, MediaType contentType, String url, HttpMethod method,
                                 Object requestBody, Class<T> responseType, Object... uriVariables) {
        URI expanded = restTemplate.getUriTemplateHandler().expand(url, uriVariables);
        return exchange(restTemplate, contentType, expanded, method, requestBody, responseType);
    }

    /**
     * 以POST方法发送HTTP请求，请求的内容为JSON
     *
     * @param restTemplate RestTemplate
     * @param url 请求的url，url中可以包含占位符{0}, {1}, {n}
     * @param requestBody 请求的内容
     * @param uriVariables url中占位符的参数值
     * @return 请求结果
     */
    public static String postJson(RestTemplate restTemplate, String url, String requestBody, Object... uriVariables) {
        return exchange(restTemplate, MediaType.APPLICATION_JSON_UTF8, url, HttpMethod.POST, requestBody, String.class,
                uriVariables);
    }

    /**
     * 以POST方法发送HTTP请求，请求的内容为form数据格式
     *
     * @param restTemplate RestTemplate
     * @param url 请求的url，url中可以包含占位符{0}, {1}, {n}
     * @param requestBody 请求的内容
     * @param uriVariables url中占位符的参数值
     * @return 请求结果
     */
    public static String postForm(RestTemplate restTemplate, String url,
                                  LinkedMultiValueMap<String, String> requestBody, Object... uriVariables) {
        return exchange(restTemplate, MediaType.APPLICATION_FORM_URLENCODED, url, HttpMethod.POST, requestBody,
                String.class, uriVariables);
    }

    /**
     * 以GET方法发送HTTP请求，并在请求的url中添加动态参数 {@code parameterMap}
     *
     * @param restTemplate RestTemplate
     * @param url 请求的url，url中可以包含占位符{0}, {1}, {n}
     * @param parameterMap 请求的参数，将会添加到url中
     * @param responseType 返回的数据类型
     * @param uriVariables url中占位符的参数值
     * @param <T> 泛型
     * @return 请求结果
     */
    public static <T> T getForObject(RestTemplate restTemplate, String url, Map<String, String> parameterMap,
                                     Class<T> responseType, Object... uriVariables) {
        URI uri = uriBuild2(url, parameterMap, uriVariables).toUri();
        return exchange(restTemplate, null, uri, HttpMethod.GET, null, responseType);
    }

    /**
     * 以GET方法发送HTTP请求，并在请求的url中添加动态参数 {@code parameterMap}，返回的数据是字符串类型
     *
     * @param restTemplate RestTemplate
     * @param url 请求的url，url中可以包含占位符{0}, {1}, {n}
     * @param parameterMap 请求的参数，将会添加到url中
     * @param uriVariables url中占位符的参数值
     * @return 请求结果
     */
    public static String getForString(RestTemplate restTemplate, String url, Map<String, String> parameterMap,
                                      Object... uriVariables) {
        return getForObject(restTemplate, url, parameterMap, String.class, uriVariables);
    }

    /**
     * 以GET方法发送HTTP请求，返回的数据是字符串类型
     *
     * @param restTemplate RestTemplate
     * @param url 请求的url，url中可以包含占位符{0}, {1}, {n}
     * @param uriVariables url中占位符的参数值
     * @return 请求结果
     */
    public static String getForString(RestTemplate restTemplate, String url, Object... uriVariables) {
        return exchange(restTemplate, null, url, HttpMethod.GET, null, String.class, uriVariables);
    }

    /**
     * 以GET方法发送HTTP请求，并在请求的url中添加动态参数 {@code parameterMap}
     *
     * @param restTemplate RestTemplate
     * @param url 请求的url，url中可以包含占位符{0}, {1}, {n}
     * @param parameterMap 请求的参数，将会添加到url中
     * @param responseType 返回的数据类型
     * @param uriVariables url中占位符的参数值
     * @param <T> 泛型
     * @return 请求结果
     */
    public static <T> T getForObject(RestTemplate restTemplate, String url,
                                     LinkedMultiValueMap<String, String> parameterMap, Class<T> responseType,
                                     Object... uriVariables) {
        URI uri = uriBuild(url, parameterMap, uriVariables).toUri();
        return exchange(restTemplate, null, uri, HttpMethod.GET, null, responseType);
    }

    /**
     * 以GET方法发送HTTP请求，并在请求的url中添加动态参数 {@code parameterMap}，返回的数据是字符串类型
     *
     * @param restTemplate RestTemplate
     * @param url 请求的url，url中可以包含占位符{0}, {1}, {n}
     * @param parameterMap 请求的参数，将会添加到url中
     * @param uriVariables url中占位符的参数值
     * @return 请求结果
     */
    public static String getForString(RestTemplate restTemplate, String url,
                                      LinkedMultiValueMap<String, String> parameterMap, Object... uriVariables) {
        return getForObject(restTemplate, url, parameterMap, String.class, uriVariables);
    }

    /**
     * 将集合对象转换成 application/x-www-form-urlencoded 表单数据格式
     *
     * @param form FORM表单数据
     * @return FORM表单数据被编码成: {@code key1=value1&key1=value2&key2=value3}
     */
    public static StringBuilder writeForm(Map<String, List<String>> form) {
        StringBuilder builder = new StringBuilder();
        String encode = PhoenixConstants.DEFAULT_CHARSET.name();
        for (Iterator<String> nameIterator = form.keySet().iterator(); nameIterator.hasNext();) {
            String name = nameIterator.next();
            for (Iterator<String> valueIterator = form.get(name).iterator(); valueIterator.hasNext();) {
                String value = valueIterator.next();
                try {
                    builder.append(URLEncoder.encode(name, encode));
                    if (value != null) {
                        builder.append('=');
                        builder.append(URLEncoder.encode(value, encode));
                        if (valueIterator.hasNext()) {
                            builder.append('&');
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    throw new BizRuntimeException(e);
                }
            }
            if (nameIterator.hasNext()) {
                builder.append('&');
            }
        }
        return builder;
    }

    /**
     * 将集合对象转换成 application/x-www-form-urlencoded 表单数据格式
     *
     * @param form FORM表单数据
     * @return FORM表单数据被编码成: {@code key1=value1&key1=value2&key2=value3}
     */
    public static StringBuilder writeForm2(Map<String, String> form) {
        StringBuilder builder = new StringBuilder();
        String encode = PhoenixConstants.DEFAULT_CHARSET.name();
        for (Iterator<Entry<String, String>> entryIterator = form.entrySet().iterator(); entryIterator.hasNext();) {
            Entry<String, String> next = entryIterator.next();
            String name = next.getKey();
            String value = next.getValue();
            if (value != null) {
                try {
                    builder.append(URLEncoder.encode(name, encode));
                    builder.append('=');
                    builder.append(URLEncoder.encode(value, encode));
                } catch (UnsupportedEncodingException e) {
                    throw new BizRuntimeException(e);
                }
            }
            if (entryIterator.hasNext()) {
                builder.append('&');
            }
        }
        return builder;
    }

    /**
     * 构造和编码URI
     *
     * @param url 原始url
     * @param parameterMap 请求的参数，将会添加到url中
     * @param uriVariables uri中占位符的参数值
     * @return 替换后的URI
     */
    public static UriComponents uriBuild(String url, Map<String, List<String>> parameterMap, Object... uriVariables) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url);
        if (!CollectionUtils.isEmpty(parameterMap)) {
            for (Entry<String, List<String>> entry : parameterMap.entrySet()) {
                String key = entry.getKey();
                List<String> values = entry.getValue();
                for (String value : values) {
                    uriComponentsBuilder.queryParam(key, value);
                }
            }
        }
        return (ArrayUtils.isNotEmpty(uriVariables) ? uriComponentsBuilder.buildAndExpand(uriVariables)
                : uriComponentsBuilder.build());
    }

    /**
     * 构造和编码URI
     *
     * @param url 原始url
     * @param parameterMap 请求的参数，将会添加到url中
     * @param uriVariables uri中占位符的参数值
     * @return 替换后的URI
     */
    public static UriComponents uriBuild2(String url, Map<String, String> parameterMap, Object... uriVariables) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url);
        if (!CollectionUtils.isEmpty(parameterMap)) {
            for (Entry<String, String> entry : parameterMap.entrySet()) {
                uriComponentsBuilder.queryParam(entry.getKey(), entry.getValue());
            }
        }
        return (ArrayUtils.isNotEmpty(uriVariables) ? uriComponentsBuilder.buildAndExpand(uriVariables)
                : uriComponentsBuilder.build());
    }
}
