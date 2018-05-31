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

package com.github.myoss.phoenix.core.spring.boot.config.http;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * OkHttp3连接池属性配置
 *
 * @author Jerry.Chen
 * @since 2018年5月21日 上午11:08:41
 */
@Data
@ConfigurationProperties(prefix = "phoenix.ok-http3.connection-pool")
public class OkHttp3ConnectionPoolProperties {
    /**
     * 启用自动配置RestTemplate，使用 OkHttp3 连接池
     */
    private boolean enabled;
    /**
     * 允许的最大空闲连接，默认为不超过10个
     */
    private int     maxIdleConnections = 10;
    /**
     * 空闲连接的存活时间（单位为：分钟），默认为5分钟
     */
    private long    keepAliveDuration  = 5;
    /**
     * 连接超时时间，单位毫秒
     */
    private Integer connectTimeout     = 5000;
    /**
     * 数据读取超时时间，单位毫秒。如果请求连接成功之后，多少时间内无法返回数据，就直接放弃此次调用
     */
    private Integer readTimeout        = 5000;
    /**
     * 写数据超时时间，单位毫秒
     */
    private Integer writeTimeout       = 3000;
}
