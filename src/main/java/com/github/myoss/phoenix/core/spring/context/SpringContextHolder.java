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

package com.github.myoss.phoenix.core.spring.context;

import java.util.Objects;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 用静态方法的方式获取 Spring 容器中管理的Bean
 * <p>
 * 可以使用 {@link org.springframework.context.annotation.Import} 注入Bean例子：
 *
 * <pre>
 * &#064;Import(SpringContextHolder.class)
 * &#064;Configuration
 * public class Config {
 * }
 * </pre>
 *
 * @author Jerry.Chen
 * @since 2018年5月21日 下午2:14:24
 */
@Slf4j
@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_SINGLETON)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpringContextHolder {
    private static ApplicationContext applicationContext = null;

    /**
     * 使用 {@link ApplicationReadyEvent} 事件获取 ApplicationContext 注入到静态变量中
     *
     * @param event ApplicationReadyEvent
     */
    @EventListener
    public static void setApplicationContext(ApplicationReadyEvent event) {
        applicationContext = event.getApplicationContext();
    }

    /**
     * 取得存储在静态变量中的ApplicationContext.
     *
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        assertContextInjected();
        return applicationContext;
    }

    /**
     * 从Spring applicationContext中取得Bean, 自动转型为所赋值对象的类型.
     *
     * @param name bean name
     * @param <T> bean class type
     * @return bean instance
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        assertContextInjected();
        return (T) applicationContext.getBean(name);
    }

    /**
     * 从Spring applicationContext中取得Bean, 自动转型为所赋值对象的类型.
     *
     * @param name bean name
     * @param requiredType type the bean must match
     * @param <T> bean class type
     * @return bean instance
     */
    public static <T> T getBean(String name, Class<T> requiredType) {
        assertContextInjected();
        return (T) applicationContext.getBean(name, requiredType);
    }

    /**
     * 从Spring applicationContext中取得Bean, 自动转型为所赋值对象的类型.
     *
     * @param requiredType type the bean must match
     * @param <T> bean class type
     * @return bean instance
     */
    public static <T> T getBean(Class<T> requiredType) {
        assertContextInjected();
        return applicationContext.getBean(requiredType);
    }

    /**
     * Does this bean factory contain a bean definition or externally registered
     * singleton instance with the given name?
     * <p>
     * If the given name is an alias, it will be translated back to the
     * corresponding canonical bean name.
     * <p>
     * If this factory is hierarchical, will ask any parent factory if the bean
     * cannot be found in this factory instance.
     * <p>
     * If a bean definition or singleton instance matching the given name is
     * found, this method will return {@code true} whether the named bean
     * definition is concrete or abstract, lazy or eager, in scope or not.
     * Therefore, note that a {@code true} return value from this method does
     * not necessarily indicate that {@link #getBean} will be able to obtain an
     * instance for the same name.
     *
     * @param name the name of the bean to query
     * @return whether a bean with the given name is present
     */
    public static boolean containsBean(String name) {
        assertContextInjected();
        return applicationContext.containsBean(name);
    }

    /**
     * Check if this bean factory contains a bean definition with the given
     * name.
     * <p>
     * Does not consider any hierarchy this factory may participate in, and
     * ignores any singleton beans that have been registered by other means than
     * bean definitions.
     *
     * @param beanName the name of the bean to look for
     * @return if this bean factory contains a bean definition with the given
     *         name
     * @see #containsBean
     */
    public static boolean containsBeanDefinition(String beanName) {
        assertContextInjected();
        return applicationContext.containsBeanDefinition(beanName);
    }

    /**
     * Return whether the local bean factory contains a bean of the given name,
     * ignoring beans defined in ancestor contexts.
     * <p>
     * This is an alternative to {@code containsBean}, ignoring a bean of the
     * given name from an ancestor bean factory.
     *
     * @param name the name of the bean to query
     * @return whether a bean with the given name is defined in the local
     *         factory
     * @see org.springframework.beans.factory.BeanFactory#containsBean
     */
    public static boolean containsLocalBean(String name) {
        return applicationContext.containsLocalBean(name);
    }

    /**
     * 检查applicationContext不为空.
     */
    private static void assertContextInjected() {
        Objects.requireNonNull(applicationContext, "请检查，applicationContext属性未注入.");
    }
}
