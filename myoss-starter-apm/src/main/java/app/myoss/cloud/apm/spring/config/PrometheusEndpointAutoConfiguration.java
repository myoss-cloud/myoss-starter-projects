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

package app.myoss.cloud.apm.spring.config;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties.Exposure;
import org.springframework.boot.actuate.metrics.export.prometheus.PrometheusScrapeEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import io.prometheus.client.hotspot.DefaultExports;

/**
 * Prometheus Endpoint 自动配置
 *
 * @author Jerry.Chen
 * @since 2019年1月18日 上午11:46:28
 */
@EnableConfigurationProperties(WebEndpointProperties.class)
@ConditionalOnClass({ TextFormat.class, CollectorRegistry.class, PrometheusScrapeEndpoint.class })
@ConditionalOnEnabledEndpoint(endpoint = PrometheusScrapeEndpoint.class)
@Configuration
public class PrometheusEndpointAutoConfiguration {
    private WebEndpointProperties webEndpointProperties;

    /**
     * 初始化
     *
     * @param webEndpointProperties web endpoint 配置属性 Bean 对象
     */
    public PrometheusEndpointAutoConfiguration(WebEndpointProperties webEndpointProperties) {
        this.webEndpointProperties = webEndpointProperties;
    }

    /**
     * 创建默认的收集器
     *
     * @return {@link CollectorRegistry#defaultRegistry}
     */
    @ConditionalOnMissingBean
    @Bean
    public CollectorRegistry collectorRegistry() {
        DefaultExports.initialize();
        return CollectorRegistry.defaultRegistry;
    }

    /**
     * 创建默认的 Prometheus Endpoint
     *
     * @param collectorRegistry 收集器Bean
     * @return PrometheusScrapeEndpoint
     */
    @ConditionalOnMissingBean
    @Bean
    public PrometheusScrapeEndpoint prometheusEndpoint(CollectorRegistry collectorRegistry) {
        Exposure exposure = this.webEndpointProperties.getExposure();
        exposure.getInclude().add("prometheus");
        return new PrometheusScrapeEndpoint(collectorRegistry);
    }
}
