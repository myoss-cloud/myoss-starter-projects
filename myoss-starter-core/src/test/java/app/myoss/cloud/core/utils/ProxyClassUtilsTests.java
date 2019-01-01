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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.reflect.Reflection;

import app.myoss.cloud.core.utils.ProxyClassUtilsTests.Config.MockProxyInterface4Impl;
import app.myoss.cloud.core.utils.ProxyClassUtilsTests.Config.MockProxyInterface5Impl;

/**
 * {@link ProxyClassUtils} 测试类
 *
 * @author Jerry.Chen
 * @since 2019年1月1日 下午5:08:02
 */
@RunWith(SpringRunner.class)
public class ProxyClassUtilsTests {
    @Autowired
    private MockProxyInterface3 mockProxyInterface3;

    @Autowired
    private MockProxyInterface4 mockProxyInterface4;
    @Autowired
    private MockProxyInterface5 mockProxyInterface5;

    @Test
    public void testConstructorIsPrivate()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<ProxyClassUtils> constructor = ProxyClassUtils.class.getDeclaredConstructor();
        Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    /**
     * null对象测试
     */
    @Test
    public void nullTest1() {
        Assert.assertFalse(ProxyClassUtils.checkIsProxy(null));
        Class<?> clazz = ProxyClassUtils.getClass(null);
        Assert.assertNull(clazz);
    }

    /**
     * null对象测试
     */
    @Test
    public void nullTest2() {
        Assert.assertFalse(ProxyClassUtils.checkIsProxy(null));
        Class<?>[] clazz = ProxyClassUtils.getClasses(null);
        Assert.assertNull(clazz);
    }

    @Test
    public void noProxyTest1() {
        Assert.assertFalse(ProxyClassUtils.checkIsProxy(mockProxyInterface3));
        Class<?> clazz = ProxyClassUtils.getClass(mockProxyInterface3);
        Assert.assertEquals(MockProxyInterface3.class, clazz);
    }

    /**
     * JDK代理测试1，代理1个接口
     */
    @Test
    public void jdkProxyTest1() {
        Object proxyInstance = Reflection.newProxy(MockProxyInterface1.class, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return null;
            }
        });
        Class<?> clazz = ProxyClassUtils.getClass(proxyInstance);
        Assert.assertEquals(MockProxyInterface1.class, clazz);
    }

    /**
     * JDK代理测试2，代理2个接口
     */
    @Test
    public void jdkProxyTest2() {
        Class[] interfaces = { MockProxyInterface1.class, MockProxyInterface2.class };
        Object proxyInstance = Proxy.newProxyInstance(MockProxyInterface1.class.getClassLoader(), interfaces,
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return null;
                    }
                });
        Class<?>[] clazz = ProxyClassUtils.getClasses(proxyInstance);
        Assert.assertArrayEquals(interfaces, clazz);
    }

    /**
     * aop jdk dynamic proxy
     */
    @Test
    public void aopJdkDynamicProxyTest1() {
        Assert.assertTrue(AopUtils.isJdkDynamicProxy(mockProxyInterface4));
        Class<?> clazz = ProxyClassUtils.getClass(mockProxyInterface4);
        Assert.assertEquals(MockProxyInterface4Impl.class, clazz);
    }

    /**
     * aop cglib proxy
     */
    @Test
    public void aopCglibProxyTest1() {
        Assert.assertFalse(AopUtils.isJdkDynamicProxy(mockProxyInterface5));
        Class<?> clazz = ProxyClassUtils.getClass(mockProxyInterface5);
        Assert.assertEquals(MockProxyInterface5Impl.class, clazz);
    }

    public interface MockProxyInterface1 {
    }

    public interface MockProxyInterface2 {
    }

    public interface MockProxyInterface3 {
        String find(String input);
    }

    public interface MockProxyInterface4 {
        String find(String input);
    }

    public interface MockProxyInterface5 {
        String find(String input);
    }

    /**
     * 使用注解扫描注册bean
     */
    @ComponentScan
    @Configuration
    protected static class Config {
        @Bean
        public MockProxyInterface3 mockProxyInterface3() {
            return new MockProxyInterface3() {
                @Override
                public String find(String input) {
                    return "hello3 " + input;
                }
            };
        }

        @Bean
        public ProxyFactoryBean mockProxyInterface4() throws ClassNotFoundException {
            ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
            proxyFactoryBean.setProxyInterfaces(new Class[] { MockProxyInterface4.class });
            proxyFactoryBean.setTarget(new MockProxyInterface4Impl());
            return proxyFactoryBean;
        }

        @Bean
        public ProxyFactoryBean mockProxyInterface5() throws ClassNotFoundException {
            ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
            proxyFactoryBean.setProxyInterfaces(new Class[] { MockProxyInterface5.class });
            proxyFactoryBean.setProxyTargetClass(true);
            proxyFactoryBean.setTarget(new MockProxyInterface5Impl());
            return proxyFactoryBean;
        }

        public class MockProxyInterface4Impl implements MockProxyInterface4 {
            @Override
            public String find(String input) {
                return "hello4 " + input;
            }
        }

        public class MockProxyInterface5Impl implements MockProxyInterface5 {
            @Override
            public String find(String input) {
                return "hello5 " + input;
            }
        }

    }
}
