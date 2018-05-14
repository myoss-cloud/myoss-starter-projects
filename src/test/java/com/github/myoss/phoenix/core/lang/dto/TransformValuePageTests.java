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

import org.assertj.core.util.Lists;
import org.assertj.core.util.Maps;
import org.junit.Assert;
import org.junit.Test;

/**
 * {@link TransformValue} 测试类
 *
 * @author Jerry.Chen 2018年5月14日 上午11:19:39
 */
public class TransformValuePageTests {
    @Test
    public void copyErrorInfoTest1() {
        Page<String> source = new Page<>();
        source.setSuccess(false);
        source.setErrorCode("invalidValue");
        source.setErrorMsg("error code: invalidValue");

        Page<String> target = new Page<>();
        Assert.assertEquals(TransformValue.copyErrorInfo(source, target), target);

        Assert.assertFalse(source.isSuccess());
        Assert.assertEquals(source.getErrorCode(), target.getErrorCode());
        Assert.assertEquals(source.getErrorMsg(), target.getErrorMsg());
        Assert.assertNull(target.getValue());
        Assert.assertNull(target.getExtraInfo());

        Assert.assertNull(source.getValue());
        Assert.assertNull(source.getExtraInfo());
    }

    @Test
    public void copyErrorInfoTest2() {
        Page<Boolean> source = new Page<>(false);
        source.setSuccess(false);
        source.setErrorCode("invalidValue");
        source.setErrorMsg("error code: invalidValue");

        Page<String> target = new Page<>("this is source value");
        Assert.assertEquals(TransformValue.copyErrorInfo(source, target), target);

        Assert.assertFalse(source.isSuccess());
        Assert.assertEquals(source.getErrorCode(), target.getErrorCode());
        Assert.assertEquals(source.getErrorMsg(), target.getErrorMsg());
        Assert.assertEquals("this is source value", target.getParam());
        Assert.assertNull(target.getExtraInfo());

        Assert.assertFalse(source.getParam());
        Assert.assertNull(source.getExtraInfo());
    }

    @Test
    public void copyErrorInfoTest3() {
        Page<String> source = new Page<>("this is source value");
        source.setSuccess(false);
        source.setErrorCode("invalidValue");
        source.setErrorMsg("error code: invalidValue");
        source.addExtraInfo("key", "value");

        Page<String> target = new Page<>();
        Assert.assertEquals(TransformValue.copyErrorInfo(source, target), target);

        Assert.assertFalse(source.isSuccess());
        Assert.assertEquals(source.getErrorCode(), target.getErrorCode());
        Assert.assertEquals(source.getErrorMsg(), target.getErrorMsg());
        Assert.assertNull(target.getValue());
        Assert.assertNull(target.getExtraInfo());
    }

    @Test
    public void copyAllInfoTest1() {
        Page<String> source = new Page<>("this is source value");
        source.setSuccess(false);
        source.setErrorCode("invalidValue");
        source.setErrorMsg("error code: invalidValue");
        source.addExtraInfo("key", "value");

        Page<String> target = new Page<>();
        Assert.assertEquals(TransformValue.copyAllInfo(source, target), target);

        Assert.assertFalse(source.isSuccess());
        Assert.assertEquals(source.getErrorCode(), target.getErrorCode());
        Assert.assertEquals(source.getErrorMsg(), target.getErrorMsg());
        Assert.assertEquals(source.getValue(), target.getValue());
        Assert.assertEquals(source.getExtraInfo(), target.getExtraInfo());
    }

    @Test
    public void setErrorInfoTest1() {
        Page<Boolean> source = new Page<>(true);
        source.setSuccess(false);
        source.setErrorCode("invalidValue");
        source.setErrorMsg("error code: invalidValue");
        source.addExtraInfo("key", "value");

        Assert.assertEquals(
                TransformValue.setErrorInfo(source, "blankValue", "error code: blankValue", Lists.newArrayList(false)),
                source);

        Assert.assertFalse(source.isSuccess());
        Assert.assertEquals(source.getErrorCode(), "blankValue");
        Assert.assertEquals(source.getErrorMsg(), "error code: blankValue");
        Assert.assertFalse(source.getValue().get(0));
        Assert.assertEquals(source.getExtraInfo(), Maps.newHashMap("key", "value"));
    }

    @Test
    public void setErrorInfoTest2() {
        Page<Boolean> source = new Page<>(true);
        source.setSuccess(false);
        source.setErrorCode("invalidValue");
        source.setErrorMsg("error code: invalidValue");
        source.addExtraInfo("key", "value");

        Assert.assertEquals(TransformValue.setErrorInfo(source, "blankValue", "error code: blankValue"), source);

        Assert.assertFalse(source.isSuccess());
        Assert.assertEquals(source.getErrorCode(), "blankValue");
        Assert.assertEquals(source.getErrorMsg(), "error code: blankValue");
        Assert.assertTrue(source.getValue().get(0));
        Assert.assertEquals(source.getExtraInfo(), Maps.newHashMap("key", "value"));
    }
}
