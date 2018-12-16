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

package app.myoss.cloud.apm.spring.job;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.CollectionUtils;

import app.myoss.cloud.apm.constants.ApmConstants;
import lombok.Data;

/**
 * 每天定时检查日志文件，是否已经滚动生成新的文件，没有就触发生成新的文件，属性配置
 *
 * @author Jerry.Chen
 * @since 2018年12月16日 上午9:30:18
 */
@Data
@ConfigurationProperties(prefix = ApmConstants.AUTO_ROLLING_LOG_FILE_CONFIG_PREFIX)
public class AutoRollingLogFileProperties {
    /**
     * 每天什么时候触发定时任务执行，cron表达式，默认是：0 10 0 * * ?
     */
    public static final String DEFAULT_CRON = "0 10 0 * * ?";

    /**
     * 启用自动配置，每天定时检查日志文件，是否已经滚动生成新的文件，没有就触发生成新的文件
     */
    private Boolean            enabled      = true;
    /**
     * 每天什么时候触发定时任务执行，cron表达式，默认是：0 10 0 * * ?
     */
    private String             cron;
    /**
     * 需要检查哪些 RollingFileAppender name，默认是：infoAppender, requestInfoAppender,
     * errorAppender
     */
    private List<String>       rollingFileAppenderNames;
    /**
     * 需要检查的 RollingFileAppender name，是在哪些 loggerName 配置下的，默认是：ROOT, WebRequest
     */
    private List<String>       loggerNames;

    /**
     * 初始化属性
     */
    @PostConstruct
    public void init() {
        if (StringUtils.isBlank(cron)) {
            cron = DEFAULT_CRON;
        }
        if (CollectionUtils.isEmpty(rollingFileAppenderNames)) {
            rollingFileAppenderNames = Stream.of("infoAppender", "requestInfoAppender", "errorAppender")
                    .collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(loggerNames)) {
            loggerNames = Stream.of(org.slf4j.Logger.ROOT_LOGGER_NAME, "WebRequest").collect(Collectors.toList());
        }
    }
}
