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

package app.myoss.cloud.web.reactive.spring.web.server.filter;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.MDC;
import org.springframework.cloud.sleuth.instrument.web.TraceWebFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import app.myoss.cloud.apm.constants.ApmConstants;
import app.myoss.cloud.web.reactive.spring.web.method.error.ControllerDefaultErrorAttributes;
import app.myoss.cloud.web.reactive.utils.IpUtils;
import brave.Span;
import brave.internal.HexCodec;
import brave.propagation.TraceContext;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

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
 * <td>%X{remoteRealIp}</td>
 * <td>客户端的真实ip地址</td>
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
 * @author Jerry.Chen
 * @since 2018年4月11日 下午12:39:20
 */
@Slf4j(topic = "WebRequest")
public class LogWebRequestFilter implements WebFilter {
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
     * 客户端的主机名
     */
    public static final String MDC_REMOTE_HOST                   = "remoteHost";
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

    /**
     * 记录web请求的日志信息
     *
     * @param logOnFilter 是否在 {@link #filter(ServerWebExchange, WebFilterChain)}
     *            输出 log 日志，用于当前过滤器（默认值：false）
     * @param putRequestInfoToMDC 是否将请求的信息放入MDC中，默认只放 {@link #MDC_START_TIME}、
     *            {@link #MDC_COST_TIME}、{@link #MDC_STATUS} 这个key（默认值：false）
     */
    public LogWebRequestFilter(boolean logOnFilter, boolean putRequestInfoToMDC) {
        this(logOnFilter, putRequestInfoToMDC, null, null);
    }

    /**
     * 记录web请求的日志信息
     *
     * @param logOnFilter 是否在 {@link #filter(ServerWebExchange, WebFilterChain)}
     *            输出 log 日志，用于当前过滤器（默认值：false）
     * @param putRequestInfoToMDC 是否将请求的信息放入MDC中，默认只放 {@link #MDC_START_TIME}、
     *            {@link #MDC_COST_TIME}、{@link #MDC_STATUS} 这个key（默认值：false）
     * @param traceIdName {@link ApmConstants#LEGACY_TRACE_ID_NAME}
     *            输出调用链的TraceId到response header中
     * @param spanIdName {@link ApmConstants#LEGACY_SPAN_ID_NAME}
     *            输出调用链的SpanId到response header中
     */
    public LogWebRequestFilter(boolean logOnFilter, boolean putRequestInfoToMDC, String traceIdName,
                               String spanIdName) {
        this.logOnFilter = logOnFilter;
        this.putRequestInfoToMDC = putRequestInfoToMDC;
        this.traceIdName = (StringUtils.isNotBlank(traceIdName) ? traceIdName : ApmConstants.LEGACY_TRACE_ID_NAME);
        this.spanIdName = (StringUtils.isNotBlank(spanIdName) ? spanIdName : ApmConstants.LEGACY_SPAN_ID_NAME);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain filterChain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 开始时间
        long startNs = System.nanoTime();
        Date date = new Date();
        String time = DateFormatUtils.format(date, "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

        // 在请求处理之前进行调用，执行key=value的设置
        putMDC(request, time);
        // 输出traceId/spanId到response head中
        Object traceContext = exchange.getAttribute(TraceWebFilter.class.getName() + ".TRACE");
        if (traceContext != null) {
            Span span = (Span) traceContext;
            TraceContext context = span.context();
            String traceId = context.traceIdString();
            String spanId = HexCodec.toLowerHex(context.spanId());
            HttpHeaders responseHeaders = response.getHeaders();
            responseHeaders.add(this.traceIdName, traceId);
            responseHeaders.add(this.spanIdName, spanId);
            MDC.put(ApmConstants.LEGACY_TRACE_ID_NAME, traceId);
            MDC.put(ApmConstants.LEGACY_SPAN_ID_NAME, spanId);
        }

        // 调用下一个 filter
        return filterChain.filter(exchange).doOnError(throwable -> {
            Map<String, String> mdc = getMDCCopy();
            // 接口消耗时间
            long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
            putMDC(mdc, MDC_COST_TIME, String.valueOf(tookMs));

            // 状态
            HttpStatus httpStatus = ControllerDefaultErrorAttributes.determineHttpStatus(throwable);
            putMDC(mdc, MDC_STATUS, String.valueOf(httpStatus.value()));

            // 将map中的值设置到MDC中。
            MDC.setContextMap(mdc);

            // 打印日志
            if (logOnFilter) {
                log.info("");
            }

            // 在整个请求结束之后进行调用，执行清理动作
            clearMDC();
        }).doOnSuccess(aVoid -> {
            Map<String, String> mdc = getMDCCopy();
            // 接口消耗时间
            long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
            putMDC(mdc, MDC_COST_TIME, String.valueOf(tookMs));

            // 状态
            putMDC(mdc, MDC_STATUS, String.valueOf(response.getStatusCode().value()));

            // 将map中的值设置到MDC中。
            MDC.setContextMap(mdc);

            // 打印日志
            if (logOnFilter) {
                log.info("");
            }

            // 在整个请求结束之后进行调用，执行清理动作
            clearMDC();
        });
    }

    /**
     * 设置内容到MDC中
     *
     * @param request 客户端请求信息
     * @param startTime 请求开始时间
     */
    protected void putMDC(ServerHttpRequest request, String startTime) {
        Map<String, String> mdc = getMDCCopy();
        putMDC(mdc, MDC_START_TIME, startTime);
        if (!this.putRequestInfoToMDC) {
            // 将map中的值设置到MDC中。
            MDC.setContextMap(mdc);
            return;
        }
        URI uri = request.getURI();
        HttpHeaders headers = request.getHeaders();

        // GET or POST
        putMDC(mdc, MDC_METHOD, request.getMethod().name());
        StringBuilder requestURL = new StringBuilder(uri.getScheme() + "://" + uri.getRawAuthority());
        putMDC(mdc, MDC_REQUEST_SERVER_INFO, requestURL.toString());

        // request URL：完整的URL
        String queryString = StringUtils.trimToNull(uri.getRawQuery());

        putMDC(mdc, MDC_REQUEST_URL, requestURL.append(uri.getRawPath()).toString());
        putMDC(mdc, MDC_REQUEST_URL_WITH_QUERY_STRING, uri.toString());

        // request URI：不包括host信息的URL
        String requestURI = uri.getRawPath();
        String requestURIWithQueryString = (queryString != null ? requestURI + "?" + queryString : requestURI);

        putMDC(mdc, MDC_REQUEST_URI, requestURI);
        putMDC(mdc, MDC_REQUEST_URI_WITH_QUERY_STRING, requestURIWithQueryString);
        putMDC(mdc, MDC_QUERY_STRING, queryString);

        // client info
        InetSocketAddress remoteAddress = request.getRemoteAddress();
        if (remoteAddress != null && remoteAddress.getAddress() != null) {
            InetAddress address = remoteAddress.getAddress();
            putMDC(mdc, MDC_REMOTE_HOST, remoteAddress.getHostName());
            putMDC(mdc, MDC_REMOTE_ADDR, address.getHostAddress());
        }
        putMDC(mdc, MDC_REMOTE_REAL_IP, IpUtils.getIpAddress(request));

        // user agent
        putMDC(mdc, MDC_USER_AGENT, headers.getFirst("User-Agent"));

        // referrer
        putMDC(mdc, MDC_REFERRER, headers.getFirst("Referer"));

        // 将map中的值设置到MDC中。
        MDC.setContextMap(mdc);
    }

    /**
     * 设置mdc，如果value为空，则不置入。
     *
     * @param mdc {@link MDC} 属性值
     * @param key set key
     * @param value set value
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
            mdc = new HashMap<>(16);
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
            MDC.remove(MDC_REMOTE_REAL_IP);
            MDC.remove(MDC_REMOTE_HOST);
            MDC.remove(MDC_USER_AGENT);
            MDC.remove(MDC_REFERRER);
        }
    }
}
