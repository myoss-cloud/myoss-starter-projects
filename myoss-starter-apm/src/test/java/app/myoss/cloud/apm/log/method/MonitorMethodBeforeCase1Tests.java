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
import app.myoss.cloud.apm.log.method.aspectj.annotation.LogMethodBefore;
import app.myoss.cloud.core.lang.json.JsonApi;
import app.myoss.cloud.core.lang.json.JsonObject;

/**
 * 注解 {@link LogMethodBefore} 放在方法上
 *
 * @author Jerry.Chen
 * @since 2019年1月30日 下午3:31:58
 */
@SpringBootTest(properties = { "myoss-cloud.log.method.app-name:myoss-starter-apm" })
@RunWith(SpringRunner.class)
public class MonitorMethodBeforeCase1Tests {
    @Rule
    public OutputCaptureRule   output = new OutputCaptureRule();

    @Autowired
    private ApplicationContext context;

    @Test
    public void isInjectMonitorMethodAdvice() {
        context.getBean(MonitorMethodBefore.class);
        context.getBean(MonitorMethodAfter.class);
        context.getBean(MonitorMethodAround.class);
    }

    @Autowired
    private LogOnMethodTest logOnMethodTest;

    @Test
    public void logOnMethodMatchTest1() {
        long startTimeMillis = System.currentTimeMillis();
        logOnMethodTest.isMatch();
        String printLog = this.output.toString();
        assertThat(printLog).contains(
                "[app.myoss.cloud.apm.log.method.MonitorMethodBeforeCase1Tests$LogOnMethodTest#isMatch]",
                "[MonitorMethodBefore.java");

        String json = StringUtils.substring(printLog, printLog.indexOf(" - {") + 3);
        JsonObject jsonBefore = JsonApi.fromJson(json);
        assertThat(jsonBefore.getAsLong("start")).isGreaterThanOrEqualTo(startTimeMillis);
        assertThat(jsonBefore.getAsJsonArray("args")).isEmpty();
        assertThat(jsonBefore.getAsString("app")).isEqualTo("myoss-starter-apm");
    }

    @Test
    public void logOnMethodMatchTest2() {
        String name = "jerry";
        long startTimeMillis = System.currentTimeMillis();
        logOnMethodTest.isMatch2(name);
        String printLog = this.output.toString();
        assertThat(printLog).contains(
                "[app.myoss.cloud.apm.log.method.MonitorMethodBeforeCase1Tests$LogOnMethodTest#isMatch2]",
                "[MonitorMethodBefore.java");

        String json = StringUtils.substring(printLog, printLog.indexOf(" - {") + 3);
        JsonObject jsonBefore = JsonApi.fromJson(json);
        assertThat(jsonBefore.getAsLong("start")).isGreaterThanOrEqualTo(startTimeMillis);
        assertThat(jsonBefore.getAsJsonArray("args")).containsExactly(name);
        assertThat(jsonBefore.getAsString("app")).isEqualTo("myoss-starter-apm");
    }

    @Test
    public void logOnMethodIsNotMatchTest() {
        logOnMethodTest.isNotMatch();
        String printLog = this.output.toString();
        assertThat(printLog).isEmpty();
    }

    // 开启AspectJ
    @EnableAspectJAutoProxy
    @EnableAopLogMethod
    @Configuration
    protected static class Config {
        @Bean
        public LogOnMethodTest logOnMethodTest() {
            return new LogOnMethodTest();
        }
    }

    /**
     * 注解 {@link LogMethodBefore} 放在方法上
     */
    protected static class LogOnMethodTest {
        @LogMethodBefore
        public String isMatch() {
            return "matched";
        }

        @LogMethodBefore
        public String isMatch2(String name) {
            return "matched2, " + name;
        }

        public String isNotMatch() {
            return "not matched";
        }
    }
}
