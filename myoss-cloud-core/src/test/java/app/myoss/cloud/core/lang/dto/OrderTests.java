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

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Condition;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * {@link Order} 测试类
 *
 * @author Jerry.Chen
 * @since 2018年5月14日 上午11:13:57
 */
public class OrderTests {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void checkValueTest() {
        Order id = new Order(Direction.ASC, "id");
        assertThat(id).is(new Condition<Order>() {
            @Override
            public boolean matches(Order value) {
                return Direction.ASC.equals(value.getDirection()) && "id".equals(value.getProperty());
            }
        });
        Order name = new Order(Direction.DESC, "name");
        assertThat(name).is(new Condition<Order>() {
            @Override
            public boolean matches(Order value) {
                return Direction.DESC.equals(value.getDirection()) && "name".equals(value.getProperty());
            }
        });
        Order age = new Order(null, "age");
        assertThat(age).is(new Condition<Order>() {
            @Override
            public boolean matches(Order value) {
                return Direction.ASC.equals(value.getDirection()) && "age".equals(value.getProperty());
            }
        });
    }

    @Test
    public void nullPropertyTest1() {
        exception.expect(IllegalArgumentException.class);
        new Order(Direction.ASC, null);
    }

    @Test
    public void whitespacePropertyTest1() {
        exception.expect(IllegalArgumentException.class);
        new Order(Direction.ASC, "");
    }

    @Test
    public void whitespacePropertyTest2() {
        exception.expect(IllegalArgumentException.class);
        new Order(Direction.ASC, " ");
    }
}
