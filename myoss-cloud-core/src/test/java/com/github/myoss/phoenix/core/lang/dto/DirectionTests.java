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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.boot.test.rule.OutputCapture;

/**
 * {@link Direction} 测试类
 *
 * @author Jerry.Chen
 * @since 2018年5月14日 上午11:13:13
 */
public class DirectionTests {
    @Rule
    public ExpectedException exception = ExpectedException.none();
    @Rule
    public OutputCapture     output    = new OutputCapture();

    @Test
    public void checkValueTest() {
        Assert.assertEquals("ASC", Direction.ASC.name());
        Assert.assertEquals("DESC", Direction.DESC.name());
    }

    @Test
    public void fromStringTest1() {
        Direction asc = Direction.fromString("ASC");
        Assert.assertEquals(Direction.ASC, asc);
        Direction desc = Direction.fromString("DESC");
        Assert.assertEquals(Direction.DESC, desc);
    }

    @Test
    public void fromStringTest2() {
        Direction asc = Direction.fromString("asc");
        Assert.assertEquals(Direction.ASC, asc);
        Direction desc = Direction.fromString("desc");
        Assert.assertEquals(Direction.DESC, desc);
    }

    @Test
    public void fromStringIllegalArgumentExceptionTest1() {
        exception.expect(IllegalArgumentException.class);
        Direction.fromString(null);
    }

    @Test
    public void fromStringIllegalArgumentExceptionTest2() {
        exception.expect(IllegalArgumentException.class);
        Direction.fromString("");
    }

    @Test
    public void fromStringIllegalArgumentExceptionTest3() {
        exception.expect(IllegalArgumentException.class);
        Direction.fromString("error");
    }

    @Test
    public void fromStringOrNullTest1() {
        Direction asc = Direction.fromStringOrNull("ASC");
        Assert.assertEquals(Direction.ASC, asc);
        Direction desc = Direction.fromStringOrNull("DESC");
        Assert.assertEquals(Direction.DESC, desc);
    }

    @Test
    public void fromStringOrNullTest2() {
        Direction asc = Direction.fromStringOrNull("asc");
        Assert.assertEquals(Direction.ASC, asc);
        Direction desc = Direction.fromStringOrNull("desc");
        Assert.assertEquals(Direction.DESC, desc);
        Direction nullDirection = Direction.fromStringOrNull(null);
        Assert.assertNull(nullDirection);
    }

    @Test
    public void fromStringOrNullIllegalArgumentExceptionTest1() {
        Direction direction = Direction.fromStringOrNull("");
        Assert.assertNull(direction);
        String output = this.output.toString();
        assertThat(output).contains("IllegalArgumentException").contains(
                "java.lang.IllegalArgumentException: Invalid value '' for orders given! Has to be either 'desc' or 'asc' (case insensitive).");
    }

    @Test
    public void fromStringOrNullIllegalArgumentExceptionTest2() {
        Direction direction = Direction.fromStringOrNull("error");
        Assert.assertNull(direction);
        String output = this.output.toString();
        assertThat(output).contains("IllegalArgumentException").contains(
                "java.lang.IllegalArgumentException: Invalid value 'error' for orders given! Has to be either 'desc' or 'asc' (case insensitive).");
    }
}
