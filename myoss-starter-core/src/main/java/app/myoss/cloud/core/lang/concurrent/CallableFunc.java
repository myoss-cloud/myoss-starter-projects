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

package app.myoss.cloud.core.lang.concurrent;

/**
 * 异步任务回调接口，和JDK的 {@link java.util.concurrent.Callable} 类似，但是不会强制抛出异常
 *
 * @author Jerry.Chen
 * @since 2018年5月9日 下午5:18:39
 * @see java.util.concurrent.Callable
 * @param <V> 泛型
 */
@FunctionalInterface
public interface CallableFunc<V> {
    /**
     * Computes a result.
     *
     * @return computed result
     */
    V call();
}
