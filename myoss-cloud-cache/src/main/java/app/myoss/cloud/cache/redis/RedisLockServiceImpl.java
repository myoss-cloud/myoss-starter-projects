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

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;

import app.myoss.cloud.cache.lock.LockService;
import app.myoss.cloud.cache.lock.functions.LockFunction;
import app.myoss.cloud.cache.lock.functions.LockFunctionGeneric;
import app.myoss.cloud.cache.lock.functions.LockFunctionGenericWithArgs;
import app.myoss.cloud.cache.lock.functions.LockFunctionWithArgs;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Redis 缓存锁实现
 *
 * @author Jerry.Chen
 * @since 2018年5月21日 下午1:13:16
 */
@Data
@AllArgsConstructor
public class RedisLockServiceImpl implements LockService {
    /**
     * Redis data access Template
     */
    private RedisTemplate<Object, Object> redisTemplate;
    /**
     * 缓存锁的时间单位
     */
    private TimeUnit                      timeUnit;

    @Override
    public boolean getLock(Serializable key, int expireTime) {
        Long value = redisTemplate.opsForValue().increment(key, 1);
        if (!Objects.equals(value, 1L)) {
            return false;
        }
        Boolean expire = redisTemplate.expire(key, expireTime, timeUnit);
        return Objects.equals(expire, true);
    }

    @Override
    public boolean releaseLock(Serializable key) {
        Boolean delete = redisTemplate.delete(key);
        return Objects.equals(delete, true);
    }

    @Override
    public boolean executeByLock(Serializable key, int expireTime, LockFunction callback) {
        boolean isGetLock = getLock(key, expireTime);
        try {
            if (isGetLock) {
                callback.onLockSuccess();
            } else {
                int tryLockTimes = callback.tryLockTimes();
                if (tryLockTimes <= 1) {
                    callback.onLockFailed();
                } else {
                    // 进行多次重试获取锁
                    for (int i = 1; i < tryLockTimes; i++) {
                        LockService.sleep(callback.tryLockSleepTime());
                        isGetLock = getLock(key, expireTime);
                        if (isGetLock) {
                            break;
                        }
                    }
                    if (isGetLock) {
                        callback.onLockSuccess();
                    } else {
                        callback.onLockFailed();
                    }
                }
            }
        } finally {
            // 如果已经获取到锁，才释放锁
            if (isGetLock) {
                releaseLock(key);
            }
        }
        return isGetLock;
    }

    @Override
    public boolean executeByLock(Serializable key, int expireTime, LockFunctionWithArgs callback, Object... args) {
        boolean isGetLock = getLock(key, expireTime);
        try {
            if (isGetLock) {
                callback.onLockSuccess(args);
            } else {
                int tryLockTimes = callback.tryLockTimes();
                if (tryLockTimes <= 1) {
                    callback.onLockFailed(args);
                } else {
                    // 进行多次重试获取锁
                    for (int i = 1; i < tryLockTimes; i++) {
                        LockService.sleep(callback.tryLockSleepTime());
                        isGetLock = getLock(key, expireTime);
                        if (isGetLock) {
                            break;
                        }
                    }
                    if (isGetLock) {
                        callback.onLockSuccess(args);
                    } else {
                        callback.onLockFailed(args);
                    }
                }
            }
        } finally {
            // 如果已经获取到锁，才释放锁
            if (isGetLock) {
                releaseLock(key);
            }
        }
        return isGetLock;
    }

    @Override
    public <T> T executeByLock(Serializable key, int expireTime, LockFunctionGeneric<T> callback) {
        boolean isGetLock = getLock(key, expireTime);
        try {
            if (isGetLock) {
                return callback.onLockSuccess();
            } else {
                int tryLockTimes = callback.tryLockTimes();
                if (tryLockTimes <= 1) {
                    return callback.onLockFailed();
                } else {
                    // 进行多次重试获取锁
                    for (int i = 1; i < tryLockTimes; i++) {
                        LockService.sleep(callback.tryLockSleepTime());
                        isGetLock = getLock(key, expireTime);
                        if (isGetLock) {
                            break;
                        }
                    }
                    if (isGetLock) {
                        return callback.onLockSuccess();
                    } else {
                        return callback.onLockFailed();
                    }
                }
            }
        } finally {
            // 如果已经获取到锁，才释放锁
            if (isGetLock) {
                releaseLock(key);
            }
        }
    }

    @Override
    public <T> T executeByLock(Serializable key, int expireTime, LockFunctionGenericWithArgs<T> callback,
                               Object... args) {
        boolean isGetLock = getLock(key, expireTime);
        try {
            if (isGetLock) {
                return callback.onLockSuccess(args);
            } else {
                int tryLockTimes = callback.tryLockTimes();
                if (tryLockTimes <= 1) {
                    return callback.onLockFailed(args);
                } else {
                    // 进行多次重试获取锁
                    for (int i = 1; i < tryLockTimes; i++) {
                        LockService.sleep(callback.tryLockSleepTime());
                        isGetLock = getLock(key, expireTime);
                        if (isGetLock) {
                            break;
                        }
                    }
                    if (isGetLock) {
                        return callback.onLockSuccess(args);
                    } else {
                        return callback.onLockFailed(args);
                    }
                }
            }
        } finally {
            // 如果已经获取到锁，才释放锁
            if (isGetLock) {
                releaseLock(key);
            }
        }
    }
}
