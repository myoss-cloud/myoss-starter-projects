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

package app.myoss.cloud.apm.log.method;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.junit4.SpringRunner;

import app.myoss.cloud.apm.log.method.aspectj.MonitorMethodAfter;
import app.myoss.cloud.apm.log.method.aspectj.MonitorMethodAround;
import app.myoss.cloud.apm.log.method.aspectj.MonitorMethodBefore;
import app.myoss.cloud.apm.log.method.aspectj.annotation.EnableAopLogMethod;
import app.myoss.cloud.apm.log.method.aspectj.annotation.LogMethodAfter;
import app.myoss.cloud.apm.log.method.aspectj.annotation.LogUnMonitor;

/**
 * 在开启 {@code  @EnableAopLogMethod(enableAopLogMethod =  false) } 的时候，注解
 * {@link LogMethodAfter}、{@link LogUnMonitor} 无效
 *
 * @author Jerry.Chen
 * @since 2019年1月30日 下午3:31:58
 */
@RunWith(SpringRunner.class)
public class DisableUnMonitorMethodAfterCase1Tests {
    @Rule
    public ExpectedException   thrown = ExpectedException.none();

    @Autowired
    private ApplicationContext context;

    @Test
    public void isInjectComponent() {
        context.getBean(LogOnMethodTest.class);
        context.getBean(LogOnClassTest.class);
        context.getBean(LogOnClassAndMethodTest.class);
    }

    @Test
    public void didNotInjectMonitorMethodBefore() {
        thrown.expect(NoSuchBeanDefinitionException.class);
        context.getBean(MonitorMethodBefore.class);
    }

    @Test
    public void didNotInjectMonitorMethodAfter() {
        thrown.expect(NoSuchBeanDefinitionException.class);
        context.getBean(MonitorMethodAfter.class);
    }

    @Test
    public void didNotInjectMonitorMethodAround() {
        thrown.expect(NoSuchBeanDefinitionException.class);
        context.getBean(MonitorMethodAround.class);
    }

    // 开启AspectJ
    @EnableAspectJAutoProxy
    @EnableAopLogMethod(enableAopLogMethod = false)
    @Configuration
    protected static class Config {
        @Bean
        public LogOnMethodTest logOnMethodTest() {
            return new LogOnMethodTest();
        }

        @Bean
        public LogOnClassTest logOnClassTest() {
            return new LogOnClassTest();
        }

        @Bean
        public LogOnClassAndMethodTest logOnClassAndMethodTest() {
            return new LogOnClassAndMethodTest();
        }
    }

    /**
     * 注解 {@link LogMethodAfter}、{@link LogUnMonitor} 放在方法上
     */
    protected static class LogOnMethodTest {
        @LogUnMonitor
        @LogMethodAfter
        public String isMatch() {
            return "matched";
        }

        @LogUnMonitor
        @LogMethodAfter
        public String isMatch2(String name) {
            return "matched2, " + name;
        }

        public String isNotMatch() {
            return "not matched";
        }
    }

    /**
     * 注解 {@link LogMethodAfter}、{@link LogUnMonitor} 放在类上
     */
    @LogUnMonitor
    @LogMethodAfter
    protected static class LogOnClassTest {
        public String isMatch1() {
            return "matched1";
        }

        public String isMatch2() {
            return "matched2";
        }

        public String isMatch3(String name) {
            return "matched3, " + name;
        }
    }

    /**
     * 注解 {@link LogMethodAfter}、{@link LogUnMonitor} 放在类和方法上
     */
    @LogUnMonitor
    @LogMethodAfter
    protected static class LogOnClassAndMethodTest {
        @LogUnMonitor
        @LogMethodAfter
        public String isMatch1() {
            return "matched1";
        }

        @LogUnMonitor
        @LogMethodAfter
        public String isMatch2() {
            return "matched2";
        }

        public String isMatch3(String name) {
            return "matched3, " + name;
        }
    }
}
