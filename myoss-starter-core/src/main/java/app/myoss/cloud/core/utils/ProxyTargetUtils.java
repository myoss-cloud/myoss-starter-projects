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
 * 代理工具类，获取代理对象的目标对象
 *
 * @author Jerry.Chen
 * @since 2019年1月1日 下午5:08:02
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProxyTargetUtils {
    /**
     * 获取代理对象的目标对象
     *
     * @param proxy 代理对象
     * @return 目标对象
     */
    public static Object getTarget(Object proxy) {
        if (proxy == null) {
            return null;
        } else if (AopUtils.isAopProxy(proxy)) {
            if (AopUtils.isJdkDynamicProxy(proxy)) {
                // aop jdk dynamic proxy
                Object target = getAopJdkDynamicProxyTargetObject(proxy);
                return getTarget(target);
            } else {
                // aop cglib proxy
                Object target = getAopCglibProxyTargetObject(proxy);
                return getTarget(target);
            }
        } else if (Proxy.isProxyClass(proxy.getClass())) {
            // jdk proxy
            Object target = getJdkProxyTargetObject(proxy);
            return getTarget(target);
        } else {
            return proxy;
        }
    }

    /**
     * 获取aop cglib dynamic proxy的目标对象
     *
     * @param proxy 代理对象
     * @return 目标对象（有可能依旧是代理对象）
     */
    public static Object getAopCglibProxyTargetObject(Object proxy) {
        try {
            Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
            h.setAccessible(true);
            Object dynamicAdvisedInterceptor = h.get(proxy);

            Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
            advised.setAccessible(true);
            return ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
        } catch (Exception e) {
            throw new BizRuntimeException(e);
        }
    }

    /**
     * 获取aop jdk dynamic proxy的目标对象
     *
     * @param proxy 代理对象
     * @return 目标对象（有可能依旧是代理对象）
     */
    public static Object getAopJdkDynamicProxyTargetObject(Object proxy) {
        try {
            Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
            h.setAccessible(true);
            AopProxy aopProxy = (AopProxy) h.get(proxy);

            Field advised = aopProxy.getClass().getDeclaredField("advised");
            advised.setAccessible(true);
            return ((AdvisedSupport) advised.get(aopProxy)).getTargetSource().getTarget();
        } catch (Exception e) {
            throw new BizRuntimeException(e);
        }
    }

    /**
     * 获取jdk proxy的目标对象
     *
     * @param proxy 代理对象
     * @return 目标对象（有可能依旧是代理对象）
     */
    public static Object getJdkProxyTargetObject(Object proxy) {
        try {
            Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
            h.setAccessible(true);
            return h.get(proxy);
        } catch (Exception e) {
            throw new BizRuntimeException(e);
        }
    }
}
