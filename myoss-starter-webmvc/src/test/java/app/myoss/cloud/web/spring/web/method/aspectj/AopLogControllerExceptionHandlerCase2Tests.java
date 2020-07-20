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

package app.myoss.cloud.web.spring.web.method.aspectj;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.OutputCaptureRule;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.mock.web.DelegatingServletInputStream;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.util.WebUtils;

import app.myoss.cloud.core.constants.MyossConstants;
import app.myoss.cloud.core.exception.BizRuntimeException;
import app.myoss.cloud.core.lang.json.JsonApi;
import app.myoss.cloud.web.spring.boot.config.AbstractWebMvcConfigurer;
import app.myoss.cloud.web.spring.web.method.aspectj.annatation.EnableAopLogController;
import app.myoss.cloud.web.spring.web.servlet.filter.ReaderBodyHttpServletRequestFilter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * 测试开启自动记录 controller 异常，使用 {@link EnableAopLogController} 开启此功能，使用 Jackson 进行
 * json 的序列化、反序列化
 *
 * @author Jerry.Chen
 * @since 2020年6月5日 下午4:50:20
 * @see AopLogControllerExceptionHandler
 * @see EnableAopLogController
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
public class AopLogControllerExceptionHandlerCase2Tests {
    @Rule
    public OutputCaptureRule      output = new OutputCaptureRule();
    @Autowired
    private WebApplicationContext context;
    private MockMvc               mvc;

    @Before
    public void setUp() {
        this.mvc = MockMvcBuilders.webAppContextSetup(this.context)
                .addFilter(new ReaderBodyHttpServletRequestFilter())
                .build();

    }

    @Test
    public void isInjectComponent() {
        context.getBean("readerBodyHttpServletRequestFilter", FilterRegistrationBean.class);
    }

    /**
     * 正常的请求，正常返回
     */
    @Test
    public void okTest1() throws Exception {
        User user = getUser();
        ResultActions actions = this.mvc.perform(MockMvcRequestBuilders.post("/user")
                .characterEncoding("UTF-8")
                .content(JsonApi.toJson(user))
                .contentType(MediaType.APPLICATION_JSON));
        actions.andDo(MockMvcResultHandlers.print());
        actions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string("OK"));
    }

    /**
     * 异常的请求，{@link AopLogControllerExceptionHandler} 抓取到请求的内容，并且使用log输出异常信息
     */
    @Test
    public void errorPostTest1() throws Exception {
        User user = getUser();
        String content = "errorJson" + JsonApi.toJson(user) + "123456789";
        ResultActions actions = this.mvc.perform(MockMvcRequestBuilders.post("/user?key1=value1")
                .characterEncoding("UTF-8")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON));
        actions.andDo(MockMvcResultHandlers.print());
        String printLog = this.output.toString();
        Assertions.assertThat(printLog)
                .contains("[" + ClassUtils.getQualifiedName(AopLogControllerExceptionHandler.class) + "]",
                        "requestUrl: ", "requestMethod: ", "requestBody: ", "contentType: ",
                        "requestUrl: http://localhost/user?key1=value1, requestMethod: POST, requestBody: " + content
                                + ", contentType: application/json");
        actions.andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("errorMsg").value("We'll be back soon ..."))
                .andExpect(MockMvcResultMatchers.jsonPath("value").isMap());
    }

    /**
     * 正常的请求，正常返回
     */
    @Test
    public void okTest2() throws Exception {
        User user = getUser();
        user.setId(1);
        ResultActions actions = this.mvc.perform(MockMvcRequestBuilders.get("/user/{0}", user.getId()));
        actions.andDo(MockMvcResultHandlers.print());
        actions.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().string(JsonApi.toJson(user)));
    }

    /**
     * 异常的请求，{@link AopLogControllerExceptionHandler} 抓取到请求的内容，并且使用log输出异常信息
     */
    @Test
    public void errorGetTest1() throws Exception {
        ResultActions actions = this.mvc.perform(MockMvcRequestBuilders.get("/user/1xx"));
        actions.andDo(MockMvcResultHandlers.print());
        String printLog = this.output.toString();
        Assertions.assertThat(printLog)
                .contains("[" + ClassUtils.getQualifiedName(AopLogControllerExceptionHandler.class) + "]",
                        "requestUrl: ", "requestMethod: ", "requestBody: ", "contentType: ",
                        "requestUrl: http://localhost/user/1xx, requestMethod: GET, requestBody: null, contentType: null");
        actions.andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("errorMsg").value("We'll be back soon ..."))
                .andExpect(MockMvcResultMatchers.jsonPath("value.traceId").isString());
    }

    @Test
    public void handleExceptionTest1() {
        AopLogControllerExceptionHandler handler = context.getBean(AopLogControllerExceptionHandler.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("javax.servlet.error.status_code", HttpStatus.BAD_REQUEST.value());
        BizRuntimeException bizRuntimeException = new BizRuntimeException("mock exception");
        ResponseEntity<Object> responseEntity = handler.handleException(bizRuntimeException, request);

        String printLog = this.output.toString();
        Assertions.assertThat(printLog)
                .contains(" ERROR [" + ClassUtils.getQualifiedName(AopLogControllerExceptionHandler.class) + "]",
                        "[AopLogControllerExceptionHandler.java",
                        "requestUrl: http://localhost, requestMethod: null, requestBody: null, contentType: nul",
                        ExceptionUtils.getStackTrace(bizRuntimeException));
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        Assertions.assertThat(responseEntity.getBody())
                .isEqualTo(
                        "{\"success\":false,\"errorCode\":\"systemException\",\"errorMsg\":\"We'll be back soon ...\",\"value\":{\"traceId\":\"null\"}}");
    }

    @Test
    public void handleExceptionTest2() {
        AopLogControllerExceptionHandler handler = context.getBean(AopLogControllerExceptionHandler.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod(HttpMethod.POST.name());
        request.setContentType(MediaType.TEXT_PLAIN_VALUE);
        request.setContent("hello world".getBytes(MyossConstants.DEFAULT_CHARSET));
        BizRuntimeException bizRuntimeException = new BizRuntimeException("mock exception");
        ResponseEntity<Object> responseEntity = handler.handleException(bizRuntimeException, request);

        String printLog = this.output.toString();
        Assertions.assertThat(printLog)
                .contains(" ERROR [" + ClassUtils.getQualifiedName(AopLogControllerExceptionHandler.class) + "]",
                        "[AopLogControllerExceptionHandler.java",
                        "requestUrl: http://localhost, requestMethod: POST, requestBody: hello world, contentType: text/plain",
                        ExceptionUtils.getStackTrace(bizRuntimeException));
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        Assertions.assertThat(responseEntity.getBody())
                .isEqualTo(
                        "{\"success\":false,\"errorCode\":\"systemException\",\"errorMsg\":\"We'll be back soon ...\",\"value\":{\"traceId\":\"null\"}}");
    }

    @Test
    public void handleExceptionTest3() {
        AopLogControllerExceptionHandler handler = context.getBean(AopLogControllerExceptionHandler.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod(HttpMethod.POST.name());
        request.setContentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        request.setParameter("key", "value");
        request.setParameter("name", "Jerry");
        Map<String, String[]> parameterMap = request.getParameterMap();
        Assertions.assertThat(JsonApi.toJson(parameterMap)).isEqualTo("{\"key\":[\"value\"],\"name\":[\"Jerry\"]}");
        BizRuntimeException bizRuntimeException = new BizRuntimeException("mock exception");
        ResponseEntity<Object> responseEntity = handler.handleException(bizRuntimeException, request);

        String printLog = this.output.toString();
        Assertions.assertThat(printLog)
                .contains(" ERROR [" + ClassUtils.getQualifiedName(AopLogControllerExceptionHandler.class) + "]",
                        "[AopLogControllerExceptionHandler.java",
                        "requestUrl: http://localhost, requestMethod: POST, requestBody: key=value&name=Jerry, contentType: application/x-www-form-urlencoded",
                        ExceptionUtils.getStackTrace(bizRuntimeException));
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        Assertions.assertThat(responseEntity.getBody())
                .isEqualTo(
                        "{\"success\":false,\"errorCode\":\"systemException\",\"errorMsg\":\"We'll be back soon ...\",\"value\":{\"traceId\":\"null\"}}");
    }

    @Test
    public void handleExceptionTest4() {
        AopLogControllerExceptionHandler handler = context.getBean(AopLogControllerExceptionHandler.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod(HttpMethod.GET.name());
        request.setParameter("key", "value");
        request.setParameter("name", "Jerry");
        Map<String, String[]> parameterMap = request.getParameterMap();
        Assertions.assertThat(JsonApi.toJson(parameterMap)).isEqualTo("{\"key\":[\"value\"],\"name\":[\"Jerry\"]}");
        BizRuntimeException bizRuntimeException = new BizRuntimeException("mock exception");
        ResponseEntity<Object> responseEntity = handler.handleException(bizRuntimeException, request);

        String printLog = this.output.toString();
        Assertions.assertThat(printLog)
                .contains(" ERROR [" + ClassUtils.getQualifiedName(AopLogControllerExceptionHandler.class) + "]",
                        "[AopLogControllerExceptionHandler.java",
                        "requestUrl: http://localhost, requestMethod: GET, requestBody: {\"key\":[\"value\"],\"name\":[\"Jerry\"]}, contentType: null",
                        ExceptionUtils.getStackTrace(bizRuntimeException));
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        Assertions.assertThat(responseEntity.getBody())
                .isEqualTo(
                        "{\"success\":false,\"errorCode\":\"systemException\",\"errorMsg\":\"We'll be back soon ...\",\"value\":{\"traceId\":\"null\"}}");
    }

    @Test
    public void handleExceptionInternalTest1() {
        AopLogControllerExceptionHandler handler = context.getBean(AopLogControllerExceptionHandler.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        BizRuntimeException bizRuntimeException = new BizRuntimeException("mock exception");
        WebRequest webRequest = new ServletWebRequest(request);
        ResponseEntity<Object> responseEntity = handler.handleExceptionInternal(bizRuntimeException, null,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, webRequest);

        String printLog = this.output.toString();
        Assertions.assertThat(printLog)
                .contains(" ERROR [" + ClassUtils.getQualifiedName(AopLogControllerExceptionHandler.class) + "]",
                        "[AopLogControllerExceptionHandler.java",
                        "requestUrl: http://localhost, requestMethod: null, requestBody: null, contentType: nul",
                        ExceptionUtils.getStackTrace(bizRuntimeException));
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        Assertions.assertThat(responseEntity.getBody())
                .isEqualTo(
                        "{\"success\":false,\"errorCode\":\"systemException\",\"errorMsg\":\"We'll be back soon ...\",\"value\":{\"traceId\":\"null\"}}");
        Assertions.assertThat(request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE)).isNull();
    }

    @Test
    public void handleExceptionInternalTest2() {
        AopLogControllerExceptionHandler handler = context.getBean(AopLogControllerExceptionHandler.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod(HttpMethod.POST.name());
        request.setContentType(MediaType.TEXT_PLAIN_VALUE);
        request.setContent("hello world".getBytes(MyossConstants.DEFAULT_CHARSET));
        BizRuntimeException bizRuntimeException = new BizRuntimeException("mock exception");
        WebRequest webRequest = new ServletWebRequest(request);
        ResponseEntity<Object> responseEntity = handler.handleExceptionInternal(bizRuntimeException, null,
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, webRequest);

        String printLog = this.output.toString();
        Assertions.assertThat(printLog)
                .contains(" ERROR [" + ClassUtils.getQualifiedName(AopLogControllerExceptionHandler.class) + "]",
                        "[AopLogControllerExceptionHandler.java",
                        "requestUrl: http://localhost, requestMethod: POST, requestBody: hello world, contentType: text/plain",
                        ExceptionUtils.getStackTrace(bizRuntimeException));
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        Assertions.assertThat(responseEntity.getBody())
                .isEqualTo(
                        "{\"success\":false,\"errorCode\":\"systemException\",\"errorMsg\":\"We'll be back soon ...\",\"value\":{\"traceId\":\"null\"}}");
        Assertions.assertThat(request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE)).isEqualTo(bizRuntimeException);
    }

    @Test
    public void handleExceptionInternalTest3() {
        AopLogControllerExceptionHandler handler = context.getBean(AopLogControllerExceptionHandler.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod(HttpMethod.POST.name());
        request.setContentType(MediaType.TEXT_PLAIN_VALUE);
        request.setContent("hello world".getBytes(MyossConstants.DEFAULT_CHARSET));
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        RestClientResponseException restClientResponseException = new RestClientResponseException("mock exception",
                HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), responseHeaders,
                "invalid parameter".getBytes(MyossConstants.DEFAULT_CHARSET), MyossConstants.DEFAULT_CHARSET);
        WebRequest webRequest = new ServletWebRequest(request);
        ResponseEntity<Object> responseEntity = handler.handleExceptionInternal(restClientResponseException, null,
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, webRequest);

        String printLog = this.output.toString();
        Assertions.assertThat(printLog)
                .contains(" ERROR [" + ClassUtils.getQualifiedName(AopLogControllerExceptionHandler.class) + "]",
                        "[AopLogControllerExceptionHandler.java",
                        "requestUrl: http://localhost, requestMethod: POST, requestBody: hello world, contentType: text/plain",
                        "org.springframework.web.client.RestClientResponseException: mock exception, responseBody: invalid parameter ",
                        ExceptionUtils.getStackTrace(restClientResponseException));
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        Assertions.assertThat(responseEntity.getBody())
                .isEqualTo(
                        "{\"success\":false,\"errorCode\":\"systemException\",\"errorMsg\":\"We'll be back soon ...\",\"value\":{\"traceId\":\"null\"}}");
        Assertions.assertThat(request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE))
                .isEqualTo(restClientResponseException);
    }

    @Test
    public void handleExceptionInternalTest4() {
        AopLogControllerExceptionHandler handler = context.getBean(AopLogControllerExceptionHandler.class);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod(HttpMethod.POST.name());
        request.setContentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        request.setParameter("key", "value");
        request.setParameter("name", "Jerry");
        BizRuntimeException bizRuntimeException = new BizRuntimeException("mock exception");
        WebRequest webRequest = new ServletWebRequest(request);
        ResponseEntity<Object> responseEntity = handler.handleExceptionInternal(bizRuntimeException, null,
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, webRequest);

        String printLog = this.output.toString();
        Assertions.assertThat(printLog)
                .contains(" ERROR [" + ClassUtils.getQualifiedName(AopLogControllerExceptionHandler.class) + "]",
                        "[AopLogControllerExceptionHandler.java",
                        "requestUrl: http://localhost, requestMethod: POST, requestBody: key=value&name=Jerry, contentType: application/x-www-form-urlencoded",
                        ExceptionUtils.getStackTrace(bizRuntimeException));
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        Assertions.assertThat(responseEntity.getBody())
                .isEqualTo(
                        "{\"success\":false,\"errorCode\":\"systemException\",\"errorMsg\":\"We'll be back soon ...\",\"value\":{\"traceId\":\"null\"}}");
        Assertions.assertThat(request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE)).isEqualTo(bizRuntimeException);
    }

    @Test
    public void handleExceptionInternalTest5() {
        AopLogControllerExceptionHandler handler = context.getBean(AopLogControllerExceptionHandler.class);
        MockHttpServletRequest request = Mockito.mock(MockHttpServletRequest.class);
        Mockito.when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost"));
        Mockito.when(request.getMethod()).thenReturn(HttpMethod.POST.name());
        Mockito.when(request.getHeaderNames()).thenReturn(new Enumeration<String>() {
            @Override
            public boolean hasMoreElements() {
                return false;
            }

            @Override
            public String nextElement() {
                return null;
            }
        });
        Mockito.when(request.getContentType()).thenReturn(MediaType.TEXT_PLAIN_VALUE);
        Mockito.when(request.getInputStream()).thenReturn(new DelegatingServletInputStream(new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("mock io exception");
            }
        }));
        BizRuntimeException bizRuntimeException = new BizRuntimeException("mock exception");
        WebRequest webRequest = new ServletWebRequest(request);
        ResponseEntity<Object> responseEntity = handler.handleExceptionInternal(bizRuntimeException, null,
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, webRequest);

        String printLog = this.output.toString();
        Assertions.assertThat(printLog)
                .contains("Could not read document", "java.io.IOException: mock io exception",
                        " ERROR [" + ClassUtils.getQualifiedName(AopLogControllerExceptionHandler.class) + "]",
                        "[AopLogControllerExceptionHandler.java",
                        "requestUrl: http://localhost, requestMethod: POST, requestBody: null, contentType: text/plain",
                        ExceptionUtils.getStackTrace(bizRuntimeException));
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertThat(responseEntity.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        Assertions.assertThat(responseEntity.getBody())
                .isEqualTo(
                        "{\"success\":false,\"errorCode\":\"systemException\",\"errorMsg\":\"We'll be back soon ...\",\"value\":{\"traceId\":\"null\"}}");
    }

    private static User getUser() {
        User user = new User();
        user.setUserName("Jerry");
        user.setAge(18);
        return user;
    }

    @Slf4j
    @EnableAopLogController
    @Controller
    @Configuration
    @EnableWebMvc
    protected static class Config extends AbstractWebMvcConfigurer {
        @Override
        public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

        }

        @ResponseBody
        @PostMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
        public String saveUser(@RequestBody User user) {
            log.info("receiver user is: {}", user);
            return "OK";
        }

        @ResponseBody
        @GetMapping(value = "/user/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
        public User findUser(@PathVariable("id") Integer id) {
            User user = getUser();
            user.setId(id);
            return user;
        }

    }

    @Data
    private static class User {
        Integer id;
        String  userName;
        Integer age;

    }
}
