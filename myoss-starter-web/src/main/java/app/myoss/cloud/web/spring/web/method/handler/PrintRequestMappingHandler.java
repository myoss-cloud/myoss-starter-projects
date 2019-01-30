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

package app.myoss.cloud.web.spring.web.method.handler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import lombok.extern.slf4j.Slf4j;

/**
 * 打印 Web 所有的接口映射关系
 *
 * @author Jerry.Chen
 * @since 2019年1月30日 下午2:19:36
 */
@Slf4j
public class PrintRequestMappingHandler {
    /**
     * 打印 {@link RequestMappingHandlerMapping} 中的映射方法
     *
     * @param event ApplicationReadyEvent
     * @see org.springframework.web.servlet.handler.AbstractHandlerMethodMapping#formatMappings(Class,
     *      Map)
     */
    @EventListener
    public void print(ApplicationReadyEvent event) {
        if (!log.isInfoEnabled()) {
            return;
        }
        ApplicationContext applicationContext = event.getApplicationContext();
        Map<String, RequestMappingInfoHandlerMapping> handlerMappingMap = applicationContext
                .getBeansOfType(RequestMappingInfoHandlerMapping.class);
        for (RequestMappingInfoHandlerMapping handlerMapping : handlerMappingMap.values()) {
            Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
            handlerMethods.entrySet()
                    .stream()
                    .collect(Collectors.groupingBy(o -> o.getValue().getBeanType()))
                    .forEach((clazz, entries) -> {
                        String msg = formatMappings(clazz, entries);
                        log.info(msg);
                    });
        }

    }

    private String formatMappings(Class<?> userType, List<Map.Entry<RequestMappingInfo, HandlerMethod>> methods) {
        String formattedType = userType.getName();

        Function<Method, String> methodFormatter = method -> Arrays.stream(method.getParameterTypes())
                .map(Class::getSimpleName)
                .collect(Collectors.joining(",", "(", ")"));

        return methods.stream().map(e -> {
            Method method = e.getValue().getMethod();
            return e.getKey() + ": " + method.getName() + methodFormatter.apply(method);
        }).collect(Collectors.joining("\n    ", "\n    " + formattedType + ":" + "\n    ", ""));
    }
}
