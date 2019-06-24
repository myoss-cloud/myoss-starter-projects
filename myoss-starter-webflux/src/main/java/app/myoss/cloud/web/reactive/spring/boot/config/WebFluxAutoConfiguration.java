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

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import app.myoss.cloud.web.reactive.spring.web.method.handler.PrintRequestMappingHandler;
import app.myoss.cloud.web.spring.boot.config.WebAutoConfiguration;

/**
 * Web flux项目自动配置
 *
 * @author Jerry.Chen
 * @since 2018年12月26日 下午2:11:02
 */
@AutoConfigureAfter(WebAutoConfiguration.class)
@Configuration
public class WebFluxAutoConfiguration extends AbstractWebFluxConfigurer {
    /**
     * 初始化
     */
    public WebFluxAutoConfiguration() {
    }

    /**
     * 打印 Web 所有的接口映射关系
     *
     * @return PrintRequestMappingHandler
     */
    @Bean
    public PrintRequestMappingHandler printRequestMappingHandler() {
        return new PrintRequestMappingHandler();
    }
}
