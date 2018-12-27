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

package app.myoss.cloud.web.http;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.util.LinkedMultiValueMap;

import app.myoss.cloud.web.utils.RestUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link HttpUrlBuilder} 测试类
 *
 * @author Jerry.Chen
 * @since 2018年12月26日 下午3:43:19
 */
@Slf4j
public class HttpUrlBuilderTests {
    @Test
    public void test1() {
        String source = "message1=123&id=1&id=2&name=%E4%B8%AD%E5%9B%BD%E6%96%B0%E5%A3%B0%E4%BB%A3&email=spider-man%40google.com";
        HttpUrlBuilder build = new HttpUrlBuilder().encodedQuery(source);

        String s1 = build.encodedQuery();
        assertEquals(source, s1);

        LinkedMultiValueMap<String, String> queryNamesAndValues = build.getQueryNamesAndValues();
        String s2 = RestUtils.writeForm(queryNamesAndValues).toString();
        assertEquals(source, s2);

        build.addQueryParameter("testKey1", "testValue@cloud.com");
        build.addQueryParameter("testKey2", "最强大脑");
        build.addEncodedQueryParameter("testKey3", "%E5%A5%BD%E6%AD%8C%E5%A3%B0");
        String s3 = build.encodedQuery();
        source += "&testKey1=testValue%40cloud.com&testKey2=%E6%9C%80%E5%BC%BA%E5%A4%A7%E8%84%91&testKey3=%E5%A5%BD%E6%AD%8C%E5%A3%B0";
        assertEquals(source, s3);

        boolean containsMessage = build.containsQueryParameter("message1");
        assertTrue(containsMessage);
        build.removeAllQueryParameters("message1");
        boolean containsMessageAfterDelete = build.containsQueryParameter("message1");
        assertTrue(!containsMessageAfterDelete);

        String s4 = build.removeQueryParameter("id", "2").encodedQuery();
        source = "id=1&name=%E4%B8%AD%E5%9B%BD%E6%96%B0%E5%A3%B0%E4%BB%A3&email=spider-man%40google.com&testKey1=testValue%40cloud.com&testKey2=%E6%9C%80%E5%BC%BA%E5%A4%A7%E8%84%91&testKey3=%E5%A5%BD%E6%AD%8C%E5%A3%B0";
        assertEquals(source, s4);

        String s5 = build.query();
        String expected = "id=1&name=中国新声代&email=spider-man@google.com&testKey1=testValue@cloud.com&testKey2=最强大脑&testKey3=好歌声";
        assertEquals(expected, s5);
        log.info("last result: {}", s5);
    }

}
