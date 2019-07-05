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

package app.myoss.cloud.web.spring.web.servlet.filter;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;

import app.myoss.cloud.apm.constants.ApmConstants;
import app.myoss.cloud.web.utils.IpUtils;
import brave.internal.HexCodec;
import brave.propagation.TraceContext;
import lombok.extern.slf4j.Slf4j;

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
 * <td>%X{remoteAddr}</td>
 * <td>用户IP地址</td>
 * </tr>
 * <tr>
 * <td>%X{remoteRealIp}</td>
 * <td>客户端的真实ip地址</td>
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
 * @author Jerry.Chen
 * @since 2018年4月11日 下午12:39:20
 */
@Slf4j(topic = "WebRequest")
public class LogWebRequestFilter extends OncePerRequestFilter {
    /**
     * HTTP request start time
     */
    public static final String MDC_START_TIME                    = "startTime";
    /**
     * HTTP cost time
     */
    public static final String MDC_COST_TIME                     = "costTime";
    /**
     * HTTP response status
     */
    public static final String MDC_STATUS                        = "status";
    /**
     * HTTP request method
     */
    public static final String MDC_METHOD                        = "method";
    /**
     * 请求服务器域名+接口地址信息
     */
    public static final String MDC_REQUEST_SERVER_INFO           = "requestServerInfo";
    /**
     * 取得当前的request URL，不包括query string
     */
    public static final String MDC_REQUEST_URL                   = "requestURL";
    /**
     * 取得当前的request URL，包括query string
     */
    public static final String MDC_REQUEST_URL_WITH_QUERY_STRING = "requestURLWithQueryString";
    /**
     * 不包括host信息的URL
     */
    public static final String MDC_REQUEST_URI                   = "requestURI";
    /**
     * 不包括host信息的URL，包括query string
     */
    public static final String MDC_REQUEST_URI_WITH_QUERY_STRING = "requestURIWithQueryString";
    /**
     * query string
     */
    public static final String MDC_QUERY_STRING                  = "queryString";
    /**
     * 客户端的ip地址（如果服务前面是 NGINX 转发过来的，那么就是它的 ip 地址）
     */
    public static final String MDC_REMOTE_ADDR                   = "remoteAddr";
    /**
     * 客户端的真实ip地址
     */
    public static final String MDC_REMOTE_REAL_IP                = "remoteRealIp";
    /**
     * user agent
     */
    public static final String MDC_USER_AGENT                    = "userAgent";
    /**
     * referrer
     */
    public static final String MDC_REFERRER                      = "referrer";

    private boolean            logOnFilter                       = false;
    private boolean            putRequestInfoToMDC               = false;
    private String             traceIdName;
    private String             spanIdName;
    private FastDateFormat     dateFormat;

    /**
     * 记录web请求的日志信息
     *
     * @param logOnFilter 是否在 {@link #doFilterInternal} 输出 log
     *            日志，用于当前过滤器（默认值：false）
     * @param putRequestInfoToMDC 是否将请求的信息放入MDC中，默认只放 {@link #MDC_START_TIME}、
     *            {@link #MDC_COST_TIME}、{@link #MDC_STATUS} 这个key（默认值：false）
     */
    public LogWebRequestFilter(boolean logOnFilter, boolean putRequestInfoToMDC) {
        this(logOnFilter, putRequestInfoToMDC, null, null, "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    }

    /**
     * 记录web请求的日志信息
     *
     * @param logOnFilter 是否在 {@link #doFilterInternal} 输出 log
     *            日志，用于当前过滤器（默认值：false）
     * @param putRequestInfoToMDC 是否将请求的信息放入MDC中，默认只放 {@link #MDC_START_TIME}、
     *            {@link #MDC_COST_TIME}、{@link #MDC_STATUS} 这个key（默认值：false）
     * @param traceIdName {@link ApmConstants#LEGACY_TRACE_ID_NAME}
     *            输出调用链的TraceId到response header中
     * @param spanIdName {@link ApmConstants#LEGACY_SPAN_ID_NAME}
     *            输出调用链的SpanId到response header中
     * @param pattern {@link #MDC_START_TIME} 日期格式化 pattern
     */
    public LogWebRequestFilter(boolean logOnFilter, boolean putRequestInfoToMDC, String traceIdName, String spanIdName,
                               String pattern) {
        this.logOnFilter = logOnFilter;
        this.putRequestInfoToMDC = putRequestInfoToMDC;
        this.traceIdName = (StringUtils.isNotBlank(traceIdName) ? traceIdName : ApmConstants.LEGACY_TRACE_ID_NAME);
        this.spanIdName = (StringUtils.isNotBlank(spanIdName) ? spanIdName : ApmConstants.LEGACY_SPAN_ID_NAME);
        this.dateFormat = FastDateFormat.getInstance(pattern, null, null);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 开始时间
        long startNs = System.nanoTime();
        Date date = new Date();
        String time = dateFormat.format(date);
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
            // 接口消耗时间
            long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
            putMDC(MDC_COST_TIME, String.valueOf(tookMs));

            // 状态
            putMDC(MDC_STATUS, String.valueOf(response.getStatus()));

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
        putMDC(MDC_START_TIME, startTime);
        if (!this.putRequestInfoToMDC) {
            return;
        }

        // GET or POST
        putMDC(MDC_METHOD, request.getMethod());
        putMDC(MDC_REQUEST_SERVER_INFO,
                request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort());

        // request URL：完整的URL
        StringBuilder requestURL = new StringBuilder(request.getRequestURL());
        String queryString = StringUtils.trimToNull(request.getQueryString());

        putMDC(MDC_REQUEST_URL, getRequestURL(requestURL, null));
        putMDC(MDC_REQUEST_URL_WITH_QUERY_STRING, getRequestURL(requestURL, queryString));

        // request URI：不包括host信息的URL
        String requestURI = request.getRequestURI();
        String requestURIWithQueryString = (queryString != null ? requestURI + "?" + queryString : requestURI);

        putMDC(MDC_REQUEST_URI, requestURI);
        putMDC(MDC_REQUEST_URI_WITH_QUERY_STRING, requestURIWithQueryString);
        putMDC(MDC_QUERY_STRING, queryString);

        // client info
        putMDC(MDC_REMOTE_ADDR, request.getRemoteAddr());
        putMDC(MDC_REMOTE_REAL_IP, IpUtils.getIpAddress(request));

        // user agent
        putMDC(MDC_USER_AGENT, request.getHeader("User-Agent"));

        // referrer
        putMDC(MDC_REFERRER, request.getHeader("Referer"));
    }

    /**
     * 取得当前的request URL，包括query string。
     *
     * @param requestURL request URL
     * @param queryString query string
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
     *
     * @param key set key
     * @param value set value
     */
    private void putMDC(String key, String value) {
        if (value != null) {
            MDC.put(key, value);
        }
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
            MDC.remove(MDC_REMOTE_REAL_IP);
            MDC.remove(MDC_USER_AGENT);
            MDC.remove(MDC_REFERRER);
        }
    }
}
