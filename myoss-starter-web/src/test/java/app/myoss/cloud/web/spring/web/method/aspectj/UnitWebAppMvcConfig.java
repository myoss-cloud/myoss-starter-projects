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

package app.myoss.cloud.web.spring.web.method.aspectj;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import app.myoss.cloud.core.spring.boot.config.FastJsonAutoConfiguration;
import app.myoss.cloud.web.spring.boot.config.AbstractWebMvcConfigurer;

/**
 * 单元测试Web的bean初始化配置
 *
 * @author Jerry.Chen
 * @since 2019年1月30日 下午3:28:56
 */
// 使用 {@code @EnableWebMvc} 开启 mvc 配置的功能，此项目不用spring boot web进行测试
@ImportAutoConfiguration(FastJsonAutoConfiguration.class)
@EnableWebMvc
public class UnitWebAppMvcConfig extends AbstractWebMvcConfigurer {
}
