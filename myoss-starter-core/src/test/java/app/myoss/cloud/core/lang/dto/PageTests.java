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

import static org.assertj.core.api.Java6Assertions.assertThat;

import java.util.Map;

import org.assertj.core.util.Maps;
import org.junit.Assert;
import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;

/**
 * {@link Page} 测试类
 *
 * @author Jerry.Chen
 * @since 2018年5月14日 上午11:14:43
 */
public class PageTests {
    @Test
    public void test1() {
        Page<Long> result = new Page<>();
        result.setParam(1000L);
        result.setValue(Lists.newArrayList(12345L, 456789L));
        result.setErrorMsg("it's ok");
        result.setErrorCode("NONE");
        String json = JSON.toJSONString(result);
        Assert.assertEquals(
                "{\"errorCode\":\"NONE\",\"errorMsg\":\"it's ok\",\"pageNum\":1,\"pageSize\":20,\"param\":1000,\"success\":true,\"totalCount\":0,\"value\":[12345,456789]}",
                json);
    }

    @Test
    public void test2() {
        Page<Long> result = new Page<>();
        result.setParam(1000L);
        result.setSort(new Sort(Direction.DESC, "userName", "userAge"));
        result.setValue(Lists.newArrayList(12345L, 456789L));
        result.setErrorMsg("it's ok");
        result.setErrorCode("NONE");
        String json = JSON.toJSONString(result);
        String expected = "{\"errorCode\":\"NONE\",\"errorMsg\":\"it's ok\",\"pageNum\":1,\"pageSize\":20,\"param\":1000,\"sort\":[{\"direction\":\"DESC\",\"property\":\"userName\"},{\"direction\":\"DESC\",\"property\":\"userAge\"}],\"success\":true,\"totalCount\":0,\"value\":[12345,456789]}";
        Assert.assertEquals(expected, json);

        Page<Long> page = JSON.parseObject(expected, new TypeReference<Page<Long>>() {
        });
        Assert.assertEquals(result, page);
        Assert.assertNull(result.getExtraInfo("name"));
    }

    @Test
    public void constructorTest1() {
        Page<Long> result = new Page<>();
        result.setPageSize(150);
        result.setPageNum(109);
        result.setParam(1000L);
        result.setTotalCount(399);
        result.setSort(new Sort(Direction.DESC, "userName", "userAge"));
        result.setValue(Lists.newArrayList(100L, 1001L));
        result.setSuccess(false);
        result.setErrorMsg("it's ok, ba la ba la...");
        result.setErrorCode("01");
        Map<String, Object> extraInfo = Maps.newHashMap("name", (Object) "HanMeiMei");
        extraInfo.put("key", new Sort(Direction.DESC, "userName", "userAge"));
        result.setExtraInfo(extraInfo);
        String toString = result.toString();
        String expected = "{\"errorCode\":\"01\",\"errorMsg\":\"it's ok, ba la ba la...\",\"extraInfo\":{\"name\":\"HanMeiMei\",\"key\":[{\"direction\":\"DESC\",\"property\":\"userName\"},{\"direction\":\"DESC\",\"property\":\"userAge\"}]},\"pageNum\":109,\"pageSize\":150,\"param\":1000,\"sort\":[{\"direction\":\"DESC\",\"property\":\"userName\"},{\"direction\":\"DESC\",\"property\":\"userAge\"}],\"success\":false,\"totalCount\":399,\"value\":[100,1001]}";
        assertThat(result.getParam()).isEqualTo(1000L);
        Assert.assertEquals(expected, toString);
    }

    @Test
    public void constructorTest2() {
        Page<Long> result = new Page<>(1000L);
        result.setPageSize(150);
        result.setPageNum(109);
        result.setTotalCount(399);
        result.setSort(new Sort(Direction.DESC, "userName", "userAge"));
        result.setValue(Lists.newArrayList(100L, 1001L));
        result.setSuccess(false);
        result.setErrorMsg("it's ok, ba la ba la...");
        result.setErrorCode("01");
        Map<String, Object> extraInfo = Maps.newHashMap("name", (Object) "HanMeiMei");
        extraInfo.put("key", new Sort(Direction.DESC, "userName", "userAge"));
        result.setExtraInfo(extraInfo);
        String toString = result.toString();
        String expected = "{\"errorCode\":\"01\",\"errorMsg\":\"it's ok, ba la ba la...\",\"extraInfo\":{\"name\":\"HanMeiMei\",\"key\":[{\"direction\":\"DESC\",\"property\":\"userName\"},{\"direction\":\"DESC\",\"property\":\"userAge\"}]},\"pageNum\":109,\"pageSize\":150,\"param\":1000,\"sort\":[{\"direction\":\"DESC\",\"property\":\"userName\"},{\"direction\":\"DESC\",\"property\":\"userAge\"}],\"success\":false,\"totalCount\":399,\"value\":[100,1001]}";
        assertThat(result.getParam()).isEqualTo(1000L);
        Assert.assertEquals(expected, toString);
    }

    @Test
    public void constructorTest3() {
        Page<Long> result = new Page<>(Lists.newArrayList(100L, 1001L));
        result.setPageSize(150);
        result.setPageNum(109);
        result.setTotalCount(399);
        result.setParam(1000L);
        result.setSort(new Sort(Direction.DESC, "userName", "userAge"));
        result.setSuccess(false);
        result.setErrorMsg("it's ok, ba la ba la...");
        result.setErrorCode("01");
        Map<String, Object> extraInfo = Maps.newHashMap("name", (Object) "HanMeiMei");
        extraInfo.put("key", new Sort(Direction.DESC, "userName", "userAge"));
        result.setExtraInfo(extraInfo);
        String toString = result.toString();
        String expected = "{\"errorCode\":\"01\",\"errorMsg\":\"it's ok, ba la ba la...\",\"extraInfo\":{\"name\":\"HanMeiMei\",\"key\":[{\"direction\":\"DESC\",\"property\":\"userName\"},{\"direction\":\"DESC\",\"property\":\"userAge\"}]},\"pageNum\":109,\"pageSize\":150,\"param\":1000,\"sort\":[{\"direction\":\"DESC\",\"property\":\"userName\"},{\"direction\":\"DESC\",\"property\":\"userAge\"}],\"success\":false,\"totalCount\":399,\"value\":[100,1001]}";
        assertThat(result.getParam()).isEqualTo(1000L);
        Assert.assertEquals(expected, toString);
    }

    @Test
    public void constructorTest4() {
        Page<Long> result = new Page<>("01", "it's ok, ba la ba la...");
        result.setPageSize(150);
        result.setPageNum(109);
        result.setTotalCount(399);
        result.setParam(1000L);
        result.setSort(new Sort(Direction.DESC, "userName", "userAge"));
        result.setValue(Lists.newArrayList(100L, 1001L));
        Map<String, Object> extraInfo = Maps.newHashMap("name", (Object) "HanMeiMei");
        extraInfo.put("key", new Sort(Direction.DESC, "userName", "userAge"));
        result.setExtraInfo(extraInfo);
        String toString = result.toString();
        String expected = "{\"errorCode\":\"01\",\"errorMsg\":\"it's ok, ba la ba la...\",\"extraInfo\":{\"name\":\"HanMeiMei\",\"key\":[{\"direction\":\"DESC\",\"property\":\"userName\"},{\"direction\":\"DESC\",\"property\":\"userAge\"}]},\"pageNum\":109,\"pageSize\":150,\"param\":1000,\"sort\":[{\"direction\":\"DESC\",\"property\":\"userName\"},{\"direction\":\"DESC\",\"property\":\"userAge\"}],\"success\":false,\"totalCount\":399,\"value\":[100,1001]}";
        assertThat(result.getParam()).isEqualTo(1000L);
        Assert.assertEquals(expected, toString);
    }

    @Test
    public void constructorTest5() {
        Page<Long> result = new Page<>(true, "01", "it's ok, ba la ba la...");
        result.setPageSize(150);
        result.setPageNum(109);
        result.setTotalCount(399);
        result.setParam(1000L);
        result.setSort(new Sort(Direction.DESC, "userName", "userAge"));
        result.setValue(Lists.newArrayList(100L, 1001L));
        Map<String, Object> extraInfo = Maps.newHashMap("name", (Object) "HanMeiMei");
        extraInfo.put("key", new Sort(Direction.DESC, "userName", "userAge"));
        result.setExtraInfo(extraInfo);
        String toString = result.toString();
        String expected = "{\"errorCode\":\"01\",\"errorMsg\":\"it's ok, ba la ba la...\",\"extraInfo\":{\"name\":\"HanMeiMei\",\"key\":[{\"direction\":\"DESC\",\"property\":\"userName\"},{\"direction\":\"DESC\",\"property\":\"userAge\"}]},\"pageNum\":109,\"pageSize\":150,\"param\":1000,\"sort\":[{\"direction\":\"DESC\",\"property\":\"userName\"},{\"direction\":\"DESC\",\"property\":\"userAge\"}],\"success\":true,\"totalCount\":399,\"value\":[100,1001]}";
        assertThat(result.getParam()).isEqualTo(1000L);
        Assert.assertEquals(expected, toString);
        Assert.assertEquals("HanMeiMei", result.getExtraInfo("name"));
    }

    @Test
    public void getPageSizeTest1() {
        assertThat(Page.DEFAULT_PAGE_SIZE).isEqualTo(20);

        Page<Long> result = new Page<>();
        // 默认值是20
        assertThat(result.getPageSize()).isEqualTo(Page.DEFAULT_PAGE_SIZE);

        // 小于0，返回为“默认值20”
        result.setPageSize(0);
        assertThat(result.getPageSize()).isEqualTo(Page.DEFAULT_PAGE_SIZE);

        result.setPageSize(1);
        assertThat(result.getPageSize()).isEqualTo(1);

        result.setPageSize(2);
        assertThat(result.getPageSize()).isEqualTo(2);
    }

    @Test
    public void getPageNumTest1() {
        assertThat(Page.DEFAULT_PAGE_NUM).isEqualTo(1);

        Page<Long> result = new Page<>();
        // 默认值是1
        assertThat(result.getPageNum()).isEqualTo(1);

        result.setPageNum(-1);
        // 小于0，返回为“0”
        assertThat(result.getPageNum()).isEqualTo(1);

        result.setPageNum(0);
        assertThat(result.getPageNum()).isEqualTo(1);

        result.setPageNum(1);
        assertThat(result.getPageNum()).isEqualTo(1);

        result.setPageNum(2);
        assertThat(result.getPageNum()).isEqualTo(2);
    }
}
