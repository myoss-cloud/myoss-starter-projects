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

package app.myoss.cloud.web.spring.web.method.aspectj;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import app.myoss.cloud.web.spring.web.method.aspectj.annatation.EnableAopLogController;
import lombok.extern.slf4j.Slf4j;

/**
 * 在开启
 * {@code  @EnableAopLogController(enableAopLogControllerException = false) }
 * 的时候，自动记录 controller 异常的功能 {@link AopLogControllerExceptionHandler} 无效
 *
 * @author Jerry.Chen
 * @since 2019年1月30日 下午3:31:58
 * @see AopLogControllerExceptionHandler
 * @see EnableAopLogController
 */
@RunWith(SpringRunner.class)
@WebAppConfiguration
public class DisableAopLogControllerExceptionHandlerCase1Tests {
    @Autowired
    private ApplicationContext context;

    @Test
    public void isInjectComponent() {
        context.getBean("readerBodyHttpServletRequestFilter", FilterRegistrationBean.class);
    }

    @Test
    public void didNotInjectControllerAdvice() {
        Assert.assertThrows(NoSuchBeanDefinitionException.class,
                () -> context.getBean(AopLogControllerExceptionHandler.class));
    }

    @Slf4j
    @EnableAopLogController(enableAopLogControllerException = false)
    @Configuration
    @Import(UnitWebAppMvcConfig.class)
    protected static class Config {
    }
}
