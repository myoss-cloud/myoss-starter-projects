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

package io.myoss.phoenix.core.spring.cloud.sleuth.trace;

import static io.myoss.phoenix.core.constants.PhoenixConstants.LEGACY_SPAN_ID_NAME;
import static io.myoss.phoenix.core.constants.PhoenixConstants.LEGACY_TRACE_ID_NAME;
import static io.myoss.phoenix.core.constants.PhoenixConstants.SPAN_ID_NAME;
import static io.myoss.phoenix.core.constants.PhoenixConstants.TRACE_ID_NAME;

import org.slf4j.MDC;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.cloud.sleuth.autoconfig.SleuthProperties;
import org.springframework.context.ConfigurableApplicationContext;

import zipkin2.reporter.Reporter;
import brave.internal.HexCodec;
import brave.internal.Nullable;
import brave.internal.Platform;
import brave.propagation.CurrentTraceContext;
import brave.propagation.Propagation.Factory;
import brave.sampler.Sampler;

/**
 * 应用事件调用链
 *
 * @author Jerry.Chen 2018年4月12日 下午1:50:20
 * @see org.springframework.cloud.sleuth.autoconfig.TraceAutoConfiguration#tracing(String,
 *      Factory, CurrentTraceContext, Reporter, Sampler, SleuthProperties)
 * @see org.springframework.cloud.sleuth.log.SleuthLogAutoConfiguration
 * @see org.springframework.cloud.sleuth.log.Slf4jCurrentTraceContext
 */
public class ApplicationEventTracer {
    /**
     * Generates a new 64-bit ID, taking care to dodge zero which can be
     * confused with absent
     */
    public long nextId() {
        long nextId = Platform.get().randomLong();
        while (nextId == 0L) {
            nextId = Platform.get().randomLong();
        }
        return nextId;
    }

    /**
     * 设置 traceId 信息到 {@link MDC} 上下文中
     *
     * @param traceId traceId
     */
    public void setTraceId(String traceId) {
        replace(TRACE_ID_NAME, traceId);
        replace(SPAN_ID_NAME, traceId);
        replace(LEGACY_TRACE_ID_NAME, traceId);
        replace(LEGACY_SPAN_ID_NAME, traceId);
    }

    /**
     * 更新 {@link MDC} 上下文中的信息
     *
     * @param key property key
     * @param value property value，如果为 null 则删除这个 key
     */
    public void replace(String key, @Nullable String value) {
        if (value != null) {
            MDC.put(key, value);
        } else {
            MDC.remove(key);
        }
    }

    /**
     * 开始记录应用启动事件
     */
    public String startApplication() {
        String traceId = HexCodec.toLowerHex(nextId());
        setTraceId(traceId);
        return traceId;
    }

    /**
     * 结束记录应用启动事件
     *
     * @param applicationContext applicationContext上下文，用于将当前对象注册到上下文中
     */
    public void startedApplication(ConfigurableApplicationContext applicationContext) {
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        beanFactory.registerSingleton("applicationEventTracer", this);
        setTraceId(null);
    }

    /**
     * 获取当前线程中保存的spanId
     */
    public static String getSpanId() {
        String spanId = MDC.get(LEGACY_SPAN_ID_NAME);
        return spanId != null ? spanId : MDC.get(SPAN_ID_NAME);
    }

    /**
     * 获取当前线程中保存的traceId
     */
    public static String getTraceId() {
        String traceId = MDC.get(LEGACY_TRACE_ID_NAME);
        return traceId != null ? traceId : MDC.get(TRACE_ID_NAME);
    }
}
