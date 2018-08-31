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

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.ClassUtils;

import lombok.Data;

/**
 * 使用slf4j记录方法的入参和出参，属性配置
 *
 * @author Jerry.Chen
 * @since 2018年4月13日 下午12:15:37
 */
@Data
@ConfigurationProperties(prefix = "myoss-cloud.log.method")
public class MonitorMethodProperties {
    /**
     * 使用slf4j记录方法的入参和出参，输出应用名字，默认没有设置
     *
     * @see MonitorMethodBefore#doBefore(JoinPoint)
     * @see MonitorMethodAfter#doAfterReturning(JoinPoint, Object)
     * @see MonitorMethodAround#doAround(ProceedingJoinPoint)
     */
    private String      appName;

    /**
     * 使用slf4j记录方法的入参和出参，动态排除的class（某些class不一定在依赖中），转换那些无法被JSON序列化的对象，比如：
     * ServletRequest/ServletResponse.
     *
     * @see #getExcludeClass()
     */
    private Set<String> dynamicExcludeClass;
    /**
     * 使用slf4j记录方法的入参和出参，动态排除的class（某些class不一定在依赖中），转换那些无法被JSON序列化的对象，比如：
     * ServletRequest/ServletResponse.
     *
     * @see AbstractMonitorMethod#convertArgs(Object[])
     */
    private Set<Class>  excludeClass;
    /**
     * 日期字段格式，默认是：yyyy-MM-dd HH:mm:ss.SSS
     *
     * @see AbstractMonitorMethod#toJSONString(Object)
     */
    private String      dateFormat                   = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * Controller异常时返回的errorCode
     *
     * @see AopLogControllerExceptionHandler#outputException(Throwable,
     *      org.springframework.http.HttpHeaders,
     *      org.springframework.http.HttpStatus,
     *      javax.servlet.http.HttpServletRequest)
     */
    private String      controllerExceptionErrorCode = "systemException";
    /**
     * Controller异常时返回的errorCode
     *
     * @see AopLogControllerExceptionHandler#outputException(Throwable,
     *      org.springframework.http.HttpHeaders,
     *      org.springframework.http.HttpStatus,
     *      javax.servlet.http.HttpServletRequest)
     */
    private String      controllerExceptionErrorMsg  = "We'll be back soon ...";

    /**
     * 初始化属性
     */
    @PostConstruct
    public void init() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        if (dynamicExcludeClass == null) {
            dynamicExcludeClass = new LinkedHashSet<>();
        }
        if (excludeClass == null) {
            excludeClass = new LinkedHashSet<>();
        }
        dynamicExcludeClass.add("javax.servlet.ServletRequest");
        dynamicExcludeClass.add("javax.servlet.ServletResponse");
        for (String item : dynamicExcludeClass) {
            if (ClassUtils.isPresent(item, classLoader)) {
                Class<?> aClass = ClassUtils.resolveClassName(item, classLoader);
                excludeClass.add(aClass);
            }
        }
    }
}
