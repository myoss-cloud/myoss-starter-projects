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

package app.myoss.cloud.core.lang.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import org.junit.Test;

import app.myoss.cloud.core.constants.MyossConstants;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link StreamUtil} 测试类
 *
 * @author Jerry.Chen
 * @since 2018年12月27日 下午3:21:43
 */
@Slf4j
public class StreamUtilTests {
    private String buildTestString(String input) {
        for (int i = 0; i < 20; i++) {
            input += input;
        }
        log.info("before compress length: {}", input.length());
        return input;
    }

    @Test
    public void compressTest() throws UnsupportedEncodingException {
        String beforeCompress = buildTestString("中国compressTest-");

        ByteArrayInputStream inputStream = new ByteArrayInputStream(
                beforeCompress.getBytes(MyossConstants.DEFAULT_CHARSET));
        ByteArrayOutputStream compressStream = new ByteArrayOutputStream();
        StreamUtil.compress(inputStream, compressStream);
        log.info("after compress length: {}", compressStream.size());

        ByteArrayInputStream decompressStream = new ByteArrayInputStream(compressStream.toByteArray());
        ByteArrayOutputStream afterDecompressStream = new ByteArrayOutputStream();
        StreamUtil.decompress(decompressStream, afterDecompressStream);
        log.info("after decompress length: {}", afterDecompressStream.size());

        String actual = afterDecompressStream.toString(MyossConstants.DEFAULT_CHARSET.name());
        assertEquals(beforeCompress, actual);
        assertEquals(beforeCompress.length(), actual.length());
        assertTrue(beforeCompress.length() > compressStream.size());
    }

    @Test
    public void compressByteTest() {
        String beforeCompress = buildTestString("中国compressByteTest-");
        byte[] byteAfterCompress = StreamUtil.compress(beforeCompress.getBytes(MyossConstants.DEFAULT_CHARSET));
        log.info("after compress length: {}", byteAfterCompress.length);

        byte[] byteAfterDeCompress = StreamUtil.decompress(byteAfterCompress);

        String actual = new String(byteAfterDeCompress, MyossConstants.DEFAULT_CHARSET);
        assertEquals(beforeCompress, actual);
        assertEquals(beforeCompress.length(), actual.length());
        assertTrue(beforeCompress.length() > byteAfterCompress.length);
    }

    @Test
    public void compressStringTest() {
        String beforeCompress = buildTestString("中国compressStringTest-");
        byte[] afterCompress = StreamUtil.compressString(beforeCompress);
        log.info("after compress length: {}", afterCompress.length);

        String afterDeCompress = StreamUtil.decompressString(afterCompress);
        assertEquals(beforeCompress, afterDeCompress);
        assertEquals(beforeCompress.length(), afterDeCompress.length());
        assertTrue(beforeCompress.length() > afterCompress.length);
    }

    @Test
    public void compressBase64StringTest() {
        String beforeCompress = buildTestString("中国compressBase64StringTest-");
        String afterCompress = StreamUtil.compressBase64String(beforeCompress);
        log.info("after compress length: {}", afterCompress.length());

        String afterDeCompress = StreamUtil.decompressBase64String(afterCompress);
        assertEquals(beforeCompress, afterDeCompress);
        assertEquals(beforeCompress.length(), afterDeCompress.length());
        assertTrue(beforeCompress.length() > afterCompress.length());
    }
}
