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
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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

import app.myoss.cloud.apm.log.method.aspectj.MonitorMethodAround;
import app.myoss.cloud.apm.log.method.aspectj.MonitorMethodProperties;
import app.myoss.cloud.apm.log.method.aspectj.annotation.MonitorMethodAdvice;

/**
 * 测试 {@link MonitorMethodAround} 的基本功能
 *
 * @author Jerry.Chen
 * @since 2019年1月30日 下午3:31:58
 * @see MonitorMethodAround
 */
@SpringBootTest(properties = { "myoss-cloud.log.method.app-name:myoss-starter-apm" })
public class MonitorMethodAroundCase0Tests {
    @Rule
    public OutputCapture        output      = new OutputCapture();
    private MonitorMethodAround methodAfter = new MonitorMethodAround();

    @Test
    public void unWantToMatchTest1() throws NoSuchMethodException {
        Method unWantToMatch = MonitorMethodAround.class.getDeclaredMethod("unWantToMatch");
        Pointcut pointcut = unWantToMatch.getAnnotation(Pointcut.class);
        String value = pointcut.value();
        String excepted = "execution(@org.springframework.web.bind.annotation.ExceptionHandler * *(..)) || execution(@org.springframework.scheduling.annotation.Scheduled * *(..)) || @within(app.myoss.cloud.apm.log.method.aspectj.annotation.LogUnMonitor) || @annotation(app.myoss.cloud.apm.log.method.aspectj.annotation.LogUnMonitor)";
        assertThat(value).isEqualTo(excepted);

        methodAfter.unWantToMatch();
        String printLog = this.output.toString();
        assertThat(printLog).isEmpty();
    }

    @Test
    public void wantToMatchTest1() throws NoSuchMethodException {
        Method wantToMatch = MonitorMethodAround.class.getDeclaredMethod("wantToMatch");
        Pointcut pointcut = wantToMatch.getAnnotation(Pointcut.class);
        String value = pointcut.value();
        String excepted = "@within(app.myoss.cloud.apm.log.method.aspectj.annotation.LogMethodAround) || @annotation(app.myoss.cloud.apm.log.method.aspectj.annotation.LogMethodAround)";
        assertThat(value).isEqualTo(excepted);

        methodAfter.wantToMatch();
        String printLog = this.output.toString();
        assertThat(printLog).isEmpty();
    }

    @Test
    public void allWantToMatchTest1() throws NoSuchMethodException {
        Method allWantToMatch = MonitorMethodAround.class.getDeclaredMethod("allWantToMatch");
        Pointcut pointcut = allWantToMatch.getAnnotation(Pointcut.class);
        String value = pointcut.value();
        String excepted = "wantToMatch() && ! unWantToMatch()";
        assertThat(value).isEqualTo(excepted);

        methodAfter.allWantToMatch();
        String printLog = this.output.toString();
        assertThat(printLog).isEmpty();
    }

    @Test
    public void doAroundTest1() throws Throwable {
        Method doAround = MonitorMethodAround.class.getDeclaredMethod("doAround", ProceedingJoinPoint.class);
        Around around = doAround.getAnnotation(Around.class);
        String value = around.value();
        String excepted = "allWantToMatch()";
        assertThat(value).isEqualTo(excepted);

        // 使用反射更新非 public 字段的值
        Field field = MonitorMethodAround.class.getSuperclass().getDeclaredField("properties");
        MonitorMethodProperties properties = new MonitorMethodProperties();
        properties.setAppName("myoss-starter-apm");
        properties.init();
        FieldUtils.writeField(field, methodAfter, properties, true);

        Signature signature = Mockito.mock(Signature.class);
        String qualifiedName = ClassUtils.getQualifiedName(MonitorMethodAroundCase0Tests.class);
        Mockito.when(signature.toString()).thenReturn(qualifiedName);
        Mockito.when(signature.getDeclaringTypeName()).thenReturn(qualifiedName);
        Mockito.when(signature.getName()).thenReturn(doAround.getName());
        ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
        Mockito.when(joinPoint.getSignature()).thenReturn(signature);
        Mockito.when(joinPoint.getArgs()).thenReturn(new Object[] { null, Long.class });
        Mockito.when(joinPoint.proceed()).thenReturn("matched");
        long startTimeMillis = System.currentTimeMillis();
        Object result = methodAfter.doAround(joinPoint);
        assertThat(result).isEqualTo("matched");
        long endTimeMillis = System.currentTimeMillis();

        String printLog = this.output.toString();
        String[] lines = printLog.split(System.getProperty("line.separator"));
        assertThat(lines).hasSize(2);
        String beforeLine = lines[0];
        String afterLine = lines[1];
        assertThat(beforeLine).contains("[" + qualifiedName + "#" + doAround.getName() + "]",
                "[MonitorMethodAround.java");
        assertThat(afterLine).contains("[" + qualifiedName + "#" + doAround.getName() + "]",
                "[MonitorMethodAround.java");

        String beforeJson = StringUtils.substring(beforeLine, beforeLine.indexOf(" - {") + 3);
        JSONObject jsonBefore = JSON.parseObject(beforeJson);
        assertThat(jsonBefore.getLong("start")).isGreaterThanOrEqualTo(startTimeMillis);
        assertThat(jsonBefore.getJSONArray("args")).isEqualTo(Lists.newArrayList(null, "java.lang.Long"));
        assertThat(jsonBefore.getString("app")).isEqualTo("myoss-starter-apm");

        String afterJson = StringUtils.substring(afterLine, afterLine.indexOf(" - {") + 3);
        JSONObject jsonAfter = JSON.parseObject(afterJson);
        assertThat(jsonAfter.getLong("start")).isGreaterThanOrEqualTo(startTimeMillis);
        assertThat(jsonAfter.getLong("end")).isLessThanOrEqualTo(endTimeMillis);
        assertThat(jsonAfter.getLong("cost")).isLessThanOrEqualTo(endTimeMillis - startTimeMillis);
        assertThat(jsonAfter.getString("result")).isEqualTo("matched");
        assertThat(jsonAfter.getString("app")).isEqualTo("myoss-starter-apm");
    }

    @Test
    public void checkClassAnnotationTest1() {
        Aspect aspect = MonitorMethodAround.class.getAnnotation(Aspect.class);
        assertThat(aspect).isNotNull();

        MonitorMethodAdvice methodAdvice = MonitorMethodAround.class.getAnnotation(MonitorMethodAdvice.class);
        assertThat(methodAdvice).isNotNull();

        Component component = MonitorMethodAround.class.getAnnotation(Component.class);
        assertThat(component).isNull();
    }
}
