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

package app.myoss.cloud.core.lang.dto;

import java.util.Locale;

import lombok.extern.slf4j.Slf4j;

/**
 * 排序枚举类
 *
 * @author Jerry.Chen
 * @since 2018年5月9日 下午3:22:33
 */
@Slf4j
public enum Direction {
    /**
     * 升序
     */
    ASC,
    /**
     * 降序
     */
    DESC;

    /**
     * Returns the {@link Direction} enum for the given {@link String} value.
     *
     * @param value given value
     * @return Returns the {@link Direction} enum
     * @throws IllegalArgumentException in case the given value cannot be parsed
     *             into an enum value.
     */
    public static Direction fromString(String value) {
        try {
            return Direction.valueOf(value.toUpperCase(Locale.US));
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format(
                    "Invalid value '%s' for orders given! Has to be either 'desc' or 'asc' (case insensitive).", value),
                    e);
        }
    }

    /**
     * Returns the {@link Direction} enum for the given {@link String} or null
     * if it cannot be parsed into an enum value.
     *
     * @param value given value
     * @return Returns the {@link Direction} enum
     */
    public static Direction fromStringOrNull(String value) {
        try {
            return (value != null ? fromString(value) : null);
        } catch (IllegalArgumentException e) {
            log.warn("IllegalArgumentException", e);
            return null;
        }
    }
}
