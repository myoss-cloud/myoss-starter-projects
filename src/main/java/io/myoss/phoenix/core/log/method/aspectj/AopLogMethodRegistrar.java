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

package io.myoss.phoenix.core.log.method.aspectj;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;

import io.myoss.phoenix.core.log.method.aspectj.annotation.EnableAopLogMethod;
import io.myoss.phoenix.core.log.method.aspectj.annotation.MonitorMethodAdvice;

/**
 * 扫描当前package下的 {@link org.springframework.stereotype.Component}，并进行 Bean 的自动注册
 *
 * @author Jerry.Chen 2018年4月11日 下午12:07:23
 */
public class AopLogMethodRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(importingClassMetadata
                .getAnnotationAttributes(EnableAopLogMethod.class.getName()));
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry);
        scanner.resetFilters(false);
        scanner.setResourceLoader(this.resourceLoader);

        // 监控 method 的入参和出参
        boolean enableAopLogMethod = attributes.getBoolean("enableAopLogMethod");
        if (!enableAopLogMethod) {
            scanner.addExcludeFilter(new AnnotationTypeFilter(MonitorMethodAdvice.class));
        } else {
            scanner.addIncludeFilter(new AnnotationTypeFilter(MonitorMethodAdvice.class));
        }

        // 监控 controller 异常
        boolean enableAopLogControllerException = attributes.getBoolean("enableAopLogControllerException");
        if (!enableAopLogControllerException) {
            scanner.addExcludeFilter(new AnnotationTypeFilter(ControllerAdvice.class));
        } else {
            scanner.addIncludeFilter(new AnnotationTypeFilter(ControllerAdvice.class));
        }

        // 扫描注册
        String packageName = ClassUtils.getPackageName(AopLogMethodRegistrar.class);
        scanner.scan(packageName);
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
