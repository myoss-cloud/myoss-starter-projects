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

package com.github.myoss.phoenix.core.utils;

import java.util.regex.Pattern;

import org.apache.commons.text.StringEscapeUtils;
import org.junit.Assert;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * {@link EmojiUtils} 测试类
 *
 * @author Jerry.Chen
 * @since 2018年6月1日 上午10:49:53
 */
@Slf4j
public class EmojiUtilsTests {
    @Test
    public void test1() {
        String emoji = "风青杨\uD83D\uDE0D";
        log.info(emoji);

        // 转成unicode
        String unicode = StringEscapeUtils.escapeJava(emoji);
        log.info(unicode);

        // 将 emoji 表情字符中的"单\"转换为"双\\"
        String addBackslashUnicode = Pattern.compile("(\\\\u[ed][0-9a-f]{3})", Pattern.CASE_INSENSITIVE)
                .matcher(unicode).replaceAll("\\\\$0");
        log.info(addBackslashUnicode);

        // 将unicode转换为字符串
        String addBackslashEmoji = StringEscapeUtils.unescapeJava(addBackslashUnicode);
        log.info(addBackslashEmoji);

        // 将添加了"双\\"字符替换为"单\" ，还原成emoji字符
        String revertEmoji = StringEscapeUtils.unescapeJava(addBackslashUnicode.replace("\\\\", "\\"));
        log.info(revertEmoji);
    }

    @Test
    public void addBackslashTest1() {
        String emoji = "风青杨\uD83D\uDE0D";
        String addBackslash = EmojiUtils.addBackslash(emoji);
        Assert.assertEquals("风青杨\\uD83D\\uDE0D", addBackslash);
    }

    @Test
    public void addBackslashTest2() {
        String emoji = "  ";
        String addBackslash = EmojiUtils.addBackslash(emoji);
        Assert.assertEquals(emoji, addBackslash);
    }

    @Test
    public void addBackslashTest3() {
        String emoji = "";
        String addBackslash = EmojiUtils.addBackslash(emoji);
        Assert.assertEquals(emoji, addBackslash);
    }

    @Test
    public void addBackslashTest4() {
        String addBackslash = EmojiUtils.addBackslash(null);
        Assert.assertNull(addBackslash);
    }

    @Test
    public void removeBackslashTest1() {
        String addBackslash = "风青杨\\uD83D\\uDE0D";
        String emoji = EmojiUtils.removeBackslash(addBackslash);
        Assert.assertEquals("风青杨\uD83D\uDE0D", emoji);
    }

    @Test
    public void removeBackslashTest2() {
        String emoji = "  ";
        String addBackslash = EmojiUtils.removeBackslash(emoji);
        Assert.assertEquals(emoji, addBackslash);
    }

    @Test
    public void removeBackslashTest3() {
        String emoji = "";
        String addBackslash = EmojiUtils.removeBackslash(emoji);
        Assert.assertEquals(emoji, addBackslash);
    }

    @Test
    public void removeBackslashTest4() {
        String addBackslash = EmojiUtils.removeBackslash(null);
        Assert.assertNull(addBackslash);
    }
}
