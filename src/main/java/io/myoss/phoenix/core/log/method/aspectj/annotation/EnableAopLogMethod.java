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

package io.myoss.phoenix.core.log.method.aspectj.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import io.myoss.phoenix.core.log.method.aspectj.AopLogControllerExceptionHandler;
import io.myoss.phoenix.core.log.method.aspectj.AopLogMethodRegistrar;
import io.myoss.phoenix.core.log.method.aspectj.MonitorMethodProperties;

/**
 * 开启下面几个注解的功能，使用slf4j记录方法的入参和出参；同时支持开启自动记录 controller 异常
 * <ul>
 * <li>{@link LogMethodBefore}
 * <li>{@link LogMethodAfter}
 * <li>{@link LogMethodAround}
 * <li>{@link LogUnMonitor}
 * <li>{@link AopLogControllerExceptionHandler}
 * </ul>
 * 使用例子：
 *
 * <pre>
 * &#064;EnableAopLogMethod
 * &#064;Configuration
 * public class Config {
 * }
 * </pre>
 *
 * @author Jerry.Chen 2018年3月31日 下午10:53:18
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@EnableConfigurationProperties(MonitorMethodProperties.class)
@Import(AopLogMethodRegistrar.class)
public @interface EnableAopLogMethod {
    /**
     * 开启自动记录 method 的入参和出参
     * <ul>
     * <li>{@link LogMethodBefore}
     * <li>{@link LogMethodAfter}
     * <li>{@link LogMethodAround}
     * <li>{@link LogUnMonitor}
     * </ul>
     */
    boolean enableAopLogMethod() default true;

    /**
     * 开启自动记录 controller 异常
     *
     * @return 默认开启（如果不是WebApplication，是不起作用的）
     * @see AopLogControllerExceptionHandler
     */
    boolean enableAopLogControllerException() default true;
}
