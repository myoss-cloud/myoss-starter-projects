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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Emoji表情工具类
 *
 * @author Jerry.Chen
 * @since 2018年6月1日 上午10:43:25
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmojiUtils {
    public static final Pattern PATTERN = Pattern.compile("(\\\\u[ed][0-9a-f]{3})", Pattern.CASE_INSENSITIVE);

    /**
     * 将 emoji 表情字符中的"单\"转换为"双\\"
     *
     * @param emoji emoji字符串
     * @return 转换后的字符串
     */
    public static String addBackslash(String emoji) {
        if (StringUtils.isBlank(emoji)) {
            return emoji;
        }
        // 转成unicode
        String unicode = StringEscapeUtils.escapeJava(emoji);

        // 将 emoji 表情字符中的"单\"转换为"双\\"
        String addBackslashUnicode = PATTERN.matcher(unicode).replaceAll("\\\\$0");

        // 将unicode转换为字符串
        return StringEscapeUtils.unescapeJava(addBackslashUnicode);
    }

    /**
     * 将添加了"双\\"字符替换为"单\" ，还原成emoji字符
     *
     * @param emoji emoji字符串
     * @return 还原后的字符串
     */
    public static String removeBackslash(String emoji) {
        if (StringUtils.isBlank(emoji)) {
            return emoji;
        }
        return StringEscapeUtils.unescapeJava(emoji.replace("\\\\", "\\"));
    }
}
