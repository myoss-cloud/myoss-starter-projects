/*
 * Copyright 2018-2019 https://github.com/myoss
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

package app.myoss.cloud.web.reactive.spring.web.method.error;

import java.net.URI;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import com.alibaba.fastjson.JSONObject;

import app.myoss.cloud.apm.spring.cloud.sleuth.trace.ApplicationEventTracer;
import lombok.extern.slf4j.Slf4j;

/**
 * 全局Controller异常处理器，替换默认的
 * {@link org.springframework.boot.web.reactive.error.DefaultErrorAttributes}
 *
 * @author Jerry.Chen
 * @since 2019年6月21日 下午4:49:02
 * @see org.springframework.boot.web.reactive.error.DefaultErrorAttributes
 * @see org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration
 */
@ConditionalOnWebApplication(type = Type.REACTIVE)
@Slf4j
@Component
public class ControllerDefaultErrorAttributes implements ErrorAttributes {
    private static final String           ERROR_ATTRIBUTE = ControllerDefaultErrorAttributes.class.getName() + ".ERROR";
    @Autowired
    protected MonitorControllerProperties properties;

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, boolean includeStackTrace) {
        Throwable error = getError(request);
        HttpStatus errorStatus = determineHttpStatus(error);
        URI requestUrl = request.uri();
        HttpMethod method = request.method();
        String contentType = request.headers().contentType().map(MimeType::getType).orElse(null);
        if (error instanceof RestClientResponseException) {
            RestClientResponseException exception = (RestClientResponseException) error;
            // 打印出发送http请求的错误信息，帮助追踪错误源
            String responseBody = exception.getResponseBodyAsString();
            log.error(
                    "requestUrl: {}, requestMethod: {}, contentType: {}\norg.springframework.web.client.RestClientResponseException: {}, responseBody: {} ",
                    requestUrl, method, contentType, exception.getMessage(), responseBody, error);
        } else {
            log.error("requestUrl: {}, requestMethod: {}, contentType: {}", requestUrl, method, contentType, error);
        }

        String traceId = ApplicationEventTracer.getTraceId();
        String errorCode = properties.getControllerExceptionErrorCode();
        String errorMsg = properties.getControllerExceptionErrorMsg();
        JSONObject errorValue = new JSONObject(1);
        errorValue.put("traceId", traceId);
        JSONObject errorAttributes = new JSONObject();
        errorAttributes.put("status", errorStatus.value());
        errorAttributes.put("success", false);
        errorAttributes.put("errorCode", errorCode);
        errorAttributes.put("errorMsg", errorMsg);
        errorAttributes.put("value", errorValue);
        return errorAttributes;
    }

    @Override
    public Throwable getError(ServerRequest request) {
        return (Throwable) request.attribute(ERROR_ATTRIBUTE)
                .orElseThrow(() -> new IllegalStateException("Missing exception attribute in ServerWebExchange"));
    }

    @Override
    public void storeErrorInformation(Throwable error, ServerWebExchange exchange) {
        exchange.getAttributes().putIfAbsent(ERROR_ATTRIBUTE, error);
    }

    /**
     * 确定HTTP返回状态: {@link HttpStatus}
     * <ul>
     * <li>异常 class 继承:
     * {@link org.springframework.web.server.ResponseStatusException}
     * <li>异常 class 增加注解:
     * {@link org.springframework.web.bind.annotation.ResponseStatus}
     * </ul>
     *
     * @param error 异常信息
     * @return HTTP返回状态
     */
    public static HttpStatus determineHttpStatus(Throwable error) {
        if (error instanceof ResponseStatusException) {
            return ((ResponseStatusException) error).getStatus();
        }
        ResponseStatus responseStatus = AnnotatedElementUtils.findMergedAnnotation(error.getClass(),
                ResponseStatus.class);
        if (responseStatus != null) {
            return responseStatus.code();
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
