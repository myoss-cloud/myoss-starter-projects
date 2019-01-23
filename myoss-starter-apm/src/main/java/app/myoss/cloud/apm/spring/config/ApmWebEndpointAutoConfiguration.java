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

import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties.Exposure;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * APM WebEndpoint 自动配置
 *
 * @author Jerry.Chen
 * @since 2019年1月20日 下午1:11:48
 */
@EnableConfigurationProperties(WebEndpointProperties.class)
@ConditionalOnClass(WebEndpointProperties.class)
@Configuration
public class ApmWebEndpointAutoConfiguration {
    /**
     * 初始化
     *
     * @param webEndpointProperties web endpoint 配置属性 Bean 对象
     */
    public ApmWebEndpointAutoConfiguration(WebEndpointProperties webEndpointProperties) {
        Exposure exposure = webEndpointProperties.getExposure();
        // {@link org.springframework.boot.actuate.logging.LoggersEndpoint}
        exposure.getInclude().add("loggers");
    }
}
