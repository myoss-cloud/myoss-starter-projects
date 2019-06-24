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

package app.myoss.cloud.web.reactive.spring.web.method.error.annatation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import app.myoss.cloud.web.reactive.spring.web.method.error.AopLogControllerRegistrar;
import app.myoss.cloud.web.reactive.spring.web.method.error.MonitorControllerProperties;

/**
 * 开启自动记录 {@link org.springframework.stereotype.Controller} 信息
 * <ul>
 * <li>{@link app.myoss.cloud.web.reactive.spring.web.method.error.ControllerDefaultErrorAttributes}
 * </ul>
 * 使用例子：
 *
 * <pre>
 * &#064;EnableAopLogController
 * &#064;Configuration
 * public class Config {
 * }
 * </pre>
 *
 * @author Jerry.Chen
 * @since 2019年6月21日 下午6:01:30
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@EnableConfigurationProperties(MonitorControllerProperties.class)
@Import(AopLogControllerRegistrar.class)
public @interface EnableAopLogController {
    /**
     * 开启自动记录 controller异常，
     * 使用：{@link app.myoss.cloud.web.reactive.spring.web.method.error.ControllerDefaultErrorAttributes}
     * 处理异常信息
     *
     * @return 默认开启（如果不是 Reactive WebApplication，是不起作用的）
     */
    boolean enableAopLogControllerException() default true;
}
