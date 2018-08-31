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

package app.myoss.cloud.cache.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Redis 缓存的配置
 *
 * @author Jerry.Chen
 * @since 2018年5月21日 下午1:41:21
 */
@Data
@ConfigurationProperties(prefix = "myoss-cloud.cache.redis")
public class RedisProperties {
    /**
     * 缓存锁的时间单位，默认为: 秒
     */
    private TimeUnit lockTimeUnit = TimeUnit.SECONDS;
}
