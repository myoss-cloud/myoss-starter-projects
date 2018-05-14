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

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.assertj.core.data.Index;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.boot.test.rule.OutputCapture;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;

/**
 * {@link Sort} 测试类
 *
 * @author Jerry.Chen 2018年5月14日 上午11:14:43
 */
public class SortTests {
    @Rule
    public ExpectedException exception = ExpectedException.none();
    @Rule
    public OutputCapture     output    = new OutputCapture();

    @Test
    public void checkValueTest1() {
        Order order = new Order(Direction.ASC, "id");
        Sort sort = new Sort(order);
        Assertions.assertThat(sort.getOrders()).hasSize(1).contains(order);
        Assert.assertEquals(JSON.toJSONString(sort), sort.toString());
    }

    @Test
    public void checkValueTest2() {
        Order id = new Order(Direction.ASC, "id");
        Order name = new Order(Direction.DESC, "name");
        Sort sort = new Sort(id, name);
        Assertions.assertThat(sort.getOrders()).hasSize(2).contains(id, name);
        Assert.assertEquals(JSON.toJSONString(sort), sort.toString());
    }

    @Test
    public void checkValueTest3() {
        Order id = new Order(Direction.ASC, "id");
        Order name = new Order(Direction.DESC, "name");
        List<Order> orders = Lists.newArrayList(id, name);
        Sort sort = new Sort(orders);
        Assertions.assertThat(sort.getOrders()).hasSize(2).contains(id, name);
        Assert.assertEquals(JSON.toJSONString(sort), sort.toString());
    }

    @Test
    public void checkValueTest4() {
        Order id = new Order(Direction.ASC, "id");
        Order name = new Order(Direction.ASC, "name");
        Sort sort = new Sort("id", "name");
        Assertions.assertThat(sort.getOrders()).hasSize(2).contains(id, name);
        Assert.assertEquals(JSON.toJSONString(sort), sort.toString());
    }

    @Test
    public void checkValueTest5() {
        Order id = new Order(Direction.DESC, "id");
        Order name = new Order(Direction.DESC, "name");
        Sort sort = new Sort(Direction.DESC, id.getProperty(), name.getProperty());
        Assertions.assertThat(sort.getOrders()).hasSize(2).contains(id, name);
        Assert.assertEquals(JSON.toJSONString(sort), sort.toString());
    }

    @Test
    public void checkValueTest6() {
        Order id = new Order(Direction.ASC, "id");
        Order name = new Order(Direction.ASC, "name");
        List<String> properties = Lists.newArrayList(id.getProperty(), name.getProperty());
        Sort sort = new Sort(Direction.ASC, properties);
        Assertions.assertThat(sort.getOrders()).hasSize(2).contains(id, name);
        Assert.assertEquals(JSON.toJSONString(sort), sort.toString());
    }

    @Test
    public void nullOrderValueTest1() {
        Order order = null;
        Sort sort = new Sort(order);
        Assertions.assertThat(sort.getOrders()).hasSize(1).contains(null, Index.atIndex(0));
    }

    @Test
    public void nullOrderValueTest2() {
        Order id = new Order(Direction.ASC, "id");
        Order name = null;
        Sort sort = new Sort(id, name);
        Assertions.assertThat(sort.getOrders()).hasSize(2).contains(id, Index.atIndex(0)).containsNull();
    }

    @Test
    public void emptyOrderIllegalArgumentExceptionTest1() {
        List<Order> orders = Lists.newArrayList();
        exception.expect(IllegalArgumentException.class);
        new Sort(orders);
    }

    @Test
    public void emptyOrderIllegalArgumentExceptionTest2() {
        List<Order> orders = null;
        exception.expect(IllegalArgumentException.class);
        new Sort(orders);
    }

    @Test
    public void nullPropertyIllegalArgumentExceptionTest1() {
        exception.expect(IllegalArgumentException.class);
        new Sort("id", null);
    }

    @Test
    public void nullPropertyIllegalArgumentExceptionTest2() {
        exception.expect(IllegalArgumentException.class);
        new Sort(Direction.DESC, "id", null);
    }

    @Test
    public void nullPropertyIllegalArgumentExceptionTest3() {
        exception.expect(IllegalArgumentException.class);
        List<String> properties = Lists.newArrayList("id", null);
        new Sort(Direction.ASC, properties);
    }

    @Test
    public void andTest1() {
        Sort sort = new Sort("id", "name");
        Sort id = new Sort(new Order(Direction.ASC, "id"));
        Sort and = id.and(sort);

        ArrayList<Order> orders = Lists.newArrayList(sort.getOrders());
        orders.addAll(id.getOrders());
        Assertions.assertThat(and.getOrders()).hasSize(3).containsAll(orders);
    }

    @Test
    public void andTest2() {
        Sort id = new Sort(new Order(Direction.ASC, "id"));
        Sort and = id.and(null);
        Assert.assertEquals(id, and);
    }

    @Test
    public void orderForTest1() {
        Order id = new Order(Direction.ASC, "id");
        Order name = new Order(Direction.DESC, "name");
        List<Order> orders = Lists.newArrayList(id, name);
        Sort sort = new Sort(orders);
        Order getId = sort.getOrderFor("id");
        Order getName = sort.getOrderFor("name");
        Order getOther = sort.getOrderFor("other");
        Assert.assertEquals(id, getId);
        Assert.assertEquals(name, getName);
        Assert.assertNull(getOther);
    }

    @Test
    public void iteratorTest1() {
        Order id = new Order(Direction.ASC, "id");
        Order name = new Order(Direction.DESC, "name");
        List<Order> orders = Lists.newArrayList(id, name);
        Sort sort = new Sort(orders);
        for (Order order : sort) {
            Assert.assertNotNull(order);
        }
    }

    /**
     * sort字段为null的反序列化测试
     */
    @Test
    public void sortDeserializeTest1() {
        Page<Integer> page = new Page<>(123);
        String json = JSON.toJSONString(page, SerializerFeature.WriteMapNullValue);
        Assert.assertEquals(
                "{\"errorCode\":null,\"errorMsg\":null,\"extraInfo\":null,\"pageNum\":1,\"pageSize\":20,\"param\":123,\"sort\":null,\"success\":true,\"totalCount\":0,\"value\":null}",
                json);

        Page<Integer> actual = JSON.parseObject(json, new TypeReference<Page<Integer>>() {
        });
        Assert.assertEquals(page, actual);
    }

    @Test
    public void sortDeserializeTest2() {
        Order order = new Order(Direction.ASC, "id");
        Sort sort = new Sort(order);
        String json = JSON.toJSONString(sort);

        Sort actual = JSON.parseObject(json, Sort.class);
        Assert.assertEquals(sort, actual);
    }
}
