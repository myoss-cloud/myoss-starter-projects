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

package app.myoss.cloud.cache.lock;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import app.myoss.cloud.cache.lock.functions.LockFunction;
import app.myoss.cloud.cache.lock.functions.LockFunctionGeneric;
import app.myoss.cloud.cache.lock.functions.LockFunctionGenericWithArgs;
import app.myoss.cloud.cache.lock.functions.LockFunctionWithArgs;

/**
 * 缓存锁服务接口
 *
 * @author Jerry.Chen
 * @since 2018年5月9日 下午5:01:14
 */
public interface LockService {
    /**
     * 模拟{@link Thread#sleep(long)}，规避 SonarQube 会扫描 Thread.sleep 代码。
     * <p>
     * Using Thread.sleep in a test is just generally a bad idea. It creates
     * brittle tests that can fail unpredictably depending on environment
     * ("Passes on my machine!") or load.
     * <p>
     * 也可以使用 {@link TimeUnit#sleep(long)} 去替代 Thread.sleep 代码
     *
     * @param milliseconds 等待的毫秒时间
     */
    static void sleep(long milliseconds) {
        long begin = System.currentTimeMillis();
        long cost = 0;
        while (cost <= milliseconds) {
            cost = System.currentTimeMillis() - begin;
        }
    }

    /**
     * 获取锁
     *
     * @param key 锁的名字
     * @param expireTime 锁的过期时间
     * @return true：获取到锁，false：没有获取到锁
     */
    boolean getLock(Serializable key, int expireTime);

    /**
     * 释放锁
     *
     * @param key 锁的名字
     * @return true：释放锁成功，false：释放锁失败
     */
    boolean releaseLock(Serializable key);

    /**
     * 如果获取锁成功，则执行 {@link LockFunction#onLockSuccess() callback.onLockSuccess()}
     * 如果获取锁失败，则执行 {@link LockFunction#onLockFailed() callback.onLockFailed()}
     *
     * @param key 锁的名字
     * @param expireTime 锁的过期时间
     * @param callback 回调函数
     * @return true：获取到锁，false：没有获取到锁
     */
    boolean executeByLock(Serializable key, int expireTime, LockFunction callback);

    /**
     * 如果获取锁成功，则执行 {@link LockFunctionWithArgs#onLockSuccess(Object...)
     * callback.onLockSuccess()} 如果获取锁失败，则执行
     * {@link LockFunctionWithArgs#onLockFailed(Object...)
     * callback.onLockFailed()}
     *
     * @param key 锁的名字
     * @param expireTime 锁的过期时间
     * @param callback 回调函数
     * @param args 回调函数的入参
     * @return true：获取到锁，false：没有获取到锁
     */
    boolean executeByLock(Serializable key, int expireTime, LockFunctionWithArgs callback, Object... args);

    /**
     * 如果获取锁成功，则执行 {@link LockFunctionGeneric#onLockSuccess()
     * callback.onLockSuccess()} 如果获取锁失败，则执行
     * {@link LockFunctionGeneric#onLockFailed() callback.onLockFailed()}
     *
     * @param key 锁的名字
     * @param expireTime 锁的过期时间
     * @param callback 回调函数
     * @param <T> 回调函数返回的泛型
     * @return 回调函数返回的结果
     */
    <T> T executeByLock(Serializable key, int expireTime, LockFunctionGeneric<T> callback);

    /**
     * 如果获取锁成功，则执行 {@link LockFunctionGenericWithArgs#onLockSuccess(Object...)
     * callback.onLockSuccess()} 如果获取锁失败，则执行
     * {@link LockFunctionGenericWithArgs#onLockFailed(Object...)
     * callback.onLockFailed()}
     *
     * @param key 锁的名字
     * @param expireTime 锁的过期时间
     * @param callback 回调函数
     * @param args 回调函数的入参
     * @param <T> 回调函数返回的泛型
     * @return 回调函数返回的结果
     */
    <T> T executeByLock(Serializable key, int expireTime, LockFunctionGenericWithArgs<T> callback, Object... args);
}
