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

import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import app.myoss.cloud.apm.log.method.aspectj.annotation.LogMethodAfter;
import app.myoss.cloud.core.lang.json.JsonApi;
import app.myoss.cloud.core.lang.json.JsonObject;

/**
 * 注解 {@link LogMethodAfter} 放在类和方法上
 *
 * @author Jerry.Chen
 * @since 2019年1月30日 下午3:31:58
 */
@SpringBootTest(properties = { "myoss-cloud.log.method.app-name:myoss-starter-apm" })
@RunWith(SpringRunner.class)
public class MonitorMethodAfterCase3Tests {
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
    private LogOnClassAndMethodTest logOnClassAndMethodTest;

    @Test
    public void logOnClassAndMethodMatchTest1() {
        logOnClassAndMethodTest.isMatch1();
        long endTimeMillis = System.currentTimeMillis();
        String printLog = this.output.toString();
        assertThat(printLog).contains(
                "[app.myoss.cloud.apm.log.method.MonitorMethodAfterCase3Tests$LogOnClassAndMethodTest#isMatch1]",
                "[MonitorMethodAfter.java");

        String json = StringUtils.substring(printLog, printLog.indexOf(" - {") + 3);
        JsonObject jsonAfter = JsonApi.fromJson(json);
        assertThat(jsonAfter.getAsLong("end")).isLessThanOrEqualTo(endTimeMillis);
        assertThat(jsonAfter.getAsString("result")).isEqualTo("matched1");
        assertThat(jsonAfter.getAsString("app")).isEqualTo("myoss-starter-apm");
    }

    @Test
    public void logOnClassAndMethodMatchTest2() {
        logOnClassAndMethodTest.isMatch2();
        long endTimeMillis = System.currentTimeMillis();
        String printLog = this.output.toString();
        assertThat(printLog).contains(
                "[app.myoss.cloud.apm.log.method.MonitorMethodAfterCase3Tests$LogOnClassAndMethodTest#isMatch2]",
                "[MonitorMethodAfter.java");

        String json = StringUtils.substring(printLog, printLog.indexOf(" - {") + 3);
        JsonObject jsonAfter = JsonApi.fromJson(json);
        assertThat(jsonAfter.getAsLong("end")).isLessThanOrEqualTo(endTimeMillis);
        assertThat(jsonAfter.getAsString("result")).isEqualTo("matched2");
        assertThat(jsonAfter.getAsString("app")).isEqualTo("myoss-starter-apm");
    }

    @Test
    public void logOnClassAndMethodMatchTest3() {
        String name = "jerry";
        logOnClassAndMethodTest.isMatch3(name);
        long endTimeMillis = System.currentTimeMillis();
        String printLog = this.output.toString();
        assertThat(printLog).contains(
                "[app.myoss.cloud.apm.log.method.MonitorMethodAfterCase3Tests$LogOnClassAndMethodTest#isMatch3]",
                "[MonitorMethodAfter.java");

        String json = StringUtils.substring(printLog, printLog.indexOf(" - {") + 3);
        JsonObject jsonAfter = JsonApi.fromJson(json);
        assertThat(jsonAfter.getAsLong("end")).isLessThanOrEqualTo(endTimeMillis);
        assertThat(jsonAfter.getAsString("result")).isEqualTo("matched3, " + name);
        assertThat(jsonAfter.getAsString("app")).isEqualTo("myoss-starter-apm");
    }

    // 开启AspectJ
    @EnableAspectJAutoProxy
    @EnableAopLogMethod
    @Configuration
    protected static class Config {
        @Bean
        public LogOnClassAndMethodTest logOnClassAndMethodTest() {
            return new LogOnClassAndMethodTest();
        }
    }

    /**
     * 注解 {@link LogMethodAfter} 放在类和方法上
     */
    @LogMethodAfter
    protected static class LogOnClassAndMethodTest {
        @LogMethodAfter
        public String isMatch1() {
            return "matched1";
        }

        @LogMethodAfter
        public String isMatch2() {
            return "matched2";
        }

        public String isMatch3(String name) {
            return "matched3, " + name;
        }
    }

}
