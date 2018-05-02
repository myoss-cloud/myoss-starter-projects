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

package com.github.myoss.phoenix.core.constants;

import java.nio.charset.Charset;

/**
 * 项目常量
 *
 * @author Jerry.Chen 2018年4月11日 下午3:08:26
 */
public interface PhoenixConstants {
    /**
     * UTF-8: 默认的字符集
     */
    Charset DEFAULT_CHARSET        = Charset.forName("UTF-8");
    /**
     * 部署的环境变量（dev,test,pre,prd）
     *
     * @see DeployEnvEnum
     */
    String  DEPLOY_ENV             = "DEPLOY_ENV";

    /**
     * add property to MDC context "spanExportable"
     *
     * @see org.springframework.cloud.sleuth.log.Slf4jCurrentTraceContext
     */
    String  SPAN_EXPORTABLE_NAME   = "spanExportable";
    /**
     * add property to MDC context "parentId"
     *
     * @see org.springframework.cloud.sleuth.log.Slf4jCurrentTraceContext
     */
    String  PARENT_ID_NAME         = "parentId";
    /**
     * add property to MDC context "traceId"
     *
     * @see org.springframework.cloud.sleuth.log.Slf4jCurrentTraceContext
     */
    String  TRACE_ID_NAME          = "traceId";
    /**
     * add property to MDC context "spanId"
     *
     * @see org.springframework.cloud.sleuth.log.Slf4jCurrentTraceContext
     */
    String  SPAN_ID_NAME           = "spanId";

    /**
     * adding legacy "X-B3" entries to MDC context "X-B3-Export"
     *
     * @see org.springframework.cloud.sleuth.log.Slf4jCurrentTraceContext
     */
    String  LEGACY_EXPORTABLE_NAME = "X-Span-Export";
    /**
     * adding legacy "X-B3" entries to MDC context "X-B3-ParentSpanId"
     *
     * @see org.springframework.cloud.sleuth.log.Slf4jCurrentTraceContext
     */
    String  LEGACY_PARENT_ID_NAME  = "X-B3-ParentSpanId";
    /**
     * adding legacy "X-B3" entries to MDC context "X-B3-TraceId"
     *
     * @see org.springframework.cloud.sleuth.log.Slf4jCurrentTraceContext
     */
    String  LEGACY_TRACE_ID_NAME   = "X-B3-TraceId";
    /**
     * adding legacy "X-B3" entries to MDC context "X-B3-SpanId"
     *
     * @see org.springframework.cloud.sleuth.log.Slf4jCurrentTraceContext
     */
    String  LEGACY_SPAN_ID_NAME    = "X-B3-SpanId";

    /**
     * 下划线
     */
    String  UNDERLINE              = "_";
}
