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

package com.github.myoss.phoenix.core.spring.web.servlet.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import com.github.myoss.phoenix.core.spring.web.servlet.support.ReaderBodyHttpServletRequestWrapper;

/**
 * 将{@link HttpServletRequest}进行包装为 {@link ReaderBodyHttpServletRequestWrapper}
 * ，使下面这两个方法支持多次读取，默认只能读取一次
 * <ul>
 * <li> {@link ReaderBodyHttpServletRequestWrapper#getReader()}
 * <li> {@link ReaderBodyHttpServletRequestWrapper#getInputStream()}
 * </ul>
 *
 * @author Jerry.Chen
 * @since 2018年4月11日 下午3:06:41
 * @see ReaderBodyHttpServletRequestWrapper
 */
public class ReaderBodyHttpServletRequestFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ServletRequest requestWrapper = new ReaderBodyHttpServletRequestWrapper(request);
        filterChain.doFilter(requestWrapper, response);
    }
}
