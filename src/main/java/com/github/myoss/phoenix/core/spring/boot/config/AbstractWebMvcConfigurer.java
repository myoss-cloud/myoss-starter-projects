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

package com.github.myoss.phoenix.core.spring.boot.config;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.github.myoss.phoenix.core.spring.web.servlet.filter.LogWebRequestFilter;
import com.github.myoss.phoenix.core.spring.web.servlet.filter.ReaderBodyHttpServletRequestFilter;

/**
 * Spring Web MVC配置基类
 *
 * @author Jerry.Chen
 * @since 2018年4月12日 下午5:18:07
 */
public abstract class AbstractWebMvcConfigurer implements WebMvcConfigurer {
    @Autowired
    private FastJsonConfig defaultFastJsonConfig;

    /**
     * 增加自定义的 HttpMessageConverter
     *
     * @see WebMvcConfigurationSupport#requestMappingHandlerAdapter
     * @see WebMvcConfigurationSupport#getMessageConverters
     * @see FastJsonAutoConfiguration#fastJsonHttpMessageConverter(FastJsonConfig)
     * @see FastJsonAutoConfiguration#fastJsonConfig()
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(3, FastJsonAutoConfiguration.fastJsonHttpMessageConverter(defaultFastJsonConfig));
    }

    /**
     * 注册 {@link ReaderBodyHttpServletRequestFilter} Filter
     *
     * @return 可以多次读取 {@link HttpServletRequest#getReader()} 和
     *         {@link HttpServletRequest#getInputStream()} 中的内容
     */
    @Bean
    public FilterRegistrationBean<ReaderBodyHttpServletRequestFilter> readerBodyHttpServletRequestFilter() {
        FilterRegistrationBean<ReaderBodyHttpServletRequestFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ReaderBodyHttpServletRequestFilter());
        return registration;
    }

    /**
     * 注册 {@link LogWebRequestFilter} Filter
     *
     * @return 记录web请求的日志信息过滤器
     */
    @Bean
    public FilterRegistrationBean<LogWebRequestFilter> webRequestLogFilter() {
        FilterRegistrationBean<LogWebRequestFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new LogWebRequestFilter(true, true));
        registration.setOrder(100);
        return registration;
    }
}
