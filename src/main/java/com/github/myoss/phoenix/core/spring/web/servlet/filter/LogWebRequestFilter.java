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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import brave.internal.HexCodec;
import brave.propagation.TraceContext;

import com.github.myoss.phoenix.core.constants.PhoenixConstants;

/**
 * 记录web请求的日志信息，设置请求的信息 到 {@link MDC Mapped Diagnostic Context(映射调试上下文)}
 * 中，用于输出到日志文件中。
 * <table border="1" cellpadding="5" summary="MDC上下文中的属性">
 * <tr>
 * <td colspan="2"><strong>请求信息</strong></td>
 * </tr>
 * <tr>
 * <td>%X{method}</td>
 * <td>请求类型：GET、POST</td>
 * </tr>
 * <tr>
 * <td>%X{requestURL}</td>
 * <td>完整的URL</td>
 * </tr>
 * <tr>
 * <td>%X{requestURLWithQueryString}</td>
 * <td>完整的URL，含querydata</td>
 * </tr>
 * <tr>
 * <td>%X{requestURI}</td>
 * <td>不包括host信息的URL</td>
 * </tr>
 * <tr>
 * <td>%X{requestURIWithQueryString}</td>
 * <td>不包括host信息的URL，含querydata</td>
 * </tr>
 * <tr>
 * <td>%X{queryString}</td>
 * <td>Querydata</td>
 * </tr>
 * <tr>
 * <td>%X{cookies}</td>
 * <td>所有cookie的名称，以逗号分隔</td>
 * </tr>
 * <tr>
 * <td>%X{cookie.*}</td>
 * <td>指定cookie的值，例如：cookie.JSESSIONID</td>
 * </tr>
 * <tr>
 * <td colspan="2"><strong>客户端信息</strong></td>
 * </tr>
 * <tr>
 * <td>%X{remoteAddr}</td>
 * <td>用户IP地址</td>
 * </tr>
 * <tr>
 * <td>%X{remoteHost}</td>
 * <td>用户域名（也可能是IP地址）</td>
 * </tr>
 * <tr>
 * <td>%X{userAgent}</td>
 * <td>用户浏览器</td>
 * </tr>
 * <tr>
 * <td>%X{referrer}</td>
 * <td>上一个链接</td>
 * </tr>
 * </table>
 *
 * @author Jerry.Chen 2018年4月11日 下午12:39:20
 */
@Slf4j(topic = "WebRequest")
public class LogWebRequestFilter extends OncePerRequestFilter {
    public static final String MDC_START_TIME                    = "startTime";
    public static final String MDC_COST_TIME                     = "costTime";
    public static final String MDC_STATUS                        = "status";
    public static final String MDC_METHOD                        = "method";
    public static final String MDC_REQUEST_SERVER_INFO           = "requestServerInfo";
    public static final String MDC_REQUEST_URL                   = "requestURL";
    public static final String MDC_REQUEST_URL_WITH_QUERY_STRING = "requestURLWithQueryString";
    public static final String MDC_REQUEST_URI                   = "requestURI";
    public static final String MDC_REQUEST_URI_WITH_QUERY_STRING = "requestURIWithQueryString";
    public static final String MDC_QUERY_STRING                  = "queryString";
    public static final String MDC_REMOTE_ADDR                   = "remoteAddr";
    public static final String MDC_REMOTE_HOST                   = "remoteHost";
    public static final String MDC_USER_AGENT                    = "userAgent";
    public static final String MDC_REFERRER                      = "referrer";
    public static final String MDC_COOKIES                       = "cookies";
    public static final String MDC_COOKIE_PREFIX                 = "cookie.";
    private boolean            logOnFilter                       = false;
    private boolean            putRequestInfoToMDC               = false;
    private String             traceIdName;
    private String             spanIdName;

    /**
     * @param logOnFilter 是否在 {@link #doFilterInternal} 输出 log
     *            日志，用于当前过滤器（默认值：false）
     * @param putRequestInfoToMDC 是否将请求的信息放入MDC中，默认只放 {@link #MDC_START_TIME}、
     *            {@link #MDC_COST_TIME}、{@link #MDC_STATUS} 这个key（默认值：false）
     */
    public LogWebRequestFilter(boolean logOnFilter, boolean putRequestInfoToMDC) {
        this(logOnFilter, putRequestInfoToMDC, null, null);
    }

    /**
     * @param logOnFilter 是否在 {@link #doFilterInternal} 输出 log
     *            日志，用于当前过滤器（默认值：false）
     * @param putRequestInfoToMDC 是否将请求的信息放入MDC中，默认只放 {@link #MDC_START_TIME}、
     *            {@link #MDC_COST_TIME}、{@link #MDC_STATUS} 这个key（默认值：false）
     * @param traceIdName {@link PhoenixConstants#LEGACY_TRACE_ID_NAME}
     *            输出调用链的TraceId到response header中
     * @param spanIdName {@link PhoenixConstants#LEGACY_SPAN_ID_NAME}
     *            输出调用链的SpanId到response header中
     */
    public LogWebRequestFilter(boolean logOnFilter, boolean putRequestInfoToMDC, String traceIdName, String spanIdName) {
        this.logOnFilter = logOnFilter;
        this.putRequestInfoToMDC = putRequestInfoToMDC;
        this.traceIdName = StringUtils.isBlank(traceIdName) ? PhoenixConstants.LEGACY_TRACE_ID_NAME : traceIdName;
        this.spanIdName = StringUtils.isBlank(spanIdName) ? PhoenixConstants.LEGACY_SPAN_ID_NAME : spanIdName;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 开始时间
        long startNs = System.nanoTime();
        Date date = new Date();
        String time = DateFormatUtils.format(date, "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        try {
            // 在请求处理之前进行调用，执行key=value的设置
            putMDC(request, time);
            // 输出traceId/spanId到response head中
            Object traceContext = request.getAttribute(TraceContext.class.getName());
            if (traceContext != null) {
                TraceContext context = (TraceContext) traceContext;
                String traceId = context.traceIdString();
                String spanId = HexCodec.toLowerHex(context.spanId());
                response.addHeader(this.traceIdName, traceId);
                response.addHeader(this.spanIdName, spanId);
            }

            // 调用下一个 filter
            filterChain.doFilter(request, response);
        } finally {
            Map<String, String> mdc = getMDCCopy();
            // 接口消耗时间
            long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
            putMDC(mdc, MDC_COST_TIME, String.valueOf(tookMs));

            // 状态
            putMDC(mdc, MDC_STATUS, String.valueOf(response.getStatus()));

            // 将map中的值设置到MDC中。
            MDC.setContextMap(mdc);

            // 打印日志
            if (logOnFilter) {
                log.info("");
            }

            // 在整个请求结束之后进行调用，执行清理动作
            clearMDC();

        }
    }

    /**
     * 设置内容到MDC中
     *
     * @param request 客户端请求信息
     * @param startTime 请求开始时间
     */
    protected void putMDC(HttpServletRequest request, String startTime) {
        Map<String, String> mdc = getMDCCopy();
        putMDC(mdc, MDC_START_TIME, startTime);
        if (!this.putRequestInfoToMDC) {
            // 将map中的值设置到MDC中。
            MDC.setContextMap(mdc);
            return;
        }

        // GET or POST
        putMDC(mdc, MDC_METHOD, request.getMethod());
        putMDC(mdc, MDC_REQUEST_SERVER_INFO,
                request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort());

        // request URL：完整的URL
        StringBuilder requestURL = new StringBuilder(request.getRequestURL());
        String queryString = StringUtils.trimToNull(request.getQueryString());

        putMDC(mdc, MDC_REQUEST_URL, getRequestURL(requestURL, null));
        putMDC(mdc, MDC_REQUEST_URL_WITH_QUERY_STRING, getRequestURL(requestURL, queryString));

        // request URI：不包括host信息的URL
        String requestURI = request.getRequestURI();
        String requestURIWithQueryString = queryString == null ? requestURI : requestURI + "?" + queryString;

        putMDC(mdc, MDC_REQUEST_URI, requestURI);
        putMDC(mdc, MDC_REQUEST_URI_WITH_QUERY_STRING, requestURIWithQueryString);
        putMDC(mdc, MDC_QUERY_STRING, queryString);

        // client info
        putMDC(mdc, MDC_REMOTE_HOST, request.getRemoteHost());
        putMDC(mdc, MDC_REMOTE_ADDR, request.getRemoteAddr());

        // user agent
        putMDC(mdc, MDC_USER_AGENT, request.getHeader("User-Agent"));

        // referrer
        putMDC(mdc, MDC_REFERRER, request.getHeader("Referer"));

        // cookies
        Cookie[] cookies = request.getCookies();
        List<String> names = Collections.emptyList();
        if (cookies != null) {
            names = new ArrayList<>(cookies.length);
            for (Cookie cookie : cookies) {
                names.add(cookie.getName());
                putMDC(mdc, MDC_COOKIE_PREFIX + cookie.getName(), cookie.getValue());
            }
            Collections.sort(names);
        }
        putMDC(mdc, MDC_COOKIES, names.toString());

        // 将map中的值设置到MDC中。
        MDC.setContextMap(mdc);
    }

    /**
     * 取得当前的request URL，包括query string。
     *
     * @return 当前请求的request URL
     */
    private String getRequestURL(StringBuilder requestURL, String queryString) {
        int length = requestURL.length();
        try {
            if (queryString != null) {
                requestURL.append('?').append(queryString);
            }
            return requestURL.toString();
        } finally {
            requestURL.setLength(length);
        }
    }

    /**
     * 设置mdc，如果value为空，则不置入。
     */
    private void putMDC(Map<String, String> mdc, String key, String value) {
        if (value != null) {
            mdc.put(key, value);
        }
    }

    /**
     * 取得当前MDC map的复本。
     *
     * @return MDC上下文中的信息
     */
    protected Map<String, String> getMDCCopy() {
        Map<String, String> mdc = MDC.getCopyOfContextMap();
        if (mdc == null) {
            mdc = new HashMap<>();
        }
        return mdc;
    }

    /**
     * 清除MDC下文中设置的信息，只有当前对象自己设置的MDC才能被清除。
     */
    public void clearMDC() {
        MDC.remove(MDC_START_TIME);
        MDC.remove(MDC_COST_TIME);
        MDC.remove(MDC_STATUS);
        if (this.putRequestInfoToMDC) {
            MDC.remove(MDC_METHOD);
            MDC.remove(MDC_REQUEST_URL);
            MDC.remove(MDC_REQUEST_URL_WITH_QUERY_STRING);
            MDC.remove(MDC_REQUEST_URI);
            MDC.remove(MDC_REQUEST_URI_WITH_QUERY_STRING);
            MDC.remove(MDC_QUERY_STRING);
            MDC.remove(MDC_REMOTE_ADDR);
            MDC.remove(MDC_REMOTE_HOST);
            MDC.remove(MDC_USER_AGENT);
            MDC.remove(MDC_REFERRER);
            MDC.remove(MDC_COOKIES);
            MDC.remove(MDC_COOKIE_PREFIX);
        }
    }
}
