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

package com.github.myoss.phoenix.core.lang.base;

import org.apache.commons.lang3.StringUtils;

/**
 * 封装字符串常用操作方法
 *
 * @author Jerry.Chen 2018年5月14日 上午10:58:02
 */
public class StringUtil {
    /**
     * 用于判断单词的第一个字符是否为字母
     *
     * @param word 单词
     * @return true：是字母；false: 不是字母
     */
    public static boolean startsWithLetter(String word) {
        return word.length() > 0 && Character.isLetter(word.charAt(0));
    }

    /**
     * 将字符串转换为驼峰格式
     *
     * @param source 原始字符串
     * @param firstCharacterUppercase 首字母是否转换为大写
     * @return 转换之后的字符串
     */
    public static String toCamelCase(String source, boolean firstCharacterUppercase) {
        String output = source.replace("-", "_").replace(".", "_");
        String[] words = StringUtils.splitByCharacterTypeCamelCase(output);

        boolean firstWordNotFound = true;
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            if (firstWordNotFound && startsWithLetter(word)) {
                words[i] = firstCharacterUppercase ? StringUtils.capitalize(word.toLowerCase()) : word.toLowerCase();
                firstWordNotFound = false;
            } else {
                words[i] = StringUtils.capitalize(word.toLowerCase());
            }
        }

        output = StringUtils.join(words).replaceAll("[\\s_]", "");
        return output;
    }

    /**
     * 将字符串转换为驼峰格式，首字母小写
     *
     * @param source 原始字符串
     * @return 转换后的字符串
     */
    public static String toCamelCase(String source) {
        return toCamelCase(source, false);
    }

    /**
     * 将字符串转换为驼峰格式，首字母大写
     *
     * @param source 原始字符串
     * @return 转换后的字符串
     */
    public static String toPascalCase(String source) {
        return toCamelCase(source, true);
    }
}
