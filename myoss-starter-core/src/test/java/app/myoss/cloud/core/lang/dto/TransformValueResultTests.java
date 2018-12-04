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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.assertj.core.util.Maps;
import org.junit.Assert;
import org.junit.Test;

/**
 * {@link TransformValue} 测试类
 *
 * @author Jerry.Chen
 * @since 2018年5月14日 上午11:19:39
 */
public class TransformValueResultTests {
    @Test
    public void testConstructorIsPrivate()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<TransformValue> constructor = TransformValue.class.getDeclaredConstructor();
        Assert.assertTrue(Modifier.isPublic(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void copyErrorInfoTest1() {
        Result<String> source = new Result<>();
        source.setSuccess(false);
        source.setErrorCode("invalidValue");
        source.setErrorMsg("error code: invalidValue");

        Result<String> target = new Result<>();
        Assert.assertEquals(TransformValue.copyErrorInfo(source, target), target);
        Assert.assertEquals(TransformValue.copyErrorInfo2(source, target), target);

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
        Result<Boolean> source = new Result<>(false);
        source.setSuccess(false);
        source.setErrorCode("invalidValue");
        source.setErrorMsg("error code: invalidValue");

        Result<String> target = new Result<>("this is source value");
        Assert.assertEquals(TransformValue.copyErrorInfo(source, target), target);

        Assert.assertFalse(source.isSuccess());
        Assert.assertEquals(source.getErrorCode(), target.getErrorCode());
        Assert.assertEquals(source.getErrorMsg(), target.getErrorMsg());
        Assert.assertEquals("this is source value", target.getValue());
        Assert.assertNull(target.getExtraInfo());

        Assert.assertFalse(source.getValue());
        Assert.assertNull(source.getExtraInfo());
    }

    @Test
    public void copyErrorInfoTest3() {
        Result<String> source = new Result<>("this is source value");
        source.setSuccess(false);
        source.setErrorCode("invalidValue");
        source.setErrorMsg("error code: invalidValue");
        source.addExtraInfo("key", "value");

        Result<String> target = new Result<>();
        Assert.assertEquals(TransformValue.copyErrorInfo(source, target), target);

        Assert.assertFalse(source.isSuccess());
        Assert.assertEquals(source.getErrorCode(), target.getErrorCode());
        Assert.assertEquals(source.getErrorMsg(), target.getErrorMsg());
        Assert.assertNull(target.getValue());
        Assert.assertNull(target.getExtraInfo());
    }

    @Test
    public void copyAllInfoTest1() {
        Result<String> source = new Result<>("this is source value");
        source.setSuccess(false);
        source.setErrorCode("invalidValue");
        source.setErrorMsg("error code: invalidValue");
        source.addExtraInfo("key", "value");

        Result<String> target = new Result<>();
        Assert.assertEquals(TransformValue.copyAllInfo(source, target), target);

        Assert.assertFalse(source.isSuccess());
        Assert.assertEquals(source.getErrorCode(), target.getErrorCode());
        Assert.assertEquals(source.getErrorMsg(), target.getErrorMsg());
        Assert.assertEquals(source.getValue(), target.getValue());
        Assert.assertEquals(source.getExtraInfo(), target.getExtraInfo());
    }

    @Test
    public void setErrorInfoTest1() {
        Result<Boolean> source = new Result<>(true);
        source.setSuccess(false);
        source.setErrorCode("invalidValue");
        source.setErrorMsg("error code: invalidValue");
        source.addExtraInfo("key", "value");

        Assert.assertEquals(TransformValue.setErrorInfo(source, "blankValue", "error code: blankValue", false), source);

        Assert.assertFalse(source.isSuccess());
        Assert.assertEquals(source.getErrorCode(), "blankValue");
        Assert.assertEquals(source.getErrorMsg(), "error code: blankValue");
        Assert.assertFalse(source.getValue());
        Assert.assertEquals(source.getExtraInfo(), Maps.newHashMap("key", "value"));
    }

    @Test
    public void setErrorInfoTest2() {
        Result<Boolean> source = new Result<>(true);
        source.setSuccess(false);
        source.setErrorCode("invalidValue");
        source.setErrorMsg("error code: invalidValue");
        source.addExtraInfo("key", "value");

        Assert.assertEquals(TransformValue.setErrorInfo(source, "blankValue", "error code: blankValue"), source);

        Assert.assertFalse(source.isSuccess());
        Assert.assertEquals(source.getErrorCode(), "blankValue");
        Assert.assertEquals(source.getErrorMsg(), "error code: blankValue");
        Assert.assertTrue(source.getValue());
        Assert.assertEquals(source.getExtraInfo(), Maps.newHashMap("key", "value"));
    }

    @Test
    public void setErrorInfoTest3() {
        Result<Boolean> source = new Result<>(true);
        source.setErrorCode("invalidValue");
        source.setErrorMsg("error code: invalidValue");
        source.addExtraInfo("key", "value");

        Result<String> target = new Result<>();
        TransformValue.copyErrorInfo2(source, target);
        Assert.assertNotEquals(target, source);
        Assert.assertEquals(target.getErrorCode(), source.getErrorCode());
        Assert.assertEquals(target.getErrorMsg(), source.getErrorMsg());
        Assert.assertTrue(source.isSuccess());
        Assert.assertNotEquals(target.getExtraInfo(), source.getExtraInfo());
        Assert.assertNotEquals(target.getValue(), source.getValue());
    }
}
