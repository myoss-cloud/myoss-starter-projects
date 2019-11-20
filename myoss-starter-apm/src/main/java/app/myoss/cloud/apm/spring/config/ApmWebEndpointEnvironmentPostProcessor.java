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

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.util.ClassUtils;

import com.alibaba.fastjson.JSON;

import app.myoss.cloud.core.constants.DeployEnvEnum;
import app.myoss.cloud.core.spring.boot.config.CoreCommonEnvironmentPostProcessor;

/**
 * APM WebEndpoint 环境变量自动配置
 *
 * @author Jerry.Chen
 * @since 2019年1月20日 下午1:11:48
 */
@EnableConfigurationProperties(WebEndpointProperties.class)
@ConditionalOnClass(WebEndpointProperties.class)
@Configuration
public class ApmWebEndpointEnvironmentPostProcessor
        implements EnvironmentPostProcessor, Ordered, SmartApplicationListener {
    /**
     * {@link EnvironmentPostProcessor} 中比较特殊，不能直接用 @Slf4j 进行输出日志
     */
    private static final DeferredLog LOGGER        = new DeferredLog();
    /**
     * The default order for the processor.
     */
    public static final int          DEFAULT_ORDER = ConfigFileApplicationListener.DEFAULT_ORDER + 6;

    private int                      order         = DEFAULT_ORDER;

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        MutablePropertySources propertySources = environment.getPropertySources();
        Map<String, Object> map = new HashMap<>();
        Set<String> include = new LinkedHashSet<>();
        include.add("health");
        if (!DeployEnvEnum.isCustomizeDev()) {
            map.put("management.server.port", "8088");
        }
        map.put("management.endpoints.enabled-by-default", "true");
        map.put("management.endpoints.web.base-path", "/");

        // 默认开启 loggers endpoint, 可用于在线配置日志
        map.put("management.endpoint.loggers.enabled", "true");
        include.add("loggers");

        ClassLoader classLoader = this.getClass().getClassLoader();
        if (ClassUtils.isPresent("io.prometheus.client.exporter.common.TextFormat", classLoader)
                && ClassUtils.isPresent("io.prometheus.client.CollectorRegistry", classLoader)
                && ClassUtils.isPresent(
                        "org.springframework.boot.actuate.metrics.export.prometheus.PrometheusScrapeEndpoint",
                        classLoader)) {
            // 启用 prometheus endpoint
            map.put("management.endpoint.prometheus.enabled", "true");
            map.put("management.metrics.export.prometheus.enabled", "true");
            include.add("prometheus");
        }

        // 暴露哪些 endpoints: org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration#webExposeExcludePropertyEndpointFilter
        map.put("management.endpoints.web.exposure.include", include);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("config APM WebEndpoint: " + JSON.toJSONString(map));
        }
        CoreCommonEnvironmentPostProcessor.addOrReplace(propertySources, map);
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return ApplicationPreparedEvent.class.isAssignableFrom(eventType);
    }

    @Override
    public boolean supportsSourceType(Class<?> sourceType) {
        return true;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationPreparedEvent) {
            LOGGER.replayTo(ApmWebEndpointEnvironmentPostProcessor.class);
        }
    }
}
