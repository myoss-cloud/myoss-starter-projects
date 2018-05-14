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

/**
 * 封装字符串常用操作方法
 *
 * @author Jerry.Chen 2018年5月14日 上午10:58:02
 */
public class StringUtil {
    /**
     * 将字符串转换为驼峰格式
     *
     * @param source 原始字符串
     * @param firstCharacterUppercase 首字母是否转换为大写
     * @return 转换之后的字符串
     */
    public static String toCamelCase(CharSequence source, boolean firstCharacterUppercase) {
        StringBuilder sb = new StringBuilder();

        boolean nextUpperCase = false;
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);

            switch (c) {
                case '_':
                case '-':
                case '@':
                case '$':
                case '#':
                case ' ':
                case '/':
                case '&':
                    if (sb.length() > 0) {
                        nextUpperCase = true;
                    }
                    break;

                default:
                    if (nextUpperCase) {
                        sb.append(Character.toUpperCase(c));
                        nextUpperCase = false;
                    } else {
                        sb.append(Character.toLowerCase(c));
                    }
                    break;
            }
        }

        if (firstCharacterUppercase) {
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        }

        return sb.toString();
    }

    /**
     * 将字符串转换为驼峰格式，首字母小写
     *
     * @param source 原始字符串
     * @return 转换后的字符串
     */
    public static String toCamelCase(CharSequence source) {
        return toCamelCase(source, false);
    }

    /**
     * 将字符串转换为驼峰格式，首字母大写
     *
     * @param source 原始字符串
     * @return 转换后的字符串
     */
    public static String toPascalCase(CharSequence source) {
        return toCamelCase(source, true);
    }
}
