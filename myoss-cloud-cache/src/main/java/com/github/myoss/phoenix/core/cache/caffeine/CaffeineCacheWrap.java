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

package com.github.myoss.phoenix.core.cache.caffeine;

import org.springframework.cache.support.NullValue;

/**
 * Caffeine Cache包装类, Caffeine Cache不允许保存null值，会将 {@code null} 值转换为
 * {@link NullValue#INSTANCE} 放进缓存中，这样后续的操作就不会进入到缓存中
 *
 * @author Jerry.Chen
 * @since 2018年5月23日 上午1:06:17
 */
public class CaffeineCacheWrap extends org.springframework.cache.caffeine.CaffeineCache {
    /**
     * Create a {@link com.github.benmanes.caffeine.cache.Cache} instance with
     * the specified name and the given internal
     * {@link com.github.benmanes.caffeine.cache.Cache} to use.
     *
     * @param name the name of the cache
     * @param cache the backing Caffeine Cache instance
     */
    public CaffeineCacheWrap(String name, com.github.benmanes.caffeine.cache.Cache<Object, Object> cache) {
        super(name, cache);
    }

    /**
     * Create a {@link com.github.benmanes.caffeine.cache.Cache} instance with
     * the specified name and the given internal
     * {@link com.github.benmanes.caffeine.cache.Cache} to use.
     *
     * @param name the name of the cache
     * @param cache the backing Caffeine Cache instance
     * @param allowNullValues whether to accept and convert {@code null} values
     *            for this cache
     */
    public CaffeineCacheWrap(String name, com.github.benmanes.caffeine.cache.Cache<Object, Object> cache,
                             boolean allowNullValues) {
        super(name, cache, allowNullValues);
    }

    @Override
    public void put(Object key, Object value) {
        if (value == null) {
            if (isAllowNullValues()) {
                super.put(key, null);
            }
        } else {
            super.put(key, value);
        }
    }
}
