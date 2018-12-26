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

package app.myoss.cloud.apm.spring.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import app.myoss.cloud.apm.constants.ApmConstants;
import app.myoss.cloud.apm.log.method.aspectj.annotation.EnableAopLogMethod;
import app.myoss.cloud.apm.spring.job.AutoRollingLogFileJob;
import app.myoss.cloud.apm.spring.job.AutoRollingLogFileProperties;

/**
 * Application performance management 自动配置
 *
 * @author Jerry.Chen
 * @since 2018年12月15日 下午10:11:48
 */
@EnableScheduling
@Configurable
public class ApmAutoConfiguration {
    /**
     * 初始化
     */
    public ApmAutoConfiguration() {
    }

    /**
     * 自动配置，每天定时检查日志文件，是否已经滚动生成新的文件，没有就触发生成新的文件
     */
    @ConditionalOnProperty(prefix = ApmConstants.AUTO_ROLLING_LOG_FILE_CONFIG_PREFIX, value = "enabled", matchIfMissing = true)
    @EnableConfigurationProperties(AutoRollingLogFileProperties.class)
    @Configuration
    public static class AutoRollingLogFileAutoConfiguration {
        /**
         * 日志文件自动滚动生成新的文件
         *
         * @param properties 属性配置
         * @return job作业
         */
        @Bean
        public AutoRollingLogFileJob autoRollingLogFile(AutoRollingLogFileProperties properties) {
            return new AutoRollingLogFileJob(properties);
        }
    }
}
