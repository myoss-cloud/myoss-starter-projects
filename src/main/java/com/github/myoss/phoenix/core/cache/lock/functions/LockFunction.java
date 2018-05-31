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
 * 使用锁执行方法的时候，使用的回调函数
 *
 * @author Jerry.Chen
 * @since 2018年5月9日 下午4:55:33
 */
public interface LockFunction extends BaseLockFunction {
    /**
     * 获取锁成功的时候执行
     */
    void onLockSuccess();

    /**
     * 获取锁失败的时候执行
     */
    void onLockFailed();
}
