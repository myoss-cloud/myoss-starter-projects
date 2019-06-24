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

package app.myoss.cloud.web.reactive.spring.boot.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import app.myoss.cloud.web.constants.WebConstants;
import app.myoss.cloud.web.reactive.spring.web.server.filter.LogWebRequestFilter;

/**
 * Spring Web Flux配置基类
 *
 * @author Jerry.Chen
 * @since 2019年6月20日 下午5:18:07
 */
public abstract class AbstractWebFluxConfigurer implements WebFluxConfigurer {
    /**
     * 注册 {@link LogWebRequestFilter} Filter
     *
     * @return 记录web请求的日志信息过滤器
     */
    @Order(100)
    @ConditionalOnMissingBean(name = WebConstants.WEB_REQUEST_LOG_FILTER_BEAN_NAME)
    @Bean(name = WebConstants.WEB_REQUEST_LOG_FILTER_BEAN_NAME)
    public LogWebRequestFilter webRequestLogFilter() {
        return new LogWebRequestFilter(true, true);
    }
}
