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

package app.myoss.cloud.core.lang.bean;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import app.myoss.cloud.core.exception.BizRuntimeException;

/**
 * JavaBeans 常用方法封装
 *
 * @author Jerry.Chen
 * @since 2018年5月10日 上午11:08:14
 */
public class BeanUtil {
    /**
     * 使用反射机制执行方法，获取返回值
     *
     * @param method 方法
     * @param target 目标对象
     * @param args 参数
     * @param <T> 结果类型
     * @return 方法的返回值
     */
    @SuppressWarnings("unchecked")
    public static <T> T methodInvoke(Method method, Object target, Object... args) {
        try {
            return (T) method.invoke(target, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BizRuntimeException(e);
        }
    }

    /**
     * Copy the property values of the given source bean into the given target
     * bean.
     * <p>
     * Note: The source and target classes do not have to match or even be
     * derived from each other, as long as the properties match. Any bean
     * properties that the source bean exposes but the target bean does not will
     * silently be ignored.
     *
     * @param source the source bean
     * @param target the target bean
     * @param editable the class (or interface) to restrict property setting to
     * @param overrideNotBlankProperty source bean 的字段如果有值，是否用 target bean
     *            字段的值覆盖掉
     * @param ignoreProperties array of property names to ignore
     * @throws BeansException if the copying failed
     * @see BeanWrapper
     */
    @SuppressWarnings("checkstyle:NestedIfDepth")
    public static void copyProperties(Object source, Object target, @Nullable Class<?> editable,
                                      boolean overrideNotBlankProperty, @Nullable String... ignoreProperties)
            throws BeansException {

        Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");

        Class<?> actualEditable = target.getClass();
        if (editable != null) {
            if (!editable.isInstance(target)) {
                throw new IllegalArgumentException("Target class [" + target.getClass().getName()
                        + "] not assignable to Editable class [" + editable.getName() + "]");
            }
            actualEditable = editable;
        }
        PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(actualEditable);
        List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties) : null);

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (!overrideNotBlankProperty) {
                Method readMethod = targetPd.getReadMethod();
                try {
                    Object value = readMethod.invoke(target);
                    if (value != null) {
                        if (value instanceof CharSequence) {
                            // 字符串类型，判断是否为空
                            if (StringUtils.isNotBlank((CharSequence) value)) {
                                continue;
                            }
                        } else if (value instanceof Collection) {
                            if (!((Collection) value).isEmpty()) {
                                continue;
                            }
                        } else if (value instanceof Map) {
                            if (!((Map) value).isEmpty()) {
                                continue;
                            }
                        } else {
                            // 其它类型，只要不为 null
                            continue;
                        }
                    }
                } catch (Throwable ex) {
                    throw new FatalBeanException(
                            "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                }
            }
            if (writeMethod != null && (ignoreList == null || !ignoreList.contains(targetPd.getName()))) {
                PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null && ClassUtils.isAssignable(writeMethod.getParameterTypes()[0],
                            readMethod.getReturnType())) {
                        try {
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }
                            Object value = readMethod.invoke(source);
                            if (!overrideNotBlankProperty && value == null) {
                                Class<?> cls = writeMethod.getParameterTypes()[0];
                                if (Collection.class.isAssignableFrom(cls) || Map.class.isAssignableFrom(cls)) {
                                    continue;
                                }
                            }
                            if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                writeMethod.setAccessible(true);
                            }
                            writeMethod.invoke(target, value);
                        } catch (Throwable ex) {
                            throw new FatalBeanException(
                                    "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                        }
                    }
                }
            }
        }
    }
}
