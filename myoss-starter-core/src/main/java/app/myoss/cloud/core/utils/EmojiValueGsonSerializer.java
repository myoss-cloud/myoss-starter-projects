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

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Emoji表情序列化工具类
 *
 * <pre>
 * // Demo示例
 * public class EmojiNickname {
 *     &#64;JsonAdapter(EmojiValueGsonSerializer.class)
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
 * @since 2020年6月4日 下午7:58:20
 */
public class EmojiValueGsonSerializer implements JsonSerializer<String> {
    @Override
    public JsonElement serialize(String src, Type typeOfSrc, JsonSerializationContext context) {
        String text = EmojiUtils.removeBackslash(src);
        return context.serialize(text);
    }
}
