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

package app.myoss.cloud.apm.log.method.aspectj;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import app.myoss.cloud.apm.log.method.aspectj.annotation.LogMethodAround;
import app.myoss.cloud.apm.log.method.aspectj.annotation.MonitorMethodAdvice;
import lombok.extern.slf4j.Slf4j;

/**
 * 记录方法的入参和返回值，使用注解： {@link LogMethodAround}
 * <p>
 * 非private/final的方法，非AOP调用的方法也是不支持的
 *
 * @author Jerry.Chen
 * @since 2018年4月11日 下午12:15:16
 * @see LogMethodAround
 */
@Slf4j(topic = "MonitorMethod")
@Aspect
@MonitorMethodAdvice
public class MonitorMethodAround extends AbstractMonitorMethod {

    /**
     * 排除哪些方法
     * <ul>
     * <li>排除方法上有@ExceptionHandler注解，Controller的异常处理:
     * <code>@org.springframework.web.bind.annotation.ExceptionHandler</code>
     * <li>排除方法上有@Scheduled注解，Spring Task定时任务:
     * <code>@org.springframework.scheduling.annotation.Scheduled</code>
     * <li>排除方法、类上有@LogUnMonitor注解，自定义Log注解:
     * <code>@app.myoss.cloud.apm.log.method.aspectj.annotation.LogUnMonitor</code>
     * </ul>
     *
     * @see org.springframework.aop.aspectj.AspectJExpressionPointcut#matches(Method,
     *      Class, boolean)
     */
    @Pointcut("execution(@org.springframework.web.bind.annotation.ExceptionHandler * *(..))"
            + " || execution(@org.springframework.scheduling.annotation.Scheduled * *(..))"
            // 放在class上有效，method上无效
            + " || @within(app.myoss.cloud.apm.log.method.aspectj.annotation.LogUnMonitor)"
            // 放在class上无效，method上有效
            + " || @annotation(app.myoss.cloud.apm.log.method.aspectj.annotation.LogUnMonitor)")
    public void unWantToMatch() {
        // Do nothing
    }

    /**
     * 监控哪些方法
     * <ul>
     * <li>方法、类上有@LogMethodAround，自定义Log注解:
     * <code>@app.myoss.cloud.apm.log.method.aspectj.annotation.LogMethodAround</code>
     * </ul>
     */
    @Pointcut("@within(app.myoss.cloud.apm.log.method.aspectj.annotation.LogMethodAround)"
            // 放在class上有效，method上无效
            + " || @annotation(app.myoss.cloud.apm.log.method.aspectj.annotation.LogMethodAround)"
    // 放在class上无效，method上有效
    )
    public void wantToMatch() {
        // Do nothing
    }

    /**
     * 监控规则
     */
    @Pointcut("wantToMatch() && ! unWantToMatch()")
    public void allWantToMatch() {
        // Do nothing
    }

    /**
     * 使用 AOP 记录方法的入参和返回值
     *
     * @param joinPoint AOP JoinPoint
     * @return 方法返回值
     * @throws Throwable 执行异常
     */
    @Around("allWantToMatch()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTimeMillis = System.currentTimeMillis();
        long start = System.nanoTime();
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getDeclaringTypeName() + "#" + signature.getName();
        Logger logger = LoggerFactory.getLogger(methodName);
        Map<String, Object> jsonBefore = new HashMap<>(3);
        jsonBefore.put("start", startTimeMillis);
        jsonBefore.put("args", convertArgs(joinPoint.getArgs()));
        jsonBefore.put("app", properties.getAppName());
        logger.info(toJSONString(jsonBefore));

        // 这里不要去做： try/catch, A catch statement should never catch throwable since it includes errors
        Object result = joinPoint.proceed();

        long costTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        Map<String, Object> jsonAfter = new HashMap<>(5);
        jsonAfter.put("start", startTimeMillis);
        jsonAfter.put("end", System.currentTimeMillis());
        jsonAfter.put("cost", costTime);
        jsonAfter.put("result", result);
        jsonAfter.put("app", properties.getAppName());
        logger.info(toJSONString(jsonAfter));

        return result;
    }
}
