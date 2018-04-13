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

package com.github.myoss.phoenix.core.log.method.aspectj;

import java.lang.reflect.Method;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.github.myoss.phoenix.core.log.method.aspectj.annotation.LogMethodBefore;
import com.github.myoss.phoenix.core.log.method.aspectj.annotation.MonitorMethodAdvice;

/**
 * 记录方法的入参，使用注解： {@link LogMethodBefore}
 * <p>
 * 非private/final的方法，非AOP调用的方法也是不支持的
 *
 * @author Jerry.Chen 2018年4月11日 下午12:15:25
 * @see LogMethodBefore
 */
@Slf4j(topic = "MonitorMethod")
@Aspect
@MonitorMethodAdvice
public class MonitorMethodBefore extends AbstractMonitorMethod {

    /**
     * 排除哪些方法
     * <ul>
     * <li>排除方法上有@ExceptionHandler注解，Controller的异常处理:
     * <code>@org.springframework.web.bind.annotation.ExceptionHandler</code>
     * <li>排除方法上有@Scheduled注解，Spring Task定时任务:
     * <code>@org.springframework.scheduling.annotation.Scheduled</code>
     * <li>排除方法、类上有@LogUnMonitor注解，自定义Log注解:
     * <code>@com.github.myoss.phoenix.core.log.method.aspectj.annotation.LogUnMonitor</code>
     * <li>排除方法、类上有@LogMethodAround注解，自定义Log注解:
     * <code>@com.github.myoss.phoenix.core.log.method.aspectj.annotation.LogMethodAround</code>
     * </ul>
     *
     * @see org.springframework.aop.aspectj.AspectJExpressionPointcut#matches(Method,
     *      Class, boolean)
     */
    @Pointcut("execution(@org.springframework.web.bind.annotation.ExceptionHandler * *(..))"
            + " || execution(@org.springframework.scheduling.annotation.Scheduled * *(..))"
            + " || @within(com.github.myoss.phoenix.core.log.method.aspectj.annotation.LogUnMonitor)" // 放在class上有效，method上无效
            + " || @annotation(com.github.myoss.phoenix.core.log.method.aspectj.annotation.LogUnMonitor)" // 放在class上无效，method上有效
            + " || @within(com.github.myoss.phoenix.core.log.method.aspectj.annotation.LogMethodAround)"
            + " || @annotation(com.github.myoss.phoenix.core.log.method.aspectj.annotation.LogMethodAround)")
    public void unWantToMatch() {
        // Do nothing
    }

    /**
     * 监控哪些方法
     * <ul>
     * <li>方法、类上有@LogMethodBefore注解，自定义Log注解:
     * <code>@com.github.myoss.phoenix.core.log.method.aspectj.annotation.LogMethodBefore</code>
     * </ul>
     */
    @Pointcut("@within(com.github.myoss.phoenix.core.log.method.aspectj.annotation.LogMethodBefore)"// 放在class上有效，method上无效
            + " || @annotation(com.github.myoss.phoenix.core.log.method.aspectj.annotation.LogMethodBefore)" // 放在class上无效，method上有效
    )
    public void wantToMatch() {
        // Do nothing
    }

    @Pointcut("wantToMatch() && ! unWantToMatch()")
    public void allWantToMatch() {
        // Do nothing
    }

    @Before("allWantToMatch()")
    public void doBefore(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getDeclaringTypeName() + "#" + signature.getName();
        Logger logger = LoggerFactory.getLogger(methodName);
        JSONObject jsonBefore = new JSONObject();
        jsonBefore.put("start", System.currentTimeMillis());
        jsonBefore.put("args", convertArgs(joinPoint.getArgs()));
        jsonBefore.put("app", properties.getAppName());
        logger.info(toJSONString(jsonBefore));
    }

}
