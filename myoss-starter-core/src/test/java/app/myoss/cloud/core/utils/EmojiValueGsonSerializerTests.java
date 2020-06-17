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

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.annotations.JsonAdapter;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link EmojiValueGsonSerializer 测试类}
 *
 * @author Jerry.Chen
 * @since 2020年6月4日 下午7:58:20
 */
@Slf4j
public class EmojiValueGsonSerializerTests {
    @Test
    public void test1() {
        String nickname = "风青杨\uD83D\uDE0D";
        EmojiNickname emoji = new EmojiNickname();
        emoji.setNickname(nickname);
        Assert.assertEquals("风青杨\\uD83D\\uDE0D", emoji.getNickname());

        String jsonString = new Gson().toJson(emoji);
        log.info("jsonString: {}", jsonString);
        Assert.assertEquals("{\"nickname\":\"风青杨\uD83D\uDE0D\"}", jsonString);
    }

    public class EmojiNickname {
        @JsonAdapter(EmojiValueGsonSerializer.class)
        @Getter
        private String nickname;

        public void setNickname(String nickname) {
            this.nickname = EmojiUtils.addBackslash(nickname);
        }
    }
}
