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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

import app.myoss.cloud.apm.log.method.aspectj.MonitorMethodBefore;
import app.myoss.cloud.apm.log.method.aspectj.MonitorMethodProperties;
import app.myoss.cloud.apm.log.method.aspectj.annotation.MonitorMethodAdvice;

/**
 * 测试 {@link MonitorMethodBefore} 的基本功能
 *
 * @author Jerry.Chen
 * @since 2019年1月30日 下午3:31:58
 * @see MonitorMethodBefore
 */
@SpringBootTest(properties = { "myoss-cloud.log.method.app-name:myoss-starter-apm" })
public class MonitorMethodBeforeCase0Tests {
    @Rule
    public OutputCapture        output      = new OutputCapture();
    private MonitorMethodBefore methodAfter = new MonitorMethodBefore();

    @Test
    public void unWantToMatchTest1() throws NoSuchMethodException {
        Method unWantToMatch = MonitorMethodBefore.class.getDeclaredMethod("unWantToMatch");
        Pointcut pointcut = unWantToMatch.getAnnotation(Pointcut.class);
        String value = pointcut.value();
        String excepted = "execution(@org.springframework.web.bind.annotation.ExceptionHandler * *(..)) || execution(@org.springframework.scheduling.annotation.Scheduled * *(..)) || @within(app.myoss.cloud.apm.log.method.aspectj.annotation.LogUnMonitor) || @annotation(app.myoss.cloud.apm.log.method.aspectj.annotation.LogUnMonitor) || @within(app.myoss.cloud.apm.log.method.aspectj.annotation.LogMethodAround) || @annotation(app.myoss.cloud.apm.log.method.aspectj.annotation.LogMethodAround)";
        assertThat(value).isEqualTo(excepted);

        methodAfter.unWantToMatch();
        String printLog = this.output.toString();
        assertThat(printLog).isEmpty();
    }

    @Test
    public void wantToMatchTest1() throws NoSuchMethodException {
        Method wantToMatch = MonitorMethodBefore.class.getDeclaredMethod("wantToMatch");
        Pointcut pointcut = wantToMatch.getAnnotation(Pointcut.class);
        String value = pointcut.value();
        String excepted = "@within(app.myoss.cloud.apm.log.method.aspectj.annotation.LogMethodBefore) || @annotation(app.myoss.cloud.apm.log.method.aspectj.annotation.LogMethodBefore)";
        assertThat(value).isEqualTo(excepted);

        methodAfter.wantToMatch();
        String printLog = this.output.toString();
        assertThat(printLog).isEmpty();
    }

    @Test
    public void allWantToMatchTest1() throws NoSuchMethodException {
        Method allWantToMatch = MonitorMethodBefore.class.getDeclaredMethod("allWantToMatch");
        Pointcut pointcut = allWantToMatch.getAnnotation(Pointcut.class);
        String value = pointcut.value();
        String excepted = "wantToMatch() && ! unWantToMatch()";
        assertThat(value).isEqualTo(excepted);

        methodAfter.allWantToMatch();
        String printLog = this.output.toString();
        assertThat(printLog).isEmpty();
    }

    @Test
    public void doBeforeTest1() throws Throwable {
        Method doBefore = MonitorMethodBefore.class.getDeclaredMethod("doBefore", JoinPoint.class);
        Before afterReturning = doBefore.getAnnotation(Before.class);
        String value = afterReturning.value();
        String excepted = "allWantToMatch()";
        assertThat(value).isEqualTo(excepted);

        // 使用反射更新非 public 字段的值
        Field field = MonitorMethodBefore.class.getSuperclass().getDeclaredField("properties");
        MonitorMethodProperties properties = new MonitorMethodProperties();
        properties.setAppName("myoss-starter-apm");
        properties.init();
        FieldUtils.writeField(field, methodAfter, properties, true);

        Signature signature = Mockito.mock(Signature.class);
        String qualifiedName = ClassUtils.getQualifiedName(MonitorMethodAfterCase0Tests.class);
        Mockito.when(signature.toString()).thenReturn(qualifiedName);
        Mockito.when(signature.getDeclaringTypeName()).thenReturn(qualifiedName);
        Mockito.when(signature.getName()).thenReturn(doBefore.getName());
        JoinPoint joinPoint = Mockito.mock(JoinPoint.class);
        Mockito.when(joinPoint.getSignature()).thenReturn(signature);
        Mockito.when(joinPoint.getArgs()).thenReturn(new Object[] { null, Long.class });
        long startTimeMillis = System.currentTimeMillis();
        methodAfter.doBefore(joinPoint);

        String printLog = this.output.toString();
        assertThat(printLog).contains("[" + qualifiedName + "#" + doBefore.getName() + "]",
                "[MonitorMethodBefore.java");

        String json = StringUtils.substring(printLog, printLog.indexOf(" - {") + 3);
        JSONObject jsonBefore = JSON.parseObject(json);
        assertThat(jsonBefore.getLong("start")).isGreaterThanOrEqualTo(startTimeMillis);
        assertThat(jsonBefore.getJSONArray("args")).isEqualTo(Lists.newArrayList(null, "java.lang.Long"));
        assertThat(jsonBefore.getString("app")).isEqualTo("myoss-starter-apm");
    }

    @Test
    public void checkClassAnnotationTest1() {
        Aspect aspect = MonitorMethodBefore.class.getAnnotation(Aspect.class);
        assertThat(aspect).isNotNull();

        MonitorMethodAdvice methodAdvice = MonitorMethodBefore.class.getAnnotation(MonitorMethodAdvice.class);
        assertThat(methodAdvice).isNotNull();

        Component component = MonitorMethodBefore.class.getAnnotation(Component.class);
        assertThat(component).isNull();
    }
}
