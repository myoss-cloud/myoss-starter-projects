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

package com.github.myoss.phoenix.core.lang.serializer.impl;

import com.github.myoss.phoenix.core.constants.PhoenixConstants;
import com.github.myoss.phoenix.core.lang.serializer.Serialization;

/**
 * String Serialization, Generic String to byte[] (and back) serializer
 *
 * @author Jerry.Chen
 * @since 2018年5月25日 下午4:52:33
 */
public class StringSerialization implements Serialization<String> {
    @Override
    public byte[] serialize(String s) {
        return (s != null ? s.getBytes(PhoenixConstants.DEFAULT_CHARSET) : null);
    }

    @Override
    public String deserialize(byte[] bytes) {
        return (bytes != null ? new String(bytes, PhoenixConstants.DEFAULT_CHARSET) : null);
    }
}
