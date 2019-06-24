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

package app.myoss.cloud.web.spring.boot.config;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.client.RestTemplate;

import app.myoss.cloud.web.spring.boot.config.http.RestTemplate4OkHttp3ClientAutoConfiguration;
import app.myoss.cloud.web.utils.RestClient;

/**
 * Web项目自动配置
 *
 * @author Jerry.Chen
 * @since 2018年12月26日 下午2:11:02
 */
@AutoConfigureAfter(RestTemplate4OkHttp3ClientAutoConfiguration.class)
@Configuration
public class WebAutoConfiguration {
    /**
     * 初始化
     */
    public WebAutoConfiguration() {
    }

    /**
     * 创建Rest API 工具类，用于发送 HTTP 请求
     *
     * @return HTTP 请求的 Rest API 工具类
     */
    @ConditionalOnBean(RestTemplate.class)
    @ConditionalOnMissingBean
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_SINGLETON)
    @Bean
    public RestClient restClient() {
        return new RestClient();
    }
}
