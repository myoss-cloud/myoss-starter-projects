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

package com.github.myoss.phoenix.core.cache.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Redis 缓存锁服务自动配置
 *
 * @author Jerry.Chen
 * @since 2018年5月21日 下午1:33:14
 */
@Import(RedisAutoConfiguration.class)
@EnableConfigurationProperties(RedisProperties.class)
@ConditionalOnClass(RedisOperations.class)
@Configuration
public class RedisLockServiceAutoConfiguration {
    private RedisProperties redisProperties;

    /**
     * 初始化 Redis 缓存锁服务自动配置
     *
     * @param redisProperties Redis 缓存的配置
     */
    public RedisLockServiceAutoConfiguration(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    /**
     * 初始化默认的Redis 缓存锁实现
     *
     * @param redisTemplate Spring RedisTemplate
     * @return 默认的Redis 缓存锁实现
     */
    @ConditionalOnBean(name = "redisTemplate")
    @ConditionalOnMissingBean
    @Bean
    public RedisLockServiceImpl redisLockService(RedisTemplate<Object, Object> redisTemplate) {
        TimeUnit timeUnit = redisProperties.getLockTimeUnit();
        return new RedisLockServiceImpl(redisTemplate, timeUnit);
    }
}
