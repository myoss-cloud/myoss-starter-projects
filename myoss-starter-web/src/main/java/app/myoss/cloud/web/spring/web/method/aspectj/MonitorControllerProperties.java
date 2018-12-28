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

package app.myoss.cloud.web.spring.web.method.aspectj;

import org.springframework.boot.context.properties.ConfigurationProperties;

import app.myoss.cloud.apm.constants.ApmConstants;
import lombok.Data;

/**
 * 使用slf4j记录 {@link org.springframework.stereotype.Controller} 的信息，属性配置
 *
 * @author Jerry.Chen
 * @since 2018年4月13日 下午12:15:37
 */
@Data
@ConfigurationProperties(prefix = ApmConstants.MONITOR_CONTROLLER_CONFIG_PREFIX)
public class MonitorControllerProperties {
    /**
     * Controller异常时返回的errorCode
     *
     * @see AopLogControllerExceptionHandler#outputException(Throwable,
     *      org.springframework.http.HttpHeaders,
     *      org.springframework.http.HttpStatus,
     *      javax.servlet.http.HttpServletRequest)
     */
    private String controllerExceptionErrorCode = "systemException";
    /**
     * Controller异常时返回的errorCode
     *
     * @see AopLogControllerExceptionHandler#outputException(Throwable,
     *      org.springframework.http.HttpHeaders,
     *      org.springframework.http.HttpStatus,
     *      javax.servlet.http.HttpServletRequest)
     */
    private String controllerExceptionErrorMsg  = "We'll be back soon ...";
}
