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

package app.myoss.cloud.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;

import app.myoss.cloud.core.exception.BizRuntimeException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 代理工具类，获取代理对象代理的class/interface
 *
 * @author Jerry.Chen
 * @since 2019年1月1日 下午5:08:02
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProxyClassUtils {
    /**
     * 检查对象是否为代理对象
     *
     * @param proxy 对象
     * @return true：是，false：否
     */
    public static boolean checkIsProxy(Object proxy) {
        if (proxy == null) {
            return false;
        }
        // aop proxy or jdk proxy
        return AopUtils.isAopProxy(proxy) || Proxy.isProxyClass(proxy.getClass());
    }

    /**
     * 获取代理对象代理的class/interface
     *
     * @param proxy 代理对象
     * @return 代理的class/interface
     */
    public static Class<?> getClass(Object proxy) {
        Class<?>[] classes = getClasses(proxy);
        return (classes != null ? classes[0] : null);
    }

    /**
     * 获取代理对象代理的class/interface
     *
     * @param proxy 代理对象
     * @return 代理的class/interface
     */
    public static Class<?>[] getClasses(Object proxy) {
        try {
            if (proxy == null) {
                return null;
            } else if (AopUtils.isAopProxy(proxy)) {
                AdvisedSupport advisedSupport;
                if (AopUtils.isJdkDynamicProxy(proxy)) {
                    // aop jdk dynamic proxy
                    advisedSupport = getAopJdkDynamicProxyAdvised(proxy);
                } else {
                    // aop cglib proxy
                    advisedSupport = getAopCglibProxyAdvised(proxy);
                }

                // 判断拿到的目标对象，是否依旧是代理对象
                Object target = advisedSupport.getTargetSource().getTarget();
                if (!checkIsProxy(target)) {
                    return getAopProxyInterfaces(advisedSupport);
                } else {
                    return getClasses(target);
                }
            } else if (Proxy.isProxyClass(proxy.getClass())) {
                // jdk proxy
                Object target = ProxyTargetUtils.getJdkProxyTargetObject(proxy);
                // 判断拿到的目标对象，是否依旧是代理对象
                if (!checkIsProxy(target)) {
                    return getJdkProxyInterfaces(proxy);
                } else {
                    return getClasses(target);
                }
            } else {
                return proxy.getClass().getInterfaces();
            }
        } catch (Exception e) {
            throw new BizRuntimeException(e);
        }
    }

    /**
     * 获取aop cglib dynamic proxy代理的切面对象
     *
     * @param proxy 代理对象
     * @return 代理的切面对象（有可能依旧是代理对象）
     */
    public static AdvisedSupport getAopCglibProxyAdvised(Object proxy) {
        try {
            Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
            h.setAccessible(true);
            Object dynamicAdvisedInterceptor = h.get(proxy);

            Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
            advised.setAccessible(true);
            return (AdvisedSupport) advised.get(dynamicAdvisedInterceptor);
        } catch (Exception e) {
            throw new BizRuntimeException(e);
        }
    }

    /**
     * 获取aop jdk dynamic proxy代理的切面对象
     *
     * @param proxy 代理对象
     * @return 代理的切面对象（有可能依旧是代理对象）
     */
    public static AdvisedSupport getAopJdkDynamicProxyAdvised(Object proxy) {
        try {
            Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
            h.setAccessible(true);
            AopProxy aopProxy = (AopProxy) h.get(proxy);

            Field advised = aopProxy.getClass().getDeclaredField("advised");
            advised.setAccessible(true);
            return (AdvisedSupport) advised.get(aopProxy);
        } catch (Exception e) {
            throw new BizRuntimeException(e);
        }
    }

    /**
     * 获取aop proxy代理的class/interface
     *
     * @param advisedSupport 代理的切面对象
     * @return 代理的class/interface
     */
    public static Class<?>[] getAopProxyInterfaces(AdvisedSupport advisedSupport) {
        try {
            if (advisedSupport.getTargetSource().getTarget().getClass().isInterface()) {
                return advisedSupport.getProxiedInterfaces();
            } else {
                return new Class[] { advisedSupport.getTargetClass() };
            }
        } catch (Exception e) {
            throw new BizRuntimeException(e);
        }
    }

    /**
     * 获取jdk proxy代理的interface
     *
     * @param proxy 代理对象
     * @return 代理的class/interface
     */
    public static Class<?>[] getJdkProxyInterfaces(Object proxy) {
        return proxy.getClass().getInterfaces();
    }
}
