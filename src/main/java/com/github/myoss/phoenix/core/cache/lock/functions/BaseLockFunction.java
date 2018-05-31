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

package com.github.myoss.phoenix.core.cache.lock.functions;

/**
 * 使用锁执行方法的时候，使用的回调函数基类
 *
 * @author Jerry.Chen
 * @since 2018年5月9日 下午5:04:21
 */
public interface BaseLockFunction {
    /**
     * 尝试获取锁的次数，默认一次
     *
     * @return 尝试获取锁的次数
     */
    default int tryLockTimes() {
        return 1;
    }

    /**
     * 重复尝试获取锁的时间，休眠时间，默认10ms
     *
     * @return 重复尝试获取锁的时间
     */
    default long tryLockSleepTime() {
        return 10L;
    }
}
