/*
 * Copyright 2018-2020 https://github.com/myoss
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

import java.security.SecureRandom;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * 随机字符串工具类
 *
 * @author jerry
 * @since 2019年5月19日 下午2:10:25
 */
@Slf4j
public class RandomUtils {
    /**
     * 安全的随机算法
     */
    public static final SecureRandom SECURE_RANDOM = new SecureRandom();

    /**
     * 生成 uuid 随机字符串
     *
     * @return 随机字符串
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 补足n位,如果不够就左边补0, 超过n位的截取前n位
     *
     * @param nextValue 下一位序列号值
     * @param length 序列号最大的长度
     * @return 序列号
     */
    public static String buildSequenceNo(long nextValue, int length) {
        String value = String.valueOf(nextValue);
        if (value.length() <= length) {
            return StringUtils.leftPad(value, length, '0');
        } else {
            // 加入安全的随机算法进行截取，防止序列号最大的长度太短，导致截取出来的字符都是一样，只能尽量避免重复
            int intValue = Math.toIntExact(nextValue);
            long nextRandom = SECURE_RANDOM.nextInt(intValue);
            String tmp = String.valueOf(nextRandom);
            int tmpLength = tmp.length() - 1;
            StringBuilder builder = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                int r = SECURE_RANDOM.nextInt(tmpLength);
                builder.append(tmp.substring(r));
                if (builder.length() >= length) {
                    break;
                }
            }
            if (builder.length() > length) {
                return builder.subSequence(0, length).toString();
            }
            return builder.toString();
        }
    }
}
