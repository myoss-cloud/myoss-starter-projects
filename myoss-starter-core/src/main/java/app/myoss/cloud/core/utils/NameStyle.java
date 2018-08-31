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

package app.myoss.cloud.core.utils;

import static app.myoss.cloud.core.constants.MyossConstants.UNDERLINE;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;

/**
 * 字段命名风格
 *
 * @author Jerry.Chen
 * @since 2018年4月26日 下午4:25:20
 */
@AllArgsConstructor
public enum NameStyle {

    /**
     * 和属性名保持一致
     */
    ORIGIN("origin", "和属性名保持一致", new String[] { "origin", "_origin", "origin_" }) {
        @Override
        public String transform(String s) {
            return s;
        }
    },

    /**
     * 驼峰转下划线，单词小写
     */
    SNAKE_CASE("snake_case", "驼峰转下划线，单词小写", new String[] { "snake_case", "_snake_case", "snake_case_" }) {
        @Override
        public String transform(String s) {
            if (StringUtils.isBlank(s)) {
                return s;
            }
            String[] split = StringUtils.splitByCharacterTypeCamelCase(s);
            int length = split.length;
            StringBuilder builder = new StringBuilder(s.length() + length);
            int lastIndex = length - 1;
            for (int i = 0; i < length; i++) {
                String word = split[i];
                if (UNDERLINE.equals(word)) {
                    if (i == 0) {
                        builder.append(word);
                    }
                    continue;
                }
                builder.append(word.toLowerCase());
                if (i != lastIndex) {
                    builder.append(UNDERLINE);
                }
            }
            return builder.toString();
        }
    },

    /**
     * 驼峰转下划线，单词小写
     */
    SCREAMING_SNAKE_CASE("SCREAMING_SNAKE_CASE", "驼峰转下划线，单词大写",
            new String[] { "SNAKE_CASE", "_SNAKE_CASE", "SNAKE_CASE_" }) {
        @Override
        public String transform(String s) {
            if (StringUtils.isBlank(s)) {
                return s;
            }
            String[] split = StringUtils.splitByCharacterTypeCamelCase(s);
            int length = split.length;
            StringBuilder builder = new StringBuilder(s.length() + length);
            int lastIndex = length - 1;
            for (int i = 0; i < length; i++) {
                String word = split[i];
                if (UNDERLINE.equals(word)) {
                    if (i == 0) {
                        builder.append(word);
                    }
                    continue;
                }
                builder.append(word.toUpperCase());
                if (i != lastIndex) {
                    builder.append(UNDERLINE);
                }
            }
            return builder.toString();
        }
    },

    /**
     * 转换为大写
     */
    UPPER_CASE("UPPERCASE", "转换为大写", new String[] { "UPPERCASE", "_UPPERCASE", "UPPERCASE_" }) {
        @Override
        public String transform(String s) {
            if (StringUtils.isBlank(s)) {
                return s;
            }
            return s.toUpperCase();
        }
    },

    /**
     * 转换为小写
     */
    LOWER_CASE("lowercase", "转换为小写", new String[] { "lowercase", "_lowercase", "lowercase_" }) {
        @Override
        public String transform(String s) {
            if (StringUtils.isBlank(s)) {
                return s;
            }
            return s.toLowerCase();
        }
    },

    /**
     * 大驼峰命名法
     */
    PASCAL_CASE("PascalCase", "大驼峰命名法", new String[] { "PascalCase", "_PascalCase", "PascalCase_" }) {
        @Override
        public String transform(String s) {
            if (StringUtils.isBlank(s)) {
                return s;
            }
            String[] split = StringUtils.splitByCharacterTypeCamelCase(s);
            int length = split.length;
            StringBuilder builder = new StringBuilder(s.length());
            int lastIndex = length - 1;
            for (int i = 0; i < length; i++) {
                String word = split[i];
                if (UNDERLINE.equals(word)) {
                    if (i == 0 || i == lastIndex) {
                        builder.append(word);
                    }
                    continue;
                }
                builder.append(StringUtils.capitalize(word));
            }
            return builder.toString();
        }
    },

    /**
     * 小驼峰命名法
     */
    CAMEL_CASE("camelCase", "小驼峰命名法", new String[] { "camelCase", "_camelCase", "camelCase_" }) {
        @Override
        public String transform(String s) {
            if (StringUtils.isBlank(s)) {
                return s;
            }
            String[] split = StringUtils.splitByCharacterTypeCamelCase(s);
            int length = split.length;
            StringBuilder builder = new StringBuilder(s.length());
            int lastIndex = length - 1;
            for (int i = 0; i < length; i++) {
                String word = split[i];
                if (UNDERLINE.equals(word)) {
                    if (i == 0 || i == lastIndex) {
                        builder.append(word);
                    }
                    continue;
                }
                if (i == 0) {
                    builder.append(StringUtils.uncapitalize(word));
                } else {
                    builder.append(StringUtils.capitalize(word));
                }
            }
            return builder.toString();
        }
    };

    /**
     * 类型
     */
    String   type;
    /**
     * 描述信息
     */
    String   desc;
    /**
     * 示例
     */
    String[] example;

    /**
     * 进行格式化
     *
     * @param s 原始字符串
     * @return 转换后的字符串
     */
    public abstract String transform(String s);
}
