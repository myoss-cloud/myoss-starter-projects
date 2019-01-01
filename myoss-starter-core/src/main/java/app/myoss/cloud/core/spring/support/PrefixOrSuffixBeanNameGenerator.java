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

package app.myoss.cloud.core.spring.support;

import java.beans.Introspector;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * 为 Spring Bean Name追加前缀或者后缀字符
 *
 * @author Jerry.Chen
 * @since 2019年1月1日 下午5:08:02
 */
public class PrefixOrSuffixBeanNameGenerator extends AnnotationBeanNameGenerator implements BeanNameGenerator {
    private String beanNamePrefix;
    private String beanNameSuffix;

    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        if (definition instanceof AnnotatedBeanDefinition) {
            String beanName = determineBeanNameFromAnnotation((AnnotatedBeanDefinition) definition);
            if (StringUtils.hasText(beanName)) {
                // Explicit bean name found.
                return beanName;
            }
        }
        // Fallback: generate a unique default bean name.
        String shortClassName = ClassUtils.getShortName(definition.getBeanClassName());
        if (StringUtils.hasText(beanNamePrefix)) {
            shortClassName = beanNamePrefix + shortClassName;
        }
        if (StringUtils.hasText(beanNameSuffix)) {
            shortClassName = shortClassName + beanNameSuffix;
        }
        return Introspector.decapitalize(shortClassName);
    }

    /**
     * 设置 Spring Bean Name的前缀
     *
     * @param beanNamePrefix Spring Bean Name的前缀
     */
    public void setBeanNamePrefix(String beanNamePrefix) {
        this.beanNamePrefix = beanNamePrefix;
    }

    /**
     * 设置 Spring Bean Name的后缀
     *
     * @param beanNameSuffix Spring Bean Name的后缀
     */
    public void setBeanNameSuffix(String beanNameSuffix) {
        this.beanNameSuffix = beanNameSuffix;
    }
}
