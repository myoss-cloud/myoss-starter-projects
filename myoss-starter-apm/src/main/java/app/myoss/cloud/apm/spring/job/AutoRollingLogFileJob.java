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

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import app.myoss.cloud.apm.constants.ApmConstants;
import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TriggeringPolicy;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 日志文件自动滚动生成新的文件
 *
 * @author Jerry.Chen
 * @since 2018年12月15日 下午9:56:04
 */
@AllArgsConstructor
@Slf4j
public class AutoRollingLogFileJob {
    /**
     * 属性配置
     */
    private AutoRollingLogFileProperties autoRollingLogFileProperties;

    /**
     * 每天凌晨检查日志文件，是否已经自动滚动了新的文件；logback的日志滚动策略，只有在有日志输出的时候，才会去检查是否需要滚动，如果第二天日志没有输出，就不会生成新的文件
     */
    @SuppressWarnings("unchecked")
    @Scheduled(cron = "${" + ApmConstants.AUTO_ROLLING_LOG_FILE_CONFIG_PREFIX + ".cron:"
            + AutoRollingLogFileProperties.DEFAULT_CRON + "}")
    public void checkAndRollFile() {
        List<Logger> loggers = new ArrayList<>();
        List<String> loggerNames = autoRollingLogFileProperties.getLoggerNames();
        for (String loggerName : loggerNames) {
            org.slf4j.Logger logger = LoggerFactory.getLogger(loggerName);
            if (!(logger instanceof Logger)) {
                log.warn("Logger {} is not ch.qos.logback.classic.Logger, ignore", loggerName);
                return;
            }
            loggers.add((Logger) logger);
        }

        LoggingEvent event = new LoggingEvent();
        List<String> rollingFileAppenderNames = autoRollingLogFileProperties.getRollingFileAppenderNames();
        for (String appenderName : rollingFileAppenderNames) {
            RollingFileAppender file = getRollingFileAppender(loggers, appenderName);
            if (file == null) {
                log.warn("{} is null, ignore check and roll file", appenderName);
                continue;
            }
            TriggeringPolicy triggeringPolicy = file.getTriggeringPolicy();
            String fileName = file.getFile();
            if (triggeringPolicy.isTriggeringEvent(new File(fileName), event)) {
                log.info("{} rollover begin", fileName);
                file.rollover();
                log.info("{} rollover complete", fileName);
            }
        }
    }

    private RollingFileAppender getRollingFileAppender(List<Logger> loggers, String appenderName) {
        for (Logger logger : loggers) {
            Appender<ILoggingEvent> appender = logger.getAppender(appenderName);
            if (appender instanceof RollingFileAppender) {
                return (RollingFileAppender) appender;
            }
            Iterator<Appender<ILoggingEvent>> appenderEvents = logger.iteratorForAppenders();
            while (appenderEvents.hasNext()) {
                Appender<ILoggingEvent> next = appenderEvents.next();
                if (next instanceof AsyncAppender) {
                    AsyncAppender asyncAppender = (AsyncAppender) next;
                    appender = asyncAppender.getAppender(appenderName);
                    if (appender instanceof RollingFileAppender) {
                        return (RollingFileAppender) appender;
                    }
                }
            }
        }
        return null;
    }
}
