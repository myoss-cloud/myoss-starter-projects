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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.system.OutputCaptureRule;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.junit4.SpringRunner;

import app.myoss.cloud.apm.log.method.aspectj.MonitorMethodAfter;
import app.myoss.cloud.apm.log.method.aspectj.MonitorMethodAround;
import app.myoss.cloud.apm.log.method.aspectj.MonitorMethodBefore;
import app.myoss.cloud.apm.log.method.aspectj.annotation.EnableAopLogMethod;
import app.myoss.cloud.apm.log.method.aspectj.annotation.LogMethodBefore;
import app.myoss.cloud.apm.log.method.aspectj.annotation.LogUnMonitor;

/**
 * 注解 {@link LogMethodBefore}、 {@link LogUnMonitor} 放在类上，不输出log，只有
 * {@link LogUnMonitor} 生效
 *
 * @author Jerry.Chen
 * @since 2019年1月30日 下午3:31:58
 */
@RunWith(SpringRunner.class)
public class UnMonitorMethodBeforeCase2Tests {
    @Rule
    public OutputCaptureRule   output = new OutputCaptureRule();
    @Rule
    public ExpectedException   thrown = ExpectedException.none();

    @Autowired
    private ApplicationContext context;

    @Test
    public void isInjectMonitorMethodAdvice() {
        context.getBean(MonitorMethodBefore.class);
        context.getBean(MonitorMethodAfter.class);
        context.getBean(MonitorMethodAround.class);
    }

    @Autowired
    private LogOnClassTest logOnClassTest;

    @Test
    public void logOnClassMatchTest1() {
        logOnClassTest.isMatch1();
        String printLog = this.output.toString();
        assertThat(printLog).isEmpty();
    }

    @Test
    public void logOnClassMatchTest2() {
        logOnClassTest.isMatch2();
        String printLog = this.output.toString();
        assertThat(printLog).isEmpty();
    }

    @Test
    public void logOnClassMatchTest3() {
        String name = "jerry";
        logOnClassTest.isMatch3(name);
        String printLog = this.output.toString();
        assertThat(printLog).isEmpty();
    }

    // 开启AspectJ
    @EnableAspectJAutoProxy
    @EnableAopLogMethod
    @Configuration
    protected static class Config {
        @Bean
        public LogOnClassTest logOnClassTest() {
            return new LogOnClassTest();
        }
    }

    /**
     * 注解 {@link LogMethodBefore}、{@link LogUnMonitor} 放在类上
     */
    @LogUnMonitor
    @LogMethodBefore
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
}
