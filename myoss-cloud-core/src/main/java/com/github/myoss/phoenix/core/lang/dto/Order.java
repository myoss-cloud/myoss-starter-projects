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

package com.github.myoss.phoenix.core.lang.dto;

import java.io.Serializable;

import org.springframework.util.StringUtils;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * PropertyPath implements the pairing of an {@link Direction} and a property.
 * It is used to provide input for {@link Sort}
 *
 * @author Jerry.Chen
 * @since 2018年5月9日 下午3:21:58
 */
@Getter
@EqualsAndHashCode
public class Order implements Serializable {
    /**
     * Default direction
     */
    public static final Direction DEFAULT_DIRECTION = Direction.ASC;

    private static final long     serialVersionUID  = -8796856103332822261L;
    private final Direction       direction;
    private final String          property;

    /**
     * Creates a new {@link Order} instance. if order is {@literal null} then
     * order defaults to {@link Order#DEFAULT_DIRECTION}
     *
     * @param direction can be {@literal null}, will default to
     *            {@link Order#DEFAULT_DIRECTION}
     * @param property must not be {@literal null} or empty.
     */
    public Order(Direction direction, String property) {
        if (!StringUtils.hasText(property)) {
            throw new IllegalArgumentException("Property must not null or empty!");
        }

        this.direction = (direction != null ? direction : DEFAULT_DIRECTION);
        this.property = property;
    }
}
