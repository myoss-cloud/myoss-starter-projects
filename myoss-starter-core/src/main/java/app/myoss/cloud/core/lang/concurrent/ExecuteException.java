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
 * 多线程执行异常
 *
 * @author Jerry.Chen
 * @since 2018年6月4日 下午11:23:04
 */
public class ExecuteException extends RuntimeException {
    private static final long serialVersionUID = -9182505389714583078L;

    /**
     * 多线程执行异常
     */
    public ExecuteException() {
        super();
    }

    /**
     * 多线程执行异常
     *
     * @param message 错误信息
     */
    public ExecuteException(String message) {
        super(message);
    }

    /**
     * 多线程执行异常
     *
     * @param message 错误信息
     * @param cause 异常信息
     */
    public ExecuteException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 多线程执行异常
     *
     * @param cause 异常信息
     */
    public ExecuteException(Throwable cause) {
        super(cause);
    }

    /**
     * 多线程执行异常
     *
     * @param message 错误信息
     * @param cause 异常信息
     * @param enableSuppression whether or not suppression is enabled or
     *            disabled
     * @param writableStackTrace whether or not the stack trace should be
     *            writable
     */
    public ExecuteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
