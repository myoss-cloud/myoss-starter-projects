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

package app.myoss.cloud.web.spring.boot;

import static app.myoss.cloud.core.constants.MyossConstants.DEPLOY_ENV;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.ClassUtils;

import app.myoss.cloud.apm.spring.cloud.sleuth.trace.ApplicationEventTracer;
import app.myoss.cloud.core.constants.DeployEnvEnum;
import app.myoss.cloud.core.constants.MyossConstants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Spring Boot Application 启动的时候一些常用方法封装
 *
 * @author Jerry.Chen
 * @since 2018年4月12日 下午1:46:07
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BootApplication {
    /**
     * 为 Application 设置 {@link MyossConstants#DEPLOY_ENV DEPLOY_ENV}
     * 环境变量，如果环境变量中没有设置，则设置部署的默认环境为：dev
     * <p>
     * 会先去检查方法的参数：deployEnvValue，如果是：<code>null</code>或者<code>""</code>
     * ，则再去系统环境变量中查找是否有，如果也没有则设置为：dev
     *
     * @param logger 日志
     * @param deployEnvValue 待检查的“部署的默认环境的值”，如果是：<code>null</code>或者
     *            <code>""</code>，则再去系统环境变量中查找是否有，如果也没有则设置为：dev
     * @return 部署的默认环境的值
     */
    public static String setStartDeployEnv(Logger logger, String deployEnvValue) {
        String deployEnv = (StringUtils.isNotBlank(deployEnvValue) ? deployEnvValue
                : StringUtils.defaultIfBlank(System.getProperty(DEPLOY_ENV), System.getenv(DEPLOY_ENV)));
        logger.info("Starting application, DEPLOY_ENV: {}", deployEnv);
        if (StringUtils.isBlank(deployEnv)) {
            // 设置部署的默认环境为：dev（即本机开发）
            deployEnv = "dev";
            DeployEnvEnum.setDeployEnv(deployEnv);
            logger.info("DEPLOY_ENV set to {}", deployEnv);
        }
        return deployEnv;
    }

    /**
     * 为 Application 设置 {@link MyossConstants#DEPLOY_ENV DEPLOY_ENV}
     * 环境变量，如果环境变量中没有设置，则设置部署的默认环境为：dev
     *
     * @param logger 日志
     * @return 部署的默认环境的值
     */
    public static String setStartDeployEnv(Logger logger) {
        return setStartDeployEnv(logger, null);
    }

    /**
     * Static helper that can be used to run a {@link SpringApplication} from
     * the specified source using default settings.
     * <p>
     * 启动 Spring boot Application，额外会做下面几件事情：
     * <ol>
     * <li>创建应用程序事件追踪 {@link ApplicationEventTracer} ，并注册到
     * {@link ApplicationContext}
     * <li>检查部署的环境变量 {@link BootApplication#setStartDeployEnv(Logger)}
     * </ol>
     *
     * @param logger 日志
     * @param setDeployEnv true/false，是否检查部署的环境变量
     *            {@link BootApplication#setStartDeployEnv(Logger)}
     * @param deployEnvValue 待检查的“部署的默认环境的值”，如果是：<code>null</code>或者
     *            <code>""</code>，则再去系统环境变量中查找是否有，如果也没有则设置为：dev
     * @param source the source to load
     * @param args the application arguments (usually passed from a Java main
     *            method)
     * @return the running {@link ApplicationContext}
     * @see SpringApplication#run(Class, String...)
     */
    public static ConfigurableApplicationContext run(Logger logger, boolean setDeployEnv, String deployEnvValue,
                                                     Class<?> source, String... args) {
        boolean isDefaultTracerPresent = ClassUtils.isPresent("brave.Tracing", BootApplication.class.getClassLoader());
        ApplicationEventTracer applicationEventTracer = null;
        if (isDefaultTracerPresent) {
            applicationEventTracer = new ApplicationEventTracer();
            applicationEventTracer.startApplication();
        }
        String deployEnv = null;
        if (setDeployEnv) {
            deployEnv = BootApplication.setStartDeployEnv(logger, deployEnvValue);
        }
        ConfigurableApplicationContext applicationContext = SpringApplication.run(source, args);
        if (setDeployEnv) {
            logger.info("Started application with args: {}, deployEnv: {}", Arrays.toString(args), deployEnv);
        } else {
            logger.info("Started application with args: {}", Arrays.toString(args));
        }
        if (isDefaultTracerPresent) {
            applicationEventTracer.startedApplication(applicationContext);
        }
        return applicationContext;
    }

    /**
     * Static helper that can be used to run a {@link SpringApplication} from
     * the specified source using default settings.
     * <p>
     * 启动 Spring boot Application，额外会做下面几件事情：
     * <ol>
     * <li>创建应用程序事件追踪 {@link ApplicationEventTracer} ，并注册到
     * {@link ApplicationContext}
     * <li>检查部署的环境变量 {@link BootApplication#setStartDeployEnv(Logger)}
     * </ol>
     *
     * @param logger 日志
     * @param source the source to load
     * @param args the application arguments (usually passed from a Java main
     *            method)
     * @return the running {@link ApplicationContext}
     * @see SpringApplication#run(Class, String...)
     */
    public static ConfigurableApplicationContext run(Logger logger, Class<?> source, String... args) {
        return run(logger, true, null, source, args);
    }

    /**
     * Static helper that can be used to run a {@link SpringApplication} from
     * the specified source using default settings.
     * <p>
     * 启动 Spring boot Application，额外会做下面几件事情：
     * <ol>
     * <li>创建应用程序事件追踪 {@link ApplicationEventTracer} ，并注册到
     * {@link ApplicationContext}
     * <li>检查部署的环境变量 {@link BootApplication#setStartDeployEnv(Logger)}
     * </ol>
     *
     * @param logger 日志
     * @param deployEnvValue 待检查的“部署的默认环境的值”，如果是：<code>null</code>或者
     *            <code>""</code>，则再去系统环境变量中查找是否有，如果也没有则设置为：dev
     * @param source the source to load
     * @param args the application arguments (usually passed from a Java main
     *            method)
     * @return the running {@link ApplicationContext}
     * @see SpringApplication#run(Class, String...)
     */
    public static ConfigurableApplicationContext run(Logger logger, String deployEnvValue, Class<?> source,
                                                     String... args) {
        return run(logger, true, deployEnvValue, source, args);
    }

    /**
     * Static helper that can be used to run a {@link SpringApplication} from
     * the specified source using default settings.
     * <p>
     * 启动 Spring boot Application，额外会做下面几件事情：
     * <ol>
     * <li>创建应用程序事件追踪 {@link ApplicationEventTracer} ，并注册到
     * {@link ApplicationContext}
     * <li>检查部署的环境变量 {@link BootApplication#setStartDeployEnv(Logger)}
     * </ol>
     *
     * @param logger 日志
     * @param setDeployEnv true/false，是否检查部署的环境变量
     *            {@link BootApplication#setStartDeployEnv(Logger)}
     * @param source the source to load
     * @param args the application arguments (usually passed from a Java main
     *            method)
     * @return the running {@link ApplicationContext}
     * @see SpringApplication#run(Class, String...)
     */
    public static ConfigurableApplicationContext run(Logger logger, boolean setDeployEnv, Class<?> source,
                                                     String... args) {
        return run(logger, setDeployEnv, null, source, args);
    }
}
