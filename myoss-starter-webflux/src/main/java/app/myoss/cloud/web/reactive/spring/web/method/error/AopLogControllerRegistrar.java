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

package app.myoss.cloud.web.reactive.spring.web.method.error;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import app.myoss.cloud.web.reactive.spring.web.method.error.annatation.EnableAopLogController;

/**
 * 扫描当前package下的 {@link org.springframework.stereotype.Component}，并进行 Bean 的自动注册
 *
 * @author Jerry.Chen
 * @since 2018年12月28日 下午3:25:48
 */
public class AopLogControllerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {
    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes = AnnotationAttributes
                .fromMap(importingClassMetadata.getAnnotationAttributes(EnableAopLogController.class.getName()));
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(registry);
        scanner.resetFilters(false);
        scanner.setResourceLoader(this.resourceLoader);
        List<String> scanPackages = new ArrayList<>();

        // 监控 controller 异常
        boolean enableAopLogControllerException = attributes.getBoolean("enableAopLogControllerException");
        if (!enableAopLogControllerException) {
            scanner.addExcludeFilter(new AnnotationTypeFilter(Component.class));
        } else {
            scanner.addIncludeFilter(new AnnotationTypeFilter(Component.class));
        }

        // 扫描注册
        String packageName = ClassUtils.getPackageName(AopLogControllerRegistrar.class);
        scanPackages.add(packageName);
        scanner.scan(scanPackages.toArray(new String[0]));
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
