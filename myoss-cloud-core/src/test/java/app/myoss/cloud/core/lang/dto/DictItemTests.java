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

import org.junit.Assert;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

/**
 * {@link DictItem} 测试类
 *
 * @author Jerry.Chen
 * @since 2018年5月14日 上午11:12:18
 */
public class DictItemTests {
    @Test
    public void test1() {
        DictItem<?> dictItem = new DictItem<>();
        Assert.assertEquals("{}", dictItem.toString());
    }

    @Test
    public void test2() {
        DictItem<Integer> dictItem = new DictItem<>();
        dictItem.setValue(1);
        dictItem.setCode("001");
        dictItem.setName("代码1");
        dictItem.addExtraInfo("key1", "value1");
        dictItem.addExtraInfo("key2", 123);

        assertThat(dictItem.getValue()).isEqualTo(1);
        Assert.assertEquals(dictItem.getCode(), "001");
        Assert.assertEquals(dictItem.getName(), "代码1");
        Assert.assertEquals(dictItem.getExtraInfo("key1"), "value1");
        Assert.assertEquals(dictItem.getExtraInfo("key2"), 123);

        String actual = dictItem.toString();
        Assert.assertEquals(
                "{\"code\":\"001\",\"extraInfo\":{\"key1\":\"value1\",\"key2\":123},\"name\":\"代码1\",\"value\":1}",
                actual);

        DictItem<Integer> dictItemNew = JSON.parseObject(actual, new TypeReference<DictItem<Integer>>() {
        });
        Assert.assertEquals(dictItem, dictItemNew);
    }

    @Test
    public void test3() {
        DictItem<String> dictItem = new DictItem<>();
        dictItem.setValue("001");
        dictItem.setCode("001");
        dictItem.setName("代码1");
        Assert.assertEquals("{\"code\":\"001\",\"name\":\"代码1\",\"value\":\"001\"}", dictItem.toString());
    }

    @Test
    public void test4() {
        DictItem<String> dictItem = new DictItem<>();
        dictItem.setValue("001");
        Assert.assertEquals("{\"value\":\"001\"}", dictItem.toString());

        dictItem = new DictItem<>();
        dictItem.setValue("001");
        dictItem.setCode("001");
        Assert.assertEquals("{\"code\":\"001\",\"value\":\"001\"}", dictItem.toString());

        dictItem = new DictItem<>();
        dictItem.setCode("001");
        Assert.assertEquals("{\"code\":\"001\"}", dictItem.toString());

        dictItem = new DictItem<>();
        dictItem.setName("代码1");
        Assert.assertEquals("{\"name\":\"代码1\"}", dictItem.toString());
    }
}
