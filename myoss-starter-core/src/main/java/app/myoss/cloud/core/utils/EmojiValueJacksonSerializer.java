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

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Emoji表情序列化工具类
 *
 * <pre>
 * // Demo示例
 * public class EmojiNickname {
 *     &#64;JsonSerialize(using = EmojiValueJacksonSerializer.class)
 *     &#64;Getter
 *     private String nickname;
 *
 *     public void setNickname(String nickname) {
 *         this.nickname = EmojiUtils.addBackslash(nickname);
 *     }
 * }
 * </pre>
 *
 * @author Jerry.Chen
 * @since 2020年6月5日 上午9:40:10
 */
public class EmojiValueJacksonSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String text = EmojiUtils.removeBackslash(value);
        gen.writeString(text);
    }
}
