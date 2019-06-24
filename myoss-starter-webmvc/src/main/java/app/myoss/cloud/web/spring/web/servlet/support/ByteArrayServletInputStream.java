/*
 * Copyright 2018-2019 https://github.com/myoss
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

package app.myoss.cloud.web.spring.web.servlet.support;

import java.io.ByteArrayInputStream;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;

/**
 * 实现{@link ServletInputStream}，使用 {@link ByteArrayInputStream} 支持多次读取
 * {@code byte[]}
 *
 * @author Jerry.Chen
 * @since 2018年4月11日 下午2:59:17
 * @see ReaderBodyHttpServletRequestWrapper
 */
public class ByteArrayServletInputStream extends ServletInputStream {
    private final ByteArrayInputStream byteArrayInputStream;

    /**
     * 实现{@link ServletInputStream}，使用 {@link ByteArrayInputStream}
     * 支持多次读取{@code byte[]}
     *
     * @param body byte[]
     */
    public ByteArrayServletInputStream(byte[] body) {
        this.byteArrayInputStream = new ByteArrayInputStream(body);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setReadListener(ReadListener readListener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int read() {
        return byteArrayInputStream.read();
    }

    /**
     * 是否支持重置 {@link #reset()}
     */
    @Override
    public boolean markSupported() {
        return true;
    }

    /**
     * 把pos的指针的位置重置为起始位置
     */
    @Override
    public synchronized void reset() {
        byteArrayInputStream.reset();
    }
}
