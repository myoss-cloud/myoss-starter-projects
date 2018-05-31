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

package com.github.myoss.phoenix.core.log.method.aspectj;

import java.io.IOException;
import java.net.URI;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import com.alibaba.fastjson.JSON;
import com.github.myoss.phoenix.core.constants.PhoenixConstants;
import com.github.myoss.phoenix.core.log.method.aspectj.annotation.EnableAopLogMethod;
import com.github.myoss.phoenix.core.spring.cloud.sleuth.trace.ApplicationEventTracer;
import com.github.myoss.phoenix.core.spring.web.servlet.support.EmptyBodyCheckingHttpInputMessage;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局Controller异常处理器
 * <p>
 * Spring Boot官方文档给出的示例是直接继承{@link ResponseEntityExceptionHandler}，但是此类中的方法
 * {@link ResponseEntityExceptionHandler#handleException(Exception, WebRequest)}
 * 是final的， 无法进行覆盖 （不覆盖将会导致此方法上捕获的异常无法在使用AOP拦截），并且没有将异常输出到log中。通过重写方法
 * {@link ResponseEntityExceptionHandler#handleExceptionInternal(Exception, Object, HttpHeaders, HttpStatus, WebRequest)}
 * ，因为所有的异常处理最后一步都会走到这里，对外的错误异常统一输出，完美解决。
 * <p>
 * 如果要开启此功能，{@link EnableAopLogMethod}
 * <p>
 * 如果要禁用掉此功能，{@link EnableAopLogMethod#enableAopLogControllerException()}
 *
 * @author Jerry.Chen
 * @since 2018年4月11日 下午12:10:23
 * @see ResponseEntityExceptionHandler
 * @see EnableAopLogMethod
 */
@ConditionalOnWebApplication
@Slf4j
@ControllerAdvice
public class AopLogControllerExceptionHandler extends ResponseEntityExceptionHandler {
    @Autowired
    protected MonitorMethodProperties properties;

    public static HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }

    /**
     * 不在页面暴露具体的异常信息
     *
     * @param ex 异常信息
     * @param request 客户端请求信息
     * @return 服务端返回信息
     */
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public ResponseEntity<Object> handleException(Throwable ex, HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        return outputException(ex, new HttpHeaders(), status, request);
    }

    /**
     * {@link ResponseEntityExceptionHandler#handleException(Exception, WebRequest)}
     * 是final的， 无法进行覆盖 （不覆盖将会导致此方法上捕获的异常无法在使用AOP拦截），并且没有将异常输出到log中。通过重写方法
     * {@link ResponseEntityExceptionHandler#handleExceptionInternal(Exception, Object, HttpHeaders, HttpStatus, WebRequest)}
     * ，因为所有的异常处理最后一步都会走到这里，对外的错误异常统一输出，完美解决。
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object responseBody, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }
        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();
        return outputException(ex, headers, status, servletRequest);
    }

    /**
     * 对外的错误异常统一输出JSON字符串，并且包含traceId，方便跟踪错误日志
     *
     * @param ex 异常信息
     * @param headers 服务端返回的HttpHeaders信息
     * @param status 服务端返回的状态码
     * @param servletRequest 客户端请求信息
     * @return 服务端返回信息
     */
    protected ResponseEntity<Object> outputException(Throwable ex, HttpHeaders headers, HttpStatus status,
                                                     HttpServletRequest servletRequest) {
        ServletServerHttpRequest serverHttpRequest = new ServletServerHttpRequest(servletRequest);
        URI requestURI = serverHttpRequest.getURI();
        String requestBody = null;
        try {
            EmptyBodyCheckingHttpInputMessage inputMessage = new EmptyBodyCheckingHttpInputMessage(serverHttpRequest);
            if (inputMessage.hasBody()) {
                requestBody = StreamUtils.copyToString(inputMessage.getBody(), PhoenixConstants.DEFAULT_CHARSET);
            } else if (!CollectionUtils.isEmpty(servletRequest.getParameterMap())) {
                requestBody = JSON.toJSONString(servletRequest.getParameterMap());
            }
        } catch (IOException e) {
            log.error("Could not read document", e);
        }
        HttpMethod method = serverHttpRequest.getMethod();
        String contentType = servletRequest.getContentType();
        if (ex instanceof RestClientResponseException) {
            RestClientResponseException exception = (RestClientResponseException) ex;
            // 打印出发送http请求的错误信息，帮助追踪错误源
            String responseBody = exception.getResponseBodyAsString();
            log.error(
                    "requestUrl: {}, requestMethod: {}, requestBody: {}, contentType: {}\norg.springframework.web.client.RestClientResponseException: {}, responseBody: {} ",
                    requestURI, method, requestBody, contentType, exception.getMessage(), responseBody, ex);
        } else {
            log.error("requestUrl: {}, requestMethod: {}, requestBody: {}, contentType: {}", requestURI, method,
                    requestBody, contentType, ex);
        }
        String traceId = ApplicationEventTracer.getTraceId();
        String errorCode = properties.getControllerExceptionErrorCode();
        String errorMsg = properties.getControllerExceptionErrorMsg();
        Object body = "{\"success\":false,\"errorCode\":\"" + errorCode + "\",\"errorMsg\":\"" + errorMsg
                + "\",\"value\":{\"traceId\":\"" + traceId + "\"}}";
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return ResponseEntity.status(status).headers(headers).body(body);
    }
}
